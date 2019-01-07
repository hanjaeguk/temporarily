package temporarily;

/*
 * ���� ����
 * 
 * �α���â�� ���� �α����ϰ�
 * �α��� �� ������ �޾ƿͼ� ����â�� �����ִ� Ŭ����
 * 
 */

public class Start {
	private DBcon myDBcon;
	LoginView loginView;
	MainFrame mainFrame;

	public static void main(String[] args) {
		Start start = new Start();
		
		start.loginView = new LoginView(); // �α���â ����
		start.loginView.setMain(start); // �α���â���� ���� Ŭ���� ������
	}
	
	// �α��� �� ���� �޾ƿͼ� myDBcon�� �Ѱ��ִ� �޼���
	public void setDBcon(DBcon dbcon) {
		this.myDBcon=dbcon;
	}
	
	// �α���â �ݰ� ����â �����ִ� �޼���
	public void showMainFrame() {
		loginView.dispose(); // �α���â �ݱ�
		this.mainFrame = new MainFrame(myDBcon); // ����â ����
	}
}



/*
 * �Ǹ���Ȳ, ������Ȳ, �����ȸ, �ش�DB�޼��� �� ��������, �ּ� �߰�
 * 
 * �α��ν� id/pw �� �´µ� store�� ���� ��� ������. ó���Ұ�
 * 
 */
