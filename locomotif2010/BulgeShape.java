package rnaeditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
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
 * This class implements a BulgeShape which represents a Bulge graphically
 *
 * @author Janina Reeder
 */
public class BulgeShape extends RnaShape implements Serializable {
	private static final long serialVersionUID = -2264250769326386088L;
	private Bulge bulge; //the underlying data structure
	private double upperseg; //distance to begin of bulge from top (xloc)
	private double loopwidth;
	private double curvepoint; //point to which the bulge curves
	private int largerthanshown;
	private boolean rightbulge;
	
	/**
	 * Constructor for a bulge shape
	 * 
	 * @param orientation, true = standard, false = reverse
	 * @param fm, the FreeMagnets of the current RnaStructure
	 */
	public BulgeShape(boolean orientation, FreeMagnets fm){
		xloc = 0;
		yloc = 0;
		width = 40;
		height = 80;
		upperseg = 15;
		loopwidth = 65;
		curvepoint = 50;
		bulge = new Bulge(orientation); 
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		area.add(new Area(new Arc2D.Double(xloc - (loopwidth - 80)/2-1,yloc+upperseg,loopwidth,curvepoint,270,180,Arc2D.OPEN)));
		rnapath = new GeneralPath();
		color = Color.green;
		theta = 0;
		offsetangle = 180;
		currentrotation = 0;
		at = new AffineTransform();
		exits = new RnaShape[2];
		exits[0] = null;
		exits[1] = null;
		standardorientation = orientation;
		selected = false;
		mtheta = new Magnet(new Line2D.Double(xloc+width,yloc+height,xloc,yloc+height),this,theta, true);
		moffset = new Magnet(new Line2D.Double(xloc,yloc,xloc+width,yloc),this,offsetangle, true);
		this.fm = fm;
		largerthanshown=0;
		isstartelement = false;
		isendelement = false;
		startangle = -1;
		endangle = -1;
		rightbulge = orientation;
		rotations = new Vector<Rotation>();
		switchforced = false;
		bb = this.bulge;
	}
	
	/**
	 * This method is needed for initializing a stored bulge
	 */
	private void initializeBulge(){
		curvepoint = 50;
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		if((rightbulge && startindex == 0) || (!rightbulge && startindex == 1)){
			area.add(new Area(new Arc2D.Double(xloc - (loopwidth - 80)/2-1,yloc+upperseg,loopwidth,curvepoint,270,180,Arc2D.OPEN)));
		}
		else{
			area.add(new Area(new Arc2D.Double(xloc-(loopwidth/2)+1,yloc+upperseg,loopwidth,curvepoint,90,180,Arc2D.OPEN)));
		}
		adjustPath();
		color = Color.green;
		selected = false;
		this.bb = bulge;
		setMotifHead();
	}
	
	public int getType(){
		return EditorGui.BULGE_TYPE;
	}
	
	/**
	 * Writes the object to an ObjectOutputStream
	 * @param s, the ObjectOutputStream
	 */
	private void writeObject(ObjectOutputStream s) throws IOException{
		s.writeDouble(xloc);
		s.writeDouble(yloc);
		s.writeDouble(width);
		s.writeDouble(height);
		s.writeDouble(upperseg);
		s.writeDouble(loopwidth);
		s.writeObject(bulge);
		s.writeInt(theta);
		s.writeInt(offsetangle);
		s.writeInt(currentrotation);
		s.writeObject(at);
		s.writeObject(exits);
		s.writeBoolean(standardorientation);
		s.writeObject(mtheta);
		s.writeObject(moffset);
		s.writeObject(fm);
		s.writeBoolean(isstartelement);
		s.writeBoolean(isendelement);
		s.writeInt(startangle);
		s.writeInt(endangle);
		s.writeObject(rotations);
		s.writeBoolean(switchforced);
		s.writeInt(largerthanshown);
		s.writeBoolean(rightbulge);
		s.writeInt(startindex);
	}
	
