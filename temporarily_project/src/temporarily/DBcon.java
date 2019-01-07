package temporarily;

/*
 * DB 연동 메서드 관리
 * 
 * 주요 기능
 * - DB 연결과 해제
 * - 로그인 체크
 * - 상품 및 재고 조회
 * - 판매 등록, 삭제 
 * - 판매 현황 조회
 * 
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DBcon {
	int loginCount; // 로그인 여부. 1성공 0실패
	int queryResultCount = 0; // 쿼리문 결과 여부. 1있음 0없음

	String loginUser; // 현재 로그인한 유저의 매장코드
	String productCode; // 상품 조회 후 등록을 위한 저장 변수
	int productPrice, stockQuantity, salesQuantity; // 상품 조회 후 반환을 위한 저장 변수

	JTable tableSave; // 가져온 테이블 저장 변수
	int dayTotalPrice = 0; // 일매출 저장 변수
	int monthTotalPrice = 0; // 월매출 저장 변수
	String salesNum = "0"; // 일판매번호 저장변수

	// LocalDate currDate = LocalDate.now(); // 오늘 날짜
	LocalDate currDate = LocalDate.of(2018, 11, 1);
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
	String currDateCode = currDate.format(formatter);

	// DB 연동
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;

	public DBcon() {
		connect();
	}

	// DB 연결
	public void connect() {
		// String URL = "jdbc:oracle:thin:@localhost:1521:xe";
		String URL = "jdbc:oracle:thin:@localhost:1521:orcl";
		String ID = "project1";
		String PW = "pro1";

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(URL, ID, PW);
			System.out.println("DB접속");

		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("DB접속 오류");
			e.printStackTrace();
		}
	}

	// DB 연결 해제 - 로그아웃, 창닫기 할 때 종료
	public void disconn() {
		try {
			System.out.println("DB 종료");
			rs.close();
			pstmt.close();
			con.close();
		} catch (Exception e) {
			System.out.println("DB 종료 오류");
		}
	}

	// *로그인
	// 로그인 체크
	public void checkLogin(String id, String pw, String divRadioResult) {
		String query;

		if (divRadioResult.equals("매장")) {
			// 매장 테이블 검색
			query = "select m_id, m_pw from manager";
		} else {
			// 본사 테이블 검색
			query = "select h_id, h_pw from head";
		}
		try {
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				if (id.equals(rs.getString(1)) && pw.equals(rs.getString(2))) {
					// 로그인 유저의 매장 코드 검색
					query = "select s_code from store where m_id='" + id + "'";

					pstmt = con.prepareStatement(query);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						// 로그인 성공
						this.loginCount = 1;
						this.loginUser = rs.getString(1); // 로그인 유저의 매장 코드 user에 입력
					}
					System.out.println("접속 매장:" + loginUser);
					break;
				} else {
					// 로그인 실패
					this.loginCount = 0;
				}
			}
		} catch (SQLException e) {
			System.out.println("checkLogin 오류");
			e.printStackTrace();
		}
	}

	public Integer getLoginCount() {
		return loginCount; // 로그인 성공 여부 LoginView에 반환
	}

	public String getLoginUser() {
		return loginUser; // 로그인 유저 MainFrame에 반환
	}

	/****************************************************************/

	// 판매관리 - 판매등록 - 색상 콤보박스
	// 등록된 상품의 모든 컬러를 콤보박스 리스트에 연동
	public void listColorCombo(JComboBox<String> colorCombo) {
		String query = "select distinct p_color from product";

		try {
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				colorCombo.addItem(rs.getString(1)); // 콤보박스 아이템에 추가
			}
			System.out.println("listColorCombo 성공");
		} catch (SQLException e) {
			System.out.println("listColorCombo 오류");
			e.printStackTrace();
		}
	}

	// 판매관리 - 판매등록 - 판매현황 테이블
	// 해당 날짜의 판매 현황을 테이블에 추가
	public void searchSalesStatus(JTable statusTable, Object date) {
		this.tableSave = statusTable;
		String salesDivText;

		String query = "select sa_no, sa_group, sales.p_code, p_price, sa_qty, sa_price\r\n" + "from sales, product\r\n"
				+ "where sales.p_code = product.p_code\r\n" + "and sa_date = '" + date + "' and s_code = '" + loginUser
				+ "' \r\n" + "order by sa_no";

		try {
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();
			dayTotalPrice = 0;

			while (rs.next()) {
				this.salesNum = rs.getString(1);
				String salesNumber = rs.getString(1);
				int salesDivCode = rs.getInt(2);
				String productCode = rs.getString(3);
				String productPrice = rs.getString(4);
				String salesQuantity = rs.getString(5);
				int salesPrice = rs.getInt(6);

				if (salesDivCode == 1) {
					// 1:판매
					salesDivText = "판매";
				} else {
					// 2:반품
					salesDivText = "반품";
				}

				String productNo = productCode.substring(0, 7);
				String productColor = productCode.substring(7, 9);
				String productSize = productCode.substring(9);

				dayTotalPrice += salesPrice;

				// 조회 결과 테이블에 추가
				Object newData[] = { salesNumber, salesDivText, productNo, productColor, productSize, productPrice,
						salesQuantity, salesPrice };
				DefaultTableModel newModel = (DefaultTableModel) statusTable.getModel();
				newModel.addRow(newData);
			}
			System.out.println("salesStatusSearch 성공");
		} catch (SQLException e) {
			System.out.println("salesStatusSearch 오류");
			e.printStackTrace();
		}
	}

	public Integer getDayTotalPrice() {
		return dayTotalPrice; // 일 총판매금액 SalesReg에 반환
	}

	/****************************************************************/

	// *판매관리 - 판매등록 - 조회 버튼
	// 상품 조회 및 판매단가,재고수량,코드 저장
	public void searchProduct(String productNo, String productColor, String productSize) {
		queryResultCount = 0;

		String query = "select p_price, p_qty, product.p_code from product, stock\r\n"
				+ "where product.p_code=stock.p_code \r\n" + "and s_code='" + this.loginUser + "'\r\n" + "and p_no='"
				+ productNo + "' and p_color='" + productColor + "' and p_size='" + productSize + "'";

		try {
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				queryResultCount = 1;
				this.productPrice = rs.getInt(1);
				this.stockQuantity = rs.getInt(2);
				this.productCode = rs.getString(3);
			}

			if (queryResultCount == 0) {
				JOptionPane.showMessageDialog(null, "해당 상품이 없습니다.");

				this.productPrice = 0;
				this.stockQuantity = 0;
				this.salesQuantity = 0;

			} else {
				this.salesQuantity = 1;
				System.out.println("searchProduct 성공");
			}
		} catch (SQLException e) {
			System.out.println("searchProduct 오류");
			e.printStackTrace();
		}
	}

	public Integer getProductPrice() {
		return productPrice; // 판매단가 SalesReg, searchProduct에 반환
	}

	public Integer getStockQuantity() {
		return stockQuantity; // 재고수량 SalesReg,StockSearch에 반환
	}

	public Integer getSalesQuantity() {
		return salesQuantity; // 판매수량 기본값 SalesReg에 반환
	}

	// *판매관리 - 판매등록 - 등록 버튼
	// 상품 판매,반품 데이터 삽입
	public void registerSales(JTable totalTable, String salesDiv, String salesQuantity, String salesPrice) {
		int salesDivCode;

		if (salesDiv.equals("판매")) {
			// 판매:1
			salesDivCode = 1;
		} else {
			// 반품:2
			salesDivCode = 2;
			salesQuantity = "-" + salesQuantity;
			salesPrice = "-" + salesPrice;
			productPrice = 0 - productPrice;
		}

		// 일 판매번호
		salesNum = String.valueOf(Integer.parseInt(salesNum) + 1);
		if (salesNum.length() == 1) {
			salesNum = "00" + salesNum;
		} else if (salesNum.length() == 2) {
			salesNum = "0" + salesNum;
		}

		String query = "insert into sales values('" + currDateCode + loginUser + salesNum + "',\r\n" + "to_date('"
				+ currDate + "','yyyy-mm-dd'),\r\n" + salesNum + ",'" + loginUser + "'," + salesDivCode + ",'"
				+ productCode + "'," + productPrice + "*" + salesQuantity + "," + salesQuantity + "," + salesPrice
				+ ")";

		try {
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();

			searchSalesStatus(tableSave, currDate);

			clear(totalTable);
			Object newData[] = { currDate, dayTotalPrice };
			DefaultTableModel newModel = (DefaultTableModel) totalTable.getModel();
			newModel.addRow(newData);

			System.out.println("registerSales 성공");
		} catch (SQLException e) {
			System.out.println("registerSales 오류");
			e.printStackTrace();
		}
	}

	// *판매관리 - 판매등록 - 삭제 버튼
	// 선택한 행 데이터 삭제
	public void salesDelete(JTable totalTable, String salesNum, int salesPrice) {
		String salesCode = currDateCode + loginUser;

		if (salesNum.length() == 1) {
			salesCode = salesCode + "00" + salesNum;
		} else if (salesNum.length() == 2) {
			salesCode = salesCode + "0" + salesNum;
		} else {
			salesCode = salesCode + salesNum;
		}

		dayTotalPrice -= salesPrice;

		// 판매현황 테이블 데이터 삭제
		String query = "delete from sales where sa_code = '" + salesCode + "'";

		try {
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();

			clear(totalTable);
			Object newData[] = { currDate, dayTotalPrice };
			DefaultTableModel newModel = (DefaultTableModel) totalTable.getModel();
			newModel.addRow(newData);

			System.out.println("salesDelete 성공");
		} catch (SQLException e) {
			System.out.println("salesDelete 오류");
			e.printStackTrace();
		}
	}

	/****************************************************************/

	// *판매관리 - 판매현황 - 조회 버튼
	// 선택한 월의 날짜별 데이터 조회
	public void searchStatus(JTable dayTable, String selectedDate) {
		this.tableSave = dayTable;
		this.monthTotalPrice = 0;
		queryResultCount = 0;

		String query = "select to_char(sa_date,'yyyy-mm-dd'), to_char(sa_date,'day'), \r\n"
				+ "sum(sa_qty), sum(ps_price), sum(sa_price)\r\n" + "from sales where s_code = '" + loginUser + "'\r\n"
				+ "and sa_code like '" + selectedDate + "%'\r\n" + "group by sa_date order by sa_date";

		try {
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				queryResultCount = 1;
				String date = rs.getString(1);
				String day = rs.getString(2);
				int salesQuantity = rs.getInt(3);
				int totalProductPrice = rs.getInt(4);
				int totalSalesPrice = rs.getInt(5);
				this.monthTotalPrice += rs.getInt(5);

				Object newData[] = { date, day, salesQuantity, totalProductPrice, totalSalesPrice, monthTotalPrice };
				DefaultTableModel newModel = (DefaultTableModel) dayTable.getModel();
				newModel.addRow(newData);
			}

			if (queryResultCount == 0) {
				JOptionPane.showMessageDialog(null, "조회된 결과가 없습니다.");
			} else {
				System.out.println("searchStatus 성공");
			}
		} catch (SQLException e) {
			System.out.println("searchStatus 오류");
			e.printStackTrace();
		}
	}

	public Integer getMonthTotalPrice() {
		return monthTotalPrice; // 월별 총판매금액 SalesStatus에 반환
	}

	/****************************************************************/

	// *재고관리 - 재고조회 - 조회 버튼
	// 해당 품번 재고 조회
	public void searchStock(JTable stockTable, String productNo) {
		this.tableSave = stockTable;
		queryResultCount = 0;

		String query = "select p_no, p_price, p_color, p_size, store.s_code, s_name, s_phone, stock.p_qty\r\n"
				+ "from product, stock, store\r\n" + "where product.p_code=stock.p_code\r\n"
				+ "and store.s_code=stock.s_code\r\n" + "and p_no='" + productNo + "'\r\n" + "order by store.s_code";

		try {
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				queryResultCount = 1;

				this.productPrice = rs.getInt(2);
				String productColor = rs.getString(3);
				String productSize = rs.getString(4);
				String storeCode = rs.getString(5);
				String storeName = rs.getString(6);
				String phone = rs.getString(7);
				String stockQuantity = rs.getString(8);

				Object newData[] = { productColor, productSize, storeCode, storeName, phone, stockQuantity };
				DefaultTableModel newModel = (DefaultTableModel) stockTable.getModel();
				newModel.addRow(newData);
			}

			if (queryResultCount == 0) {
				JOptionPane.showMessageDialog(null, "해당 상품이 없습니다.");
				this.productPrice = 0; // 판매단가 초기화
			} else {
				System.out.println("searchStock 성공");
			}
		} catch (SQLException e) {
			System.out.println("searchStock 오류");
			e.printStackTrace();
		}
	}

	// 매장내 전체재고 조회
	// public void searchStock(JTable table) {}

	// JTable 필드 초기화
	public void clear(JTable table) {
		DefaultTableModel newModel = (DefaultTableModel) table.getModel();
		while (newModel.getRowCount() > 0) {
			newModel.removeRow(0);
		}
	}

	/*************************************************************************/
	/*************************************************************************/
	/*************************************************************************/
	/*************************************************************************/
	/*************************************************************************/
	/*************************************************************************/
	// 신상품등록
	public void insertProduct(String productCode, String productNo, String productColor, String productSize,
			String productPrice) {// 상품코드, 품번, 색상, 사이즈, 가격
		// 상품 코드는 품번+색상+사이즈 로 자동 생성, 품번,색상,사이즈,가격만 사용자에게 입력받음

		String query1 = "INSERT INTO PRODUCT VALUES(UPPER('" + productCode + "')," + productNo + ",UPPER('"
				+ productColor + "'),UPPER('" + productSize + "')," + productPrice + ")";

		String query2 = "select p_code from product"; // 상품코드 중복검사

		PreparedStatement pstmt1 = null;
		ResultSet rs1 = null;

		try {
			pstmt1 = con.prepareStatement(query2);
			rs1 = pstmt1.executeQuery();
			int checkCode = 0; // p_code 중복검사
			
			while(rs1.next()) { //product에서 상품코드 중복검사
				if(productCode.toUpperCase().equals(rs1.getString(1))) {
					checkCode = 0; // 상품코드가 존재하면 checkCode가 0
					break;
				}else {
					checkCode = 1; // 존재하지 않으면 checkCode는 1
				}
			}
			if(checkCode == 0) { // 상품이 존재하면 신상품등록 쿼리 실행 안함
				JOptionPane.showMessageDialog(null, "해당상품이 이미존재합니다.");
			}else { // 상품이 존재하지 않으면 상품등록 쿼리실행
				pstmt = con.prepareStatement(query1);
				rs = pstmt.executeQuery();
				JOptionPane.showMessageDialog(null, "입력되었습니다.");				
			}


		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 단가수정 - 품번으로 상품가격 조회
	public void searchProduct(String productNo) {
		String query1 = "SELECT DISTINCT P_PRICE FROM PRODUCT WHERE P_NO =" + productNo;
		String query2 = "select p_no from product";
		PreparedStatement pstmt1 = null;
		ResultSet rs1 = null;
		
		try {
			pstmt1 = con.prepareStatement(query2);
			rs1 = pstmt1.executeQuery();
			int checkproductNo = 0; //품번이 존재하는지 검사
			
			while(rs1.next()) { // 조회하고자 하는 품번의 존재유무
				if(productNo.equals(rs1.getString(1))) {
					checkproductNo = 1; // 품번이 존재하면 1
					break;
				}else {
					checkproductNo = 0; // 존재하지 않으면 0
				}
			}
			
			if(checkproductNo == 1) { // 품번이 존재하면
				pstmt = con.prepareStatement(query1); // 상품가격 조회
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int getPrice = rs.getInt(1);
					this.productPrice = getPrice;
					// getPrice에 품번으로 조회한 상품가격을 저장하여 다시 productPrice로 넘겨준다
				}				
			}else { // 존재하지 않으면 조회 쿼리 실행하지 않음
				JOptionPane.showMessageDialog(null, "해당품번이 존재하지않습니다.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 단가수정 - 판매단가 수정
	public void updatePrice(String priceModify, String productNo) { // 수정할 가격, 품번
		String query1 = "UPDATE PRODUCT SET P_PRICE = " + priceModify + "WHERE P_NO = " + productNo;
		String query2 = "select p_no from product"; // 품번검사
		PreparedStatement pstmt1 = null;
		ResultSet rs1 = null;
		
		try {
			pstmt1 = con.prepareStatement(query2);
			rs1 = pstmt1.executeQuery();
			int checkproductNo = 0; //품번이 존재하는지 검사
			
			while(rs1.next()) {
				if(productNo.equals(rs1.getString(1))) {
					checkproductNo = 1;
					break;
				}else
					checkproductNo = 0;
			}
			
			if(checkproductNo == 1) { // 품번이 존재하면
				pstmt = con.prepareStatement(query1);
				rs = pstmt.executeQuery();
				JOptionPane.showMessageDialog(null, "변경되었습니다.");				
			}else { // 품번이 존재하지 않으면
				JOptionPane.showMessageDialog(null, "품번을 확인해주세요.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//재고 등록/수정 - 매장 이름 콤보박스에 존재하는 매장이름들 추가하기
	public void getStoreNameCombobox(JComboBox storeComboBox) { // 매장이름 콤보박스
		String query = "select distinct sr.s_name from store sr, stock sc "
				+ "where sr.s_code = sc.s_code and sc.p_qty > 0";
						//재고 수량을 하나이상 가지고 있는 매장이름 출력
		try {
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				storeComboBox.addItem(rs.getString(1));
				// 콤보박스에 query의 결과 값들을 추가
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//재고 등록/수정  - 매장이름과 입력받은 품번으로 존재하는 컬러 콤보박스에 추가
	public void searchStockColor(JComboBox colorComboBox, JComboBox getStoreComboBox, String productNo) {
		//	컬러 콤보박스, 매장 콤보박스에서 선택된 매장, 품번

		String query1 = "select distinct pro.p_color from product pro,stock sc,store sr\r\n"
				+ "Where pro.p_code = sc.p_code and\r\n" + "sr.s_code = sc.s_code and\r\n" + "pro.p_no =" + productNo
				+ "\r\n" + "and sr.s_name ='" + getStoreComboBox.getSelectedItem() + "'";
				// 품번과 매장이름으로 해당 상품의 등록된 컬러를 콤보박스에 출력
				// 품번은 입력 받은 값으로 가져오고, 매장이름은 매장콤보박스에서 선택된 것을 가져와서 해당 컬러를 찾음
		
		String query2 = "select p_no from product"; // 품번검사
		PreparedStatement pstmt1 = null;
		ResultSet rs1 = null;
		


		try {
			pstmt1 = con.prepareStatement(query2);
			rs1 = pstmt1.executeQuery();
			
			int checkproductNo = 0; //품번이 존재하는지 검사
			
			while(rs1.next()) {
				if(productNo.equals(rs1.getString(1))) {
					checkproductNo = 1;
					break;
				}else
					checkproductNo = 0;
			}
			
			if(checkproductNo == 1) { // 품번이 존재하면
				pstmt = con.prepareStatement(query1);
				rs = pstmt.executeQuery();				
				while (rs.next()) { 
					colorComboBox.addItem(rs.getString(1));
					// 콤보박스에 query의 결과 값들을 추가 

				}
			}else {// 존재하지 않으면
				JOptionPane.showMessageDialog(null, "존재하지 않는 품번입니다.");				

			}


		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// 매장이름,품번,색상을 사이즈별로 조회하여 각각의 사이즈 재고출력
	public void searchSize(String storeName, String productNo, String productColor, String productSize) {
		String query = "select sc.p_qty from product pro,stock sc,store sr\r\n" + "Where pro.p_code = sc.p_code and\r\n"
				+ "sr.s_code=sc.s_code and sr.s_name = '" + storeName + "'and pro.p_no =" + productNo
				+ "and pro.p_color = '" + productColor + "' and pro.p_size = '" + productSize + "'";
		//	매장 이름,품번,색상,사이즈 별로 재고 수량을 출력 


		try {

			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();

			if (rs.next()) { // 해당 query의 결과값이 있으면 아래를 실행
				int getStockQuantity = rs.getInt(1);
				this.stockQuantity = getStockQuantity;
				// query를 실행시킨 결과값(상품수량)을  stockQuantity에 저장
			} else {// 없으면 stockQuantity(수량)을 0으로!
				this.stockQuantity = 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	// 매장이름 품번 색상 사이즈를 받아서 나온 재고들을 업데이트
	public void updateStock(String stockModift, String storeName, String productNo, String productColor) {
		// 수정할 재고수량, 매장이름, 품번, 색상

		String[] sizeArray = { "S", "M", "L", "XL" };// 사이즈를 배열에 저장
		String updateSizeText = "";// 존재하는 상품코드
		String nullSizeText = "";// 존재하지 않는 상품코드
		

		for (int i = 0; i < sizeArray.length; i++) {
			queryResultCount = 0;
			String query1 = "select p_code from product \r\n" + "where p_no = " + productNo + " and p_color = '"
					+ productColor + "' \r\n" + "and p_size = '" + sizeArray[i] + "'";
							// 품번, 색상, 사이즈로 p_code(상품코드)의 존재여부 확인
			
			String query2 = "update stock\r\n" + "set p_qty = " + stockModift + "\r\n"
					+ "where stock.p_code in(select p_code from product \r\n" + "where p_no = " + productNo
					+ " and p_color = '" + productColor + "' and p_size = '" + sizeArray[i] + "') \r\n"
					+ "and stock.s_code in(select s_code from store where s_name = '" + storeName + "')";
			 				// 품번, 색상, 사이즈, 매장 으로 재고 수량을 조회하여 해당 재고수량 업데이트
			
			try {
				pstmt = con.prepareStatement(query1);
				rs = pstmt.executeQuery();

				while (rs.next()) {// query1이 존재하면(해당 상품이 있으면)
					pstmt = con.prepareStatement(query2);// 업데이트 실행
					ResultSet rs2 = pstmt.executeQuery();

					queryResultCount = 1;// query의 결과카운트를 1로 설정
					updateSizeText = updateSizeText + sizeArray[i] + " ";
					// 존재하는 사이즈를  updateSizeText에 함께 저장(다같이 한번에 출력하기 위하여)
				} 
		

				if (queryResultCount == 0) {	// 없는 사이즈일 경우
					// 없는 사이즈일 경우
					nullSizeText = nullSizeText + sizeArray[i] + " ";
					//존재하지 않는 사이즈를  nullSizeText에 함께 저장(다같이 한번에 출력하기 위하여)
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println(updateSizeText);
		JOptionPane.showMessageDialog(null,updateSizeText + "사이즈는 수정되었습니다. \n" + nullSizeText + "사이즈는 상품 등록이 필요합니다.");
		// for문 종료 후 해당 사이즈가 존재하여 수정된 사이즈들이 저장된 변수 updateSizeText와
		// 해당 사이즈의 상품이 등록되지 않아 상품등록이 먼저 필요한 사이즈들이 저장된 변수  nullSizeText을 창으로 띄워줌
	}

	// 계정생성
public void createAccount(String id, String password, String personName, String phone, 
		String storeName, String manager,String radio) {
	// 아이디, 비밀번호, 이름, 전화번호, 매장명, 담당자명, 라디오번튼(매장인지 본사인지 구분하는 버튼)

	PreparedStatement pstmt1 = null;
	ResultSet rs1 = null;

	PreparedStatement checkHeadId = null;
	ResultSet checkHeadIdRs = null;

	PreparedStatement checkStoreId = null;
	ResultSet checkStoreIdRs = null;

	PreparedStatement createScode = null;
	ResultSet createScodeRs = null;

	PreparedStatement checkScode = null;
	ResultSet checkScodeRs = null;

	String query1;
	String query2;
	String checkStoreIdQuery = "select m_id from manager"; // 매니저 id를 검색
	String checkHeadIdQuery = "select h_id from head"; // 본사 id를 검색
	String createScodeQuery = "SELECT round(DBMS_RANDOM.VALUE(1,10)*1000) RANDOM FROM dual";
											//s_code를 생성하기 위해 무작위 4자리 숫자를 생성하는 코드
	String checkScodeQuery = "select SUBSTR(s_code,2,5) from store";
											//s_code의 중복을 막기 위해 무작위로 4자리 숫자가 들어가는 부분을 출력
	int headCount = 0; // 본사 아이디 중복여부
	int storeCount = 0; // 매니저 아이디 중복여부
	int checkManagerCount = 0; // 담당자 아이디 중복여부(매장만)
	String sCode = null; //생성된 코드 삽입(createScodeQuery에서 생성된)

	// 매장코드(s_code)의 무작위 숫자 4개를 생성(단, 중복되지 않게)
	try {
		checkScode = con.prepareStatement(checkScodeQuery);
		checkScodeRs = checkScode.executeQuery(); // 이미 존재하는 매장코드 검색

		while (checkScodeRs.next()) { // 이미 존재하는 매장코드(첫번째 while문)
			createScode = con.prepareStatement(createScodeQuery); // 무작위로 숫자 4개 생성
			createScodeRs = createScode.executeQuery();
			while (createScodeRs.next()) { // 생성된 숫자 4개를 가지고(두번째 while문)
				if (checkScodeRs.getString(1) != createScodeRs.getString(1)) {
						// 이미 있는 매장코드와 새롭게 생성된 매장코드가 다르면 
					sCode = createScodeRs.getString(1); // sCode는 새롭게 생성된 매장코드
					break; // 두번째 while 멈춤
				} else { // 그렇지 않으면
					sCode = null;  //sCode는 null값을 넣고 
					break; // 두번째 while문 멈춤
				}
			}
			if (sCode != null) { // s_code가 null이 아니면 첫번째 while 멈춤
				break;
			} else { // s_code가 null이면 첫번째 while다시 실행하여 처음부터 다시!
				continue;
			}
		}
	} catch (SQLException e1) {
		e1.printStackTrace();
	}

		// 본사일 경우와 매장일 경우 계정생성
	if (radio.equals("본사")) {
		try {
			checkHeadId = con.prepareStatement(checkHeadIdQuery);
			checkHeadIdRs = checkHeadId.executeQuery();

			checkStoreId = con.prepareStatement(checkHeadIdQuery);
			checkStoreIdRs = checkStoreId.executeQuery();

			while (checkHeadIdRs.next()) { // 본사 아이디 중복검사
				if (id.equals(checkHeadIdRs.getString(1)) || id.isEmpty()) { 
					// 같은 아이디가 있거나 아이디를 입력하지 않았을때
					headCount = 0; // headCount에 0 을저장
					break; // while 종료
				} else { // 같은아이디가 없거나 아이디가 빈칸이 아닐경우
					headCount = 1; // headCount에 1을 저장
				}
			}
				//본사직원, 매장직원의 아이디를 겹치지 않게 하기 위해서 매장아이디도 함꼐 중복검사함!
			while (checkStoreIdRs.next()) {  // 매장아이디 중복검사
				if (id.equals(checkStoreIdRs.getString(1))) {
					// 같은 아이디가 있거나 아이디를 입력하지 않았을때
					storeCount = 0; // storeCount에 0 을저장
					break; // while 종료
				} else { // 같은아이디가 없거나 아이디가 빈칸이 아닐경우
					storeCount = 1; // headCount에 1을 저장
				}
			}

			if (headCount == 1 && storeCount == 1) { 
					// 매장ID중복검사와 본사ID 중복검사에서 중복이 안된경우에만 각각 변수에 1을 저장 
					// 따라서 headCount와 storeCount가 1일 경우 본사 계정 생성
				query1 = "insert into head values('" + id + "','" + password + "','" + personName + "','" + phone + "')";
									// id,pw,이름,전화번호를 입력받아서 아이디 생성
				query2 = "insert into store values('H" + sCode + "',1,'" + storeName + "','" + phone + "','" + id
						+ "','" + id + "')";    // 아이디 생성과 동시에 매장도 같이 생성되어야 함!
												// 따라서 본사아이디를 생성할 경우 매장의 그룹을 본사로 구분하여 매장을 생성하고
												// 본사의 경우 담당자는 자기자신의 ID로 지정!
				pstmt = con.prepareStatement(query1);
				rs = pstmt.executeQuery();

				pstmt1 = con.prepareStatement(query2);
				rs1 = pstmt1.executeQuery();
				JOptionPane.showMessageDialog(null, "생성되었습니다.");
			} else { //  headCount와 storeCount의 값이 둘다 1이 아니면 ID가 존재한다는 뜻
				JOptionPane.showMessageDialog(null, "이미 존재하는 ID입니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	} else { // 매장
		try {

			checkHeadId = con.prepareStatement(checkHeadIdQuery);
			checkHeadIdRs = checkHeadId.executeQuery();

			checkStoreId = con.prepareStatement(checkHeadIdQuery);
			checkStoreIdRs = checkStoreId.executeQuery();

			
			// ID는 본사와 매장직원들의 ID가 겹치게 하지 않게 하기 위해 본사 매장 두군데에서 전부 중복검사 실시
			while (checkHeadIdRs.next()) { //본사와 마찬가지로 본사ID 중복검사를 하지만
									// 매장은 담당자ID를 입력 받으므로 입력받은 담당자ID가 본사ID에 있는지 확인
				
				//따라서 본사 ID중복검사시 입력 받은 담당자ID가 본사ID에 있는지 아래와같이 확인
				if (id.equals(checkHeadIdRs.getString(1))) {
					headCount = 0; // ID가 존재하면 headCount에 0을 저장하고 
					break; // while 종료 
				} else { //본사 ID가 존재하지 않으면(중복검사에서 통과하면)
					headCount = 1; // headCount에 1을 저장하고
					
					// 입력받은 담당자ID가 존재하는지 확인
					if (manager.equals(checkHeadIdRs.getString(1))) {
						checkManagerCount = 1; //만약 입력받은 담당자ID(매장만 입력받는 곳)가
												//본사ID에 존재하는 경우 checkManagerCount에 1을 저장
						break; //while 종료
					} else { // 입력받은 담당자ID가 본사ID중에서 없을경우
						checkManagerCount = 0; // checkManagerCount에 0을 저장
					}
				}
			}

			while (checkStoreIdRs.next()) { // 본사와 마찬가지로 매장ID 중복검사
				if (id.equals(checkStoreIdRs.getString(1))) { 
					storeCount = 0; // 매장ID에서 입력받은 ID가 존재히면 0을 저장하고
					break; // while 종료
				} else { // 중복검사 통과시
					storeCount = 1; // 1을 저장
				}
			} 

			if (headCount == 1 && storeCount == 1) { //본사ID와 매장ID에서 중복검사 통과시
				if (checkManagerCount == 1) { // 입력받은 담당자ID가 존재하면
					// 계정 및 매장생성(1개의 계정 생성시 해당 매장도 같이 생성, 1개 매장당 계정이 1개)
					query1 = "insert into manager values('" + id + "','" + password + "','" + personName + "','" + phone
							+ "')";
					query2 = "insert into store values('S" + sCode + "',2,'" + storeName + "','" + phone + "','"
							+ id + "','" + manager + "')";
					pstmt = con.prepareStatement(query1);
					rs = pstmt.executeQuery();

					pstmt1 = con.prepareStatement(query2);
					rs1 = pstmt1.executeQuery();
					JOptionPane.showMessageDialog(null, "생성되었습니다.");
				} else { // 입력받은 담당자 ID가 존재하지 않으면
					JOptionPane.showMessageDialog(null, "담당자 ID를 확인해주세요.");
				}
			} else { //본사 ID와 매장ID에서 입력받은 ID가 존재시
				JOptionPane.showMessageDialog(null, "이미 존재하는 ID입니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}

	public void searchAccount(JTable searchTable, String radio) {
		// 출력할 테이블, 라디오버튼(매장+본사, 매장, 본사 세개로 구분)

		String query;

		if (radio.equals("매장+본사")) {//라디오 버튼이 매장+본사일경우
			try {
				query = "select s_group,m_id,s_name,h_name,s_phone from store sr, head hd\r\n"
						+ "where sr.h_id = hd.h_id order by 1 desc";
				// 해당 테이블에 구분,id,매장명,담당자이름,전화번호를 출력(구분으로 내림차순)
				pstmt = con.prepareStatement(query);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int checkGroup = rs.getInt(1);// 구분에서 1은 본사, 2는 매장
					String group;
					if(checkGroup == 1) { // 구분이 1이면
						group = "본사";
					}else { // 2이면
						group = "매장";
					}
					String id = rs.getString(2);
					String storeName = rs.getString(3);
					String manager = rs.getString(4);
					String phone = rs.getString(5);
					Object data[] = { group, id, storeName, manager, phone };
					DefaultTableModel model = (DefaultTableModel) searchTable.getModel();
					model.addRow(data);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (radio.equals("매장")) { // 라디오버튼이 매장일 경우
			try {
				query = "select s_group,m_id,s_name,h_name,s_phone from store sr, head hd\r\n"
						+ "where sr.h_id = hd.h_id\r\n" + "and s_group = 2";
				pstmt = con.prepareStatement(query);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int checkGroup = rs.getInt(1);
					String group;
					if(checkGroup == 1) { // 구분이 1이면 본사
						group = "본사";
					}else { // 2면 매장
						group = "매장";
					}
					String id = rs.getString(2);
					String storeName = rs.getString(3);
					String manager = rs.getString(4);
					String phone = rs.getString(5);
					Object data[] = { group, id, storeName, manager, phone };
					DefaultTableModel model = (DefaultTableModel) searchTable.getModel();
					model.addRow(data);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (radio.equals("본사")) {// 라디오 버튼이 본사일경우
			try {
				query = "select s_group,m_id,s_name,h_name,s_phone from store sr, head hd\r\n"
						+ "where sr.h_id = hd.h_id\r\n" + "and s_group = 1";
				pstmt = con.prepareStatement(query);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int checkGroup = rs.getInt(1);
					String group;
					if(checkGroup == 1) { // 구븐이 1이면 본사
						group = "본사";
					}else { // 2면 매장
						group = "매장";
					}
					String id = rs.getString(2);
					String storeName = rs.getString(3);
					String manager = rs.getString(4);
					String phone = rs.getString(5);
					Object data[] = { group, id, storeName, manager, phone };
					DefaultTableModel model = (DefaultTableModel) searchTable.getModel();
					model.addRow(data);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

}
