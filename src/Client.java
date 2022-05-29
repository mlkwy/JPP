import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Client {
	static Socket socket;
	static ChatRoomFrame client_frame;
	static String name;
	static Boolean is_leader;
	static Boolean is_muted;
	static ArrayList<String> client_list;

	Client(Socket socket, String name)
	{	
		Client.socket=socket;
		Client.name=name;
		is_leader=false;
		is_muted=false;
		client_list=new ArrayList<String>();
		client_frame=new ChatRoomFrame();
		
		new ReceiveFromServer(socket, client_frame).start();
	}
	
	public static String getName() {
		return name;
	}
		
	public static Boolean isLeader() {
		return is_leader;
	}
	
	public static void setLeader() {
		is_leader=!is_leader;
	}
	
	public static Boolean isMuted() {
		return is_muted;
	}
	
	public static void setMuted() {
		is_muted=!is_muted;
	}
}

class SendToServer extends Thread {
	private Socket socket;
	private String message;
	
	public SendToServer(Socket socket, String message) {
		this.socket=socket;
		this.message=message;
	}
	
	public void run( ) {
		try {
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF(message);
		}catch (IOException e)
		{
			//e.printStackTrace();
		}	
	}
}

class ReceiveFromServer extends Thread {
	private Socket socket;
	private ChatRoomFrame client_frame;
	
	public ReceiveFromServer(Socket socket, ChatRoomFrame client_frame) {
		this.socket=socket;
		this.client_frame=client_frame;
	}
	
	public void run ( ) { 
		try {
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			String arrived=dis.readUTF();
		
			StringTokenizer st = new StringTokenizer(arrived, "`");
			String command = st.nextToken();
			String name = st.nextToken();
			String message = "";
			while(st.hasMoreTokens())
				message = message + st.nextToken();
			
			String printed_message = "";
			if(command.equals("Message"))
			{
				if(name.equals(Client.getName()))
					printed_message = "[Me] " + message;
				else
					printed_message = "[" + name + "] " + message;
			}
				
			else if(command.equals("Joined"))
				printed_message = name + " joined the chatroom.";

		
			else if(command.equals("Left"))
				printed_message = name + " left the chatroom.";
			
			else if(command.equals("Muted") && name.equals(Client.getName()) && !Client.isLeader())
			{
					
				if(Client.isMuted())
					printed_message = "The leader unmuted you.";
				else
					printed_message = "The leader muted you.";
					
				client_frame.send_button.setEnabled(!client_frame.send_button.isEnabled());	
				Client.setMuted();
			}
				
			
			else if(command.equals("Unmuted All")) {
				printed_message = "All users are unmuted.";
				if(Client.isMuted())
				{
					client_frame.send_button.setEnabled(true);
					Client.setMuted();
				}
			}
			
			else if(command.equals("Changed"))
			{
				if(name.equals(Client.getName()))
				{
					Client.setLeader();
					printed_message = "You are the new leader of the chatroom.";
					client_frame.alterLayout();
				}
				
				else
					printed_message = name + " is the new leader of the chatroom.";
				
			}
			
			else if(command.equals("List"))
			{
				System.out.println(name);
				StringTokenizer token = new StringTokenizer(name, ",");
				Client.client_list=new ArrayList<String>();
				while(token.hasMoreTokens())
				{
					String s = token.nextToken();
					Client.client_list.add(s.trim());
				}
			}
			
			if(!printed_message.equals(""))
				client_frame.message_area.append(printed_message+"\n");
			new ReceiveFromServer(socket, client_frame).start();
			
		}catch (IOException e)
		{
			//e.printStackTrace();
		}
	}
}