	/**
	 * Reads the object form an ObjectInputStream
	 * @param s, the ObjectInputStream
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream s) throws IOException,ClassNotFoundException{
		xloc = s.readDouble();
		yloc = s.readDouble();
		width = s.readDouble();
		height = s.readDouble();
		upperseg = s.readDouble();
		loopwidth = s.readDouble();
		bulge = (Bulge) s.readObject();
		theta = s.readInt();
		offsetangle = s.readInt();
		currentrotation = s.readInt();
		at = (AffineTransform) s.readObject();
		exits = (RnaShape[]) s.readObject();
		standardorientation = s.readBoolean();
		mtheta = (Magnet) s.readObject();
		moffset = (Magnet) s.readObject();
		fm = (FreeMagnets) s.readObject();
		isstartelement = s.readBoolean();
		isendelement = s.readBoolean();
		startangle = s.readInt();
		endangle = s.readInt();
		rotations = (Vector<Rotation>) s.readObject();
		switchforced = s.readBoolean();
		largerthanshown = s.readInt();
		rightbulge = s.readBoolean();
		startindex = s.readInt();
		initializeBulge();
	}

	/**
	 * @return the Bulge of the shape
	 */
	public Bulge getBulge(){
		return this.bulge;
	}
	
	/**
	 * changes the orientation of the shape
	 */
	public void changeOrientation(boolean seqreversal){
		if(standardorientation){
			standardorientation = false;
		}
		else{
			standardorientation = true;
		}
		bulge.changeBulgeLoc();
		bulge.setOrientation(standardorientation);
		if(exits[0] != null && exits[0].getType() == EditorGui.SS_DOUBLE){
			exits[0].changeOrientation(seqreversal);
		}
		if(exits[1] != null && exits[1].getType() == EditorGui.SS_DOUBLE){
			exits[1].changeOrientation(seqreversal);
		}
        this.bulge.switchSides();
		if(seqreversal){
			this.bulge.reverseStrings();
		}
	}
	
	/**
	 * Method for redrawing a bulge using the changeLocation method with the current parameters.
	 * Called during traversal to new start
	 *
	 */
	public void redraw(){
		changeLocation(xloc,yloc);		
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
		float h = (float) height;
		float us = (float) upperseg;
		float lw = (float) loopwidth;
		float cp = (float) curvepoint;
		
		if((rightbulge && startindex == 0) || (!rightbulge && startindex == 1)){
			//straight line
			rnapath.moveTo(x + (w/4), y);
			rnapath.lineTo(x + (w/4), y + h);
			//bulge side
			rnapath.moveTo(x + w - (w/4), y + h);
			rnapath.lineTo(x + w - (w/4), y + h - (us+5));
			rnapath.quadTo(x - (lw - 80)/2 - 1 + lw, y + us + cp/2, x + w - (w/4), y + (us+5));
			rnapath.lineTo(x + w - (w/4), y);
			//basepair connections
			rnapath.moveTo(x + (w/4), y + us/2 + 2);
			rnapath.lineTo(x + w - (w/4), y + us/2 + 2);
			rnapath.moveTo(x + (w/4), y + h - us/2 - 2);
			rnapath.lineTo(x + w - (w/4), y + h - us/2 - 2);
		}
		else{
			//straight line
			rnapath.moveTo(x + w - (w/4), y);
			rnapath.lineTo(x + w - (w/4), y + h);
			//bulge side
			rnapath.moveTo(x + (w/4), y + h);
			rnapath.lineTo(x + (w/4), y + h - (us+5));
			rnapath.quadTo(x - (lw/2) + 1, y + us + cp/2, x + (w/4), y + (us+5));
			rnapath.lineTo(x + (w/4), y);
			//basepair connections
			rnapath.moveTo(x + (w/4), y + us/2 + 2);
			rnapath.lineTo(x + w - (w/4), y + us/2 + 2);
			rnapath.moveTo(x + (w/4), y + h - us/2 - 2);
			rnapath.lineTo(x + w - (w/4), y + h - us/2 - 2);
		}
	}
	
	
	/**
	 * Method for setting this element as a start element according to the given parameter
	 * 
	 * @param isstartelement, true if this is the start element; else false
	 * @param angle, the start angle of the building block
	 */
	public void setIsStartElement(boolean isstartelement, int angle){
		this.isstartelement = isstartelement;
		startangle = angle;
		if(startangle == (theta+offsetangle)%360){
			startindex = 0;
		}
		else if(startangle == theta){
			startindex = 1;
			changeBulgeLoc();
			switchsides();
		}
		if(isstartelement){
			setMotifHead();
		}
	}
	
	
	/**
	 * changes the location of the Bulge
	 */
	public void changeBulgeLoc(){
		if(rightbulge){
			rightbulge = false;
		}
		else{
			rightbulge = true;
		}
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		adjustPath();
		if((rightbulge && startindex == 0) || (!rightbulge && startindex == 1)){
			area.add(new Area(new Arc2D.Double(xloc - (loopwidth - 80)/2-1,yloc+upperseg,loopwidth,curvepoint,270,180,Arc2D.OPEN)));
		}
		else{
			area.add(new Area(new Arc2D.Double(xloc-(loopwidth/2)+1,yloc+upperseg,loopwidth,curvepoint,90,180,Arc2D.OPEN)));
		}
	}
	
