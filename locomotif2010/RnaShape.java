package rnaeditor;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

/**
 * 
 * @author Janina Reeder
 *
 * The abstract class RnaShape defines the methods essential for every
 * Shape element. Most methods implemented here only work for the 
 * standard shapes in stem form (Stem, InternalLoop, Bulge Loop and Closed Struct).
 * Some methods are thus overwritten in the other subclasses.
 */
public abstract class RnaShape{
	
	protected Area area; //the geometric form of the Shape
	protected AffineTransform at; //the transform on what the user sees
	protected boolean selected;
	protected boolean isstartelement, isendelement;
	protected int startangle, endangle, startindex=0;
	protected boolean switchforced, standardorientation;
	protected Magnet mtheta, moffset; //the ends of the Shape
	protected int theta, offsetangle; //the visual orientation
	protected double height, xloc, yloc, width; //main coordinates
	protected int currentrotation;
	protected Vector<Rotation> rotations; //stores all rotations on the building block
	protected RnaShape[] exits;
	protected FreeMagnets fm; //overall free magnets
	protected static Color color;
	protected GeneralPath rnapath; //squiggle plot
	protected BuildingBlock bb;
	protected Ellipse2D motifheadarea;
	protected boolean editflag;
	
	/**
	 * This method returns the @see Area of the RnaShape
	 * 
	 * @return the Area of the shape
	 */
	public java.awt.geom.Area getArea(){
		return this.area;
	}
	
	/**
	 * This method checks whether the point (x,y) is contained within the building block's area
	 * 
	 * @param x, x coordinate
	 * @param y, y coordinate
	 * @return true, if (x,y) contained within area; else false
	 */
	public boolean contains(int x, int y){
		return this.area.contains(x,y);
	}
	
	/**
	 * This method returns the tool tip text of the building block
	 * 
	 * @return String representation of the tool tip
	 */
	public String getToolTipText(){
		return bb.getInfo();
	}
	
	/**
	 * Getter method for the motifhead which is transformed according to the current zoomfactor and any other AffineTransform in effect
	 * 
	 * @param zoomfactor, the current zoomfactor
	 * @return Shape representing the view on the motif head
	 */
	public Shape getMotifHead(double zoomfactor){
		AffineTransform zoom = new AffineTransform();
		zoom.setToScale(zoomfactor,zoomfactor);
		AffineTransform at2 = (AffineTransform) at.clone();
		zoom.concatenate(at2);
		this.setMotifHead();
		return zoom.createTransformedShape(this.motifheadarea);
	}
	
	/**
	 * Method for computing the view of the motif head for this building block. This depends on the location
	 * of the 5' end and the current AffineTransform of the building block.
	 *
	 */
	public void setMotifHead(){
		if(startangle == ((theta+offsetangle)%360)){
			if(exits[0] != null && exits[0].getType() == EditorGui.SS_DOUBLE && ((DoubleSingleShape)exits[0]).getThreePrime() != null){
				Point2D.Double transXY = new Point2D.Double(xloc,yloc);
				at.transform(transXY,transXY);
				if((((DoubleSingleShape)exits[0]).getThreePrime()).endContains(transXY)){
					motifheadarea = new Ellipse2D.Double(xloc+20,yloc-35,25,25);
				}
				else{
					motifheadarea = new Ellipse2D.Double(xloc-5,yloc-35,25,25);
				}
			}
			else{
				if(standardorientation){
					motifheadarea = new Ellipse2D.Double(xloc+20,yloc-35,25,25);
				}
				else{
					motifheadarea = new Ellipse2D.Double(xloc-5,yloc-35,25,25);
				}
			}
		}
		else{
			if(exits[1] != null && exits[1].getType() == EditorGui.SS_DOUBLE && ((DoubleSingleShape)exits[1]).getThreePrime() != null){
				Point2D.Double transXY = new Point2D.Double(xloc,yloc+height);
				at.transform(transXY,transXY);
				if((((DoubleSingleShape)exits[1]).getThreePrime()).endContains(transXY)){
					motifheadarea = new Ellipse2D.Double(xloc+20,yloc+height+10,25,25);
				}
				else{
					motifheadarea = new Ellipse2D.Double(xloc-5,yloc+height+10,25,25);
				}
			}
			else{
				if(standardorientation){
					motifheadarea = new Ellipse2D.Double(xloc-5,yloc+height+10,25,25);
				}
				else{
					motifheadarea = new Ellipse2D.Double(xloc+20,yloc+height+10,25,25);
				}
			}
		}
	}
	
	/**
	 * This method returns the x-coordinate of the upper left corner 
	 * of the bounding box of the RnaShape's @see Area
	 * 
	 * @return the x-coordinate of the upper left corner of the 
	 * bounding box
	 */
	public int getX(){
		Rectangle2D bounds = this.area.getBounds2D();
		return (int) bounds.getX();
	}
	
	/**
	 * This method returns the y-coordinate of the upper left corner 
	 * of the bounding box of the RnaShape's @see Area
	 * 
	 * @return the y-coordinate of the upper left corner of the 
	 * bounding box
	 */
	public int getY(){
		Rectangle2D bounds = this.area.getBounds2D();
		return (int) bounds.getY();
	}
	
