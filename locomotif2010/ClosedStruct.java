package rnaeditor;

/**
 * This class defines the building block closedstruct
 *
 * @author: Janina Reeder
 */
public class ClosedStruct extends BuildingBlock{
	
	
	private static final long serialVersionUID = 3021280628311655711L;

	/**
	 * Constructor for the extended building block closedstruct
	 *
	 * @param boolean orientation; true = standard orientation, false = reverse
	 */
	public ClosedStruct(boolean orientation){
		super(orientation);
		this.name = new String("<u>ClosedStruct</u>");
		this.info = new String("<html>"+name+"</html>");
	}
	
	/**
	 * Responsible for updating the information string on the building block
	 * This method is called whenever changes are stored for the building block
	 */
	public void updateInfo(){
		info = "<html>";
		info += name;
		if(hasGlobalLength()){
			String min = getMinGlobalLength();
			String max = getMaxGlobalLength();
			info += "<br><br>Global Size:<br>";
			if(min != null){
				info += "minsize: ";
				info += min;
				info += "<br>";
			}
			if(max != null){
				info += "maxsize: ";
				info += max;
			}
		}
		info += "</html>";
	}
}
