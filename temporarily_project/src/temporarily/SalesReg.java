package temporarily;

/*
 * (����) �ǸŰ��� - �Ǹŵ��
 * 
 * ��ǰ�� ��ȸ�Ͽ� �Ǹ� ������ ��� Ȥ�� ���� �� �� �ִ�.
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
	
	String divComboArray[] = {"�Ǹ�","��ǰ"};
	String sizeComboArray[] = {"S","M","L","XL"};	
	
	//LocalDate currDate = LocalDate.now(); //���� ��¥
	LocalDate currDate = LocalDate.of(2018, 11, 1);
	
	String code = null;
	int dayTotalPrice = 0;
	
	private void setDBcon(DBcon dbcon) {
		myDBcon = dbcon;
	}
	
	public SalesReg(DBcon dbcon) {
		setDBcon(dbcon);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// 1 - ����
		JPanel p1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) p1.getLayout();
		flowLayout.setHgap(10);
		flowLayout.setVgap(10);
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(p1);
		titleLabel = new JLabel("�Ǹŵ��");
		titleLabel.setFont(new Font("����", Font.PLAIN, 18));
		p1.add(titleLabel);
		
		// 2 - �Ǹ�����, ���Ǹűݾ�
		String firstTableName[] = { "�Ǹ�����", "���Ǹűݾ�" };
		Object firstTableData[][] = { { currDate, dayTotalPrice } };
		firstTableModel = new DefaultTableModel(firstTableData, firstTableName){
			public boolean isCellEditable(int row, int col) {
				return false; // ���̺� ���� ���ϰ�
			}
		};
		firstTable = new JTable(firstTableModel);
		firstTable.getTableHeader().setReorderingAllowed(false); // ���̺� �� ����
		firstSc = new JScrollPane(firstTable);
		firstSc.setPreferredSize(new Dimension(450, 80));
		add(firstSc);
		
		// 3 - ��ǰ ���� ���� �� �Է�
		JPanel panel = new JPanel();	add(panel);
		JPanel p2 = new JPanel();
		panel.add(p2);
		p2.setLayout(new GridLayout(2, 9, 0, 5));
		
			// 1�� - ��ǰ ��ȸ
		divLabel = new JLabel(" ����");		p2.add(divLabel);		
		divCombo = new JComboBox<String>(divComboArray);
		p2.add(divCombo);
		
		noLabel = new JLabel(" ǰ��");	p2.add(noLabel);		
		productNoField = new JTextField();
		p2.add(productNoField);
		
		colorLabel = new JLabel(" ����"); 	p2.add(colorLabel);		
		colorCombo = new JComboBox<String>();
		dbcon.listColorCombo(colorCombo); // ���� �޺��ڽ� ���� �޼��� ȣ��
		p2.add(colorCombo);
		
		sizeLabel = new JLabel(" ������");	p2.add(sizeLabel);		
		sizeCombo = new JComboBox<String>(sizeComboArray);
		p2.add(sizeCombo);
		
		searchButton = new JButton("��ȸ");
		searchButton.addActionListener(this);
		p2.add(searchButton);
		
			// 2�� - ��ǰ ���
		productPriceLabel = new JLabel(" �ǸŴܰ�");	p2.add(productPriceLabel);		
		productPriceField = new JTextField("0");
		productPriceField.setEditable(false); // ���� �Ұ�
		p2.add(productPriceField);
		
		stockQuantityLabel = new JLabel(" ���");	p2.add(stockQuantityLabel);		
		stockQuantityField = new JTextField("0");
		stockQuantityField.setEditable(false); // ���� �Ұ�
		p2.add(stockQuantityField);
		
		salesQuantityLabel = new JLabel(" ����");	p2.add(salesQuantityLabel);		
		salesQuantityField = new JTextField("0");
		p2.add(salesQuantityField);
		
		salesPriceLabel = new JLabel(" ���Ǹűݾ�");	p2.add(salesPriceLabel);		
		salesPriceField = new JTextField("0");
		p2.add(salesPriceField);
		
		registrationButton = new JButton("���");
		registrationButton.addActionListener(this);
		p2.add(registrationButton);
				
		// 4 - �Ǹ� ��� ��Ȳ ���̺�
		String secTabName[] = { "��ȣ", "����", "ǰ��", "����", "������", "�ǸŴܰ�", "����", "���Ǹűݾ�"};
		Object secData[][] = new Object[0][8];
		secTableModel = new DefaultTableModel(secData, secTabName){
			public boolean isCellEditable(int row, int col) {
				return false; // ���̺� ���� ���ϰ�
			}
		};
		secTable = new JTable(secTableModel);
		secTable.getTableHeader().setReorderingAllowed(false); // ���̺� �� ����
		secSc = new JScrollPane(secTable);
		add(secSc);		
		dbcon.searchSalesStatus(secTable, currDate); // �Ǹ���Ȳ ���̺� 
		
			//���Ǹűݾ� ����, firstTable ������Ʈ
		dayTotalPrice = dbcon.getDayTotalPrice();		
		myDBcon.clear(firstTable);
		Object newData[] = { currDate, dayTotalPrice };
		DefaultTableModel newModel = (DefaultTableModel) firstTable.getModel();
		newModel.addRow(newData);
		
			// ��� ����
		JPanel p3 = new JPanel();
		add(p3);		
		deleteButton = new JButton("����");
		deleteButton.addActionListener(this);
		p3.add(deleteButton);

		// ���̺� ��� ����
		DefaultTableCellRenderer tCellRenderer = new DefaultTableCellRenderer();
		tCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		TableColumnModel t1ColModel = firstTable.getColumnModel();
		TableColumnModel t2ColModel = secTable.getColumnModel();

		for (int i = 0; i < t1ColModel.getColumnCount(); i++)
			t1ColModel.getColumn(i).setCellRenderer(tCellRenderer);
		for (int i = 0; i < t2ColModel.getColumnCount(); i++)
			t2ColModel.getColumn(i).setCellRenderer(tCellRenderer);
	}
	
	// �Ǹż���, ���ǸŰ� �������� üũ
	public boolean numberCheck(String salesQuantity, String salesPrice) {
		boolean checkResult = false;
		String regularEx = "[1-9]\\d*"; // ����ǥ���� - 0�� �ƴ� ����

		boolean salesQuantityCheck = Pattern.matches(regularEx, salesQuantity);
		boolean salesPriceCheck = Pattern.matches(regularEx, salesPrice);

		if (salesQuantityCheck && salesPriceCheck) {
			// �� �� �´� ����
			checkResult = true;
		} else {
			// 0���� �����ϰų� ���ڰ� �ƴ� ���
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
		
		// ��ȸ ��ư action
		if (e.getSource() == searchButton) {
			myDBcon.searchProduct(productNo,productColor,productSize); // ��ǰ��ȸ �޼��� ȣ��
			
			String productPrice = myDBcon.getProductPrice().toString();
			String stockQuantity = myDBcon.getStockQuantity().toString();
			salesQuantity = myDBcon.getSalesQuantity().toString();
			
			productPriceField.setText(productPrice);	
			stockQuantityField.setText(stockQuantity);
			salesQuantityField.setText(salesQuantity);	
			salesPriceField.setText(productPrice);	
		}
		
		// ��� ��ư action
		if (e.getSource() == registrationButton) {
			String productPrice = myDBcon.getProductPrice().toString();
			
			if(productPrice.equals("0")) {
				// ��ȸ�� ��ǰ�� ���� ���
				JOptionPane.showMessageDialog(null, "��ǰ ��ȸ �� ����� �����մϴ�.");
			} else {
				// ��ȸ�� ��ǰ�� ���� ���
				myDBcon.clear(secTable);
				myDBcon.searchProduct(productNo,productColor,productSize); // ��ǰ��ȸ �޼��� ȣ��
				productPrice = myDBcon.getProductPrice().toString();
				
				if(productPrice.equals("0")) {
					// ��� ��ǰ ������ �����Ǿ� ��ǰ�� ã�� �� ���� ��� - ���̺� �����ֱ�
					myDBcon.searchSalesStatus(secTable, currDate); // �Ǹ���Ȳ ���̺� 
				} else if (numberCheck(salesQuantity, salesPrice) == false){
					// ���� Ȥ�� ���ǸŰ��� 0���� �����ϰų� ���ڰ� �ƴ� ���
					JOptionPane.showMessageDialog(null, "����, ���ǸŰ��� Ȯ���ϼ���");
					myDBcon.searchSalesStatus(secTable, currDate); // �Ǹ���Ȳ ���̺� 
				} else {
					// ��� ����
					myDBcon.registerSales(firstTable,salesDiv,salesQuantity,salesPrice);
					
					// �ʵ� �ʱ�ȭ 
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
		
		// ���� ��ư action
		if (e.getSource() == deleteButton) {
			int row = secTable.getSelectedRow(); // ������ �� ��������
			
			if(secTable.getSelectedRow() >= 0) {
				// ������ ���� ���� ���
				String deleteSalesNum = (String) secTableModel.getValueAt(row,0);
				int deleteSalesPrice = (int) secTableModel.getValueAt(row,7);
				
				myDBcon.salesDelete(firstTable, deleteSalesNum, deleteSalesPrice);
				
				secTableModel.removeRow(secTable.getSelectedRow());
			} else {
				// ������ ���� ���� ���
				JOptionPane.showMessageDialog(null,"������ ���� Ŭ���ϼ���.");
			}			
		}
	}
}