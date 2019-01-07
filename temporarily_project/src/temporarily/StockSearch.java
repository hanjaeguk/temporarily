package temporarily;

/*
 * (����,����) ������ - �����ȸ
 * 
 * �˻��� ǰ���� �� ������ ���� �ǸŴܰ��� Ȯ���� �� �ִ�.
 * 
 */

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JTextField;

public class StockSearch extends JPanel implements ActionListener {
	private DefaultTableModel firstTabModel;
	private JTable firstTab;
	private JScrollPane firstSc;
	private JButton searchButton;
	private JLabel titleLabel, productNoLabel, priceLabel, productPriceLabel;
	private JTextField productNoField;
	
	private DBcon myDBcon;
	
	String productPrice = "0";
	
	private void setDBcon(DBcon dbcon) {
		myDBcon = dbcon;
	}
	
	public StockSearch(DBcon dbcon) {
		setDBcon(dbcon);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// 1 - ����
		JPanel p1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) p1.getLayout();
		flowLayout.setHgap(10);
		flowLayout.setVgap(10);
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(p1);
		titleLabel = new JLabel("�����ȸ");
		p1.add(titleLabel);
		titleLabel.setFont(new Font("����", Font.PLAIN, 18));

		// 2 - ǰ�� �Է� �� ��ȸ
		JPanel p2 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) p2.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		add(p2);

		productNoLabel = new JLabel("ǰ��");
		p2.add(productNoLabel);
		productNoField = new JTextField();
		productNoField.setColumns(10);
		p2.add(productNoField);

		searchButton = new JButton("��ȸ");
		searchButton.addActionListener(this);
		p2.add(searchButton);
		
		priceLabel = new JLabel(" �ǸŴܰ� : ");
		p2.add(priceLabel);		
		productPriceLabel = new JLabel(productPrice);
		p2.add(productPriceLabel);

		// 3 - �� �г� (���̾ƿ��� ����)
		JPanel p3 = new JPanel();
		FlowLayout fl_p3 = (FlowLayout) p3.getLayout();
		fl_p3.setAlignment(FlowLayout.RIGHT);
		add(p3);

		// 4 - ��� ��ȸ ���̺�
		String firstTabName[] = { "����", "������", "�����ڵ�", "�����", "��ȭ��ȣ", "���" };
		Object firstData[][] = new Object[0][6];
		firstTabModel = new DefaultTableModel(firstData, firstTabName){
			public boolean isCellEditable(int row, int col) {
				return false; // ���̺� ���� ���ϰ�
			}
		};
		firstTab = new JTable(firstTabModel);
		firstTab.getTableHeader().setReorderingAllowed(false); // ���̺� �� ����
		firstSc = new JScrollPane(firstTab);
		add(firstSc);

		// ���̺� ��� ����
		DefaultTableCellRenderer tCellRenderer = new DefaultTableCellRenderer();
		tCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		TableColumnModel t1ColModel = firstTab.getColumnModel();

		for (int i = 0; i < t1ColModel.getColumnCount(); i++)
			t1ColModel.getColumn(i).setCellRenderer(tCellRenderer);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// ��ȸ ��ư action
		if (e.getSource() == searchButton) {
			String productNo = productNoField.getText();
			
			myDBcon.clear(firstTab);
			myDBcon.searchStock(firstTab,productNo);
			productPrice = myDBcon.getProductPrice().toString();
			productPriceLabel.setText(productPrice);
		}
	}
}