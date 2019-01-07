package temporarily;

/*
 * DB ���� �޼��� ����
 * 
 * �ֿ� ���
 * - DB ����� ����
 * - �α��� üũ
 * - ��ǰ �� ��� ��ȸ
 * - �Ǹ� ���, ���� 
 * - �Ǹ� ��Ȳ ��ȸ
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
	int loginCount; // �α��� ����. 1���� 0����
	int queryResultCount = 0; // ������ ��� ����. 1���� 0����

	String loginUser; // ���� �α����� ������ �����ڵ�
	String productCode; // ��ǰ ��ȸ �� ����� ���� ���� ����
	int productPrice, stockQuantity, salesQuantity; // ��ǰ ��ȸ �� ��ȯ�� ���� ���� ����

	JTable tableSave; // ������ ���̺� ���� ����
	int dayTotalPrice = 0; // �ϸ��� ���� ����
	int monthTotalPrice = 0; // ������ ���� ����
	String salesNum = "0"; // ���ǸŹ�ȣ ���庯��

	// LocalDate currDate = LocalDate.now(); // ���� ��¥
	LocalDate currDate = LocalDate.of(2018, 11, 1);
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
	String currDateCode = currDate.format(formatter);

	// DB ����
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;

	public DBcon() {
		connect();
	}

	// DB ����
	public void connect() {
		// String URL = "jdbc:oracle:thin:@localhost:1521:xe";
		String URL = "jdbc:oracle:thin:@localhost:1521:orcl";
		String ID = "project1";
		String PW = "pro1";

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(URL, ID, PW);
			System.out.println("DB����");

		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("DB���� ����");
			e.printStackTrace();
		}
	}

	// DB ���� ���� - �α׾ƿ�, â�ݱ� �� �� ����
	public void disconn() {
		try {
			System.out.println("DB ����");
			rs.close();
			pstmt.close();
			con.close();
		} catch (Exception e) {
			System.out.println("DB ���� ����");
		}
	}

	// *�α���
	// �α��� üũ
	public void checkLogin(String id, String pw, String divRadioResult) {
		String query;

		if (divRadioResult.equals("����")) {
			// ���� ���̺� �˻�
			query = "select m_id, m_pw from manager";
		} else {
			// ���� ���̺� �˻�
			query = "select h_id, h_pw from head";
		}
		try {
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				if (id.equals(rs.getString(1)) && pw.equals(rs.getString(2))) {
					// �α��� ������ ���� �ڵ� �˻�
					query = "select s_code from store where m_id='" + id + "'";

					pstmt = con.prepareStatement(query);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						// �α��� ����
						this.loginCount = 1;
						this.loginUser = rs.getString(1); // �α��� ������ ���� �ڵ� user�� �Է�
					}
					System.out.println("���� ����:" + loginUser);
					break;
				} else {
					// �α��� ����
					this.loginCount = 0;
				}
			}
		} catch (SQLException e) {
			System.out.println("checkLogin ����");
			e.printStackTrace();
		}
	}

	public Integer getLoginCount() {
		return loginCount; // �α��� ���� ���� LoginView�� ��ȯ
	}

	public String getLoginUser() {
		return loginUser; // �α��� ���� MainFrame�� ��ȯ
	}

	/****************************************************************/

	// �ǸŰ��� - �Ǹŵ�� - ���� �޺��ڽ�
	// ��ϵ� ��ǰ�� ��� �÷��� �޺��ڽ� ����Ʈ�� ����
	public void listColorCombo(JComboBox<String> colorCombo) {
		String query = "select distinct p_color from product";

		try {
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				colorCombo.addItem(rs.getString(1)); // �޺��ڽ� �����ۿ� �߰�
			}
			System.out.println("listColorCombo ����");
		} catch (SQLException e) {
			System.out.println("listColorCombo ����");
			e.printStackTrace();
		}
	}

	// �ǸŰ��� - �Ǹŵ�� - �Ǹ���Ȳ ���̺�
	// �ش� ��¥�� �Ǹ� ��Ȳ�� ���̺� �߰�
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
					// 1:�Ǹ�
					salesDivText = "�Ǹ�";
				} else {
					// 2:��ǰ
					salesDivText = "��ǰ";
				}

				String productNo = productCode.substring(0, 7);
				String productColor = productCode.substring(7, 9);
				String productSize = productCode.substring(9);

				dayTotalPrice += salesPrice;

				// ��ȸ ��� ���̺� �߰�
				Object newData[] = { salesNumber, salesDivText, productNo, productColor, productSize, productPrice,
						salesQuantity, salesPrice };
				DefaultTableModel newModel = (DefaultTableModel) statusTable.getModel();
				newModel.addRow(newData);
			}
			System.out.println("salesStatusSearch ����");
		} catch (SQLException e) {
			System.out.println("salesStatusSearch ����");
			e.printStackTrace();
		}
	}

	public Integer getDayTotalPrice() {
		return dayTotalPrice; // �� ���Ǹűݾ� SalesReg�� ��ȯ
	}

	/****************************************************************/

	// *�ǸŰ��� - �Ǹŵ�� - ��ȸ ��ư
	// ��ǰ ��ȸ �� �ǸŴܰ�,������,�ڵ� ����
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
				JOptionPane.showMessageDialog(null, "�ش� ��ǰ�� �����ϴ�.");

				this.productPrice = 0;
				this.stockQuantity = 0;
				this.salesQuantity = 0;

			} else {
				this.salesQuantity = 1;
				System.out.println("searchProduct ����");
			}
		} catch (SQLException e) {
			System.out.println("searchProduct ����");
			e.printStackTrace();
		}
	}

	public Integer getProductPrice() {
		return productPrice; // �ǸŴܰ� SalesReg, searchProduct�� ��ȯ
	}

	public Integer getStockQuantity() {
		return stockQuantity; // ������ SalesReg,StockSearch�� ��ȯ
	}

	public Integer getSalesQuantity() {
		return salesQuantity; // �Ǹż��� �⺻�� SalesReg�� ��ȯ
	}

	// *�ǸŰ��� - �Ǹŵ�� - ��� ��ư
	// ��ǰ �Ǹ�,��ǰ ������ ����
	public void registerSales(JTable totalTable, String salesDiv, String salesQuantity, String salesPrice) {
		int salesDivCode;

		if (salesDiv.equals("�Ǹ�")) {
			// �Ǹ�:1
			salesDivCode = 1;
		} else {
			// ��ǰ:2
			salesDivCode = 2;
			salesQuantity = "-" + salesQuantity;
			salesPrice = "-" + salesPrice;
			productPrice = 0 - productPrice;
		}

		// �� �ǸŹ�ȣ
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

			System.out.println("registerSales ����");
		} catch (SQLException e) {
			System.out.println("registerSales ����");
			e.printStackTrace();
		}
	}

	// *�ǸŰ��� - �Ǹŵ�� - ���� ��ư
	// ������ �� ������ ����
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

		// �Ǹ���Ȳ ���̺� ������ ����
		String query = "delete from sales where sa_code = '" + salesCode + "'";

		try {
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();

			clear(totalTable);
			Object newData[] = { currDate, dayTotalPrice };
			DefaultTableModel newModel = (DefaultTableModel) totalTable.getModel();
			newModel.addRow(newData);

			System.out.println("salesDelete ����");
		} catch (SQLException e) {
			System.out.println("salesDelete ����");
			e.printStackTrace();
		}
	}

	/****************************************************************/

	// *�ǸŰ��� - �Ǹ���Ȳ - ��ȸ ��ư
	// ������ ���� ��¥�� ������ ��ȸ
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
				JOptionPane.showMessageDialog(null, "��ȸ�� ����� �����ϴ�.");
			} else {
				System.out.println("searchStatus ����");
			}
		} catch (SQLException e) {
			System.out.println("searchStatus ����");
			e.printStackTrace();
		}
	}

	public Integer getMonthTotalPrice() {
		return monthTotalPrice; // ���� ���Ǹűݾ� SalesStatus�� ��ȯ
	}

	/****************************************************************/

	// *������ - �����ȸ - ��ȸ ��ư
	// �ش� ǰ�� ��� ��ȸ
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
				JOptionPane.showMessageDialog(null, "�ش� ��ǰ�� �����ϴ�.");
				this.productPrice = 0; // �ǸŴܰ� �ʱ�ȭ
			} else {
				System.out.println("searchStock ����");
			}
		} catch (SQLException e) {
			System.out.println("searchStock ����");
			e.printStackTrace();
		}
	}

	// ���峻 ��ü��� ��ȸ
	// public void searchStock(JTable table) {}

	// JTable �ʵ� �ʱ�ȭ
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
	// �Ż�ǰ���
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
			int checkCode = 0; // p_code �ߺ��˻�
			
			while(rs1.next()) {
				if(productCode.toUpperCase().equals(rs1.getString(1))) {
					checkCode = 0;
					break;
				}else {
					checkCode = 1;
				}
			}
			if(checkCode == 0) {
				JOptionPane.showMessageDialog(null, "�ش��ǰ�� �̹������մϴ�.");
			}else {
				pstmt = con.prepareStatement(query1);
				rs = pstmt.executeQuery();
				JOptionPane.showMessageDialog(null, "�ԷµǾ����ϴ�.");				
			}


		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// �ܰ����� - ǰ������ ��ǰ���� ��ȸ
	public void searchProduct(String productNo) {
		String query1 = "SELECT DISTINCT P_PRICE FROM PRODUCT WHERE P_NO =" + productNo;
		String query2 = "select p_no from product";
		PreparedStatement pstmt1 = null;
		ResultSet rs1 = null;
		
		try {
			pstmt1 = con.prepareStatement(query2);
			rs1 = pstmt1.executeQuery();
			int checkproductNo = 0; //ǰ���� �����ϴ��� �˻�
			
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
					// getPrice�� ǰ������ ��ȸ�� ��ǰ������ �����Ͽ� �ٽ� productPrice�� �Ѱ��ش�
				}				
			}else {
				JOptionPane.showMessageDialog(null, "�ش�ǰ���� ���������ʽ��ϴ�.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// �ܰ����� - �ǸŴܰ� ����
	public void updatePrice(String priceModify, String productNo) {
		String query1 = "UPDATE PRODUCT SET P_PRICE = " + priceModify + "WHERE P_NO = " + productNo;
		String query2 = "select p_no from product"; // ǰ���˻�
		PreparedStatement pstmt1 = null;
		ResultSet rs1 = null;
		
		try {
			pstmt1 = con.prepareStatement(query2);
			rs1 = pstmt1.executeQuery();
			int checkproductNo = 0; //ǰ���� �����ϴ��� �˻�
			
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
				JOptionPane.showMessageDialog(null, "����Ǿ����ϴ�.");				
			}else {
				JOptionPane.showMessageDialog(null, "ǰ���� Ȯ�����ּ���.");
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
		String query2 = "select p_no from product"; // ǰ���˻�
		PreparedStatement pstmt1 = null;
		ResultSet rs1 = null;
		


		try {
			pstmt1 = con.prepareStatement(query2);
			rs1 = pstmt1.executeQuery();
			
			int checkproductNo = 0; //ǰ���� �����ϴ��� �˻�
			
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
				JOptionPane.showMessageDialog(null, "�������� �ʴ� ǰ���Դϴ�.");				

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
					// ���� �������� ���
					nullSizeText = nullSizeText + sizeArray[i] + " ";
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println(updateSizeText);
		JOptionPane.showMessageDialog(null, updateSizeText + "������� �����Ǿ����ϴ�. \n" + nullSizeText + "������� ��ǰ ����� �ʿ��մϴ�.");
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

		if (radio.equals("����")) {
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
					JOptionPane.showMessageDialog(null, "�����Ǿ����ϴ�.");
				} else {
					JOptionPane.showMessageDialog(null, "�̹� �����ϴ� ID�Դϴ�.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else { // ����
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
						JOptionPane.showMessageDialog(null, "�����Ǿ����ϴ�.");
					} else {
						JOptionPane.showMessageDialog(null, "����� ID�� Ȯ�����ּ���.");
					}
				} else {
					JOptionPane.showMessageDialog(null, "�̹� �����ϴ� ID�Դϴ�.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

	public void searchAccount(JTable searchTable, String radio) {
		String query;

		if (radio.equals("����+����")) {
			try {
				query = "select s_group,m_id,s_name,h_name,s_phone from store sr, head hd\r\n"
						+ "where sr.h_id = hd.h_id order by 1";
				pstmt = con.prepareStatement(query);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int checkGroup = rs.getInt(1);
					String group;
					if(checkGroup == 1) {
						group = "����";
					}else {
						group = "����";
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
		} else if (radio.equals("����")) {
			try {
				query = "select s_group,m_id,s_name,h_name,s_phone from store sr, head hd\r\n"
						+ "where sr.h_id = hd.h_id\r\n" + "and s_group = 2";
				pstmt = con.prepareStatement(query);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int checkGroup = rs.getInt(1);
					String group;
					if(checkGroup == 1) {
						group = "����";
					}else {
						group = "����";
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
		} else if (radio.equals("����")) {
			try {
				query = "select s_group,m_id,s_name,h_name,s_phone from store sr, head hd\r\n"
						+ "where sr.h_id = hd.h_id\r\n" + "and s_group = 1";
				pstmt = con.prepareStatement(query);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int checkGroup = rs.getInt(1);
					String group;
					if(checkGroup == 1) {
						group = "����";
					}else {
						group = "����";
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
