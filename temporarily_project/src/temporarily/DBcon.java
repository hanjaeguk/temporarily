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
			String productPrice) {

		String query1 = "INSERT INTO PRODUCT VALUES(UPPER('" + productCode + "')," + productNo + ",UPPER('"
				+ productColor + "'),UPPER('" + productSize + "')," + productPrice + ")";

		String query2 = "select p_code from product";

		PreparedStatement pstmt1 = null;
		ResultSet rs1 = null;

		try {
			pstmt1 = con.prepareStatement(query2);
			rs1 = pstmt1.executeQuery();
			int checkCode = 0; // p_code 중복검사
			
			while(rs1.next()) {
				if(productCode.toUpperCase().equals(rs1.getString(1))) {
					checkCode = 0;
					break;
				}else {
					checkCode = 1;
				}
			}
			if(checkCode == 0) {
				JOptionPane.showMessageDialog(null, "해당상품이 이미존재합니다.");
			}else {
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
			
			while(rs1.next()) {
				if(productNo.equals(rs1.getString(1))) {
					checkproductNo = 1;
					break;
				}else {
					checkproductNo = 0;
				}
			}
			
			if(checkproductNo == 1) {
				pstmt = con.prepareStatement(query1);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int getPrice = rs.getInt(1);
					this.productPrice = getPrice;
					// getPrice에 품번으로 조회한 상품가격을 저장하여 다시 productPrice로 넘겨준다
				}				
			}else {
				JOptionPane.showMessageDialog(null, "해당품번이 존재하지않습니다.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 단가수정 - 판매단가 수정
	public void updatePrice(String priceModify, String productNo) {
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
			
			if(checkproductNo == 1) {
				pstmt = con.prepareStatement(query1);
				rs = pstmt.executeQuery();
				JOptionPane.showMessageDialog(null, "변경되었습니다.");				
			}else {
				JOptionPane.showMessageDialog(null, "품번을 확인해주세요.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//
	public void getStoreNameCombobox(JComboBox storeComboBox) {
		String query = "select distinct sr.s_name from store sr, stock sc "
				+ "where sr.s_code = sc.s_code and sc.p_qty > 0";
		try {
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				storeComboBox.addItem(rs.getString(1));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchStockColor(JComboBox colorComboBox, JComboBox getStoreComboBox, String productNo) {
		String query1 = "select distinct pro.p_color from product pro,stock sc,store sr\r\n"
				+ "Where pro.p_code = sc.p_code and\r\n" + "sr.s_code = sc.s_code and\r\n" + "pro.p_no =" + productNo
				+ "\r\n" + "and sr.s_name ='" + getStoreComboBox.getSelectedItem() + "'";
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
			
			if(checkproductNo == 1) {
				pstmt = con.prepareStatement(query1);
				rs = pstmt.executeQuery();				
				while (rs.next()) {
					colorComboBox.addItem(rs.getString(1));
				}
			}else {
				JOptionPane.showMessageDialog(null, "존재하지 않는 품번입니다.");				

			}


		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchSize(String storeName, String productNo, String productColor, String productSize) {
		String query = "select sc.p_qty from product pro,stock sc,store sr\r\n" + "Where pro.p_code = sc.p_code and\r\n"
				+ "sr.s_code=sc.s_code and sr.s_name = '" + storeName + "'and pro.p_no =" + productNo
				+ "and pro.p_color = '" + productColor + "' and pro.p_size = '" + productSize + "'";

		try {

			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();

//				while(rs.next()) {
//					int qt = rs.getInt(1);
//					this.qty = qt;
//				}

			if (rs.next()) {
				int getStockQuantity = rs.getInt(1);
				this.stockQuantity = getStockQuantity;
			} else {
				this.stockQuantity = 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void updateStock(String stockModift, String storeName, String productNo, String productColor) {
		String[] sizeArray = { "S", "M", "L", "XL" };
		String updateSizeText = "";
		String nullSizeText = "";
		

		for (int i = 0; i < sizeArray.length; i++) {
			queryResultCount = 0;
			String query1 = "select p_code from product \r\n" + "where p_no = " + productNo + " and p_color = '"
					+ productColor + "' \r\n" + "and p_size = '" + sizeArray[i] + "'";

			String query2 = "update stock\r\n" + "set p_qty = " + stockModift + "\r\n"
					+ "where stock.p_code in(select p_code from product \r\n" + "where p_no = " + productNo
					+ " and p_color = '" + productColor + "' and p_size = '" + sizeArray[i] + "') \r\n"
					+ "and stock.s_code in(select s_code from store where s_name = '" + storeName + "')";
			try {
				pstmt = con.prepareStatement(query1);
				rs = pstmt.executeQuery();

				while (rs.next()) {
					pstmt = con.prepareStatement(query2);
					ResultSet rs2 = pstmt.executeQuery();

					queryResultCount = 1;
					updateSizeText = updateSizeText + sizeArray[i] + " ";
				} 
		

				if (queryResultCount == 0) {
					// 없는 사이즈일 경우
					nullSizeText = nullSizeText + sizeArray[i] + " ";
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println(updateSizeText);
		JOptionPane.showMessageDialog(null, updateSizeText + "사이즈는 수정되었습니다. \n" + nullSizeText + "사이즈는 상품 등록이 필요합니다.");
	}

	public void createAccount(String id, String password, String personName, String phone, String storeName,
			String manager, String radio) {

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
		String checkStoreIdQuery = "select m_id from manager";
		String checkHeadIdQuery = "select h_id from head";
		String createScodeQuery = "SELECT round(DBMS_RANDOM.VALUE(1,10)*1000) RANDOM FROM dual";
		String checkScodeQuery = "select SUBSTR(s_code,2,5) from store";
		int headCount = 0;
		int storeCount = 0;
		int checkManagerCount = 0;
		String sCode = null;

		try {

			checkScode = con.prepareStatement(checkScodeQuery);
			checkScodeRs = checkScode.executeQuery();

			while (checkScodeRs.next()) {
				createScode = con.prepareStatement(createScodeQuery);
				createScodeRs = createScode.executeQuery();
				while (createScodeRs.next()) {
					if (checkScodeRs.getString(1) != createScodeRs.getString(1)) {
						sCode = createScodeRs.getString(1);
						break;
					} else {
						continue;
					}
				}
				if (sCode != null) {
					break;
				} else {
					continue;
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		if (radio.equals("본사")) {
			try {
				checkHeadId = con.prepareStatement(checkHeadIdQuery);
				checkHeadIdRs = checkHeadId.executeQuery();

				checkStoreId = con.prepareStatement(checkHeadIdQuery);
				checkStoreIdRs = checkStoreId.executeQuery();

				while (checkHeadIdRs.next()) {
					if (id.equals(checkHeadIdRs.getString(1)) || id.isEmpty()) {
						headCount = 0;
						break;
					} else {
						headCount = 1;
					}
				}

				while (checkStoreIdRs.next()) {
					if (id.equals(checkStoreIdRs.getString(1))) {
						storeCount = 0;
						break;
					} else {
						storeCount = 1;
					}
				}

				if (headCount == 1 && storeCount == 1) {
					query1 = "insert into head values('" + id + "','" + password + "','" + personName + "','" + phone
							+ "')";
					query2 = "insert into store values('H" + sCode + "',1,'" + storeName + "','" + phone + "','" + id
							+ "','" + id + "')";
					pstmt = con.prepareStatement(query1);
					rs = pstmt.executeQuery();

					pstmt1 = con.prepareStatement(query2);
					rs1 = pstmt1.executeQuery();
					JOptionPane.showMessageDialog(null, "생성되었습니다.");
				} else {
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

				while (checkHeadIdRs.next()) {
					if (id.equals(checkHeadIdRs.getString(1))) {
						headCount = 0;
						break;
					} else {
						headCount = 1;
						if (manager.equals(checkHeadIdRs.getString(1))) {
							checkManagerCount = 1;
							break;
						} else {
							checkManagerCount = 0;
						}
					}
				}

				while (checkStoreIdRs.next()) {
					if (id.equals(checkStoreIdRs.getString(1))) {
						storeCount = 0;
						break;
					} else {
						storeCount = 1;
					}
				}

				if (headCount == 1 && storeCount == 1) {
					if (checkManagerCount == 1) {
						query1 = "insert into manager values('" + id + "','" + password + "','" + personName + "','"
								+ phone + "')";
						query2 = "insert into store values('S" + sCode + "',2,'" + storeName + "','" + phone + "','"
								+ id + "','" + manager + "')";
						pstmt = con.prepareStatement(query1);
						rs = pstmt.executeQuery();

						pstmt1 = con.prepareStatement(query2);
						rs1 = pstmt1.executeQuery();
						JOptionPane.showMessageDialog(null, "생성되었습니다.");
					} else {
						JOptionPane.showMessageDialog(null, "담당자 ID를 확인해주세요.");
					}
				} else {
					JOptionPane.showMessageDialog(null, "이미 존재하는 ID입니다.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

	public void searchAccount(JTable searchTable, String radio) {
		String query;

		if (radio.equals("매장+본사")) {
			try {
				query = "select s_group,m_id,s_name,h_name,s_phone from store sr, head hd\r\n"
						+ "where sr.h_id = hd.h_id order by 1";
				pstmt = con.prepareStatement(query);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int checkGroup = rs.getInt(1);
					String group;
					if(checkGroup == 1) {
						group = "본사";
					}else {
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
		} else if (radio.equals("매장")) {
			try {
				query = "select s_group,m_id,s_name,h_name,s_phone from store sr, head hd\r\n"
						+ "where sr.h_id = hd.h_id\r\n" + "and s_group = 2";
				pstmt = con.prepareStatement(query);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int checkGroup = rs.getInt(1);
					String group;
					if(checkGroup == 1) {
						group = "본사";
					}else {
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
		} else if (radio.equals("본사")) {
			try {
				query = "select s_group,m_id,s_name,h_name,s_phone from store sr, head hd\r\n"
						+ "where sr.h_id = hd.h_id\r\n" + "and s_group = 1";
				pstmt = con.prepareStatement(query);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int checkGroup = rs.getInt(1);
					String group;
					if(checkGroup == 1) {
						group = "본사";
					}else {
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
