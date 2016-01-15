package rnaeditor;

/**
 * This class defines the building block single strand
 *
 * @author: Janina Reeder
 */
public class SingleStrand extends BuildingBlock{
	
	private static final long serialVersionUID = 2590598050076298457L;
	public static final int MIN=0;
	public static final int MAX=200;
	private String strandmotif;
	private boolean isstraight;
	private boolean isconnector;
	
	/**
	 * Constructor for the extended building block Single Strand
	 * 
	 * @param boolean orientation; true = standard, false = reversed
	 */
	public SingleStrand(boolean orientation){
		super(orientation);
		this.name = new String("<u>Single Strand</u>");
		this.info = new String("<html>"+name+"</html>");
		this.defaultlength = 8;
		this.defaultmin = MIN;
		this.defaultmax = MAX;
		this.strandmotif = null;
		this.isstraight = true;
		this.isconnector = false;
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
				info += "<br>length: ";
				info += length;
			}
			else{
				if(!isdefaultmin){
					info += "<br>minlength: ";
					info += minlength;
				}
				if(!isdefaultmax){
					info += "<br>maxlength: ";
					info += maxlength;
				}
			}
		}
		if(strandmotif != null){
			info += "<br>strandmotif: ";
			info += strandmotif;
		}
		if(!isstraight){
			info += "<br><br>possible motif part contained";
		}
		info += "</html>";
	}
	
	/**
	 * This method stores a motif for the single strand
	 * 
	 * @param strandmotif, String holding the motif to be stored
	 */
	public void setStrandMotif(String strandmotif){
		if(strandmotif != null && strandmotif.length() == 0){
			this.strandmotif = null;
		}
		else{
			this.strandmotif = strandmotif;
		}
		updateInfo();
	}
	
	/**
	 * Getter method for the strand motif
	 * 
	 * @return the String motif
	 */
	public String getStrandMotif(){
		return this.strandmotif;
	}
	
	/**
	 * This method reverses the stored string
	 *
	 */
	public void reverseStrings(){
		if(this.strandmotif != null){
			StringBuffer st = new StringBuffer(this.strandmotif);
			st.reverse();
			this.strandmotif = st.toString();
			updateInfo();
		} 
	}
	
	/**
	 * Setter method for the flag indicated whether internal folding is allowed
	 * 
	 * @param isstraight, true if no folding is allowed; else false
	 */
	public void setIsStraight(boolean isstraight){
		this.isstraight = isstraight;
	}
	
	/**
	 * Getter method for the flag indicating whether internal folding is allowed
	 * 
	 * @return true, if not allowed; else false
	 */
	public boolean getIsStraight(){
		return this.isstraight;
	}
	
	/**
	 * This method is called to store the fact that the single strand is a connecting one
	 *
	 */
	public void setIsConnector(){
		this.isconnector = true;
		updateInfo();
	}
	
	/**
	 * Checks whether the single strand is a connecting one
	 * 
	 * @return true, if ss is a connector; else false
	 */
	public boolean getIsConnector(){
		return this.isconnector;
	}
}
