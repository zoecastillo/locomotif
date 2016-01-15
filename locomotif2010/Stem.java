package rnaeditor;

/**
 * This class defines the building block stem
 *
 * @author: Janina Reeder
 */
public class Stem extends BuildingBlock{
	
	private static final long serialVersionUID = 1498036838537908050L;
	public static final int MIN = 1; //a stem must have at least 1 basepair
	public static final int MAX = 100; //a stem can have at most 100 basepairs
	private String seqmotif;
	private boolean motifon53strand;
	private Basepair[] bps;
	private boolean ismotifgiven;
	private boolean allowinterrupt;
	
	/**
	 * Constructor for the extended building block stem
	 *
	 * @param boolean orientation; true = standard orientation, false = reverse
	 */
	public Stem(boolean orientation){
		super(orientation);
		this.name = new String("<u>Stem</u>");
		this.info = new String("<html>"+name+"</html>");
		this.defaultlength = 8; //length refers to basepairs!!!
		this.defaultmin = MIN;
		this.defaultmax = MAX;
		this.seqmotif = null;
		this.motifon53strand = true;
		this.ismotifgiven = true;
		this.bps = null;
		this.allowinterrupt= false;
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
				info += "<br># of basepairs: ";
				info += length;
			}
			else{
				if(!isdefaultmin){
					info += "<br>min # of basepairs: ";
					info += minlength;
				}
				if(!isdefaultmax){
					info += "<br>max # of basepairs: ";
					info += maxlength;
				}
			}
		}
		if(ismotifgiven && seqmotif != null){
			info += "<br>motif: ";
			info += seqmotif;
			info += "<br>location: ";
			if(motifon53strand){
				info += "5' strand";
			}
			else{
				info += "3' strand";
			}
		}
		else if(bps != null){
			info += "<br>basepairs: ";
			info += getBasepairString();
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
		if(allowinterrupt){
			info += "<br><br>Continuity not enforced";
		}
		info += "</html>";
	}

	/**
	 * @return int maxlength = maximum possible length
	 */
	public int getStemsMaxLength(){
		if(!this.isdefaultmax){
			return this.maxlength;
		}
		return this.minlength + 10;
	}
	
	
	/**
	 * Method for storing a sequence motif on one of the strands.
	 * 
	 * @param seqmotif, the motif as a String
	 * @param motifon53strand, true, if the motif is on the 5' strand; else false.
	 */
	public void setSeqMotif(String seqmotif, boolean motifon53strand){
		if(seqmotif != null && seqmotif.length() == 0){
			this.seqmotif = null;
			this.motifon53strand = true;
		}
		else{
			this.seqmotif = seqmotif;
			this.motifon53strand = motifon53strand;
		}
		this.ismotifgiven = true;
		this.bps = null;
	}
	
	/**
	 * Getter method for the sequence motif
	 * 
	 * @return the String representation of the motif
	 */
	public String getSeqMotif(){
		return this.seqmotif;
	}
	
	/**
	 * This method returns true, if the motif is on the 5' strand.
	 * 
	 * @return true, if motif is on 5' strand; else false.
	 */
	public boolean getIsMotifOn53Strand(){
		return this.motifon53strand;
	}
	
	/**
	 * Method for setting the motif location.
	 * 
	 * @param motifon53strand, true if on 5' strand; else false.
	 */
	public void setIsMotifOn53Strand(boolean motifon53strand){
		this.motifon53strand = motifon53strand;
		updateInfo();
	}
	
	/**
	 * Method for setting the sequence motif.
	 * 
	 * @param seqmotif, string holding the motif.
	 */
	public void setSeqMotif(String seqmotif){
		this.seqmotif = seqmotif;
		this.ismotifgiven = true;
		this.bps = null;
		updateInfo();
	}
	
	/**
	 * Method for storing a number of basepairs.
	 * 
	 * @param bptext, String representation of the basepairs
	 */
	public void setBasepairs(String bptext){
		bps = null;
		if(bptext!= null && bptext.length() > 1){
			String[] bplist = bptext.split(";");
			bps = new Basepair[bplist.length];
			for(int i=0;i<bplist.length;i++){
				if(bplist[i].length() == 2){
					bps[i] = new Basepair(bplist[i].charAt(0),bplist[i].charAt(1));
				}
				else if(bplist[i].equals("-")){
					bps[i] = new Basepair('N','N');
				}
				else{
					System.out.println("error while setting basepairs in stem: "+bplist[i]);
				}
			}
			this.ismotifgiven = false;
			this.seqmotif = null;
		}
		updateInfo();
	}
	
	/**
	 * This method returns the basepairs as an array.
	 * 
	 * @return a Basepair[].
	 */
	public Basepair[] getBasepairs(){
		return this.bps;
	}
	
	/**
	 * This method returns the basepairs in their String representation.
	 * 
	 * @return the String representation of the stored basepairs
	 */
	public String getBasepairString(){
		String bptext = null;
		int i =0;
		if(bps != null){
			bptext = "";
			for(;i<bps.length-1;i++){
				bptext += bps[i].getString();
				bptext += ";";
			}
			bptext += bps[bps.length-1].getString();
		}
		return bptext;
	}
	
	/**
	 * This method checks wether a sequence motif is stored.
	 * 
	 * @return true, if a motif is stored; else false.
	 */
	public boolean getIsMotifGiven(){
		return this.ismotifgiven;
	}
	
	/**
	 * This method checks wether the length of the given basepairs is not
	 * larger than the given parameter.
	 * 
	 * @param bpseq, the basepairs in String representation
	 * @param l, the maximum allowed length
	 * @return true, if length is not larger than l; else false.
	 */
	public int checkBPLength(String bpseq, int l){
		bpseq = bpseq.replaceAll("-","NN");
		String[] bpsarray = bpseq.split(";");
		if(bpsarray.length > l){
			return 1;
		}
		if(bpsarray.length == l){
			return 0;
		}
		return -1;
	}
	
	/**
	 * This method exchanges either the sequence motif location or 
	 * switches all stored basepairs (depending on which is available).
	 *
	 */
	public void switchSides(){
		if(this.seqmotif != null){
			if(this.motifon53strand){
				motifon53strand = false;
			}
			else{
				motifon53strand = true;
			}
		}
		else if(this.bps != null){
			for(int i=0;i<this.bps.length;i++){
				this.bps[i].switchSides();
			}
		}
		updateInfo();
	}
	
	public void reverseBPSeq(){
	    if(this.bps != null){
	        Basepair[] newbps = new Basepair[this.bps.length];
	        for(int i=0,j=this.bps.length-1;i<this.bps.length;i++,j--){
	            newbps[i] = this.bps[j];
	        }
	        this.bps = newbps;
	        updateInfo();
	    }
	}
	
	/**
	 * This method reverses the stored motif 
	 * (depending on what is available).
	 *
	 */
	public void reverseStrings(){
		if(this.seqmotif != null){
			StringBuffer st = new StringBuffer(this.seqmotif);
			st.reverse();
			this.seqmotif = st.toString();
			updateInfo();
		}    
	}
	
	/**
	 * Setter method for the flag storing whether the stem can be interrupted by small loops
	 * 
	 * @param allowinterrupt, boolean value indicating whether the stem can be interrupted
	 */
	public void setAllowInterrupt(boolean allowinterrupt){
		this.allowinterrupt = allowinterrupt;
		updateInfo();
	}
	
	/**
	 * Getter method for the flag indicating whether the stem can be interrupted
	 * 
	 * @return true, if small loop interruptions are possible; else false
	 */
	public boolean getAllowInterrupt(){
		return this.allowinterrupt;
	}
}

