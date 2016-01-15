package rnaeditor;

/**
 * This class defines the building block Bulge
 *
 * @author: Janina Reeder
 */
public class Bulge extends BuildingBlock{
	
	private static final long serialVersionUID = 2576519529398858308L;
    /**
	 * The minimum allowed size for a Bulge (2 basepairs plus min. 1 base in the loop)
	 */
	public final static int MIN = 5;
	/**
	 * The maximum recommended size for a Bulge (30 bases in the loop segments)
	 */
	public final static int MAX = 34;
	private boolean bulgeloc_5_3; // true if bulge is on 5'-3' sequence
	private String loopmotif;
	private Basepair bp1,bp2;
	
	/**
	 * Constructor for the extended building block Bulge
	 *
	 * @param boolean orientation; true = standard, false = reversed
	 */
	public Bulge(boolean orientation){
		super(orientation);
		this.name = new String("<u>Bulge</u>");
		this.info = new String("<html>"+name+"</html>");
		this.defaultlength = 8;
		this.defaultmin = MIN;
		this.defaultmax = MAX;
		this.bulgeloc_5_3 = true;
		this.loopmotif = null;
		this.bp1 = null;
		this.bp2 = null;
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
				info += (length-4);
			}
			else{
				if(!isdefaultmin){
					info += "<br>loop minlength: ";
					info += (minlength-4);
				}
				if(!isdefaultmax){
					info += "<br>loop maxlength: ";
					info += (maxlength-4);
				}
			}
		}
		if(loopmotif != null){
			info += "<br>loopmotif: ";
			info += loopmotif;
		}
		if(bulgeloc_5_3){
			info += "<br>loop location: 5' strand";
		}
		else{
			info += "<br>loop location: 3' strand";
		}
		if(bp1 != null){
			info += "<br>5' basepair: ";
			info += bp1.getString();
		}
		if(bp2 != null){
			info += "<br>3' basepair: ";
			info += bp2.getString();
		}
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
	
	/**
	 * Switches the location of the bulge from 5'-3' to 3'-5' and vice versa
	 */
	public void changeBulgeLoc(){
		if(bulgeloc_5_3){
			this.bulgeloc_5_3 = false;
		}
		else{
			this.bulgeloc_5_3 = true;
		}
	}
	
	/**
	 * Method to check for the bulge location
	 *
	 *@return true, if located on 5'-3' strand, else false
	 */
	public boolean getBulgeLoc(){
		return this.bulgeloc_5_3;
	}
	
	/**
	 * Stores a sequence motif for the loop section
	 * @param loopmotif, the sequence to be stored
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
	 * 
	 * @return the sequence of the loop section
	 */
	public String getLoopMotif(){
		return this.loopmotif;
	}
	
	/**
	 * Stores a sequence motif for one of the exit basepairs
	 * @param f, the first letter of the basepair
	 * @param s, the second letter of the basepair
	 * @param num, the index of the basepair: 0 = 5'-pair, 1 = 3'-pair
	 */
	public void setBasepair(char f, char s, int num){
		if(num == 0){
			if(bp1 == null){
				bp1 = new Basepair(f,s);
			}
			else{
				bp1.setPair(f,s);
			}
		}
		else{
			if(bp2 == null){
				bp2 = new Basepair(f,s);
			}
			else{
				bp2.setPair(f,s);
			}
		}
	}
	
	/**
	 * Stores a Basepair sequence motif via a String holding the motif
	 * @param bpstring, the String storing the Basepair (consists of two chars)
	 * @param num, the index of the basepair: 0 = 5'-pair, 1 = 3'-pair
	 */
	public void setBasepair(String bpstring, int num){
		if(bpstring.length() == 2){
			setBasepair(bpstring.charAt(0), bpstring.charAt(1), num);
		}
		else if(num == 0){
			bp1 = null;
		}
		else{
			bp2 = null;
		}	
		updateInfo();
	}
	
	/**
	 * Returns the chosen basepair
	 * @param num, the index of the basepair: 0 = 5'-pair, 1 = 3'-pair
	 * @return the Basepair
	 */
	public Basepair getBasepair(int num){
		if(num == 0){
			return bp1;
		}
		return bp2;
	}
	
	/**
	 * Returns the chosen basepair in String representation
	 * @param num, the index of the basepair: 0 = 5'-pair, 1 = 3'-pair
	 * @return the Basepair as a String
	 */
	public String getBasepairString(int num){
		if(num == 0 && bp1 != null){
			return bp1.getString();
		}
		else if(num == 1 && bp2 != null){
			return bp2.getString();
		}
		return null;
	}
	
	/**
	 * Switches the Sides of the Bulge, i.e. both Basepairs must switch sides
	 *
	 */
	public void switchSides(){
		if(this.bp1 != null){
			this.bp1.switchSides();
		}
		if(this.bp2 != null){
			this.bp2.switchSides();
		}
		updateInfo();
	}
	
	/**
	 * Reverses the stored sequence motif of the loop (orientation was changed).
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

