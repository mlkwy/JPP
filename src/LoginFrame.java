import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class LoginFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	static Socket socket;
	static String name;
	
	LoginFrame() {		
		setTitle("Login");
		setLayout(new GridLayout(4, 0));
		setResizable(false);
		
		JLabel label0=new JLabel("IP Adresss");
		JLabel label1=new JLabel("Port Number");
		JLabel label2=new JLabel("User Name");
		
		JPanel panel0=new JPanel();
		JPanel panel1=new JPanel();
		JPanel panel2=new JPanel();
		
		JTextField t0=new JTextField(15);
		JTextField t1=new JTextField(5);
		JTextField t2=new JTextField(15);
		
		JButton button=new JButton("Login");
		button.setOpaque(true);;
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ip = t0.getText();
				int port =Integer.parseInt(t1.getText());
				String name = t2.getText();
				
				if(!ip.equals("") && !name.equals(""))
				{
					LoginFrame.name=name;
					Boolean valid=true;
					try {
						socket = new Socket(ip, port);
					} catch (IOException e1) {
						valid=false;
					} 
					
					if(valid) {
						new SendToServer(socket, "Joined`"+name).start();
						
						if(socket.isClosed()) {
							System.exit(0);
						}
						
						else {
							@SuppressWarnings("unused")
							Client client = new Client(socket, name);
							dispose();
						}
					}
				}
			}
		});
		
		panel0.add(label0);
		panel0.add(t0);
		
		panel1.add(label1);
		panel1.add(t1);
		
		panel2.add(label2);
		panel2.add(t2);
		
		Container c = getContentPane();
		
		c.add(panel0);
		c.add(panel1);
		c.add(panel2);
		c.add(button);
		
		pack();
		setVisible(true);
	}
	
	@SuppressWarnings("unused")
	public static void main(String args[])
	{
		LoginFrame loginframe = new LoginFrame();
	}
}
