package rnaeditor;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

/**
 * This class defines the already designed RNA structure, i.e. the class
 * that holds all RnaShape elements together
 * 
 * @author Janina Reeder
 */
public class RnaStructure implements Serializable{
	
	private static final long serialVersionUID = -5032902562209108817L;
	private Vector<RnaShape> structure;
	private Area area;
	private double xmove;
	private double ymove;
	private AffineTransform movetransform;
	private int globalmin,globalmax;
	
	/**
	 * Constructor for a new RnaStructure. Both Vectors are initialized. One contains the RnaShapes, the other contains the combined Areas.
	 */
	public RnaStructure(){
		structure = new Vector<RnaShape>();
		area = new Area();
		xmove = 0;
		ymove = 0;
		globalmin = 0;
		globalmax = 0;
		movetransform = new AffineTransform();
	}
	
	
	/**
	 * Storing the global size of the overall structure
	 * 
	 * @param minsize, global minimum
	 * @param maxsize, global maximum
	 */
	public void storeGlobalSize(int minsize,int maxsize){
		this.globalmin = minsize;
		this.globalmax = maxsize;
	}
	
	/**
	 * Obtaining the global minimum size
	 * @return, global minimum
	 */
	public int getGlobalMin(){
		return this.globalmin;
	}
	
	/**
	 * Getter for the global maximum size
	 * @return, the global maximum
	 */
	public int getGlobalMax(){
		return this.globalmax;
	}
	
	/**
	 * This method checks wether the RnaStructure contains any elements
	 *
	 * @return true, if empty; else false
	 */
	public boolean isEmpty(){
		return structure.isEmpty();
	}
	
	/**
	 * adds an RnaShape to this structure. The shape is stored in the 
	 * structure Vector, its Area in the area Vector.
	 * 
	 * @param shape, the RnaShape which is added
	 */
	public void addElement(RnaShape shape){
		structure.add(shape);
		area.add(shape.getArea());
	}
	
	/**
	 * This method removes a specific RnaShape from the RnaStructure
	 *
	 * @param shape, the RnaShape to be removed
	 */
	public void remove(RnaShape shape){
		area.subtract(shape.getArea());
		structure.remove(shape);
	}
	
	/**
	 * This method is called when the RnaStructure is to be drawn. It calls
	 * the show methods of each shape stored in it.
	 *
	 * @param g2, the Graphics2D object that performs the drawing
	 */
	public void drawStructure(Graphics2D g2, boolean fixed){
		this.area = new Area();
		for(RnaShape shape : structure){
			this.area.add(shape.getArea());
			shape.show(g2,fixed);
		}
	}
	
	/**
	 * Checks wether the RnaStructure contains a single stranded region
	 *
	 * @return boolean, true or false.
	 */
	public boolean containsSS(){
		for(RnaShape shape : structure){
			if(shape.getType() == EditorGui.SINGLE_TYPE){
				return true;
			}
		}
		return false;
	}

	
	/**
	 * Checks wether the RnaStructure contains a pseudoknot
	 *
	 * @return boolean, true or false.
	 */
	public boolean containsPseudo(){
		for(RnaShape shape : structure){
			if(shape.getType() == EditorGui.PSEUDO_TYPE){
				return true;
			}
		}
		return false;
	}
	
	
	
	/**
	 * This method adjusts the RnaStructure and all its elements to a given
	 * rotation angle
	 *
	 * @param int degree, the rotation angle
	 */
	public void adjust(int degree){
		double xmiddle = getXMiddle();
		double ymiddle = getYMiddle();
		for(RnaShape shape : structure){
			shape.rotateBy(degree,xmiddle,ymiddle);
		}
		java.awt.geom.AffineTransform rotarea = new AffineTransform();
		rotarea.rotate(StrictMath.toRadians(degree),xmiddle,ymiddle);
		area = new Area(rotarea.createTransformedShape(area));
	}
	
	
	/**
	 * This method recalculates the x-middle point of the RnaStructure
	 *
	 * @return double, the x middle point of the structure
	 */
	private double getXMiddle(){
		this.area = new Area();
		for(RnaShape shape : structure){
			this.area.add(shape.getArea());
		}
		Rectangle2D r2 = this.area.getBounds2D();
		return r2.getX() + r2.getWidth()/2;
	}
	
	/**
	 * This method recalculates the y-middle point of the RnaStructure
	 *
	 * @return double, the y middle point of the structure
	 */
	private double getYMiddle(){
		this.area = new Area();
		for(RnaShape shape : structure){
			this.area.add(shape.getArea());
		}
		Rectangle2D r2 = this.area.getBounds2D();
		return r2.getY() + r2.getHeight()/2;
	}
	
	
	/**
	 * This method checks wether the Point2D object is within this RnaStructure
	 * and returns the belonging RnaShape.
	 *
	 * @param p the Point2D object to look for
	 * @return the RnaShape that contains p
	 */
	public RnaShape getShape(Point2D p, double zoomfactor){
		for(RnaShape shape : structure){
			if(shape.contains(p,zoomfactor)){
				return shape;
			}
		}
		return null;
	}
	
