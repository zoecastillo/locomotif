package rnaeditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
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
 * This class implements a StemShape which represents a stem graphically
 *
 * @author Janina Reeder
 */
public class StemShape extends RnaShape implements Serializable {
	private static final long serialVersionUID = -2628029522944658941L;
	private Stem stem; //the underlying data structure
	private GeneralPath rnarangedashes;
	private Magnet oldmoffset;
	private Magnet oldmtheta;
	
	/**
	 * Constructor for a stem shape
	 * 
	 * @param orientation, true = standard, false = reverse
	 */
	public StemShape(boolean orientation, FreeMagnets fm){
		xloc = 0;
		yloc = 0;
		width = 40;
		height = 80;
		//area is a single rectangle
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		rnapath = new GeneralPath();
		rnarangedashes = new GeneralPath();
		stem = new Stem(orientation);  
		bb = this.stem;
		color = Color.orange;
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
		oldmtheta = new Magnet(new Line2D.Double(xloc+width,yloc+height,xloc,yloc+height),this,theta,true);
		this.fm = fm;
		isstartelement = false;
		isendelement = false;
		startangle = -1;
		endangle = -1;
		rotations = new Vector<Rotation>();
		switchforced = false;
	}
	
	
	/**
	 * This method is needed for initializing a stored stem
	 */
	private void initializeStem(){
		//area is a single rectangle
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		adjustPath();
		color = Color.orange;
		selected = false;
		this.bb = stem;
		setMotifHead();
	}
	
	/**
	 * Getter for the type of this building block
	 * 
	 * @return EditorGui.STEM_TYPE
	 */
	public int getType(){
		return EditorGui.STEM_TYPE;
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
		s.writeObject(stem);
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
		stem = (Stem) s.readObject();
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
		initializeStem();
	}
	
	
	/**
	 * This method gives the internal data structure Stem
	 * 
	 * @return the Stem of the shape
	 */
	public Stem getStem(){
		return this.stem;
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
		stem.setOrientation(standardorientation);
		if(exits[0] != null && exits[0].getType() == EditorGui.SS_DOUBLE){
			exits[0].changeOrientation(seqreversal);
		}
		if(exits[1] != null && exits[1].getType() == EditorGui.SS_DOUBLE){
			exits[1].changeOrientation(seqreversal);
		}
        this.stem.switchSides();
		if(seqreversal){
			this.stem.reverseStrings();
		}
	}
	
	/**
	 * This method changes the size of the Stem, i.e. changes its Area
	 *
	 * @param seglength, the sequence length upon which the size depends
	 */
	public void changeSize(int seqlength){
		height = ((double)seqlength) * 10;
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		adjustPath();
		oldmoffset = moffset.clone();
		oldmtheta = mtheta.clone();
		adjustMagnets();
		if(isstartelement){
			setMotifHead();
		}
	}
	
