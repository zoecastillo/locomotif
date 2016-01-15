package rnaeditor;

/**
 * This class defines the building block multiloop
 *
 * @author: Janina Reeder
 */
public class MultiLoop extends BuildingBlock{
	
	private static final long serialVersionUID = 928566724720855897L;
	public static final int MIN=0;//a multiloop has at least 3 basepairs (and no bases in the loops connecting the basepairs
	public static final int MAX=30;
	private String[] loopmotifs;
	private int[] looplengths;
	private int[] minlengths;
	private int[] maxlengths;
	private int[] exitstructure;
	private Basepair[] bps;
	private int startexit;
	
	
	/**
	 * Constructor for the extended building block MultiLoop
	 *
	 * @param boolean orientation; true = standard, false = reversed
	 */
	public MultiLoop(boolean orientation, int[] exitstructure){
		super(orientation);
		this.name = new String("<u>Multiloop</u>");
		this.info = new String("<html>"+name+"</html>");
		this.defaultlength = 12;
		this.defaultmin = MIN;
		this.defaultmax = MAX;
		this.loopmotifs = new String[8];
		this.looplengths = new int[8];
		this.minlengths = new int[8];
		this.maxlengths = new int[8];
		for(int i=0;i<8;i++){
			this.looplengths[i] = -1;
			this.minlengths[i] = -1;
			this.maxlengths[i] = -1;
		}
		this.exitstructure = new int[8];
		for(int i= 0;i<8;i++){
			this.exitstructure[i] = exitstructure[i];
		}
		this.bps = new Basepair[8];
		this.startexit = 0;
	}
	
	public void setStartIndex(int startexit){
		if(startexit < 0){
			startexit = 8 + startexit;
		}
		this.startexit = startexit;
		updateInfo();
	}
	