	/**
	 * This method returns the type of the RnaShape
	 * 
	 * @return the int value describing the shape's type
	 */
	public abstract int getType();
	
	/**
	 * This method returns the bounding box of the RnaShape's @see Area
	 * 
	 * @return the bounding box of this shape's area
	 */
	public java.awt.geom.Rectangle2D getBounds2D(){
		return area.getBounds2D();
	}
	
	/**
	 * This method returns all exits of the shape as a Magnet Vector
	 * 
	 * @return the Vector containing all Magnets of this shape
	 */
	public java.util.Vector<Magnet> getBorders(){
		Vector<Magnet> borders = new Vector<Magnet>();
		borders.add(moffset);
		borders.add(mtheta);
		return borders;
	}
	
	/**
	 * This method returns all free Magnets of this shape, i.e. not m
	 *
	 * @param m, the Magnet that is not to be returned in the Vector
	 * @return a Vector containing all free Magnets of this shape
	 */
	public java.util.Vector<Magnet> getBorders(Magnet m){
		Vector<Magnet> borders = new Vector<Magnet>();
		if(m.getAngle() == theta){
			borders.add(mtheta);
		}
		else{
			borders.add(moffset);
		}
		return borders;
	}
	
	/**
	 * Getter method for this RnaShape's AffineTransform
	 *
	 * @return the AffineTransform of this RnaShape
	 */
	public java.awt.geom.AffineTransform getTransform(){
		return this.at;
	}
	
	/**
	 * This method sets the flag switchforced. Used internally for traversals.
	 * 
	 * @param switchforced
	 */
	public void setSwitchForced(boolean switchforced){
		this.switchforced = switchforced;
	}
	
	/**
	 * Method to check wether a given Point2D is contained within the
	 * RnaShape
	 *
	 * @param p, the Point2D object to look for
	 * @return true, if p is contained within this RnaShape; else false.
	 */
	public boolean contains(java.awt.geom.Point2D p, double zoomfactor){
		AffineTransform zoomtransform = new AffineTransform();
		zoomtransform.setToScale(zoomfactor,zoomfactor);
		zoomtransform.concatenate(at);
		Shape transarea = zoomtransform.createTransformedShape(this.area);
		return transarea.contains(p);
	}
	
	/**
	 * This method changes the orientation of the shape
	 */
	public abstract void changeOrientation(boolean seqreversal);
	
	/**
	 * This method changes the size of the Shape, i.e. changes its Area
	 *
	 *
	 * @param seglength, the sequence length upon which the size depends
	 */
	public abstract void changeSize(int seqlength);
	
	/**
	 * This method changes the size of the Shape, i.e. changes its Area
	 * according to a median value for the sequence length
	 *
	 * @param minlength, the minimum sequence length
	 * @param maxlength, the maximum sequence length
	 */
	public abstract void changeSize(int minlength, int maxlength);
	
	/**
	 * This method implements a change in location to a new point of origin
	 *
	 * @param x, the x-coordinate of the new origin
	 * @param y, the y-coordinate of the new origin
	 */
	public abstract void changeLocation(double x, double y);
	
	/**
	 * This method implements a change in location to a new point of origin
	 *
	 * @param x, the x-coordinate of the new origin
	 * @param y, the y-coordinate of the new origin
	 * @param bottom, the boolean value indicates the point of rotation
	 */
	public abstract void changeLocation(double x, double y, boolean bottom);
	
	/**
	 * This method changes the location according to a transformation
	 * stored in the AffineTransform
	 *
	 * @param movetransform stores a move transform that the bulge should
	 * make
	 */
	public abstract void changeLocation(java.awt.geom.AffineTransform movetransform);
	
	/**
	 * This method adjust the Magnets according to a location or size change
	 */
	public void adjustMagnets(){
		Line2D.Double line = new Line2D.Double(xloc,yloc,xloc+width,yloc);
		Point2D p1 = line.getP1();
		Point2D p2 = line.getP2();
		at.transform(p1,p1);
		at.transform(p2,p2);
		if(fm.contains(moffset)){
			fm.remove(moffset);
			moffset = new Magnet(new Line2D.Double(p1,p2),this,(theta+offsetangle)%360,moffset.getIsHairpinAccessible());
			fm.add(moffset);
		}
		else if(exits[0] != null && exits[0].getType() == EditorGui.SS_DOUBLE){
			fm.adjustMagnet(moffset, new Magnet(new Line2D.Double(p1,p2),this,(theta+offsetangle)%360,moffset.getIsHairpinAccessible()));
			moffset = new Magnet(new Line2D.Double(p1,p2),this,(theta+offsetangle)%360,moffset.getIsHairpinAccessible());
		}
		else{
			moffset = new Magnet(new Line2D.Double(p1,p2),this,(theta+offsetangle)%360,moffset.getIsHairpinAccessible());
		}
		
		line = new Line2D.Double(xloc+width,yloc+height,xloc,yloc+height); 
		p1 = line.getP1();
		p2 = line.getP2();
		at.transform(p1,p1);
		at.transform(p2,p2);
		if(fm.contains(mtheta)){
			fm.remove(mtheta);
			mtheta = new Magnet(new Line2D.Double(p1,p2),this,theta,mtheta.getIsHairpinAccessible());
			fm.add(mtheta);
		}
		else if(exits[1] != null && exits[1].getType() == EditorGui.SS_DOUBLE){
			fm.adjustMagnet(mtheta, new Magnet(new Line2D.Double(p1,p2),this,theta,mtheta.getIsHairpinAccessible()));
			mtheta = new Magnet(new Line2D.Double(p1,p2),this,theta,mtheta.getIsHairpinAccessible());
		}
		else{
			mtheta = new Magnet(new Line2D.Double(p1,p2),this,theta,mtheta.getIsHairpinAccessible());
		}
	}
	
