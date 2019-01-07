package temporarily;

/*
 * (����) �ǸŰ��� - �Ǹ���Ȳ
 * 
 * �Ⱓ�� �����Ͽ� ���Ǹ���Ȳ �� �ش� �� �� ���Ǹűݾ� ��ȸ �����ϴ�.
 * ���ϴ� ������ ���̺� ���� ����Ŭ���ϸ� ���Ǹ���Ȳ�� �˾�â�� ��Ÿ����.
 * 
 */

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class SalesStatus extends JPanel implements ActionListener {
	private JLabel titleLabel, dateLabel, yearLabel, monthLabel, totalLabel;
	private DefaultTableModel firstTabModel;
	private JTable firstTab;
	private JScrollPane firstSc;
	private JComboBox<String> yearCombo, monthCombo;
	private JButton searchButton;

	private DBcon myDBcon;

	String yearArray[] = { "2018", "2019" };
	String monthArray[] = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };

	String totalSalesPrice = "0";

	private void setDBcon(DBcon dbcon) {
		myDBcon = dbcon;
	}

	public SalesStatus(DBcon dbcon) {
		setDBcon(dbcon);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// 1 - ����
		JPanel p1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) p1.getLayout();
		flowLayout.setHgap(10);
		flowLayout.setVgap(10);
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(p1);
		titleLabel = new JLabel("�Ǹ���Ȳ");
		p1.add(titleLabel);
		titleLabel.setFont(new Font("����", Font.PLAIN, 18));

		// 2 - �Ⱓ ����, ��ȸ
		JPanel p2 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) p2.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		add(p2);

		dateLabel = new JLabel("�� ��");
		dateLabel.setBounds(100, 100, 450, 300);
		p2.add(dateLabel);

		yearCombo = new JComboBox<String>(yearArray);
		p2.add(yearCombo);
		yearLabel = new JLabel("�� ");
		p2.add(yearLabel);

		monthCombo = new JComboBox<String>(monthArray);
		p2.add(monthCombo);
		monthLabel = new JLabel("�� ");
		p2.add(monthLabel);

		searchButton = new JButton("��ȸ");
		searchButton.addActionListener(this);
		p2.add(searchButton);

		// 3 - ��ȸ ���� ���̺�
		String firstTabName[] = { "����", "����", "�Ǹż���", "�ܰ��ݾ�", "���Ǹűݾ�", "�����ݾ�(���Ǹ�)" };
		Object firstData[][] = new Object[0][6];
		firstTabModel = new DefaultTableModel(firstData, firstTabName) {
			public boolean isCellEditable(int row, int col) {
				return false; // ���̺� ���� ���ϰ�
			}
		};
		firstTab = new JTable(firstTabModel);
		firstTab.getTableHeader().setReorderingAllowed(false); // ���̺� �� ����
		firstSc = new JScrollPane(firstTab);
		add(firstSc);

			// ���ϴ� ������ ���̺� �� ����Ŭ�� �� �̺�Ʈ
		firstTab.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = firstTab.getSelectedRow();
					Object selectedDate = firstTabModel.getValueAt(row, 0);
					OpenStatus op = new OpenStatus(myDBcon, selectedDate); // ���Ǹ���Ȳ �˾�
				}
			}
		});

		// 4 - �ش� �� �� ���Ǹűݾ�
		JPanel p3 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) p3.getLayout();
		flowLayout_1.setHgap(20);
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		add(p3);

		totalLabel = new JLabel();
		totalLabel.setText("�� ���Ǹűݾ�: " + totalSalesPrice);
		p3.add(totalLabel);

		// ���̺� ��� ����
		DefaultTableCellRenderer tCellRenderer = new DefaultTableCellRenderer();
		tCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		TableColumnModel t1ColModel = firstTab.getColumnModel();

		for (int i = 0; i < t1ColModel.getColumnCount(); i++)
			t1ColModel.getColumn(i).setCellRenderer(tCellRenderer);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String year = (String) yearCombo.getSelectedItem();
		String month = (String) monthCombo.getSelectedItem();
		String selectedDate = year.substring(2) + month;

		// ��ȸ ��ư action
		if (e.getSource() == searchButton) {
			myDBcon.clear(firstTab);
			
			myDBcon.searchStatus(firstTab, selectedDate); // ������ ���� ��¥�� ������ ��ȸ �޼���
			
			totalSalesPrice = myDBcon.getMonthTotalPrice().toString();
			totalLabel.setText("�� ���Ǹűݾ�: " + totalSalesPrice);
		}		
	}
}