	/**
	 * Responsible for updating the information string on the building block
	 * This method is called whenever changes are stored for the building block
	 */
	public void updateInfo(){
		info = "<html>";
		info += name;
		String lengths = "[";
		String motifs = "[";
		String basepairs = "[";
		String comma = "";
		int i = startexit;
		if(!orientation){
			i--;
			if(i<0){
				i= 7;
			}
		}
		for(int j=0;j<8;j++){
			if(exitstructure[i] == 1){
				lengths += comma;
				motifs += comma;
				basepairs += comma;
				if(looplengths[i] != -1){
					lengths += looplengths[i];
				}
				else if(minlengths[i] != -1 && maxlengths[i] != -1){
					lengths += minlengths[i];
					lengths += "-";
					lengths += maxlengths[i];
				}
				else if(minlengths[i] != -1){
					lengths += "min ";
					lengths += minlengths[i];
				}
				else if(maxlengths[i] != -1){
					lengths += "max ";
					lengths += maxlengths[i];
				}
				else{
					lengths += "_";
				}
				if(loopmotifs[i] != null){
					motifs += loopmotifs[i];
				}
				else{
					motifs += "_";
				}
				if(bps[i] != null){
					basepairs += getBasepairString(i);
				}
				else{
					basepairs += "_";
				}
				comma = ",";
			}
			if(orientation){
				i++;
				i = i%8;
			}
			else{
				i--;
				if(i<0){
					i= 7;
				}
			}
		}
		lengths += "]";
		motifs += "]";
		basepairs += "]";
		info += "<br>loop lengths: ";
		info += lengths;
		info += "<br>loop motifs: ";
		info += motifs;
		info += "<br>basepairs: ";
		info += basepairs;
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
	 * Method for changing the exits of the multiloop
	 * 
	 * @param exitstructure, the new exitstructure indicating which exits are present
	 */
	public void changeExits(int[] exitstructure){
		for(int i=0;i<8;i++){
			this.exitstructure[i] = exitstructure[i];
		}
		updateInfo();
	}
	
	/**
	 * This method computes the nearest available exit counterclockwise
	 * 
	 * @param index, the current exit
	 * @return index of the nearest exit counterclockwise
	 */
	public int getNextAvailable(int index){
		for(int j=0;j<8;j++){
			index -= 1;
			if(index < 0){
				index = 7;
			}
			if(exitstructure[index] == 1){
				return index;
			}
		}
		return -1;
	}
	
	/**
	 * Method for storing a loopmotif at a specific location
	 * 
	 * @param loopnum, the index of the loop holding the motif
	 * @param loopmotif, the String representation of the motif
	 */
	public void setLoopMotif(int loopnum, String loopmotif){
		if(loopnum > 7){
			//change to exception later on!!!!
			System.out.println("Error in setting loop motif: should not occur");
			return;
		}
		if(loopmotif != null && loopmotif.length() == 0){
			this.loopmotifs[loopnum] = null;
		}
		else{
			this.loopmotifs[loopnum] = loopmotif;
		}
		updateInfo();
	}
	
	/**
	 * Method for storing an exact looplength at a specific location
	 * 
	 * @param loopnum, the index of the loop holding the motif
	 * @param looplength, the length of the loop
	 */
	public void setExactLoopLength(int loopnum, int looplength){
		if(loopnum > 7){
			//change to exception later on!!!!
			System.out.println("Error in setting loop motif: should not occur");
			return;
		}
		this.looplengths[loopnum] = looplength;
		this.minlengths[loopnum] = -1;
		this.maxlengths[loopnum] = -1;
		updateInfo();
	}
	
	/**
	 * Method for storing a range looplength at a specific location
	 * 
	 * @param loopnum, the index of the loop holding the motif
	 * @param minlength, the length of the loop
	 * @param maxlength
	 */
	public void setRangeLoopLength(int loopnum, int minlength, int maxlength){
		if(loopnum > 7){
			//change to exception later on!!!!
			System.out.println("Error in setting loop motif: should not occur");
			return;
		}
		this.minlengths[loopnum] = minlength;
		this.maxlengths[loopnum] = maxlength;
		this.looplengths[loopnum] = -1;
		updateInfo();
	}
	
	/**
	 * Resets the specified loop to the default length
	 * 
	 * @param loopnum, the index of the loop which is reseted
	 */
	public void setToDefaultLength(int loopnum){
		if(loopnum > 7){
			//change to exception later on!!!!
			System.out.println("Error in setting loop motif: should not occur");
			return;
		}
		this.looplengths[loopnum] = -1;
		this.minlengths[loopnum] = -1;
		this.maxlengths[loopnum] = -1;
		updateInfo();
	}
	
	/**
	 * This method gives the stored loopmotif of a given index
	 * 
	 * @param loopnum, the index for the motif
	 * @return a String holding the motif (can be null if unavailable).
	 */
	public String getLoopMotif(int loopnum){
		if(loopnum > 7){
			//change to exception later on or remove
			System.out.println("Error in getting loop motif: should not occur");
			return null;
		}
		return this.loopmotifs[loopnum];
	}
	
	/**
	 * This method gives the exact looplength of a given index
	 * 
	 * @param loopnum, the index for the motif
	 * @return the length as int
	 */
	public int getExactLoopLength(int loopnum){
		if(loopnum > 7){
			//change to exception later on or remove
			System.out.println("Error in getting loop motif: should not occur");
			return -1;
		}
		return this.looplengths[loopnum];
	}
	
	/**
	 * This method gives the minimum looplength of a given index
	 * 
	 * @param loopnum, the index for the motif
	 * @return the length as int
	 */
	public int getMinLoopLength(int loopnum){
		if(loopnum > 7){
			//change to exception later on or remove
			System.out.println("Error in getting loop motif: should not occur");
			return -1;
		}
		return this.minlengths[loopnum];
	}
	
	/**
	 * This method gives the maximum looplength of a given index
	 * 
	 * @param loopnum, the index for the motif
	 * @return the length as int
	 */
	public int getMaxLoopLength(int loopnum){
		if(loopnum > 7){
			//change to exception later on or remove
			System.out.println("Error in getting loop motif: should not occur");
			return -1;
		}
		return this.maxlengths[loopnum];
	}
	
	/**
	 * Checks wether the addition of a loop motif at a specific location is 
	 * possible because of size requirements.
	 * 
	 * @param loopnum, the index where the motif should be added
	 * @param loopmotif, the motif that is to be added
	 * @return true, if addition is possible; else false.
	 */
	public boolean motifAdditionPossible(int loopnum, String loopmotif){
		if(loopnum > 7){
			//change to exception later on or remove
			System.out.println("Error in getting loop motif: should not occur");
			return false;
		}
		if(looplengths[loopnum] == -1 && maxlengths[loopnum] == -1){
			return true;
		}
		else if(looplengths[loopnum] > -1 && loopmotif.length() <= looplengths[loopnum]){
			return true;
		}
		else if(maxlengths[loopnum] > -1 && loopmotif.length() <= maxlengths[loopnum]){
			return true;
		}
		return false;
	}
	
	
	/**
	 * This method fuses two stored loop motifs after the exit between them 
	 * was removed
	 * 
	 * @param index, the index of the exit that was removed
	 * @param exitstructure, the current exit structure
	 */
	public void fuseLoopMotifs(int index, int[] exitstructure){
		int j = index - 1;
		for(int i=0;i<6;i++){
			if(j<0){
				j=7;
			}
			if(exitstructure[j] == 1){
				break;
			}
			j--;
		}
		if(loopmotifs[index] != null){
			if(loopmotifs[j] != null){
				loopmotifs[j] = loopmotifs[j].concat(loopmotifs[index]);
			}
			else{
				loopmotifs[j] = loopmotifs[index];
			}
			loopmotifs[index] = null;
		}
	}
	
	/**
	 * This method divides a stored loop motif if an exit was introduced at
	 * its location
	 * 
	 * @param index, the exit that was introduced
	 * @param exitstructure, the current exit structure
	 */
	public void partLoopMotifs(int index, int[] exitstructure){
		int j = index - 1;
		for(int i=0;i<6;i++){
			if(j<0){
				j=7;
			}
			if(exitstructure[j] == 1){
				break;
			}
			j--;
		}
		if(loopmotifs[j] != null){
			String firsthalf = loopmotifs[j];
			String secondhalf = null;
			if(loopmotifs[j].length() > 1){
				int half = (loopmotifs[j].length())/2;
				firsthalf = loopmotifs[j].substring(0,half);
				secondhalf = loopmotifs[j].substring(half,loopmotifs[j].length());
			}
			loopmotifs[j] = firsthalf;
			loopmotifs[index] = secondhalf;
		}
	}
	
	/**
	 * This method stores a Basepair according to the given parameters.
	 * 
	 * @param exitnum, the index of the exit of the Basepair
	 * @param f, the first base of the Pair
	 * @param s, the second base of the Pair
	 */
	public void setBasepair(int exitnum, char f, char s){
		if(exitnum > 7){
			//change to exception later on!!!!
			System.out.println("Error in setting basepair: should not occur");
			return;
		}
		if(bps[exitnum] == null){
			bps[exitnum] = new Basepair(f,s);
		}
		else{
			bps[exitnum].setPair(f,s);
		}
	}
	
	/**
	 * This method stores a Basepair according to the given parameters.
	 * 
	 * @param exitnum, the index of the exit of the Basepair
	 * @param bpstring, the String representing the Basepair
	 */
	public void setBasepair(int exitnum, String bpstring){
		if(exitnum > 7){
			//change to exception later on!!!!
			System.out.println("Error in setting basepair: should not occur");
			return;
		}
		if(bpstring == null){
			bps[exitnum] = null;
		}
		else{
			if(bpstring.length() == 2){
				setBasepair(exitnum, bpstring.charAt(0), bpstring.charAt(1));
			}
			else{
				bps[exitnum] = null;
			}	
		}
		updateInfo();
	}
	
	/**
	 * This method adjust the stored Basepairs according to the given exitstructure:
	 * if there is no exit at an index, then the Basepair will be set to null.
	 * 
	 * @param exitstructure, the exit structure to use
	 */
	public void adjustBasepairs(int[] exitstructure){
		for(int i=0;i<8;i++){
			if(exitstructure[i] == 0){
				this.bps[i] = null;
			}
		}
		updateInfo();
	}
	
	/**
	 * This method gives the stored Basepair of the given index. (can be null).
	 * 
	 * @param exitnum, the index of the Basepair
	 * @return, the stored Basepair or null if unavailable.
	 */
	public Basepair getBasepair(int exitnum){
		if(exitnum > 7){
			//change to exception later on!!!!
			System.out.println("Error in getting basepair: should not occur");
			return null;
		}
		return this.bps[exitnum];
	}
	
	/**
	 * This method returns a String representation of the Basepair at the given index.
	 * 
	 * @param exitnum, the index of the Basepair
	 * @return its String representation or null if unavailable.
	 */
	public String getBasepairString(int exitnum){
		if(exitnum > 7){
			//change to exception later on!!!!
			System.out.println("Error in getting basepair: should not occur");
			return null;
		}
		if(this.bps[exitnum] != null){
			return this.bps[exitnum].getString();
		}
		return null;
	}
	
	/**
	 * Method for switching the sides of all basepairs (called after orientation
	 * change).
	 *
	 */
	public void switchSides(){
		for(int i=0;i<8;i++){  
			if(this.bps[i] != null){
				this.bps[i].switchSides();
			}
		}
		updateInfo();
	}
	
	/**
	 * This method switches the sides of the Basepair at the given index if available.
	 * (needed for Traversal to a new Start via a Multiloop).
	 * 
	 * @param exitnum, the exit index whose basepair is to be switched.
	 */
	public void switchSides(int exitnum){
		if(this.bps[exitnum] != null){
			this.bps[exitnum].switchSides();
			updateInfo();
		}
	}
	
	/**
	 * This method reverses the Strings for all stored loop motifs
	 *
	 */
	public void reverseStrings(){
		for(int i=0;i<8;i++){
			if(this.loopmotifs[i] != null){
				StringBuffer st = new StringBuffer(this.loopmotifs[i]);
				st.reverse();
				this.loopmotifs[i] = st.toString();
			} 
		}
		updateInfo();
	}
}
