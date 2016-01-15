package rnaeditor;
/**
 * This class defines the basic properties that each building block possesses.
 *
 * @author: Janina Reeder
 */

import java.io.Serializable;

/**
 * Parent class for all building blocks on the data level
 * @author Janina
 */
public class BuildingBlock implements Serializable{
	
	private static final long serialVersionUID = -3958987443799528118L;
	protected String name;   //the name of the building block (its type)
	protected boolean orientation; //true for standard, false for reversed
	protected int length; //exact length (number of bases)
	protected int defaultlength; //used for visualization purposes
	protected int minlength; //minimum length
	protected int defaultmin; //used for visualization purposes
	protected int maxlength; //maximum length
	protected int defaultmax; //used for visualization purposes
	protected boolean exactlength; //exact length or length range
	protected boolean isdefaultlength;
	protected boolean isdefaultmin;
	protected boolean isdefaultmax;
	protected int minglobal, maxglobal; //global length variables
	protected boolean hasglobal;
	protected String info; //contains all information on the building block
	
	/**
	 * Constructor for Building Block
	 * 
	 * @param orientation defines the 5'-3' orientation of the building block 
	 *        (true = standard orientation, false = reverse orientation)
	 */
	public BuildingBlock(boolean orientation){
		this.exactlength = true;
		this.orientation = orientation;
		this.length = -1;
		this.minlength = -1;
		this.maxlength = -1;
		this.isdefaultlength = true;
		this.isdefaultmin = false;
		this.isdefaultmax = false;
		name = null;
		this.minglobal = 0;
		this.maxglobal = 0;
		this.info = null;
	}
	
	/**
	 * This method returns the information string for the building block
	 * 
	 * @return a String containing all relevant information on the building block
	 */
	public String getInfo(){
		return this.info;
	}
	
	/**
	 * This method body is empty and overwritten in all subclasses.
	 * It updates the info string
	 *
	 */
	protected void updateInfo(){
		//defined in subclasses
	}
	
	/**
	 * @return boolean true if exactlength, else false
	 */
	public boolean getIsExactLength(){
		return this.exactlength;
	}
	
	/**
	 * @return int maxlength = maximum possible length
	 */
	public int getMaxLength(){
		if(!this.isdefaultmax){
			return this.maxlength;
		}
		return this.defaultmax;
	}
	
	
	/**
	 * Sets the maximum length
	 *
	 * @param int maxlength, the maximum length
	 */
	public void setMaxLength(int maxlength){
		this.maxlength =  maxlength;
		this.exactlength = false;
		this.isdefaultlength = false;
		this.isdefaultmax = false;
		updateInfo();
	}
	
	/** 
	 * @return int minlength = the minimum length
	 */
	public int getMinLength(){
		if(!this.isdefaultmin){
			return this.minlength;	
		}
		return this.defaultmin;
	}

	/**
	 * Sets the minimum length
	 *
	 * @param int minlength, the minimum length
	 */
	public void setMinLength(int minlength){
		this.minlength = minlength;
		this.exactlength = false;
		this.isdefaultlength = false;
		this.isdefaultmin = false;
		updateInfo();
	}
	
	/**
	 * Sets the length of the building block
	 *
	 * @param int length, the length of the building block
	 */
	public void setLength(int length){
		this.length = length;
		this.exactlength = true;
		this.isdefaultlength = false;
		this.isdefaultmin = false;
		this.isdefaultmax = false;
		updateInfo();
	}
	
	/**
	 * @return int length, the length of the building block
	 */
	public int getLength(){
		if(!this.isdefaultlength){
			return this.length;
		}
		else{
			return this.defaultlength;
		}
	}

	/**
	 * Resets this building block to its default exact length
	 *
	 */
	public void setToDefaultLength(){
		this.length = -1;
		this.minlength = -1;
		this.maxlength = -1;
		this.exactlength = true;
		this.isdefaultlength = true;
		this.isdefaultmin = false;
		this.isdefaultmax = false;
		updateInfo();
	}
	