	/**
	 * This method adjusts the hairpin accessability of all magnets depending on the given parameter.
	 * 
	 * @param accessible determines whether the magnets are hairpin accessible
	 */
	public void adjustMagnetAccessability(boolean accessible){
		moffset.setIsHairpinAccessible(accessible);
		mtheta.setIsHairpinAccessible(accessible);
	}
	
	/**
	 * Method for starting a traversal after a hairpin was added to the structure. Then,
	 * the hairpin accessibility for all other magnets must be determined. This method should
	 * only be called from hairpin loop or closedend and hence, the message body is empty in all other cases.
	 * 
	 * @param angle describing the direction of the traversal
	 */
	public void startHPAccessTraversal(int angle){
		//should only be called for hairpin and multiend
	}
	
	/**
	 * Method called during the traversal invoked after a hairpin was added to the structure.
	 * Its purpose is to determine the number of remaining open ends.
	 * 
	 * @param angle describing the direction of the traversal
	 */
	public void hairpinAccessTraversal(int angle){
		if(angle == theta){
		    //the traversal ended in the same helix to which the hairpin was added: last open end!
			if(exits[0] == null){
				moffset.setIsHairpinAccessible(false);
			}
			else{
				this.exits[0].hairpinAccessTraversal(angle);
			}
		}
		else if(angle == (theta+offsetangle)%360){
		    //the traversal ended in the same helix to which the hairpin was added: last open end!
			if(exits[1] == null){
				mtheta.setIsHairpinAccessible(false);
			}
			else{
				this.exits[1].hairpinAccessTraversal(angle);
			}
		}
	}
	
	/**
	 * This method is called in a hairpin access traversals after a multiloop was encountered.
	 * Then, the result is not straightforward, but rather all open ends are collected in the 
	 * openmagnets array. In the end, their number determines whether all are accessible (if > 1) or not.
	 * 
	 * @param angle describing the direction of the traversal
	 * @param openmagnets array for collecting all available magnets
	 */
	public void hairpinAccessTraversal(int angle, Vector<Magnet> openmagnets){
		if(angle == theta){
		    //we found an open end: store it
			if(exits[0] == null){
				openmagnets.add(moffset);
			}
			//recursion continues with neighbor
			else{
				this.exits[0].hairpinAccessTraversal(angle, openmagnets);
			}
		}
		else if(angle == (theta+offsetangle)%360){
			if(exits[1] == null){
				openmagnets.add(mtheta);
			}
			else{
				this.exits[1].hairpinAccessTraversal(angle,openmagnets);
			}
		}
	}
	
	/**
	 * This method implements a snapping mechanism which takes place if 
	 * the shape is close to a free Magnet
	 *
	 *
	 * @param m, the Magnet to which the shape snaps
	 */
	public void snapTo(Magnet m){
		Line2D.Double line = m.getLine();
		double x1 = line.getX1();
		double y1 = line.getY1();
		double x2 = line.getX2();
		double y2 = line.getY2();
		//the angles fit together at theta's side
		if(m.getAngle()==theta){
			changeLocation(x2,y2,false);
		}
		//the angles fit together at (theta+offsetangle)%360's side
		else if(m.getAngle()==((theta+offsetangle)%360)){
			changeLocation(x1,y1-height,true);
		}
		//snap to theta
		else if(currentrotation == 0){
			changeLocation(x2,y2);
			at.rotate(StrictMath.toRadians(m.getAngle()),xloc,yloc);
			rotations.add(new Rotation(m.getAngle(),1));
			theta = StrictMath.abs(m.getAngle());
			adjustMagnets();
		}
	}
	
	/**
	 * This method restores the affine transformation to identity, 
	 * i.e. all rotations are undone and redone according to the currentrotation
	 * 
	 */
	public void restore(){
			at.setToIdentity();
			rotations.clear();
			if(currentrotation!=0){
				at.rotate(StrictMath.toRadians(currentrotation),xloc+width/2,yloc+height/2);
				rotations.add(new Rotation(currentrotation,3));
			}
			theta = currentrotation;
			adjustMagnets();
	}
	
