package temporarily;

/*
 * (매장) 판매관리 - 판매현황 - 일판매현황
 * 
 * 판매현황에서 선택한 일자의 일판매현황을 팝업창으로 보여준다.
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
			// 1 - 판매일자, 총판매금액
			String firstTabName[] = { "판매일자", "총판매금액" };
			Object firstData[][] = { { selectedDate, "20,000" } };
			firstTabModel = new DefaultTableModel(firstData, firstTabName){
				public boolean isCellEditable(int row, int col) {
					return false; // 테이블 수정 못하게
				}
			};
			contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
			firstTab = new JTable(firstTabModel);
			firstTab.getTableHeader().setReorderingAllowed(false); // 테이블 열 고정
			firstSc = new JScrollPane(firstTab);
			firstSc.setPreferredSize(new Dimension(450, 80));
			contentPanel.add(firstSc);			
			
			// 2 - 판매 현황 테이블
			String secTabName[] = { "번호", "구분", "품번", "색상", "사이즈", "판매단가", "수량", "실판매금액"};
			Object secData[][] = new Object[0][8];
			secTabModel = new DefaultTableModel(secData, secTabName){
				public boolean isCellEditable(int row, int col) {
					return false; // 테이블 수정 못하게
				}
			};
			secTab = new JTable(secTabModel);
			secTab.getTableHeader().setReorderingAllowed(false); // 테이블 열 고정
			secSc = new JScrollPane(secTab);
			contentPanel.add(secSc);
			dbcon.searchSalesStatus(secTab, selectedDate);
			
				//총판매금액 수정
			int totalPrice = dbcon.getDayTotalPrice();		
			myDBcon.clear(firstTab);
			Object data[] = { selectedDate, totalPrice };
			DefaultTableModel model = (DefaultTableModel) firstTab.getModel();
			model.addRow(data);
			
			// 테이블 가운데 정렬
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
