package rnaeditor;

import java.awt.geom.Area;
import java.io.Serializable;
import java.util.Vector;

/**
 * This class implements a DoubleSingleShape which stores two SingleShapes.
 * A DoubleSingleShape is actually not a visual object, but just serves as 
 * a storage for two Single Shapes which are shown graphically.
 *
 * @author Janina Reeder
 */
public class DoubleSingleShape extends RnaShape implements Serializable {
	
	private static final long serialVersionUID = 7783410659227508633L;
	private SingleShape fiveprimess;
	private SingleShape threeprimess;
	private boolean switched;
	
	
	/**
	 * Constructor for a double single shape. No coordinates are needed here,
	 * as this object is never shown!
	 * 
	 */
	public DoubleSingleShape(){
		this.fiveprimess = null;
		this.threeprimess = null;
		switched = false;
	}
	
	/**
	 * Constructor for a double single shape
	 * 
	 * @param fiveprimess, the 5' single shape
	 * @param threeprimess, the 3' single shape
	 */
	public DoubleSingleShape(SingleShape fiveprimess,SingleShape threeprimess){
		this.fiveprimess = fiveprimess;
		this.threeprimess = threeprimess;
		switched = false;
	}
	
	/**
	 * Getter method for the type of this building block
	 * 
	 * @return EditorGui.SS_DOUBLE
	 */
	public int getType(){
		return EditorGui.SS_DOUBLE;
	}
	
	/**
	 * This methods stores the given fiveprimess. If a fiveprimess is already
	 * contained in the double single shape, the old one will be stored as
	 * the threeprimess.
	 *
	 * @param fiveprimess, the fiveprimess to be stored
	 */
	public void addFivePrime(SingleShape fiveprimess){
		SingleShape fivebuf = this.fiveprimess;
		this.fiveprimess = fiveprimess;
		if(fivebuf != null){ //swap is necessary
			this.threeprimess = fivebuf;
			this.threeprimess.propagateFivePrimeSwitch(this);
		}
	}
	
	/**
	 * This methods stores the given threeprimess. If a threeprimess is already
	 * contained in the double single shape, the old one will be stored as
	 * the fiveprimess.
	 *
	 * @param threeprimess, the threeprimess to be stored
	 */
	public void addThreePrime(SingleShape threeprimess){
		SingleShape threebuf = this.threeprimess;
		this.threeprimess = threeprimess;
		if(threebuf != null){ //swap is necessary
			this.fiveprimess = threebuf;
			this.fiveprimess.propagateThreePrimeSwitch(this);
		}
	}
	
	/**
	 * This method switches five- and threeprimess.
	 *
	 *	@param origin, the strand demanding the swith
	 *	@return the other strand
	 */
	public SingleShape switchStrands(SingleShape origin){
		SingleShape buf = this.fiveprimess;
		this.fiveprimess = this.threeprimess;
		this.threeprimess = buf;
		if(origin == this.fiveprimess){
			return this.threeprimess;
		}
		else{
			return this.fiveprimess;
		}
	}
	
	/**
	 * Checks wether the stored fiveprimess is null
	 *
	 * @return boolean true, if null, else false
	 */
	public boolean getOpenFivePrime(){
		if(this.fiveprimess == null){
			return true;
		}
		return false;
	}
	
	/**
	 * Checks wether the stored threeprimess is null
	 *
	 * @return boolean true, if null, else false
	 */ 
	public boolean getOpenThreePrime(){
		if(this.threeprimess == null){
			return true;
		}
		return false;
	}
	
	
	/**
	 * Checks wether any of the two single strands is null
	 *
	 * @return boolean true, if one is null, else false
	 */
	public boolean getOpenPrime(){
		if(this.fiveprimess == null || this.threeprimess == null){
			return true;
		}
		return false;
	}
	
	/**
	 * Checks wether the DSS contains the given RnaShape
	 *
	 * @param s, the RnaShape
	 * @return boolean true, if the DSS contains s, else false
	 */
	public boolean contains(RnaShape s){
		if(((SingleShape)s).equals(fiveprimess) || ((SingleShape)s).equals(threeprimess)){
			return true;
		}
		return false;
	}
	