	/**
	 * Rotation method which rotates the structure around its center
	 *
	 *
	 * @param degree, the degree of the rotation
	 */
	public void rotateBy(int degree){
		at.rotate(StrictMath.toRadians(degree),xloc+width/2,yloc+height/2);
		rotations.add(new Rotation(degree,3));
		theta += degree;
		theta = theta %360;
		currentrotation += degree;
		currentrotation = currentrotation %360;
		adjustMagnets();
	}
	
	/**
	 * Rotation method which rotates the structure around a given point
	 *
	 *
	 * @param degree, the degree of the rotation
	 * @param xmiddle, the x location of the rotation point
	 * @param ymiddle, the y location of the rotation point
	 */
	public void rotateBy(int degree, double xmiddle, double ymiddle){
		AffineTransform buf = new AffineTransform(at);
		at.setToIdentity();
		at.rotate(StrictMath.toRadians(degree),xmiddle,ymiddle);
		rotations.add(0,new Rotation(degree,xmiddle,ymiddle));
		at.concatenate(buf);
		theta += degree;
		theta = theta %360;
		startangle += degree;
		startangle = startangle % 360;
		endangle += degree;
		endangle = endangle % 360;
		adjustMagnets();
	}
	
	public int getRotation(){
	    return this.theta;
	}
	
	
	/**
	 * Setter method for the flag isstartelement which indicates
	 * wether this element is the main open end of the structure.
	 *
	 *
	 * @param isstartelement, true means this is the main open end
	 * @param angle, the angle of the open end
	 */
	public void setIsStartElement(boolean isstartelement, int angle, int direct){
		this.isstartelement = isstartelement;
		startangle = angle;
		if(startangle == (theta+offsetangle)%360){
			startindex = 0;
		}
		else if(startangle == theta){
			startindex = 1;
		}
		if(isstartelement){
			setMotifHead();
		}
	}
	
	/**
	 * This method calls the other setIsStartElement method with a value of 0 for direct!
	 * It is called whenever a building block with more than 1 exit (not ss) is added to the start
	 * element. In case of the multiloop, we must compute the nearest exit: new start element! Thus direct=0.
	 * 
	 * @param isstartelement determines whether the element is the startelement or not
	 * @param angle describing the location of the start element; -1 if isstartelement = false
	 */
	public void setIsStartElement(boolean isstartelement, int angle){
		setIsStartElement(isstartelement, angle, 0);
	}
	
	/**
	 * Getter method for the flag isstartelement
	 *
	 * @return true, if this is the start element, else false
	 */
	public boolean getIsStartElement(){
		return this.isstartelement;
	}
	
	/**
	 * Getter method for the angle of the main open end
	 *
	 * @return -1, if not start element, else the angle (=magnet)
	 */
	public int getStartAngle(){
		return this.startangle;
	}
	
	/**
	 * Traversal method which proceeds until a new open end is found.
	 * Called if the current main open end is closed by a hairpin
	 *
	 *
	 * @param angle, the direction of the traversal
	 * @param real, necessary for bulge switching sides!!
	 * @return this RnaShape or the result of a recursive call
	 */
	public RnaShape traverseToNewStart(int angle, boolean real){
		if(real){
			switchsides();
			startindex = ++startindex % 2;
			if(this.getType() == EditorGui.BULGE_TYPE){
				((BulgeShape)this).changeBulgeLoc();
			}
		}
		if(angle == theta){
			if(exits[0] == null){
				setIsStartElement(true, (theta+offsetangle)%360, 0);
				if(real&& this.getType() == EditorGui.BULGE_TYPE){
					((BulgeShape)this).redraw();
				}
				return this;
			}
			else{
				if(exits[0].getType() == EditorGui.SS_DOUBLE){
					SingleShape next = ((DoubleSingleShape)exits[0]).getFivePrime();
					if(next == null){
						setIsStartElement(true, (theta+offsetangle)%360, 0);
						return this;
					}
					return next.traverseToNewStart(angle, real);
				}
				else{
					return this.exits[0].traverseToNewStart(angle, real);
				}
			}
		}
		else{
			if(exits[1] == null){
				setIsStartElement(true, theta, 0);
				if(real&& this.getType() == EditorGui.BULGE_TYPE){
					((BulgeShape)this).redraw();
				}
				return this;
			}
			else{
				if(exits[1].getType() == EditorGui.SS_DOUBLE){
					SingleShape next = ((DoubleSingleShape)exits[1]).getFivePrime();
					if(next == null){
						setIsStartElement(true, theta, 0);
						return this;
					}
					return next.traverseToNewStart(angle, real);
				}
				else{
					return this.exits[1].traverseToNewStart(angle, real);
				}
			}
		}
	}

	/**
	 * This method returns the new 5' end, if the old one was occupied
	 * by attaching a new building block
	 *
	 * @param index, the exit where we came from
	 * @return the new start element
	 */
	public RnaShape findNewStartElement(int index){
		if(exits[index] != null){
			exits[index].setIsStartElement(true,startangle,1);
			return exits[index];
		}
		return null;
	}

	/**
	 * This method returns the Magnet which belongs to the 5' end
	 *
	 * @return the Magnet which belongs to the 5' end
	 */
	public Magnet getStartExit(){
		if(startangle == theta){
			return mtheta;
		}
		return moffset;
	}
	
