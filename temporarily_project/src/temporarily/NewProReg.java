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
import java.util.regex.Pattern;
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

		TitledBorder Tb = new TitledBorder(new LineBorder(Color.black), "�Ż�ǰ���");
		Tb.setTitleColor(Color.black);
		Tb.setTitleFont(new Font("����", Font.BOLD, 18));

		JPanel newProductPanel = new JPanel();
		newProductPanel.setBounds(12, 22, 426, 240);
		add(newProductPanel);
		newProductPanel.setLayout(null);
		newProductPanel.setBorder(Tb);

		JLabel productNoLabel = new JLabel("ǰ�� : ");
		productNoLabel.setBounds(12, 40, 62, 29);
		newProductPanel.add(productNoLabel);

		JLabel colorLabel = new JLabel("���� : ");
		colorLabel.setBounds(12, 80, 62, 29);
		newProductPanel.add(colorLabel);

		JLabel sizeLabel = new JLabel("������ : ");
		sizeLabel.setBounds(12, 120, 62, 29);
		newProductPanel.add(sizeLabel);

		JLabel priceLabel = new JLabel("�ǸŴܰ� : ");
		priceLabel.setBounds(12, 160, 62, 29);
		newProductPanel.add(priceLabel);

		productNoField = new JTextField(); // ǰ���Է�
		productNoField.setBounds(74, 40, 213, 29);
		newProductPanel.add(productNoField);
		productNoField.setColumns(10);

		colorField = new JTextField(); // �����Է�
		colorField.setBounds(74, 79, 213, 29);
		newProductPanel.add(colorField);
		colorField.setColumns(10);

		sizeField = new JTextField(); // �������Է�
		sizeField.setBounds(74, 121, 213, 29);
		newProductPanel.add(sizeField);
		sizeField.setColumns(10);

		priceField = new JTextField(); // �ǸŴܰ�
		priceField.setBounds(74, 160, 213, 29);
		newProductPanel.add(priceField);
		priceField.setColumns(10);

		JButton insertButton = new JButton("Ȯ��");
		insertButton.setBounds(333, 67, 70, 58);
		newProductPanel.add(insertButton);

		insertButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String productCode = productNoField.getText() + colorField.getText() + sizeField.getText();
				String productNo = productNoField.getText();
				String productColor = colorField.getText();
				String productSize = sizeField.getText();
				String productPrice = priceField.getText();

				String checkProductNo = "\\d{7}";
				String checkProductColor = "[a-zA-Z]{2}";
				String checkProductSize = "[sml]|(xl)|[SML]|(XL){1,2}";
				String checkProductPrice = "^[1-9]\\d*";

				boolean isProductNo = Pattern.matches(checkProductNo, productNo);
				boolean isProductColor = Pattern.matches(checkProductColor, productColor);
				boolean isProductSize = Pattern.matches(checkProductSize, productSize);
				boolean isProductPrice = Pattern.matches(checkProductPrice, productPrice);

				if (productNo.isEmpty()) {
					JOptionPane.showMessageDialog(null, "ǰ���� �Է����ּ���.");
				} else if (isProductNo == false) {
					JOptionPane.showMessageDialog(null, "ǰ���� ����7�ڸ��� �Է����ּ���.");
				} else if (productColor.isEmpty()) {
					JOptionPane.showMessageDialog(null, "������ �Է����ּ���.");
				} else if (isProductColor == false) {
					JOptionPane.showMessageDialog(null, "������ 2�ڸ� ����� �Է����ּ���. ");
				} else if (productSize.isEmpty()) {
					JOptionPane.showMessageDialog(null, "����� �Է����ּ���.");
				} else if (isProductSize == false) {
					JOptionPane.showMessageDialog(null, "������� S,M,L,XL�� �Է����ּ���. ");
				} else if (productPrice.isEmpty()) {
					JOptionPane.showMessageDialog(null, "������ �Է����ּ���.");
				} else if (isProductPrice == false) {
					JOptionPane.showMessageDialog(null, "�ܰ��� ���ڷ� �Է����ּ���. ");
				} else {
					myDBcon.insertProduct(productCode, productNo, productColor, productSize, productPrice);
					// productCode(��ǰ�ڵ�) = ǰ�� + ���� + ������� �ڵ�����
				}

			}
		});
	}
}