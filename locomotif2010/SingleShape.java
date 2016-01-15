package rnaeditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

/**
 * This class implements a SingleShape which represents a single stranded 
 * sequence graphically
 *
 * @author Janina Reeder
 */
public class SingleShape extends RnaShape implements Serializable {
	private static final long serialVersionUID = -6625117217516317469L;
	private SingleStrand single; //the underlying data structure
	private float choseny;
	private float chosenx;
	private RnaShape exitfive;
	private int exitfiveangle;
	private RnaShape exitthree;
	private int exitthreeangle;
	public DoubleSingleShape dssthree;
	public DoubleSingleShape dssfive;
	private float strx, stry;
	private float px1,py1,px2,py2,px3,py3,px4,py4;
	private float bax,bay,bbx,bby,bcx,bcy,bdx,bdy,bex,bey,bfx,bfy,bgx,bgy,bhx,bhy;
	private boolean thetaattached;
	Point2D test1;
	Line2D test2;
	
	/**
	 * Constructor for a single strand shape
	 * 
	 * @param orientation, true = standard, false = reverse
	 */
	public SingleShape(boolean orientation, FreeMagnets fm){
		xloc = 0;
		yloc = 0;
		width = 20;
		height = 80;
		choseny = 0;
		chosenx = 0;
		//area is a simple rectangle
		area = new Area(new Rectangle2D.Double(xloc,yloc,width,height));
		rnapath = new GeneralPath();
		single = new SingleStrand(orientation);    
		bb = this.single;
		color = Color.pink;
		theta = 0;
		offsetangle = 180;
		currentrotation = 0;
		at = new AffineTransform();
		exitfive = null;
		exitthree = null;
		exitfiveangle = 0;
		exitthreeangle = 0;
		standardorientation = orientation;
		selected = false;
		mtheta = new Magnet(new Line2D.Double(xloc+width,yloc+height,xloc,yloc+height),this,theta,false);
		moffset = new Magnet(new Line2D.Double(xloc,yloc,xloc+width,yloc),this,offsetangle,false);
		moffset.setIsHairpinAccessible(false);
		mtheta.setIsHairpinAccessible(false);
		this.fm = fm;
		isstartelement = false;
		isendelement = false;
		startangle = -1;
		endangle = -1;
		rotations = new Vector<Rotation>();
		dssthree = null;
		dssfive = null;
		strx=0;
		stry=0;
		px1=0;
		py1=0;
		px2=0;
		py2=0;
		px3=0;
		py3=0;
		px4=0;
		py4=0;
		bax = 0;
		bbx = 0;
		bcx = 0;
		bdx = 0;
		bex= 0;
		bfx = 0;
		bgx = 0;
		bhx = 0;
		bay = 0;
		bby = 0;
		bcy = 0;
		bdy = 0;
		bey = 0;
		bfy = 0;
		bgy = 0;
		bhy = 0;
		thetaattached = false;
	}
	
	
	/**
	 * Initialization of a stored single strand
	 */
	private void initializeSingle(){
		//area is a single rectangle
		loadUpAreaAndPath();
		color = Color.pink;
		selected = false;
		this.bb = single;
		setMotifHead();
	}
	
	/**
	 * Getter for the type of this building block
	 * 
	 * @return EditorGui.SINGLE_TYPE
	 */
	public int getType(){
		return EditorGui.SINGLE_TYPE;
	}
	
	/**
	 * Storage Method
	 * 
	 * @param s, the ObjectOutputStream
	 */ 
	private void writeObject(ObjectOutputStream s) throws IOException{
		s.writeDouble(xloc);
		s.writeDouble(yloc);
		s.writeDouble(width);
		s.writeDouble(height);
		s.writeObject(single);
		s.writeInt(theta);
		s.writeInt(offsetangle);
		s.writeInt(currentrotation);
		s.writeObject(at);
		s.writeObject(exitfive);
		s.writeInt(exitfiveangle);
		s.writeObject(exitthree);
		s.writeInt(exitthreeangle);
		s.writeBoolean(standardorientation);
		s.writeObject(mtheta);
		s.writeObject(moffset);
		s.writeObject(fm);
		s.writeBoolean(isstartelement);
		s.writeBoolean(isendelement);
		s.writeInt(startangle);
		s.writeInt(endangle);
		s.writeObject(rotations);
		s.writeObject(dssthree);
		s.writeObject(dssfive);
		s.writeFloat(strx);
		s.writeFloat(stry);
		s.writeFloat(chosenx);
		s.writeFloat(choseny);
		s.writeFloat(px1);
		s.writeFloat(py1);
		s.writeFloat(px2);
		s.writeFloat(py2);
		s.writeFloat(px3);
		s.writeFloat(py3);
		s.writeFloat(px4);
		s.writeFloat(py4);
		s.writeFloat(bax);
		s.writeFloat(bay);
		s.writeFloat(bbx);
		s.writeFloat(bby);
		s.writeFloat(bcx);
		s.writeFloat(bcy);
		s.writeFloat(bdx);
		s.writeFloat(bdy);
		s.writeFloat(bex);
		s.writeFloat(bey);
		s.writeFloat(bfx);
		s.writeFloat(bfy);
		s.writeFloat(bgx);
		s.writeFloat(bgy);
		s.writeFloat(bhx);
		s.writeFloat(bhy);
		s.writeInt(startindex);
	}
	
