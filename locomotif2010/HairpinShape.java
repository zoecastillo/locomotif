package rnaeditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;



/**
 * This class implements a HairpinShape which represents a hairpin graphically
 *
 * @author Janina Reeder
 */
public class HairpinShape extends RnaShape implements Serializable {
	private static final long serialVersionUID = -4735203190138411141L;
	private HairpinLoop hairpin; //the underlying data structure
	private double rheight; //height of the exit rectangle
	private double circleheight; //width of the circle shape
	private double circlewidth; //width of the circle shape
	private boolean standardorientation;
	private int largerthanshown;
	private RnaShape exit;
	
	/**
	 * Constructor for a hairpin shape
	 * 
	 * @param orientation, true = standard, false = reverse
	 */
	public HairpinShape(boolean orientation, FreeMagnets fm){
		xloc = 0;
		yloc = 0;
		width = 40;
		rheight = 15;
		circlewidth = 87.5;
		circleheight = 87.5;
		hairpin = new HairpinLoop(orientation);
		bb = this.hairpin;
		//area is a circle (round ellipse) and a small exit rectangle
		area = new Area(new Rectangle2D.Double(xloc,yloc, width, rheight));
		area.add(new Area(new Ellipse2D.Double(xloc-((circlewidth-width)/2),yloc+((rheight/3)*2),circlewidth,circleheight)));
		color = Color.red;
		theta = 0;
		offsetangle = 180;
		currentrotation = 0;
		at = new AffineTransform();
		exit = null;
		standardorientation = orientation;
		selected = false;
		this.fm = fm;
		moffset = new Magnet(new Line2D.Double(xloc,yloc,xloc+width,yloc),this,offsetangle,false);
		moffset.setIsHairpinAccessible(false);
		largerthanshown = 0;
		isstartelement = false;
		isendelement = false;
		startangle = -1;
		endangle = -1;
		rotations = new Vector<Rotation>();
		switchforced = false;
		editflag = false;
	}
	
	/**
	 * This method is needed for initializing a stored hairpin
	 */
	private void initializeHairpin(){
		//area is a circle (round ellipse) and a small exit rectangle
		area = new Area(new Rectangle2D.Double(xloc,yloc, width, rheight));
		area.add(new Area(new Ellipse2D.Double(xloc-((circlewidth-width)/2),yloc+((rheight/3)*2),circlewidth,circleheight)));
		adjustPath();
		color = Color.red;
		selected = false;
		largerthanshown = 0;
		this.bb = hairpin;
		setMotifHead();
	}
	
	/**
	 * Getter method for the type of the building block
	 * 
	 * @return EditorGui.HAIRPIN_TYPE
	 */
	public int getType(){
		return EditorGui.HAIRPIN_TYPE;
	}
	
	/**
	 * Writes the object to an ObjectOutputStream
	 *
	 * @param s, the ObjectOutputStream
	 */
	private void writeObject(ObjectOutputStream s) throws IOException{
		s.writeDouble(xloc);
		s.writeDouble(yloc);
		s.writeDouble(width);
		s.writeDouble(rheight);
		s.writeDouble(circlewidth);
		s.writeDouble(circleheight);
		s.writeObject(hairpin);
		s.writeInt(theta);
		s.writeInt(offsetangle);
		s.writeInt(currentrotation);
		s.writeObject(at);
		s.writeObject(exit);
		s.writeBoolean(standardorientation);	
		s.writeObject(moffset);
		s.writeObject(fm);
		s.writeBoolean(isstartelement);
		s.writeBoolean(isendelement);
		s.writeInt(startangle);
		s.writeInt(endangle);
		s.writeObject(rotations);
		s.writeBoolean(switchforced);
		s.writeInt(largerthanshown);
		s.writeInt(startindex);
	}
	
	
	/**
	 * Reads the object form an ObjectInputStream
	 *
	 * @param s, the ObjectInputStream
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream s) throws IOException,ClassNotFoundException{
		xloc = s.readDouble();
		yloc = s.readDouble();
		width = s.readDouble();
		rheight = s.readDouble();
		circlewidth = s.readDouble();
		circleheight = s.readDouble();
		hairpin = (HairpinLoop) s.readObject();
		theta = s.readInt();
		offsetangle = s.readInt();
		currentrotation = s.readInt();
		at = (AffineTransform) s.readObject();
		exit = (RnaShape) s.readObject();
		standardorientation = s.readBoolean();
		moffset = (Magnet) s.readObject();
		fm = (FreeMagnets) s.readObject();
		isstartelement = s.readBoolean();
		isendelement = s.readBoolean();
		startangle = s.readInt();
		endangle = s.readInt();
		rotations = (Vector<Rotation>) s.readObject();
		switchforced = s.readBoolean();
		largerthanshown = s.readInt();
		startindex = s.readInt();
		initializeHairpin();
	}
	/**
	 * This method gives the internally stored HairpinLoop
	 * 
	 * @return the HairpinLoop of the shape
	 */
	public HairpinLoop getHairpin(){
		return this.hairpin;
	}
	
