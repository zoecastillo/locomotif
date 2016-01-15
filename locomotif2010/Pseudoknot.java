package rnaeditor;

/**
 * This class defines the building block Pseudoknot
 *
 * @author: Janina Reeder
 */
public class Pseudoknot extends BuildingBlock{
	
	private static final long serialVersionUID = 2576519529398858308L;
	/**
	 * The minimum allowed size for a Pseudoknot
	 */
	public final static int MIN = 11;
	public final static int MINSTEM = 2;
	public final static int MINLOOP = 1;
	
	/**
	 * The maximum recommended size for a Pseudoknot
	 */
	public final static int MAX = 200;
	
	private int stem1len, stem1min, stem1max;
	private boolean stem1exact, stem1default;
	
	private int stem2len, stem2min, stem2max;
	private boolean stem2exact, stem2default;
	
	private int loop1len, loop1min, loop1max;
	private boolean loop1exact, loop1default, loop1straight;
	
	private int loop2len, loop2min, loop2max;
	private boolean loop2exact, loop2default, loop2straight;
	
	private int loop3len, loop3min, loop3max;
	private boolean loop3exact, loop3default, loop3straight;
	
	
	/**
	 * Constructor for the extended building block Bulge
	 *
	 * @param boolean orientation; true = standard, false = reversed
	 */
	public Pseudoknot(boolean orientation){
		super(orientation);
		this.name = new String("<u>Pseudoknot</u>");
		this.info = new String("<html>"+name+"</html>");
		this.defaultlength = 12;
		this.defaultmin = MIN;
		this.defaultmax = MAX;
		this.stem1exact = false;
		this.stem1default = true;
		this.stem2default = true;
		this.loop1default = true;
		this.loop2default = true;
		this.loop3default = true;
		this.stem2exact = false;
		this.loop2exact = false;
		this.loop3exact = false;
		this.loop1exact = false;
		this.loop1straight = true;
		this.loop2straight = true;
		this.loop3straight = true;
	}
	
