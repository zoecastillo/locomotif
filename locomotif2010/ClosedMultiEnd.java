package rnaeditor;

/**
 * This class defines the building block external loop (hairpin)
 *
 * @author: Janina Reeder
 */
public class ClosedMultiEnd extends BuildingBlock{
	
	private static final long serialVersionUID = 9174152236029031054L;
	public static final int MIN = 5;//a hairpin has 1 basepair (=2) plus at least 3 bases in the loop
	public static final int MAX = 500;
	private Basepair bp;
	
	/**
	 * Constructor for the extended building block Hairpin
	 *
	 * @param boolean orientation; true = standard, false = reversed
	 */
	public ClosedMultiEnd(boolean orientation){
		super(orientation);
		this.name = new String("<u>ClosedMultiEnd</u>");
		this.info = new String("<html>"+name+"</html>");
		this.defaultlength = 8;
		this.defaultmin = MIN;
		this.defaultmax = MAX;
		this.bp = null;
	}
	
	/**
	 * Responsible for updating the information string on the building block
	 * This method is called whenever changes are stored for the building block
	 */
	public void updateInfo(){
		info = "<html>";
		info += name;
		if(!isdefaultlength){
			if(exactlength){
				info += "<br>Compound size: ";
				info += (length);
			}
			else{
				if(!isdefaultmin){
					info += "<br>Compound minsize: ";
					info += (minlength);
				}
				if(!isdefaultmax){
					info += "<br>Compound maxsize: ";
					info += (maxlength);
				}
			}
		}
		if(bp != null){
			info += "<br>basepair: ";
			info += bp.getString();
		}
		info += "</html>";
	}
	
	
	/**
	 * This method stores the two chars as a basepair.
	 * 
	 * @param first, the first base of the pair
	 * @param second, the second base of the pair
	 */
	public void setBasepair(char first, char second){
		if(bp == null){
			bp = new Basepair(first, second);
		}
		else{
			bp.setPair(first,second);
		}
	}
	
	/**
	 * This method stores the given string as a basepair
	 * 
	 * @param bpstring, the String containing the basepair
	 */
	public void setBasepair(String bpstring){
		if(bpstring.length() == 2){
			setBasepair(bpstring.charAt(0), bpstring.charAt(1));
		}
		else{
			bp = null;
		}
		updateInfo();
	}
	
	/**
	 * This method gives the stored basepair
	 * 
	 * @return the Basepair
	 */
	public Basepair getBasepair(){
		return this.bp;
	}
	
	/**
	 * This method gives the stored basepair in its String representation
	 * 
	 * @return the String holding the basepair
	 */
	public String getBasepairString(){
		if(bp != null){
			return bp.getString();
		}
		return null;
	}
	
	/**
	 * This method calls the same method of the Basepair, i.e. the bases are exchanged.
	 *
	 */
	public void switchSides(){
		if(this.bp != null){
			this.bp.switchSides();
			updateInfo();
		}
	}
}