	/**
	 * This method changes the size of the Bulge, i.e. changes its Area.
	 * Only the bulge loop is drawn larger or smaller accordingly.
	 *
	 * @param seglength, the sequence length upon which the size depends
	 */
	public void changeSize(int seqlength){
		if(seqlength > 20){
			largerthanshown = seqlength - 20;
			seqlength = 20;
		}
		else{
			largerthanshown = 0;
		}
		loopwidth = ((double)seqlength) * 8;
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		adjustPath();
		if((rightbulge && startindex == 0) || (!rightbulge && startindex == 1)){
			area.add(new Area(new Arc2D.Double(xloc - (loopwidth - 80)/2-1,yloc+upperseg,loopwidth,curvepoint,270,180,Arc2D.OPEN)));
		}
		else{
			area.add(new Area(new Arc2D.Double(xloc-(loopwidth/2)+1,yloc+upperseg,loopwidth,curvepoint,90,180,Arc2D.OPEN)));
		}
		adjustMagnets();
	}
	
	/**
	 * This method changes the size of the Bulge, i.e. changes its Area
	 * according to a median value for the sequence length
	 * Only the bulge loop is drawn larger or smaller accordingly.
	 *
	 * @param minlength, the minimum sequence length
	 * @param maxlength, the maximum sequence length
	 */
	public void changeSize(int minlength, int maxlength){
		double middle = ((double)(minlength + maxlength))/2;
		if(middle > 20){
			largerthanshown = (int) (middle - 20);
			middle = 20;
		}
		else{
			largerthanshown = 0;
		}
		loopwidth = (middle) * 8;
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		adjustPath();
		if((rightbulge && startindex == 0) || (!rightbulge && startindex == 1)){
			area.add(new Area(new Arc2D.Double(xloc - (loopwidth - 80)/2-1,yloc+upperseg,loopwidth,curvepoint,270,180,Arc2D.OPEN)));
		}
		else{
			area.add(new Area(new Arc2D.Double(xloc-(loopwidth/2)+1,yloc+upperseg,loopwidth,curvepoint,90,180,Arc2D.OPEN)));
		}
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
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		adjustPath();
		if((rightbulge && startindex == 0) || (!rightbulge && startindex == 1)){
			area.add(new Area(new Arc2D.Double(xloc - (loopwidth - 80)/2-1,yloc+upperseg,loopwidth,curvepoint,270,180,Arc2D.OPEN)));
		}
		else{
			area.add(new Area(new Arc2D.Double(xloc-(loopwidth/2)+1,yloc+upperseg,loopwidth,curvepoint,90,180,Arc2D.OPEN)));
		}
		if(currentrotation != 0){
			at.setToIdentity();
			at.rotate(StrictMath.toRadians(currentrotation),xloc,yloc);
			rotations.clear();
			rotations.add(new Rotation(currentrotation, 1));
		}
		adjustMagnets();
	}
	