	/**
	 * Removes the given RnaShape from the DSS
	 *
	 * @param s, the RnaShape to be removed
	 */
	public void remove(RnaShape s){
		if(((SingleShape)s).equals(fiveprimess)){
			this.fiveprimess = null;
		}
		else if(((SingleShape)s).equals(threeprimess)){
			this.threeprimess = null;
		}
	}
	
	/**
	 * Checks wether the DSS is empty, i.e. if both five- and threeprimess are null
	 * 
	 * @return true, if both strands are null; else false
	 */
	public boolean isEmpty(){
		if(this.fiveprimess == null && this.threeprimess == null){
			return true;
		}
		return false;
	}
	
	/**
	 * Not needed for DSS
	 */
	public void setSwitchForced(boolean switchforced){
	}
	
	
	/**
	 * changes the orientation of the shape
	 * 
	 * @param seqreversal describing whether stored sequences should be reversed
	 */
	public void changeOrientation(boolean seqreversal){
		SingleShape buf = this.fiveprimess;
		this.fiveprimess = this.threeprimess;
		this.threeprimess = buf;
	}
	
	
	
	
	/**
	 * Starting method for a traversal of the current structure which can
	 * have several open ends.
	 *
	 * SHOULD NOT BE CALLED FOR DSS!
	 *
	 * @return String, the current shape notation of the structure
	 */
	public String currentForwardTraversal(){
		return "ERROR: currentForwardTraversal attempted on DSS";
	}
	
	
	
	/**
	 *The subsequent method during a traversal.
	 * The return String is complemented here and subsequent methods are called
	 *
	 * SHOULD NOT BE CALLED FOR DSS!
	 *
	 * @param angle the angle where the previous shape came from
	 */
	protected String ctraverseF(int angle){
		return "ERROR: ctraverseF attempted on DSS";
	}
	
	
	/**
	 * The starting method for traversed an rna structure. 
	 * The return String is initialized here and subsequent methods are called.
	 *
	 * SHOULD NOT BE CALLED FOR DSS!
	 * @return String containing the shape notation for the whole structure
	 */
	public String beginTraversal(int type){
		return "ERROR: beginTraversal attempted on DSS";
	}
	
	/**
	 * The subsequent method during a traversal.
	 * The return String is complemented here and subsequent methods are called
	 *
	 * SHOULD NOT BE CALLED FOR DSS!
	 * @param angle the angle where the previous shape came from
	 */
	protected String traverse(int angle, int type){
		return "ERROR: traverse attempted on DSS";
	}
	
	
	/**
	 * Getter method for the threeprimess
	 *
	 * @return SingleShape threeprimess
	 */
	public SingleShape getThreePrime(){
		return this.threeprimess;
	}
	
	/**
	 * Getter method for the fiveprimess
	 *
	 * @return SingleShape fiveprimess
	 */
	public SingleShape getFivePrime(){
		return this.fiveprimess;
	}
	
	/**
	 * Getter method for the Area of the DSS
	 *
	 * @return Area
	 */
	public java.awt.geom.Area getArea(){
		Area area1 = null;
		if(this.fiveprimess != null){
			area1 = this.fiveprimess.getArea();
			if(this.threeprimess != null){
				area1.add(threeprimess.getArea());
			}
		}
		else if(this.threeprimess != null){
			area1 = this.threeprimess.getArea();
		}
		return area1;
	}
	
	
	/**
	 * SHOULD NOT BE CALLED FOR DSS!
	 * @return int, the x-coordinate of the upper left corner of the 
	 * bounding box
	 */
	public int getX(){
		return -1;
	}
	
	/**
	 * SHOULD NOT BE CALLED FOR DSS!
	 * @return int, the y-coordinate of the upper left corner of the 
	 * bounding box
	 */
	public int getY(){
		return -1;
	}
	
	/**
	 * This method changes the size of the Shape, i.e. changes its Area
	 *
	 * not needed for DSS!
	 *
	 * @param seglength, the sequence length upon which the size depends
	 */
	public void changeSize(int seqlength){}
	
	/**
	 * This method changes the size of the Shape, i.e. changes its Area
	 * according to a median value for the sequence length.
	 *
	 * not needed for DSS!
	 * @param minlength, the minimum sequence length
	 * @param maxlength, the maximum sequence length
	 */
	public void changeSize(int minlength, int maxlength){}
	
