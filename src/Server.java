import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Server {
	private ServerSocket server_socket;
	private List <ReceiveFromClientsThread> thread_list;
	private String client_list;
	static ArrayList<String> array_client_list;
	private Boolean is_leader_decided;
	private ReceiveFromClientsThread leader_thread;
	
	public Server() {
		client_list=new String("List`");
		array_client_list=new ArrayList<String>();
		is_leader_decided=false;
		try {
			server_socket = new ServerSocket(1000);
			System.out.println("Server ready.");
			
			thread_list = new ArrayList<ReceiveFromClientsThread>();
			while (true)
			{
				Socket socket = server_socket.accept();
				System.out.println("New connection arrived.");
				
				ReceiveFromClientsThread receive_thread = new ReceiveFromClientsThread(socket, thread_list);
				thread_list.add(receive_thread);
				
				receive_thread.start();
				removeDeadThreads();
			}
		} catch (IOException e) {
			e.printStackTrace();	
		}
	}
	
	public static void main(String[] args) {
		new Server();
	}
	
	public void removeDeadThreads()
	{
		for(int i=0; i<thread_list.size(); i++)
			if(!thread_list.get(i).isAlive())
				thread_list.remove(i);
	
		System.out.println(thread_list.size()+" threads");
	}
	
	public void constructList()
	{
		client_list=new String("List`");
		array_client_list=new ArrayList<String>();
		for(int i=0; i<thread_list.size(); i++)
		{
			client_list=client_list+thread_list.get(i).getThreadName()+",";
			array_client_list.add(thread_list.get(i).getThreadName());
		}
	}
	
	public Boolean isDecided()
	{
		return is_leader_decided;
	}
	
	public void setDecided()
	{
		is_leader_decided=!is_leader_decided;
	}
	
	class ReceiveFromClientsThread extends Thread {
		private Socket socket;
		private String name;
		private List <ReceiveFromClientsThread> thread_list;
		
		public ReceiveFromClientsThread(Socket socket, List <ReceiveFromClientsThread> thread_list)
		{
			this.socket=socket;
			this.thread_list=thread_list;
		}
		
		public String getThreadName()
		{
			return this.name;
		}
		
		public void run() {
			try {
					while(true)
					{
						removeDeadThreads();
						
						DataInputStream dis=new DataInputStream(socket.getInputStream());
						String arrived=dis.readUTF();
						System.out.println(arrived);
						
						StringTokenizer token = new StringTokenizer(arrived, "`");
						String command=token.nextToken();
						String name=token.nextToken();
						
						if(command.equals("Joined"))
						{
							this.name=name;
							if(array_client_list.contains(name)) {
								System.out.println("User name collides");
								socket.close();
								return;
							}
						}
						
						removeDeadThreads();
						constructList();
							
						for(int i=0; i<thread_list.size(); i++)
						{
							DataOutputStream dos = new DataOutputStream(thread_list.get(i).socket.getOutputStream());
							dos.writeUTF(client_list);
							dos.writeUTF(arrived);
						}
						
						if(thread_list.size()==1 && !isDecided())
						{
							leader_thread=this;
							setDecided();
							
							for(int i=0; i<thread_list.size(); i++)
							{
								DataOutputStream dos_1 = new DataOutputStream(thread_list.get(i).socket.getOutputStream());
								dos_1.writeUTF("Changed`"+this.getThreadName());
							}
						}
					}
			} catch (IOException e) {
				System.out.println("Lost connection with a user.");
				//e.printStackTrace();
				
				for(int i=0; i<thread_list.size(); i++)
				{
					if(thread_list.get(i).getName()==this.getName())
						thread_list.remove(i);
				}
				
				constructList();
				
				for(int i=0; i<thread_list.size(); i++) {
					try {
						DataOutputStream dos = new DataOutputStream(thread_list.get(i).socket.getOutputStream());
						dos.writeUTF("Left`"+this.getThreadName());
						dos.writeUTF(client_list);
					} catch(IOException t) {
						//t.printStackTrace();		
					}
				}				
				
				if(this==leader_thread)
				{
					if(thread_list.size()>0)
					{
						leader_thread=thread_list.get(0);
						for(int i=0; i<thread_list.size(); i++) {
							try {
								DataOutputStream dos = new DataOutputStream(thread_list.get(i).socket.getOutputStream());
								dos.writeUTF("Changed`"+thread_list.get(0).getThreadName());
								dos.writeUTF("Unmuted All`"+"dummy");
							} catch(IOException s) {
								//s.printStackTrace();		
							}
						}
					}	
					else
						setDecided();	
				}
			}
		}
	}
}