	/**
	 * Storage Method
	 * 
	 * @param s, the ObjectInputStream
	 */ 
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream s) throws IOException,ClassNotFoundException{
		xloc = s.readDouble();
		yloc = s.readDouble();
		width = s.readDouble();
		height = s.readDouble();
		single = (SingleStrand) s.readObject();
		theta = s.readInt();
		offsetangle = s.readInt();
		currentrotation = s.readInt();
		at = (AffineTransform) s.readObject();
		exitfive = (RnaShape) s.readObject();
		exitfiveangle = s.readInt();
		exitthree = (RnaShape) s.readObject();
		exitthreeangle = s.readInt();
		standardorientation = s.readBoolean();
		mtheta = (Magnet) s.readObject();
		moffset = (Magnet) s.readObject();
		fm = (FreeMagnets) s.readObject();
		isstartelement = s.readBoolean();
		isendelement = s.readBoolean();
		startangle = s.readInt();
		endangle = s.readInt();
		rotations = (Vector<Rotation>) s.readObject();
		dssthree = (DoubleSingleShape) s.readObject();
		dssfive = (DoubleSingleShape) s.readObject();
		strx = s.readFloat();
		stry = s.readFloat();
		chosenx = s.readFloat();
		choseny = s.readFloat();
		px1 = s.readFloat();
		py1 = s.readFloat();
		px2 = s.readFloat();
		py2 = s.readFloat();
		px3 = s.readFloat();
		py3 = s.readFloat();
		px4 = s.readFloat();
		py4 = s.readFloat();
		bax = s.readFloat();
		bay = s.readFloat();
		bbx = s.readFloat();
		bby = s.readFloat();
		bcx = s.readFloat();
		bcy = s.readFloat();
		bdx = s.readFloat();
		bdy = s.readFloat();
		bex = s.readFloat();
		bey = s.readFloat();
		bfx = s.readFloat();
		bfy = s.readFloat();
		bgx = s.readFloat();
		bgy = s.readFloat();
		bhx = s.readFloat();
		bhy = s.readFloat();
		startindex = s.readInt();
		initializeSingle();
	}
	
	/**
	 * Getter Method for the SingleStrand this Shape stores
	 *
	 * @return this.single
	 */
	public SingleStrand getSingleStrand(){
		return this.single;
	}
	
	/**
	 * Returns the RnaShape neighboring this one at the 5' end
	 * 
	 * @return the 5' RnaShape
	 */
	public RnaShape getFivePrimeEnd(){
		return exitfive;
	}
	
	/**
	 * Returns the RnaShape neighboring this one at the 3' end
	 * 
	 * @return the 3' RnaShape
	 */
	public RnaShape getThreePrimeEnd(){
		return exitthree;
	}
	
	/**
	 * Returns the orientation of RnaShape neighboring this one at the 5' end
	 * 
	 * @return the angle of the 5' neighbor
	 */
	public int getFivePrimeAngle(){
		return exitfiveangle;
	}
	
	/**
	 * Returns the orientation of RnaShape neighboring this one at the 3' end
	 * 
	 * @return the angle of the 3' neighbor
	 */
	public int getThreePrimeAngle(){
		return exitthreeangle;
	}
	
	/**
	 * This method determines wether it is allowed to remove this Single Strand Shape.
	 * A single strand can only be removed if it is the start or end element of the 
	 * structure or if it is connected to the start or end element directly.
	 * 
	 * @return true or false.
	 */
	public boolean removalAllowed(){
		if(this.getIsStartElement() || this.getIsEndElement()){
			return true;
		}
		if(exitfive != null && (exitfive.getIsStartElement() || exitfive.getIsEndElement())){
			return true;
		}
		if(exitthree != null && (exitthree.getIsStartElement() || exitthree.getIsEndElement())){
			return true;
		}
		return false;
	}
	
	/**
	 * This method sets the flag switchforced. Used internally for traversals.
	 * Overwrites the method with empty body.
	 * 
	 * @param switchforced
	 */
	public void setSwitchForced(boolean switchforced){
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
		single.setOrientation(standardorientation);
		DoubleSingleShape dssbuf = dssthree;
		dssthree = dssfive;
		dssfive = dssbuf;
		RnaShape exitbuf = exitthree;
		int anglebuf = exitthreeangle;
		exitthree = exitfive;
		exitthreeangle = exitfiveangle;
		exitfive = exitbuf;
		exitfiveangle = anglebuf;
		if(seqreversal){
			this.single.reverseStrings();
		}
	}
	
	
	/**
	 * This method initializes the Line that is to be drawn
	 *
	 * @param Magnet m, the Magnet, that the line is attached to
	 * @param double mex, the x-coordinate it leads to
	 * @param double mey, the y-coordinate it leads to
	 */
	public void initializeLine(Magnet m, double mex, double mey){
		rnapath = new GeneralPath();
		float x = (float) xloc;
		chosenx = x;
		float y = (float) yloc;
		float w = (float) width;
		float h = (float) height;
		px1 = chosenx + (w/2);
		mtheta = m;
		if(m.getAngle() != (theta+offsetangle)%360){
			choseny = y;
			py1 = y;
			bax = chosenx + w;
			bay = choseny;
			bbx = chosenx + w;
			bgx = chosenx;
			bhx = chosenx;
			bhy = choseny;
			thetaattached = true;
		}
		else{
			py1 = y + h;
			choseny = y + h;
			bax = chosenx;
			bay = choseny;
			bbx = chosenx;
			bgx = chosenx + w;
			bhx = chosenx + w;
			bhy = choseny;
			thetaattached = false;
		}
		rnapath.moveTo(px1, py1);
		rnapath.lineTo((float)mex,(float)mey);
	}
	
	/**
	 * Adjust the currently showing line according to mouse movements
	 * 
	 * @param double mex, the new x-coordinate it leads to
	 * @param double mey, the new y-coordinate it leads to
	 */
	public void adjustCurrentLine(double mex, double mey){
		Point2D.Double movepoint = new Point2D.Double(mex, mey);
		try{
			at.inverseTransform(movepoint,movepoint);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		float xp = (float) movepoint.getX();
		float yp = (float) movepoint.getY();
		rnapath = new GeneralPath();
		rnapath.moveTo(px1, py1);
		rnapath.lineTo(xp, yp);
	}
	
	/**
	 * This method stores the final Path that the single strand line is set to.
	 * Here, the frame of the single shape is added to the line.
	 */
	public void storeFinalPath(){
		Point2D p = rnapath.getCurrentPoint();
		float w = (float) width;
		float xp = (float) p.getX();
		float yp = (float) p.getY();
		float l = (float) Math.sqrt(Math.pow((p.getX()-(chosenx+(w/2))),2) + Math.pow((p.getY()-choseny),2));
		px4 = xp + ((xp-(chosenx+(w/2)))/l)*10;
		py4 = yp + ((yp-choseny)/l)*10;
		px3 = px4;
		py3 =px4;
		
		rnapath = new GeneralPath();
		rnapath.moveTo(px1, py1);
		px2 = chosenx + (w/2);
		if(thetaattached){
			py2 = choseny + 10;
			bby = choseny + 10;
			bgy = choseny + 10;
			bcx = xp + (w/2);
			bex = xp - (w/2);
		}
		else{
			py2 = choseny - 10;
			bby = choseny - 10;
			bgy = choseny - 10;
			bcx = xp - (w/2);
			bex = xp + (w/2);
		}
		rnapath.lineTo(px2,py2);
		rnapath.lineTo(px4,py4);
		
		strx = xp + ((xp-(chosenx+(w/2)))/l)*20;
		stry = yp + ((yp-choseny)/l)*20;

		
		GeneralPath ssoutline = new GeneralPath();
		bcy = yp;
		bdx = bcx;
		bdy = bcy;
		bey = yp;
		bfx = bex;
		bfy = bey;
		
		ssoutline.moveTo(bax,bay);
		ssoutline.lineTo(bbx,bby);
		ssoutline.lineTo(bcx,bcy);
		ssoutline.lineTo(bdx,bdy);
		ssoutline.lineTo(bex,bey);
		ssoutline.lineTo(bfx,bfy);
		ssoutline.lineTo(bgx,bgy);
		ssoutline.lineTo(bhx,bhy);
		ssoutline.closePath();

		area = new Area(ssoutline);
		setMotifHead();
	}   
	
	public void loadUpAreaAndPath(){
		rnapath = new GeneralPath();
		rnapath.moveTo(px1,py1);
		rnapath.lineTo(px2, py2);
		rnapath.lineTo(px3,py3);
		rnapath.lineTo(px4,py4);
		GeneralPath ssoutline = new GeneralPath();
		ssoutline.moveTo(bax,bay);
		ssoutline.lineTo(bbx,bby);
		ssoutline.lineTo(bcx,bcy);
		ssoutline.lineTo(bdx,bdy);
		ssoutline.lineTo(bex,bey);
		ssoutline.lineTo(bfx,bfy);
		ssoutline.lineTo(bgx,bgy);
		ssoutline.lineTo(bhx,bhy);
		ssoutline.closePath();
		area = new Area(ssoutline);
	}
	
	/**
	 * Checks wether any of the two ends of the single shape contains the
	 * given Point
	 * 
	 * @param Point2D.Double, the Point2D to search for
	 * @return true or false
	 */
	public boolean endContains(Point2D.Double p){
		if(mtheta != null && mtheta.contains(p)){
			return true;
		}
		if(moffset != null && moffset.contains(p)){
			return true;
		}
		return false;
	}
	
	
	/**
	 * This method changes the size of the Single Shape, i.e. changes its Area.
	 * not needed for Single Strand. Size cannot be changed.
	 *
	 * @param seglength, the sequence length upon which the size depends
	 */
	public void changeSize(int seqlength){
	}
	
	/**
	 * This method changes the size of the Single Shape, i.e. changes its Area
	 * according to a median value for the sequence length.
	 * not needed for Single Strand. Size cannot be changed.
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
		adjustPath();
		if(currentrotation != 0){
			at.setToIdentity();
			at.rotate(StrictMath.toRadians(currentrotation),xloc,yloc);
			rotations.clear();
			rotations.add(new Rotation(currentrotation, 1));
		}
	}
	
	/**
	 * This method changes the location according to a transformation
	 * stored in the AffineTransform
	 *
	 * @param AffineTransform stores a move transform that the ss should
	 * make
	 */
	public void changeLocation(AffineTransform movetransform){
		Point2D.Double movepoint = new Point2D.Double(xloc,yloc);
		movetransform.transform(movepoint,movepoint);
		xloc = movepoint.getX();
		yloc = movepoint.getY();
		Point2D.Float movepoint2 = new Point2D.Float(strx,stry);
		movetransform.transform(movepoint2,movepoint2);
		strx = (float) movepoint2.getX();
		stry = (float) movepoint2.getY();
		Point2D.Float movepoint3 = new Point2D.Float(px1,py1);
		movetransform.transform(movepoint3,movepoint3);
		px1 = (float) movepoint3.getX();
		py1 = (float) movepoint3.getY();
		movepoint3 = new Point2D.Float(px2,py2);
		movetransform.transform(movepoint3,movepoint3);
		px2 = (float) movepoint3.getX();
		py2 = (float) movepoint3.getY();
		movepoint3 = new Point2D.Float(px3,py3);
		movetransform.transform(movepoint3,movepoint3);
		px3 = (float) movepoint3.getX();
		py3 = (float) movepoint3.getY();
		movepoint3 = new Point2D.Float(px4,py4);
		movetransform.transform(movepoint3,movepoint3);
		px4 = (float) movepoint3.getX();
		py4 = (float) movepoint3.getY();
		movepoint3 = new Point2D.Float(px4,py4);
		movetransform.transform(movepoint3,movepoint3);
		px4 = (float) movepoint3.getX();
		py4 = (float) movepoint3.getY();
		movepoint3 = new Point2D.Float(bax,bay);
		movetransform.transform(movepoint3,movepoint3);
		bax = (float) movepoint3.getX();
		bay = (float) movepoint3.getY();
		movepoint3 = new Point2D.Float(bbx,bby);
		movetransform.transform(movepoint3,movepoint3);
		bbx = (float) movepoint3.getX();
		bby = (float) movepoint3.getY();
		movepoint3 = new Point2D.Float(bcx,bcy);
		movetransform.transform(movepoint3,movepoint3);
		bcx = (float) movepoint3.getX();
		bcy = (float) movepoint3.getY();
		movepoint3 = new Point2D.Float(bdx,bdy);
		movetransform.transform(movepoint3,movepoint3);
		bdx = (float) movepoint3.getX();
		bdy = (float) movepoint3.getY();
		movepoint3 = new Point2D.Float(bex,bey);
		movetransform.transform(movepoint3,movepoint3);
		bex = (float) movepoint3.getX();
		bey = (float) movepoint3.getY();
		movepoint3 = new Point2D.Float(bfx,bfy);
		movetransform.transform(movepoint3,movepoint3);
		bfx = (float) movepoint3.getX();
		bfy = (float) movepoint3.getY();
		movepoint3 = new Point2D.Float(bgx,bgy);
		movetransform.transform(movepoint3,movepoint3);
		bgx = (float) movepoint3.getX();
		bgy = (float) movepoint3.getY();
		movepoint3 = new Point2D.Float(bhx,bhy);
		movetransform.transform(movepoint3,movepoint3);
		bhx = (float) movepoint3.getX();
		bhy = (float) movepoint3.getY();
		movepoint3 = new Point2D.Float(chosenx,choseny);
		movetransform.transform(movepoint3,movepoint3);
		chosenx = (float) movepoint3.getX();
		choseny = (float) movepoint3.getY();
		area.transform(movetransform);
		rnapath.transform(movetransform);
		mtheta.transform(movetransform);
		moffset.transform(movetransform);
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
	}

	//not needed for this building block: ss have no magnets themselves
	public void adjustMagnets(){}

	/**
	 * irrelevant for this building block
	 */
	public void adjustMagnetAccessability(boolean accessible){
		moffset.setIsHairpinAccessible(false);
		mtheta.setIsHairpinAccessible(false);
	}

	/**
	 * not needed for ss
	 */
	public void hairpinAccessTraversal(int angle){
		System.out.println("hairpinAccessTraversal in SS: should not occur!!!");
	}

	/**
	 * not needed for ss
	 */
	public void hairpinAccessTraversal(int angle, Vector<Magnet> openmagnets){
		System.out.println("hairpinAccessTraversal in SS: should not occur!!!");
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
		// main shape
		rnapath.moveTo(x + (w/2), y);
		rnapath.lineTo(x + (w/2), y + h);
	}
	
	/**
	 * This method implements a snapping mechanism which takes place if 
	 * the shape is close to a free Magnet
	 *
	 * @param m, the Magnet to which the shape snaps
	 */
	public void snapTo(Magnet m){
		Line2D.Double line = m.getLine();
		double x1 = line.getX1();
		double y1 = line.getY1();
		double x2 = line.getX2();
		double y2 = line.getY2();
		if(m.getAngle()==theta){
			//the angles fit together at theta's side
			changeLocation(x2,y2,false);
		}
		else if(m.getAngle()==((theta+offsetangle)%360)){
			//the angles fit together at theta+offsetangle's side
			changeLocation(x1,y1-height,true);
		}
		//snap to theta
		else if(currentrotation == 0){
			changeLocation(x2,y2);
			at.rotate(StrictMath.toRadians(m.getAngle()),xloc,yloc);
			rotations.add(new Rotation(m.getAngle(),1));
			theta = StrictMath.abs(m.getAngle());
		}
	}
	
	/**
	 * Performs the snapping mechanism for a currently drawn line still 
	 * attached to the mouse cursor
	 * 
	 * @param Magnet m, the Magnet that the line snaps to
	 */
	public void lineSnapTo(Magnet m){
		Line2D.Double line = m.getLine();
		Point2D magnetpoint1 = line.getP1();
		Point2D magnetpoint2 = line.getP2();
		Point2D movepoint1 = new Point2D.Float();
		Point2D movepoint2 = new Point2D.Float();
		GeneralPath ssoutline = new GeneralPath();
		Line2D.Float pathline;
		Point2D startcomparison, endcomparison = null;
		rnapath = new GeneralPath();
		moffset = m;
		try{
			at.inverseTransform(magnetpoint1,movepoint1);
			at.inverseTransform(magnetpoint2,movepoint2);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		float transx1 = (float) movepoint1.getX();
		float transy1 = (float) movepoint1.getY();
		float transx2 = (float) movepoint2.getX();
		float transy2 = (float) movepoint2.getY();
		float transx = transx1 + ((transx2-transx1)/2);
		float transy = transy1 + ((transy2-transy1)/2);
		float w = (float) width;
		
		
		float outymin, outymax, outy1min, outy1max, outy2min, outy2max, outx1min, outx1max, outx2min, outx2max;
		

		bdx = transx2;
		bdy = transy2;
		bex = transx1;
		bey = transy1;
		
		rnapath.moveTo(px1, py1);
		px2 = chosenx + (w/2);
		if(thetaattached){
			py2 = choseny + 20;
			startcomparison = new Point2D.Float(chosenx + (w/2), choseny + 50);
			outymin = choseny + 11;
			outymax = choseny + 31;
		}
		else{
			py2 = choseny - 20;
			startcomparison = new Point2D.Float(chosenx + (w/2), choseny - 50);
			outymin = choseny - 11;
			outymax = choseny - 31;
		}
		rnapath.lineTo(px2,py2);
	
		if(m.getAngle() == theta){
			px3 = transx;
			py3 = transy + 20;
			endcomparison = new Point2D.Float(transx, transy + 50);
			outy1min = transy1 + 11;
			outy1max = transy1 + 31;
			outy2min = transy2 + 11;
			outy2max = transy2 + 31;
			outx2min = transx2;
			outx2max = transx2;
			outx1min = transx1;
			outx1max = transx1;
		}
		else if(m.getAngle() == (theta+offsetangle)%360){
			px3 = transx;
			py3 = transy - 20;
			endcomparison = new Point2D.Float(transx, transy - 50);
			outy1min = transy1 - 11;
			outy1max = transy1 - 31;
			outy2min = transy2 - 11;
			outy2max = transy2 - 31;
			outx2min = transx2;
			outx2max = transx2;
			outx1min = transx1;
			outx1max = transx1;
		}
		else{
			AffineTransform mat = new AffineTransform();
			mat.setToRotation(Math.toRadians(360 - ((m.getAngle()+offsetangle)%360)), magnetpoint1.getX(), magnetpoint1.getY());
			mat.transform(magnetpoint2, magnetpoint2);
			float endx = (float)(magnetpoint1.getX() + ((magnetpoint2.getX()-magnetpoint1.getX())/2));
			float endy = (float)(magnetpoint1.getY() + ((magnetpoint2.getY()-magnetpoint1.getY())/2));
			Point2D realend = new Point2D.Float(endx, endy - 20);
			Point2D real1min = new Point2D.Float((float)magnetpoint1.getX(), (float)magnetpoint1.getY() - 11);
			Point2D real1max = new Point2D.Float((float)magnetpoint1.getX(), (float)magnetpoint1.getY() - 31);
			Point2D real2min = new Point2D.Float((float)magnetpoint2.getX(), (float)magnetpoint2.getY() - 11);
			Point2D real2max = new Point2D.Float((float)magnetpoint2.getX(), (float)magnetpoint2.getY() - 31);
			endcomparison = new Point2D.Float(endx, endy - 50);
			try {
				mat.inverseTransform(realend,realend);
				mat.inverseTransform(real1min,real1min);
				mat.inverseTransform(real1max,real1max);
				mat.inverseTransform(real2min,real2min);
				mat.inverseTransform(real2max,real2max);
				mat.inverseTransform(endcomparison, endcomparison);
				at.inverseTransform(realend,realend);
				at.inverseTransform(real1min,real1min);
				at.inverseTransform(real1max,real1max);
				at.inverseTransform(real2min,real2min);
				at.inverseTransform(real2max,real2max);
				at.inverseTransform(endcomparison, endcomparison);
			} catch (NoninvertibleTransformException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			px3 = (float)realend.getX();
			py3 = (float)realend.getY();
			outy1min = (float)real1min.getY();
			outy1max = (float)real1max.getY();
			outx1min = (float)real1min.getX();
			outx1max = (float)real1max.getX();
			outy2min = (float)real2min.getY();
			outy2max = (float)real2max.getY();
			outx2min = (float)real2min.getX();
			outx2max = (float)real2max.getX();
		}

		pathline = new Line2D.Float(px2,py2, px3,py3);
		test2 = pathline;
		test1 = endcomparison;
		rnapath.lineTo(px3,py3);
		px4 = transx;
		py4 = transy;
		rnapath.lineTo(px4, py4);
		
		
		if(pathline.relativeCCW(startcomparison) >= 0){
			bby = outymax;
			bgy = outymin;
		}
		else{
			bby = outymin;
			bgy = outymax;
		}
		
		pathline = new Line2D.Float(px3,py3, px2,py2);

		if(pathline.relativeCCW(endcomparison) >= 0){
			bcx = outx2min;
			bcy = outy2min;
			bfx = outx1max;
			bfy = outy1max;
		}
		else{
			bcx = outx2max;
			bcy = outy2max;
			bfx = outx1min;
			bfy = outy1min;
		}
		
		Line2D.Float testline = new Line2D.Float(bbx,bby,bcx,bcy);
		if(testline.intersectsLine(bgx,bgy,bfx,bfy)){
			float bufcx = bcx;
			float bufcy = bcy;
			float bufdx = bdx;
			float bufdy = bdy;
			bcx = bfx;
			bcy = bfy;
			bdx = bex;
			bdy = bey;
			bfx = bufcx;
			bfy = bufcy;
			bex = bufdx;
			bey = bufdy;
		}

		ssoutline.moveTo(bax, bay);
		ssoutline.lineTo(bbx,bby);
		ssoutline.lineTo(bcx,bcy);
		ssoutline.lineTo(bdx,bdy);
		ssoutline.lineTo(bex,bey);
		ssoutline.lineTo(bfx,bfy);
		ssoutline.lineTo(bgx,bgy);
		ssoutline.lineTo(bhx,bhy);
		ssoutline.closePath();
		
		
		area = new Area(ssoutline);
		
		if(!area.contains(px2,py2)){
			px2 = px1;
			py2 = py1;
			bbx = bax;
			bby = bay;
			bgx = bhx;
			bgy = bhy;		
			rnapath = new GeneralPath();
			ssoutline = new GeneralPath();
			rnapath.moveTo(px1, py1);
			rnapath.lineTo(px2,py2);
			rnapath.lineTo(px3,py3);
			rnapath.lineTo(px4, py4);
			ssoutline.moveTo(bax, bay);
			ssoutline.lineTo(bbx,bby);
			ssoutline.lineTo(bcx,bcy);
			ssoutline.lineTo(bdx,bdy);
			ssoutline.lineTo(bex,bey);
			ssoutline.lineTo(bfx,bfy);
			ssoutline.lineTo(bgx,bgy);
			ssoutline.lineTo(bhx,bhy);
			ssoutline.closePath();
			area = new Area(ssoutline);
		}
		if(!area.contains(px3,py3)){
			px3 = px4;
			py3 = py4;
			bcx = bdx;
			bcy = bdy;
			bfx = bex;
			bfy = bey;	
			rnapath = new GeneralPath();
			ssoutline = new GeneralPath();
			rnapath.moveTo(px1, py1);
			rnapath.lineTo(px2,py2);
			rnapath.lineTo(px3,py3);
			rnapath.lineTo(px4, py4);
			ssoutline.moveTo(bax, bay);
			ssoutline.lineTo(bbx,bby);
			ssoutline.lineTo(bcx,bcy);
			ssoutline.lineTo(bdx,bdy);
			ssoutline.lineTo(bex,bey);
			ssoutline.lineTo(bfx,bfy);
			ssoutline.lineTo(bgx,bgy);
			ssoutline.lineTo(bhx,bhy);
			ssoutline.closePath();
			area = new Area(ssoutline);
		}
	}
	
	/**
	 * Setter method for connector flag in SingleStrand
	 *
	 */
	public void setIsConnector(){
		this.single.setIsConnector();
	}
	
	/**
	 * Checks whether this single strand is a connector
	 * 
	 * @return true, if SingleShape connects 2 motif parts
	 */
	public boolean getIsConnector(){
		return single.getIsConnector();
	}
	
	/**
	 * restores the affine transformation to identity, 
	 * i.e. all rotations are undone
	 */
	public void restore(){
			try{
				mtheta.transform(at.createInverse());
				moffset.transform(at.createInverse());
			}
			catch(NoninvertibleTransformException nte){
				System.out.println("Rotation exception in restore");
			}
			at.setToIdentity();
			rotations.clear();
			if(currentrotation!=0){
				at.rotate(StrictMath.toRadians(currentrotation),xloc+width/2,yloc+height/2);
				mtheta.transform(at);
				moffset.transform(at);
				rotations.add(new Rotation(currentrotation,3));
			}
			theta = currentrotation;
	}
	
	/**
	 * This method rotates our Shape around the center of the shape
	 * wird nicht aufgerufen!!!! SS kann alleine nicht gedreht werden!
	 *
	 * @param int degree, the angle of rotation
	 */
	public void rotateBy(int degree){ 
		at.rotate(StrictMath.toRadians(degree),xloc+width/2,yloc+height/2);
		rotations.add(new Rotation(degree,3));
		theta += degree;
		theta = theta %360;
		currentrotation += degree;
		currentrotation = currentrotation %360;
		mtheta.transform(AffineTransform.getRotateInstance(StrictMath.toRadians(degree),xloc+width/2,yloc+height/2));
		moffset.transform(AffineTransform.getRotateInstance(StrictMath.toRadians(degree),xloc+width/2,yloc+height/2));
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
		try{
			mtheta.transform(at.createInverse());
			moffset.transform(at.createInverse());
		}
		catch(NoninvertibleTransformException nte){
			System.out.println("Rotation exception in restore");
		}
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
		if(exitfive != null){
			exitfiveangle += degree;
			exitfiveangle = exitfiveangle % 360;
		}
		if(exitthree != null){
			exitthreeangle += degree;
			exitthreeangle = exitthreeangle % 360;
		}
		mtheta.transform(at);
		moffset.transform(at);
	}


	/**
	 * This method finds the new start element starting from the next exit
	 * specified by the given index
	 *
	 * @param int index, the index from where the start
	 * @return RnaShape, the new start element
	 */
	public RnaShape findNewStartElement(int index){
		if(index == 1 && exitthree != null){
			exitthree.setIsStartElement(true,startangle,1);
			return exitthree;
		}
		else if(exitfive != null){
			exitfive.setIsStartElement(true,startangle,1);
			return exitfive;
		}
		return null;
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
		if(exitfive == null){
			setIsStartElement(true, exitthreeangle, 0);
			return this;
		}
		else{
			return this.exitfive.traverseToNewStart((exitfiveangle+180)%360, real);
		}
	}


	/**
	 * This method finds the new end element starting from the next exit
	 * specified by the given index
	 *
	 * @param int index, the index from where the end
	 * @return RnaShape, the new end element
	 */
	public RnaShape findNewEndElement(int index){
		if(index == 1 && exitthree != null){
			exitthree.setIsEndElement(true,endangle,1);
			return exitthree;
		}
		else if(exitfive != null){
			exitfive.setIsEndElement(true,endangle,1);
			return exitfive;
		}
		return null;
	}


	/**
	 * Traversal method which proceeds until a new open end is found.
	 * Called if the current main open end is closed by a hairpin
	 *
	 * @param int angle, the direction of the traversal
	 * @return RnaShape this or result of a recursive call
	 */
	public RnaShape traverseToNewEnd(int angle){
		if(exitthree == null){
			setIsEndElement(true, exitfiveangle, 0);
			return this;
		}
		else{
			return this.exitthree.traverseToNewEnd((exitthreeangle+180)%360);
		}
	}


	/**
	 * @return the index of the neighbor 
	 */
	public int getEndType(){
		if(exitfive == null) return 1;
		return 0;
	}
	
	/**
	 * Method for traversing to a compound block
	 * 
	 * @param angle describing the direction of the traversal
	 * @return false, because no ambiguity issues arise when connected by single strands
	 */
	public boolean traverseToClosed(int angle){
		return false;
	}
	
	/**
	 * This method adds a SingleStrand neighbor to this element
	 *
	 *	NOT NEEDED BECAUSE SS CANNOT BE ADDED TO OTHER SS
	 *
	 * @param SingleShape shape, the shape to be added
	 * @param int angle, the angle where the shape is added
	 * @return int returns 1 if the singleshape was added as fiveprime or 
	 * 2 if it was added as threeprime
	 */
	public int addSSNeighbor(SingleShape shape, int angle){
		return -1;
	}
	
	/**
	 * adds a neighboring shape to this one
	 * 
	 * @param shape, the shape that is added as a neighbor
	 * @param angle, the angle of the exit, where the shape is neighboring
	 * @param type describes whether the building block is added as a 3' neighbor (case 1) or a 5' neighbor (case 2)
	 */
	public void addToNeighbors(RnaShape shape, int angle, int type){
		switch(type){
		case 1:
			if(exitthree != null){
				exitfive = exitthree;
				exitfiveangle = exitthreeangle;
			}
			exitthree = shape;
			exitthreeangle = angle;
			break;
		case 2: 
			if(exitfive != null){
				exitthree = exitfive;
				exitthreeangle = exitfiveangle;
			}
			exitfive = shape;
			exitfiveangle = angle;
			break;
		}
	}
	
	/**
	 * This method checks wether there is a DoubleSingleStrand at exit of the given index
	 *
	 * @param index, the index where a DSS is searched for
	 */
	public boolean hasDSSNeighbor(int index){
		return false;
	}
	
	/**
	 * This method removes all neighbors of this shape
	 */
	public void removeNeighbors(){
		if(exitfive != null){
			exitfive.remove(this);
			exitfive = null;
		}
		if(exitthree != null){
			exitthree.remove(this);
			exitthree = null;
		}
		this.dssthree = null;
		this.dssfive = null;
	}
	
	/**
	 * irrelevant for single strand
	 */
	public void resetHPAccessability(){
		mtheta.setIsHairpinAccessible(false);
		moffset.setIsHairpinAccessible(false);
	}
	
	/**
	 * This method removes a specific RnaShape from the neighbors of this
	 * shape
	 *
	 * @param RnaShape s, the shape to be removed
	 */
	public void remove(RnaShape s){
		//should not be called!
		if(exitfive == s){
			exitfive = null;
		}
		else if(exitthree == s){
			exitthree = null;
		}
	}
	
	
	/**
	 * Checks wether the SingleShape is connected to something
	 */
	public boolean getIsConnected(){
		if(this.dssfive != null || this.dssthree != null){
			return true;
		}
		return false;
	}

	
	/**
	 * Adds a new three prime DSS to the SingleShape. If an old one was 
	 * present before, it is then stored as the five prime dss.
	 *
	 * @param DoubleSingleShape, the new three prime dss
	 */
	public void addThreePrimeDSS(DoubleSingleShape dss){
		DoubleSingleShape bufdss = this.dssthree;
		this.dssthree = dss;
		if(bufdss != null){
			this.dssfive = bufdss;
			SingleShape other = this.dssfive.switchStrands(this);
			if(other != null){
				System.out.println("Should not be necessary: SS-propagation");
				other.propagateThreePrimeSwitch(this.dssfive);
			}
			exitthree.setSwitchForced(true);
			exitthree.switchSidesTraversal(exitthree, false, exitthreeangle);
		}
	}
	
	/**
	 * Adds a new five prime DSS to the SingleShape. If an old one was 
	 * present before, it is then stored as the three prime dss.
	 *
	 * @param DoubleSingleShape, the new five prime dss
	 */
	public void addFivePrimeDSS(DoubleSingleShape dss){
		DoubleSingleShape bufdss = this.dssfive;
		this.dssfive = dss;
		if(bufdss != null){
			this.dssthree = bufdss;
			SingleShape other = this.dssthree.switchStrands(this);
			if(other != null){
				System.out.println("Should not be necessary: SS-propagation");
				other.propagateFivePrimeSwitch(this.dssthree);
			}
			exitfive.setSwitchForced(true);
			exitfive.switchSidesTraversal(exitfive,false,exitfiveangle);
		}
	}
	
	/**
	 * This method is invoked if the 5'-3' end must be exchanged because a new
	 * element was added to the same DoubleSingleShape.
	 * 
	 * @param newdssfive, the new 5' DoubleSingleShape.
	 */
	public void propagateFivePrimeSwitch(DoubleSingleShape newdssfive){
		DoubleSingleShape bufdss = this.dssfive;
		this.dssfive = newdssfive;
		if(bufdss == null){
			this.dssthree = null;
			exitfive = exitthree;
			exitfiveangle = exitthreeangle;
			exitthree = null;
			exitthreeangle = 0;
		}
		else{
			this.dssthree = bufdss;
			RnaShape fivebuf = exitfive;
			int anglebuf = exitfiveangle;
			exitfive = exitthree;
			exitfiveangle = exitthreeangle;
			//changeOrientation im exitthree
			exitthree = fivebuf;
			exitthreeangle = anglebuf;
			SingleShape other = this.dssthree.switchStrands(this);
			if(other != null){
				other.propagateFivePrimeSwitch(this.dssfive);
			}
		}
	}
	
	/**
	 * This method is invoked if the 5'-3' end must be exchanged because a new
	 * element was added to the same DoubleSingleShape.
	 * 
	 * @param newdssthree, the new 3' DoubleSingleShape.
	 */
	public void propagateThreePrimeSwitch(DoubleSingleShape newdssthree){
		DoubleSingleShape bufdss = this.dssthree;
		this.dssthree = newdssthree;
		if(bufdss == null){
			this.dssfive = null;
			exitthree = exitfive;
			exitthreeangle = exitfiveangle;
			exitfive = null;
			exitfiveangle = 0;
		}
		else{
			this.dssfive = bufdss;
			RnaShape threebuf = exitthree;
			int anglebuf = exitthreeangle;
			exitthree = exitfive;
			exitthreeangle = exitfiveangle;
			//changeOrientation im exitfive
			exitfive = threebuf;
			exitfiveangle = anglebuf;
			SingleShape other = this.dssfive.switchStrands(this);
			if(other != null){
				other.propagateThreePrimeSwitch(this.dssthree);
			}
		}
	}
	
	/**
	 * Checks wether a five prime dss is stored in this SingleShape
	 * 
	 * @return true or false
	 */
	public boolean getIsFivePrimeDSSAvailable(){
		if(this.dssfive != null){
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
		if(this.exitthree == origin){
			if(this.exitfive == null){
				return false;
			}
			else if(this.exitfive == shape){
				return true;
			}
			else{
				return this.exitfive.connectedTo(shape, exitfiveangle, this);
			}
		}
		else{
			if(this.exitthree == null){
				return false;
			}
			else if(this.exitthree == shape){
				return true;
			}
			else{
				return this.exitthree.connectedTo(shape, exitthreeangle, this);
			}
		}
	}

	/**
	 * This method tries to find a SingleShape in the given direction
	 * 
	 * @param angle, the direction where to search
	 * @return true: this is a Single Strand
	 */
	public boolean findSS(int angle){
		return true;
	}


	/**
	 * Method for adjusting the both strings according to a traversal 
	 * for a new start
	 */
	public void switchsides(){
		System.out.println("Switchsides called for single strand: is it necessary???");
	}

	/**
	 * This method is used for traversing the structure with a change of 5' and
	 * 3' strands.
	 * 
	 * @param start, the origin RnaShape of the search
	 * @param fiveprimedir, the direction of the search: true indicates 5', false 3'
	 * @param angle, the angle in whose direction the search is to be continued.
	 */
	public void switchSidesTraversal(RnaShape start, boolean fiveprimedir, int angle){
		this.single.reverseStrings();
		//exits wurden noch nicht geswappt!! erst anschliessend!
		if(fiveprimedir && exitthree != null){
			exitthree.switchSidesTraversal(exitthree, fiveprimedir, exitthreeangle); //stimmt der angle???
		}
		else if(!fiveprimedir && exitfive != null){
			exitfive.switchSidesTraversal(exitfive, fiveprimedir, exitfiveangle);
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
		String shapestring = Translator.getLeftContent(this.single, type,-1);
		if(exitthree != null){
			exitthree.setIsStartElement(false,exitthreeangle,1); //wozu braucht man das?
			shapestring += exitthree.beginTraversal(type);
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
		String shapestring = Translator.getLeftContent(this.single, type,-1);
		if(exitthree != null){
			exitthree.setIsStartElement(false,exitthreeangle,1);
			shapestring += exitthree.beginTraversal(type);
		}
		return shapestring;
	}


	/**
	 * Method for a traversal and shift operation. 
	 * The RnaShape is adjusted according to a size change of another
	 * element in the RnaStructure. No shift traversal is necessary once
	 * a Single Strand was reached.
	 *
	 * @param angle, the direction the shift is going
	 */
	public void traverseShift(int angle){
	}

	/**
	 * not needed for single strand
	 */
	public void traverseShift(AffineTransform movetransform, int angle){}
	
	/**
	 * Starting method for a traversal of the current structure which can
	 * have several open ends.
	 *
	 * @return String, the current shape notation of the structure
	 */
	public String currentForwardTraversal(){
		String shapestring = Translator.getLeftContent(this.single, Translator.SHAPE_LANG,-1);
		if(exitthree != null){
			exitthree.setIsStartElement(false,exitthreeangle,1); //wozu braucht man das?
			shapestring += exitthree.currentForwardTraversal();
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
		String shapestring = Translator.getLeftContent(this.single, Translator.SHAPE_LANG,-1);
		if(exitthree != null){
			exitthree.setIsStartElement(false,exitthreeangle,1);
			shapestring += exitthree.currentForwardTraversal();
		}
		return shapestring;
	}
	
	/**
	 * method to check wether the given coordinates are contained within the
	 * RnaShape
	 *
	 * @param double x
	 * @param double y
	 * @return true else false.
	 */
	public boolean contains(double x, double y){
		Shape transarea = at.createTransformedShape(this.area);
		return transarea.intersects(x-1,y-1,2,2);
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
	        new SingleEdit(this,ds,type);
	    }
	}
	
	/**
	 * Shows the current Line of this SingleShape
	 *
	 * @param g2, the Graphics2D object that performs the drawing
	 */
	public void showCurrentLine(Graphics2D g2){
		AffineTransform oldtransf = g2.getTransform();
		g2.transform(at); //do possible rotation
		g2.setPaint(Color.black);
		g2.draw(rnapath);
		g2.setTransform(oldtransf);
	}
	
	/**
	 * Sets the motif head for this building block
	 */
	public void setMotifHead(){
		Point2D p = rnapath.getCurrentPoint();
		if(p.getY() > stry){
			motifheadarea = new Ellipse2D.Double(strx-9,stry-16,25,25);
		}
		else{
			motifheadarea = new Ellipse2D.Double(strx-9,stry-8,25,25);
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
			color = new Color(255,204,255,70);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		//fixed=false, color is transparent
		else{
			color = new Color(255,204,255,50);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		AffineTransform oldtransf = g2.getTransform();
		g2.transform(at); //do possible rotation
		
		//draw box
		g2.setPaint(color);
		g2.fill(area);

		g2.setPaint(Color.lightGray);
		g2.draw(area); //box outline
		
		
		g2.setPaint(Color.black);
		Stroke strokebuf = g2.getStroke();
		if(!single.getIsStraight()){
			g2.setStroke(new BasicStroke(1.1f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,new float[]{10.0f},0.0f));
		}
		g2.draw(rnapath); //Basepair connections + backbone
		
		
		g2.setStroke(strokebuf);
		
		//show the current orientation
		
		if(isstartelement){
			g2.setPaint(Color.RED);
			g2.draw(motifheadarea);
			g2.setPaint(Color.black);
			g2.drawString("5'",strx,stry+5);
		}
		if(isendelement){
			g2.drawString("3'",strx,stry+5);
		}

		g2.setTransform(oldtransf);
	}
}


