package temporarily;

/*
 * (매장) 판매관리 - 판매등록
 * 
 * 상품을 조회하여 판매 내용을 등록 혹은 삭제 할 수 있다.
 * 
 */

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class SalesReg extends JPanel implements ActionListener{
	private DefaultTableModel firstTableModel, secTableModel;	
	private JTable firstTable, secTable;	
	private JScrollPane firstSc, secSc;
	private JLabel titleLabel, divLabel, noLabel, colorLabel, sizeLabel, 
				productPriceLabel, stockQuantityLabel, salesQuantityLabel, salesPriceLabel;
	private JTextField productNoField, productPriceField, stockQuantityField, 
				salesQuantityField, salesPriceField;
	private JButton searchButton, registrationButton, deleteButton;
	private JComboBox<String> divCombo, colorCombo, sizeCombo;
	
	private DBcon myDBcon;
	
	String divComboArray[] = {"판매","반품"};
	String sizeComboArray[] = {"S","M","L","XL"};	
	
	//LocalDate currDate = LocalDate.now(); //오늘 날짜
	LocalDate currDate = LocalDate.of(2018, 11, 1);
	
	String code = null;
	int dayTotalPrice = 0;
	
	private void setDBcon(DBcon dbcon) {
		myDBcon = dbcon;
	}
	
	public SalesReg(DBcon dbcon) {
		setDBcon(dbcon);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// 1 - 제목
		JPanel p1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) p1.getLayout();
		flowLayout.setHgap(10);
		flowLayout.setVgap(10);
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(p1);
		titleLabel = new JLabel("판매등록");
		titleLabel.setFont(new Font("굴림", Font.PLAIN, 18));
		p1.add(titleLabel);
		
		// 2 - 판매일자, 총판매금액
		String firstTableName[] = { "판매일자", "총판매금액" };
		Object firstTableData[][] = { { currDate, dayTotalPrice } };
		firstTableModel = new DefaultTableModel(firstTableData, firstTableName){
			public boolean isCellEditable(int row, int col) {
				return false; // 테이블 수정 못하게
			}
		};
		firstTable = new JTable(firstTableModel);
		firstTable.getTableHeader().setReorderingAllowed(false); // 테이블 열 고정
		firstSc = new JScrollPane(firstTable);
		firstSc.setPreferredSize(new Dimension(450, 80));
		add(firstSc);
		
		// 3 - 상품 정보 선택 및 입력
		JPanel panel = new JPanel();	add(panel);
		JPanel p2 = new JPanel();
		panel.add(p2);
		p2.setLayout(new GridLayout(2, 9, 0, 5));
		
			// 1행 - 상품 조회
		divLabel = new JLabel(" 구분");		p2.add(divLabel);		
		divCombo = new JComboBox<String>(divComboArray);
		p2.add(divCombo);
		
		noLabel = new JLabel(" 품번");	p2.add(noLabel);		
		productNoField = new JTextField();
		p2.add(productNoField);
		
		colorLabel = new JLabel(" 색상"); 	p2.add(colorLabel);		
		colorCombo = new JComboBox<String>();
		dbcon.listColorCombo(colorCombo); // 색상 콤보박스 연동 메서드 호출
		p2.add(colorCombo);
		
		sizeLabel = new JLabel(" 사이즈");	p2.add(sizeLabel);		
		sizeCombo = new JComboBox<String>(sizeComboArray);
		p2.add(sizeCombo);
		
		searchButton = new JButton("조회");
		searchButton.addActionListener(this);
		p2.add(searchButton);
		
			// 2행 - 상품 등록
		productPriceLabel = new JLabel(" 판매단가");	p2.add(productPriceLabel);		
		productPriceField = new JTextField("0");
		productPriceField.setEditable(false); // 수정 불가
		p2.add(productPriceField);
		
		stockQuantityLabel = new JLabel(" 재고");	p2.add(stockQuantityLabel);		
		stockQuantityField = new JTextField("0");
		stockQuantityField.setEditable(false); // 수정 불가
		p2.add(stockQuantityField);
		
		salesQuantityLabel = new JLabel(" 수량");	p2.add(salesQuantityLabel);		
		salesQuantityField = new JTextField("0");
		p2.add(salesQuantityField);
		
		salesPriceLabel = new JLabel(" 실판매금액");	p2.add(salesPriceLabel);		
		salesPriceField = new JTextField("0");
		p2.add(salesPriceField);
		
		registrationButton = new JButton("등록");
		registrationButton.addActionListener(this);
		p2.add(registrationButton);
				
		// 4 - 판매 등록 현황 테이블
		String secTabName[] = { "번호", "구분", "품번", "색상", "사이즈", "판매단가", "수량", "실판매금액"};
		Object secData[][] = new Object[0][8];
		secTableModel = new DefaultTableModel(secData, secTabName){
			public boolean isCellEditable(int row, int col) {
				return false; // 테이블 수정 못하게
			}
		};
		secTable = new JTable(secTableModel);
		secTable.getTableHeader().setReorderingAllowed(false); // 테이블 열 고정
		secSc = new JScrollPane(secTable);
		add(secSc);		
		dbcon.searchSalesStatus(secTable, currDate); // 판매현황 테이블 
		
			//총판매금액 수정, firstTable 업데이트
		dayTotalPrice = dbcon.getDayTotalPrice();		
		myDBcon.clear(firstTable);
		Object newData[] = { currDate, dayTotalPrice };
		DefaultTableModel newModel = (DefaultTableModel) firstTable.getModel();
		newModel.addRow(newData);
		
			// 등록 삭제
		JPanel p3 = new JPanel();
		add(p3);		
		deleteButton = new JButton("삭제");
		deleteButton.addActionListener(this);
		p3.add(deleteButton);

		// 테이블 가운데 정렬
		DefaultTableCellRenderer tCellRenderer = new DefaultTableCellRenderer();
		tCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		TableColumnModel t1ColModel = firstTable.getColumnModel();
		TableColumnModel t2ColModel = secTable.getColumnModel();

		for (int i = 0; i < t1ColModel.getColumnCount(); i++)
			t1ColModel.getColumn(i).setCellRenderer(tCellRenderer);
		for (int i = 0; i < t2ColModel.getColumnCount(); i++)
			t2ColModel.getColumn(i).setCellRenderer(tCellRenderer);
	}
	
	// 판매수량, 실판매가 숫자인지 체크
	public boolean numberCheck(String salesQuantity, String salesPrice) {
		boolean checkResult = false;
		String regularEx = "[1-9]\\d*"; // 정규표현식 - 0이 아닌 숫자

		boolean salesQuantityCheck = Pattern.matches(regularEx, salesQuantity);
		boolean salesPriceCheck = Pattern.matches(regularEx, salesPrice);

		if (salesQuantityCheck && salesPriceCheck) {
			// 둘 다 맞는 숫자
			checkResult = true;
		} else {
			// 0으로 시작하거나 숫자가 아닐 경우
			checkResult = false;
		}

		return checkResult;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String salesDiv = (String)divCombo.getSelectedItem();
		String productNo = productNoField.getText();
		String productColor = (String)colorCombo.getSelectedItem();
		String productSize = (String)sizeCombo.getSelectedItem();
		String salesQuantity = salesQuantityField.getText();
		String salesPrice = salesPriceField.getText();
		
		// 조회 버튼 action
		if (e.getSource() == searchButton) {
			myDBcon.searchProduct(productNo,productColor,productSize); // 상품조회 메서드 호출
			
			String productPrice = myDBcon.getProductPrice().toString();
			String stockQuantity = myDBcon.getStockQuantity().toString();
			salesQuantity = myDBcon.getSalesQuantity().toString();
			
			productPriceField.setText(productPrice);	
			stockQuantityField.setText(stockQuantity);
			salesQuantityField.setText(salesQuantity);	
			salesPriceField.setText(productPrice);	
		}
		
		// 등록 버튼 action
		if (e.getSource() == registrationButton) {
			String productPrice = myDBcon.getProductPrice().toString();
			
			if(productPrice.equals("0")) {
				// 조회한 상품이 없을 경우
				JOptionPane.showMessageDialog(null, "상품 조회 후 등록이 가능합니다.");
			} else {
				// 조회한 상품이 있을 경우
				myDBcon.clear(secTable);
				myDBcon.searchProduct(productNo,productColor,productSize); // 상품조회 메서드 호출
				productPrice = myDBcon.getProductPrice().toString();
				
				if(productPrice.equals("0")) {
					// 등록 상품 정보가 수정되어 상품을 찾을 수 없을 경우 - 테이블만 보여주기
					myDBcon.searchSalesStatus(secTable, currDate); // 판매현황 테이블 
				} else if (numberCheck(salesQuantity, salesPrice) == false){
					// 수량 혹은 실판매가가 0으로 시작하거나 숫자가 아닐 경우
					JOptionPane.showMessageDialog(null, "수량, 실판매가를 확인하세요");
					myDBcon.searchSalesStatus(secTable, currDate); // 판매현황 테이블 
				} else {
					// 등록 가능
					myDBcon.registerSales(firstTable,salesDiv,salesQuantity,salesPrice);
					
					// 필드 초기화 
					divCombo.setSelectedIndex(0);
					productNoField.setText("");
					colorCombo.setSelectedIndex(0);
					sizeCombo.setSelectedIndex(0);
					productPriceField.setText("");	
					stockQuantityField.setText("");
					salesQuantityField.setText("");	
					salesPriceField.setText("");
				}
			}
		}	
		
		// 삭제 버튼 action
		if (e.getSource() == deleteButton) {
			int row = secTable.getSelectedRow(); // 선택한 행 가져오기
			
			if(secTable.getSelectedRow() >= 0) {
				// 선택한 행이 있을 경우
				String deleteSalesNum = (String) secTableModel.getValueAt(row,0);
				int deleteSalesPrice = (int) secTableModel.getValueAt(row,7);
				
				myDBcon.salesDelete(firstTable, deleteSalesNum, deleteSalesPrice);
				
				secTableModel.removeRow(secTable.getSelectedRow());
			} else {
				// 선택한 행이 없을 경우
				JOptionPane.showMessageDialog(null,"삭제할 행을 클릭하세요.");
			}			
		}
	}
}