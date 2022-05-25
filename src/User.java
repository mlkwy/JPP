
public class User {
	String name;
	Boolean is_leader;
	Boolean is_muted;
	
	User(String name) {
		this.name=name;
		is_leader=false;
		is_muted=false;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Boolean getIsLeader()
	{
		return is_leader;
	}
	
	public Boolean getIsMuted()
	{
		return is_muted;
	}
	
	public void setName(String name)
	{
		this.name=name;
	}
	
	public void onMute()
	{
		is_muted=true;
	} // Mute enabled
	
	public void offMute()
	{
		is_muted=false;
	} // Mute disabled
	
	public void setLeader()
	{
		is_leader=true;
	} // Become a leader
	
	public void resignLeader()
	{
		is_leader=false;
	} // Resign leader
	
	public void passLeader(User user)
	{
		this.resignLeader();
		user.setLeader();
	} // Pass leader's permission to dest
	
}
