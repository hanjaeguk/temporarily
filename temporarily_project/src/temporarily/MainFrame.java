package temporarily;

/*
 * ����â
 * 
 * �޴��ٰ� �ִ� �⺻ Ʋ
 * ī�� ���̾ƿ����� ������ �޴����� ī�忡 �÷� ȣ��
 * â�ݱ� Ȥ�� �α׾ƿ� �� DB ������ ����
 * 
 * �޴��� ����� ���翡 ���� �ٸ��� ��Ÿ��
 * ���� - �ǸŰ���(�Ǹŵ��,�Ǹ���Ȳ) ������(�����ȸ) �α׾ƿ�
 * ���� - ������(��� ���/����, �����ȸ) �����ڸ޴�(�Ż�ǰ���, ��ǰ�ܰ� ����, ���� ����/��ȸ) �α׾ƿ�
 * 
 */

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MainFrame extends JFrame implements ActionListener{
	private DBcon myDBcon;
	
	JPanel contentPane;
	JMenu salesMenu,stockMenu,adminMenu,logoutMenu;
	JMenuItem salesRegItem,salesStatusItem,stockModifyItem,stockSearchItem, 
				newProdRegItem,prodInfoModifyItem,accountLookupItem,logoutItem;

	CardLayout card = new CardLayout();
	String userCode;
	
	public MainFrame(DBcon dbcon) {
		setDBcon(dbcon);
		userCode = myDBcon.getLoginUser().substring(0, 1); // S:����, H:����
		
		// â�ݱ�
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				int dialogResult = JOptionPane.showConfirmDialog(contentPane, "���� �Ͻðڽ��ϱ�?", "exit", 
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(dialogResult == 0) { 
					// Yes
					myDBcon.disconn(); // DB ���� ����
					System.exit(0); // ����
				}
			}
		});
		
		setBounds(100, 100, 650, 450);

		contentPane = new JPanel();
		contentPane.setLayout(card);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);	
		
		// �ǸŰ���(����) �޴� - �Ǹŵ��, �Ǹ���Ȳ
		if(userCode.equals("S")) {
			salesMenu = new JMenu("�ǸŰ���");
			menuBar.add(salesMenu);
	
			// SalesReg �Ǹŵ��
			salesRegItem = new JMenuItem("�Ǹŵ��");
			salesMenu.add(salesRegItem);
			salesRegItem.addActionListener(this);
			contentPane.add("SalesReg", new SalesReg(myDBcon));
			
			//SalesStatus �Ǹ���Ȳ
			salesStatusItem = new JMenuItem("�Ǹ���Ȳ");
			salesMenu.add(salesStatusItem);
			salesStatusItem.addActionListener(this);
			contentPane.add("SalesStatus", new SalesStatus(myDBcon));
		}

		// ������ �޴� - �����/����(����), �����ȸ(����,����)
		stockMenu = new JMenu("������");
		menuBar.add(stockMenu);	

		if(userCode.equals("H")) {
			// StockModify ��� ���/����
			stockModifyItem = new JMenuItem("��� ���/����");
			stockMenu.add(stockModifyItem);	
			stockModifyItem.addActionListener(this);
			contentPane.add("StockModify", new StockModify(myDBcon));
		}
		
		// StockSearch �����ȸ
		stockSearchItem = new JMenuItem("�����ȸ");
		stockMenu.add(stockSearchItem);	
		stockSearchItem.addActionListener(this);
		contentPane.add("StockSearch", new StockSearch(myDBcon));
			
		
		// �����ڸ޴�(����) - �Ż�ǰ ���, ��ǰ�ܰ� ����, ���� ����/��ȸ
		if(userCode.equals("H")) {
			adminMenu = new JMenu("�����ڸ޴�");
			menuBar.add(adminMenu);
	
			// NewProdReg �Ż�ǰ ���
			newProdRegItem = new JMenuItem("�Ż�ǰ ���");
			adminMenu.add(newProdRegItem);
			newProdRegItem.addActionListener(this);
			contentPane.add("NewProdReg", new NewProReg(myDBcon));
			
			// ProdInfoModify ��ǰ�ܰ� ����
			prodInfoModifyItem = new JMenuItem("��ǰ�ܰ� ����");
			adminMenu.add(prodInfoModifyItem);
			prodInfoModifyItem.addActionListener(this);
			contentPane.add("ProdInfoModify", new ProdInfoModify(myDBcon));
			
			// AccountLookupCreate ���� ����/��ȸ
			accountLookupItem = new JMenuItem("���� ����/��ȸ");
			adminMenu.add(accountLookupItem);
			accountLookupItem.addActionListener(this);
			contentPane.add("Account", new AccountLookupCreate(myDBcon));
		}
		
		// �α׾ƿ� �޴� - �α׾ƿ�
		logoutMenu = new JMenu("�α׾ƿ�");
		menuBar.add(logoutMenu);

		logoutItem = new JMenuItem("�α׾ƿ�");
		logoutMenu.add(logoutItem);
		logoutItem.addActionListener(this);

		add(contentPane);
		setVisible(true);
	}
	
	private void setDBcon(DBcon dbcon) {
		myDBcon = dbcon;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() ==salesRegItem) {
			card.show(contentPane, "SalesReg");
		} else if (e.getSource() == salesStatusItem) {
			card.show(contentPane, "SalesStatus");
		} else if (e.getSource() == stockModifyItem) {	
			card.show(contentPane, "StockModify");
		} else if (e.getSource() == stockSearchItem) {
			card.show(contentPane, "StockSearch");
		} else if (e.getSource() == newProdRegItem) {
			card.show(contentPane, "NewProdReg");
		} else if(e.getSource() == prodInfoModifyItem) {
			card.show(contentPane, "ProdInfoModify");
		} else if (e.getSource() == accountLookupItem) {
			card.show(contentPane, "Account");
		} else if (e.getSource() == logoutItem) {
			// �α׾ƿ�
			int dialogResult = JOptionPane.showConfirmDialog(this, "�α׾ƿ� �Ͻðڽ��ϱ�?", "logout", 
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if(dialogResult == 0) { 
				// Yes  
				myDBcon.disconn(); // DB ���� ����
				dispose(); // ����â �ݱ�
				Start.main(null); // Start�� ���θ޼��� ȣ���Ͽ� �����(�α���â)
			} 			
		}
	}
}