	/**
	 * This method implements a change in location to a new point of origin
	 *
	 * not needed for DSS!
	 * @param x, the x-coordinate of the new origin
	 * @param y, the y-coordinate of the new origin
	 */
	public void changeLocation(double x, double y){}
	
	/**
	 * This method implements a change in location to a new point of origin
	 *
	 * not needed for DSS!
	 * @param x, the x-coordinate of the new origin
	 * @param y, the y-coordinate of the new origin
	 */
	public void changeLocation(java.awt.geom.AffineTransform movetransform){}
	
	/**
	 * This method implements a snapping mechanism which takes place if 
	 * the shape is close to a free Magnet
	 *
	 * not needed for DSS!
	 * @param m, the Magnet to which the shape snaps
	 */
	public void snapTo(Magnet m){}
	
	/**
	 * not needed for DSS!
	 * restores the affine transformation to identity, 
	 * i.e. all rotations are undone
	 */
	public void restore(){}
	
	
	
	/**
	 * not needed for DSS!
	 * @return Rectangle2D, the bounding box of this shape's area
	 */
	public java.awt.geom.Rectangle2D getBounds2D(){return null;}
	
	/**
	 * not needed for DSS!
	 * This method returns all exits of the shape as a Magnet Vector
	 * 
	 * @return Vector containing all Magnets of this shape
	 */
	public java.util.Vector<Magnet> getBorders(){return null;}
	
	/**
	 * not needed for DSS!
	 * This method returns all free Magnets of this shape, i.e. not m
	 *
	 * @param m, the Magnet that is not to be returned in the Vector
	 * @return Vector containing all free Magnets of this shape
	 */
	public java.util.Vector<Magnet> getBorders(Magnet m){return null;}
	
	/**
	 * not needed for DSS!
	 * adds a neighboring shape to this one
	 * 
	 * @param shape, the shape that is added as a neighbor
	 * @param angle, the angle of the exit, where the shape is neighboring
	 */
	public void addToNeighbors(RnaShape shape,int angle,int type){
	}
	
	/**
	 * This method adds a SingleStrand neighbor to this bulge element
	 *
	 * not needed for DSS!
	 * @param SingleShape shape, the shape to be added
	 * @param int angle, the angle where the shape is added
	 * @return int returns 1 if the singleshape was added as fiveprime or 
	 * 2 if it was added as threeprime
	 */
	public int addSSNeighbor(SingleShape shape, int angle){
		return -1;
	}
	
	
	/**
	 * not needed for DSS!
	 * @return index of the end or -1 if not found
	 */
	public int getEndType(){return -1;}
	
	
	/**
	 * Method for finding an open end of the structure part:
	 * SHOULD NOT BE CALLED FOR DSS
	 */
	public boolean findOpenEnd(int angle){
		System.out.println("find open end for DSS should not occur!!!");
		return false;
	}
	
	/**
	 * Method for traversing to a compound block
	 * 
	 * @param angle describing the direction of the traversal
	 * @return false, since any compound block is separated by this dss
	 */
	public boolean traverseToClosed(int angle){
		return false;
	}
	
	/**
	 * 
	 * not needed for DSS!
	 */
	public boolean hasDSSNeighbor(int index){
		return false;
	}
	
	/**
	 * 
	 * not needed for DSS!
	 */
	public boolean findSS(int angle){
		return true;
	}
	
	/**
	 * Method for a traversal and shift operation. 
	 * The RnaShape is adjusted according to a size change of another
	 * element in the RnaStructure
	 *
	 * not needed for DSS!
	 * @param angle, the direction the shift is going
	 */
	public void traverseShift(int angle){}
	
	/**
	 * not needed for dss
	 */
	public void traverseShift(java.awt.geom.AffineTransform movetransform, int angle){}
	
	/**
	 * not needed for dss
	 */
	public void hairpinAccessTraversal(int angle){
		
	}
	
	/**
	 * not needed for dss
	 */
	public void hairpinAccessTraversal(int angle, Vector<Magnet> openmagnets){
		
	}
	
	
	/**
	 * method to check wether a given Point2D is contained within the
	 * RnaShape
	 *
	 * not needed for DSS!
	 * @param p the Point2D object to look for
	 * @return true, if p is contained within this RnaShape; else false.
	 */
	public boolean contains(java.awt.geom.Point2D p, double zoomfactor){return false;}

