package temporarily;

/*
 * (����) �ǸŰ��� - �Ǹ���Ȳ - ���Ǹ���Ȳ
 * 
 * �Ǹ���Ȳ���� ������ ������ ���Ǹ���Ȳ�� �˾�â���� �����ش�.
 * 
 */

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class OpenStatus extends JDialog {
	private final JPanel contentPanel = new JPanel();
	DefaultTableModel firstTabModel, secTabModel;
	JTable firstTab, secTab;
	JScrollPane firstSc, secSc;
	
	private DBcon myDBcon;
	
	private void setDBcon(DBcon dbcon) {
		myDBcon = dbcon;
	}

	public OpenStatus(DBcon dbcon, Object selectedDate) {
		setDBcon(dbcon);
		setBounds(100, 100, 450, 300);
		setSize(600, 400);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			// 1 - �Ǹ�����, ���Ǹűݾ�
			String firstTabName[] = { "�Ǹ�����", "���Ǹűݾ�" };
			Object firstData[][] = { { selectedDate, "20,000" } };
			firstTabModel = new DefaultTableModel(firstData, firstTabName){
				public boolean isCellEditable(int row, int col) {
					return false; // ���̺� ���� ���ϰ�
				}
			};
			contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
			firstTab = new JTable(firstTabModel);
			firstTab.getTableHeader().setReorderingAllowed(false); // ���̺� �� ����
			firstSc = new JScrollPane(firstTab);
			firstSc.setPreferredSize(new Dimension(450, 80));
			contentPanel.add(firstSc);			
			
			// 2 - �Ǹ� ��Ȳ ���̺�
			String secTabName[] = { "��ȣ", "����", "ǰ��", "����", "������", "�ǸŴܰ�", "����", "���Ǹűݾ�"};
			Object secData[][] = new Object[0][8];
			secTabModel = new DefaultTableModel(secData, secTabName){
				public boolean isCellEditable(int row, int col) {
					return false; // ���̺� ���� ���ϰ�
				}
			};
			secTab = new JTable(secTabModel);
			secTab.getTableHeader().setReorderingAllowed(false); // ���̺� �� ����
			secSc = new JScrollPane(secTab);
			contentPanel.add(secSc);
			dbcon.searchSalesStatus(secTab, selectedDate);
			
				//���Ǹűݾ� ����
			int totalPrice = dbcon.getDayTotalPrice();		
			myDBcon.clear(firstTab);
			Object data[] = { selectedDate, totalPrice };
			DefaultTableModel model = (DefaultTableModel) firstTab.getModel();
			model.addRow(data);
			
			// ���̺� ��� ����
			DefaultTableCellRenderer tCellRenderer = new DefaultTableCellRenderer();
			tCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);

			TableColumnModel t1ColModel = firstTab.getColumnModel();
			TableColumnModel t2ColModel = secTab.getColumnModel();

			for (int i = 0; i < t1ColModel.getColumnCount(); i++)
				t1ColModel.getColumn(i).setCellRenderer(tCellRenderer);
			for (int i = 0; i < t2ColModel.getColumnCount(); i++)
				t2ColModel.getColumn(i).setCellRenderer(tCellRenderer);
		}		
		
		setModal(true);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

}