	/**
	 * This method returns all exits of the shape as a Magnet Vector
	 * 
	 * @return Vector containing all Magnets of this shape
	 */
	public Vector<Magnet> getBorders(){
		Vector<Magnet> borders = new Vector<Magnet>();
		borders.add(moffset);
		return borders;
	}

	/**
	 * This method returns all free Magnets of this shape, i.e. not m
	 *
	 * @param m, the Magnet that is not to be returned in the Vector
	 * @return Vector containing all free Magnets of this shape
	 */
	public Vector<Magnet> getBorders(Magnet m){
		Vector<Magnet> borders = new Vector<Magnet>();
		return borders;
	}
	
	/**
	 * Changes the orientation of the shape
	 * 
	 * @param seqreversal determines whether stored sequence motifs shall be reversed
	 */
	public void changeOrientation(boolean seqreversal){
		if(standardorientation){
			standardorientation = false;
		}
		else{
			standardorientation = true;
		}
		if(exit != null && exit.getType() == EditorGui.SS_DOUBLE){
			exit.changeOrientation(seqreversal);
		}
        this.hairpin.switchSides();
		if(seqreversal){
			this.hairpin.reverseStrings();
		}
	}  
	
	/**
	 * This method changes the size of the Bulge, i.e. changes its Area
	 *
	 * @param seglength, the sequence length upon which the size depends
	 */
	public void changeSize(int seqlength){
		double seqsize = ((seqlength - 5)*2.5) + 80;
		circlewidth = seqsize;
		circleheight = seqsize;
		area = new Area(new Rectangle2D.Double(xloc,yloc, width, rheight));
		adjustPath();
		area.add(new Area(new Ellipse2D.Double(xloc-((circlewidth-width)/2),yloc+((rheight/3)*2),circlewidth,circleheight)));
		adjustMagnets();
	}
	
	/**
	 * This method changes the size of the Bulge, i.e. changes its Area
	 * according to a median value for the sequence length
	 *
	 * @param minlength, the minimum sequence length
	 * @param maxlength, the maximum sequence length
	 */
	public void changeSize(int minlength, int maxlength){
		double middle = (((((double)(minlength + maxlength))/2)-5)*2.5)+80;
		circlewidth = (double) middle;
		circleheight = (double) middle;
		area = new Area(new Rectangle2D.Double(xloc,yloc, width, rheight));
		adjustPath();
		area.add(new Area(new Ellipse2D.Double(xloc-((circlewidth-width)/2),yloc+((rheight/3)*2),circlewidth,circleheight)));
		adjustMagnets();
	}
	
