package temporarily;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.GridLayout;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.SwingConstants;

public class AccountLookupCreate extends JPanel implements ActionListener {
	private JTable searchAccountTable;

	private DBcon myDBcon;
	private JTextField idField;
	private JTextField passwordField;
	private JTextField storeNameField;
	private JTextField personNameField;
	private JTextField phoneField;
	private JTextField managerField;
	private JButton createButton;
	private JRadioButton[] radioButton1 = new JRadioButton[2];
	private JRadioButton[] radioButton2 = new JRadioButton[3];
	String[] radioText1 = { "매장", "본사" };
	String[] radioText2 = { "매장+본사", "매장", "본사" };
	String radio1 = "매장";
	String radio2 = "매장+본사";

	private void setDBcon(DBcon dbcon) {
		myDBcon = dbcon;
	}

	public AccountLookupCreate(DBcon dbcon) {
		setDBcon(dbcon);
		setLayout(null);

		JLabel createAccountLabel = new JLabel("계정 생성");
		createAccountLabel.setFont(new Font("굴림", Font.BOLD, 20));
		createAccountLabel.setBounds(12, 10, 99, 32);
		add(createAccountLabel);

		JPanel radiobuttonPanel1 = new JPanel();
		radiobuttonPanel1.setBounds(323, 10, 183, 32);
		add(radiobuttonPanel1);

		ButtonGroup radioGroup1 = new ButtonGroup();
		for (int i = 0; i < radioButton1.length; i++) {
			radioButton1[i] = new JRadioButton(radioText1[i]);
			radioGroup1.add(radioButton1[i]);
			radiobuttonPanel1.add(radioButton1[i]);
			radioButton1[i].addActionListener(this);
		}
		radioButton1[0].setSelected(true);

		radiobuttonPanel1.setVisible(true);

		createButton = new JButton("생성");
		createButton.addActionListener(this);
		createButton.setBounds(518, 17, 70, 25);
		add(createButton);

		JPanel createAccountPanel = new JPanel();
		createAccountPanel.setBounds(0, 52, 600, 74);
		add(createAccountPanel);
		createAccountPanel.setLayout(new GridLayout(0, 6, 0, 0));

		JLabel idLabel = new JLabel("ID *");
		idLabel.setHorizontalAlignment(SwingConstants.CENTER);
		createAccountPanel.add(idLabel);

		JLabel passwardLabel = new JLabel("PW *");
		passwardLabel.setHorizontalAlignment(SwingConstants.CENTER);
		createAccountPanel.add(passwardLabel);

		JLabel storeNameLabel = new JLabel("매장명 *");
		storeNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		createAccountPanel.add(storeNameLabel);

		JLabel personNameLabel = new JLabel("이름 *");
		personNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		createAccountPanel.add(personNameLabel);

		JLabel phoneLabel = new JLabel("전화번호");
		phoneLabel.setHorizontalAlignment(SwingConstants.CENTER);
		createAccountPanel.add(phoneLabel);

		JLabel managerLabel = new JLabel("담당자 ID(매장)*");
		managerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		createAccountPanel.add(managerLabel);

		idLabel.setBorder(new MatteBorder(1, 1, 1, 0, Color.BLACK));
		passwardLabel.setBorder(new MatteBorder(1, 1, 1, 0, Color.BLACK));
		storeNameLabel.setBorder(new MatteBorder(1, 1, 1, 0, Color.BLACK));
		personNameLabel.setBorder(new MatteBorder(1, 1, 1, 0, Color.BLACK));
		phoneLabel.setBorder(new MatteBorder(1, 1, 1, 0, Color.BLACK));
		managerLabel.setBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK));

		idField = new JTextField();
		createAccountPanel.add(idField);
		idField.setColumns(10);

		passwordField = new JTextField();
		createAccountPanel.add(passwordField);
		passwordField.setColumns(10);

		storeNameField = new JTextField();
		createAccountPanel.add(storeNameField);
		storeNameField.setColumns(10);

		personNameField = new JTextField();
		createAccountPanel.add(personNameField);
		personNameField.setColumns(10);

		phoneField = new JTextField();
		createAccountPanel.add(phoneField);
		phoneField.setColumns(10);

		managerField = new JTextField();
		createAccountPanel.add(managerField);
		managerField.setColumns(10);

		JLabel searchAccountLabel = new JLabel("계정 조회");
		searchAccountLabel.setFont(new Font("굴림", Font.BOLD, 20));
		searchAccountLabel.setBounds(12, 136, 99, 32);
		add(searchAccountLabel);

		JScrollPane searchAccountscrollPane = new JScrollPane();
		searchAccountscrollPane.setBounds(0, 178, 600, 200);
		add(searchAccountscrollPane);

		searchAccountTable = new JTable() {
			public boolean isCellEditable(int row, int col) {
				return false; // 테이블 수정 못하게
			}
		};
