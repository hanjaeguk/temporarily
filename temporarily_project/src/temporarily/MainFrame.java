package temporarily;

/*
 * 메인창
 * 
 * 메뉴바가 있는 기본 틀
 * 카드 레이아웃으로 각각의 메뉴들을 카드에 올려 호출
 * 창닫기 혹은 로그아웃 시 DB 연결이 해제
 * 
 * 메뉴는 매장과 본사에 따라 다르게 나타남
 * 매장 - 판매관리(판매등록,판매현황) 재고관리(재고조회) 로그아웃
 * 본사 - 재고관리(재고 등록/수정, 재고조회) 관리자메뉴(신상품등록, 상품단가 수정, 계정 생성/조회) 로그아웃
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
		userCode = myDBcon.getLoginUser().substring(0, 1); // S:매장, H:본사
		
		// 창닫기
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				int dialogResult = JOptionPane.showConfirmDialog(contentPane, "종료 하시겠습니까?", "exit", 
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(dialogResult == 0) { 
					// Yes
					myDBcon.disconn(); // DB 연결 해제
					System.exit(0); // 종료
				}
			}
		});
		
		setBounds(100, 100, 650, 450);

		contentPane = new JPanel();
		contentPane.setLayout(card);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);	
		
		// 판매관리(매장) 메뉴 - 판매등록, 판매현황
		if(userCode.equals("S")) {
			salesMenu = new JMenu("판매관리");
			menuBar.add(salesMenu);
	
			// SalesReg 판매등록
			salesRegItem = new JMenuItem("판매등록");
			salesMenu.add(salesRegItem);
			salesRegItem.addActionListener(this);
			contentPane.add("SalesReg", new SalesReg(myDBcon));
			
			//SalesStatus 판매현황
			salesStatusItem = new JMenuItem("판매현황");
			salesMenu.add(salesStatusItem);
			salesStatusItem.addActionListener(this);
			contentPane.add("SalesStatus", new SalesStatus(myDBcon));
		}

		// 재고관리 메뉴 - 재고등록/수정(본사), 재고조회(본사,매장)
		stockMenu = new JMenu("재고관리");
		menuBar.add(stockMenu);	

		if(userCode.equals("H")) {
			// StockModify 재고 등록/수정
			stockModifyItem = new JMenuItem("재고 등록/수정");
			stockMenu.add(stockModifyItem);	
			stockModifyItem.addActionListener(this);
			contentPane.add("StockModify", new StockModify(myDBcon));
		}
		
		// StockSearch 재고조회
		stockSearchItem = new JMenuItem("재고조회");
		stockMenu.add(stockSearchItem);	
		stockSearchItem.addActionListener(this);
		contentPane.add("StockSearch", new StockSearch(myDBcon));
			
		
		// 관리자메뉴(본사) - 신상품 등록, 상품단가 수정, 계정 생성/조회
		if(userCode.equals("H")) {
			adminMenu = new JMenu("관리자메뉴");
			menuBar.add(adminMenu);
	
			// NewProdReg 신상품 등록
			newProdRegItem = new JMenuItem("신상품 등록");
			adminMenu.add(newProdRegItem);
			newProdRegItem.addActionListener(this);
			contentPane.add("NewProdReg", new NewProReg(myDBcon));
			
			// ProdInfoModify 상품단가 수정
			prodInfoModifyItem = new JMenuItem("상품단가 수정");
			adminMenu.add(prodInfoModifyItem);
			prodInfoModifyItem.addActionListener(this);
			contentPane.add("ProdInfoModify", new ProdInfoModify(myDBcon));
			
			// AccountLookupCreate 계정 생성/조회
			accountLookupItem = new JMenuItem("계정 생성/조회");
			adminMenu.add(accountLookupItem);
			accountLookupItem.addActionListener(this);
			contentPane.add("Account", new AccountLookupCreate(myDBcon));
		}
		
		// 로그아웃 메뉴 - 로그아웃
		logoutMenu = new JMenu("로그아웃");
		menuBar.add(logoutMenu);

		logoutItem = new JMenuItem("로그아웃");
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
			// 로그아웃
			int dialogResult = JOptionPane.showConfirmDialog(this, "로그아웃 하시겠습니까?", "logout", 
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if(dialogResult == 0) { 
				// Yes  
				myDBcon.disconn(); // DB 연결 해제
				dispose(); // 메인창 닫기
				Start.main(null); // Start의 메인메서드 호출하여 재시작(로그인창)
			} 			
		}
	}
}