	/**
	 * This method implements a change in location to a new point of origin
	 *
	 * @param x, the x-coordinate of the new origin
	 * @param y, the y-coordinate of the new origin
	 */
	public void changeLocation(double x, double y){
		xloc = (double)x;
		yloc = (double)y; 
		area = new Area(new Rectangle2D.Double(xloc,yloc, width, rheight));
		adjustPath();
		area.add(new Area(new Ellipse2D.Double(xloc-((circlewidth-width)/2),yloc+((rheight/3)*2),circlewidth,circleheight)));
		if(currentrotation != 0){
			at.setToIdentity();
			at.rotate(StrictMath.toRadians(currentrotation),xloc,yloc);
			rotations.clear();
			rotations.add(new Rotation(currentrotation, 1));
		}
		adjustMagnets();
	}
	
	//not needed for Hairpin
	public void changeLocation(double x, double y, boolean bottom){}
	
	/**
	 * This method changes the location according to a transformation
	 * stored in the AffineTransform
	 *
	 * @param AffineTransform stores a move transform that the hairpin should
	 * make
	 */
	public void changeLocation(AffineTransform movetransform){
		Point2D.Double movepoint = new Point2D.Double(xloc,yloc);
		movetransform.transform(movepoint,movepoint);
		xloc = movepoint.getX();
		yloc = movepoint.getY();
		area = new Area(new Rectangle2D.Double(xloc,yloc, width, rheight));
		adjustPath();
		area.add(new Area(new Ellipse2D.Double(xloc-((circlewidth-width)/2),yloc+((rheight/3)*2),circlewidth,circleheight)));
		if(!rotations.isEmpty()){
			at.setToIdentity();
			for(Rotation r : rotations){
				switch(r.getType()){
				case 1: 
					at.rotate(StrictMath.toRadians(r.getAngle()),xloc,yloc);
					break;
				case 3: 
					at.rotate(StrictMath.toRadians(theta),xloc-((circlewidth-width)/2+(circlewidth/2)),yloc+((rheight/3)*2)+(circleheight/2));
					break;
				case 4: 
					Point2D rotpoint = new Point2D.Double(r.getXMiddle(),r.getYMiddle());
					movetransform.transform(rotpoint,rotpoint);
					r.setNewMiddle(rotpoint);
					at.rotate(StrictMath.toRadians(r.getAngle()),rotpoint.getX(),rotpoint.getY()); 
					break;
				}
			}
		}
		adjustMagnets();
		if(isstartelement){
			setMotifHead();
		}
	}

	/**
	 * adjust the Magnets according to a location or size change
	 */
	public void adjustMagnets(){	
		Line2D.Double line = new Line2D.Double(xloc,yloc,xloc+width,yloc);
		Point2D p1 = line.getP1();
		Point2D p2 = line.getP2();
		at.transform(p1,p1);
		at.transform(p2,p2);
		if(fm.contains(moffset)){
			fm.remove(moffset);
			moffset = new Magnet(new Line2D.Double(p1,p2),this,(theta+offsetangle)%360,false);
			fm.add(moffset);
		}
		else if(exit != null && exit.getType() == EditorGui.SS_DOUBLE){
			fm.adjustMagnet(moffset, new Magnet(new Line2D.Double(p1,p2),this,(theta+offsetangle)%360,false));
			moffset = new Magnet(new Line2D.Double(p1,p2),this,(theta+offsetangle)%360,false);
		}
		else{
			moffset = new Magnet(new Line2D.Double(p1,p2),this,(theta+offsetangle)%360,false);
		}
	}
	
	/**
	 * Method for adjusting magnet accessibility
	 * 
	 * @param accessible determines whether the buildin block is hairpin accessible
	 */
	public void adjustMagnetAccessability(boolean accessible){
		moffset.setIsHairpinAccessible(false);
	}

	/**
	 * Method for traversing to check for hairpin accessibility.
	 * Traversal ends here
	 * 
	 * @param angle describing the direction of the traversal
	 */
	public void hairpinAccessTraversal(int angle){
	}

	/** 
	 * Method for starting a hairpin access traversal
	 * 
	 * @param angle describing the direction of the traversal
	 */
	public void startHPAccessTraversal(int angle){
		this.exit.hairpinAccessTraversal(angle);
	}