	/**
	 * This method returns the Magnet which belongs to the 5' end
	 *
	 * @return Magnet, the Magnet which belongs to the 5' end
	 */
	public Magnet getEndExit(){
		return this.threeprimess.getEndExit();
	}

	/**
	 * This method returns the Magnet which belongs to the 5' end
	 *
	 * @return Magnet, the Magnet which belongs to the 5' end
	 */
	public Magnet getStartExit(){
		return this.fiveprimess.getStartExit();
	}
	
	/**
	 * sets this RnaShape to be selected/unselected
	 * 
	 * not needed for DSS!
	 * @param selected flag for the selection of this RnaShape
	 */
	public void setSelected(boolean selected){}

	/**
	 * 
	 * not needed for DSS!
	 */
	public void changeLocation(double x, double y, boolean bottom){}
	
	/**
	 * Method that opens a new Edit Window for this RnaShape
	 *
	 * not needed for DSS!
	 * @param ds, the parent DrawingSurface
	 * @param type defines which tab is shown first (0=standard, 1=size, 2=sequence, 3=loc in bulge, 4=loop53 in internal, 5=loop35 in internal, 6=exits in multi)
	 */
	public void openEditWindow(DrawingSurface ds, int type){}
	
	/**
	 * Getter method for this RnaShape's AffineTransform
	 *
	 * not needed for DSS!
	 * @return the AffineTransform of this RnaShape
	 */
	public java.awt.geom.AffineTransform getTransform(){return null;}
	
	/**
	 * Setter method for the flag isstartelement which indicates
	 * wether this element is the main open end of the structure.
	 *
	 * @param boolean isstartelement, true means this is the main open end
	 * @param int angle, the angle of the open end
	 */
	public void setIsStartElement(boolean isstartelement, int angle, int direct){
		if(this.fiveprimess != null){
			this.fiveprimess.setIsStartElement(isstartelement, angle, direct);
		}
		else{
			this.threeprimess.setIsStartElement(isstartelement, (angle+180)%360, direct);
		}
	}
	
	/**
	 * Setter method for the flag isendelement which indicates
	 * wether this element is the main open end of the structure.
	 *
	 * @param boolean isendelement, true means this is the main open end
	 * @param int angle, the angle of the open end
	 */
	public void setIsEndElement(boolean isendelement, int angle, int direct){
		if(this.threeprimess != null){
			this.threeprimess.setIsEndElement(isendelement,angle, direct);
		}
		else{
			this.fiveprimess.setIsEndElement(isendelement, (angle+180)%360, direct);
		}
	}
	
	/**
	 * Sets the flag switched to false
	 */
	public void resetSwitches(){
		this.switched = false;
	}
	
	
	/**
	 * Getter method for the flag isstartelement
	 *
	 * @return true, if this is the start element, else false
	 */
	public boolean getIsStartElement(){
		if(this.fiveprimess.getIsStartElement() || this.threeprimess.getIsStartElement()){
			return true;
		}
		return false;
	}
	
	/**
	 * Getter method for the flag isendelement
	 *
	 * @return true, if this is the end element, else false
	 */
	public boolean getIsEndElement(){
		if(this.fiveprimess.getIsEndElement() || this.threeprimess.getIsEndElement()){
			return true;
		}
		return false;
	}
	
	/**
	 * Getter method for the angle of the main open end
	 *
	 * @return -1, if not start element, else the angle (=magnet)
	 */
	public int getStartAngle(){
		if(this.fiveprimess.getIsStartElement()){
			return this.fiveprimess.getStartAngle();
		}
		else if(this.threeprimess.getIsStartElement()){
			return this.threeprimess.getStartAngle();
		}
		return -1;
	}   
	
	/**
	 * Getter method for the angle of the main open end
	 *
	 * @return -1, if not end element, else the angle (=magnet)
	 */
	public int getEndAngle(){
		if(this.threeprimess.getIsEndElement()){
			return this.threeprimess.getEndAngle();
		}
		else if(this.fiveprimess.getIsEndElement()){
			return this.fiveprimess.getEndAngle();
		}
		return -1;
	}
	
