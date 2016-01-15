package rnaeditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
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
 * This class implements a MultiShape which represents a multiloop graphically
 *
 * @author Janina Reeder
 */
public class MultiShape extends RnaShape implements Serializable {
	private static final long serialVersionUID = 5445546914840515008L;
	private MultiLoop multiloop; //the underlying data structure
	private double circleheight; //height of the imposed circle
	private double circlewidth;
	private double rheight;
	private int[] offsetangle; //other exits
	private Magnet[] magnets;
	
	/**
	 * Constructor for a multiloop shape
	 * 
	 * @param orientation, true = standard, false = reverse
	 */
	public MultiShape(boolean orientation, FreeMagnets fm){
		xloc = 0;
		yloc = 0;
		width = 40;
		circlewidth = 120;
		circleheight = 120;
		rheight = 15;
		color = Color.blue;
		theta = 0;
		offsetangle = new int[8];
		magnets = new Magnet[8];
		offsetangle[0] = 1;
		offsetangle[1] = 0;
		offsetangle[2] = 0;
		offsetangle[3] = 0;
		offsetangle[4] = 1;
		offsetangle[5] = 0;
		offsetangle[6] = 1;
		offsetangle[7] = 0;
		multiloop = new MultiLoop(orientation,offsetangle);
		bb = this.multiloop;
		createArea();
		createMagnets();
		rnapath = new GeneralPath();
		currentrotation = 0;
		at = new AffineTransform();
		exits = new RnaShape[8];
		for(int i=0;i<8;i++){
			exits[i] = null;
		}
		standardorientation = orientation;
		selected = false;
		this.fm = fm;
		isstartelement = false;
		isendelement = false;
		startangle = -1;
		endangle = -1;
		rotations = new Vector<Rotation>();
		switchforced = false;
	}
	
	
	/**
	 * This method is needed for initializing a stored multiloop
	 */
	private void initializeMulti(){
		color = Color.blue;
		createArea();
		adjustPath();
		selected = false;
		this.bb = multiloop;
		setMotifHead();
	}
	
	/**
	 * Getter method for the type of this building block
	 * 
	 * @return EditorGui.MULTI_TYPE
	 */
	public int getType(){
		return EditorGui.MULTI_TYPE;
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
		s.writeDouble(rheight);
		s.writeDouble(circlewidth);
		s.writeDouble(circleheight);
		s.writeObject(multiloop);
		s.writeInt(theta);
		s.writeObject(offsetangle);
		s.writeInt(currentrotation);
		s.writeObject(at);
		s.writeObject(exits);
		s.writeBoolean(standardorientation);
		s.writeObject(magnets);
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
		rheight = s.readDouble();
		circlewidth = s.readDouble();
		circleheight = s.readDouble();
		multiloop = (MultiLoop) s.readObject();
		theta = s.readInt();
		offsetangle = (int [])s.readObject();
		currentrotation = s.readInt();
		at = (AffineTransform) s.readObject();
		exits = (RnaShape[]) s.readObject();
		standardorientation = s.readBoolean();
		magnets = (Magnet[]) s.readObject();
		fm = (FreeMagnets) s.readObject();
		isstartelement = s.readBoolean();
		isendelement = s.readBoolean();
		startangle = s.readInt();
		endangle = s.readInt();
		rotations = (Vector<Rotation>) s.readObject();
		switchforced = s.readBoolean();
		startindex = s.readInt();
		initializeMulti();
	}


	/**
	 * This method gives the internal data structure MultiLoop of this Shape.
	 * 
	 * @return the MultiLoop of the shape
	 */
	public MultiLoop getMultiLoop(){
		return this.multiloop;
	}


