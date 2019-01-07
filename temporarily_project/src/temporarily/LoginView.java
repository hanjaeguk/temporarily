package temporarily;
/*
 * 로그인창
 * 
 * 매장, 본사 구분하여 로그인
 *  
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoginView extends JFrame implements ActionListener{
	private Start start;
	
	private JPanel contentPane;
	private JTextField idField;
	private JPasswordField pwField;
	private JButton loginButton;
	private JRadioButton[] divRadio = new JRadioButton[2];
	
	String[] divRadioList = { "매장", "본사" };
	String divRadioResult = "매장";

	public LoginView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// 매장,본사 구분 라디오버튼
		ButtonGroup g = new ButtonGroup(); // 라디오 버튼 묶을 그룹
		for (int i = 0; i < divRadio.length; i++) {
			divRadio[i] = new JRadioButton(divRadioList[i]);
			g.add(divRadio[i]);
			add(divRadio[i]);

			divRadio[i].addActionListener(this);
		}
		divRadio[0].setSelected(true); // '매장' 버튼이 기본 선택
		divRadio[0].setBounds(240, 84, 62, 23);
		divRadio[1].setBounds(317, 84, 121, 23);		

		// 아이디
		JLabel idLabel = new JLabel("ID :");
		idLabel.setBounds(228, 134, 57, 15);
		contentPane.add(idLabel);

		idField = new JTextField();
		idField.setBounds(263, 131, 116, 21);
		contentPane.add(idField);
		idField.setColumns(10);

		// 비밀번호
		JLabel pwLable = new JLabel("PW :");
		pwLable.setBounds(228, 191, 36, 15);
		contentPane.add(pwLable);

		pwField = new JPasswordField();
		pwField.setBounds(263, 188, 116, 21);
		contentPane.add(pwField);

		// 로그인 버튼
		loginButton = new JButton("LOGIN");
		loginButton.addActionListener(this);
		loginButton.setBounds(251, 252, 97, 23);
		contentPane.add(loginButton);
			
		setVisible(true);
	}	

	// 메인과 연결 메서드
	public void setMain(Start start) {
		this.start = start;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// 라디오 버튼 결과
		String es = e.getActionCommand();
		if (es.equals(divRadio[0].getText())) {
			this.divRadioResult = es;
		}else if (es.equals(divRadio[1].getText())){
			this.divRadioResult = es;
		}
		
		// 로그인 버튼 action
		if (e.getSource() == loginButton) {
			DBcon dbcon = new DBcon(); // DB 연결
			
			String id = idField.getText();
			String pw = new String(pwField.getPassword());
			dbcon.checkLogin(id, pw, divRadioResult);
			
			if(dbcon.getLoginCount() == 1) {	
				// 로그인 성공
				JOptionPane.showMessageDialog(null, "로그인 되었습니다.");
				start.setDBcon(dbcon); // 로그인 된 계정 DB 넘기기
				start.showMainFrame(); // 로그인창 닫고 메인창 열어주기
			} else {	
				// 로그인 실패
				JOptionPane.showMessageDialog(null, "ID/PW를 확인해주세요.");
			}			
		}
	}
}