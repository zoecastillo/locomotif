package rnaeditor;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.JOptionPane;

/**
 * This class defines the Magnet objects which are used to determine free
 * binding places for the new elements of the RNA structure
 *
 * @author Janina Reeder
 */
public class Magnet implements Serializable{
	
	private static final long serialVersionUID = 2562079168746098176L;
	private Line2D.Double line;
	private int theta;
	private RnaShape parent;
	private AffineTransform at;
	private boolean isfull;
	private boolean ishairpinaccessible;
	private boolean careforambiguity = true;
	private boolean ambiguitychecked = false;
	private boolean closedaccessible;
	
	/**
	 * Constructor for a Magnet
	 *
	 * @param line the area of the binding site
	 * @param parent the parent of the Magnet
	 * @param theta the angle of the Magnet with respect to the x-axis
	 * @param iha, boolean value describing the hairpin accessibility
	 */
	public Magnet(Line2D.Double line, RnaShape parent, int theta, boolean iha){
		this.line = line;
		this.theta = theta;
		this.parent = parent;
		this.at = new AffineTransform();
		this.isfull = true;
		this.ishairpinaccessible = iha;
		this.closedaccessible = true;
	}
	
	/**
	 * Constructor for a Magnet
	 *
	 * @param line the area of the binding site
	 * @param parent the parent of the Magnet
	 * @param theta the angle of the Magnet with respect to the x-axis
	 * @param iha, boolean value describing the hairpin accessibility
	 * @param isfull determines whether it is a full magnet or only single strand magnet
	 */
	public Magnet(Line2D.Double line, RnaShape parent, int theta, boolean iha, boolean isfull){
		this.line = line;
		this.theta = theta;
		this.parent = parent;
		this.at = new AffineTransform();
		this.isfull = isfull;
		this.ishairpinaccessible = (iha && isfull);
		this.closedaccessible = isfull;
	}
	
	/**
	 * This method checks for equality of two magnets based on their angles and their parents.
	 * 
	 * @param cmp, the Magnet to compare this one to
	 * @return true, if angle and parent are equal; else false.
	 */
	public boolean equals(Magnet cmp){
		if(this.theta == cmp.getAngle() && this.parent == cmp.getParent()){
			return true;
		}
		else return false;
	}
	
	/**
	 * The methods checks for equality based on angles, parents and the line
	 * 
	 * @param cmp, the Magnet to compare this one to
	 * @return true, if angle, parent and line are equals; else false.
	 */
	public boolean equalsWithLine(Magnet cmp){
		if(this.equals(cmp) && this.line.equals(cmp.getLine())){
			return true;
		}
		else return false;
	}
	
	/**
	 * Creates a clone of this Magnet which is returned
	 * 
	 * @return a clone of this Magnet
	 */
	public Magnet clone(){
		return new Magnet((Line2D.Double)this.line.clone(),parent, theta, ishairpinaccessible, isfull);
	}
	
	
	/**
	 * Returns the first half of the magnet
	 * 
	 * @return the first half Magnet
	 */ 
	public Magnet getFirstHalf(){
		Line2D.Double firstline = new Line2D.Double(this.line.getX1(),this.line.getY1(),this.line.getX1()+((this.line.getX2()-this.line.getX1())/2),this.line.getY1()+((this.line.getY2()-this.line.getY1())/2));
		Magnet firsthalf = new Magnet(firstline, this.parent, this.theta, false, false);
		return firsthalf;
	}
	
	/**
	 * Returns the second half of the magnet
	 * 
	 * @return the second half Magnet
	 */
	public Magnet getSecondHalf(){
		Line2D.Double secondline = new Line2D.Double(this.line.getX1()+((this.line.getX2()-this.line.getX1())/2),this.line.getY1()+((this.line.getY2()-this.line.getY1())/2),this.line.getX2(),this.line.getY2());
		Magnet secondhalf = new Magnet(secondline, this.parent, this.theta, false, false);
		return secondhalf;
	}
	
