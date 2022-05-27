import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Client {
	private Socket socket;
	private static String name;
	public Boolean is_leader;
	public Boolean is_muted;
	
	public static void main(String args[]) throws UnknownHostException, IOException
	{	
		Scanner scn = new Scanner(System.in);
		name = scn.nextLine();
		Socket socket = new Socket("localhost", 100);
		new ReceiveFromServer(socket).start();
		new SendToServer(socket).start();
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public static String getName() {
		return Client.name;
	}
	
	public void setName(String name) {
		Client.name = name;
	}
}

class SendToServer extends Thread {
	private Socket socket;
	
	public SendToServer(Socket socket) {
		this.socket=socket;
	}
	
	public void run( ) {
		try {
			Scanner scn = new Scanner(System.in);
			String input = scn.nextLine();
			String sent_message = "Message:" + Client.getName() + ":" + input;
			
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF(sent_message);
			new SendToServer(socket).start();
		}catch (IOException e)
		{
			e.printStackTrace();
		}	
	}
}

class ReceiveFromServer extends Thread {
	private Socket socket;
	
	public ReceiveFromServer(Socket socket)
	{
		this.socket=socket;
	}
	
	public void run ( ) { 
		try {
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			String arrived=dis.readUTF();
			
			// 1 : Message 2 : Joined 3: Left 4 : Muted 5 : Unmuted 6 : Leader changed
			StringTokenizer st = new StringTokenizer(arrived, ":");
			String command = st.nextToken();
			String name = st.nextToken();
			String message = "";
			while(st.hasMoreTokens())
				message = message + st.nextToken();
			
			String printed_message = "";
			if(command.equals("Message"))
				printed_message = name + ": " + message;
				
			else if(command.equals("Joined"))
				printed_message = name + " joined the chatroom.";
		
			else if(command.equals("Left"))
				printed_message = name + " left the chatroom";
			
			else if(command.equals("Muted"))
				printed_message = "The leader muted " + name + ".";
			
			else if(command.equals("Unmuted"))
				printed_message = "The leader unmuted " + name + ".";
			
			else
				printed_message = name + " is the new leader of the chatroom.";
				
				
			System.out.println(printed_message);
			
			//dis.close();
			new ReceiveFromServer(socket).start();
		}catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
