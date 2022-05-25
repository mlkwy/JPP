
public class MessagePacket {
	String name;
	String operation;
	String message;
	
	MessagePacket(String name, String operation, String message){
		this.name=name;
		this.operation=operation;
		this.message=message;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getOperation()
	{
		return operation;
	}
	
	public String getMessage()
	{
		return message;
	}
}

// For sending message packet
