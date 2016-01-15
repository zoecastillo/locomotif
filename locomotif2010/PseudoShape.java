package rnaeditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
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
 * This class implements a PseudoShape which represents a Pseudoknot graphically
 *
 * @author Janina Reeder
 */
public class PseudoShape extends RnaShape implements Serializable {
	
    private static final long serialVersionUID = 2970274700993640008L;
    private Pseudoknot pknot; //the underlying data structure
	private double rheight; //height of the exit rectangles
	private double squarewidth; //width of the main square excluding exits
	
	/**
	 * Constructor for a bulge shape
	 * 
	 * @param orientation, true = standard, false = reverse
	 * @param fm, the FreeMagnets of the current RnaStructure
	 */
	public PseudoShape(boolean orientation, FreeMagnets fm){
		xloc = 0;
		yloc = 0;
		width = 20;
		rheight = 15;
		squarewidth = 50;
		height = 120;
		pknot = new Pseudoknot(orientation); 
		//main square
		area = new Area(new Rectangle2D.Double(xloc,yloc+rheight,squarewidth+2*width,height));
		//upper exit
		area.add(new Area(new Rectangle2D.Double(xloc,yloc,width,rheight)));
		//lower exit
		area.add(new Area(new Rectangle2D.Double(xloc+width+squarewidth,yloc+rheight+height,width,rheight)));
		rnapath = new GeneralPath();
		color = Color.cyan;
		theta = 0;
		offsetangle = 180;
		currentrotation = 0;
		at = new AffineTransform();
		exits = new RnaShape[2];
		exits[0] = null;
		exits[1] = null;
		standardorientation = orientation;
		selected = false;
		//two half magnets!
		mtheta = new Magnet(new Line2D.Double(xloc+width,yloc+height,xloc,yloc+height),this,theta, false, false);
		moffset = new Magnet(new Line2D.Double(xloc,yloc,xloc+width,yloc),this,offsetangle, false, false);
		this.fm = fm;
		isstartelement = false;
		isendelement = false;
		startangle = -1;
		endangle = -1;
		rotations = new Vector<Rotation>();
		switchforced = false;
		bb = this.pknot;
	}
	
	/**
	 * This method is needed for initializing a stored bulge
	 */
	private void initializePseudo(){
		squarewidth = 50;
		height = 120;
		//main square
		area = new Area(new Rectangle2D.Double(xloc,yloc+rheight,squarewidth+2*width,height));
		//upper exit
		area.add(new Area(new Rectangle2D.Double(xloc,yloc,width,rheight)));
		//lower exit
		area.add(new Area(new Rectangle2D.Double(xloc+width+squarewidth,yloc+rheight+height,width,rheight)));
		adjustPath();
		color = Color.cyan;
		selected = false;
		this.bb = this.pknot;
		setMotifHead();
	}
	
	public int getType(){
		return EditorGui.PSEUDO_TYPE;
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
		s.writeDouble(rheight);
		s.writeDouble(squarewidth);
		s.writeObject(pknot);
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
	 * @param s, the ObjectInputStream
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream s) throws IOException,ClassNotFoundException{
		xloc = s.readDouble();
		yloc = s.readDouble();
		width = s.readDouble();
		height = s.readDouble();
		rheight = s.readDouble();
		squarewidth = s.readDouble();
		pknot = (Pseudoknot) s.readObject();
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
		startindex = s.readInt();
		initializePseudo();
	}

	/**
	 * @return the Pseudoknot of the shape
	 */
	public Pseudoknot getPseudo(){
		return this.pknot;
	}
	
	/**
	 * changes the orientation of the shape
	 */
	public void changeOrientation(boolean seqreversal, boolean local){
		if(standardorientation){
			standardorientation = false;
		}
		else{
			standardorientation = true;
		}
		pknot.setOrientation(standardorientation);
		if(!local && exits[0] != null && exits[0].getType() == EditorGui.SS_DOUBLE){
			exits[0].changeOrientation(seqreversal);
		}
		if(!local && exits[1] != null && exits[1].getType() == EditorGui.SS_DOUBLE){
			exits[1].changeOrientation(seqreversal);
		}
	}
	