	/**
	 * Method for a hairpin access traversal after a multiloop was visited
	 * 
	 * @param angle describing the direction of the traversal
	 * @param openmagnets storing all available magnets of this motif part
	 */
	public void hairpinAccessTraversal(int angle, Vector<Magnet> openmagnets){
	}
	
	/**
	 * This method adjust the GeneralPath containing the squiggle plot
	 * according to changes in size, location or other settings
	 */
	private void adjustPath(){
		rnapath = new GeneralPath();
		float x = (float) xloc;
		float y = (float) yloc;
		float w = (float) width;
		float rh = (float) rheight;
		float ch = (float) circleheight;
		float cw = (float) circlewidth;
		
		float distangle = (float) Math.toDegrees(Math.asin((w/4)/(cw/2)));
		distangle += 5;
		
		Arc2D.Float innercircle = new Arc2D.Float(x -((cw-w)/2) + 10,y+((rh/3)*2)+10,cw-20,ch-20,90 + 18,360 - 36,Arc2D.OPEN);
		
		
		rnapath.moveTo(x + (w/4), y);
		rnapath.lineTo(x + (w/4), y + rh + 5);
		rnapath.append(innercircle,true);
		
		rnapath.lineTo(x + w - (w/4), y + rh + 5);
		rnapath.lineTo(x + w - (w/4), y);
		rnapath.moveTo(x + (w/4), y + rh/2+5);
		rnapath.lineTo(x + w - (w/4), y + rh/2+5);
		
	}
	
	
	/**
	 * This method implements a snapping mechanism which takes place if 
	 * the shape is close to a free Magnet
	 *
	 * @param m, the Magnet to which the shape snaps
	 */
	public void snapTo(Magnet m){
		Line2D.Double line = m.getLine();
		double x2 = line.getX2();
		double y2 = line.getY2();
		//the angles fit together at theta+offsetangle's side
		if(m.getAngle()==theta){
			changeLocation(x2,y2);
		}
		//the angles fit together at theta's side, snapping is necessary
		else if(currentrotation == 0){
			changeLocation(x2,y2);
			at.rotate(StrictMath.toRadians(m.getAngle()),xloc,yloc);
			rotations.add(new Rotation(m.getAngle(),1));
			theta = StrictMath.abs(m.getAngle());
			adjustMagnets();
		}
	}

	/**
	 * Restores the affine transformation to identity, 
	 * i.e. all rotations are undone
	 */  	
	public void restore(){
			at.setToIdentity();
			rotations.clear();
			if(currentrotation!=0){
				at.rotate(StrictMath.toRadians(currentrotation),xloc-((circlewidth-width)/2+(circlewidth/2)),yloc+((rheight/3)*2)+(circleheight/2));
				rotations.add(new Rotation(currentrotation,3));
			}
			theta = currentrotation;
			adjustMagnets();
	}
	
	/**
	 * This method rotates our Shape around the center of the shape
	 *
	 * @param int degree, the angle of rotation
	 */
	public void rotateBy(int degree){
		at.rotate(StrictMath.toRadians(degree),xloc-((circlewidth-width)/2+(circlewidth/2)),yloc+((rheight/3)*2)+(circleheight/2));
		rotations.add(new Rotation(degree,3));
		theta += degree;
		theta = theta%360;
		currentrotation += degree;
		currentrotation = currentrotation%360;
		adjustMagnets();
	}
	
	/**
	 * This method rotates our Shape around a given point
	 *
	 * @param int degree, the angle of rotation
	 * @param double xmiddle, the x coordinate of the rotation point
	 * @param double ymiddle, the y coordinate of the rotation point
	 */
	public void rotateBy(int degree, double xmiddle, double ymiddle){
		AffineTransform buf = new AffineTransform(at);
		at.setToIdentity();
		at.rotate(StrictMath.toRadians(degree),xmiddle,ymiddle);
		rotations.add(0,new Rotation(degree,xmiddle,ymiddle));
		at.concatenate(buf);
		theta += degree;
		theta = theta %360;
		if(isstartelement){
			startangle += degree;
			startangle = startangle %360;
		}
		if(isendelement){
			endangle += degree;
			endangle = endangle % 360;
		}
		adjustMagnets();
	}
	
