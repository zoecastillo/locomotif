package rnaeditor;

/**
 * This class defines the building block internal loop
 *
 * @author: Janina Reeder
 */
public class InternalLoop extends BuildingBlock{
	
	private static final long serialVersionUID = -3695631556554485046L;
	private int loop53length; //length of the loop segment on the 5' strand
	private int loop35length; //length of the loop segment on the 3' strand
	private int default53length;
	private int default35length;
	private int min53length;
	private int max53length;
	private int defaultmin53length;
	private int defaultmax53length;
	private int min35length;
	private int max35length;
	private int defaultmin35length;
	private int defaultmax35length;
	public static final int MIN = 1; //an internal loop has 2 basepairs (=4) plus at least 1 base per loop (on both sides)
	public static final int MAX = 30;
	private boolean exact53length;
	private boolean exact35length;
	private boolean isdefault53length;
	private boolean isdefault35length;
	private boolean isdefault53min;
	private boolean isdefault53max;
	private boolean isdefault35min;
	private boolean isdefault35max;
	private String loop53motif;
	private String loop35motif;
	private Basepair bp1,bp2;

	
	/**
	 * Constructor for the extended building block Internal Loop
	 *
	 * @param boolean orientation; true = standard, false = reversed
	 */
	public InternalLoop(boolean orientation){
		super(orientation);
		this.name = new String("<u>Internal Loop</u>");
		this.info = new String("<html>"+name+"</html>");
		this.loop53length = -1;
		this.loop35length = -1;
		this.default53length = 3;
		this.default35length = 3;
		this.min53length = -1;
		this.max53length = -1;
		this.defaultmin53length = MIN;
		this.defaultmax53length = MAX;
		this.min35length = -1;
		this.max35length = -1;
		this.defaultmin35length = MIN;
		this.defaultmax35length = MAX;
		this.exact53length = true;
		this.exact35length = true;
		this.isdefault53length = true;
		this.isdefault35length = true;
		this.isdefault53min = false;
		this.isdefault53max = false;
		this.isdefault35min = false;
		this.isdefault35max = false;
		this.bp1 = null;
		this.bp2 = null;
		this.loop53motif = null;
		this.loop35motif = null;
		this.defaultlength = 10;
		this.defaultmin = MIN;
		this.defaultmax = MAX;
	}
	