	/**
	 * Checks wether this Magnet contains the given Magnet
	 * 
	 * @param m, the Magnet to check for
	 * @return true, if it contains m; else false.
	 */
	public boolean contains(Magnet m){
		if(this.theta == m.getAngle() && this.parent.equals(m.getParent())){
			Point2D p1 = (m.getLine()).getP1();
			Point2D p2 = (m.getLine()).getP2();
			if(this.line.ptSegDist(p1) == 0.0 && this.line.ptSegDist(p2) == 0.0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks wether this Magnet contains the given Point
	 * 
	 * @param p, the Point2D.Double to check for
	 * @return true, if it contains p, else false.
	 */
	public boolean contains(Point2D.Double p){
		Point2D p1 = line.getP1();
		Point2D p2 = line.getP2();
		return (p.distance(p1) < 1 || p.distance(p2) < 1);
	}
	
	
	/**
	 * Checks wether this Magnet contains the given Vector
	 * 
	 * @param v, the Vector<Magnet> to check (first and last element)
	 * @return true, if it contains either element; else false.
	 */
	public boolean contains(java.util.Vector<Magnet> v){
		Magnet m1 = v.firstElement();
		Magnet m2 = v.lastElement();
		Point2D p1 = (m1.getLine()).getP1();
		Point2D p2 = (m1.getLine()).getP2();
		Point2D p3 = (m2.getLine()).getP1();
		Point2D p4 = (m2.getLine()).getP2();
		if((this.line.ptSegDist(p1) == 0.0 && this.line.ptSegDist(p2) == 0.0) || (this.line.ptSegDist(p3) == 0.0 && this.line.ptSegDist(p4) == 0.0)){
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the rest of the intersection of this Magnet and the given one
	 * 
	 * @param m, the Magnet to check for (a half Magnet)
	 * @return the rest of the intersection of m with this one.
	 */
	public Magnet getRest(Magnet m){
		Point2D p1;
		Point2D p2;
		if((this.line.getP1()).equals((m.getLine()).getP1())){
			p1 = (m.getLine()).getP2();
			p2 = this.line.getP2();
		}
		else{
			p1 = this.line.getP1();
			p2 = (m.getLine()).getP1();
		}
		Magnet retm = new Magnet(new Line2D.Double(p1,p2),this.parent,this.theta, false);
		retm.setIsFull(false);
		return retm;
	}
	
	/**
	 * Return the half of this magnet that contains the given 
	 * Vector v (either its first or last element).
	 * 
	 * @param v, the Vector<Magnet> to check
	 * @return the half of this Vector that contains v.
	 */
	public Magnet getRest(java.util.Vector<Magnet> v){
		Magnet firsthalf = this.getFirstHalf();
		Magnet secondhalf = this.getSecondHalf();
		
		if(firsthalf.contains(v)){
			firsthalf.setIsFull(false);
			return firsthalf;
		}
		else{
			secondhalf.setIsFull(false);
			return secondhalf;
		}
	}
	
	
	
	
	
	/**
	 * This method gives the Line2D.Double attribute of this Magnet
	 * 
	 * @return Line2D.Double, the area of the binding site
	 */
	public Line2D.Double getLine(){
		return this.line;
	}
	
	/** 
	 * This method gives the angle of this Magnet.
	 * 
	 * @return int, the angle of the binding site with respect to the x-axis
	 */
	public int getAngle(){
		return this.theta;
	}
	
	/**
	 * Setter method for the angle theta
	 * 
	 * @param theta, the new value of the angle of this Magnet
	 */
	public void setAngle(int theta){
		this.theta = theta;
	}
	
	/**
	 * This method gives the Parent RnaShape of this Magnet
	 * 
	 * @return RnaShape, the parent of the binding site
	 */
	public RnaShape getParent(){
		return this.parent;
	}
	
	/**
	 * Checks wether this is a full magnet
	 * 
	 * @return true, if this is a full Magnet; else false.
	 */
	public boolean getIsFull(){
		return this.isfull;
	}
	
	/**
	 * Setter method for the flag isfull (magnet).
	 * 
	 * @param isfull
	 */ 
	public void setIsFull(boolean isfull){
		this.isfull = isfull;
	}
	
	/**
	 * Checks wether this magnet is Hairpin Accessible
	 * 
	 * @return true, if the Magnet is available for docking with a hairpin, i.e.
	 * it does not lead to a closed RnaStructure.
	 */
	public boolean getIsHairpinAccessible(){
		return this.ishairpinaccessible;
	}
	
	/**
	 * Checks whether a compound building block can be added to this magnet. It checks for the presence of another compound building block in the same helix which would cause ambiguity issues.
	 * @return true, if accessible; else false
	 */
	public boolean getIsClosedAccessible(){
		if(!ambiguitychecked && careforambiguity && parent.traverseToClosed(this.theta)){
			int retval = JOptionPane.showConfirmDialog(null,"The combination of two compound building blocks within one branch\nof the structure can cause ambiguity problems.\nThe same solution might be returned several times.\n Do you wish to ignore ambiguity issues for this part of the motif?","Ambiguous motif defintion",JOptionPane.YES_NO_OPTION);
			ambiguitychecked = true;
			if(retval == JOptionPane.YES_OPTION){
				careforambiguity = false;
				closedaccessible = true;
				return true;
			}
			else{
				closedaccessible = false;
				return false;
			}
		}
		return closedaccessible;
	}
	
	/**
	 * Resets the variables for ambiguity checking to false as in not checked and true as in ambiguity is relevant.
	 *
	 */
	public void resetAmbiguityCheck(){
		ambiguitychecked = false;
		careforambiguity = true;
	}
	
	/**
	 * Setter Method for Hairpin Accessability
	 * 
	 * @param ishairpinaccessible
	 */ 
	public void setIsHairpinAccessible(boolean ishairpinaccessible){
		this.ishairpinaccessible = ishairpinaccessible;
	}
	
	/**
	 * Storage method for this magnet
	 * 
	 * @param s, the ObjectOutputStream
	 */
	private void writeObject(ObjectOutputStream s) throws IOException{
		s.writeDouble(line.getX1());
		s.writeDouble(line.getY1());
		s.writeDouble(line.getX2());
		s.writeDouble(line.getY2());
		s.writeInt(theta);
		s.writeObject(parent);
		s.writeObject(at);
		s.writeBoolean(isfull);
	}
	
	
	/**
	 * Storage method for this Magnet
	 * 
	 * @param s, the ObjectInputStream
	 */
	private void readObject(ObjectInputStream s) throws IOException,ClassNotFoundException{
		line = new Line2D.Double(s.readDouble(),s.readDouble(),s.readDouble(),s.readDouble());
		theta = s.readInt();
		parent = (RnaShape) s.readObject();
		at = (AffineTransform) s.readObject();
		isfull = s.readBoolean();
	}
	
	
	/**
	 * Transforms this Magnet by the given AffineTransform
	 * 
	 * @param at, the AffineTransform to operate on this Magnet
	 */
	public void transform(AffineTransform at){
		Point2D p1 = line.getP1();
		Point2D p2 = line.getP2();
		at.transform(p1,p1);
		at.transform(p2,p2);
		line = new Line2D.Double(p1,p2);
	}
	
}



