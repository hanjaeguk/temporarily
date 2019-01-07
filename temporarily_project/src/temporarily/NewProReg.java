package temporarily;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.BoxLayout;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.JToggleButton;
import javax.swing.JDesktopPane;
import javax.swing.JRadioButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;

public class NewProReg extends JPanel {
	private JTextField productNoField;
	private JTextField colorField;
	private JTextField sizeField;
	private JTextField priceField;
	
	private DBcon myDBcon;

	private void setDBcon(DBcon dbcon) {
		myDBcon = dbcon;
	}
	
	public NewProReg(DBcon dbcon) {
		setDBcon(dbcon);
		setLayout(null);

		TitledBorder Tb = new TitledBorder(new LineBorder(Color.black), "신상품등록");
		Tb.setTitleColor(Color.black);
		Tb.setTitleFont(new Font("굴림", Font.BOLD, 18));

		JPanel newProductPanel = new JPanel();
		newProductPanel.setBounds(12, 22, 426, 240);
		add(newProductPanel);
		newProductPanel.setLayout(null);
		newProductPanel.setBorder(Tb);

		JLabel productNoLabel = new JLabel("품번 : ");
		productNoLabel.setBounds(12, 40, 62, 29);
		newProductPanel.add(productNoLabel);

		JLabel colorLabel = new JLabel("색상 : ");
		colorLabel.setBounds(12, 80, 62, 29);
		newProductPanel.add(colorLabel);

		JLabel sizeLabel = new JLabel("사이즈 : ");
		sizeLabel.setBounds(12, 120, 62, 29);
		newProductPanel.add(sizeLabel);

		JLabel priceLabel = new JLabel("판매단가 : ");
		priceLabel.setBounds(12, 160, 62, 29);
		newProductPanel.add(priceLabel);

		productNoField = new JTextField(); // 품번입력
		productNoField.setBounds(74, 40, 213, 29);
		newProductPanel.add(productNoField);
		productNoField.setColumns(10);

		colorField = new JTextField(); // 색상입력
		colorField.setBounds(74, 79, 213, 29);
		newProductPanel.add(colorField);
		colorField.setColumns(10);

		sizeField = new JTextField(); // 사이즈입력
		sizeField.setBounds(74, 121, 213, 29);
		newProductPanel.add(sizeField);
		sizeField.setColumns(10);

		priceField = new JTextField(); // 판매단가
		priceField.setBounds(74, 160, 213, 29);
		newProductPanel.add(priceField);
		priceField.setColumns(10);

		JButton insertButton = new JButton("확인");
		insertButton.setBounds(333, 67, 70, 58);
		newProductPanel.add(insertButton);

		insertButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				String productCode = productNoField.getText()+colorField.getText()+sizeField.getText();
				String productNo = productNoField.getText();
				String productColor = colorField.getText();
				String productSize = sizeField.getText();
				String productPrice = priceField.getText();
				
				myDBcon.insertProduct(productCode, productNo, productColor, productSize, productPrice);
				// productCode(상품코드) = 품번 + 색상 + 사이즈로 자동생성
			}
		});
	}
}