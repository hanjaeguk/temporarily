package temporarily;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;

public class StockModify extends JPanel {
	private JTextField proNoTextField;

	private DBcon myDBcon;
	private JTextField S_SizeField;
	private JTextField M_SizeField;
	private JTextField L_SizeField;
	private JTextField XL_SizeField;
	
	private void setDBcon(DBcon dbcon) {
		myDBcon = dbcon;
	}

	public StockModify(DBcon dbcon) {
		setDBcon(dbcon);
		setLayout(null);

		JLabel Title = new JLabel("재고 등록/수정");
		Title.setFont(new Font("굴림", Font.BOLD, 20));
		Title.setBounds(12, 10, 201, 26);
		add(Title);

		TitledBorder Tb = new TitledBorder(new LineBorder(Color.black), "등록/수정");
		Tb.setTitleColor(Color.black);

		JPanel stockModifyPanel = new JPanel();
		stockModifyPanel.setBounds(12, 53, 540, 314);

		add(stockModifyPanel);
		stockModifyPanel.setLayout(null);
		stockModifyPanel.setBorder(Tb);

		JLabel storeLabel = new JLabel("매장 :");
		storeLabel.setBounds(11, 39, 54, 36);
		storeLabel.setFont(new Font("굴림", Font.BOLD, 13));
		stockModifyPanel.add(storeLabel);

		JComboBox storeComboBox = new JComboBox();
		storeComboBox.setBounds(67, 44, 148, 26);
		storeComboBox.setFont(new Font("굴림", Font.PLAIN, 13));
		storeComboBox.setMaximumRowCount(100);
		stockModifyPanel.add(storeComboBox);
		myDBcon.getStoreNameCombobox(storeComboBox);
		
		JLabel proNoLabel = new JLabel("품번 :");
		proNoLabel.setBounds(239, 39, 59, 36);
		proNoLabel.setFont(new Font("굴림", Font.BOLD, 13));
		stockModifyPanel.add(proNoLabel);

		proNoTextField = new JTextField();
		proNoTextField.setColumns(10);
		proNoTextField.setBounds(289, 45, 109, 26);
		stockModifyPanel.add(proNoTextField);
		
		JComboBox colorComboBox = new JComboBox();
		colorComboBox.setBounds(67, 104, 105, 26);
		stockModifyPanel.add(colorComboBox);
		colorComboBox.setMaximumRowCount(100);
		colorComboBox.setFont(new Font("굴림", Font.PLAIN, 13));
		

		JButton searchColorButton = new JButton("색상 조회");
		searchColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				colorComboBox.removeAllItems();
				S_SizeField.setText(null);
				M_SizeField.setText(null);
				L_SizeField.setText(null);
				XL_SizeField.setText(null);
				String p_no = proNoTextField.getText();
				myDBcon.searchStockColor(colorComboBox, storeComboBox, p_no);
			}
		});
		searchColorButton.setFont(new Font("굴림", Font.PLAIN, 12));
		searchColorButton.setBounds(406, 45, 111, 25);
		stockModifyPanel.add(searchColorButton);

		JLabel colorLabel = new JLabel("색상 :");
		colorLabel.setBounds(11, 99, 54, 36);
		stockModifyPanel.add(colorLabel);
		colorLabel.setFont(new Font("굴림", Font.BOLD, 13));
	
		JButton searchSizeButton = new JButton("재고 조회");
		searchSizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String stockQuantity = "0";
				String storeName = storeComboBox.getSelectedItem().toString();
				String productNo = proNoTextField.getText();
				String productColor = colorComboBox.getSelectedItem().toString();
				
				
				myDBcon.searchSize(storeName, productNo, productColor, "S");
				stockQuantity = myDBcon.getStockQuantity().toString();
				S_SizeField.setText(stockQuantity);
				
				myDBcon.searchSize(storeName, productNo, productColor, "M");
				stockQuantity = myDBcon.getStockQuantity().toString();
				M_SizeField.setText(stockQuantity);

				myDBcon.searchSize(storeName, productNo, productColor, "L");
				stockQuantity = myDBcon.getStockQuantity().toString();
				L_SizeField.setText(stockQuantity);
				
				myDBcon.searchSize(storeName, productNo, productColor, "XL");
				stockQuantity = myDBcon.getStockQuantity().toString();
				XL_SizeField.setText(stockQuantity);
	
			}
		});
		
		searchSizeButton.setFont(new Font("굴림", Font.PLAIN, 12));
		searchSizeButton.setBounds(196, 105, 102, 25);
		stockModifyPanel.add(searchSizeButton);


		JLabel sizeLabel = new JLabel("사이즈 :");
		sizeLabel.setBounds(11, 169, 79, 26);
		stockModifyPanel.add(sizeLabel);
		sizeLabel.setFont(new Font("굴림", Font.BOLD, 13));


		JPanel sizePanel = new JPanel();
		sizePanel.setBounds(42, 205, 384, 75);
		stockModifyPanel.add(sizePanel);
		sizePanel.setLayout(new GridLayout(0, 4, 0, 0));
		
		JLabel S_SizeLabel = new JLabel("S");
		S_SizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		S_SizeLabel.setBackground(Color.LIGHT_GRAY);
		sizePanel.add(S_SizeLabel);
		S_SizeLabel.setOpaque(true);
//		Border border =BorderFactory.createLineBorder(Color.BLACK); // 라벨 테두리
		S_SizeLabel.setBorder(new MatteBorder(1, 1, 1, 0, Color.BLACK));  // 원하는 위치에 라벨 테두리
		
		JLabel M_SizeLabel = new JLabel("M");
		M_SizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		M_SizeLabel.setBackground(Color.LIGHT_GRAY);
		sizePanel.add(M_SizeLabel);
		M_SizeLabel.setOpaque(true);
		M_SizeLabel.setBorder(new MatteBorder(1, 1, 1, 0, Color.BLACK)); 


		JLabel L_SizeLabel = new JLabel("L");
		L_SizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		L_SizeLabel.setBackground(Color.LIGHT_GRAY);
		sizePanel.add(L_SizeLabel);
		L_SizeLabel.setOpaque(true);
		L_SizeLabel.setBorder(new MatteBorder(1, 1, 1, 0, Color.BLACK)); 


		JLabel XL_SizeLabel = new JLabel("XL");
		XL_SizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		XL_SizeLabel.setBackground(Color.LIGHT_GRAY);
		sizePanel.add(XL_SizeLabel);
		XL_SizeLabel.setOpaque(true);
		XL_SizeLabel.setBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK)); 
		
		
		S_SizeField = new JTextField();
		sizePanel.add(S_SizeField);
		S_SizeField.setColumns(10);
		
		M_SizeField = new JTextField();
		sizePanel.add(M_SizeField);
		M_SizeField.setColumns(10);
		
		L_SizeField = new JTextField();
		sizePanel.add(L_SizeField);
		L_SizeField.setColumns(10);
		
		XL_SizeField = new JTextField();
		sizePanel.add(XL_SizeField);
		XL_SizeField.setColumns(10);
		
		
		JButton updateButton = new JButton("확인");
		updateButton.setBounds(438, 215, 79, 55);
		stockModifyPanel.add(updateButton);
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				String changeStockQuantity = S_SizeField.getText();
				String storeName = storeComboBox.getSelectedItem().toString();
				String productNo = proNoTextField.getText();
				String productColor = colorComboBox.getSelectedItem().toString();

				myDBcon.updateStock(changeStockQuantity, storeName, productNo, productColor);										


			}
		});
		updateButton.setFont(new Font("굴림", Font.PLAIN, 12));
		


	}
}