	/**
	 * Responsible for updating the information string on the building block
	 * This method is called whenever changes are stored for the building block
	 */
	public void updateInfo(){
		info = "<html>";
		info += name;
		if(!isdefault53length){
			if(exact53length){
				info += "<br>5' loop length: ";
				info += loop53length;
			}
			else{
				if(!isdefault53min){
					info += "<br>5' loop minlength: ";
					info += min53length;
				}
				if(!isdefault53max){
					info += "<br>5' loop maxlength: ";
					info += max53length;
				}
			}
		}
		if(loop53motif != null){
			info += "<br>5' loopmotif: ";
			info += loop53motif;
		}
		if(!isdefault35length){
			if(exact35length){
				info += "<br>3' loop length: ";
				info += loop35length;
			}
			else{
				if(!isdefault35min){
					info += "<br>3' loop minlength: ";
					info += min35length;
				}
				if(!isdefault35max){
					info += "<br>3' loop maxlength: ";
					info += max35length;
				}
			}
		}
		if(loop35motif != null){
			info += "<br>5' loopmotif: ";
			info += loop35motif;
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
	 * @return boolean true if exactlength, else false
	 */
	public boolean getIsExactLength(){
		return (this.exact53length && this.exact35length);
	}
	
	/**
	 * @return int maxlength = maximum possible length
	 */
	public int getMaxLength(){
		int a=0,b=0;
		if(!this.isdefault53max){
			a = this.max53length;
		}
		else{
			a = this.defaultmax53length;
		}
		if(!this.isdefault35max){
			b = this.max35length;
		}
		else{
			b = this.defaultmax35length;
		}
		return (a+b+4);
	}
	
	
	
	/** 
	 * @return int minlength = the minimum length
	 */
	public int getMinLength(){
		int a=0,b=0;
		if(!this.isdefault53min){
			a = this.min53length;
		}
		else{
			a = this.defaultmin53length;
		}
		if(!this.isdefault35min){
			b = this.min35length;
		}
		else{
			b = this.defaultmin35length;
		}
		return (a+b+4);
	}

	
	/**
	 * @return int length, the length of the building block
	 */
	public int getLength(){
		int a=0,b=0;
		if(!this.isdefault53length){
			a = this.loop53length;
		}
		else{
			a = this.default53length;
		}
		if(!this.isdefault35length){
			b = this.loop35length;
		}
		else{
			b = this.default35length;
		}
		return (a+b+4);
	}
	
	/**
	 * @return boolean true if exactlength, else false
	 */
	public boolean getIsExact53Length(){
		return this.exact53length;
	}
	
	/**
	 * @return int maxlength = maximum possible length
	 */
	public int getMax53Length(){
		if(!this.isdefault53max){
			return this.max53length;
		}
		return this.defaultmax53length;
	}
	
	
	/**
	 * sets the maximum length
	 *
	 * @param int maxlength, the maximum length
	 */
	public void setMax53Length(int max53length){
		this.max53length =  max53length;
		this.exact53length = false;
		this.isdefault53length = false;
		this.isdefault53max = false;
		updateInfo();
	}
	
	/** 
	 * @return int minlength = the minimum length
	 */
	public int getMin53Length(){
		if(!this.isdefault53min){
			return this.min53length;	
		}
		return this.defaultmin53length;
	}

	/**
	 * sets the minimum length
	 *
	 * @param int minlength, the minimum length
	 */
	public void setMin53Length(int min53length){
		this.min53length = min53length;
		this.exact53length = false;
		this.isdefault53length = false;
		this.isdefault53min = false;
		updateInfo();
	}
	
	/**
	 * sets the length of the building block
	 *
	 * @param int length, the length of the building block
	 */
	public void set53Length(int length53){
		this.loop53length = length53;
		this.exact53length = true;
		this.isdefault53length = false;
		this.isdefault53min = false;
		this.isdefault53max = false;
		updateInfo();
	}
	
	/**
	 * @return int length, the length of the building block
	 */
	public int get53Length(){
		if(!this.isdefault53length){
			return this.loop53length;
		}
		else{
			return this.default53length;
		}
	}

	/**
	 * Reseting the 5' loop to default exact length
	 *
	 */
	public void setToDefault53Length(){
		this.loop53length = -1;
		this.min53length = -1;
		this.max53length = -1;
		this.exact53length = true;
		this.isdefault53length = true;
		this.isdefault53min = false;
		this.isdefault53max = false;
		updateInfo();
	}
	
	/**
	 * Reseting the 5' loop to default minimum and maximum values
	 *
	 */
	public void setToDefault53MinMax(){
		this.loop53length = -1;
		this.min53length = -1;
		this.max53length = -1;
		this.exact53length = false;
		this.isdefault53length = false;
		this.isdefault53min = true;
		this.isdefault53max = true;
		updateInfo();
	}
	
	/**
	 * Reseting the 5' loop to default minimum
	 *
	 */
	public void setToDefault53Min(){
		this.loop53length = -1;
		this.min53length = -1;
		this.exact53length = false;
		this.isdefault53length = false;
		this.isdefault53min = true;
		updateInfo();
	}
	
	/**
	 * Reseting the 5' loop to default maximum
	 *
	 */
	public void setToDefault53Max(){
		this.loop53length = -1;
		this.max53length = -1;
		this.exact53length = false;
		this.isdefault53length = false;
		this.isdefault53max = true;
		updateInfo();
	}
	
	/**
	 * Checks wether default length effective for 5' loop
	 * @return true, if default for 5'
	 */
	public boolean getIsDefault53Length(){
		return this.isdefault53length;
	}
	
	/**
	 * 
	 * @return true, if default minimum for 5'
	 */
	public boolean getIsDefault53Min(){
		return this.isdefault53min;
	}
	
	/**
	 * 
	 * @return true, if default maximum for 5'
	 */
	public boolean getIsDefault53Max(){
		return this.isdefault53max;
	}
	
	/**
	 * 
	 * @return true, if default min and max for 5'
	 */
	public boolean getIsDefault53MinMax(){
		return (this.isdefault53max && this.isdefault53min);
	}
	
	
	/**
	 * @return boolean true if exactlength, else false
	 */
	public boolean getIsExact35Length(){
		return this.exact35length;
	}
	
	/**
	 * @return int maxlength = maximum possible length
	 */
	public int getMax35Length(){
		if(!this.isdefault35max){
			return this.max35length;
		}
		return this.defaultmax35length;
	}
	
	
	/**
	 * sets the maximum length
	 *
	 * @param int maxlength, the maximum length
	 */
	public void setMax35Length(int max35length){
		this.max35length =  max35length;
		this.exact35length = false;
		this.isdefault35length = false;
		this.isdefault35max = false;
		updateInfo();
	}
	
	/** 
	 * @return int minlength = the minimum length
	 */
	public int getMin35Length(){
		if(!this.isdefault35min){
			return this.min35length;	
		}
		return this.defaultmin35length;
	}

	/**
	 * sets the minimum length
	 *
	 * @param int minlength, the minimum length
	 */
	public void setMin35Length(int min35length){
		this.min35length = min35length;
		this.exact35length = false;
		this.isdefault35length = false;
		this.isdefault35min = false;
		updateInfo();
	}
	
	/**
	 * sets the length of the building block
	 *
	 * @param int length, the length of the building block
	 */
	public void set35Length(int length35){
		this.loop35length = length35;
		this.exact35length = true;
		this.isdefault35length = false;
		this.isdefault35min = false;
		this.isdefault35max = false;
		updateInfo();
	}
	
	/**
	 * @return int length, the length of the building block
	 */
	public int get35Length(){
		if(!this.isdefault35length){
			return this.loop35length;
		}
		else{
			return this.default35length;
		}
	}

	
	/**
	 * reset to default 3' exact length
	 *
	 */
	public void setToDefault35Length(){
		this.loop35length = -1;
		this.min35length = -1;
		this.max35length = -1;
		this.exact35length = true;
		this.isdefault35length = true;
		this.isdefault35min = false;
		this.isdefault35max = false;
		updateInfo();
	}
	
	/**
	 * reset to default 3' minimum and maximum
	 *
	 */
	public void setToDefault35MinMax(){
		this.loop35length = -1;
		this.min35length = -1;
		this.max35length = -1;
		this.exact35length = false;
		this.isdefault35length = false;
		this.isdefault35min = true;
		this.isdefault35max = true;
		updateInfo();
	}
	
	/**
	 * reset to default 3' minimum
	 *
	 */
	public void setToDefault35Min(){
		this.loop35length = -1;
		this.min35length = -1;
		this.exact35length = false;
		this.isdefault35length = false;
		this.isdefault35min = true;
		updateInfo();
	}
	
	/**
	 * reset to default 3' maximum
	 *
	 */
	public void setToDefault35Max(){
		this.loop35length = -1;
		this.max35length = -1;
		this.exact35length = false;
		this.isdefault35length = false;
		this.isdefault35max = true;
		updateInfo();
	}
	
	/**
	 * 
	 * @return true, if default exact length for 3'
	 */
	public boolean getIsDefault35Length(){
		return this.isdefault35length;
	}
	
	/**
	 * 
	 * @return true, if default min for 3'
	 */
	public boolean getIsDefault35Min(){
		return this.isdefault35min;
	}
	
	/**
	 * 
	 * @return true, if default max for 3'
	 */
	public boolean getIsDefault35Max(){
		return this.isdefault35max;
	}
	
	/**
	 * 
	 * @return true, if default min and max for 3'
	 */
	public boolean getIsDefault35MinMax(){
		return (this.isdefault35max && this.isdefault35min);
	}

	
	/**
	 * Stores motif for 5' strand
	 * @param loop53motif, the motif to be stored
	 */
	public void setLoop53Motif(String loop53motif){
		if(loop53motif != null && loop53motif.length() == 0){
			this.loop53motif = null;
		}
		else{
			this.loop53motif = loop53motif;
		}
		updateInfo();
	}
	
	/**
	 * Returns 5' strand motif
	 * @return String holding the motif
	 */
	public String getLoop53Motif(){
		return this.loop53motif;
	}
	
	/**
	 * Stores motif for 3' loop
	 * @param loop35motif, the motif to be stored
	 */
	public void setLoop35Motif(String loop35motif){
		if(loop35motif != null && loop35motif.length() == 0){
			this.loop35motif = null;
		}
		else{
			this.loop35motif = loop35motif;
		}
		updateInfo();
	}
	
	/**
	 * Returns 3' strand motif
	 * @return String holding the motif
	 */
	public String getLoop35Motif(){
		return this.loop35motif;
	}
	
	/**
	 * Stores a basepair
	 * @param f, 5' char of basepair
	 * @param s, 3' char of basepair
	 * @param num, 0 for 5' basepair, 1 for 3' basepair
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
	 * Stores basepair according to given string
	 * @param bpstring holding the basepair
	 * @param num, 0 for 5' basepair, 1 for 3'
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
	 * Returns a stored basepair
	 * @param num, number of the basepair (1st = 0, 2nd = 1)
	 * @return Basepair
	 */
	public Basepair getBasepair(int num){
		if(num == 0){
			return bp1;
		}
		return bp2;
	}
	
	/**
	 * Returns the stored basepair in string format
	 * @param num, number of the basepair (1st = 0, 2nd = 1)
	 * @return String holding the basepair
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
	 * this method switches the 5'-3' loop with the 3'-5' loop
	 */
	public void switchSides(){
		int buf;
		
		buf = loop53length;
		this.loop53length = this.loop35length;
		this.loop35length = buf;
		
		buf = this.min53length;
		this.min53length = this.min35length;
		this.min35length = buf;
		
		buf = this.max53length;
		this.max53length = this.max35length;
		this.max35length = buf;
		
		boolean buf2;
		buf2 = this.exact53length;
		this.exact53length = this.exact35length;
		this.exact35length = buf2;
		
		buf2 = this.isdefault53length;
		this.isdefault53length = this.isdefault35length;
		this.isdefault35length = buf2;
		
		buf2 = this.isdefault53min;
		this.isdefault53min = this.isdefault35min;
		this.isdefault35min = buf2;
		
		buf2 = this.isdefault53max;
		this.isdefault53max = this.isdefault35max;
		this.isdefault35max = buf2;
	

        if(this.bp1 != null){
            this.bp1.switchSides();
        }
        if(this.bp2 != null){
            this.bp2.switchSides();
        }
        
		String loopbuf = loop53motif;
		loop53motif = loop35motif;
		loop35motif = loopbuf;
		updateInfo();
	}
	
	/**
	 * Method for reversing the stored sequence motifs
	 *
	 */
	public void reverseStrings(){
		if(this.loop53motif != null){
			StringBuffer st = new StringBuffer(this.loop53motif);
			st.reverse();
			this.loop53motif = st.toString();
		} 
		if(this.loop35motif != null){
			StringBuffer st = new StringBuffer(this.loop35motif);
			st.reverse();
			this.loop35motif = st.toString();
		} 
		updateInfo();
	}
}
