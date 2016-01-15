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
 * This class implements a ClosedStructShape which represents a closedstruct graphically
 *
 * @author Janina Reeder
 */
public class ClosedStructShape extends RnaShape implements Serializable {
	private static final long serialVersionUID = -4084054596031732731L;
	private ClosedStruct closedstruct; //the underlying data structure
	private GeneralPath rnarangedashes;
	private float loopwidth1, loopwidth2, loopwidth3, loopwidth4;
	private float curvepoint1, curvepoint2, curvepoint3, curvepoint4;
	private float upperseg, lowerseg;
	private float rightdist, leftdist;
	
	/**
	 * Constructor for a closedstruct shape
	 * 
	 * @param orientation, true = standard, false = reverse
	 * @param fm, the FreeMagnets of the current RnaStructure
	 */
	public ClosedStructShape(boolean orientation, FreeMagnets fm){
		xloc = 0;
		yloc = 0;
		width = 40;
		height = 120;
		upperseg = 15;
		lowerseg = 15;
		loopwidth1 = 35;
		loopwidth2 = 30;
		loopwidth3 = 30;
		loopwidth4 = 25;
		curvepoint1 = 20;
		curvepoint2 = 30;
		curvepoint3 = 30;
		curvepoint4 = 25;
		rightdist = 5;
		leftdist = 10;
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));   
		area.add(new Area(new Arc2D.Double(xloc - (loopwidth4 - 80)/2-1,yloc+upperseg,loopwidth4,curvepoint4,270,180,Arc2D.OPEN))); 
		area.add(new Area(new Arc2D.Double(xloc - (loopwidth3 - 80)/2-1,yloc+upperseg+curvepoint3+rightdist,loopwidth3,curvepoint3,270,180,Arc2D.OPEN)));
		area.add(new Area(new Arc2D.Double(xloc-(loopwidth2/2)+1,yloc+height-curvepoint2-leftdist-curvepoint1-lowerseg,loopwidth2,curvepoint2,90,180,Arc2D.OPEN)));
		area.add(new Area(new Arc2D.Double(xloc-(loopwidth1/2)+1,yloc+height-curvepoint1-lowerseg,loopwidth1,curvepoint1,90,180,Arc2D.OPEN)));
		rnapath = new GeneralPath();
		rnarangedashes = new GeneralPath();
		closedstruct = new ClosedStruct(orientation);  
		bb = this.closedstruct;
		color = Color.magenta;    
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
		isstartelement = false;
		isendelement = false;
		startangle = -1;
		endangle = -1;
		rotations = new Vector<Rotation>();
		switchforced = false;
	}
	
	
	/**
	 * This method is needed for initializing a stored closedstruct
	 */
	private void initializeClosedStruct(){
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));   
		area.add(new Area(new Arc2D.Double(xloc - (loopwidth4 - 80)/2-1,yloc+upperseg,loopwidth4,curvepoint4,270,180,Arc2D.OPEN))); 
		area.add(new Area(new Arc2D.Double(xloc - (loopwidth3 - 80)/2-1,yloc+upperseg+curvepoint3+rightdist,loopwidth3,curvepoint3,270,180,Arc2D.OPEN)));
		area.add(new Area(new Arc2D.Double(xloc-(loopwidth2/2)+1,yloc+height-curvepoint2-leftdist-curvepoint1-lowerseg,loopwidth2,curvepoint2,90,180,Arc2D.OPEN)));
		area.add(new Area(new Arc2D.Double(xloc-(loopwidth1/2)+1,yloc+height-curvepoint1-lowerseg,loopwidth1,curvepoint1,90,180,Arc2D.OPEN)));
		adjustPath();
		color = Color.magenta;  
		selected = false;
		this.bb = closedstruct;
		setMotifHead();
	}

	/**
	 * Getter method for the type of this building block
	 * 
	 * @return EditorGui.CLOSED_STRUCT_TYPE
	 */
	public int getType(){
		return EditorGui.CLOSED_STRUCT_TYPE;
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
		s.writeDouble(height);
		s.writeFloat(upperseg);
		s.writeFloat(lowerseg);
		s.writeFloat(loopwidth1);
		s.writeFloat(loopwidth2);
		s.writeFloat(loopwidth3);
		s.writeFloat(loopwidth4);
		s.writeFloat(curvepoint1);
		s.writeFloat(curvepoint2);
		s.writeFloat(curvepoint3);
		s.writeFloat(curvepoint4);
		s.writeFloat(rightdist);
		s.writeFloat(leftdist);
		s.writeObject(closedstruct);
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
		height = s.readDouble();
		upperseg = s.readFloat();
		lowerseg = s.readFloat();
		loopwidth1 = s.readFloat();
		loopwidth2 = s.readFloat();
		loopwidth3 = s.readFloat();
		loopwidth4 = s.readFloat();
		curvepoint1 = s.readFloat();
		curvepoint2 = s.readFloat();
		curvepoint3 = s.readFloat();
		curvepoint4 = s.readFloat();
		rightdist = s.readFloat();
		leftdist = s.readFloat();
		closedstruct = (ClosedStruct) s.readObject();
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
		rotations = (Vector<Rotation>)s.readObject();
		switchforced = s.readBoolean();
		startindex = s.readInt();
		initializeClosedStruct();
	}

	/**
	 * @return the ClosedStruct of the shape
	 */
	public ClosedStruct getClosedStruct(){
		return this.closedstruct;
	}
	
	/**
	 * Changes the orientation of the shape
	 */
	public void changeOrientation(boolean seqreversal){
		if(standardorientation){
			standardorientation = false;
		}
		else{
			standardorientation = true;
		}
		closedstruct.setOrientation(standardorientation);
		if(exits[0] != null && exits[0].getType() == EditorGui.SS_DOUBLE){
			exits[0].changeOrientation(seqreversal);
		}
		if(exits[1] != null && exits[1].getType() == EditorGui.SS_DOUBLE){
			exits[1].changeOrientation(seqreversal);
		}
	}
	
	/**
	 * This method adjust the GeneralPath containing the squiggle plot
	 * according to changes in size, location or other settings
	 */
	private void adjustPath(){
		rnapath = new GeneralPath();
		rnarangedashes = new GeneralPath();
		float x = (float) xloc;
		float y = (float) yloc;
		float w = (float) width;
		float h = (float) height;
		// main shape
		rnapath.moveTo(x + (w/4), y);
		rnapath.lineTo(x + (w/4), y + 15);
		rnapath.moveTo(x + (w/4), y + 8);
		rnapath.lineTo(x + w - (w/4), y + 8);
		rnapath.moveTo(x + w - (w/4), y + 15);
		rnapath.lineTo(x + w - (w/4), y);
		
		rnapath.moveTo(x + (w/4), y + h);
		rnapath.lineTo(x + (w/4), y + h - 15);
		rnapath.moveTo(x + (w/4), y + h - 8);
		rnapath.lineTo(x + w - (w/4), y + h - 8);
		rnapath.moveTo(x + w - (w/4), y + h - 15);
		rnapath.lineTo(x + w - (w/4), y + h);
		
		rnarangedashes.moveTo(x + (w/4), y + 17);
		rnarangedashes.lineTo(x + (w/4), y + 21);
		rnarangedashes.moveTo(x + (w/4), y + 23);
		rnarangedashes.lineTo(x + (w/4), y + 26);
		rnarangedashes.moveTo(x + (w/4), y + 28);
		rnarangedashes.lineTo(x + (w/4), y + 31);
		
		
		rnarangedashes.moveTo(x + (w/4), y + h - 31);
		rnarangedashes.lineTo(x + (w/4), y + h - 28);
		rnarangedashes.moveTo(x + (w/4), y + h - 26);
		rnarangedashes.lineTo(x + (w/4), y + h - 23);
		rnarangedashes.moveTo(x + (w/4), y + h - 21);
		rnarangedashes.lineTo(x + (w/4), y + h - 17);
		
		
		rnarangedashes.moveTo(x + w - (w/4), y + 17);
		rnarangedashes.lineTo(x + w - (w/4), y + 21);
		rnarangedashes.moveTo(x + w - (w/4), y + 23);
		rnarangedashes.lineTo(x + w - (w/4), y + 26);
		rnarangedashes.moveTo(x + w - (w/4), y + 28);
		rnarangedashes.lineTo(x + w - (w/4), y + 31);
		
		
		rnarangedashes.moveTo(x + w - (w/4), y + h - 31);
		rnarangedashes.lineTo(x + w - (w/4), y + h - 28);
		rnarangedashes.moveTo(x + w - (w/4), y + h - 26);
		rnarangedashes.lineTo(x + w - (w/4), y + h - 23);
		rnarangedashes.moveTo(x + w - (w/4), y + h - 21);
		rnarangedashes.lineTo(x + w - (w/4), y + h - 17);
	}
	
	
	
	/**
	 * This method changes the size of the ClosedStruct, i.e. changes its Area
	 * A ClosedStruct is always drawn the same, therefore this method has no body.
	 *
	 * @param seglength, the sequence length upon which the size depends
	 */
	public void changeSize(int seqlength){
	}
	
	/**
	 * This method changes the size of the ClosedStruct, i.e. changes its Area
	 * according to a median value for the sequence length
	 * A ClosedStruct is always drawn the same, therefore this method has no body.
	 *
	 * @param minlength, the minimum sequence length
	 * @param maxlength, the maximum sequence length
	 */
	public void changeSize(int minlength, int maxlength){
	}
	
	/**
	 * This method implements a change in location to a new point of origin
	 *
	 * @param x, the x-coordinate of the new origin
	 * @param y, the y-coordinate of the new origin
	 */
	public void changeLocation(double x, double y){
		xloc = x;
		yloc = y;
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));   
		area.add(new Area(new Arc2D.Double(xloc - (loopwidth4 - 80)/2-1,yloc+upperseg,loopwidth4,curvepoint4,270,180,Arc2D.OPEN))); 
		area.add(new Area(new Arc2D.Double(xloc - (loopwidth3 - 80)/2-1,yloc+upperseg+curvepoint3+rightdist,loopwidth3,curvepoint3,270,180,Arc2D.OPEN)));
		area.add(new Area(new Arc2D.Double(xloc-(loopwidth2/2)+1,yloc+height-curvepoint2-leftdist-curvepoint1-lowerseg,loopwidth2,curvepoint2,90,180,Arc2D.OPEN)));
		area.add(new Area(new Arc2D.Double(xloc-(loopwidth1/2)+1,yloc+height-curvepoint1-lowerseg,loopwidth1,curvepoint1,90,180,Arc2D.OPEN)));
		adjustPath();
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
		area.add(new Area(new Arc2D.Double(xloc - (loopwidth4 - 80)/2-1,yloc+upperseg,loopwidth4,curvepoint4,270,180,Arc2D.OPEN))); 
		area.add(new Area(new Arc2D.Double(xloc - (loopwidth3 - 80)/2-1,yloc+upperseg+curvepoint3+rightdist,loopwidth3,curvepoint3,270,180,Arc2D.OPEN)));
		area.add(new Area(new Arc2D.Double(xloc-(loopwidth2/2)+1,yloc+height-curvepoint2-leftdist-curvepoint1-lowerseg,loopwidth2,curvepoint2,90,180,Arc2D.OPEN)));
		area.add(new Area(new Arc2D.Double(xloc-(loopwidth1/2)+1,yloc+height-curvepoint1-lowerseg,loopwidth1,curvepoint1,90,180,Arc2D.OPEN)));
		adjustPath();
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
		area.add(new Area(new Arc2D.Double(xloc - (loopwidth4 - 80)/2-1,yloc+upperseg,loopwidth4,curvepoint4,270,180,Arc2D.OPEN))); 
		area.add(new Area(new Arc2D.Double(xloc - (loopwidth3 - 80)/2-1,yloc+upperseg+curvepoint3+rightdist,loopwidth3,curvepoint3,270,180,Arc2D.OPEN)));
		area.add(new Area(new Arc2D.Double(xloc-(loopwidth2/2)+1,yloc+height-curvepoint2-leftdist-curvepoint1-lowerseg,loopwidth2,curvepoint2,90,180,Arc2D.OPEN)));
		area.add(new Area(new Arc2D.Double(xloc-(loopwidth1/2)+1,yloc+height-curvepoint1-lowerseg,loopwidth1,curvepoint1,90,180,Arc2D.OPEN)));
		adjustPath();
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
	 * Method for checking if connected to a compound block: which is found here
	 * 
	 * @param angle describing the directin of the traversal
	 * @return true, since this is a compound block
	 */
	public boolean traverseToClosed(int angle){
		return true;
	}
	
	/**
	 * Method for adjusting the both strings according to a traversal 
	 * for a new start
	 */
	public void switchsides(){
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
			shapestring += Translator.getLeftContent(this.closedstruct, type,-1);
			shapestring += exits[0].traverse(startangle,type);
			shapestring += Translator.getRightContent(this.closedstruct, type,-1);
			if(!this.getIsEndElement() && exits[1] != null && exits[1].getType() == EditorGui.SS_DOUBLE){
				SingleShape next = ((DoubleSingleShape)exits[1]).getThreePrime();
				shapestring += next.traverse(startangle, type);
			}
		}
		else{ 
			shapestring += Translator.getLeftContent(this.closedstruct, type,-1);
			shapestring += exits[1].traverse(startangle,type);
			shapestring += Translator.getRightContent(this.closedstruct, type,-1);
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
				shapestring += Translator.getLeftContent(this.closedstruct, type,-1);
				shapestring += exits[0].traverse(angle,type);
				shapestring += Translator.getRightContent(this.closedstruct, type,-1);
				return shapestring;
			}
			else{
				return "ERROR: closedstructShape.traverse";
			}
		}
		//continue in (theta+offsetangle)'s direction
		else if(angle == ((theta+offsetangle)%360)){
			if(exits[1] != null){
				shapestring += Translator.getLeftContent(this.closedstruct, type,-1);
				shapestring += exits[1].traverse(angle,type);
				shapestring += Translator.getRightContent(this.closedstruct, type,-1);
				return shapestring;
			}
			else{
				return "ERROR: closedstructShape.traverse";
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
		shapestring += Translator.getLeftContent(this.closedstruct, Translator.SHAPE_LANG,-1);
		shapestring += " R ";
		shapestring += Translator.getRightContent(this.closedstruct, Translator.SHAPE_LANG,-1);
		if(startangle == theta){
			if(exits[0] != null){   	
				shapestring = Translator.getLeftContent(this.closedstruct, Translator.SHAPE_LANG,-1);
				shapestring += exits[0].ctraverseF(startangle);
				shapestring += Translator.getRightContent(this.closedstruct, Translator.SHAPE_LANG,-1);
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
				shapestring = Translator.getLeftContent(this.closedstruct, Translator.SHAPE_LANG,-1);
				shapestring += exits[1].ctraverseF(startangle);
				shapestring += Translator.getRightContent(this.closedstruct, Translator.SHAPE_LANG,-1);
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
				shapestring += Translator.getLeftContent(this.closedstruct, Translator.SHAPE_LANG,-1);
				shapestring += exits[0].ctraverseF(angle);
				shapestring += Translator.getRightContent(this.closedstruct, Translator.SHAPE_LANG,-1);
			}
			else{
				shapestring += Translator.getLeftContent(this.closedstruct, Translator.SHAPE_LANG,-1);
				shapestring += " R ";
				shapestring += Translator.getRightContent(this.closedstruct, Translator.SHAPE_LANG,-1);
			}
		}
		//continue in (theta+offsetangle)'s direction
		else if(angle == ((theta+offsetangle)%360)){
			if(exits[1] != null){
				shapestring += Translator.getLeftContent(this.closedstruct, Translator.SHAPE_LANG,-1);
				shapestring += exits[1].ctraverseF(angle);
				shapestring += Translator.getRightContent(this.closedstruct, Translator.SHAPE_LANG,-1);
			}
			else{
				shapestring += Translator.getLeftContent(this.closedstruct, Translator.SHAPE_LANG,-1);
				shapestring += " R ";
				shapestring += Translator.getRightContent(this.closedstruct, Translator.SHAPE_LANG,-1);
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
	        new ClosedStructEdit(this,ds,type);
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
		AffineTransform oldtransf = g2.getTransform();
		g2.transform(at); //do possible rotation
		
		//fixed=true, color is opaque
		if(fixed){
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			color = new Color(221,221,221,70);
		}
		//fixed=false, color is transparent
		else{
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			color = new Color(221,221,221,50);
		}
		
		//draw closed struct
		g2.setPaint(color);
		g2.fill(area);
		
		g2.setPaint(Color.lightGray);
		g2.draw(area); //box outline
		
		g2.setPaint(Color.gray);
		g2.draw(rnarangedashes);
		
		g2.setPaint(Color.black);
		g2.draw(rnapath); //Basepair connections + backbone
		
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
		//show the current orientation
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