	/**
	 * This method returns the Magnet of the start exit of this shape
	 *
	 * @return Magnet, the start exit of this shape
	 */
	public Magnet getStartExit(){
		return moffset;
	}

	/**
	 * Traversal method which proceeds until a new open end is found.
	 * Called if the current main open end is closed by a hairpin
	 *
	 * @param int angle, the direction of the traversal
	 * @return RnaShape this or result of a recursive call
	 */
	public RnaShape traverseToNewStart(int angle, boolean real){
		if(real){
			switchsides();
		}
		if(exit != null && exit.getType() == EditorGui.SS_DOUBLE){
			SingleShape next = ((DoubleSingleShape)exit).getFivePrime();
			if(next == null){
				setIsStartElement(true, (theta+offsetangle)%360, 0);
				return this;
			}
			return next.traverseToNewStart(angle, real);
		}
		return null;
	}

	/**
	 * This method finds the new start element starting from the next exit
	 * specified by the given index. This method should not be called for a Hairpin.
	 *
	 * @param int index, the index from where the start
	 * @return RnaShape, the new start element
	 */	
	public RnaShape findNewStartElement(int index){
		return null; //should not be called!!!
	}
	
	/**
	 * This method returns the Magnet of the end exit of this shape
	 *
	 * @return Magnet, the end exit of this shape
	 */
	public Magnet getEndExit(){
		return moffset;
	}

	/**
	 * Traversal method which proceeds until a new open end is found.
	 * Called if the current main open end is closed by a hairpin
	 *
	 * @param int angle, the direction of the traversal
	 * @return RnaShape this or result of a recursive call
	 */
	public RnaShape traverseToNewEnd(int angle){
		if(exit != null && exit.getType() == EditorGui.SS_DOUBLE){
			SingleShape next = ((DoubleSingleShape)exit).getThreePrime();
			if(next == null){
				setIsEndElement(true, (theta+offsetangle)%360, 0);
				return this;
			}
			return next.traverseToNewEnd(angle);
		}
		return null;
	}

	/**
	 * This method finds the new end element ending from the next exit
	 * specified by the given index. This method should not be called for a Hairpin.
	 *
	 * @param int index, the index from where the end
	 * @return RnaShape, the new end element
	 */	
	public RnaShape findNewEndElement(int index){
		return null;
	}

	/**
	 * This method checks wether the Hairpin is at the end of the structure.
	 * 
	 * @return true, if it has an open exit, else false
	 */
	public int getEndType(){
		if(exit == null) return 1;
		else if(exit.getType() != EditorGui.SS_DOUBLE) return 0;
		else return -1;
	}
	
	/**
	 * Method for finding an open end connected to this building block
	 * 
	 * @param angle describing the direction of the traversal
	 * @return true, if the exit is null; else false
	 */
	public boolean findOpenEnd(int angle){
		if(exit == null) return true;
		else return false;
	}
	
	/**
	 * Method for traversing to a compound building block
	 * 
	 * @param angle describing the direction of the traversal
	 * @return false since the traversal ends here and it is not a compound block
	 */
	public boolean traverseToClosed(int angle){
		return false;
	}
	
	/**
	 * Adds a neighboring shape to this one
	 * 
	 * @param shape, the shape that is added as a neighbor
	 * @param angle, the angle of the exit, where the shape is neighboring
	 * @param type is only relevant for SingleShape
	 */
	public void addToNeighbors(RnaShape shape, int angle, int type){
		exit = shape;
	}
	
