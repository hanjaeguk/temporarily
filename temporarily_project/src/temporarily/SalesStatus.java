package temporarily;

/*
 * (매장) 판매관리 - 판매현황
 * 
 * 기간을 선택하여 월판매현황 및 해당 월 총 실판매금액 조회 가능하다.
 * 원하는 일자의 테이블 행을 더블클릭하면 일판매현황이 팝업창에 나타난다.
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

		// 1 - 제목
		JPanel p1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) p1.getLayout();
		flowLayout.setHgap(10);
		flowLayout.setVgap(10);
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(p1);
		titleLabel = new JLabel("판매현황");
		p1.add(titleLabel);
		titleLabel.setFont(new Font("굴림", Font.PLAIN, 18));

		// 2 - 기간 선택, 조회
		JPanel p2 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) p2.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		add(p2);

		dateLabel = new JLabel("기 간");
		dateLabel.setBounds(100, 100, 450, 300);
		p2.add(dateLabel);

		yearCombo = new JComboBox<String>(yearArray);
		p2.add(yearCombo);
		yearLabel = new JLabel("년 ");
		p2.add(yearLabel);

		monthCombo = new JComboBox<String>(monthArray);
		p2.add(monthCombo);
		monthLabel = new JLabel("월 ");
		p2.add(monthLabel);

		searchButton = new JButton("조회");
		searchButton.addActionListener(this);
		p2.add(searchButton);

		// 3 - 조회 내용 테이블
		String firstTabName[] = { "일자", "요일", "판매수량", "단가금액", "실판매금액", "누적금액(실판매)" };
		Object firstData[][] = new Object[0][6];
		firstTabModel = new DefaultTableModel(firstData, firstTabName) {
			public boolean isCellEditable(int row, int col) {
				return false; // 테이블 수정 못하게
			}
		};
		firstTab = new JTable(firstTabModel);
		firstTab.getTableHeader().setReorderingAllowed(false); // 테이블 열 고정
		firstSc = new JScrollPane(firstTab);
		add(firstSc);

			// 원하는 일자의 테이블 행 더블클릭 시 이벤트
		firstTab.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = firstTab.getSelectedRow();
					Object selectedDate = firstTabModel.getValueAt(row, 0);
					OpenStatus op = new OpenStatus(myDBcon, selectedDate); // 일판매현황 팝업
				}
			}
		});

		// 4 - 해당 월 총 실판매금액
		JPanel p3 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) p3.getLayout();
		flowLayout_1.setHgap(20);
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		add(p3);

		totalLabel = new JLabel();
		totalLabel.setText("총 실판매금액: " + totalSalesPrice);
		p3.add(totalLabel);

		// 테이블 가운데 정렬
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

		// 조회 버튼 action
		if (e.getSource() == searchButton) {
			myDBcon.clear(firstTab);
			
			myDBcon.searchStatus(firstTab, selectedDate); // 선택한 월의 날짜별 데이터 조회 메서드
			
			totalSalesPrice = myDBcon.getMonthTotalPrice().toString();
			totalLabel.setText("총 실판매금액: " + totalSalesPrice);
		}		
	}
}