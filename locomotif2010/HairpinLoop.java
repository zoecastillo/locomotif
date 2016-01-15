package rnaeditor;

/**
 * This class defines the building block external loop (hairpin)
 *
 * @author: Janina Reeder
 */
public class HairpinLoop extends BuildingBlock{
	
	private static final long serialVersionUID = -4972294532141445718L;
	public static final int MIN = 5;//a hairpin has 1 basepair (=2) plus at least 3 bases in the loop
	public static final int MAX = 32;
	private String loopmotif;
	private Basepair bp;
	
	/**
	 * Constructor for the extended building block Hairpin
	 *
	 * @param boolean orientation; true = standard, false = reversed
	 */
	public HairpinLoop(boolean orientation){
		super(orientation);
		this.name = new String("<u>Hairpin Loop</u>");
		this.info = new String("<html>"+name+"</html>");
		this.defaultlength = 8;
		this.defaultmin = MIN;
		this.defaultmax = MAX;
		this.loopmotif = null;
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
				info += "<br>loop length: ";
				info += (length-2);
			}
			else{
				if(!isdefaultmin){
					info += "<br>loop minlength: ";
					info += (minlength-2);
				}
				if(!isdefaultmax){
					info += "<br>loop maxlength: ";
					info += (maxlength-2);
				}
			}
		}
		if(loopmotif != null){
			info += "<br>loopmotif: ";
			info += loopmotif;
		}
		if(bp != null){
			info += "<br>basepair: ";
			info += bp.getString();
		}
		info += "</html>";
	}
	
	/**
	 * This method stores the given loopmotif.
	 * 
	 * @param loopmotif, the motif to be stored
	 */
	public void setLoopMotif(String loopmotif){
		if(loopmotif != null && loopmotif.length() == 0){
			this.loopmotif = null;
		}
		else{
			this.loopmotif = loopmotif;
		}
		updateInfo();
	}
	
	/**
	 * This method gives a stored loop motif.
	 * 
	 * @return the stored loop motif
	 */
	public String getLoopMotif(){
		return this.loopmotif;
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
	
	/**
	 * This method reverses the stored loop motif
	 *
	 */
	public void reverseStrings(){
		if(this.loopmotif != null){
			StringBuffer st = new StringBuffer(this.loopmotif);
			st.reverse();
			this.loopmotif = st.toString();
			updateInfo();
		} 
	}
}