	/**
	 * Adds a neighboring SingleShape to this one
	 * 
	 * @param shape, the SingleShape that is added as a neighbor
	 * @param angle, the angle of the exit, where the shape is neighboring
	 * @return 1 if ss added to 5' end; 2 if added to 3' end
	 */
	public int addSSNeighbor(SingleShape shape, int angle){
		if(this.isstartelement || this.isendelement){
			if(exit == null){
				DoubleSingleShape dss = new DoubleSingleShape();
				Point2D mp1 = new Point2D.Double(xloc,yloc);
				Point2D mp1trans = new Point2D.Double();
				at.transform(mp1,mp1trans);
				double realx = mp1trans.getX();
				double realy = mp1trans.getY();
				if(standardorientation){
					if(shape.contains(realx,realy)){
						dss.addThreePrime(shape);
						exit = dss;
						shape.addFivePrimeDSS(dss);
						return 2;
					}
					else{
						dss.addFivePrime(shape);
						exit = dss;
						shape.addThreePrimeDSS(dss);
						return 1;
					}
				}
				else{
					if(shape.contains(realx,realy)){
						dss.addFivePrime(shape);
						exit = dss;
						shape.addThreePrimeDSS(dss);
						return 1;
					}
					else{
						dss.addThreePrime(shape);
						exit = dss;
						shape.addFivePrimeDSS(dss);
						return 2;
					}
				}
			}
			else{
				if(((DoubleSingleShape)exit).getOpenFivePrime()){
					((DoubleSingleShape)exit).addFivePrime(shape);
					shape.addThreePrimeDSS((DoubleSingleShape)exit);
					return 1;
				}
				else{
					((DoubleSingleShape)exit).addThreePrime(shape);
					shape.addFivePrimeDSS((DoubleSingleShape)exit);
					return 2;
				}
			}
		}
		else if(shape.getIsConnected()){
			//shape ist 3' ende (haengt an 3' position)
			if(shape.getIsFivePrimeDSSAvailable()){
				if(exit == null){
					DoubleSingleShape dss = new DoubleSingleShape();
					dss.addFivePrime(shape);
					exit = dss;
					shape.addThreePrimeDSS(dss);
				}
				else{
					((DoubleSingleShape)exit).addFivePrime(shape);
					shape.addThreePrimeDSS((DoubleSingleShape)exit);
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
			//shape ist 5' ende (haengt an 5' position)
			else{
				if(exit == null){
					DoubleSingleShape dss = new DoubleSingleShape();
					dss.addThreePrime(shape);
					exit = dss;
					shape.addFivePrimeDSS(dss);
				}
				else{
					((DoubleSingleShape)exit).addThreePrime(shape);
					shape.addFivePrimeDSS((DoubleSingleShape)exit);
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
		}
		else{
			System.out.println("AddSSNeighbor in HP: Should not be needed");
		}
		return 0;
	}
	
	/**
	 * This method removes all neighbors of this shape
	 */
	public void removeNeighbors(){
		if(exit != null){
			exit.remove(this);
			exit.adjustMagnetAccessability(true);
			exit = null;
		}
	}

	/**
	 * Resets hairpin accessibility: hairpin is not accessible!
	 */
	public void resetHPAccessability(){
		moffset.setIsHairpinAccessible(false);
	}
	
	/**
	 * This method removes a specific RnaShape from the neighbors of this
	 * shape
	 *
	 * @param RnaShape s, the shape to be removed
	 */
	public void remove(RnaShape s){
		if(s.getType() != EditorGui.SINGLE_TYPE){
			if(exit == s){
				fm.add(moffset);
				exit = null;
			}
		}
		else{
			if(exit != null && exit.getType() == EditorGui.SS_DOUBLE && ((DoubleSingleShape)exit).contains(s)){
				((DoubleSingleShape)exit).remove(s);
				if(((DoubleSingleShape)exit).isEmpty()){
					exit = null;
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
		}
	}
	
	/**
	 * This method checks wether there is a DoubleSingleStrand at exit of the given index
	 *
	 * @param index, the index where a DSS is searched for
	 * @return true, if it has a DSS Neighbor, else false.
	 */		
	public boolean hasDSSNeighbor(int index){
		if(exit != null && exit.getType() == EditorGui.SS_DOUBLE){
			return true;
		}
		return false;
	}
	
	/**
	 * This method checks wether the Bulge is connected to the given shape at the given angle
	 *
	 * @param shape, the shape
	 * @param angle, the location
	 */
	public boolean connectedTo(RnaShape shape, int angle, RnaShape origin){
		boolean found = false;
		if(this.exit != null){
			if(exit == shape){
				return true;
			}
			else if(this == origin){
				return exit.connectedTo(shape, theta+offsetangle, this);
			}
			else{
				return false;
			}
		}
		return found;
	}

	public boolean startFindSS(){
		if(exit != null){
			return exit.findSS(theta); //oder anderer Winkel???
		}
		else return false;
	}
	
	/**
	 * This method should not be called for a Hairpin. It checks for a Single Strand
	 * in the Rna Structure.
	 * 
	 * @param angle indicating the direction of the search.
	 * @return false.
	 */
	public boolean findSS(int angle){
		return false;
	}

	/**
	 * Method for adjusting both strings according to a traversal 
	 * for a new start. Not needed for Hairpin.
	 */
	public void switchsides(){
	    this.hairpin.switchSides();
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
		this.hairpin.switchSides();
		this.hairpin.reverseStrings();
		if(start != null){
			start.switchSidesTraversal(null, fiveprimedir, -1);
		}
	}
	
	
	/**
	 * The starting method for traversing an rna structure. 
	 * The return String is initialized here and subsequent methods are called.
	 *
	 * @param type representing the language to be returned.
	 * @return String containing the shape notation for the whole structure
	 */
	public String beginTraversal(int type){
		String shapestring = Translator.getLeftContent(this.hairpin, type,-1);
		if(!this.getIsEndElement() && this.exit != null && exit.getType() == EditorGui.SS_DOUBLE){
			SingleShape next = ((DoubleSingleShape)exit).getThreePrime();
			shapestring += next.traverse(startangle, type);
		}
		return shapestring;
	}

	/**
	 * The subsequent method during a traversal.
	 * The return String is complemented here and subsequent methods are called
	 *
	 * @param type representing the language to be returned.
	 * @param angle the angle where the previous shape came from
	 */
	protected String traverse(int angle, int type){
		return Translator.getLeftContent(this.hairpin, type,-1);
	}

	/**
	 * Continues a change that was invoked by another RnaShape.
	 * The recursion ends at the hairpin element.
	 * 
	 * @param angle the direction of the shift
	 */
	public void traverseShift(int angle){
	}
	
	/**
	 * traverseshift based on affinetransform: not needed for hairpin shape:
	 * traversal ends here
	 */
	public void traverseShift(AffineTransform movetransform, int angle){}
	
	/**
	 * The starting method for traversing an rna structure. 
	 * The return String is initialized here and subsequent methods are called.
	 *
	 * @return String containing the shape notation for the whole structure
	 */
	public String currentForwardTraversal(){
		String shapestring = Translator.getLeftContent(this.hairpin, Translator.SHAPE_LANG,-1);
		if(!this.getIsEndElement() && this.exit != null && exit.getType() == EditorGui.SS_DOUBLE){
			SingleShape next = ((DoubleSingleShape)exit).getThreePrime();
			shapestring += next.ctraverseF(startangle);
		}
		return shapestring;
	}
	
	
	/**
	 * The subsequent method during a traversal.
	 * The return String is complemented here and subsequent methods are called
	 *
	 * @return the String containing the shape notation.
	 * @param angle the angle where the previous shape came from
	 */
	protected String ctraverseF(int angle){
		return Translator.getLeftContent(this.hairpin, Translator.SHAPE_LANG,-1);
	}
	
	
	
	/**
	 * Method to open the edit window of this shape
	 *
	 * @param ds the parent DrawingSurface
	 * @param type determines which tab is shown at first
	 */
	public void openEditWindow(DrawingSurface ds, int type){
	    if(!editflag){
	        editflag = true;
	        new HairpinEdit(this,ds,type);
	    }
	}
	
	
	/**
	 * Method for setting the motif head to this building block
	 */
	public void setMotifHead(){
		if(exit != null && exit.getType() == EditorGui.SS_DOUBLE && ((DoubleSingleShape)exit).getThreePrime() != null){
			Point2D.Double transXY = new Point2D.Double(xloc,yloc);
			at.transform(transXY,transXY);
			if((((DoubleSingleShape)exit).getThreePrime()).endContains(transXY)){
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
	
	/**
	 * This method does the actual drawing of this shape's Area
	 *
	 * @param g2, the Graphics2D object that performs the drawing
	 * @param fixed, boolean value describing wether the shape is part of 
	 * the current RnaStructure or not
	 */
	public void show(Graphics2D g2, boolean fixed){
		//fixed=true, color is opaque
		if(fixed){
			color = new Color(205,0,0,50);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		//fixed=false, color is transparent
		else{
			color = new Color(205,0,0,30);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		AffineTransform oldtransf = g2.getTransform();
		g2.transform(at); //do possible rotation
		
		g2.setPaint(color);
		g2.fill(area);
		

		g2.setPaint(Color.lightGray);
		g2.draw(area);
		
		g2.setPaint(Color.black);
		g2.draw(rnapath);
		
		
		//show the current orientation
		
		if(isstartelement){
			g2.setPaint(Color.RED);
			g2.draw(motifheadarea);
			g2.setPaint(Color.black);
			if(exit != null){
				Point2D.Double transXY = new Point2D.Double(xloc,yloc);
				at.transform(transXY,transXY);
				if((((DoubleSingleShape)exit).getThreePrime()).endContains(transXY)){
					g2.drawString("5'",(float)xloc+28,(float)yloc-15);
					g2.drawLine((int)(xloc+width-(width/4)),(int)yloc-1,(int)(xloc+width-(width/4)),(int)yloc-10);
				}
				else{
					g2.drawString("5'",(float)xloc+3,(float)yloc-15);
					g2.drawLine((int)(xloc+(width/4)),(int)yloc-1,(int)(xloc+(width/4)),(int)yloc-10);
				}
			}
			else{
				if(standardorientation){
					g2.drawString("5'",(float)xloc+28,(float)yloc-15);
					g2.drawLine((int)(xloc+width-(width/4)),(int)yloc-1,(int)(xloc+width-(width/4)),(int)yloc-10);
				}
				else{
					g2.drawString("5'",(float)xloc+3,(float)yloc-15);
					g2.drawLine((int)(xloc+(width/4)),(int)yloc-1,(int)(xloc+(width/4)),(int)yloc-10);
				}
			}
		}
		if(isendelement){
			if(exit != null){
				Point2D.Double transXY = new Point2D.Double(xloc,yloc);
				at.transform(transXY,transXY);
				if((((DoubleSingleShape)exit).getFivePrime()).endContains(transXY)){
					g2.drawString("3'",(float)xloc+28,(float)yloc-15);
					g2.drawLine((int)(xloc+width-(width/4)),(int)yloc-1,(int)(xloc+width-(width/4)),(int)yloc-10);
				}
				else{
					g2.drawString("3'",(float)xloc+3,(float)yloc-15);
					g2.drawLine((int)(xloc+(width/4)),(int)yloc-1,(int)(xloc+(width/4)),(int)yloc-10);
				}
			}
			else{
				if(standardorientation){
					g2.drawString("3'",(float)xloc+3,(float)yloc-15);
					g2.drawLine((int)(xloc+(width/4)),(int)yloc-1,(int)(xloc+(width/4)),(int)yloc-10);
				}
				else{
					g2.drawString("3'",(float)xloc+28,(float)yloc-15);
					g2.drawLine((int)(xloc+width-(width/4)),(int)yloc-1,(int)(xloc+width-(width/4)),(int)yloc-10);
				}
			}
		}
		
		g2.setTransform(oldtransf);
	}
}