	/**
	 * Setter method for the flag isendelement which indicates
	 * wether this element is the main open end of the structure.
	 *
	 * @param isendelement, true means this is the main open end
	 * @param angle, the angle of the open end
	 */
	public void setIsEndElement(boolean isendelement, int angle, int direct){
		this.isendelement = isendelement;
		endangle = angle;
	}

	/**
	 * Getter method for the flag isendelement
	 *
	 * @return true, if this is the end element, else false
	 */
	public boolean getIsEndElement(){
		return this.isendelement;
	}
	
	/**
	 * Getter method for the angle of the main open end
	 *
	 * @return -1, if not end element, else the angle (=magnet)
	 */
	public int getEndAngle(){
		return this.endangle;
	}
	
	/**
	 * Traversal method which proceeds until a new open end is found.
	 * Called if the current main open end is closed by a hairpin
	 *
	 * @param angle, the direction of the traversal
	 * @return this RnaShape or the result of a recursive call
	 */
	public RnaShape traverseToNewEnd(int angle){
		if(angle == theta){
			if(exits[0] == null){
				setIsEndElement(true, (theta+offsetangle)%360, 0);
				return this;
			}
			else{
				if(exits[0].getType() == EditorGui.SS_DOUBLE){
					SingleShape next = ((DoubleSingleShape)exits[0]).getThreePrime();
					if(next == null){
						setIsEndElement(true, (theta+offsetangle)%360, 0);
						return this;
					}
					return next.traverseToNewEnd(angle);
				}
				else{
					return this.exits[0].traverseToNewEnd(angle);
				}
			}
		}
		else{
			if(exits[1] == null){
				setIsEndElement(true, theta, 0);
				return this;
			}
			else{
				if(exits[1].getType() == EditorGui.SS_DOUBLE){
					SingleShape next = ((DoubleSingleShape)exits[1]).getThreePrime();
					if(next == null){
						setIsEndElement(true, theta, 0);
						return this;
					}
					return next.traverseToNewEnd(angle);
				}
				else{
					return this.exits[1].traverseToNewEnd(angle);
				}
			}
		}
	}
	
	
	/**
	 * This method returns the new 5' end, if the old one was occupied
	 * by attaching a new building block
	 *
	 * @param index, the exit where we came from
	 * @return RnaShape the new end element
	 */
	public RnaShape findNewEndElement(int index){
		if(exits[index] != null){
			exits[index].setIsEndElement(true,endangle,1);
			return exits[index];
		}
		return null;
	}
	
	/**
	 * This method returns the Magnet which belongs to the 5' end
	 *
	 * @return the Magnet which belongs to the 5' end
	 */
	public Magnet getEndExit(){
		if(endangle == theta){
			return mtheta;
		}
		return moffset;
	}

	/**
	 * This method checks wether the RnaShape is at the end of the structure
	 * 
	 * @return index of the end or -1 if not found
	 */
	public int getEndType(){
		if(exits[0] == null && (exits[1] == null || exits[1].getType() != EditorGui.SS_DOUBLE)) return 1;
		else if(exits[1] == null && (exits[0] == null || exits[0].getType() != EditorGui.SS_DOUBLE)) return 0;
		else return -1;
	}
	
	/**
	 * Method for finding any open end within the current motif part.
	 * 
	 * @param angle describing the direction of the search
	 * @return true, if motif part contains an open end; else false
	 */
	public boolean findOpenEnd(int angle){
		if(angle == theta){
			if(exits[0] == null){
				return true;
			}
			else{
				return exits[0].findOpenEnd(angle);
			}
		}
		else{
			if(exits[1] == null){
				return true;
			}
			else{
				return exits[1].findOpenEnd(angle);
			}
		}
	}
	
	/**
	 * Method for traversing to a compound block for ambiguity checking (recursive method)
	 * 
	 * @param angle describing the direction of the traversal
	 * @return true, if compound block is encountered; else false
	 */
	public boolean traverseToClosed(int angle){
		if(angle == theta){
			if(exits[0] == null){
				return false;
			}
			else{
				return exits[0].traverseToClosed(angle);
			}
		}
		else{
			if(exits[1] == null){
				return false;
			}
			else{
				return exits[1].traverseToClosed(angle);
			}
		}
	}
	
	/**
	 * This method adds a neighboring shape to this one
	 * 
	 * @param shape, the shape that is added as a neighbor
	 * @param angle, the angle of the exit, where the shape is neighboring
	 * @param type is only required for SingleShape
	 */
	public void addToNeighbors(RnaShape shape,int angle,int type){
		if(angle == theta){
			exits[0] = shape;
		}
		else if(angle == (theta+offsetangle)%360){
			exits[1] = shape;
		}
	}
	