	/**
	 * This method recursively performs a Traversal during which the sides are
	 * switched.
	 * @param start, the RnaShape that initiated the traversal
	 * @param fiveprimedir, boolean value indicating wether the traversal proceeds
	 * in the 5' to 3' direction.
	 * @param angle, indicates at which exit the traversal must continue.
	 */
	public void switchSidesTraversal(RnaShape start, boolean fiveprimedir, int angle){
		if(fiveprimedir && this.threeprimess != null){
			this.threeprimess.switchSidesTraversal(null, fiveprimedir, -1);
		}
		else if(!fiveprimedir && this.fiveprimess != null){
			this.fiveprimess.switchSidesTraversal(null, fiveprimedir, -1);
		}
	}
	
	
	/**
	 * Traversal method which proceeds until a new open end is found.
	 * Called if the current main open end is closed by a hairpin
	 *
	 * @param int angle, the direction of the traversal
	 * @return RnaShape this or result of a recursive call
	 */
	public RnaShape traverseToNewStart(int angle, boolean real){
		SingleShape buf = this.fiveprimess;
		this.fiveprimess = this.threeprimess;
		this.threeprimess = buf;
		switched = true;
		return this.fiveprimess.traverseToNewStart(angle, real);
	}
	
	/**
	 * Traversal method which proceeds until a new open end is found.
	 * Called if the current main open end is closed by a hairpin
	 *
	 * @param int angle, the direction of the traversal
	 * @return RnaShape this or result of a recursive call
	 */
	public RnaShape traverseToNewEnd(int angle){
		if(!switched){
			SingleShape buf = this.fiveprimess;
			this.fiveprimess = this.threeprimess;
			this.threeprimess = buf;
			switched = true;
		}
		return this.threeprimess.traverseToNewEnd(angle);
	}
	
	
	/**
	 * Rotation method which rotates the structure around its center
	 *
	 * not needed for DSS!
	 * @param int degree, the degree of the rotation
	 */
	public void rotateBy(int degree){}
	
	/**
	 * Rotation method which rotates the structure around a given point
	 *
	 * not needed for DSS!
	 * @param int degree, the degree of the rotation
	 * @param double xmiddle, the x location of the rotation point
	 * @param double ymiddle, the y location of the rotation point
	 */
	public void rotateBy(int degree, double xmiddle, double ymiddle){}
	
	/**
	 * This method returns the new 5' end, if the old one was occupied
	 * by attaching a new building block
	 *
	 * @param int index, the exit where we came from
	 * @return RnaShape the new start element
	 */
	public RnaShape findNewStartElement(int index){
		return this.fiveprimess.findNewStartElement(index);
	}
	
	/**
	 * This method returns the new 5' end, if the old one was occupied
	 * by attaching a new building block
	 *
	 * @param int index, the exit where we came from
	 * @return RnaShape the new end element
	 */
	public RnaShape findNewEndElement(int index){
		return this.threeprimess.findNewEndElement(index);
	}
	
	/**
	 * This method checks wether the DSS is connected to the given shape at the given angle
	 *
	 * @param shape, the shape
	 * @param angle, the location
	 */
	public boolean connectedTo(RnaShape shape, int angle, RnaShape origin){
		boolean found0 = false;
		boolean found1 = false;
		if(this.fiveprimess != null){
			if(this.fiveprimess == shape){
				return true;
			}
			found0 = this.fiveprimess.connectedTo(shape, angle, origin);
		}
		if(this.threeprimess != null && !found0){
			if(this.threeprimess == shape){
				return true;
			}
			found1 = this.threeprimess.connectedTo(shape, angle, origin);
		}
		return (found0 || found1);
	}
	
	/** 
	 * This method removes all neighbors of an RnaShape
	 * not needed for DSS!
	 */
	public void removeNeighbors(){}
	
	/**
	 * 
	 * not needed for DSS!
	 */
	public void switchsides(){}

	public void setMotifHead(){
		this.motifheadarea = null;
	}
	
	/**
	 * This method does the actual drawing of this shape's Area
	 *
	 * not needed for DSS!
	 * @param g2, the Graphics2D object that performs the drawing
	 * @param fixed, boolean value describing wether the shape is part of 
	 * the current RnaStructure or not
	 */
	public void show(java.awt.Graphics2D g2,boolean fixed){}
	
}

