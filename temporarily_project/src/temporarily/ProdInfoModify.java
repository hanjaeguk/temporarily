package temporarily;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ProdInfoModify extends JPanel {
	private JTextField ProductNoField;
	private JTextField originalPriceField;
	private JTextField priceModifyField;

	private DBcon myDBcon;
	
	private void setDBcon(DBcon dbcon) {
		myDBcon = dbcon;
	}

	public ProdInfoModify(DBcon dbcon) {
		setDBcon(dbcon);
		setLayout(null);

		JLabel Title = new JLabel("상품단가 수정");
		Title.setFont(new Font("굴림", Font.BOLD, 20));
		Title.setBounds(12, 10, 201, 26);
		add(Title);

		TitledBorder Tb = new TitledBorder(new LineBorder(Color.black), "단가수정");
		Tb.setTitleColor(Color.black);

		JPanel priceModifyPanel = new JPanel();
		priceModifyPanel.setBounds(12, 46, 540, 254);
		add(priceModifyPanel);
		priceModifyPanel.setLayout(null);
		priceModifyPanel.setBorder(Tb);

		JLabel productNoLabel = new JLabel("품번 :");
		productNoLabel.setBounds(12, 31, 57, 16);
		productNoLabel.setFont(new Font("굴림", Font.BOLD, 13));
		productNoLabel.setHorizontalAlignment(SwingConstants.LEFT);
		priceModifyPanel.add(productNoLabel);

		ProductNoField = new JTextField();
		ProductNoField.setBounds(59, 27, 109, 26);
		priceModifyPanel.add(ProductNoField);
		ProductNoField.setColumns(10);

		JLabel originalPriceLabel = new JLabel("기존 판매단가 :");
		originalPriceLabel.setBounds(12, 85, 100, 36);
		priceModifyPanel.add(originalPriceLabel);
		originalPriceLabel.setFont(new Font("굴림", Font.BOLD, 13));

		originalPriceField = new JTextField();
		originalPriceField.setBounds(118, 91, 109, 26);
		priceModifyPanel.add(originalPriceField);
		originalPriceField.setColumns(10);
		originalPriceField.setEditable(false);

		JButton searchButton = new JButton("조회");
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String price = "0";
				myDBcon.searchProduct(ProductNoField.getText());
				price = myDBcon.getProductPrice().toString();
				originalPriceField.setText(price);
			}

		});
		searchButton.setBounds(180, 27, 70, 25);
		priceModifyPanel.add(searchButton);

		JLabel priceModifyLabel = new JLabel("변경 판매단가 :");
		priceModifyLabel.setBounds(12, 143, 100, 36);
		priceModifyPanel.add(priceModifyLabel);
		priceModifyLabel.setFont(new Font("굴림", Font.BOLD, 13));
		priceModifyLabel.setHorizontalAlignment(SwingConstants.CENTER);

		priceModifyField = new JTextField();
		priceModifyField.setBounds(118, 149, 109, 26);
		priceModifyPanel.add(priceModifyField);
		priceModifyField.setColumns(10);

		JButton updateButton = new JButton("확인");
		updateButton.setBounds(252, 134, 83, 54);
		priceModifyPanel.add(updateButton);
		updateButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String priceModify =priceModifyField.getText();
				String productNo = ProductNoField.getText();
				myDBcon.updatePrice(priceModify, productNo);
				
			}
		});
		updateButton.setFont(new Font("굴림", Font.PLAIN, 12));

	}
}