//		table_1.setEnabled(false); // 수정불가
		searchAccountTable.getTableHeader().setReorderingAllowed(false); // 이동불가
		searchAccountTable.getTableHeader().setResizingAllowed(false); // 크기조절 불가

		searchAccountTable.setModel(
				new DefaultTableModel(new Object[][] {}, new String[] { "구분", "ID", "매장명", "담당자 이름", "전화번호" }));

		searchAccountscrollPane.setViewportView(searchAccountTable);
		searchAccountTable.setRowHeight(30);

		JButton searchButton = new JButton("조회");
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myDBcon.clear(searchAccountTable);
				myDBcon.searchAccount(searchAccountTable, radio2);
				System.out.println(radio2);
			}
		});
		searchButton.setBounds(518, 143, 70, 25);
		add(searchButton);

		JPanel radiobuttonPanel2 = new JPanel();
		radiobuttonPanel2.setBounds(228, 136, 278, 32);
		add(radiobuttonPanel2);
		ButtonGroup radioGroup2 = new ButtonGroup();
		for (int i = 0; i < radioButton2.length; i++) {
			radioButton2[i] = new JRadioButton(radioText2[i]);
			radioGroup2.add(radioButton2[i]);
			radiobuttonPanel2.add(radioButton2[i]);
			radioButton2[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// 계정 조회부분 라디오버튼 Action
					String es1 = e.getActionCommand();
					if (es1.equals(radioButton2[0].getText())) {
						radio2 = es1;
					} else if (es1.equals(radioButton2[1].getText())) {
						radio2 = es1;
					} else if (es1.equals(radioButton2[2].getText())) {
						radio2 = es1;
					}

				}
			});
		}
		radioButton2[0].setSelected(true);
		radiobuttonPanel2.setVisible(true);

		searchAccountTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		searchAccountTable.getColumnModel().getColumn(1).setPreferredWidth(50);
		searchAccountTable.getColumnModel().getColumn(3).setPreferredWidth(50);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// 계정 생성부분 라디오버튼 Action
		String es = e.getActionCommand();
		if (es.equals(radioButton1[0].getText())) {
			this.radio1 = es;
			managerField.setEditable(true);
		} else if (es.equals(radioButton1[1].getText())) {
			this.radio1 = es;
			managerField.setText(null);
			managerField.setEditable(false);
		}

		if (e.getSource() == createButton) {
//			String id = null;
//			String pw = null;
//			String storeName = null;
//			String personName = null;
//			String phone = null;
//			String manage = null;
			
			String id = idField.getText();
			String password = passwordField.getText();
			String storeName = storeNameField.getText();
			String personName = personNameField.getText();
			String phone = phoneField.getText();
			String manage = managerField.getText();
			


			if (radio1.equals("본사")) {
				if(id.isEmpty() || storeName.isEmpty() || personName.isEmpty()) {
					JOptionPane.showMessageDialog(null, "필수사항(*)을 입력해주세요.");
				}else {
					myDBcon.createAccount(id, password, personName, phone, storeName, id, radio1);					
				}

			} else {
				if(id.isEmpty() || storeName.isEmpty() || personName.isEmpty() || manage.isEmpty()) {
					JOptionPane.showMessageDialog(null, "필수사항(*)을 입력해주세요.");
				}else {
					myDBcon.createAccount(id, password, personName, phone, storeName, manage, radio1);					
				}
			}

		}

	}

}
