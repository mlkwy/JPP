import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	private ServerSocket server_socket;
	private List <ReceiveFromClientsThread> receive_thread_list;
	
	public Server() {
		try {
			server_socket = new ServerSocket(100);
			System.out.println("Server ready.");
			
			receive_thread_list = new ArrayList<ReceiveFromClientsThread>();
			while (true)
			{
				Socket socket = server_socket.accept();
				System.out.println("New connection arrived.");
				
				ReceiveFromClientsThread receive_thread = new ReceiveFromClientsThread(socket, receive_thread_list);
				receive_thread_list.add(receive_thread);
				
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
		for(int i=0; i<receive_thread_list.size(); i++)
			if(!receive_thread_list.get(i).isAlive())
				receive_thread_list.remove(i);
	}
	
	class ReceiveFromClientsThread extends Thread {
		private Socket socket;
		private List <ReceiveFromClientsThread> receive_thread_list;
		
		public ReceiveFromClientsThread(Socket socket, List <ReceiveFromClientsThread> receive_thread_list)
		{
			this.socket=socket;
			this.receive_thread_list=receive_thread_list;
		}
		
		public void run() {
			try {
					while(true)
					{
						removeDeadThreads();
						DataInputStream dis=new DataInputStream(socket.getInputStream());
						String arrived=dis.readUTF();
						System.out.println(arrived);
						for(int i=0; i<receive_thread_list.size(); i++)
						{
							OutputStream os = receive_thread_list.get(i).socket.getOutputStream();
							DataOutputStream dos = new DataOutputStream(os);
							dos.writeUTF(arrived);
						}
					}
					//dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