	/**
	 * This method adds a SingleShape neighbor to this element
	 *
	 * @param shape, the SinglShape to be added
	 * @param angle, the angle where the shape is added
	 * @return 1 if the singleshape was added as fiveprime or 
	 * 2 if it was added as threeprime
	 */
	public int addSSNeighbor(SingleShape shape, int angle){
		if(this.isstartelement || this.isendelement){
			if(angle == theta){
				if(exits[0] == null){
					DoubleSingleShape dss = new DoubleSingleShape();
					Point2D mp1 = new Point2D.Double(xloc,yloc);
					Point2D mp1trans = new Point2D.Double();
					at.transform(mp1,mp1trans);
					double realx = mp1trans.getX();
					double realy = mp1trans.getY();
					if(standardorientation){
						if(shape.contains(realx,realy)){
							dss.addThreePrime(shape);
							exits[0] = dss;
							shape.addFivePrimeDSS(dss);
							return 2;
						}
						else{
							dss.addFivePrime(shape);
							exits[0] = dss;
							shape.addThreePrimeDSS(dss);
							return 1;
						}
					}
					else{
						if(shape.contains(realx,realy)){
							dss.addFivePrime(shape);
							exits[0] = dss;
							shape.addThreePrimeDSS(dss);
							return 1;
						}
						else{
							dss.addThreePrime(shape);
							exits[0] = dss;
							shape.addFivePrimeDSS(dss);
							return 2;
						}
					}
				}
				else{
					if(((DoubleSingleShape)exits[0]).getOpenFivePrime()){
						((DoubleSingleShape)exits[0]).addFivePrime(shape);
						shape.addThreePrimeDSS((DoubleSingleShape)exits[0]);
						return 1;
					}
					else{
						((DoubleSingleShape)exits[0]).addThreePrime(shape);
						shape.addFivePrimeDSS((DoubleSingleShape)exits[0]);
						return 2;
					}
				}
			}
			else if(angle == (theta+offsetangle)%360){
				if(exits[1] == null){
					DoubleSingleShape dss = new DoubleSingleShape();
					Point2D mp1 = new Point2D.Double(xloc,yloc+height);
					Point2D mp1trans = new Point2D.Double();
					at.transform(mp1,mp1trans);
					double realx = mp1trans.getX();
					double realy = mp1trans.getY();
					if(standardorientation){
						if(shape.contains(realx,realy)){
							dss.addFivePrime(shape);
							exits[1] = dss;
							shape.addThreePrimeDSS(dss);
							return 1;
						}
						else{
							dss.addThreePrime(shape);
							exits[1] = dss;
							shape.addFivePrimeDSS(dss);
							return 2;
						}
					}
					else{
						if(shape.contains(realx,realy)){
							dss.addThreePrime(shape);
							exits[1] = dss;
							shape.addFivePrimeDSS(dss);
							return 2;
						}
						else{
							dss.addFivePrime(shape);
							exits[1] = dss;
							shape.addThreePrimeDSS(dss);
							return 1;
						}
					}
				}
				else{
					if(((DoubleSingleShape)exits[1]).getOpenFivePrime()){
						((DoubleSingleShape)exits[1]).addFivePrime(shape);
						shape.addThreePrimeDSS((DoubleSingleShape)exits[1]);
						return 1;
					}
					else{
						((DoubleSingleShape)exits[1]).addThreePrime(shape);
						shape.addFivePrimeDSS((DoubleSingleShape)exits[1]);
						return 2;
					}
				}
			}
		}
		else if(shape.getIsConnected()){
			//shape ist 3' ende (haengt an 3' position)
			if(shape.getIsFivePrimeDSSAvailable()){
				if(angle == theta){
					if(exits[0] == null){
						DoubleSingleShape dss = new DoubleSingleShape();
						dss.addFivePrime(shape);
						exits[0] = dss;
						shape.addThreePrimeDSS(dss);
					}
					else{
						((DoubleSingleShape)exits[0]).addFivePrime(shape);
						shape.addThreePrimeDSS((DoubleSingleShape)exits[0]);
					}
					Point2D mp1 = new Point2D.Double(xloc,yloc);
					Point2D mp1trans = new Point2D.Double();
					at.transform(mp1,mp1trans);
					double realx = mp1trans.getX();
					double realy = mp1trans.getY();
					if((standardorientation && shape.contains(realx,realy)) || (!standardorientation && !shape.contains(realx,realy))){
						//3' dockt an 3'!!!
						//changeOrientation fuer alle in diesem
						switchSidesTraversal(this, true, (theta+offsetangle)%360);
						switchforced = true;
					}
					return 1;
				}
				else{
					if(exits[1] == null){
						DoubleSingleShape dss = new DoubleSingleShape();
						dss.addFivePrime(shape);
						exits[1] = dss;
						shape.addThreePrimeDSS(dss);
					}
					else{
						((DoubleSingleShape)exits[1]).addFivePrime(shape);
						shape.addThreePrimeDSS((DoubleSingleShape)exits[1]);
					}
					Point2D mp1 = new Point2D.Double(xloc,yloc+height);
					Point2D mp1trans = new Point2D.Double();
					at.transform(mp1,mp1trans);
					double realx = mp1trans.getX();
					double realy = mp1trans.getY();
					if((standardorientation && !shape.contains(realx,realy)) || (!standardorientation && shape.contains(realx,realy))){
						//3' dockt an 3'!!!
						//changeOrientation fuer alle in diesem
						switchSidesTraversal(this, true, theta);
						switchforced = true;
					}
					return 1;
				}
			}
			//shape ist 5' ende (haengt an 5' position)
			else{
				if(angle == theta){
					if(exits[0] == null){
						DoubleSingleShape dss = new DoubleSingleShape();
						dss.addThreePrime(shape);
						exits[0] = dss;
						shape.addFivePrimeDSS(dss);
					}
					else{
						((DoubleSingleShape)exits[0]).addThreePrime(shape);
						shape.addFivePrimeDSS((DoubleSingleShape)exits[0]);
					}
					Point2D mp1 = new Point2D.Double(xloc,yloc);
					Point2D mp1trans = new Point2D.Double();
					at.transform(mp1,mp1trans);
					double realx = mp1trans.getX();
					double realy = mp1trans.getY();
					if((standardorientation && !shape.contains(realx,realy)) || (!standardorientation && shape.contains(realx,realy))){
						//5' dockt an 5'!!!
						//changeOrientation fuer alle in diesem
						switchSidesTraversal(this, false, (theta+offsetangle)%360);
						switchforced = true;
					}
					return 2;
				}
				else{
					if(exits[1] == null){
						DoubleSingleShape dss = new DoubleSingleShape();
						dss.addThreePrime(shape);
						exits[1] = dss;
						shape.addFivePrimeDSS(dss);
					}
					else{
						((DoubleSingleShape)exits[1]).addThreePrime(shape);
						shape.addFivePrimeDSS((DoubleSingleShape)exits[1]);
					}
					Point2D mp1 = new Point2D.Double(xloc,yloc+height);
					Point2D mp1trans = new Point2D.Double();
					at.transform(mp1,mp1trans);
					double realx = mp1trans.getX();
					double realy = mp1trans.getY();
					if((standardorientation && shape.contains(realx,realy)) || (!standardorientation && !shape.contains(realx,realy))){
						//5' dockt an 5'!!!
						//changeOrientation fuer alle in diesem
						switchSidesTraversal(this, false, theta);
						switchforced = true;
					}
					return 2;
				}
			}
		}
		else{
			System.out.println("AddSSNeighbor in RnaShape: Should not be needed");
		}
		return 0;
	}
	