	/**
	 * Resets this building block to its default minimum and maximum length.
	 *
	 */
	public void setToDefaultMinMax(){
		this.length = -1;
		this.minlength = -1;
		this.maxlength = -1;
		this.exactlength = false;
		this.isdefaultlength = false;
		this.isdefaultmin = true;
		this.isdefaultmax = true;
		updateInfo();
	}
	
	/**
	 * Resets this building block to its default minimum length.
	 *
	 */
	public void setToDefaultMin(){
		this.length = -1;
		this.minlength = -1;
		this.exactlength = false;
		this.isdefaultlength = false;
		this.isdefaultmin = true;
		updateInfo();
	}
	
	/**
	 * Resets this building block to its default maximum length.
	 *
	 */
	public void setToDefaultMax(){
		this.length = -1;
		this.maxlength = -1;
		this.exactlength = false;
		this.isdefaultlength = false;
		this.isdefaultmax = true;
		updateInfo();
	}
	
	/**
	 * 
	 * @return true, if the default length is active
	 */
	public boolean getIsDefaultLength(){
		return this.isdefaultlength;
	}
	
	/**
	 * 
	 * @return true, if the default minimum length is active.
	 */
	public boolean getIsDefaultMin(){
		return this.isdefaultmin;
	}
	
	/**
	 * 
	 * 	@return true, if the default maximum length is active.
	 */
	public boolean getIsDefaultMax(){
		return this.isdefaultmax;
	}
	
	/**
	 * 
	 * @return true, if the default minimum and maximum lengths are active.
	 */
	public boolean getIsDefaultMinMax(){
		return (this.isdefaultmax && this.isdefaultmin);
	}
	
	/**
	 * @return boolean orientation, the orientation of the building block
	 */
	public boolean getOrientation(){
		return this.orientation;
	}
	
	/**
	 * Sets the orientation of the building block
	 *
	 * @param boolean orientation, true = standard, false = reverse
	 */
	public void setOrientation(boolean orientation){
		this.orientation = orientation;
	}
	
	/**
	 * @return String name, the name of the building block
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * Removes any global length restrictions for this building block
	 *
	 */
	public void removeGlobalLength(){
		this.hasglobal = false;
		this.minglobal = 0;
		this.maxglobal = 0;
		updateInfo();
	}
	
	/**
	 * Removes a minimum global length restriction for this building block
	 *
	 */
	public void removeGlobalMin(){
		this.minglobal = 0;
		if(this.maxglobal == 0){
			this.hasglobal = false;
		}
		updateInfo();
	}
	
	/**
	 * Removes a maximum global length restriction for this building block
	 *
	 */
	public void removeGlobalMax(){
		this.maxglobal = 0;
		if(this.minglobal == 0){
			this.hasglobal = false;
		}
		updateInfo();
	}
	
	/**
	 * Stores a minimum global length for the building block
	 * 
	 * @param minglobal, the minimum length of the substructure rooted at this building block
	 */
	public void setMinGlobalLength(int minglobal){
		this.minglobal = minglobal;
		this.hasglobal = true;
		updateInfo();
	}
	
	/**
	 * Returns the minimum global length stored for the building block
	 * 
	 * @return String representation of the minimum length or null, if none given
	 */
	public String getMinGlobalLength(){
		if(this.minglobal > 0){
			return String.valueOf(this.minglobal);
		}
		return null;
	}
	
	/**
	 * Stores a maximum global length for the building block
	 * 
	 * @param maxglobal, the maximum length of the substructure rooted at this building block
	 */
	public void setMaxGlobalLength(int maxglobal){
		this.maxglobal = maxglobal;
		this.hasglobal = true;
		updateInfo();
	}
	
	/**
	 * Returns the maximum global length stored for the building block
	 * 
	 * @return String representation of the maximum length or null, if none given
	 */
	public String getMaxGlobalLength(){
		if(this.maxglobal > 0){
			return String.valueOf(this.maxglobal);
		}
		return null;
	}
	
	/**
	 * Checks whether a global length restriction is given for this building block
	 * 
	 * @return boolean value, true if global length restricted, else false.
	 */
	public boolean hasGlobalLength(){
		return this.hasglobal;
	}
}