	/**
	 * This method changes the location according to a transformation
	 * stored in the AffineTransform
	 *
	 * @param AffineTransform stores a move transform that the bulge should
	 * make
	 */
	public void changeLocation(AffineTransform movetransform){
		Point2D.Double movepoint = new Point2D.Double(xloc,yloc);
		movetransform.transform(movepoint,movepoint);
		xloc = movepoint.getX();
		yloc = movepoint.getY();
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		adjustPath();
		if((rightbulge && startindex == 0) || (!rightbulge && startindex == 1)){
			area.add(new Area(new Arc2D.Double(xloc - (loopwidth - 80)/2-1,yloc+upperseg,loopwidth,curvepoint,270,180,Arc2D.OPEN)));
		}
		else{
			area.add(new Area(new Arc2D.Double(xloc-(loopwidth/2)+1,yloc+upperseg,loopwidth,curvepoint,90,180,Arc2D.OPEN)));
		}
		if(!rotations.isEmpty()){
			at.setToIdentity();
			for( Rotation r : rotations){
				switch(r.getType()){
				case 1: 
					at.rotate(StrictMath.toRadians(r.getAngle()),xloc,yloc);
					break;
				case 2: 
					at.rotate(StrictMath.toRadians(r.getAngle()),xloc,yloc+height);
					break;
				case 3: 
					at.rotate(StrictMath.toRadians(r.getAngle()),xloc+width/2,yloc+height/2);
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
	 * This method changes the location for the snap function if no
	 * rotation is necessary. Depending on wether the element snaps to
	 * the top or bottom, an already existing rotation must be made
	 * around a different rotation point
	 *
	 * @param x, the x-coordinate of the new origin
	 * @param y, the y-coordinate of the new origin
	 * @param bottom, boolean value indicating where the point of rotation
	 *        must be
	 */
	public void changeLocation(double x, double y, boolean bottom){
		xloc = x;
		yloc = y;
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		adjustPath();
		if((rightbulge && startindex == 0) || (!rightbulge && startindex == 1)){
			area.add(new Area(new Arc2D.Double(xloc - (loopwidth - 80)/2-1,yloc+upperseg,loopwidth,curvepoint,270,180,Arc2D.OPEN)));
		}
		else{
			area.add(new Area(new Arc2D.Double(xloc-(loopwidth/2)+1,yloc+upperseg,loopwidth,curvepoint,90,180,Arc2D.OPEN)));
		}
		if(currentrotation != 0){
			at.setToIdentity();
			rotations.clear();
			if(!bottom){
				at.rotate(StrictMath.toRadians(currentrotation),xloc,yloc);
				rotations.add(new Rotation(currentrotation,1));
			}
			else{
				at.rotate(StrictMath.toRadians(currentrotation),xloc,yloc+height);
				rotations.add(new Rotation(currentrotation,2));
			}
		}
		adjustMagnets();
	}
	
	
	

	/**
	 * Method for adjusting both strings and the bulge location according to a traversal 
	 * for a new start
	 */
	public void switchsides(){
		bulge.changeBulgeLoc();
		bulge.switchSides();
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
		if(angle == -1){
			if(exits[0].getType() == EditorGui.SS_DOUBLE){
				exits[0].switchSidesTraversal(null, fiveprimedir, -1);
			}
			else{
				exits[1].switchSidesTraversal(null, fiveprimedir, -1);
			}
		}
		else{
			switchsides();
			this.bulge.reverseStrings();
			//continue in theta's direction
			if(angle == theta){
				if(exits[0] != null){
					exits[0].switchSidesTraversal(start, fiveprimedir,angle);
				}
			}
			//continue in (theta+offsetangle)'s direction
			else if(angle == ((theta+offsetangle)%360)){
				if(exits[1] != null){
					exits[1].switchSidesTraversal(start, fiveprimedir,angle);
				}
			}
		}
	}
	
	
	/**
	 * The starting method for traversing an rna structure. 
	 * The return String is initialized here and subsequent methods are called.
	 *
	 * @param type, the language the String should be: XML or Shapes
	 * @return String containing the shape notation for the whole structure
	 */
	public String beginTraversal(int type){
		String shapestring = "";
		if(startangle == theta){
			shapestring += Translator.getLeftContent(this.bulge, type,-1);
			shapestring += exits[0].traverse(startangle,type);
			shapestring += Translator.getRightContent(this.bulge, type,-1);
			if(!this.getIsEndElement() && exits[1] != null && exits[1].getType() == EditorGui.SS_DOUBLE){
				SingleShape next = ((DoubleSingleShape)exits[1]).getThreePrime();
				shapestring += next.traverse(startangle, type);
			}
		}
		else{ 
			shapestring += Translator.getLeftContent(this.bulge, type,-1);
			shapestring += exits[1].traverse(startangle,type);
			shapestring += Translator.getRightContent(this.bulge, type,-1);
			if(!this.getIsEndElement() && exits[0] != null && exits[0].getType() == EditorGui.SS_DOUBLE){
				SingleShape next = ((DoubleSingleShape)exits[0]).getThreePrime();
				shapestring += next.traverse(startangle, type);
			}
		}
		return shapestring;
	}
	
	/**
	 * The subsequent method during a traversal.
	 * The return String is complemented here and subsequent methods are called
	 *
	 * @param type, the language the String should be: XML or Shapes
	 * @param angle the angle where the previous shape came from
	 */
	protected String traverse(int angle, int type){
		String shapestring = "";
		//continue in theta's direction
		if(angle == theta){
			if(exits[0] != null){
				shapestring += Translator.getLeftContent(this.bulge, type,-1);
				shapestring += exits[0].traverse(angle,type);
				shapestring += Translator.getRightContent(this.bulge, type,-1);
				return shapestring;
			}
			else{
				return "ERROR: BulgeShape.traverse";
			}
		}
		//continue in (theta+offsetangle)'s direction
		else if(angle == ((theta+offsetangle)%360)){
			if(exits[1] != null){
				shapestring += Translator.getLeftContent(this.bulge, type,-1);
				shapestring += exits[1].traverse(angle,type);
				shapestring += Translator.getRightContent(this.bulge, type,-1);
				return shapestring;
			}
			else{
				return "ERROR: BulgeShape.traverse";
			} 
		}
		return "";
	}
	
	/**
	 * The starting method for traversing an rna structure. 
	 * The return String is initialized here and subsequent methods are called.
	 *
	 * @return String containing the shape notation for the whole structure
	 */
	public String currentForwardTraversal(){
		String shapestring = "";
		shapestring += Translator.getLeftContent(this.bulge, Translator.SHAPE_LANG,-1);
		shapestring += " R ";
		shapestring += Translator.getRightContent(this.bulge, Translator.SHAPE_LANG,-1);
		if(startangle == theta){
			if(exits[0] != null){   	
				shapestring = Translator.getLeftContent(this.bulge, Translator.SHAPE_LANG,-1);
				shapestring += exits[0].ctraverseF(startangle);
				shapestring += Translator.getRightContent(this.bulge, Translator.SHAPE_LANG,-1);
			}
			if(exits[1] != null && exits[1].getType() == EditorGui.SS_DOUBLE){
				SingleShape next = ((DoubleSingleShape)exits[1]).getThreePrime();
				if(next != null){
					shapestring += next.ctraverseF(startangle);
				}
			}
		}
		else{
			if(exits[1] != null){    			
				shapestring = Translator.getLeftContent(this.bulge, Translator.SHAPE_LANG,-1);
				shapestring += exits[1].ctraverseF(startangle);
				shapestring += Translator.getRightContent(this.bulge, Translator.SHAPE_LANG,-1);
			}
			if(exits[0] != null && exits[0].getType() == EditorGui.SS_DOUBLE){
				SingleShape next = ((DoubleSingleShape)exits[0]).getThreePrime();
				if(next != null){
					shapestring += next.ctraverseF(startangle);
				}
			}
		}
		return shapestring;
	}
	

	/**
	 * The subsequent method during a traversal.
	 * The return String is complemented here and subsequent methods are called
	 *
	 * @param angle the angle where the previous shape came from
	 */
	protected String ctraverseF(int angle){
		String shapestring ="";
		//continue in theta's direction
		if(angle == theta){
			if(exits[0] != null){
				shapestring += Translator.getLeftContent(this.bulge, Translator.SHAPE_LANG,-1);
				shapestring += exits[0].ctraverseF(angle);
				shapestring += Translator.getRightContent(this.bulge, Translator.SHAPE_LANG,-1);
			}
			else{
				shapestring += Translator.getLeftContent(this.bulge, Translator.SHAPE_LANG,-1);
				shapestring += " R ";
				shapestring += Translator.getRightContent(this.bulge, Translator.SHAPE_LANG,-1);
			}
		}
		//continue in (theta+offsetangle)'s direction
		else if(angle == ((theta+offsetangle)%360)){
			if(exits[1] != null){
				shapestring += Translator.getLeftContent(this.bulge, Translator.SHAPE_LANG,-1);
				shapestring += exits[1].ctraverseF(angle);
				shapestring += Translator.getRightContent(this.bulge, Translator.SHAPE_LANG,-1);
			}
			else{
				shapestring += Translator.getLeftContent(this.bulge, Translator.SHAPE_LANG,-1);
				shapestring += " R ";
				shapestring += Translator.getRightContent(this.bulge, Translator.SHAPE_LANG,-1);
			} 
		}
		return shapestring;
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
	        new BulgeEdit(this,ds,type);
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
			color = new Color(0,139,69,50);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		//fixed=false, color is transparent
		else{
			color = new Color(0,139,69,30);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		
		//ausserdem: male largerthanshown in bulge!
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
			if(startangle == ((theta+offsetangle)%360)){
				if(exits[0] != null){
					Point2D.Double transXY = new Point2D.Double(xloc,yloc);
					at.transform(transXY,transXY);
					if((((DoubleSingleShape)exits[0]).getThreePrime()).endContains(transXY)){
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
			else{
				if(exits[1] != null){
					Point2D.Double transXY = new Point2D.Double(xloc,yloc+height);
					at.transform(transXY,transXY);
					if((((DoubleSingleShape)exits[1]).getThreePrime()).endContains(transXY)){
						g2.drawString("5'",(float)xloc+28,(float)(yloc+height+25));
						g2.drawLine((int)(xloc+width-(width/4)),(int)(yloc+height+1),(int)(xloc+width-(width/4)),(int)(yloc+height+10));
					}
					else{
						g2.drawString("5'",(float)xloc+3,(float)(yloc+height+25));
						g2.drawLine((int)(xloc+(width/4)),(int)(yloc+height+1),(int)(xloc+(width/4)),(int)(yloc+height+10));
					}
				}
				else{
					if(standardorientation){
						g2.drawString("5'",(float)xloc+3,(float)(yloc+height+25));
						g2.drawLine((int)(xloc+(width/4)),(int)(yloc+height+1),(int)(xloc+(width/4)),(int)(yloc+height+10));
					}
					else{
						g2.drawString("5'",(float)xloc+28,(float)(yloc+height+25));
						g2.drawLine((int)(xloc+width-(width/4)),(int)(yloc+height+1),(int)(xloc+width-(width/4)),(int)(yloc+height+10));
					}
				}
			}
		}
		if(isendelement){
			if(endangle == ((theta+offsetangle)%360)){
				if(exits[0] != null){
					Point2D.Double transXY = new Point2D.Double(xloc,yloc);
					at.transform(transXY,transXY);
					if((((DoubleSingleShape)exits[0]).getFivePrime()).endContains(transXY)){
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
			else{
				if(exits[1] != null){
					Point2D.Double transXY = new Point2D.Double(xloc,yloc+height);
					at.transform(transXY,transXY);
					if((((DoubleSingleShape)exits[1]).getFivePrime()).endContains(transXY)){
						g2.drawString("3'",(float)xloc+28,(float)(yloc+height+25));
						g2.drawLine((int)(xloc+width-(width/4)),(int)(yloc+height+1),(int)(xloc+width-(width/4)),(int)(yloc+height+10));
					}
					else{
						g2.drawString("3'",(float)xloc+3,(float)(yloc+height+25));
						g2.drawLine((int)(xloc+(width/4)),(int)(yloc+height+1),(int)(xloc+(width/4)),(int)(yloc+height+10));
					}
				}
				else{
					if(standardorientation){
						g2.drawString("3'",(float)xloc+28,(float)(yloc+height+25));
						g2.drawLine((int)(xloc+width-(width/4)),(int)(yloc+height+1),(int)(xloc+width-(width/4)),(int)(yloc+height+10));
					}
					else{
						g2.drawString("3'",(float)xloc+3,(float)(yloc+height+25));
						g2.drawLine((int)(xloc+(width/4)),(int)(yloc+height+1),(int)(xloc+(width/4)),(int)(yloc+height+10));
					}
				}
			}
		}
		
		g2.setTransform(oldtransf);
	}
}