	/**
	 * returns the x-distance between the origin (xloc, yloc) of this shape
	 * and the translated origin according to the index (rotate by i*45)
	 *
	 * @param index, the index to analyze
	 * @return the x-distance between the two points
	 */
	private double getXDist(int index){
		//differenz zwischen ursprung und zielpunkt berechnen
		Point2D origin = new Point2D.Double(xloc,yloc);
		Point2D translated = new Point2D.Double();
		AffineTransform athelp = new AffineTransform();
		athelp.rotate(StrictMath.toRadians(index*45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
		athelp.transform(origin,translated);
		return (translated.getX()-origin.getX());
	}


	/**
	 * returns the y-distance between the origin (xloc, yloc) of this shape
	 * and the translated origin according to the index (rotate by i*45)
	 *
	 * @param index, the index to analyze
	 * @return the y-distance between the two points
	 */
	private double getYDist(int index){
		//differenz zwischen ursprung und zielpunkt berechnen
		Point2D origin = new Point2D.Double(xloc,yloc);
		Point2D translated = new Point2D.Double();
		AffineTransform athelp = new AffineTransform();
		athelp.rotate(StrictMath.toRadians(index*45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
		athelp.transform(origin,translated);
		return (translated.getY()-origin.getY());
	}


	/**
	 * Getter method for the rotation angle of this element
	 *
	 * @return theta, the angle representing the orientation of this Shape
	 */
	public int getTheta(){
		return this.theta;
	}


	/**
	 * This method returns all exits of the shape as a Magnet Vector
	 * 
	 * @return Vector containing all Magnets of this shape
	 */
	public Vector<Magnet> getBorders(){
		Vector<Magnet> borders = new Vector<Magnet>();
		for(int i=0;i<8;i++){
			if(magnets[i]!=null){
				borders.add(magnets[i]);
			}
		}
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
		
		for(int i=0;i<8;i++){
			if(magnets[i]!=null && ((theta + (i*45))%360) != m.getAngle()){
				borders.add(magnets[i]);
			}
		}
		return borders;
	}

	/**
	 * This method returns the current exit structure of the MultiShape
	 * 
	 * @return the exit structure of this MultiShape
	 */
	public int[] getExitStructure(){
		return this.offsetangle;
	}


	/**
	 * returns the number of exits of this multiloop
	 *
	 * @return the number of exits
	 */
	public int getNumberOfExits(){
		int numofexits=0;
		for(int i=0;i<8;i++){
			numofexits += offsetangle[i];
		}
		return numofexits;
	}


	/**
	 * checks wether the given exit exist and wether it is occupied.
	 *
	 * @param index, the index of the exit to look for (0=0grad, 1=45 grad ...)
	 * @return int value that is 0, if exit does not exist, 2 if available and 1 if taken
	 */
	public int getExit(int index){
		index -= (theta/45);
		if(index<0){
			index = 8+index;
		}
		if(offsetangle[index]==1){
			if(exits[index] == null){
				return 2;
			}
			else{
				return 1;
			}
		}
		return 0;
	}


	/**
	 * looks for the nearest existing exit to the one at the given index
	 *
	 * @param index the starting exit for the search
	 * @return the index of the nearest neighbor
	 */
	private int getNearest(int index){
		int l=index-1,u=index+1;
		while(l>=0 || u<8){
			if(l>=0 && offsetangle[l]==1){
				return l;
			}
			else if(u<8 && offsetangle[u]==1){
				return u;
			}
			l--;
			u++;
		}
		return -1;
	}


	/**
	 * Method that creates this shape's area according to the exit structure
	 */
	private void createArea(){
		area = new Area(new Ellipse2D.Double(xloc-((circlewidth-width)/2),yloc+((rheight/3)*2),circlewidth, circleheight));
		Rectangle2D base = new Rectangle2D.Double(xloc,yloc,width,rheight);
		AffineTransform atexits = new AffineTransform();
		for(int i=0;i<8;i++){
			if(offsetangle[i]==1){
				Shape transarea = atexits.createTransformedShape(base);
				area.add(new Area(transarea));
			}
			atexits.rotate(StrictMath.toRadians(45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
		}
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
		multiloop.setOrientation(standardorientation);
		for(int i=0;i<8;i++){
			if(exits[i] != null && exits[i].getType() == EditorGui.SS_DOUBLE){
				exits[i].changeOrientation(seqreversal);
			}
		}
        this.multiloop.switchSides();
		if(seqreversal){
			this.multiloop.reverseStrings();
		}
	}
	
	
	/**
	 * This method changes the size of the MultiLoop, i.e. changes its Area.
	 * This method has an empty body as MultiLoops always have the same size.
	 *
	 * @param seglength, the sequence length upon which the size depends
	 */
	public void changeSize(int seqlength){
	}
	
	
	/**
	 * This method changes the size of the MultiLoop, i.e. changes its Area
	 * according to a median value for the sequence length.
	 * This method has an empty body as MultiLoops always have the same size.
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
		createArea();
		adjustPath();
		adjustMagnets();
	}

	/**
	 * This method implements a change in location to a new point of origin.
	 * Not needed for MultiLoop
	 *
	 * @param x, the x-coordinate of the new origin
	 * @param y, the y-coordinate of the new origin
	 * @param bottom
	 */
	public void changeLocation(double x, double y, boolean bottom){}


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
		createArea();
		adjustPath();
		if(!rotations.isEmpty()){
			at.setToIdentity();
			for(Rotation r : rotations){
				switch(r.getType()){
				case 1: at.rotate(StrictMath.toRadians(r.getAngle()),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2); break;
				case 3: at.rotate(StrictMath.toRadians(r.getAngle()),xloc-((circlewidth-width)/2)+(circlewidth/2),yloc+((rheight/3)*2)+(circleheight/2)); break;
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
		Point2D.Double mp1 = new Point2D.Double(xloc,yloc);
		Point2D.Double mp1trans = new Point2D.Double();
		Point2D.Double mp2 = new Point2D.Double(xloc+width, yloc);
		Point2D.Double mp2trans = new Point2D.Double();
		AffineTransform atexits = new AffineTransform();
		for(int i=0;i<8;i++){
			if(offsetangle[i]==1){
				atexits.transform(mp1,mp1trans);
				at.transform(mp1trans,mp1trans);
				atexits.transform(mp2,mp2trans);
				at.transform(mp2trans,mp2trans);
				if(magnets[i]!=null && fm.contains(magnets[i])){
					fm.remove(magnets[i]);
					magnets[i] = new Magnet(new Line2D.Double(mp1trans,mp2trans),this,(theta+((i+4)%8)*45)%360,magnets[i].getIsHairpinAccessible());
					fm.add(magnets[i]);
				}
				else if(exits[i] != null && exits[i].getType() == EditorGui.SS_DOUBLE){
					fm.adjustMagnet(magnets[i], new Magnet(new Line2D.Double(mp1trans,mp2trans),this,(theta+((i+4)%8)*45)%360,magnets[i].getIsHairpinAccessible()));
					magnets[i] = new Magnet(new Line2D.Double(mp1trans,mp2trans),this,(theta+((i+4)%8)*45)%360,magnets[i].getIsHairpinAccessible());
				}
				else if(magnets[i] == null){
					magnets[i] = new Magnet(new Line2D.Double(mp1trans,mp2trans),this,(theta+((i+4)%8)*45)%360,true);
					fm.add(magnets[i]);
				}
				else{
					magnets[i] = new Magnet(new Line2D.Double(mp1trans,mp2trans),this,(theta+((i+4)%8)*45)%360,magnets[i].getIsHairpinAccessible());
				}
			}
			else{
				if(magnets[i]!=null && fm.contains(magnets[i])){
					fm.remove(magnets[i]);
				}
				magnets[i] = null;
			}
			atexits.rotate(StrictMath.toRadians(45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
		}
	}

	/**
	 * Adjusts the hairpin accessibilty for this building block
	 * 
	 * @param accessible: if true, hairpin accessible; else not accessible
	 */
	public void adjustMagnetAccessability(boolean accessible){
		for(int i=0;i<8;i++){
			if(offsetangle[i]==1 && exits[i] == null){
				magnets[i].setIsHairpinAccessible(accessible);
			}
		}
	}
	
	/**
	 * Method for a traversal invoked after a hairpin is added. In a multiloop, the traversal is
	 * split and for each arm we must count the open ends. If more than one, all ends are
	 * hairpin accessible. Else, the one is not accessible.
	 * 
	 * @param angle describing the direction of the traversal
	 */
	public void hairpinAccessTraversal(int angle){
		Vector<Magnet> openmagnets = new Vector<Magnet>();
		int index = ((angle/45)+4)%8 - (theta/45);
		if(index < 0){
			index = 8 + index;
		}
		if(standardorientation){
			for(int j=0;j<7;j++){
				index--;
				if(index<0){
					index=7;
				}
				if(exits[index] == null && offsetangle[index] == 1){
					openmagnets.add(magnets[index]);
				}
				else if(offsetangle[index] == 1){
					exits[index].hairpinAccessTraversal((index*45+theta)%360,openmagnets);
				}
			}
		}
		else{
			for(int j=0;j<7;j++){
				index++;
				index = index%8;
				if(exits[index] == null && offsetangle[index] == 1){
					openmagnets.add(magnets[index]);
				}
				else if(offsetangle[index] == 1){
					exits[index].hairpinAccessTraversal((index*45+theta)%360,openmagnets);
				}
			}
		}
		if(openmagnets.size() > 1){
			for(Magnet m : openmagnets){
				m.setIsHairpinAccessible(true);
			}
		}
		else{
			for(Magnet m : openmagnets){
				m.setIsHairpinAccessible(false);
			}
		}
	}
		
	/**
	 * Method called from the MultiShape during a hairpin access traversal: for each arm
	 * the open ends are kept in the openmagnets Vector.
	 * 
	 * @param openmagnets it a Vector collecting all available magnets of this motif part
	 */
	public void hairpinAccessTraversal(int angle, Vector<Magnet> openmagnets){
		int index = ((angle/45)+4)%8 - (theta/45);
		if(index < 0){
			index = 8 + index;
		}
		if(standardorientation){
			for(int j=0;j<7;j++){
				index--;
				if(index<0){
					index=7;
				}
				if(exits[index] == null && offsetangle[index] == 1){
					openmagnets.add(magnets[index]);
				}
				else if(offsetangle[index] == 1){
					exits[index].hairpinAccessTraversal((index*45+theta)%360,openmagnets);
				}
			}
		}
		else{
			for(int j=0;j<7;j++){
				index++;
				index = index%8;
				if(exits[index] == null && offsetangle[index] == 1){
					openmagnets.add(magnets[index]);
				}
				if(offsetangle[index] == 1){
					exits[index].hairpinAccessTraversal((index*45+theta)%360,openmagnets);
				}
			}
		}
	}
	
	/**
	 * Method computing whether an exit is availble that can be removed.
	 * 
	 * @return true, if one exit is hairpin accessible (more than one left); else false
	 */
	public boolean exitRemovalPossible(){
		for(int i = 0;i<8;i++){
			if(offsetangle[i] == 1 && exits[i] == null){
				return magnets[i].getIsHairpinAccessible();
			}
		}
		return false;
	}
	
	/**
	 * This method adjust the GeneralPath containing the squiggle plot
	 * according to changes in size, location or other settings
	 */
	private void adjustPath(){
		rnapath = new GeneralPath();
		Arc2D.Float arcspan;
		GeneralPath exitshape = new GeneralPath();
		//GeneralPath exitshaperight = new GeneralPath();
		AffineTransform atexits = new AffineTransform();
		float x = (float) xloc;
		float y = (float) yloc;
		float w = (float) width;
		float rh = (float) rheight;
		float ch = (float) circleheight;
		float cw = (float) circlewidth;
		
		int j = 1;
		int k = 0;
		int i = 1;
		int currentstart = 0;
		
		
		exitshape.moveTo(x + w - (w/4), y + rh + 5);
		exitshape.lineTo(x + w - (w/4), y);
		exitshape.moveTo(x + (w/4), y + rh/2+5);
		exitshape.lineTo(x + w - (w/4), y + rh/2+5);
		exitshape.moveTo(x + (w/4), y);
		exitshape.lineTo(x + (w/4), y + rh + 5);
		
		for(; i <= 8; i++){
			k++;
			if(offsetangle[j] == 1){
				if(offsetangle[2] == 0){
					arcspan = new Arc2D.Float(x-((cw-w)/2)+10,y+((rh/3)*2)+10,cw-20, ch-20, currentstart, (k*45)-18, Arc2D.OPEN);
				}
				else{
					currentstart = 18;
					arcspan = new Arc2D.Float(x-((cw-w)/2)+10,y+((rh/3)*2)+10,cw-20, ch-20, currentstart, (k*45)-2*18, Arc2D.OPEN);
					GeneralPath sp = new GeneralPath();
					sp.moveTo(x + (w/4), y + rh + 5);
					atexits.setToIdentity();
					atexits.rotate(StrictMath.toRadians(90),x+w/2,y+((rh/3)*2)+ch/2);
					Shape transarea = atexits.createTransformedShape(sp);
					rnapath.append(transarea,false);
				}
				rnapath.append(arcspan,true);
				currentstart = (i*45)+18;
				k=0;
				
				atexits.setToIdentity();
				atexits.rotate(StrictMath.toRadians(j*45),x+w/2,y+((rh/3)*2)+ch/2);
				Shape transarea = atexits.createTransformedShape(exitshape);
				rnapath.append(transarea,true);
				
				break;
			}
			j--;
			if(j<0){
				j=7;
			}
		}
		
		i++;
		j--;
		if(j<0){
			j=7;
		}
		
		for(; i <= 8; i++){
			k++;
			if(offsetangle[j] == 1){
				arcspan = new Arc2D.Float(x-((cw-w)/2)+10,y+((rh/3)*2)+10,cw-20, ch-20, currentstart, (k*45)-2*18, Arc2D.OPEN);
				rnapath.append(arcspan,true);
				currentstart = (i*45)+18;
				k=0;
				
				atexits.setToIdentity();
				atexits.rotate(StrictMath.toRadians(j*45),x+w/2,y+((rh/3)*2)+ch/2);
				Shape transarea = atexits.createTransformedShape(exitshape);
				rnapath.append(transarea,true);
				
			}
			j--;
			if(j<0){
				j=7;
			}
		}
		if(offsetangle[2] == 0){
			arcspan = new Arc2D.Float(x-((cw-w)/2)+10,y+((rh/3)*2)+10,cw-20, ch-20, currentstart, (k*45)-18, Arc2D.OPEN);
			rnapath.append(arcspan,true);
		}
	}


	/** 
	 * adjust the number of exits, the area and the corresponding magnets
	 * according to the given exitstructure.
	 *
	 * @param exitstructure the array contains the ints 1 and 0, 
	 * where 1 stands for an exit and 0 for no exit
	 * @return array containing the new start and the new end element: null if multishape remains start/end; else other RnaShape
	 */
	public RnaShape[] adjustAll(int[] exitstructure){
		int i = 0;
		RnaShape[] startend = new RnaShape[2];
		//theta must be taken into account here, because the visual interface for 
		//defining the exits does not include information on rotations,
		//yet the actual structure might be rotated!
		int j = (theta/45); //startindex  of the given array depending on orientation
		int s1 = 0,s2 = 0;
		boolean oldaccess = true;
		for(int t = 0;t<8;t++){
			if(offsetangle[t] == 1){
				s1++;   //s1 adds up all current exits
				oldaccess = magnets[t].getIsHairpinAccessible(); //is any exit hairpin accessible (either all are or none is)
			}
			if(exitstructure[t] == 1) s2++; //s2 adds up all new exits
		}
		for(;i<8;i++){
			j = j%8;
			offsetangle[i] = exitstructure[j++]; //change the current exits to the new ones
		}
		multiloop.changeExits(offsetangle);
		createArea();
		adjustPath();
		adjustMagnets();
		if(isstartelement){
			startend[0] = adjustStartAngle();
		}
		if(isendelement){
			startend[1] = adjustEndAngle();
		}
		multiloop.adjustBasepairs(exitstructure);
		if(s2 > s1){
			resetHPAccessability();
		}
		else if(s2 == s1){
			adjustMagnetAccessability(oldaccess);
		}
		else{
			hairpinAccessTraversal(theta);
		}
		return startend;
	}


	/**
	 * this method creates this shape's magnets according to the exit structure
	 */
	private void createMagnets(){
		Point2D.Double mp1 = new Point2D.Double(xloc,yloc);
		Point2D.Double mp1trans = new Point2D.Double();
		Point2D.Double mp2 = new Point2D.Double(xloc+width, yloc);
		Point2D.Double mp2trans = new Point2D.Double();
		AffineTransform atexits = new AffineTransform();
		for(int i=0;i<8;i++){
			if(offsetangle[i]==1){
				atexits.transform(mp1,mp1trans);
				atexits.transform(mp2,mp2trans);
				magnets[i] = new Magnet(new Line2D.Double(mp1trans,mp2trans),this,((i+4)%8)*45,true);
			}
			else{
				magnets[i] = null;
			}
			atexits.rotate(StrictMath.toRadians(45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
		}
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
		
		for(int i=0;i<8;i++){
			if(m.getAngle() == ((theta+(i*45))%360)){
				switch(i){
				case 0: changeLocation(x2,y2); break;
				case 1: changeLocation(x2-getXDist(i),y2-getYDist(i)); break;
				case 2: changeLocation(x1-(circlewidth-width)/2-(rheight/3)*2-width, y1-(rheight/3)*2-(circleheight-width)/2-width); break;
				case 3: changeLocation(x2-getXDist(i),y2-getYDist(i)); break;
				case 4: changeLocation(x1,y1-(circleheight+(rheight/3)*4)); break;
				case 5: changeLocation(x2-getXDist(i),y2-getYDist(i)); break;
				case 6: changeLocation(x1+(circlewidth-width)/2+(rheight/3)*2, y1-(rheight/3)*2-(circleheight-width)/2); break;
				case 7: changeLocation(x2-getXDist(i),y2-getYDist(i)); break;
				}
				if(offsetangle[i]!=1 && currentrotation == 0){
					int nearest = getNearest(i);
					at.rotate(StrictMath.toRadians((i-nearest)*45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
					rotations.add(new Rotation((i-nearest)*45,1));
					theta = (theta+(i-nearest)*45)%360;
					if(theta < 0){
					    theta += 360;
					}
					adjustMagnets();
				}
				break;
			}
		}	
	}	    
	
	
	/**
	 * restores the affine transformation to identity, 
	 * i.e. all rotations are undone
	 */	
	public void restore(){
			at.setToIdentity();
			rotations.clear();
			at.rotate(StrictMath.toRadians(currentrotation),xloc-((circlewidth-width)/2)+(circlewidth/2),yloc+((rheight/3)*2)+(circleheight/2));
			rotations.add(new Rotation(currentrotation,3));
			theta = currentrotation;
			adjustMagnets();
	}
	
	/**
	 * This method rotates our Shape around the center of the shape
	 *
	 * @param int degree, the angle of rotation
	 */
	public void rotateBy(int degree){
		at.rotate(StrictMath.toRadians(degree),xloc-((circlewidth-width)/2)+(circlewidth/2),yloc+((rheight/3)*2)+(circleheight/2));
		rotations.add(new Rotation(degree,3));
		theta += degree;
		theta = theta % 360;
		currentrotation += degree;
		currentrotation = currentrotation % 360;
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
		at.concatenate(buf);
		rotations.add(0,new Rotation(degree,xmiddle,ymiddle));
		theta += degree;
		theta = theta %360;
		startangle += degree;
		startangle = startangle %360;
		endangle += degree;
		endangle = endangle % 360;
		adjustMagnets();
	}


	/**
	 * Setter method for the flag isstartelement
	 *
	 * @param boolean isstartelement
	 * @param int angle
	 */
	public void setIsStartElement(boolean isstartelement, int angle, int direct){
		int i = ((angle+180-theta)%360)/45-4;
		if(i<0){
			i=8+i;
		}
		this.isstartelement = isstartelement;
		if(direct == 1){
			startangle = angle;
			if(startangle != -1){
				this.multiloop.setStartIndex(((startangle/45)+4)%8 - (theta/45));
			}
			if(isstartelement){
				setMotifHead();
			}
			return;
		}
		else{
			if(standardorientation){
				for(int j=0;j<7;j++){
					i--;
					if(i<0){
						i=7;
					}
					if(this.offsetangle[i] == 1 && exits[i]==null){
						startangle = (((i+4)%8)*45+theta) % 360;
						this.multiloop.setStartIndex(((startangle/45)+4)%8 - (theta/45));
						if(isstartelement){
							setMotifHead();
						}
						return;
					}
				}
			}
			else{
				for(int j=0;j<7;j++){
					i++;
					i=i%8;
					if(this.offsetangle[i] == 1 && exits[i]==null){
						startangle = (((i+4)%8)*45+theta)%360;
						this.multiloop.setStartIndex(((startangle/45)+4)%8 - (theta/45));
						if(isstartelement){
							setMotifHead();
						}
						return;
					}
				}
			}
		}
	}


	/**
	 * This method adjust the start angle according to a change in the
	 * exit structure (the current main open end was removed etc.)
	 * 
	 * @return RnaShape which is the new start element
	 */
	private RnaShape adjustStartAngle(){
		boolean foundnewstart = false;
		RnaShape newstart = null;
		int i = ((startangle/45)+4)%8 - (theta/45);
		if(i<0){
			i = 8+i;
		}
		if(offsetangle[i] == 0){
			if(standardorientation){
				for(int j=0;j<7;j++){
					i--;
					if(i<0){
						i=7;
					}
					if(offsetangle[i] == 1 && exits[i] == null){
						startangle = (((i+4)%8)*45+theta)%360;
						foundnewstart = true;
						break;
					}
				}
			}
			else{
				for(int j=0;j<7;j++){
					i++;
					i=i%8;
					if(this.offsetangle[i] == 1 && exits[i]== null){
						startangle = (((i+4)%8)*45+theta)%360;
						foundnewstart = true;
						break;
					}
				}
			}
			if(foundnewstart){
				this.multiloop.setStartIndex(((startangle/45)+4)%8 - (theta/45));
			}
			else{
				int angle = startangle;
				setIsStartElement(false,-1,1);
				newstart = traverseToNewStart(angle,true);
			}
		}
		return newstart;
	}
		
	
	/**
	 * This method returns the Magnet of the start exit of this shape
	 *
	 * @return Magnet, the start exit of this shape
	 */
	public Magnet getStartExit(){
		int i = (startangle/45 + 4)%8 - (theta/45);
		if(i<0){
			i+=8;
		}
		return magnets[i];
	}


	/**
	 * This method finds the new start element starting from the next exit
	 * specified by the given index
	 *
	 * @param int index, the index from where the start
	 * @return RnaShape, the new start element
	 */
	public RnaShape findNewStartElement(int index){
		if(exits[index] != null){
			exits[index].setIsStartElement(true,(theta+index*45)%360,1);
			return exits[index];
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
		RnaShape newstart = null;
		int index = ((angle/45)+4)%8 - (theta/45);
		if(index < 0){
			index = 8 + index;
		}
		if(real){
			this.multiloop.switchSides(index);
		}
		if(standardorientation){
			for(int j=0;j<7;j++){
				index--;
				if(index<0){
					index=7;
				}
				if(exits[index] == null && offsetangle[index] == 1){
					setIsStartElement(true, (((index+4)%8)*45+theta)%360,1);
					if(real){
						this.multiloop.switchSides(index);
					}
					return this;
				}
				else if(offsetangle[index] == 1){
					if(exits[index].getType() == EditorGui.SS_DOUBLE){
						SingleShape next = ((DoubleSingleShape)exits[index]).getFivePrime();
						if(next == null){
							setIsStartElement(true,(((index+4)%8)*45+theta)%360,1);
							if(real){
								this.multiloop.switchSides(index);
							}
							return this;
						}
						this.multiloop.setStartIndex(index);
						return next.traverseToNewStart(angle,real);
					}
					else{
						newstart = exits[index].traverseToNewStart((index*45+theta)%360,false);
						if(newstart != null){
							if(real){
								exits[index].traverseToNewStart((index*45+theta)%360,true);
								this.multiloop.switchSides(index);
							}
							this.multiloop.setStartIndex(index);
							return newstart;
						}
					}
				}
			}
		}
		else{
			for(int j=0;j<7;j++){
				index++;
				index = index%8;
				if(exits[index] == null && offsetangle[index] == 1){
					setIsStartElement(true, (((index+4)%8)*45+theta)%360,1);
					if(real){
						this.multiloop.switchSides(index);
					}
					return this;
				}
				if(offsetangle[index] == 1){
					if(exits[index].getType() == EditorGui.SS_DOUBLE){
						SingleShape next = ((DoubleSingleShape)exits[index]).getFivePrime();
						if(next == null){
							setIsStartElement(true,(((index+4)%8)*45+theta)%360,1);
							if(real){
								this.multiloop.switchSides(index);
							}
							return this;
						}
						this.multiloop.setStartIndex(index);
						return next.traverseToNewStart(angle,real);
					}
					else{
						newstart = exits[index].traverseToNewStart((index*45+theta)%360,false);
						if(newstart != null){
							if(real){
								exits[index].traverseToNewStart((index*45+theta)%360,true);
								this.multiloop.switchSides(index);
							}
							this.multiloop.setStartIndex(index);
							return newstart;
						}
					}
				}
			}
		}
		return null;
	}


	/**
	 * Setter method for the flag isendelement
	 *
	 * @param boolean isendelement
	 * @param int angle
	 */
	public void setIsEndElement(boolean isendelement, int angle, int direct){
		
		int i = ((angle+180-theta)%360)/45-4;
		if(i<0){
			i=8+i;
		}
		this.isendelement = isendelement;
		if(direct == 1){
			endangle = angle;
			return;
		}
		else{
			if(standardorientation){
				for(int j=0;j<7;j++){
					i--;
					if(i<0){
						i=7;
					}
					if(this.offsetangle[i] == 1 && exits[i]==null){
						endangle = (((i+4)%8)*45 + theta)%360;
						return;
					}
				}
			}
			else{
				for(int j=0;j<7;j++){
					i++;
					i=i%8;
					if(this.offsetangle[i] == 1 && exits[i]==null){
						endangle = (((i+4)%8)*45 + theta)%360;
						return;
					}
				}
			}
		}
	}


	/**
	 * This method adjust the end angle according to a change in the
	 * exit structure (the current main open end was removed etc.)
	 * 
	 * @return RnaShape which is the new end
	 */
	private RnaShape adjustEndAngle(){
		boolean foundnewend = false;
		RnaShape newend = null;
		int i = ((endangle/45)+4)%8 - (theta/45);
		if(i<0){
			i = 8+i;
		}
		if(offsetangle[i] == 0){
			if(standardorientation){
				for(int j=0;j<7;j++){
					i--;
					if(i<0){
						i=7;
					}
					if(offsetangle[i] == 1 && exits[i] == null){
						endangle = (((i+4)%8)*45 + theta)%360;
						foundnewend = true;
						break;
					}
				}
			}
			else{
				for(int j=0;j<7;j++){
					i++;
					i=i%8;
					if(this.offsetangle[i] == 1 && exits[i]==null){
						endangle = (((i+4)%8)*45 + theta)%360;
						foundnewend = true;
						break;
					}
				}
			}
			if(!foundnewend){
				int angle = endangle;
				setIsEndElement(false,-1,1);
				newend = traverseToNewEnd(angle);
			}
		}
		return newend;
	}
	
	/**
	 * This method returns the Magnet of the end exit of this shape
	 *
	 * @return Magnet, the end exit of this shape
	 */
	public Magnet getEndExit(){
		int i = (endangle/45 + 4)%8 - (theta/45);
		if(i<0){
			i+=8;
		}
		return magnets[i];
	}


	/**
	 * This method finds the new end element ending from the next exit
	 * specified by the given index
	 *
	 * @param int index, the index from where the end
	 * @return RnaShape, the new end element
	 */
	public RnaShape findNewEndElement(int index){
		if(exits[index] != null){
			exits[index].setIsEndElement(true,(theta+index*45)%360,1);
			return exits[index];
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
		RnaShape newend = null;
		int index = ((angle/45)+4)%8 - (theta/45);
		if(index < 0){
			index = 8 + index;
		}
		
		if(standardorientation){
			for(int j=0;j<7;j++){
				index--;
				if(index<0){
					index=7;
				}
				if(exits[index] == null && offsetangle[index] == 1){
					setIsEndElement(true, (((index+4)%8)*45+theta)%360,1);
					return this;
				}
				else if(offsetangle[index] == 1){
					if(exits[index].getType() == EditorGui.SS_DOUBLE){
						SingleShape next = ((DoubleSingleShape)exits[index]).getThreePrime();
						if(next == null){
							setIsEndElement(true,(((index+4)%8)*45+theta)%360,1);
							return this;
						}
						return next.traverseToNewEnd(angle);
					}
					else{
						newend = exits[index].traverseToNewEnd((index*45+theta)%360);
						if(newend != null){
							return newend;
						}
					}
				}
			}
		}
		else{
			for(int j=0;j<7;j++){
				index++;
				index = index%8;
				if(exits[index] == null && offsetangle[index] == 1){
					setIsEndElement(true, (((index+4)%8)*45+theta)%360,1);
					return this;
				}
				if(offsetangle[index] == 1){
					if(exits[index].getType() == EditorGui.SS_DOUBLE){
						SingleShape next = ((DoubleSingleShape)exits[index]).getThreePrime();
						if(next == null){
							setIsEndElement(true,(((index+4)%8)*45+theta)%360,1);
							return this;
						}
						return next.traverseToNewEnd(angle);
					}
					else{
						newend = exits[index].traverseToNewEnd((index*45+theta)%360);
						if(newend != null){
							return newend;
						}
					}
				}
			}
		}
		return null;
	}


	/**
	 * @return 1 , if it has an open exit, else 0
	 */
	public int getEndType(){
		int j=0;
		int k=0;
		for(int i=0;i<8;i++){
			if(offsetangle[i]==1 && exits[i]!=null && exits[i].getType() != EditorGui.SS_DOUBLE){
				j++;
				k=i;
			}
		}
		if(j > 1){
			return -1;
		}
		return k;
	}
	
	/**
	 * Method for traversing to a compound block.
	 * 
	 * @return false, because ambiguity issues only occur within one helix
	 */
	public boolean traverseToClosed(int angle){
		return false;
	}
	
	/** 
	 * Method checking for an open end connected to this building block
	 * 
	 * @return true, if an open exit is available or recursively checks neighbors; false if none available
	 */
	public boolean findOpenEnd(int angle){
		int index = ((angle/45)+4)%8 - (theta/45);
		if(index < 0){
			index = 8 + index;
		}
		for(int j=0;j<7;j++){
			index--;
			if(index<0){
				index=7;
			}
			if(offsetangle[index]==1 && exits[index] == null){
				return true;
			}
		}
		for(int j=0;j<7;j++){
			index--;
			if(index<0){
				index=7;
			}
			if(offsetangle[index]==1){
				if(exits[index].findOpenEnd((index*45+theta)%360)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * adds a neighboring shape to this one
	 * 
	 * @param shape, the shape that is added as a neighbor
	 * @param angle, the angle of the exit, where the shape is neighboring
	 */
	public void addToNeighbors(RnaShape shape,int angle, int type){
		for(int i=0;i<8;i++){
			if(offsetangle[i]==1 && angle == ((theta+(i*45))%360)){
				exits[i] = shape;
			}
		}
	}
	
	/**
	 * adds a single strand shape to this one
	 * 
	 * @param shape, the shape that is added as a neighbor
	 * @param angle, the angle of the exit, where the shape is neighboring
	 */
	public int addSSNeighbor(SingleShape shape,int angle){
		for(int i=0;i<8;i++){
			if(offsetangle[i]==1 && angle == ((theta+(i*45))%360)){
				if(this.isstartelement || this.isendelement){
					if(exits[i] == null){
						DoubleSingleShape dss = new DoubleSingleShape();
						Point2D mp1 = new Point2D.Double(xloc,yloc);
						Point2D mp1trans = new Point2D.Double();
						AffineTransform atexits = new AffineTransform();
						atexits.rotate(StrictMath.toRadians(i*45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
						atexits.concatenate(at);
						atexits.transform(mp1,mp1trans);
						double realx = mp1trans.getX();
						double realy = mp1trans.getY();
						if(standardorientation){
							if(shape.contains(realx,realy)){
								dss.addThreePrime(shape);
								exits[i] = dss;
								shape.addFivePrimeDSS(dss);
								return 2;
							}
							else{
								dss.addFivePrime(shape);
								exits[i] = dss;
								shape.addThreePrimeDSS(dss);
								return 1;
							}
						}
						else{
							if(shape.contains(realx,realy)){
								dss.addFivePrime(shape);
								exits[i] = dss;
								shape.addThreePrimeDSS(dss);
								return 1;
							}
							else{
								dss.addThreePrime(shape);
								exits[i] = dss;
								shape.addFivePrimeDSS(dss);
								return 2;
							}
						}
					}
					else{
						if(((DoubleSingleShape)exits[i]).getOpenFivePrime()){
							((DoubleSingleShape)exits[i]).addFivePrime(shape);
							shape.addThreePrimeDSS((DoubleSingleShape)exits[i]);
							return 1;
						}
						else{
							((DoubleSingleShape)exits[i]).addThreePrime(shape);
							shape.addFivePrimeDSS((DoubleSingleShape)exits[i]);
							return 2;
						}
					}
				}
				else if(shape.getIsConnected()){
					//shape ist 3' ende (haengt an 3' position)
					if(shape.getIsFivePrimeDSSAvailable()){
						if(exits[i] == null){
							DoubleSingleShape dss = new DoubleSingleShape();
							dss.addFivePrime(shape);
							exits[i] = dss;
							shape.addThreePrimeDSS(dss);
						}
						else{
							((DoubleSingleShape)exits[i]).addFivePrime(shape);
							shape.addThreePrimeDSS((DoubleSingleShape)exits[i]);
						}
						Point2D mp1 = new Point2D.Double(xloc,yloc);
						Point2D mp1trans = new Point2D.Double();
						AffineTransform atexits = new AffineTransform();
						atexits.rotate(StrictMath.toRadians(i*45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
						atexits.concatenate(at);
						atexits.transform(mp1,mp1trans);
						double realx = mp1trans.getX();
						double realy = mp1trans.getY();
						if((standardorientation && shape.contains(realx,realy)) || (!standardorientation && !shape.contains(realx,realy))){
							//3' dockt an 3'!!!
							//changeOrientation fuer alle in diesem
							switchSidesTraversal(this, true, ((theta+(i*45))%360));
							switchforced = true;
						}
						return 1;
					}
					//shape ist 5' ende (haengt an 5' position)
					else{
						if(exits[i] == null){
							DoubleSingleShape dss = new DoubleSingleShape();
							dss.addThreePrime(shape);
							exits[i] = dss;
							shape.addFivePrimeDSS(dss);
						}
						else{
							((DoubleSingleShape)exits[i]).addThreePrime(shape);
							shape.addFivePrimeDSS((DoubleSingleShape)exits[i]);
						}
						Point2D mp1 = new Point2D.Double(xloc,yloc);
						Point2D mp1trans = new Point2D.Double();
						AffineTransform atexits = new AffineTransform();
						atexits.rotate(StrictMath.toRadians(i*45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
						atexits.concatenate(at);
						atexits.transform(mp1,mp1trans);
						double realx = mp1trans.getX();
						double realy = mp1trans.getY();
						if((standardorientation && !shape.contains(realx,realy)) || (!standardorientation && shape.contains(realx,realy))){
							//5' dockt an 5'!!!
							//changeOrientation fuer alle in diesem
							switchSidesTraversal(this, false, ((theta+(i*45))%360));
							switchforced = true;
						}
						return 2;
					}
				}
				else{
					System.out.println("AddSSNeighbor in Multi: Should not be needed");
				}
			}
		}
		return 0;
	}

	/**
	 * This method removes all neighbors of this shape
	 */
	public void removeNeighbors(){
		for(int i=0;i<8;i++){
			if(exits[i] != null && offsetangle[i] == 1){
				exits[i].remove(this);
				//TODO:pruefen, ob hp accessible
				if(exits[i].findOpenEnd(((theta+(i*45))%360))){
					exits[i].adjustMagnetAccessability(true);
				}
				else{
					exits[i].adjustMagnetAccessability(false);
				}
				exits[i] = null;
			}
		}
	}
	

	/**
	 * Resets hairpin accessibility: all available magnets of this multiloop are accessible.
	 * Independent of the remaining motif!
	 */
	public void resetHPAccessability(){
		for(int i=0;i<8;i++){
			if(offsetangle[i] == 1){
				magnets[i].setIsHairpinAccessible(true);
			}
		}
	}
	
	/**
	 * This method removes a specific RnaShape from the neighbors of this
	 * shape
	 *
	 * @param RnaShape s, the shape to be removed
	 */
	public void remove(RnaShape s){
		for(int i=0;i<8;i++){
			if(s.getType() != EditorGui.SINGLE_TYPE){
				if(exits[i] == s && offsetangle[i] == 1){
					fm.add(magnets[i]);
					exits[i] = null;
					break;
				}
			}
			else{
				if(exits[i] != null && exits[i].getType() == EditorGui.SS_DOUBLE && ((DoubleSingleShape)exits[i]).contains(s)){
					((DoubleSingleShape)exits[i]).remove(s);
					if(((DoubleSingleShape)exits[i]).isEmpty()){
						exits[i] = null;
						fm.remove(this);
						fm.add(magnets[i]);
						if(switchforced){
							switchSidesTraversal(null,false,(((i+4)%8)*45+theta)%360);
							switchforced = false;
						}
					}
					else{
						fm.add(magnets[i].getRest(s.getBorders()));
					}
					break;
				}
			}
		}	
	}
	
	/**
	 * This method checks wether the Bulge is connected to the given shape at the given angle
	 *
	 * @param shape, the shape
	 * @param angle, the location
	 */
	public boolean connectedTo(RnaShape shape, int angle, RnaShape origin){
		boolean found = false;
		int index = ((angle/45)+4)%8 - (theta/45);
		if(index < 0){
			index = 8 + index;
		}
		for(int i=0;i<8;i++){
			index++;
			index = index%8;
			if(this.exits[index] != null){
				if(exits[index] == shape){
					return true;
				}
				else{
					found = exits[index].connectedTo(shape, (index*45+theta)%360, this);
					if(found == true){
						break;
					}
				}
			}
		}
		return found;
	}

	/**
	 * This method checks recursively for a single strand within the structure
	 * 
	 * @param angle, the direction of the search
	 * @return true, if the structure contains a single strand; else false.
	 */
	public boolean findSS(int angle){
		int i = ((angle/45)+4)%8 - (theta/45);
		
		if(i<0){
			i = 8+i;
		}
		
		for(int j=0;j<7;j++){
			i++;
			i = i%8;
			if(exits[i] != null){
				if(exits[i].getType() == EditorGui.SS_DOUBLE){
					return true;
				}
				else if(exits[i].findSS((theta+i*45)%360)){
					return true;
				}
			}
		}
		return false;
	}

	public boolean startFindSS(){
		return findAllSS();
	}
	
	
	/**
	 * Method for finding any ss connected to the multishape.
	 * 
	 * @return true, if a single strand is connected somewhere to this building block; else false
	 */
	public boolean findAllSS(){
		for(int i=0;i<=7;i++){
			if(exits[i] != null){
				if(exits[i].getType() == EditorGui.SS_DOUBLE){
					return true;
				}
				else if(exits[i].findSS((theta+i*45)%360)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * This method switches the sides of any stored information.
	 * Not used for MultiLoop (empty body).
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
		int index = (angle/45) - (theta/45);
		if(index < 0){
			index += 8;
		}
		if(angle == -1){
			for(int i = 0;i<8;i++){
				if(exits[i] != null && exits[i].getType() == EditorGui.SS_DOUBLE){
					exits[i].switchSidesTraversal(null, fiveprimedir, -1);
				}
			}
		}
		else{
			this.multiloop.switchSides(); 
			this.multiloop.reverseStrings();
			if((standardorientation && !fiveprimedir) || (!standardorientation) && fiveprimedir){
				for(int i=0;i<7;i++){
					index++;
					index = index%8;
					if(exits[index] != null){
						exits[index].switchSidesTraversal(start, fiveprimedir, (theta+(index*45))%360);
					}
				}
			}
			else{
				for(int i=0;i<7;i++){
					index--;
					if(index < 0){
						index = 7;
					}
					if(exits[index] != null){
						exits[index].switchSidesTraversal(start, fiveprimedir, (theta+(index*45))%360);
					}
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
		int index = ((this.startangle/45)+4)%8 - (theta/45);
		if(index<0){
			index += 8;
		}
		int i = index;
		String shapestring = Translator.getLeftContent(this.multiloop, type,i);
		
		if(standardorientation){
			for(int j=0;j<7;j++){
				i++;
				i = i%8;
				if(this.exits[i] != null){
					shapestring += Translator.getMLExitOpen(this.multiloop,type,i);
					shapestring += exits[i].traverse((theta+i*45)%360,type);
					shapestring += Translator.getMLExitClose(this.multiloop,type,i);
				}
			}
		}
		else{
			for(int j=0;j<7;j++){
				i--;
				if(i<0){
					i=7;
				}
				if(this.exits[i] != null){
					shapestring += Translator.getMLExitOpen(this.multiloop,type,i);
					shapestring += exits[i].traverse((theta+i*45)%360,type);
					shapestring += Translator.getMLExitClose(this.multiloop,type,i);
				}
			}
		}
		shapestring += Translator.getRightContent(this.multiloop,type,index);
		if(!this.getIsEndElement() && exits[index] != null && exits[index].getType() == EditorGui.SS_DOUBLE){
			SingleShape next = ((DoubleSingleShape)exits[index]).getThreePrime();
			shapestring += next.traverse(startangle, type);
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
		int index = ((angle/45)+4)%8 - (theta/45);
		
		if(index<0){
			index = 8+index;
		}
		int i = index;
		String shapestring = Translator.getLeftContent(this.multiloop, type, i);
		if(standardorientation){
			for(int j=0;j<7;j++){
				i++;
				i = i%8;
				if(this.offsetangle[i] == 1){
					shapestring += Translator.getMLExitOpen(this.multiloop,type,i);
					if(exits[i] == null){
						//should not be called!
						shapestring += " R ";
					}
					else{
						shapestring += exits[i].traverse((theta+i*45)%360,type);
					}
					shapestring += Translator.getMLExitClose(this.multiloop,type,i);
				}
			}
		}
		else{
			for(int j=0;j<7;j++){
				i--;
				if(i<0){
					i=7;
				}
				if(this.offsetangle[i] == 1){
					shapestring += Translator.getMLExitOpen(this.multiloop,type,i);
					if(exits[i] == null){
						//should not be called!
						shapestring += " R ";
					}
					else{
						shapestring += exits[i].traverse((theta+i*45)%360,type);
					}
					shapestring += Translator.getMLExitClose(this.multiloop,type,i);
				}
			}
		}
		shapestring += Translator.getRightContent(this.multiloop, type,index);
		return shapestring;
	}


	/**
	 * continues a change that was invoked by another RnaShape
	 * 
	 * @param angle the direction of the shift
	 */
	public void traverseShift(int angle){
		int i = ((angle/45)+4)%8 - (theta/45);
		
		if(i<0){
			i = 8+i;
		}
		
		for(int j=0;j<7;j++){
			i++;
			i = i%8;
			if(this.exits[i] != null){
				exits[i].restore();
				exits[i].snapTo(magnets[i]);
				exits[i].traverseShift((theta+i*45)%360);
			}
		}
	}
	
	/**
	 * Method for traversing a shift to all neighbors of this building block
	 * 
	 * @param movetransform describes the extent of the shift
	 * @param angle describes the direction of the traversal
	 */
	public void traverseShift(AffineTransform movetransform, int angle){
		int i = ((angle/45)+4)%8 - (theta/45);
		
		if(i<0){
			i = 8 + i;
		}
		
		for(int j=0;j<7;j++){
			i++;
			i = i%8;
			if(this.exits[i] != null){
				exits[i].changeLocation(movetransform);
				exits[i].traverseShift(movetransform,(theta+i*45)%360);
			}
		}
	}
	
	/**
	 * The starting method for traversing an rna structure. 
	 * The return String is initialized here and subsequent methods are called.
	 *
	 * @return String containing the shape notation for the whole structure
	 */
	public String currentForwardTraversal(){
		//a multilloop is represented with (.R.R.)
		int index = ((this.startangle/45)+4)%8 - (theta/45);
		if(index < 0){
			index += 8;
		}
		int i = index;
		String shapestring = Translator.getLeftContent(this.multiloop, Translator.SHAPE_LANG,i);
		
		if(standardorientation){
			for(int j=0;j<7;j++){
				i++;
				i = i%8;
				if(this.offsetangle[i] == 1){
					shapestring += Translator.getMLExitOpen(this.multiloop,Translator.SHAPE_LANG,i);
					if(this.exits[i] != null){
						shapestring += exits[i].ctraverseF((theta+i*45)%360);
					}
					else{
						shapestring += " R ";
					}
					shapestring += Translator.getMLExitClose(this.multiloop,Translator.SHAPE_LANG,i);
				}
			}
		}
		else{
			for(int j=0;j<7;j++){
				i--;
				if(i<0){
					i=7;
				}
				if(this.offsetangle[i] == 1){
					shapestring += Translator.getMLExitOpen(this.multiloop,Translator.SHAPE_LANG,i);
					if(this.exits[i] != null){
						shapestring += exits[i].ctraverseF((theta+i*45)%360);
					}
					else{
						shapestring += " R ";
					}
					shapestring += Translator.getMLExitClose(this.multiloop,Translator.SHAPE_LANG,i);
				}
			}
		}
		shapestring += Translator.getRightContent(this.multiloop,Translator.SHAPE_LANG,index);
		if(exits[index] != null && exits[index].getType() == EditorGui.SS_DOUBLE){
			SingleShape next = ((DoubleSingleShape)exits[index]).getThreePrime();
			if(next != null){
				shapestring += next.ctraverseF(startangle);
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
		int index = ((angle/45)+4)%8 - (theta/45);
		if(index<0){
			index = 8+index;
		}
		int i = index;
		String shapestring = Translator.getLeftContent(this.multiloop, Translator.SHAPE_LANG,i);
		

		if(standardorientation){
			for(int j=0;j<7;j++){
				i++;
				i = i%8;
				if(this.offsetangle[i] == 1){
					shapestring += Translator.getMLExitOpen(this.multiloop,Translator.SHAPE_LANG,i);
					if(exits[i] == null){
						shapestring += " R ";
					}
					else{
						shapestring += exits[i].ctraverseF((theta+i*45)%360);
					}
					shapestring += Translator.getMLExitClose(this.multiloop,Translator.SHAPE_LANG,i);
				}
			}
		}
		else{
			for(int j=0;j<7;j++){
				i--;
				if(i<0){
					i=7;
				}
				if(this.offsetangle[i] == 1){
					shapestring += Translator.getMLExitOpen(this.multiloop,Translator.SHAPE_LANG,i);
					if(exits[i] == null){
						shapestring += " R ";
					}
					else{
						shapestring += exits[i].ctraverseF((theta+i*45)%360);
					}
					shapestring += Translator.getMLExitClose(this.multiloop,Translator.SHAPE_LANG,i);
				}
			}
		}
		shapestring += Translator.getRightContent(this.multiloop, Translator.SHAPE_LANG,index);
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
	        new MultiEdit(this,ds,type);
	    }
	}
	
	/**
	 * Method for setting this building block as the motif head of the structure
	 */
	public void setMotifHead(){
		AffineTransform atexits = new AffineTransform();
		atexits.rotate(StrictMath.toRadians((startangle-theta+180)%360),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
		int index = ((startangle/45)+4)%8 - (theta/45);
		if(index < 0){
			index+=8;
		}
		
		if(exits[index] != null && exits[index].getType() == EditorGui.SS_DOUBLE && ((DoubleSingleShape)exits[index]).getThreePrime() != null){
			Point2D.Double transXY = new Point2D.Double(xloc,yloc);
			atexits.preConcatenate(at);
			atexits.transform(transXY,transXY);
			if((((DoubleSingleShape)exits[index]).getThreePrime()).endContains(transXY)){
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
	 * Returns the current Shape of the motifhead
	 * 
	 * @param zoomfactor, the currently effective zoom factor
	 * @return Shape of the motif head
	 */
	public Shape getMotifHead(double zoomfactor){
		AffineTransform zoom = new AffineTransform();
		zoom.setToScale(zoomfactor,zoomfactor);
		AffineTransform at2 = (AffineTransform) at.clone();
		zoom.concatenate(at2);
		AffineTransform atexits = new AffineTransform();
		atexits.rotate(StrictMath.toRadians((startangle-theta+180)%360),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
		zoom.concatenate(atexits);
		this.setMotifHead();
		return zoom.createTransformedShape(this.motifheadarea);
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
			color = new Color(0,0,128,50);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		//fixed=false, color is transparent
		else{
			color = new Color(0,0,128,30);
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
		
		AffineTransform starttransf = g2.getTransform();
		
		//show the current orientation
		if(isstartelement){    
			AffineTransform atexits = new AffineTransform();
			atexits.rotate(StrictMath.toRadians((startangle-theta+180)%360),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
			g2.transform(atexits);
			int index = ((startangle/45)+4)%8 - (theta/45);
			if(index < 0){
				index+=8;
			}

			g2.setPaint(Color.RED);
			g2.draw(motifheadarea);
			g2.setPaint(Color.black);
			if(exits[index] != null){
				Point2D.Double transXY = new Point2D.Double(xloc,yloc);
				atexits.preConcatenate(at);
				atexits.transform(transXY,transXY);
				if((((DoubleSingleShape)exits[index]).getThreePrime()).endContains(transXY)){
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
		g2.setTransform(starttransf);
		if(isendelement){    
			AffineTransform atexits = new AffineTransform();
			atexits.rotate(StrictMath.toRadians((endangle-theta+180)%360),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
			//atexits.preConcatenate(at);
			g2.transform(atexits);
			int index = ((endangle/45)+4)%8 - (theta/45);
			if(index < 0){
				index+=8;
			}
			
			if(exits[index] != null){
				Point2D.Double transXY = new Point2D.Double(xloc,yloc);
				atexits.preConcatenate(at);
				atexits.transform(transXY,transXY);
				if((((DoubleSingleShape)exits[index]).getFivePrime()).endContains(transXY)){
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