	/**
	 * Responsible for updating the information string on the building block
	 * This method is called whenever changes are stored for the building block
	 */
	public void updateInfo(){
		info = "<html>";
		info += name;
		if(!stem1default){
			if(stem1exact){
			    if(orientation){
			        info += "<br>Stem1 length: ";
			    }
			    else{
			        info += "<br>Stem2 length: ";
			    }
			    info += stem1len;
			}
			else{
				if(stem1min != -1){
				    if(orientation){
				        info += "<br>Stem1 minlength: ";
				    }
				    else{
				        info += "<br>Stem2 minlength: ";
				    }
					info += stem1min;
				}
				if(stem1max != -1){
				    if(orientation){
				        info += "<br>Stem1 maxlength: ";
				    }
				    else{
				        info += "<br>Stem2 maxlength: ";
				    }
					info += stem1max;
				}
			}
			info += "<br>";
		}
		if(!stem2default){
			if(stem2exact){
			    if(orientation){
			        info += "<br>Stem2 length: ";
			    }
			    else{
			        info += "<br>Stem1 length: ";
			    }
				info += stem2len;
			}
			else{
				if(stem2min != -1){
				    if(orientation){
				        info += "<br>Stem2 minlength: ";
				    }
				    else{
				        info += "<br>Stem1 minlength: ";
				    }
					info += stem2min;
				}
				if(stem2max != -1){
				    if(orientation){
				        info += "<br>Stem2 maxlength: ";
				    }
				    else{
				        info += "<br>Stem1 maxlength: ";
				    }
					info += stem2max;
				}
			}
			info += "<br>";
		}
		if(!loop1default){
			if(loop1exact){
			    if(orientation){
			        info += "<br>Loop1 length: ";
			    }
			    else{
			        info += "<br>Loop3 length: ";
			    }
				info += loop1len;
			}
			else{
				if(loop1min != -1){
				    if(orientation){
				        info += "<br>Loop1 minlength: ";
				    }
				    else{
				        info += "<br>Loop3 minlength: ";
				    }
					info += loop1min;
				}
				if(loop1max != -1){
				    if(orientation){
				        info += "<br>Loop1 maxlength: ";
				    }
				    else{
				        info += "<br>Loop3 maxlength: ";
				    }
					info += loop1max;
				}
			}
		}
		else{
		    if(orientation){
		        info += "<br>Loop1 folding: ";
		    }
		    else{
		        info += "<br>Loop3 folding: ";
		    }
		    if(loop1straight){
		        info += "forbidden";
		    }
		    else{
		        info += "allowed";
		    }
		}
		if(!loop2default){
			if(loop2exact){
				info += "<br>Loop2 length: ";
				info += loop2len;
			}
			else{
				if(loop2min != -1){
					info += "<br>Loop2 minlength: ";
					info += loop2min;
				}
				if(loop2max != -1){
					info += "<br>Loop2 maxlength: ";
					info += loop2max;
				}
			}
		}
		else{
		    info += "<br>Loop2 folding: ";
		    if(loop2straight){
		        info += "forbidden";
		    }
		    else{
		        info += "allowed";
		    }
		}
		if(!loop3default){
			if(loop3exact){
			    if(orientation){
			        info += "<br>Loop3 length: ";
			    }
			    else{
			        info += "<br>Loop1 length: ";
			    }
				info += loop3len;
			}
			else{
				if(loop3min != -1){
				    if(orientation){
				        info += "<br>Loop3 minlength: ";
				    }
				    else{
				        info += "<br>Loop1 minlength: ";
				    }
					info += loop3min;
				}
				if(loop3max != -1){
				    if(orientation){
				        info += "<br>Loop3 maxlength: ";
				    }
				    else{
				        info += "<br>Loop1 maxlength: ";
				    }
					info += loop3max;
				}
			}
		}
		else{
		    if(orientation){
		        info += "<br>Loop3 folding: ";
		    }
		    else{
		        info += "<br>Loop1 folding: ";
		    }
		    if(loop3straight){
		        info += "forbidden";
		    }
		    else{
		        info += "allowed";
		    }
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
	 * Switches the Sides of the Pseudoknot, i.e. both Basepairs must switch sides
	 */
	public void switchSides(){
	    System.out.println("Switchin sides");
	    int bufint = this.stem1len;
	    this.stem1len = this.stem2len;
	    this.stem2len = bufint;
	    
	    bufint = this.stem1min;
	    this.stem1min = this.stem2min;
	    this.stem2min = bufint;
	    
	    bufint = this.stem1max;
	    this.stem1max = this.stem2max;
	    this.stem2max = bufint;
	    
	    boolean bufbool = this.stem1exact;
	    this.stem1exact = this.stem2exact;
	    this.stem2exact = bufbool;
	    
	    bufbool = this.stem1default;
	    this.stem1default = this.stem2default;
	    this.stem2default = bufbool;
	    

	    bufint = this.loop1len;
	    this.loop1len = this.loop3len;
	    this.loop3len = bufint;
	    
	    bufint = this.loop1min;
	    this.loop1min = this.loop3min;
	    this.loop3min = bufint;
	    
	    bufint = this.loop1max;
	    this.loop1max = this.loop3max;
	    this.loop3max = bufint;
	    
	    bufbool = this.loop1exact;
	    this.loop1exact = this.loop3exact;
	    this.loop3exact = bufbool;
	    
	    bufbool = this.loop1default;
	    this.loop1default = this.loop3default;
	    this.loop3default = bufbool;
	    
	    bufbool = this.loop1straight;
	    this.loop1straight = this.loop3straight;
	    this.loop3straight = bufbool;
	    
	    updateInfo();
	}
	
    /**
     * @return Returns the loop1default.
     */
    public boolean isLoop1default() {
        if(orientation){
            return loop1default;
        }
        else return loop3default;
    }
    /**
     * @param loop1default The loop1default to set.
     */
    public void setLoop1default() {
        if(orientation){
            this.loop1default = true;
            this.loop1exact = false;
        }
        else{
            this.loop3default = true;
            this.loop3exact = false;
        }
        updateInfo();
    }
    /**
     * @return Returns the loop1exact.
     */
    public boolean isLoop1exact() {
        if(orientation) return loop1exact;
        else return loop3exact;
    }
    /**
     * @param loop1exact The loop1exact to set.
     */
    public void setLoop1exact(boolean loop1exact) {
        if(orientation){
            this.loop1exact = loop1exact;
        }
        else{
            this.loop3exact = loop1exact;
        }
        updateInfo();
    }
    /**
     * @return Returns the loop1len.
     */
    public int getLoop1len() {
        if(orientation) return loop1len;
        else return loop3len;
    }
    /**
     * @param loop1len The loop1len to set.
     */
    public void setLoop1len(int loop1len) {
        if(orientation){
            this.loop1len = loop1len;
            this.loop1default = false;
            this.loop1straight = true;
            this.loop1exact = true;
            this.loop1min = -1;
            this.loop1max = -1;
        }
        else{
            this.loop3len = loop1len;
            this.loop3default = false;
            this.loop3straight = true;
            this.loop3exact = true;
            this.loop3min = -1;
            this.loop3max = -1;
        }
        updateInfo();
    }
    /**
     * @return Returns the loop1max.
     */
    public int getLoop1max() {
        if(orientation) return loop1max;
        else return loop3max;
    }
    
    /**
     * @param loop1max The loop1max to set.
     */
    public void setLoop1max(int loop1max) {
        if(orientation){
            this.loop1max = loop1max;
            this.loop1straight = true;
            this.loop1default = false;
            this.loop1exact = false;
            this.loop1len = -1;
        }
        else{
            this.loop3max = loop1max;
            this.loop3straight = true;
            this.loop3default = false;
            this.loop3exact = false;
            this.loop3len = -1;
        }
        updateInfo();
    }
    /**
     * @return Returns the loop1min.
     */
    public int getLoop1min() {
        if(orientation) return loop1min;
        else return loop3min;
    }
    /**
     * @param loop1min The loop1min to set.
     */
    public void setLoop1min(int loop1min) {
        if(orientation){
            this.loop1min = loop1min;
            this.loop1straight = true;
            this.loop1default = false;
            this.loop1default = false;
            this.loop1len = -1;
        }
        else{
            this.loop3min = loop1min;
            this.loop3straight = true;
            this.loop3default = false;
            this.loop3default = false;
            this.loop3len = -1;
        }
        updateInfo();
    }
    /**
     * @return Returns the loop1straight.
     */
    public boolean isLoop1straight() {
        if(orientation) return loop1straight;
        else return loop3straight;
    }
    /**
     * @param loop1straight The loop1straight to set.
     */
    public void setLoop1straight(boolean loop1straight) {
        if(orientation){
            this.loop1straight = loop1straight;
        }
        else{
            this.loop3straight = loop1straight;
        }
        updateInfo();
    }
    /**
     * @return Returns the loop2default.
     */
    public boolean isLoop2default() {
        return loop2default;
    }
    /**
     * @param loop2default The loop2default to set.
     */
    public void setLoop2default() {
        this.loop2default = true;
        this.loop2exact = false;
        updateInfo();
    }
    /**
     * @return Returns the loop2exact.
     */
    public boolean isLoop2exact() {
        return loop2exact;
    }
    /**
     * @param loop2exact The loop2exact to set.
     */
    public void setLoop2exact(boolean loop2exact) {
        this.loop2exact = loop2exact;
        updateInfo();
    }
    /**
     * @return Returns the loop2len.
     */
    public int getLoop2len() {
        return loop2len;
    }
    /**
     * @param loop2len The loop2len to set.
     */
    public void setLoop2len(int loop2len) {
        this.loop2len = loop2len;
        this.loop2exact = true;
        this.loop2straight = true;
        this.loop2default = false;
        this.loop2min = -1;
        this.loop2max = -1;
        updateInfo();
    }
    /**
     * @return Returns the loop2max.
     */
    public int getLoop2max() {
        return loop2max;
    }
    /**
     * @param loop2max The loop2max to set.
     */
    public void setLoop2max(int loop2max) {
        this.loop2max = loop2max;
        this.loop2exact = false;
        this.loop2straight = true;
        this.loop2default = false;
        this.loop2len = -1;
        updateInfo();
    }
    /**
     * @return Returns the loop2min.
     */
    public int getLoop2min() {
        return loop2min;
    }
    /**
     * @param loop2min The loop2min to set.
     */
    public void setLoop2min(int loop2min) {
        this.loop2min = loop2min;
        this.loop2exact = false;
        this.loop2straight = false;
        this.loop2default = false;
        this.loop2len = -1;
        updateInfo();
    }
    /**
     * @return Returns the loop2straight.
     */
    public boolean isLoop2straight() {
        return loop2straight;
    }
    /**
     * @param loop2straight The loop2straight to set.
     */
    public void setLoop2straight(boolean loop2straight) {
        this.loop2straight = loop2straight;
        updateInfo();
    }
    /**
     * @return Returns the loop3default.
     */
    public boolean isLoop3default() {
        if(orientation) return loop3default;
        else return loop1default;
    }
    /**
     * @param loop3default The loop3default to set.
     */
    public void setLoop3default() {
        if(orientation){
            this.loop3default = true;
            this.loop3exact = false;
        }
        else{
            this.loop1default = true;
            this.loop1exact = false;
        }
        updateInfo();
    }
    /**
     * @return Returns the loop3exact.
     */
    public boolean isLoop3exact() {
        if(orientation) return loop3exact;
        else return loop1exact;
    }
    /**
     * @param loop3exact The loop3exact to set.
     */
    public void setLoop3exact(boolean loop3exact) {
        if(orientation){
            this.loop3exact = loop3exact;
        }
        else{
            this.loop1exact = loop3exact;
        }
        updateInfo();
    }
    /**
     * @return Returns the loop3len.
     */
    public int getLoop3len() {
        if(orientation) return loop3len;
        else return loop1len;
    }
    /**
     * @param loop3len The loop3len to set.
     */
    public void setLoop3len(int loop3len) {
        if(orientation){
            this.loop3len = loop3len;
            this.loop3exact = true;
            this.loop3default = false;
            this.loop3min = -1;
            this.loop3max = -1;
            this.loop3straight = true;
        }
        else{
            this.loop1len = loop3len;
            this.loop1exact = true;
            this.loop1default = false;
            this.loop1min = -1;
            this.loop1max = -1;
            this.loop1straight = true;
        }
        updateInfo();
    }
    /**
     * @return Returns the loop3max.
     */
    public int getLoop3max() {
        if(orientation) return loop3max;
        else return loop1max;
    }
    /**
     * @param loop3max The loop3max to set.
     */
    public void setLoop3max(int loop3max) {
        if(orientation){
            this.loop3max = loop3max;
            this.loop3default = false;
            this.loop3exact = false;
            this.loop3len = -1;
            this.loop3straight = true;
        }
        else{
            this.loop1max = loop3max;
            this.loop1default = false;
            this.loop1exact = false;
            this.loop1len = -1;
            this.loop1straight = true;
        }
        updateInfo();
    }
    /**
     * @return Returns the loop3min.
     */
    public int getLoop3min() {
        if(orientation) return loop3min;
        else return loop1min;
    }
    /**
     * @param loop3min The loop3min to set.
     */
    public void setLoop3min(int loop3min) {
        if(orientation){
            this.loop3min = loop3min;
            this.loop3default = false;
            this.loop3exact = false;
            this.loop3len = -1;
            this.loop3straight = true;
        }
        else{
            this.loop1min = loop3min;
            this.loop1default = false;
            this.loop1exact = false;
            this.loop1len = -1;
            this.loop1straight = true;
        }
        updateInfo();
    }
    /**
     * @return Returns the loop3straight.
     */
    public boolean isLoop3straight() {
        if(orientation) return loop3straight;
        else return loop1straight;
    }
    /**
     * @param loop3straight The loop3straight to set.
     */
    public void setLoop3straight(boolean loop3straight) {
        if(orientation){
            this.loop3straight = loop3straight;
        }
        else{
            this.loop1straight = loop3straight;
        }
        updateInfo();
    }
    /**
     * @return Returns the stem1default.
     */
    public boolean isStem1default() {
        if(orientation) return stem1default;
        else return stem2default;
    }
    /**
     * @param stem1default The stem1default to set.
     */
    public void setStem1default(boolean stem1default) {
        if(orientation){
            this.stem1default = stem1default;
            if(stem1default){
                this.stem1len = -1;
                this.stem1min = -1;
                this.stem1max = -1;
            }
        }
        else{
            this.stem2default = stem1default;
            if(stem2default){
                this.stem2len = -1;
                this.stem2min = -1;
                this.stem2max = -1;
            }
        }
        updateInfo();
    }
    /**
     * @return Returns the stem1exact.
     */
    public boolean isStem1exact() {
        if(orientation) return stem1exact;
        else return stem2exact;
    }
    /**
     * @param stem1exact The stem1exact to set.
     */
    public void setStem1exact(boolean stem1exact) {
        if(orientation){
            this.stem1exact = stem1exact;
        }
        else{
            this.stem2exact = stem1exact;
        }
        updateInfo();
    }
    /**
     * @return Returns the stem1len.
     */
    public int getStem1len() {
        if(orientation) return stem1len;
        else return stem2len;
    }
    /**
     * @param stem1len The stem1len to set.
     */
    public void setStem1len(int stem1len) {
        if(orientation){
            this.stem1len = stem1len;
            this.stem1exact = true;
            this.stem1default = false;
            this.stem1min = -1;
            this.stem1max = -1;
        }
        else{
            this.stem2len = stem1len;
            this.stem2exact = true;
            this.stem2default = false;
            this.stem2min = -1;
            this.stem2max = -1;
        }
        updateInfo();
    }
    /**
     * @return Returns the stem1max.
     */
    public int getStem1max() {
        if(orientation) return stem1max;
        else return stem2max;
    }
    /**
     * @param stem1max The stem1max to set.
     */
    public void setStem1max(int stem1max) {
        if(orientation){
            this.stem1max = stem1max;
            this.stem1exact = false;
            this.stem1default = false;
            this.stem1len = -1;
        }
        else{
            this.stem2max = stem1max;
            this.stem2exact = false;
            this.stem2default = false;
            this.stem2len = -1;
        }
        updateInfo();
    }
    /**
     * @return Returns the stem1min.
     */
    public int getStem1min() {
        if(orientation) return stem1min;
        else return stem2min;
    }
    /**
     * @param stem1min The stem1min to set.
     */
    public void setStem1min(int stem1min) {
        if(orientation){
            this.stem1min = stem1min;
            this.stem1exact = false;
            this.stem1default = false;
            this.stem1len = -1;
        }
        else{
            this.stem2min = stem1min;
            this.stem2exact = false;
            this.stem2default = false;
            this.stem2len = -1;
        }
        updateInfo();
    }
    /**
     * @return Returns the stem2default.
     */
    public boolean isStem2default() {
        if(orientation) return stem2default;
        else return stem1default;
    }
    /**
     * @param stem2default The stem2default to set.
     */
    public void setStem2default(boolean stem2default) {
        if(orientation){
            this.stem2default = stem2default;
        }
        else{
            this.stem1default = stem2default;
        }
        updateInfo();
    }
    /**
     * @return Returns the stem2exact.
     */
    public boolean isStem2exact() {
        if(orientation) return stem2exact;
        else return stem1exact;
    }
    /**
     * @param stem2exact The stem2exact to set.
     */
    public void setStem2exact(boolean stem2exact) {
        if(orientation){
            this.stem2exact = stem2exact;
        }
        else{
            this.stem1exact = stem2exact;
        }
        updateInfo();
    }
    /**
     * @return Returns the stem2len.
     */
    public int getStem2len() {
        if(orientation) return stem2len;
        else return stem1len;
    }
    /**
     * @param stem2len The stem2len to set.
     */
    public void setStem2len(int stem2len) {
        if(orientation){
            this.stem2len = stem2len;
            this.stem2exact = true;
            this.stem2default = false;
            this.stem2min = -1;
            this.stem2max = -1;
        }
        else{
            this.stem1len = stem2len;
            this.stem1exact = true;
            this.stem1default = false;
            this.stem1min = -1;
            this.stem1max = -1;
        }
        updateInfo();
    }
    /**
     * @return Returns the stem2max.
     */
    public int getStem2max() {
        if(orientation) return stem2max;
        else return stem1max;
    }
    /**
     * @param stem2max The stem2max to set.
     */
    public void setStem2max(int stem2max) {
        if(orientation){
            this.stem2max = stem2max;
            this.stem2exact = false;
            this.stem2default = false;
            this.stem2len = -1;
        }
        else{
            this.stem1max = stem2max;
            this.stem1exact = false;
            this.stem1default = false;
            this.stem1len = -1;
        }
        updateInfo();
    }
    /**
     * @return Returns the stem2min.
     */
    public int getStem2min() {
        if(orientation) return stem2min;
        else return stem1min;
    }
    /**
     * @param stem2min The stem2min to set.
     */
    public void setStem2min(int stem2min) {
        if(orientation){
            this.stem2min = stem2min;
            this.stem2exact = false;
            this.stem2default = false;
            this.stem2len = -1;
        }
        else{
            this.stem1min = stem2min;
            this.stem1exact = false;
            this.stem1default = false;
            this.stem1len = -1;
        }
        updateInfo();
    }
}