	/** 
	 * This method removes all neighbors of an RnaShape
	 */
	public void removeNeighbors(){
		if(exits[0] != null){
			exits[0].remove(this);
			exits[0].adjustMagnetAccessability(mtheta.getIsHairpinAccessible());
			exits[0] = null;
		}
		else if(exits[1] != null){
			exits[1].remove(this);
			exits[1].adjustMagnetAccessability(moffset.getIsHairpinAccessible());
			exits[1] = null;
		}
	}
	
	/**
	 * Method for reseting the hairpin accessability. For all building blocks with 2 exits,
	 * both magnets are accessible.
	 *
	 */
	public void resetHPAccessability(){
		mtheta.setIsHairpinAccessible(true);
		moffset.setIsHairpinAccessible(true);
	}
	
	/**
	 * This method removes a specific RnaShape from the neighbors of this one
	 *
	 * @param s, the RnaShape to be removed
	 */
	public void remove(RnaShape s){
		if(s.getType() != EditorGui.SINGLE_TYPE){
			if(exits[0] == s){
				fm.add(moffset);
				exits[0] = null;
			}
			else if(exits[1] == s){
				fm.add(mtheta);
				exits[1] = null;
			}
		}
		else{
			if(exits[0] != null && exits[0].getType() == EditorGui.SS_DOUBLE && ((DoubleSingleShape)exits[0]).contains(s)){
				((DoubleSingleShape)exits[0]).remove(s);
				if(((DoubleSingleShape)exits[0]).isEmpty()){
					exits[0] = null;
					fm.remove(this);
					fm.add(moffset);
					if(switchforced){
						switchSidesTraversal(null,false,theta+offsetangle);
						switchforced = false;
					}
				}
				else{
					fm.add(moffset.getRest(s.getBorders()));
				}
			}
			else if(exits[1] != null && exits[1].getType() == EditorGui.SS_DOUBLE && ((DoubleSingleShape)exits[1]).contains(s)){
				((DoubleSingleShape)exits[1]).remove(s);
				if(((DoubleSingleShape)exits[1]).isEmpty()){
					exits[1] = null;
					fm.remove(this);
					fm.add(mtheta);
					if(switchforced){
						switchSidesTraversal(null,false,theta);
						switchforced = false;
					}
				}
				else{
					fm.add(mtheta.getRest(s.getBorders()));
				}
			}
		}
	}
	
	/**
	 * This method checks wether there is a DoubleSingleStrand at exit of the given index
	 *
	 * @param index, the index where a DSS is searched for
	 */
	public boolean hasDSSNeighbor(int index){
		if(exits[index] != null && exits[index].getType() == EditorGui.SS_DOUBLE){
			return true;
		}
		return false;
	}
	
