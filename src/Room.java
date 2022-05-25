import java.util.ArrayList;

public class Room {
	ArrayList<User> user_list; // List containing users in the room
	
	Room(String name) {
		user_list=new ArrayList<User>();
	}
	
	public int count()
	{
		return this.user_list.size();
	} // Returns the number of people in the room
	
	public void addUser(User new_user) {
		if(user_list.isEmpty())
			new_user.setLeader();
		
		for(int i=0; i<count(); i++)
			if(user_list.get(i).getName()==new_user.getName()) {
				int j=1;
				while(true) {
					Boolean found=false;
					for(int k=0; k<count(); k++)
						if(user_list.get(k).getName()==new_user.getName()+j) {
							found=true;
							break;
						} 
					
					if(found)
						j++;
				
					else {
						new_user.setName(new_user.getName()+j);
						break;
					}
				}	
				
				break;
			} // If 'John' exists, 'John1', 'John2', 'John3' ...
				
		user_list.add(new_user);
	} // Adds a user in the room
	
	public void removeUser(User target_user) {
		user_list.remove(target_user);
	} // Remove specific user in the room
}
