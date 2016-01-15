package rnaeditor;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * This class is essential to store any internal rotations of a building block
 *
 * @author Janina Reeder
 */
public class Rotation implements Serializable{
	
	private static final long serialVersionUID = -6461418550474932107L;
	private double rotangle;
	private int type;
	private double xmiddle;
	private double ymiddle;
	
	/**
	 * Constructor
	 *
	 * @param double angle, the rotation degree
	 * @param int type, the type of the rotation (INCLUDE EXPLANATION)
	 */
	public Rotation(double angle, int type){
		this.rotangle = angle;
		this.type = type;
	}
	
	/**
	 * Constructor
	 *
	 * @param double angle, the rotation degree
	 * @param double xmiddle, the x-coordinate of the rotation center
	 * @param double ymiddle, the y-coordinate of the rotation center
	 */
	public Rotation(double angle, double xmiddle, double ymiddle){
		this.rotangle = angle;
		this.type = 4;
		this.xmiddle = xmiddle;
		this.ymiddle = ymiddle;
	}
	
	/**
	 * Getter Method for the x-coordinate of the rotation center
	 *
	 * @return double xmiddle
	 */
	public double getXMiddle(){
		return this.xmiddle;
	}
	
	/**
	 * Getter Method for the y-coordinate of the rotation center
	 *
	 * @return double ymiddle
	 */
	public double getYMiddle(){
		return this.ymiddle;
	}
	
	/**
	 * Setter Method for the rotation center
	 *
	 * @param Point2D middle, the new center
	 */
	public void setNewMiddle(Point2D middle){
		this.xmiddle = middle.getX();
		this.ymiddle = middle.getY();
	}
	
	/**
	 * Getter method for the rotation angle
	 * 
	 * @return double rotangle
	 */
	public double getAngle(){
		return this.rotangle;
	}
	
	/**
	 * Getter method for the rotation type
	 * 
	 * @return double type
	 */
	public int getType(){
		return this.type;
	}
	
}