	/**
	 * changes the orientation of the shape
	 */
	public void changeOrientation(boolean seqreversal){
		changeOrientation(seqreversal, false);
	}
	
	/**
	 * Method for redrawing a pseudoknot using the changeLocation method with the current parameters.
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
		float rh = (float) rheight;
		float sw = (float) squarewidth;

		rnapath.moveTo(x+w/2,y);
		rnapath.lineTo(x+w/2,y+h/2+rh);
		
		rnapath.quadTo(x+5,y+rh+h-5,x+w+sw/2,y+h+6);
		rnapath.curveTo(x+w+2+sw,y+5+h,x+w+2+sw,y+rh+h/2+5,x+w+sw/2,y+h/2+rh);
		rnapath.curveTo(x+w/2+6,y+rh+h/2-2,x+w/2+6,y+rh+10,x+w+sw/2,y+rh+10);
		rnapath.quadTo(x+2*w+sw-5,y+rh+5,x+w+sw+w/2,y+h/2+rh);
		
		rnapath.moveTo(x+w+sw+w/2,y+h/2+rh);
		rnapath.lineTo(x+w+sw+w/2,y+2*rh+h);
		
		//dashed
		rnapath.moveTo(x+w/2,y+rh+h/4-7);
		rnapath.lineTo(x+w/2+15,y+rh+h/4-7);
		rnapath.moveTo(x+w/2,y+rh+h/4+1);
		rnapath.lineTo(x+w/2+13,y+rh+h/4+1);
		rnapath.moveTo(x+w/2,y+rh+h/4+9);
		rnapath.lineTo(x+w/2+14,y+rh+h/4+9);
		
		rnapath.moveTo(x+sw+w/2+w,y+rh+h/4-7+h/2-4);
		rnapath.lineTo(x+sw+15,y+rh+h/4-7+h/2-4);
		rnapath.moveTo(x+w/2+sw+w,y+rh+h/4+1+h/2-4);
		rnapath.lineTo(x+15+sw,y+rh+h/4+1+h/2-4);
		rnapath.moveTo(x+w/2+sw+w,y+rh+h/4+9+h/2-4);
		rnapath.lineTo(x+sw+14,y+rh+h/4+9+h/2-4);
		
	}
	
	
	/**
	 * Method for setting this element as a start element according to the given parameter
	 * 
	 * @param isstartelement, true if this is the start element; else false
	 * @param angle, the start angle of the building block
	 */
	public void setIsStartElement(boolean isstartelement, int angle){
		this.isstartelement = isstartelement;
		if(isstartelement){
		    if(exits[0] == null){
		        startangle = (theta+offsetangle)%360;
		    }
		    else if(exits[1] == null){
		        startangle = theta;
		    }
		    else{
		        startangle = angle;
		    }
		}
		else{
		    startangle = angle;
		}
		if(startangle == (theta+offsetangle)%360){
			startindex = 0;
		}
		else if(startangle == theta){
			startindex = 1;
			switchsides();
		}
		if(isstartelement){
			setMotifHead();
		}
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
		if(isendelement){
		    if(isstartelement){
		        endangle = (startangle + 180)%360;
		    }
		    else if(exits[1] == null){
		        endangle = theta;
		    }
		    else if(exits[0] == null){
		        endangle = (theta+offsetangle)%360;
		    }
		    else{
		        endangle = angle;
		    }
		}
		else{
		    endangle = angle;
		}
	}
	
	/**
	 * This method changes the size of the Pseudoknot, i.e. changes its Area.
	 * No effect on knot, since its size is constant
	 *
	 * @param seglength, the sequence length upon which the size depends
	 */
	public void changeSize(int seqlength){
	}
	
