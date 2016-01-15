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
 * This class implements a InternalShape which represents an InternalLoop 
 * graphically
 *
 * @author Janina Reeder
 */
public class InternalShape extends RnaShape implements Serializable {
	private static final long serialVersionUID = -4634663529401572313L;
	private InternalLoop internal; //the underlying data structure
	private float upperseg; //distance to begin of loop from top (xloc)
	private float loop53loopwidth, loop35loopwidth;
	private float curvepoint; //point to which the loops curve
	private int loop53largerthanshown, loop35largerthanshown;
	
	/**
	 * Constructor for an internal loop shape
	 * 
	 * @param orientation, true = standard, false = reverse
	 */
	public InternalShape(boolean orientation, FreeMagnets fm){
		xloc = 0;
		yloc = 0;
		width = 40;
		height = 80;
		upperseg = 15;
		loop53loopwidth = 30;
		loop35loopwidth = 30;
		curvepoint = 50;
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));   
		area.add(new Area(new Arc2D.Double(xloc - (loop53loopwidth - 80)/2-1,yloc+upperseg,loop53loopwidth,curvepoint,270,180,Arc2D.OPEN)));
		area.add(new Area(new Arc2D.Double(xloc-(loop35loopwidth/2)+1,yloc+upperseg,loop35loopwidth,curvepoint,90,180,Arc2D.OPEN)));
		rnapath = new GeneralPath();
		internal = new InternalLoop(orientation);  
		bb = this.internal;
		color = Color.yellow;
		theta = 0;
		offsetangle = 180;
		currentrotation = 0;
		at = new AffineTransform();
		exits = new RnaShape[2];
		exits[0] = null;
		exits[1] = null;
		standardorientation = orientation;
		selected = false;
		mtheta = new Magnet(new Line2D.Double(xloc+width,yloc+height,xloc,yloc+height),this,theta,true);
		moffset = new Magnet(new Line2D.Double(xloc,yloc,xloc+width,yloc),this,offsetangle,true);
		this.fm = fm;
		loop53largerthanshown = 0;
		loop35largerthanshown = 0;
		isstartelement = false;
		isendelement = false;
		startangle = -1;
		endangle = -1;
		rotations = new Vector<Rotation>();
		switchforced = false;
	}
	
	
	/**
	 * This method is needed for initializing a stored bulge
	 */
	private void initializeInternal(){  
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));  
		if((standardorientation && startindex == 0)||(!standardorientation && startindex == 1)){
			area.add(new Area(new Arc2D.Double(xloc - (loop53loopwidth - 80)/2-1,yloc+upperseg,loop53loopwidth,curvepoint,270,180,Arc2D.OPEN)));
			area.add(new Area(new Arc2D.Double(xloc-(loop35loopwidth/2)+1,yloc+upperseg,loop35loopwidth,curvepoint,90,180,Arc2D.OPEN)));
		}
		else{
			area.add(new Area(new Arc2D.Double(xloc-(loop53loopwidth/2)+1,yloc+upperseg,loop53loopwidth,curvepoint,90,180,Arc2D.OPEN)));
			area.add(new Area(new Arc2D.Double(xloc - (loop35loopwidth - 80)/2-1,yloc+upperseg,loop35loopwidth,curvepoint,270,180,Arc2D.OPEN)));
		} 
		adjustPath();
		color = Color.yellow;
		selected = false;
		this.bb = internal;
		setMotifHead();
	}
	
	/**
	 * Getter method for the type of this building block
	 * 
	 * @return EditorGui.INTERNAL_TYPE
	 */
	public int getType(){
		return EditorGui.INTERNAL_TYPE;
	}
	
	/**
	 * writes the object to an ObjectOutputStream
	 *
	 * @param s, the ObjectOutputStream
	 */
	private void writeObject(ObjectOutputStream s) throws IOException{
		s.writeDouble(xloc);
		s.writeDouble(yloc);
		s.writeDouble(width);
		s.writeDouble(height);
		s.writeFloat(upperseg);
		s.writeFloat(loop53loopwidth);
		s.writeFloat(loop35loopwidth);
		s.writeFloat(curvepoint);
		s.writeObject(internal);
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
		s.writeInt(loop53largerthanshown);
		s.writeInt(loop35largerthanshown);
		s.writeInt(startindex);
	}
	
	/**
	 * reads the object form an ObjectInputStream
	 *
	 * @param s, the ObjectInputStream
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream s) throws IOException,ClassNotFoundException{
		xloc = s.readDouble();
		yloc = s.readDouble();
		width = s.readDouble();
		height = s.readDouble();
		upperseg = s.readFloat();
		loop53loopwidth = s.readFloat();
		loop35loopwidth = s.readFloat();
		curvepoint = s.readFloat();
		internal = (InternalLoop) s.readObject();
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
		loop53largerthanshown = s.readInt();
		loop35largerthanshown = s.readInt();
		startindex = s.readInt();
		initializeInternal();
	}

	/**
	 * This method gives the internal data structure InternalLoop
	 * 
	 * @return the InternalLoop of the shape
	 */
	public InternalLoop getInternal(){
		return this.internal;
	}
	
	/**
	 * Changes the orientation of the shape
	 */
	public void changeOrientation(boolean seqreversal){
		float buf = loop53loopwidth;
		loop53loopwidth = loop35loopwidth;
		loop35loopwidth = buf;
		int buf2 = loop53largerthanshown;
		loop53largerthanshown = loop35largerthanshown;
		loop35largerthanshown = buf2;
		if(standardorientation){
			standardorientation = false;
		}
		else{
			standardorientation = true;
		}
		internal.setOrientation(standardorientation);
		if(exits[0] != null && exits[0].getType() == EditorGui.SS_DOUBLE){
			exits[0].changeOrientation(seqreversal);
		}
		if(exits[1] != null && exits[1].getType() == EditorGui.SS_DOUBLE){
			exits[1].changeOrientation(seqreversal);
		}
        internal.switchSides();
		if(seqreversal){
			internal.reverseStrings();
		}
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
		float lw53 = (float) loop53loopwidth;
		float lw35 = (float) loop35loopwidth;
		float cp = (float) curvepoint;

		if((standardorientation && startindex == 0)||(!standardorientation && startindex == 1)){
			//5'-3' loop
			rnapath.moveTo(x + w - (w/4), y + h);
			rnapath.lineTo(x + w - (w/4), y + h - (us+5));
			rnapath.quadTo(x - (lw53 - 80)/2 - 1 + lw53, y + us + cp/2, x + w - (w/4), y + (us+5));
			rnapath.lineTo(x + w - (w/4), y);
			//3'-5' loop
			rnapath.moveTo(x + (w/4), y + h);
			rnapath.lineTo(x + (w/4), y + h - (us+5));
			rnapath.quadTo(x - (lw35/2) + 1, y + us + cp/2, x + (w/4), y + (us+5));
			rnapath.lineTo(x + (w/4), y);
			//basepair connections
			rnapath.moveTo(x + (w/4), y + us/2 + 2);
			rnapath.lineTo(x + w - (w/4), y + us/2 + 2);
			rnapath.moveTo(x + (w/4), y + h - us/2 - 2);
			rnapath.lineTo(x + w - (w/4), y + h - us/2 - 2);
		}
		else{
			//5'-3' loop
			rnapath.moveTo(x + (w/4), y + h);
			rnapath.lineTo(x + (w/4), y + h - (us+5));
			rnapath.quadTo(x - (lw53/2) + 1, y + us + cp/2, x + (w/4), y + (us+5));
			rnapath.lineTo(x + (w/4), y);
			//3'-5' loop
			rnapath.moveTo(x + w - (w/4), y + h);
			rnapath.lineTo(x + w - (w/4), y + h - (us+5));
			rnapath.quadTo(x - (lw35 - 80)/2 - 1 + lw35, y + us + cp/2, x + w - (w/4), y + (us+5));
			rnapath.lineTo(x + w - (w/4), y);
			//basepair connections
			rnapath.moveTo(x + (w/4), y + us/2 + 2);
			rnapath.lineTo(x + w - (w/4), y + us/2 + 2);
			rnapath.moveTo(x + (w/4), y + h - us/2 - 2);
			rnapath.lineTo(x + w - (w/4), y + h - us/2 - 2);
		}
	}
	
	
	/**
	 * This method changes the size of the InternalLoop, i.e. changes its Area
	 *
	 * @param seglength, the sequence length upon which the size depends
	 */
	public void changeSize(int seqlength){
		int loop53size = (seqlength - 4)/2;
		int loop35size = seqlength - 4 - loop53size;
		change53Size(loop53size);
		change35Size(loop35size);
	}
	
	/**
	 * This method changes the size of the InternalLoop, i.e. changes its Area
	 * according to a median value for the sequence length
	 *
	 * @param minlength, the minimum sequence length
	 * @param maxlength, the maximum sequence length
	 */
	public void changeSize(int minlength, int maxlength){
		int middle = (minlength + maxlength) / 2;
		int loop53size = (middle - 4)/2;
		int loop35size = middle - 4 - loop53size;
		change53Size(loop53size);
		change35Size(loop35size);
	}
	
	/** 
	 * This method changes only the size of the 5'-3' loop according to the 
	 * given value.
	 * 
	 * @param loop53size, the new size of the 5'-3' loop
	 */
	public void change53Size(int loop53size){
		if(loop53size > 20){
			loop53largerthanshown = loop53size - 20;
			loop53size = 20;
		}
		else{
			loop53largerthanshown = 0;
		}
		loop53loopwidth = ((float)loop53size) * 8;
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		adjustPath();
		if((standardorientation && startindex == 0)||(!standardorientation && startindex == 1)){
			area.add(new Area(new Arc2D.Double(xloc - (loop53loopwidth - 80)/2-1,yloc+upperseg,loop53loopwidth,curvepoint,270,180,Arc2D.OPEN)));
			area.add(new Area(new Arc2D.Double(xloc-(loop35loopwidth/2)+1,yloc+upperseg,loop35loopwidth,curvepoint,90,180,Arc2D.OPEN)));
		}
		else{
			area.add(new Area(new Arc2D.Double(xloc-(loop53loopwidth/2)+1,yloc+upperseg,loop53loopwidth,curvepoint,90,180,Arc2D.OPEN)));
			area.add(new Area(new Arc2D.Double(xloc - (loop35loopwidth - 80)/2-1,yloc+upperseg,loop35loopwidth,curvepoint,270,180,Arc2D.OPEN)));
		}
		adjustMagnets();
	}
	
	/** 
	 * This method changes only the size of the 3'-5' loop according to the 
	 * given value.
	 * 
	 * @param loop35size, the new size of the 3'-5' loop
	 */
	public void change35Size(int loop35size){
		if(loop35size > 20){
			loop35largerthanshown = loop35size - 20;
			loop35size = 20;
		}
		else{
			loop35largerthanshown = 0;
		}
		loop35loopwidth = ((float)loop35size) * 8;
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		adjustPath();
		if((standardorientation && startindex == 0)||(!standardorientation && startindex == 1)){
			area.add(new Area(new Arc2D.Double(xloc - (loop53loopwidth - 80)/2-1,yloc+upperseg,loop53loopwidth,curvepoint,270,180,Arc2D.OPEN)));
			area.add(new Area(new Arc2D.Double(xloc-(loop35loopwidth/2)+1,yloc+upperseg,loop35loopwidth,curvepoint,90,180,Arc2D.OPEN)));
		}
		else{
			area.add(new Area(new Arc2D.Double(xloc-(loop53loopwidth/2)+1,yloc+upperseg,loop53loopwidth,curvepoint,90,180,Arc2D.OPEN)));
			area.add(new Area(new Arc2D.Double(xloc - (loop35loopwidth - 80)/2-1,yloc+upperseg,loop35loopwidth,curvepoint,270,180,Arc2D.OPEN)));
		}
		adjustMagnets();
	}
	
	/** 
	 * This method changes only the size of the 5'-3' loop according to the 
	 * given values.
	 * 
	 * @param loop53minsize, the new minimum size of the 5'-3' loop
	 * @param loop53maxsize, the new maximum size of the 5'-3' loop
	 */
	public void change53Size(int loop53minsize, int loop53maxsize){
		double loop53middle = ((double)(loop53minsize + loop53maxsize))/2;
		
		if(loop53middle > 20){
			loop53largerthanshown = (int)loop53middle - 20;
			loop53middle = 20;
		}
		else{
			loop53largerthanshown = 0;
		}
		loop53loopwidth = ((float)loop53middle) * 8;
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		adjustPath();
		if((standardorientation && startindex == 0)||(!standardorientation && startindex == 1)){
			area.add(new Area(new Arc2D.Double(xloc - (loop53loopwidth - 80)/2-1,yloc+upperseg,loop53loopwidth,curvepoint,270,180,Arc2D.OPEN)));
			area.add(new Area(new Arc2D.Double(xloc-(loop35loopwidth/2)+1,yloc+upperseg,loop35loopwidth,curvepoint,90,180,Arc2D.OPEN)));
		}
		else{
			area.add(new Area(new Arc2D.Double(xloc-(loop53loopwidth/2)+1,yloc+upperseg,loop53loopwidth,curvepoint,90,180,Arc2D.OPEN)));
			area.add(new Area(new Arc2D.Double(xloc - (loop35loopwidth - 80)/2-1,yloc+upperseg,loop35loopwidth,curvepoint,270,180,Arc2D.OPEN)));
		}
		adjustMagnets();
	}
	
	/** 
	 * This method changes only the size of the 3'-5' loop according to the 
	 * given values.
	 * 
	 * @param loop35minsize, the new minimum size of the 3'-5' loop
	 * @param loop35maxsize, the new maximum size of the 3'-5' loop
	 */
	public void change35Size(int loop35minsize, int loop35maxsize){
		double loop35middle = ((double)(loop35minsize + loop35maxsize))/2;
		
		if(loop35middle > 20){
			loop35largerthanshown = (int)loop35middle - 20;
			loop35middle = 20;
		}
		else{
			loop35largerthanshown = 0;
		}
		loop35loopwidth = ((float)loop35middle) * 8;
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		adjustPath();
		if((standardorientation && startindex == 0)||(!standardorientation && startindex == 1)){
			area.add(new Area(new Arc2D.Double(xloc - (loop53loopwidth - 80)/2-1,yloc+upperseg,loop53loopwidth,curvepoint,270,180,Arc2D.OPEN)));
			area.add(new Area(new Arc2D.Double(xloc-(loop35loopwidth/2)+1,yloc+upperseg,loop35loopwidth,curvepoint,90,180,Arc2D.OPEN)));
		}
		else{
			area.add(new Area(new Arc2D.Double(xloc-(loop53loopwidth/2)+1,yloc+upperseg,loop53loopwidth,curvepoint,90,180,Arc2D.OPEN)));
			area.add(new Area(new Arc2D.Double(xloc - (loop35loopwidth - 80)/2-1,yloc+upperseg,loop35loopwidth,curvepoint,270,180,Arc2D.OPEN)));
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
		xloc = (float)x;
		yloc = (float)y;
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		adjustPath();
		if((standardorientation && startindex == 0)||(!standardorientation && startindex == 1)){
			area.add(new Area(new Arc2D.Double(xloc - (loop53loopwidth - 80)/2-1,yloc+upperseg,loop53loopwidth,curvepoint,270,180,Arc2D.OPEN)));
			area.add(new Area(new Arc2D.Double(xloc-(loop35loopwidth/2)+1,yloc+upperseg,loop35loopwidth,curvepoint,90,180,Arc2D.OPEN)));
		}
		else{
			area.add(new Area(new Arc2D.Double(xloc-(loop53loopwidth/2)+1,yloc+upperseg,loop53loopwidth,curvepoint,90,180,Arc2D.OPEN)));
			area.add(new Area(new Arc2D.Double(xloc - (loop35loopwidth - 80)/2-1,yloc+upperseg,loop35loopwidth,curvepoint,270,180,Arc2D.OPEN)));
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
	 * @param AffineTransform stores a move transform that the internal loop
	 * should make
	 */
	public void changeLocation(AffineTransform movetransform){
		Point2D.Double movepoint = new Point2D.Double(xloc,yloc);
		movetransform.transform(movepoint,movepoint);
		xloc = movepoint.getX();
		yloc = movepoint.getY();
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		adjustPath();
		if((standardorientation && startindex == 0)||(!standardorientation && startindex == 1)){
			area.add(new Area(new Arc2D.Double(xloc - (loop53loopwidth - 80)/2-1,yloc+upperseg,loop53loopwidth,curvepoint,270,180,Arc2D.OPEN)));
			area.add(new Area(new Arc2D.Double(xloc-(loop35loopwidth/2)+1,yloc+upperseg,loop35loopwidth,curvepoint,90,180,Arc2D.OPEN)));
		}
		else{
			area.add(new Area(new Arc2D.Double(xloc-(loop53loopwidth/2)+1,yloc+upperseg,loop53loopwidth,curvepoint,90,180,Arc2D.OPEN)));
			area.add(new Area(new Arc2D.Double(xloc - (loop35loopwidth - 80)/2-1,yloc+upperseg,loop35loopwidth,curvepoint,270,180,Arc2D.OPEN)));
		}
		if(!rotations.isEmpty()){
			at.setToIdentity();
			for(Rotation r : rotations){
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
		if((standardorientation && startindex == 0)||(!standardorientation && startindex == 1)){
			area.add(new Area(new Arc2D.Double(xloc - (loop53loopwidth - 80)/2-1,yloc+upperseg,loop53loopwidth,curvepoint,270,180,Arc2D.OPEN)));
			area.add(new Area(new Arc2D.Double(xloc-(loop35loopwidth/2)+1,yloc+upperseg,loop35loopwidth,curvepoint,90,180,Arc2D.OPEN)));
		}
		else{
			area.add(new Area(new Arc2D.Double(xloc-(loop53loopwidth/2)+1,yloc+upperseg,loop53loopwidth,curvepoint,90,180,Arc2D.OPEN)));
			area.add(new Area(new Arc2D.Double(xloc - (loop35loopwidth - 80)/2-1,yloc+upperseg,loop35loopwidth,curvepoint,270,180,Arc2D.OPEN)));
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
	 * Method for adjusting both strings according to a traversal 
	 * for a new start
	 */
	public void switchsides(){
		this.internal.switchSides();
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
			this.internal.switchSides();
			this.internal.reverseStrings();
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
	 * @return String containing the shape notation for the whole structure
	 * @param type, the language of the String (XML or Shapes)
	 */
	public String beginTraversal(int type){
		String shapestring = "";
		if(startangle == theta){
			shapestring += Translator.getLeftContent(this.internal, type,-1);
			shapestring += exits[0].traverse(startangle,type);
			shapestring += Translator.getRightContent(this.internal, type,-1);
			if(!this.getIsEndElement() && exits[1] != null && exits[1].getType() == EditorGui.SS_DOUBLE){
				SingleShape next = ((DoubleSingleShape)exits[1]).getThreePrime();
				shapestring += next.traverse(startangle, type);
			}
		}
		else{ 
			shapestring += Translator.getLeftContent(this.internal, type,-1);
			shapestring += exits[1].traverse(startangle,type);
			shapestring += Translator.getRightContent(this.internal, type,-1);
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
	 * @param angle the angle where the previous shape came from
	 * @param type, the language of the String (XML or Shapes)
	 */
	protected String traverse(int angle, int type){
		String shapestring = "";
		//continue in theta's direction
		if(angle == theta){
			if(exits[0] != null){
				shapestring += Translator.getLeftContent(this.internal, type,-1);
				shapestring += exits[0].traverse(angle,type);
				shapestring += Translator.getRightContent(this.internal, type,-1);
				return shapestring;
			}
			else{
				return "ERROR: internalShape.traverse";
			}
		}
		//continue in (theta+offsetangle)'s direction
		else if(angle == ((theta+offsetangle)%360)){
			if(exits[1] != null){
				shapestring += Translator.getLeftContent(this.internal, type,-1);
				shapestring += exits[1].traverse(angle,type);
				shapestring += Translator.getRightContent(this.internal, type,-1);
				return shapestring;
			}
			else{
				return "ERROR: internalShape.traverse";
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
		shapestring += Translator.getLeftContent(this.internal, Translator.SHAPE_LANG,-1);
		shapestring += " R ";
		shapestring += Translator.getRightContent(this.internal, Translator.SHAPE_LANG,-1);
		if(startangle == theta){
			if(exits[0] != null){   	
				shapestring = Translator.getLeftContent(this.internal, Translator.SHAPE_LANG,-1);
				shapestring += exits[0].ctraverseF(startangle);
				shapestring += Translator.getRightContent(this.internal, Translator.SHAPE_LANG,-1);
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
				shapestring = Translator.getLeftContent(this.internal, Translator.SHAPE_LANG,-1);
				shapestring += exits[1].ctraverseF(startangle);
				shapestring += Translator.getRightContent(this.internal, Translator.SHAPE_LANG,-1);
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
				shapestring += Translator.getLeftContent(this.internal, Translator.SHAPE_LANG,-1);
				shapestring += exits[0].ctraverseF(angle);
				shapestring += Translator.getRightContent(this.internal, Translator.SHAPE_LANG,-1);
			}
			else{
				shapestring += Translator.getLeftContent(this.internal, Translator.SHAPE_LANG,-1);
				shapestring += " R ";
				shapestring += Translator.getRightContent(this.internal, Translator.SHAPE_LANG,-1);
			}
		}
		//continue in (theta+offsetangle)'s direction
		else if(angle == ((theta+offsetangle)%360)){
			if(exits[1] != null){
				shapestring += Translator.getLeftContent(this.internal, Translator.SHAPE_LANG,-1);
				shapestring += exits[1].ctraverseF(angle);
				shapestring += Translator.getRightContent(this.internal, Translator.SHAPE_LANG,-1);
			}
			else{
				shapestring += Translator.getLeftContent(this.internal, Translator.SHAPE_LANG,-1);
				shapestring += " R ";
				shapestring += Translator.getRightContent(this.internal, Translator.SHAPE_LANG,-1);
			} 
		}
		return shapestring;
	}
	
	/**
	 * Method to open the edit window of this shape
	 *
	 * @param ds the parent DrawingSurface
	 * @param type determines which tab is shown first
	 */
	public void openEditWindow(DrawingSurface ds, int type){
	    if(!editflag){
	        editflag = true;
	        new InternalEdit(this,ds, type);
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
			color = new Color(255,255,0,70);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		//fixed=false, color is transparent
		else{
			color = new Color(255,255,0,50);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		
		//ausserdem: male beide largerthanshown in internal!
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