	/**
	 * Checks wether the given RnaShape is in range of any element within
	 * this RnaStructure
	 *
	 * @param RnaShape element, the element of interest
	 * @return boolean, true or false.
	 */
	public boolean inRange(RnaShape element){
		Rectangle2D elemrect = (element.getArea()).getBounds2D();
		for(RnaShape shape : structure){
			if((shape.getArea()).intersects(elemrect)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks wether the given coordinates are in range of any element within
	 * this RnaStructure
	 *
	 * @param double x
	 * @param double y
	 * @return boolean true or false.
	 */
	public boolean inRange(double x, double y){
		Rectangle2D.Double ssrect = new Rectangle2D.Double(x-10,y-10,20,20);
		for(RnaShape shape : structure){
			if((shape.getArea()).intersects(ssrect)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * this method checks whether the given coordinates are contained within the RnaStructure
	 * @param x, x coordinate
	 * @param y, y coordinate
	 * @return true, if (x,y) is contained within any shape stored in the RnaStructure; else false.
	 */
	public RnaShape contains(int x, int y){
		for(RnaShape shape : structure){
			if(shape.contains(x,y)){
				return shape;
			}
		}
		return null;
	}
	
	/**
	 * This method is called when the structure is to be traversed. E.g. to get
	 * the shape notation of the current structure:
	 * 
	 * @param RnaShape start, the open end of the structure
	 * @return String, the shape notation of the structure
	 */
	public String traverseStructure(RnaShape start, int type, boolean globalsearch){
		String structstring = "";
		String endstring = "";
		if(type == Translator.JDOM_LANG){
			Translator.beginJDOM(globalsearch,globalmin,globalmax);
			start.beginTraversal(type);
			structstring = Translator.getPrettyPrint();
		}
		else{
			if(type == Translator.XML_LANG){
				structstring = Translator.beginXML();
				endstring = Translator.endXML();
			}
			structstring += start.beginTraversal(type);
			structstring += endstring;
		}
		return structstring;
	}
	
	
	
	/**
	 * This method is called when the current structure is to be traversed.
	 * The structure can have several open ends.
	 *
	 * @param RnaShape start, the current main open end of the structure
	 * @return String, the shape notation of the current structure
	 */
	public String getCurrentShape(RnaShape start,Vector<RnaShape> furtherstarts){
		String structstring = "R ";
		structstring += start.currentForwardTraversal();
		if(furtherstarts != null){
			for(RnaShape nextstruct : furtherstarts){
				structstring += " R  ----  R ";
				structstring += nextstruct.currentForwardTraversal();
			}
		}
		structstring += " R";
		return structstring;
	}
	
	/**
	 * changes the orientation of the current structure
	 */
	public void changeOrientation(boolean seqreversal){
		for(RnaShape shape : structure){
			shape.changeOrientation(seqreversal);
		}
	}

	
	/**
	 * @return int, the height of the structure
	 */
	public int getHeight(){
		Rectangle2D bounds = this.area.getBounds2D();
		return (int)(bounds.getY()+bounds.getHeight()+100);
	}
	
	/**
	 * @return int, the width of the structure
	 */
	public int getWidth(){
		Rectangle2D bounds = this.area.getBounds2D();
		return (int)(bounds.getX()+bounds.getWidth()+100);
	}
	
	/**
	 * Stores the center around which a move transform is located
	 * 
	 * @param double mx, the x-coordinate of the center
	 * @param double my, the y-coordinate of the center
	 */
	public void storeMoveCenter(double mx, double my){
		xmove = mx;
		ymove = my;
		movetransform.setToIdentity();
	}
	
	/**
	 * Invokes a move transform of the RnaStructure taking into account the
	 * currently stored move center
	 * 
	 * @param double mx, the x-coordinate of the goal
	 * @param double my, the y-coordinate of the goal
	 */
	public void changeLocation(double mx,double my){
		movetransform.setToTranslation(mx-xmove,my-ymove);
		xmove = mx;
		ymove = my;
		for(RnaShape shape : structure){
			shape.changeLocation(movetransform);
		}
	}
	
	/**
	 * Storage method
	 * 
	 * @param s, the ObjectOutputStream
	 */
	private void writeObject(ObjectOutputStream s) throws IOException{
		s.writeObject(structure);
		s.writeDouble(xmove);
		s.writeDouble(ymove);
		s.writeObject(movetransform);
		s.writeInt(globalmin);
		s.writeInt(globalmax);
	}
	
	/**
	 * Storage method
	 * 
	 * @param s, the ObjectInputStream
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream s) throws IOException,ClassNotFoundException{
		structure = (Vector<RnaShape>) s.readObject();
		xmove = s.readDouble();
		ymove = s.readDouble();
		movetransform = (AffineTransform) s.readObject();
		try{
			globalmin = s.readInt();
			globalmax = s.readInt();
		}
		catch(EOFException eof){
			globalmin = 0;
			globalmax = 0;
		}
		initializeArea();
	}
	
	/**
	 * Initializes the Area of the RnaStructure to the current areas of the
	 * contained RnaShapes
	 */
	private void initializeArea(){
		this.area = new Area();
		for(RnaShape shape : structure){
			this.area.add(shape.getArea());
		}
	}
}


