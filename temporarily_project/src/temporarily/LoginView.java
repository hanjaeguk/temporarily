package temporarily;
/*
 * �α���â
 * 
 * ����, ���� �����Ͽ� �α���
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
	
	String[] divRadioList = { "����", "����" };
	String divRadioResult = "����";

	public LoginView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// ����,���� ���� ������ư
		ButtonGroup g = new ButtonGroup(); // ���� ��ư ���� �׷�
		for (int i = 0; i < divRadio.length; i++) {
			divRadio[i] = new JRadioButton(divRadioList[i]);
			g.add(divRadio[i]);
			add(divRadio[i]);

			divRadio[i].addActionListener(this);
		}
		divRadio[0].setSelected(true); // '����' ��ư�� �⺻ ����
		divRadio[0].setBounds(240, 84, 62, 23);
		divRadio[1].setBounds(317, 84, 121, 23);		

		// ���̵�
		JLabel idLabel = new JLabel("ID :");
		idLabel.setBounds(228, 134, 57, 15);
		contentPane.add(idLabel);

		idField = new JTextField();
		idField.setBounds(263, 131, 116, 21);
		contentPane.add(idField);
		idField.setColumns(10);

		// ��й�ȣ
		JLabel pwLable = new JLabel("PW :");
		pwLable.setBounds(228, 191, 36, 15);
		contentPane.add(pwLable);

		pwField = new JPasswordField();
		pwField.setBounds(263, 188, 116, 21);
		contentPane.add(pwField);

		// �α��� ��ư
		loginButton = new JButton("LOGIN");
		loginButton.addActionListener(this);
		loginButton.setBounds(251, 252, 97, 23);
		contentPane.add(loginButton);
			
		setVisible(true);
	}	

	// ���ΰ� ���� �޼���
	public void setMain(Start start) {
		this.start = start;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// ���� ��ư ���
		String es = e.getActionCommand();
		if (es.equals(divRadio[0].getText())) {
			this.divRadioResult = es;
		}else if (es.equals(divRadio[1].getText())){
			this.divRadioResult = es;
		}
		
		// �α��� ��ư action
		if (e.getSource() == loginButton) {
			DBcon dbcon = new DBcon(); // DB ����
			
			String id = idField.getText();
			String pw = new String(pwField.getPassword());
			dbcon.checkLogin(id, pw, divRadioResult);
			
			if(dbcon.getLoginCount() == 1) {	
				// �α��� ����
				JOptionPane.showMessageDialog(null, "�α��� �Ǿ����ϴ�.");
				start.setDBcon(dbcon); // �α��� �� ���� DB �ѱ��
				start.showMainFrame(); // �α���â �ݰ� ����â �����ֱ�
			} else {	
				// �α��� ����
				JOptionPane.showMessageDialog(null, "ID/PW�� Ȯ�����ּ���.");
			}			
		}
	}
}