	/**
	 * This method changes the size of the Pseudoknot, i.e. changes its Area
	 * according to a median value for the sequence length
	 * Empty body since size is constant
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
		xloc = (double)x;
		yloc = (double)y;
		//main square
		area = new Area(new Rectangle2D.Double(xloc,yloc+rheight,squarewidth+2*width,height));
		//upper exit
		area.add(new Area(new Rectangle2D.Double(xloc,yloc,width,rheight)));
		//lower exit
		area.add(new Area(new Rectangle2D.Double(xloc+width+squarewidth,yloc+rheight+height,width,rheight)));
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
	 * @param AffineTransform stores a move transform that the pseudoknot should
	 * make
	 */
	public void changeLocation(AffineTransform movetransform){
		Point2D.Double movepoint = new Point2D.Double(xloc,yloc);
		movetransform.transform(movepoint,movepoint);
		xloc = movepoint.getX();
		yloc = movepoint.getY();
		//main square
		area = new Area(new Rectangle2D.Double(xloc,yloc+rheight,squarewidth+2*width,height));
		//upper exit
		area.add(new Area(new Rectangle2D.Double(xloc,yloc,width,rheight)));
		//lower exit
		area.add(new Area(new Rectangle2D.Double(xloc+width+squarewidth,yloc+rheight+height,width,rheight)));
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
	 * A Pseudoknot cannot snap!
	 *
	 * @param x, the x-coordinate of the new origin
	 * @param y, the y-coordinate of the new origin
	 * @param bottom, boolean value indicating where the point of rotation
	 *        must be
	 */
	public void changeLocation(double x, double y, boolean bottom){
		xloc = x;
		yloc = y;	
		//main square
		area = new Area(new Rectangle2D.Double(xloc,yloc+rheight,squarewidth+2*width,height));
		//upper exit
		area.add(new Area(new Rectangle2D.Double(xloc,yloc,width,rheight)));
		//lower exit
		area.add(new Area(new Rectangle2D.Double(xloc+width+squarewidth,yloc+rheight+height,width,rheight)));
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
	 * Method for switching sides according to a traversal 
	 * for a new start
	 * 
	 */
	public void switchsides(){
		pknot.switchSides();
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
		    System.out.println("SwitchSideTraversal");
			switchsides();
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
			shapestring += Translator.getLeftContent(this.pknot, type,-1);
			if(!this.getIsEndElement() && exits[0] != null){
				SingleShape next = ((DoubleSingleShape)exits[0]).getThreePrime();
				shapestring += next.traverse(startangle, type);
			}
			//shapestring += Translator.getRightContent(this.pknot, type,-1);
		}
		else{ 
			shapestring += Translator.getLeftContent(this.pknot, type,-1);
			if(!this.getIsEndElement() && exits[1] != null){
				SingleShape next = ((DoubleSingleShape)exits[1]).getThreePrime();
				shapestring += next.traverse(startangle, type);
			}
			//shapestring += Translator.getRightContent(this.pknot, type,-1);
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
				shapestring += Translator.getLeftContent(this.pknot, type,-1);
				//shapestring += Translator.getRightContent(this.pknot, type,-1);
				SingleShape next = ((DoubleSingleShape)exits[0]).getThreePrime();
				shapestring += next.traverse(startangle, type);
				return shapestring;
			}
			else{
				return "ERROR: PseudoShape.traverse";
			}
		}
		//continue in (theta+offsetangle)'s direction
		else if(angle == ((theta+offsetangle)%360)){
			if(exits[1] != null){
				shapestring += Translator.getLeftContent(this.pknot, type,-1);
				//shapestring += Translator.getRightContent(this.pknot, type,-1);
				SingleShape next = ((DoubleSingleShape)exits[1]).getThreePrime();
				shapestring += next.traverse(startangle, type);
				return shapestring;
			}
			else{
				return "ERROR: PseudoShape.traverse";
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
		shapestring += Translator.getLeftContent(this.pknot, Translator.SHAPE_LANG,-1);
		//shapestring += Translator.getRightContent(this.pknot, Translator.SHAPE_LANG,-1);
		if(startangle == theta){
			if(exits[0] != null){   	
				shapestring = Translator.getLeftContent(this.pknot, Translator.SHAPE_LANG,-1);
				//shapestring += Translator.getRightContent(this.pknot, Translator.SHAPE_LANG,-1);
				SingleShape next = ((DoubleSingleShape)exits[0]).getThreePrime();
				if(next != null){
				    shapestring += next.ctraverseF(startangle);
				}
			}
		}
		else{
			if(exits[1] != null){    			
				shapestring = Translator.getLeftContent(this.pknot, Translator.SHAPE_LANG,-1);
				//shapestring += Translator.getRightContent(this.pknot, Translator.SHAPE_LANG,-1);
				SingleShape next = ((DoubleSingleShape)exits[1]).getThreePrime();
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
				shapestring += Translator.getLeftContent(this.pknot, Translator.SHAPE_LANG,-1);
				//shapestring += Translator.getRightContent(this.pknot, Translator.SHAPE_LANG,-1);
				SingleShape next = ((DoubleSingleShape)exits[0]).getThreePrime();
				if(next != null){
				    shapestring += next.ctraverseF(startangle);
				}
			}
			else{
				shapestring += Translator.getLeftContent(this.pknot, Translator.SHAPE_LANG,-1);
				//shapestring += Translator.getRightContent(this.pknot, Translator.SHAPE_LANG,-1);
				shapestring += " R ";
			}
		}
		//continue in (theta+offsetangle)'s direction
		else if(angle == ((theta+offsetangle)%360)){
		    if(exits[1] != null){
				shapestring += Translator.getLeftContent(this.pknot, Translator.SHAPE_LANG,-1);
				//shapestring += Translator.getRightContent(this.pknot, Translator.SHAPE_LANG,-1);
				SingleShape next = ((DoubleSingleShape)exits[1]).getThreePrime();
				if(next != null){
				    shapestring += next.ctraverseF(startangle);
				}
			}
			else{
				shapestring += Translator.getLeftContent(this.pknot, Translator.SHAPE_LANG,-1);
				//shapestring += Translator.getRightContent(this.pknot, Translator.SHAPE_LANG,-1);
				shapestring += " R ";
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
	        new PseudoEdit(this,ds,type);
	    }
	}
	
	/**
	 * Method for computing the view of the motif head for this building block. This depends on the location
	 * of the 5' end and the current AffineTransform of the building block.
	 * 
	 */
	public void setMotifHead(){
		if(startangle == ((theta+offsetangle)%360)){
		    motifheadarea = new Ellipse2D.Double(xloc-2,yloc-35,25,25);
		}
		else{
		    motifheadarea = new Ellipse2D.Double(xloc+width+squarewidth-2,yloc+height+2*rheight+10,25,25);
		}
	}
	
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
			moffset = new Magnet(new Line2D.Double(p1,p2),this,(theta+offsetangle)%360,false,false);
			fm.add(moffset);
		}
		else{
			moffset = new Magnet(new Line2D.Double(p1,p2),this,(theta+offsetangle)%360,false,false);
		}
		
		line = new Line2D.Double(xloc+2*width+squarewidth,yloc+2*rheight+height,xloc+width+squarewidth,yloc+2*rheight+height); 
		p1 = line.getP1();
		p2 = line.getP2();
		at.transform(p1,p1);
		at.transform(p2,p2);
		if(fm.contains(mtheta)){
			fm.remove(mtheta);
			mtheta = new Magnet(new Line2D.Double(p1,p2),this,theta,false,false);
			fm.add(mtheta);
		}
		else{
			mtheta = new Magnet(new Line2D.Double(p1,p2),this,theta,false,false);
		}
	}
	
	/**
	 * This method adjusts the hairpin accessability of all magnets depending on the given parameter.
	 * 
	 * @param accessible determines whether the magnets are hairpin accessible
	 */
	public void adjustMagnetAccessability(boolean accessible){
		moffset.setIsHairpinAccessible(false);
		mtheta.setIsHairpinAccessible(false);
	}
	
	/**
	 * Method called during the traversal invoked after a hairpin was added to the structure.
	 * Its purpose is to determine the number of remaining open ends.
	 * 
	 * @param angle describing the direction of the traversal
	 */
	public void hairpinAccessTraversal(int angle){
	    System.out.println("Should never be reached: hairpin cannot be added to simple pseudoknot");
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
	    System.out.println("Should never be reached: hairpin cannot be added to simple pseudoknot");
	}
	
	/**
	 * This method implements a snapping mechanism which takes place if 
	 * the shape is close to a free Magnet
	 * A pseudoknot cannot be attached to another building block. Therefore it never snaps
	 * to another magnet: nothing happens!
	 *
	 * @param m, the Magnet to which the shape snaps
	 */
	public void snapTo(Magnet m){
	}
	
	
	/**
	 * This method restores the affine transformation to identity, 
	 * i.e. all rotations are undone
	 * 
	 */
	public void restore(){
			at.setToIdentity();
			rotations.clear();
			if(currentrotation!=0){
				at.rotate(StrictMath.toRadians(currentrotation),xloc+squarewidth/2+width,yloc+height/2+rheight);
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
		at.rotate(StrictMath.toRadians(degree),xloc+squarewidth/2+width,yloc+height/2+rheight);
		rotations.add(new Rotation(degree,3));
		theta += degree;
		theta = theta %360;
		currentrotation += degree;
		currentrotation = currentrotation %360;
		adjustMagnets();
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
		}
		if(angle == theta){
			if(exits[1] == null){
				setIsStartElement(true, theta, 0);
				return this;
			}
			else{
			    SingleShape next = ((DoubleSingleShape)exits[1]).getFivePrime();
			    return next.traverseToNewStart(angle, real);
			}
		}
		else{
			if(exits[0] == null){
				setIsStartElement(true, (theta+offsetangle)%360, 0);
				return this;
			}
			else{
			    SingleShape next = ((DoubleSingleShape)exits[0]).getFivePrime();
			    return next.traverseToNewStart(angle, real);
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
		return null;
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
			if(exits[1] == null){
				setIsEndElement(true, theta, 0);
				return this;
			}
			else{
			    SingleShape next = ((DoubleSingleShape)exits[1]).getThreePrime();
			    return next.traverseToNewEnd(angle);
			}
		}
		else{
			if(exits[0] == null){
				setIsEndElement(true, (theta+offsetangle)%360, 0);
				return this;
			}
			else{
			    SingleShape next = ((DoubleSingleShape)exits[0]).getThreePrime();
			    return next.traverseToNewEnd(angle);
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
		return null;
	}
	
	/**
	 * This method checks wether the RnaShape is at the end of the structure
	 * 
	 * @return index of the end or -1 if not found
	 */
	public int getEndType(){
		if(exits[0] == null && exits[1] == null) return 1;
		else return -1;
	}
	
	
	/**
	 * Method for traversing to a compound block for ambiguity checking (recursive method)
	 * 
	 * @param angle describing the direction of the traversal
	 * @return false: no ambiguity issues: should already be caught by connecting ss
	 */
	public boolean traverseToClosed(int angle){
		return false;
	}
	
	/**
	 * This method adds a neighboring shape to this one
	 * 
	 * @param shape, the shape that is added as a neighbor
	 * @param angle, the angle of the exit, where the shape is neighboring
	 * @param type is only required for SingleShape
	 */
	public void addToNeighbors(RnaShape shape,int angle,int type){
	    //only single strands can be added to a pseudoknot
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
			    DoubleSingleShape dss = new DoubleSingleShape();
			    if(startangle == theta){
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
			else if(angle == (theta+offsetangle)%360){
			    DoubleSingleShape dss = new DoubleSingleShape();
			    if(startangle == theta){
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
		}
		else if(shape.getIsConnected()){
			//shape ist 3' ende (haengt an 3' position)
			if(shape.getIsFivePrimeDSSAvailable()){
				if(angle == theta){
				    DoubleSingleShape dss = new DoubleSingleShape();
				    dss.addFivePrime(shape);
				    exits[0] = dss;
				    shape.addThreePrimeDSS(dss);
				    if(!standardorientation){
				        changeOrientation(false, true);
				    }
					return 1;
				}
				else{
				    DoubleSingleShape dss = new DoubleSingleShape();
				    dss.addFivePrime(shape);
				    exits[1] = dss;
				    shape.addThreePrimeDSS(dss);
				    if(standardorientation) {
				        changeOrientation(false, true);
				    }
					return 1;
				}
			}
			//shape ist 5' ende (haengt an 5' position)
			else{
				if(angle == theta){
				    DoubleSingleShape dss = new DoubleSingleShape();
				    dss.addThreePrime(shape);
				    exits[0] = dss;
				    shape.addFivePrimeDSS(dss);
				    if(standardorientation){
				        changeOrientation(false,true);
				    }
					return 2;
				}
				else{
				    DoubleSingleShape dss = new DoubleSingleShape();
				    dss.addThreePrime(shape);
				    exits[1] = dss;
				    shape.addFivePrimeDSS(dss);
				    if(!standardorientation){
				        changeOrientation(false,true);
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
		mtheta.setIsHairpinAccessible(false);
		moffset.setIsHairpinAccessible(false);
	}
	
	/**
	 * This method removes a specific RnaShape from the neighbors of this one
	 *
	 * @param s, the RnaShape to be removed
	 */
	public void remove(RnaShape s){
	    if(exits[0] != null && exits[0].getType() == EditorGui.SS_DOUBLE && ((DoubleSingleShape)exits[0]).contains(s)){
	        ((DoubleSingleShape)exits[0]).remove(s);
	        exits[0] = null;
	        fm.remove(moffset);
	        fm.add(moffset);
	    }
	    else if(exits[1] != null && exits[1].getType() == EditorGui.SS_DOUBLE && ((DoubleSingleShape)exits[1]).contains(s)){
	        ((DoubleSingleShape)exits[1]).remove(s);
	        exits[1] = null;
	        fm.remove(mtheta);
	        fm.add(mtheta);
	    }
	}
	
	/**
	 * This method checks wether there is a DoubleSingleStrand at exit of the given index
	 *
	 * @param index, the index where a DSS is searched for
	 */
	public boolean hasDSSNeighbor(int index){
		if(exits[index] != null){
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
		    found0 = exits[0].connectedTo(shape, theta, this);
		}
		if(found0 == false && (angle != theta || angle == -1) && this.exits[1] != null){
		    found1 = exits[1].connectedTo(shape, (theta+offsetangle)%360, this);
		}
		return (found0 || found1);
		
	}
	
	public boolean startFindSS(){
		if(exits[1] == null && exits[0] == null){
			return false;
		}
		return true;
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
			else{
				return true;
			}
		}
		else{
			if(exits[0] == null){
				return false;
			}
			else{
				return true;
			}
		}
	}

	/**
	 * Method for a traversal and shift operation. 
	 * The RnaShape is adjusted according to a size change of another
	 * element in the RnaStructure
	 *
	 * @param angle, the direction the shift is going
	 */
	public void traverseShift(int angle){
	    //should never be reached
	}
	
	public void traverseShift(AffineTransform movetransform, int angle){
	    //should never be reached
	}
	
	
	
	
	
	/**
	 * This method does the actual drawing of this shape's Area
	 *
	 * @param g2, the Graphics2D object that performs the drawing
	 * @param fixed, boolean value describing wether the shape is part of 
	 * the current RnaStructure or not
	 * 
	 */
	public void show(Graphics2D g2, boolean fixed){
		//fixed=true, color is opaque
		if(fixed){
			color = new Color(0,255,255,50);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		//fixed=false, color is transparent
		else{
			color = new Color(0,255,255,30);
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
			    g2.drawString("5'",(float)xloc+6,(float)yloc-15);	
			    g2.drawLine((int)(xloc+(width/2)),(int)yloc-1,(int)(xloc+(width/2)),(int)yloc-10);
			}
			else{
			    g2.drawString("5'",(float)(xloc+width+squarewidth+6),(float)(yloc+2*rheight+height+25));
			    g2.drawLine((int)(xloc+width+squarewidth+(width/2)),(int)(yloc+2*rheight+height+1),(int)(xloc+width+squarewidth+(width/2)),(int)(yloc+2*rheight+height+10));
			}
		}
		if(isendelement){
			if(endangle == ((theta+offsetangle)%360)){
			    g2.drawString("3'",(float)xloc+6,(float)yloc-15);	
			    g2.drawLine((int)(xloc+(width/2)),(int)yloc-1,(int)(xloc+(width/2)),(int)yloc-10);
			}
			else{
			    g2.drawString("3'",(float)(xloc+width+squarewidth+6),(float)(yloc+2*rheight+height+25));
			    g2.drawLine((int)(xloc+width+squarewidth+(width/2)),(int)(yloc+2*rheight+height+1),(int)(xloc+width+squarewidth+(width/2)),(int)(yloc+2*rheight+height+10));
			}
		}
		
		g2.setTransform(oldtransf);
	}
}