	/**
	 * This method checks wether the Bulge is connected to the given shape at the given angle
	 *
	 * @param shape, the RnaShape
	 * @param angle, the angle where the shape might be connected
	 * @param origin, the RnaShape used initially in the recursive search for a connection
	 */
	public boolean connectedTo(RnaShape shape, int angle, RnaShape origin){
		boolean found0 = false, found1 = false;
		if((angle == theta || angle == -1) && this.exits[0] != null){
			if(exits[0] == shape){
				return true;
			}
			else{
				found0 = exits[0].connectedTo(shape, theta, this);
			}
		}
		if(found0 == false && (angle != theta || angle == -1) && this.exits[1] != null){
			if(exits[1] == shape){
				return true;
			}
			else{
				found1 = exits[1].connectedTo(shape, (theta+offsetangle)%360, this);
			}
		}
		return (found0 || found1);
		
	}
	
	public boolean startFindSS(){
		return findSS(theta) || findSS((theta+offsetangle)%360);
	}
	
	
	/**
	 * This method tries to find a SingleShape in the given direction
	 * 
	 * @param angle, the direction where to search
	 * @return true, if a SingleShape is found; else false.
	 */
	public boolean findSS(int angle){
		if(angle == (theta+offsetangle)%360){
			if(exits[1] == null){
				return false;
			}
			else if(exits[1].getType() == EditorGui.SS_DOUBLE){
				return true;
			}
			else{
				return exits[1].findSS(angle);
			}
		}
		else{
			if(exits[0] == null){
				return false;
			}
			else if(exits[0].getType() == EditorGui.SS_DOUBLE){
				return true;
			}
			else{
				return exits[0].findSS(angle);
			}
		}
	}

	/**
	 * This method switches 5' and 3' strand information
	 */
	public abstract void switchsides();
	
	/**
	 * This method is used for traversing the structure with a change of 5' and
	 * 3' strands.
	 * 
	 * @param start, the origin RnaShape of the search
	 * @param fiveprimedir, the direction of the search: true indicates 5', false 3'
	 * @param angle, the angle in whose direction the search is to be continued.
	 */
	public abstract void switchSidesTraversal(RnaShape start, boolean fiveprimedir, int angle);
	
	
	/**
	 * The starting method for traversing an RNA structure. 
	 * The return String is initialized here and subsequent methods are called.
	 *
	 * @return a String containing the shape notation for the whole structure
	 */
	public abstract String beginTraversal(int type);
	
	/**
	 * The subsequent method during a traversal.
	 * The return String is complemented here and subsequent methods are called
	 *
	 * @param angle, the angle where the previous shape came from
	 * @return a String containing the current shape notation
	 */
	protected abstract String traverse(int angle, int type);
	
	/**
	 * Method for a traversal and shift operation. 
	 * The RnaShape is adjusted according to a size change of another
	 * element in the RnaStructure
	 *
	 * @param angle, the direction the shift is going
	 */
	public void traverseShift(int angle){
		if(angle == theta && exits[0] != null){
			exits[0].restore();
			exits[0].snapTo(moffset);
			exits[0].traverseShift(theta);
		}
		else if(angle == ((theta+offsetangle)%360) && exits[1] != null){
			exits[1].restore();
			exits[1].snapTo(mtheta);
			exits[1].traverseShift((theta+offsetangle)%360);
		}
	}
	
	public void traverseShift(AffineTransform movetransform, int angle){
		if(angle == theta && exits[0] != null){
			exits[0].changeLocation(movetransform);
			exits[0].traverseShift(movetransform,theta);
		}
		else if(angle == ((theta+offsetangle)%360) && exits[1] != null){
			exits[1].changeLocation(movetransform);
			exits[1].traverseShift(movetransform,(theta+offsetangle)%360);
		}
	}
	
	/**
	 * Starting method for a traversal of the current structure which can
	 * have several open ends.
	 *
	 * @return a String containing the current shape notation of the structure
	 */
	public abstract String currentForwardTraversal();
	
	
	/**
	 * The subsequent method during a traversal.
	 * The return String is complemented here and subsequent methods are called
	 *
	 * @param angle the angle where the previous shape came from
	 * @return the String containing the current shape notation
	 */
	protected abstract String ctraverseF(int angle);
	
	
	/**
	 * Method that opens a new Edit Window for this RnaShape
	 *
	 * @param ds, the parent DrawingSurface
	 * @param type defines which tab is shown first (0=standard, 1=size, 2=sequence, 3=loc in bulge, 4=loop53 in internal, 5=loop35 in internal, 6=exits in multi)
	 */
	public abstract void openEditWindow(DrawingSurface ds, int type);
	
	/**
	 * This method sets this RnaShape to be selected/unselected
	 * 
	 * @param selected, flag for the selection of this RnaShape
	 */
	public void setSelected(boolean selected){
		this.selected = selected;
	}
	
	public void editClosed(){
	    this.editflag = false;
	}
	
	/**
	 * This method does the actual drawing of this shape's Area
	 *
	 * @param g2, the Graphics2D object that performs the drawing
	 * @param fixed, boolean value describing wether the shape is part of 
	 * the current RnaStructure or not
	 */
	public abstract void show(java.awt.Graphics2D g2,boolean fixed);
	
}