	/**
	 * This method changes the size of the Stem, i.e. changes its Area
	 * according to a median value for the sequence length
	 *
	 * @param minlength, the minimum sequence length
	 * @param maxlength, the maximum sequence length
	 */
	public void changeSize(int minlength, int maxlength){
		double middle = ((double)(minlength + maxlength));
		height = middle/2 * 10;
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		adjustPath();
		oldmoffset = moffset.clone();
		oldmtheta = mtheta.clone();
		adjustMagnets();
		if(isstartelement){
			setMotifHead();
		}
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
		adjustPath();
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
		int num = 0; // number of necessary dashes
		float ddist = 0; //distance between the dashes
		// base connections for exact length
		if(stem.getIsExactLength()){
			// main shape
			rnapath.moveTo(x + (w/4), y);
			rnapath.lineTo(x + (w/4), y + h);
			rnapath.moveTo(x + w - (w/4), y + h);
			rnapath.lineTo(x + w - (w/4), y);
			num = stem.getLength();
			ddist = h / (num + 1);
			for(int i=1;i<=num;i++){
				rnapath.moveTo(x + (w/4), y + i*ddist);
				rnapath.lineTo(x + w - (w/4), y + i*ddist);
			}
		}
		//base connections for approx. length
		else{
			int minnum = stem.getMinLength();
			int maxnum = stem.getStemsMaxLength();
			int minmaxdiff = maxnum - minnum;    		
			ddist = h / (maxnum + 1);
			for(int i=1;i<=minnum/2;i++){
				rnapath.moveTo(x + (w/4), y + i*ddist);
				rnapath.lineTo(x + w - (w/4), y + i*ddist);
			}
			if(stem.getIsDefaultMax()){
				float j = (minnum/2+1) * ddist;
				// main shape
				rnapath.moveTo(x + (w/4), y);
				rnapath.lineTo(x + (w/4), y + j - 2);
				rnapath.moveTo(x + w - (w/4), y + j - 2);
				rnapath.lineTo(x + w - (w/4), y);
				rnapath.moveTo(x + (w/4), y + h);
				rnapath.lineTo(x + (w/4), y + h -j - 3);
				rnapath.moveTo(x + w - (w/4), y + h -j - 3);
				rnapath.lineTo(x + w - (w/4), y + h);

				rnarangedashes.moveTo(x + (w/4), y + j);
				rnarangedashes.lineTo(x + (w/4), y + j + 3);
				rnarangedashes.moveTo(x + (w/4), y + j + 5);
				rnarangedashes.lineTo(x + (w/4), y + j + 8);
				rnarangedashes.moveTo(x + (w/4), y + j + 10);
				rnarangedashes.lineTo(x + (w/4), y + j + 13);
				
				
				rnarangedashes.moveTo(x + (w/4), y + h - (j+17));
				rnarangedashes.lineTo(x + (w/4), y + h - (j+14));
				rnarangedashes.moveTo(x + (w/4), y + h - (j+12));
				rnarangedashes.lineTo(x + (w/4), y + h - (j+9));
				rnarangedashes.moveTo(x + (w/4), y + h - (j+7));
				rnarangedashes.lineTo(x + (w/4), y + h - (j+5));
				
				rnarangedashes.moveTo(x + w - (w/4), y + j);
				rnarangedashes.lineTo(x + w - (w/4), y + j + 3);
				rnarangedashes.moveTo(x + w - (w/4), y + j + 5);
				rnarangedashes.lineTo(x + w - (w/4), y + j + 8);
				rnarangedashes.moveTo(x + w - (w/4), y + j + 10);
				rnarangedashes.lineTo(x + w - (w/4), y + j + 13);
				
				
				rnarangedashes.moveTo(x + w - (w/4), y + h - (j+17));
				rnarangedashes.lineTo(x + w - (w/4), y + h - (j+14));
				rnarangedashes.moveTo(x + w - (w/4), y + h - (j+12));
				rnarangedashes.lineTo(x + w - (w/4), y + h - (j+9));
				rnarangedashes.moveTo(x + w - (w/4), y + h - (j+7));
				rnarangedashes.lineTo(x + w - (w/4), y + h - (j+5));
			}
			else{
				// main shape
				rnapath.moveTo(x + (w/4), y);
				rnapath.lineTo(x + (w/4), y + h);
				rnapath.moveTo(x + w - (w/4), y + h);
				rnapath.lineTo(x + w - (w/4), y);
				for(int i=(minnum/2+1);i<=(minnum/2 + minmaxdiff);i++){
					rnarangedashes.moveTo(x + (w/4), y + i*ddist);
					rnarangedashes.lineTo(x + (w/4) + (w/10), y + i*ddist);
					rnarangedashes.moveTo(x + (w/4) + 2*(w/10), y + i*ddist);
					rnarangedashes.lineTo(x + (w/4) + 3*(w/10), y + i*ddist);
					rnarangedashes.moveTo(x + (w/4) + 4*(w/10), y + i*ddist);
					rnarangedashes.lineTo(x + w - (w/4), y + i*ddist);
				}
			}
			for(int i=(minnum/2 + minmaxdiff + 1);i<=maxnum;i++){
				rnapath.moveTo(x + (w/4), y + i*ddist);
				rnapath.lineTo(x + w - (w/4), y + i*ddist);
			}
		}
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
			this.stem.switchSides();
			this.stem.reverseStrings();
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
	 * @param type, the language of the String
	 */
	public String beginTraversal(int type){
		String shapestring = "";
		if(startangle == theta){
			shapestring += Translator.getLeftContent(this.stem, type,-1);
			shapestring += exits[0].traverse(startangle,type);
			shapestring += Translator.getRightContent(this.stem, type,-1);
			if(!this.getIsEndElement() && exits[1] != null && exits[1].getType() == EditorGui.SS_DOUBLE){
				SingleShape next = ((DoubleSingleShape)exits[1]).getThreePrime();
				shapestring += next.traverse(startangle, type);
			}
		}
		else{ 
			shapestring += Translator.getLeftContent(this.stem, type,-1);
			shapestring += exits[1].traverse(startangle,type);
			shapestring += Translator.getRightContent(this.stem, type,-1);
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
	 * @param type, the language of the String
	 */
	protected String traverse(int angle, int type){
		String shapestring = "";
		//continue in theta's direction
		if(angle == theta){
			if(exits[0] != null){
				shapestring += Translator.getLeftContent(this.stem, type,-1);
				shapestring += exits[0].traverse(angle,type);
				shapestring += Translator.getRightContent(this.stem, type,-1);
				return shapestring;
			}
			else{
				return "ERROR: stemShape.traverse";
			}
		}
		//continue in (theta+offsetangle)'s direction
		else if(angle == ((theta+offsetangle)%360)){
			if(exits[1] != null){
				shapestring += Translator.getLeftContent(this.stem, type,-1);
				shapestring += exits[1].traverse(angle,type);
				shapestring += Translator.getRightContent(this.stem, type,-1);
				return shapestring;
			}
			else{
				return "ERROR: stemShape.traverse";
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
		shapestring += Translator.getLeftContent(this.stem, Translator.SHAPE_LANG,-1);
		shapestring += " R ";
		shapestring += Translator.getRightContent(this.stem, Translator.SHAPE_LANG,-1);
		if(startangle == theta){
			if(exits[0] != null){   	
				shapestring = Translator.getLeftContent(this.stem, Translator.SHAPE_LANG,-1);
				shapestring += exits[0].ctraverseF(startangle);
				shapestring += Translator.getRightContent(this.stem, Translator.SHAPE_LANG,-1);
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
				shapestring = Translator.getLeftContent(this.stem, Translator.SHAPE_LANG,-1);
				shapestring += exits[1].ctraverseF(startangle);
				shapestring += Translator.getRightContent(this.stem, Translator.SHAPE_LANG,-1);
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
				shapestring += Translator.getLeftContent(this.stem, Translator.SHAPE_LANG,-1);
				shapestring += exits[0].ctraverseF(angle);
				shapestring += Translator.getRightContent(this.stem, Translator.SHAPE_LANG,-1);
			}
			else{
				shapestring += Translator.getLeftContent(this.stem, Translator.SHAPE_LANG,-1);
				shapestring += " R ";
				shapestring += Translator.getRightContent(this.stem, Translator.SHAPE_LANG,-1);
			}
		}
		//continue in (theta+offsetangle)'s direction
		else if(angle == ((theta+offsetangle)%360)){
			if(exits[1] != null){
				shapestring += Translator.getLeftContent(this.stem, Translator.SHAPE_LANG,-1);
				shapestring += exits[1].ctraverseF(angle);
				shapestring += Translator.getRightContent(this.stem, Translator.SHAPE_LANG,-1);
			}
			else{
				shapestring += Translator.getLeftContent(this.stem, Translator.SHAPE_LANG,-1);
				shapestring += " R ";
				shapestring += Translator.getRightContent(this.stem, Translator.SHAPE_LANG,-1);
			} 
		}
		return shapestring;
	}

	/**
	 * starts a shift due to a size change
	 */
	public void beginnShift(){
		AffineTransform movetransform = new AffineTransform();
		if(findSS((theta+offsetangle)%360)){
			Line2D.Double oldline = oldmtheta.getLine();
			Line2D.Double newline = mtheta.getLine();
			movetransform.setToTranslation(oldline.getX1()-newline.getX1(),oldline.getY1()-newline.getY1());
			changeLocation(movetransform);
			oldline = oldmoffset.getLine();
			newline = moffset.getLine();
			movetransform.setToTranslation(newline.getX1()-oldline.getX1(),newline.getY1()-oldline.getY1());
			traverseShift(movetransform, theta);
		}
		else{
			Line2D.Double oldline = oldmtheta.getLine();
			Line2D.Double newline = mtheta.getLine();
			movetransform.setToTranslation(newline.getX1()-oldline.getX1(),newline.getY1()-oldline.getY1());
			traverseShift(movetransform, ((theta+offsetangle)%360));
		}
	}
	
	/**
	 * Method for adjusting the both strings according to a traversal 
	 * for a new start
	 */
	public void switchsides(){
		this.stem.switchSides();
		this.stem.reverseBPSeq();
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
	        new StemEdit(this,ds,type);
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
			color = new Color(250,190,112,70);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		//fixed=false, color is transparent
		else{
			color = new Color(250,190,112,50);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		AffineTransform oldtransf = g2.getTransform();
		g2.transform(at); //do possible rotation
		
		//draw box
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


