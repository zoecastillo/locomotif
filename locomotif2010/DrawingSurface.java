package rnaeditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * This class defines all methods and data fields for the actual drawing area
 * of the program.
 *
 * @author Janina Reeder
 *
 */
public class DrawingSurface extends JPanel implements MouseListener, MouseMotionListener, ActionListener{
	private static final long serialVersionUID = -693312240295302788L;
	private RnaShape element; //the element that is currently being added
	private RnaShape selectedshape; //the element that is currently selected, if available
	private RnaStructure rnastruct; //the current RnaStructure
	private RnaShape fiveprimeend;
	private RnaShape threeprimeend;
	private SingleShape sselement;
	private boolean allowelongation, allowconnection;
	private Vector<RnaShape> furtherstarts;//needed if several structure parts are present
	private boolean mousecontained;
	private int elementtype;
	private int buttontype;
	private FreeMagnets magnets; //contains all free binding sites
	private Magnet currentmagnet;//binding site intersecting with element
	private Magnet sspartner;
	private boolean first;
	private EditorGui eg;
	private boolean orientation;
	private JMenuItem size, sequence, exits, loop53, loop35, loc, folding, continuity, globalsize;
	private int degree, structdegree;
	private double zoomfactor;
	private boolean fixed; //is RnaStructure fixed or movable
	private boolean hairpindockable;
	private Shape motifhead;
	private boolean headflag;

	/**
	 * Constructor for a DrawingSurface
	 *
	 * @param eg, the base EditorGui ... needed to call its methods
	 */
	public DrawingSurface(EditorGui eg){
		setPreferredSize(new Dimension(800,600));
		setBackground(Color.white);
		addMouseListener(this);
		addMouseMotionListener(this);
		mousecontained = false;   
		elementtype = 0;
		buttontype = 0;
		rnastruct = new RnaStructure();
		magnets = new FreeMagnets();
		first = true;
		currentmagnet = null;
		this.eg = eg;
		orientation = true;
		selectedshape = null;
		fiveprimeend = null;
		motifhead = null;
		threeprimeend = null;
		furtherstarts = null;
		sselement = null;
		sspartner = null;
		allowelongation = false;
		allowconnection = false;
		size = new JMenuItem("Size");
		size.addActionListener(this);
		sequence = new JMenuItem("Sequence");
		sequence.addActionListener(this);
		exits = new JMenuItem("Exits");
		exits.addActionListener(this);
		loop53 = new JMenuItem("5'-3' loop");
		loop53.addActionListener(this);
		loop35 = new JMenuItem("3'-5' loop");
		loop35.addActionListener(this);
		loc = new JMenuItem("Bulge location");
		loc.addActionListener(this);
		folding = new JMenuItem("Folding");
		folding.addActionListener(this);
		globalsize = new JMenuItem("Global Size");
		globalsize.addActionListener(this);
		continuity = new JMenuItem("Continuity");
		continuity.addActionListener(this);
		degree = 0;
		structdegree = 0;
		zoomfactor = 1;
		fixed = true;
		hairpindockable = false;
		headflag = false;
	}

	/**
	 * The DrawingSurface is reinitialized. Anything stored already is removed
	 */
	public void removeAll(){
		rnastruct = new RnaStructure();
		magnets = new FreeMagnets();
		first = true;
		currentmagnet = null;
		elementtype = 0;
		buttontype = 0;
		element = null;
		sselement = null;
		sspartner = null;
		allowelongation = false;
		allowconnection = false;
		mousecontained = false;
		orientation = true;
		fiveprimeend = null;
		motifhead = null;
		threeprimeend = null;
		furtherstarts = null;
		selectedshape = null;
		eg.setShapefieldText("");
		eg.enableRotation();
		degree = 0;
		structdegree = 0;
		zoomfactor = 1;
		fixed = true;
		hairpindockable = false;
		headflag = false;
		eg.resetAllButtons();
		repaint();
	}

	/**
	 * Getter Method for the RnaStructure shown on this DrawingSurface
	 *
	 * @return the current RnaStructure
	 */
	public RnaStructure getRnaStructure(){
		return this.rnastruct;
	}

	/**
	 * This method checks whether a motif is already constructed within the DrawingSurface
	 * 
	 * @return true, if at least one building block is on the screen; else false
	 */
	public boolean containsStruct(){
		if(this.rnastruct.isEmpty()){
			return false;
		}
		return true;
	}

	/**
	 * changes the current orientation of its RnaStructure
	 * 
	 * @param seqreversal determines whether stored sequence motifs should be reversed as well
	 */
	public void changeOrientation(boolean seqreversal){
		if(orientation){
			orientation = false;
		}
		else{
			orientation = true;
		}
		rnastruct.changeOrientation(seqreversal);
		RnaShape buffiveprime = this.fiveprimeend;
		int buffiveangle = this.threeprimeend.getEndAngle();
		int bufthreeangle = this.fiveprimeend.getStartAngle();
		this.fiveprimeend = this.threeprimeend;
		if(!this.fiveprimeend.getIsStartElement()){
			this.fiveprimeend.setIsEndElement(false,-1,1);
		}
		this.fiveprimeend.setIsStartElement(true,buffiveangle,1);
		updateMotifHead();
		this.threeprimeend = buffiveprime;
		if(!this.threeprimeend.getIsEndElement()){
			this.threeprimeend.setIsStartElement(false,-1,1);
		}
		this.threeprimeend.setIsEndElement(true,bufthreeangle,1);
		adjustShapeField();
		repaint();
	}

	/**
	 * Method for storing a global size range for the entire motif
	 * 
	 * @param minsize, minimum global size
	 * @param maxsize, maximum global size
	 */
	public void storeGlobalSize(int minsize, int maxsize){
		rnastruct.storeGlobalSize(minsize,maxsize);
	}

	/**
	 * Getter method for the global minimum size
	 * 
	 * @return global minimum size
	 */
	public int getGlobalMin(){
		return rnastruct.getGlobalMin();
	}

	/**
	 * Getter method for the global maximum size
	 * 
	 * @return global maximum size
	 */
	public int getGlobalMax(){
		return rnastruct.getGlobalMax();
	}

	public void headClosed(){
		headflag = false;
	}

	public boolean getSearchType(){
		return eg.getSearchType();
	}

	/**
	 * As long as the current structure is finished, it is traversed and
	 * its shape notation or XML code is returned. Else an error message is returned.
	 *
	 * @param type indicates the language the returned String should have
	 * @return String containing a representation of the RnaStructure
	 */
	public String traverseStructure(int type){
		if(rnastruct.isEmpty()){
			return "ERROR: empty structure";
		}
		if(magnets.getNumberOfMagnets()>1 && !(magnets.getNumberOfMagnets() == 2 && (fiveprimeend != threeprimeend || (fiveprimeend.getType() == EditorGui.PSEUDO_TYPE)))){
			return "ERROR in DrawingSurface: attempted a traverseStructure on a non finished RnaStructure";
		}
		return rnastruct.traverseStructure(fiveprimeend,type,eg.getSearchType());
	}

	/**
	 * defines which element was added. This element is then created
	 *
	 * @param elementtype, the type of the element to be created
	 */
	public void setElementType(int elementtype){
		this.elementtype = elementtype;
		this.degree = 0;
		switch(elementtype){
		case 0: element = null; break;
		case EditorGui.STEM_TYPE: element = new StemShape(orientation,magnets);
		break;
		case EditorGui.BULGE_TYPE: element = new BulgeShape(orientation,magnets);
		break;
		case EditorGui.SINGLE_TYPE: element = new SingleShape(orientation,magnets);
		break;
		case EditorGui.INTERNAL_TYPE: element = new InternalShape(orientation,magnets);
		break;
		case EditorGui.HAIRPIN_TYPE: element = new HairpinShape(orientation,magnets);
		break;
		case EditorGui.MULTI_TYPE: element = new MultiShape(orientation,magnets);
		break;
		case EditorGui.CLOSED_STRUCT_TYPE: element = new ClosedStructShape(orientation, magnets);
		break;
		case EditorGui.MULTIEND_TYPE: element = new ClosedMultiShape(orientation, magnets);
		break;
		case EditorGui.PSEUDO_TYPE: element = new PseudoShape(orientation,magnets);
		break;
		}
	}

	/**
	 * This method rotates the element currently attached to the mouse cursor
	 * or the RnaStructure by 45 degrees in the given direction
	 * 
	 * @param int type, the direction in which to rotate
	 */
	public void setRotation(int type){
		// rotate the element attached to the cursor
		if(elementtype > 0){
			if(type==0){ 
				degree += 45;
				element.rotateBy(45);
			}
			else{
				degree -= 45;
				element.rotateBy(315);
			}
			repaint();
		}
		//rotate the RnaStructure
		else{
			if(type == 0){
				structdegree += 45;
				structdegree = structdegree%360;
				rnastruct.adjust(45);
			}
			else{
				structdegree -= 45;
				structdegree = structdegree%360;
				rnastruct.adjust(315);
			}
			updateMotifHead();
			repaint();
		}
	}

	/**
	 * Setter Method for the type of editing button that was pressed
	 *
	 * @param type the type of the button
	 */
	public void setButtonType(int type){
		unselectShape();
		this.buttontype = type;
	}

	/**
	 * This method returns the number of available magnets of the given multishape.
	 * This method is needed for the interactive edit interface of the multishape
	 * 
	 * @param ms, the MultiShape in question
	 * @return the number of full available magnets of the MultiShape
	 */
	public int getNumberOfAvailableMagnets(MultiShape ms){
		return magnets.getNumberOfFullMagnets(ms);
	}

	/**
	 * Method that adjust the shape field content according to the 
	 * current RNA structure shown
	 */
	public void adjustShapeField(){
		if(furtherstarts == null && allowconnection == false && ((magnets.getNumberOfMagnets() ==1 && !rnastruct.containsSS()) || magnets.getNumberOfFullMagnets() == 0)){
			eg.setShapefieldText(rnastruct.traverseStructure(fiveprimeend,0,eg.getSearchType()));
		}
		else{
			eg.setShapefieldText(rnastruct.getCurrentShape(fiveprimeend,furtherstarts));
		}
	}

	/**
	 * Method that checks wether the element added to 
	 * the previous start element is now the open end or not.
	 * In case of the addition of a Hairpin the whole structure is
	 * changed accordingly
	 */
	private void adjustStartElement(){
		if(currentmagnet.getAngle() == (currentmagnet.getParent()).getStartAngle()){
			(currentmagnet.getParent()).setIsStartElement(false,-1,1);
			(currentmagnet.getParent()).setIsEndElement(false,-1,1);
			if(this.elementtype == EditorGui.HAIRPIN_TYPE || this.elementtype == EditorGui.MULTIEND_TYPE){
				fiveprimeend = (currentmagnet.getParent()).traverseToNewStart(currentmagnet.getAngle(), true);
			}
			else{
				element.setIsStartElement(true,currentmagnet.getAngle());
				fiveprimeend = element;
			}
			updateMotifHead();
			threeprimeend = fiveprimeend;
			threeprimeend.setIsEndElement(true, fiveprimeend.getStartAngle(),1);
		}
	}

	/**
	 * This method adjust the start of remote parts of the RnaStructure after they were connected
	 * via a single strand or closed by a hairpin
	 *
	 */
	private void adjustPotentialFurtherStarts(){
		if(furtherstarts.contains(currentmagnet.getParent()) && currentmagnet.getAngle() == (currentmagnet.getParent()).getStartAngle()){
			RnaShape newfurtherstart = null;
			if(this.elementtype == EditorGui.HAIRPIN_TYPE || this.elementtype == EditorGui.MULTIEND_TYPE){
				newfurtherstart = (currentmagnet.getParent()).traverseToNewStart(currentmagnet.getAngle(), true);
				newfurtherstart.setIsStartElement(false,newfurtherstart.getStartAngle(),1);
			}
			else if(this.elementtype == EditorGui.SINGLE_TYPE){
				newfurtherstart = element;
				newfurtherstart.setIsStartElement(false, currentmagnet.getAngle(),1);
			}
			else{
				newfurtherstart = element;
				newfurtherstart.setIsStartElement(false, currentmagnet.getAngle());
			}
			furtherstarts.remove(currentmagnet.getParent());
			furtherstarts.add(newfurtherstart);
		}
	}

	/**
	 * This method is called to adjust the fiveprime and/or threeprimeend according to changes
	 * made via the MultiShape editing interface (e.g. previous start was deleted)
	 * 
	 * @param startend containing the new start and end element: if either is null, the multishape remains start/end element
	 */
	public void adjustEnds(RnaShape[] startend){
		if(startend[0] != null){
			this.fiveprimeend = startend[0];
		}
		if(startend[1] != null){
			this.threeprimeend = startend[1];
		}
	}

	/**
	 * Opens the given File
	 * 
	 * @param rnafile, the File to be opened
	 */
	@SuppressWarnings("unchecked")
	public void openFile(File rnafile){
		try{
			ObjectInputStream s = EditorIO.openObjectInputStream(rnafile);
			rnastruct = (RnaStructure) s.readObject();
			magnets = (FreeMagnets) s.readObject();
			fiveprimeend = (RnaShape) s.readObject();
			updateMotifHead();
			threeprimeend = (RnaShape) s.readObject();
			zoomfactor = s.readDouble();
			degree = s.readInt();
			structdegree = s.readInt();
			orientation = s.readBoolean();
			furtherstarts = (Vector <RnaShape>) s.readObject();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		catch(ClassNotFoundException cnfe){
			cnfe.printStackTrace();
		}
		first = false;
		currentmagnet = null;
		elementtype = 0;
		buttontype = 0;
		element = null;
		mousecontained = false;
		selectedshape = null;
		adjustShapeField();
		eg.enableRotation();
		fixed = true;
		if(magnets.getNumberOfMagnets() == 0){
			eg.disableAllButtons();
		}
		else{
			eg.resetAllButtons();
			adjustSingleStrand();
			adjustClosedStruct();
			adjustHairpinDockable();
		}
		repaint();
	}

	/**
	 * Saves information to the given file
	 * 
	 * @param rnafile, the File to store the information in
	 */
	public void saveFile(File rnafile){
		try{
			ObjectOutputStream s = EditorIO.openObjectOutputStream(rnafile);
			s.writeObject(rnastruct);
			s.writeObject(magnets);
			s.writeObject(fiveprimeend);
			s.writeObject(threeprimeend);
			s.writeDouble(zoomfactor);
			s.writeInt(degree);
			s.writeInt(structdegree);
			s.writeBoolean(orientation);
			s.writeObject(furtherstarts);
			s.flush();
			s.close();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
	}



	/**
	 * This method enables or disables the singlestrand button according to 
	 * the number of open ends of the structure (no ss in open structures)
	 */
	private void adjustSingleStrand(){
		int numofstructs = 1;
		if(furtherstarts != null){
			numofstructs += furtherstarts.size();
		}
		if(rnastruct.isEmpty()){
			eg.setSingleStrandEnabled(false);
		}
		else if(magnets.getNumberOfSingleMagnets() == 0 && magnets.getNumberOfFullMagnets() == numofstructs){ 
			eg.setSingleStrandEnabled(true);

		}
		//else if(magnets.getNumberOfSingleMagnets() > 0 && magnets.getNumberOfFullMagnets() == numofstructs-1){
		//	eg.setSingleStrandEnabled(true);
		//	System.out.println("Each struct one end");
		//}
		else if((magnets.getNumberOfSingleMagnets()/2 + magnets.getNumberOfFullMagnets()) <= numofstructs){
			eg.setSingleStrandEnabled(true);
		}
		else{
			eg.setSingleStrandEnabled(false);
		}
	}

	/**
	 * This method enables or disables the closedstruct button according to 
	 * the available types of magnets
	 */
	private void adjustClosedStruct(){
		if(magnets.containsStandard() || rnastruct.containsPseudo() || rnastruct.isEmpty()){
			eg.setClosedStructEnabled(true);
		}
		else{
			eg.setClosedStructEnabled(false);
		}
	}



	/**
	 * Checks wether a hairpin can be attached to the currend structure
	 */
	public void adjustHairpinDockable(){
		int numofstructs = 1;
		if(furtherstarts != null){
			numofstructs += furtherstarts.size();
		}
		if(magnets.getNumberOfFullMagnets() > numofstructs || (magnets.getNumberOfFullMagnets() == numofstructs) && rnastruct.containsSS() || rnastruct.containsPseudo()){
			hairpindockable = true;
		}
		else{
			hairpindockable = false;
		}
	}

	/**
	 * Checks wether the given element is connected to a furtherstart element.
	 * In that case, the furtherstart element will be removed from the vector
	 *
	 * @param element, the RnaShape that is to be checked against the 
	 *                 furtherstart elements
	 */
	private void adjustFurtherStarts(RnaShape element){
		for(RnaShape fstart : furtherstarts){
			if(element == fstart || element.connectedTo(fstart,-1,element)){
				furtherstarts.remove(fstart);
				break;
			}
		}
	}


	/**
	 * implementation of the MouseListener method.
	 * An Element is added to the current structure if possible
	 *
	 * @param me the MouseEvent
	 */
	public void mouseClicked(MouseEvent me){
		// new element added to the structure
		if(elementtype > 0 && me.getButton() == MouseEvent.BUTTON1){
			//first element of the RnaStructure
			if(first){
				fiveprimeend = element;
				threeprimeend = element;
				if(degree < 0){
					degree = 360+degree;
				}
				//first element is start and end element and thus contains motifhead
				element.setIsStartElement(true,(180+degree)%360,1);
				element.setIsEndElement(true,(180+degree)%360,1);
				updateMotifHead();
				magnets.add(element.getBorders());
				rnastruct.addElement(element);
				//adjusting all fields and buttons
				adjustShapeField();
				adjustHairpinDockable();
				adjustClosedStruct();
				eg.enableRotation();
				this.elementtype = 0;
				this.element = null;
				first = false;
				setButtonType(EditorGui.SELECT_TYPE);
				repaint();
			}
			//adding a standard element, i.e. not a single strand
			else if(elementtype != EditorGui.SINGLE_TYPE){
				//element is within range of a magnet
				if (currentmagnet != null){
					//update the free magnets
					magnets.remove(currentmagnet);
					magnets.add(element.getBorders(currentmagnet));
					//add the new building block to the previous structure
					(currentmagnet.getParent()).addToNeighbors(element,(180+currentmagnet.getAngle())%360,0);
					//add the previous structure's building block to the new one
					element.addToNeighbors(currentmagnet.getParent(),currentmagnet.getAngle(),0);
					rnastruct.addElement(element);
					//update start/potential start element
					if((currentmagnet.getParent()).getIsStartElement()){
						adjustStartElement();
					}
					else if(furtherstarts != null){
						adjustPotentialFurtherStarts();
					}
					//do hairpin access traversal if the new element is a hairpin/closedend
					if(elementtype == EditorGui.HAIRPIN_TYPE || elementtype == EditorGui.MULTIEND_TYPE){
						element.startHPAccessTraversal(currentmagnet.getAngle());
					}
					//new element is multiloop: now we can definitly add hairpins
					else if(elementtype == EditorGui.MULTI_TYPE){
						element.adjustMagnetAccessability(true);
					}
					//if previous building block is hairpin and new element is not multiloop: we cannot add hairpins to new one!
					else if((currentmagnet.getParent()).getType() == EditorGui.HAIRPIN_TYPE || (currentmagnet.getParent()).getType() == EditorGui.MULTIEND_TYPE){
						element.adjustMagnetAccessability(false);
					}
					//in all other cases, hairpin accessability depends on the previous building block
					else{
						element.adjustMagnetAccessability(currentmagnet.getIsHairpinAccessible());
					}
					eg.enableRotation();
					adjustShapeField();
					this.elementtype = 0;
					this.element = null;
					this.currentmagnet = null;
					setButtonType(EditorGui.SELECT_TYPE);
					repaint();
				}
				//new remote part of the structure
				else if(!rnastruct.inRange(element)){
					//initialize furtherstarts
					if(furtherstarts == null){
						furtherstarts = new Vector<RnaShape>();
					}
					furtherstarts.add(element);
					//new element is never start/end element, but it stores a potential start/end angle
					element.setIsStartElement(false,(180+degree)%360,1); //startangle setzen!
					element.setIsEndElement(false,(180+degree)%360,1); //endangle setzen!
					if(degree < 0){
						degree = 360+degree;
					}
					//update free magnets
					magnets.add(element.getBorders());
					rnastruct.addElement(element);
					adjustShapeField();
					eg.enableRotation();
					this.elementtype = 0;
					this.element = null;
					setButtonType(EditorGui.SELECT_TYPE);
					repaint();
				}
				adjustSingleStrand();
				adjustHairpinDockable();
				adjustClosedStruct();
			}
			//single strand is being added
			else{
				if (currentmagnet != null){
					//the single strand is being attached to a structure part: line is formed
					if(sselement == null){
						sselement = (SingleShape) element;
						//update free magnets
						sspartner = magnets.removeSS(currentmagnet);
						//loc keeps track of where the single strand was added to a Magnet (5' or 3')
						int loc = (currentmagnet.getParent()).addSSNeighbor(sselement,(180+currentmagnet.getAngle())%360);
						if((currentmagnet.getParent()).getIsStartElement() && loc == 1){
							//single strand extending 5' end
							sselement.addToNeighbors(currentmagnet.getParent(),currentmagnet.getAngle(),loc);
							sselement.setIsStartElement(true,(currentmagnet.getParent()).getStartAngle(),0);
							(currentmagnet.getParent()).setIsStartElement(false,-1,1);
							this.fiveprimeend = sselement;
						}
						else if((currentmagnet.getParent()).getIsEndElement() && loc == 2){		
							//single strand extending 3' end
							sselement.addToNeighbors(currentmagnet.getParent(),currentmagnet.getAngle(),loc);
							sselement.setIsEndElement(true,(currentmagnet.getParent()).getEndAngle(),0);
							(currentmagnet.getParent()).setIsEndElement(false,-1,1);
							this.threeprimeend = sselement;
						}
						else{
							//single strand added to non 5' or 3' end
							//TODO: this is forbidden at the moment to ensure correctness of the
							//structure and orientation at all times
							//should be improved if possible!
							sselement.addToNeighbors(currentmagnet.getParent(),currentmagnet.getAngle(),loc);
							if(loc == 1 && furtherstarts != null){
								adjustPotentialFurtherStarts();
							}
						}
						//another single strand can be added to this one OR final ss without further motif parts: we can elongate
						if(sspartner != null || (sspartner == null && furtherstarts == null)){
							allowelongation = true;
						}
						//if other parts are present, we can always connect
						if(furtherstarts != null){
							allowconnection = true;
						}
						//create the line
						sselement.initializeLine(currentmagnet,me.getX(),me.getY());
						eg.disableRotation();
						eg.disableFunctionalButtons();
						this.currentmagnet = null;
						updateMotifHead();
						repaint();
					}
					//a previously attached single strand line is connected to another building block
					//single strand can now connect two structure parts
					else if(allowconnection){
						//update free magnets
						magnets.removeSS(currentmagnet);
						//determine where the single strand is added
						int loc = (currentmagnet.getParent()).addSSNeighbor(sselement,(180+currentmagnet.getAngle())%360);
						//TODO: not used at the moment since ss can only be added directly to 5' or 3' end
						if((currentmagnet.getParent()).getIsStartElement() && loc == 1){
							//single strand connecting to 5' end
							sselement.addToNeighbors(currentmagnet.getParent(),currentmagnet.getAngle(),loc);
							this.fiveprimeend = sselement.traverseToNewStart((currentmagnet.getParent()).getStartAngle(), false);
							(currentmagnet.getParent()).setIsStartElement(false,-1,1);
						}
						else if((currentmagnet.getParent()).getIsEndElement() && loc == 2){
							//single strand connecting to 3' end
							sselement.addToNeighbors(currentmagnet.getParent(),currentmagnet.getAngle(),loc);
							this.threeprimeend = sselement.traverseToNewEnd((currentmagnet.getParent()).getEndAngle());
							(currentmagnet.getParent()).setIsEndElement(false,-1,1);
						}
						else{
							//single strand connecting to non 5' or 3' end
							sselement.addToNeighbors(currentmagnet.getParent(),currentmagnet.getAngle(),loc);
							if(sselement.getIsStartElement()){
								this.fiveprimeend = (currentmagnet.getParent()).traverseToNewStart((180+currentmagnet.getAngle())%360, false);
								sselement.setIsStartElement(false,-1,1);
							}
							else if(sselement.getIsEndElement()){
								this.threeprimeend = (currentmagnet.getParent()).traverseToNewEnd((180+currentmagnet.getAngle())%360);
								sselement.setIsEndElement(false,-1,1);
							}
							else if(loc == 1 && furtherstarts != null){
								adjustPotentialFurtherStarts();
							}
						}
						adjustFurtherStarts(currentmagnet.getParent());
						if(furtherstarts.isEmpty()){
							furtherstarts = null;
						}
						rnastruct.addElement(sselement);
						sselement.setIsConnector();
						this.currentmagnet = null;
						this.sselement = null;
						this.element = null;
						this.elementtype = 0;
						this.sspartner = null;
						allowconnection = false;
						allowelongation = false;
						eg.enableRotation();
						eg.enableFunctionalButtons();
						adjustShapeField();
						adjustSingleStrand();
						adjustClosedStruct();
						setButtonType(EditorGui.SELECT_TYPE);
						updateMotifHead();
						repaint();
					}
				}
				//single strand was previously attached and is now used to extend a structure part
				else if(allowelongation){
					if(!rnastruct.inRange(me.getX(),me.getY())){
						//not in range of another magnet
						sselement.storeFinalPath();
						rnastruct.addElement(sselement);
						this.currentmagnet = null;
						this.sselement = null;
						this.sspartner = null;
						this.element = null;
						this.elementtype = 0;
						allowconnection = false;
						allowelongation = false;
						eg.enableRotation();
						eg.enableFunctionalButtons();
						adjustShapeField();
						adjustSingleStrand();
						setButtonType(EditorGui.SELECT_TYPE);
						updateMotifHead();
						repaint();
					}
				}
				if(magnets.getNumberOfMagnets() == 0){
					eg.disableAllButtons();
				}
			}
			//enlarge the viewing port
			if(rnastruct.getWidth() > (int)(this.getSize()).getWidth()){
				setPreferredSize(new Dimension(rnastruct.getWidth(),(int)(this.getSize()).getHeight()));
				eg.adjustScrollPane(this);
			}
			//enlarge the viewing port
			if(rnastruct.getHeight() > (int)(this.getSize()).getHeight()){
				setPreferredSize(new Dimension((int)(this.getSize()).getWidth(),rnastruct.getHeight()));
				eg.adjustScrollPane(this);
			}
			adjustSingleStrand();
		}
		//an editing operation is invoked
		else if(me.getButton() == MouseEvent.BUTTON1){
			RnaShape s = rnastruct.getShape(me.getPoint(),zoomfactor);
			//clock within the motif structure
			if(s!=null){
				if(me.getClickCount() == 1){
					switch(buttontype){
					//draw the selection
					case EditorGui.SELECT_TYPE: showSelected(s); 
					break;
					//detach a building block
					case EditorGui.DETACH_TYPE:
						int i=s.getEndType();
						boolean isend = s.getIsStartElement() || s.getIsEndElement();
						//we are detaching an end element of the structure
						if(isend || i != -1){
							if(s.getType() != EditorGui.SINGLE_TYPE){
								//we can only detach elements not connected to a ss
								if(rnastruct.containsSS() && s.startFindSS()){
									JOptionPane.showMessageDialog(null,"You cannot detach this element as long as it is connected to any single strands.","Impossible",JOptionPane.ERROR_MESSAGE);
									break;
								}
							}
							else{
								//we must remove ss from 5' or 3' first to avoid motif disruption
								if(!((SingleShape)s).removalAllowed()){
									JOptionPane.showMessageDialog(null,"You can only detach single strands from the 5' or 3' end of the structure.","Impossible",JOptionPane.ERROR_MESSAGE);
									break;
								}
							}
							//the detached element is the new element next to the cursor
							element = s;
							elementtype = element.getType();
							//update free magnets and rnastructure
							magnets.remove(element);
							rnastruct.remove(element);
							//we detached the last element of the structure
							if(rnastruct.isEmpty()){
								eg.setShapefieldText("");
								first = true;
								element.removeNeighbors();
								element.setIsStartElement(false,-1,1);
								element.setIsEndElement(false,-1,1);
								fiveprimeend = null;
								threeprimeend = null;
								motifhead = null;
							}
							//a hairpin or closed end was detached which was the start element: only element of this motif part!
							else if((s.getType() == EditorGui.HAIRPIN_TYPE || s.getType() == EditorGui.MULTIEND_TYPE) && s.getIsStartElement()){
								fiveprimeend = furtherstarts.remove(0);
								threeprimeend = fiveprimeend;
								fiveprimeend.setIsStartElement(true,fiveprimeend.getStartAngle(),1);
								threeprimeend.setIsEndElement(true,fiveprimeend.getStartAngle(),1);
								element.setIsStartElement(false,-1,1);
								element.setIsEndElement(false,-1,1);
								adjustShapeField();
							}
							else{
								//we are detaching the start element (not a hairpin/closedend)
								if(s.getIsStartElement()){
									fiveprimeend = s.findNewStartElement(i);
									//we are detaching last element of the main motif part, but another motif part is available
									if(fiveprimeend == null && furtherstarts != null){
										fiveprimeend = furtherstarts.remove(0);
										fiveprimeend.setIsStartElement(true,fiveprimeend.getStartAngle(),1);
										if(furtherstarts.isEmpty()){
											furtherstarts = null;
										}
									}
								}
								//detaching the end element
								if(s.getIsEndElement()){
									//find new end element
									threeprimeend = s.findNewEndElement(i);
									//if not within this motif part, it is the same as 5' from new motif part
									if(threeprimeend == null){
										threeprimeend = fiveprimeend;
										threeprimeend.setIsEndElement(true,fiveprimeend.getStartAngle(),1);
									}
								}
								//detaching a single strand element
								if(s.getType() == EditorGui.SINGLE_TYPE && fiveprimeend != threeprimeend){
									//ss to be detached connects 3' end with rest: 3' end must be moved to rest
									if(this.threeprimeend == ((SingleShape)s).getThreePrimeEnd()){
										threeprimeend.setIsEndElement(false, threeprimeend.getEndAngle(),1);
										threeprimeend.setIsStartElement(false, threeprimeend.getEndAngle(),1);
										threeprimeend = ((SingleShape)s).getFivePrimeEnd();
										threeprimeend.setIsEndElement(true,(degree+((SingleShape)s).getFivePrimeAngle())%360,1);
										if(furtherstarts == null){
											furtherstarts = new Vector<RnaShape>();
										}
										furtherstarts.add(((SingleShape)s).getThreePrimeEnd());
									}
									//ss connects 5' end with rest: move 5' end to rest
									else if(this.fiveprimeend == ((SingleShape)s).getFivePrimeEnd()){
										fiveprimeend.setIsStartElement(false, fiveprimeend.getStartAngle(), 1);
										fiveprimeend = ((SingleShape)s).getThreePrimeEnd();
										fiveprimeend.setIsStartElement(true,(degree+((SingleShape)s).getThreePrimeAngle())%360,1);
										if(furtherstarts == null){
											furtherstarts = new Vector<RnaShape>();
										}
										furtherstarts.add(((SingleShape)s).getFivePrimeEnd());
									}
								}
								//detached element is not part of the main motif part, but potential start of another part
								if(furtherstarts != null && furtherstarts.contains(s)){
									furtherstarts.remove(s);
									//potential further start element must be updated
									RnaShape newfurtherstart = s.findNewStartElement(i);
									if(newfurtherstart != null){
										newfurtherstart.setIsStartElement(false,newfurtherstart.getStartAngle(),1);
										newfurtherstart.setIsEndElement(false,newfurtherstart.getStartAngle(),1);
										furtherstarts.add(newfurtherstart);
									}
									//or perhaps the last element of any further motif part is detached
									if(furtherstarts.isEmpty()){
										furtherstarts = null;
									}
								}
								s.removeNeighbors();
								s.setIsStartElement(false,-1,1);
								s.setIsEndElement(false, -1,1);
								adjustShapeField();
							}
							//update all buttons and fields
							eg.resetAllButtons();
							if(elementtype == EditorGui.MULTI_TYPE || elementtype == EditorGui.SINGLE_TYPE){
								eg.disableRotation();
							}
							element.resetHPAccessability();
							adjustSingleStrand();
							adjustHairpinDockable();
							adjustClosedStruct();
							updateMotifHead();
							repaint();
						}
						//click in a building block which is not an end element of the structure
						else{
							JOptionPane.showMessageDialog(null,"You can only detach end elements of the structure.","Impossible",JOptionPane.ERROR_MESSAGE);
						}
						break;
						//deleting an element
					case EditorGui.REMOVE_TYPE:
						i = s.getEndType();
						isend = s.getIsStartElement() || s.getIsEndElement();
						//it is an end element
						if(isend || i != -1){
							if(s.getType() != EditorGui.SINGLE_TYPE){
								//only parts not connected to ss can be removed
								if(rnastruct.containsSS() && s.startFindSS()){
									JOptionPane.showMessageDialog(null,"You cannot remove this element as long as it is connected to any single strands.","Impossible",JOptionPane.ERROR_MESSAGE);
									break;
								}
							}
							else{
								//ss can only be removed from 5' or 3' to avoid disruption of ends
								if(!((SingleShape)s).removalAllowed()){
									JOptionPane.showMessageDialog(null,"You can only remove single strands from the 5' or 3' end of the structure.","Impossible",JOptionPane.ERROR_MESSAGE);
									break;
								}
							}
							//update free magnets and rnastructure
							magnets.remove(s);
							rnastruct.remove(s);
							//last element was removed
							if(rnastruct.isEmpty()){
								eg.setShapefieldText("");
								first = true;
								s.removeNeighbors();
								s.setIsStartElement(false,-1,1);
								s.setIsEndElement(false,-1,1);
								fiveprimeend = null;
								threeprimeend = null;
								motifhead = null;
							}
							//deleting a hairpin/closedend which is the start element
							else if((s.getType() == EditorGui.HAIRPIN_TYPE || s.getType() == EditorGui.MULTIEND_TYPE) && s.getIsStartElement()){
								//another motif part must contain building blocks: use on further start as new start element
								fiveprimeend = furtherstarts.remove(0);
								threeprimeend = fiveprimeend;
								fiveprimeend.setIsStartElement(true,fiveprimeend.getStartAngle(),1);
								threeprimeend.setIsEndElement(true,fiveprimeend.getStartAngle(),1);//last element in furtherstarts was deleted
								if(furtherstarts.isEmpty()){
									furtherstarts = null;
								}
								adjustShapeField();
							}
							else{
								//deleting the start/end element (not hairpin/closedend, can be ss!)
								if(s.getIsStartElement()){
									//find new start element in the same motif part
									fiveprimeend = s.findNewStartElement(i);
									//not available: get one from other motif part
									if(fiveprimeend == null && furtherstarts != null){
										fiveprimeend = furtherstarts.remove(0);
										fiveprimeend.setIsStartElement(true,fiveprimeend.getStartAngle(),1);
										//last element in furtherstarts was deleted
										if(furtherstarts.isEmpty()){
											furtherstarts = null;
										}
									}
								}
								if(s.getIsEndElement()){
									//find new end element from same motif part
									threeprimeend = s.findNewEndElement(i);
									//not available: get one from other motif part
									if(threeprimeend == null){
										threeprimeend = fiveprimeend;
										threeprimeend.setIsEndElement(true,fiveprimeend.getStartAngle(),1);
									}
								}
								//deleting ss and 5' not equal to 3'
								if(s.getType() == EditorGui.SINGLE_TYPE && fiveprimeend != threeprimeend){
									//ss connects 3' with rest: move 3' to rest
									if(this.threeprimeend == ((SingleShape)s).getThreePrimeEnd()){
										threeprimeend.setIsEndElement(false, threeprimeend.getEndAngle(),1);
										threeprimeend.setIsStartElement(false, threeprimeend.getEndAngle(),1);
										threeprimeend = ((SingleShape)s).getFivePrimeEnd();
										threeprimeend.setIsEndElement(true,(degree+((SingleShape)s).getFivePrimeAngle())%360,1);
										if(furtherstarts == null){
											furtherstarts = new Vector<RnaShape>();
										}
										furtherstarts.add(((SingleShape)s).getThreePrimeEnd());
									}
									//ss connects 5' with rest: move 5' to rest
									else if(this.fiveprimeend == ((SingleShape)s).getFivePrimeEnd()){
										fiveprimeend.setIsStartElement(false, fiveprimeend.getStartAngle(), 1);
										fiveprimeend = ((SingleShape)s).getThreePrimeEnd();
										fiveprimeend.setIsStartElement(true,(degree+((SingleShape)s).getThreePrimeAngle())%360,1);
										if(furtherstarts == null){
											furtherstarts = new Vector<RnaShape>();
										}
										furtherstarts.add(((SingleShape)s).getFivePrimeEnd());
									}
								}
								//deleting a potential further start
								if(furtherstarts != null && furtherstarts.contains(s)){
									furtherstarts.remove(s);
									//find a new potential further start
									RnaShape newfurtherstart = s.findNewStartElement(i);
									//add new furtherstart to array
									if(newfurtherstart != null){
										newfurtherstart.setIsStartElement(false,newfurtherstart.getStartAngle(),1);
										newfurtherstart.setIsEndElement(false,newfurtherstart.getStartAngle(),1);
										furtherstarts.add(newfurtherstart);
									}
									//last element in furtherstarts was deleted
									if(furtherstarts.isEmpty()){
										furtherstarts = null;
									}
								}
								s.removeNeighbors();
								s.setIsStartElement(false,-1,1);
								s.setIsEndElement(false, -1,1);
								adjustShapeField();
							}
							eg.resetAllButtons();
							adjustSingleStrand();
							adjustHairpinDockable();
							adjustClosedStruct();
							updateMotifHead();
							repaint();
						}
						else{
							JOptionPane.showMessageDialog(null,"You can only remove end elements of the structure.","Impossible",JOptionPane.ERROR_MESSAGE);
						}
						break;
						//moving the structure
					case EditorGui.MOVE_TYPE:
						//lift the structure from screen and store the move center
						if(fixed){
							fixed = false;
							double mex = me.getX()/zoomfactor;
							double mey = me.getY()/zoomfactor;
							rnastruct.storeMoveCenter(mex,mey);
						}
						//reattach the structure to the screen
						else{
							fixed = true;
						}
						//update the motifhead
						updateMotifHead();
						repaint();
						break;
					}
				}
				//editing window to be opened
				else {
					if(s != selectedshape && selectedshape != null){
						selectedshape.setSelected(false);
					}
					setButtonType(EditorGui.SELECT_TYPE);
					s.setSelected(true);
					selectedshape = s;
					fixed = true;
					repaint();
					s.openEditWindow(this,0);
				}
			}
			//edit the motif head
			else if(motifhead != null && motifhead.contains(me.getPoint()) && headflag == false){
				headflag = true;
				new MotifheadEdit(this.eg);
			}
			//remove selection
			else if(selectedshape != null){
				selectedshape.setSelected(false);
				selectedshape = null;
				repaint();
			}
		}
		//delete ss currently attached to mouse cursor
		else if(me.getButton() == MouseEvent.BUTTON3 && element != null){
			//elongated single strand
			if(sselement != null){
				selectedshape = sselement;
				removeSelected();
				sselement = null;
			}
			setElementType(0);
			repaint();
		}
		//option menu
		else if(me.getButton() == MouseEvent.BUTTON2 || me.getButton() == MouseEvent.BUTTON3){
			RnaShape s = rnastruct.getShape(me.getPoint(),zoomfactor);
			if(s!=null){
				if(s != selectedshape && selectedshape != null){
					selectedshape.setSelected(false);
				}
				setButtonType(EditorGui.SELECT_TYPE);
				s.setSelected(true);
				selectedshape = s;
				fixed = true;
				repaint();
				JPopupMenu popup = new JPopupMenu("Edit");
				if(s instanceof StemShape){
					popup.add(size);
					popup.add(sequence); 
					popup.add(continuity);
					popup.add(globalsize);
				}
				else if(s instanceof HairpinShape){
					popup.add(size);
					popup.add(sequence);
				}
				else if(s instanceof ClosedMultiShape){
					popup.add(size);
				}
				else if(s instanceof BulgeShape){
					popup.add(size);
					popup.add(loc);
					popup.add(sequence); 
					popup.add(globalsize);
				}
				else if (s instanceof InternalShape){
					popup.add(loop53);
					popup.add(loop35);
					popup.add(sequence); 
					popup.add(globalsize);
				}
				else if (s instanceof MultiShape){
					popup.add(exits);
					popup.add(size);
					popup.add(sequence); 
					popup.add(globalsize);
				}
				else if (s instanceof SingleShape){
					popup.add(size);
					popup.add(sequence); 
					if(((SingleShape)s).getIsConnector()){
						popup.add(folding);
					}
				}
				else if (s instanceof ClosedStructShape){
					popup.add(globalsize);
				}
				popup.show(me.getComponent(),me.getX(),me.getY());
			}
			else if(selectedshape != null){
				selectedshape.setSelected(false);
				selectedshape = null;
				repaint();
			}
		}
		if(fiveprimeend != null){
			updateMotifHead();
		}
	}

	/**
	 * mark the given RnaShape as selected
	 *
	 * @param s the RnaShape that was chosen
	 */
	private void showSelected(RnaShape s){
		if(s != selectedshape && selectedshape != null){
			selectedshape.setSelected(false);
		}
		s.setSelected(true);
		selectedshape = s;
		repaint();
	}

	/**
	 * This method checks wether an element is selected
	 *
	 * @return true, if an element is selected; else false
	 */
	public boolean getIsSingleSelected(){
		if(selectedshape != null){
			return true;
		}
		return false;
	}

	/**
	 * This method detaches a selected element from the RnaStructure
	 */
	public void detachSelected(){
		int i=selectedshape.getEndType();
		boolean isend = selectedshape.getIsStartElement() || selectedshape.getIsEndElement();
		//detaching an end element
		if(isend || i != -1){
			if(selectedshape.getType() != EditorGui.SINGLE_TYPE){
				//only parts not connected to a ss can be detached
				if(rnastruct.containsSS() && selectedshape.startFindSS()){
					JOptionPane.showMessageDialog(null,"You cannot detach this element as long as it is connected to any single strands.","Impossible",JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			else{
				//ss connected to 5' or 3' end must be removed first
				if(!((SingleShape)selectedshape).removalAllowed()){
					JOptionPane.showMessageDialog(null,"You can only detach single strands from the 5' or 3' end of the structure.","Impossible",JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			//reattach element to cursor
			element = selectedshape;
			elementtype = element.getType();
			//update free magnets and rnastructure
			magnets.remove(element);
			rnastruct.remove(element);
			//detach last element of structure
			if(rnastruct.isEmpty()){
				eg.setShapefieldText("");
				first = true;
				element.removeNeighbors();
				element.setIsStartElement(false,-1,1);
				element.setIsEndElement(false,-1,1);
				fiveprimeend = null;
				threeprimeend = null;
				motifhead = null;
			}
			//detaching hairpin/closedend which is start element
			else if((selectedshape.getType() == EditorGui.HAIRPIN_TYPE || selectedshape.getType() == EditorGui.MULTIEND_TYPE) && selectedshape.getIsStartElement()){
				//furtherstarts contains the new start element
				fiveprimeend = furtherstarts.remove(0);
				threeprimeend = fiveprimeend;
				fiveprimeend.setIsStartElement(true,fiveprimeend.getStartAngle(),1);
				threeprimeend.setIsEndElement(true,fiveprimeend.getStartAngle(),1);
				element.setIsStartElement(false,-1,1);
				element.setIsEndElement(false,-1,1);
				adjustShapeField();
			}
			else{
				//detaching start element (not hairpin/closedend; can be ss)
				if(element.getIsStartElement()){
					//find new start
					fiveprimeend = element.findNewStartElement(i);
					//none in this motif part: use other part
					if(fiveprimeend == null && furtherstarts != null){
						fiveprimeend = furtherstarts.remove(0);
						fiveprimeend.setIsStartElement(true,fiveprimeend.getStartAngle(),1);
						if(furtherstarts.isEmpty()){
							furtherstarts = null;
						}
					}
				}
				//detaching end element (not hairpin/closedend)
				if(element.getIsEndElement()){
					//find new end
					threeprimeend = element.findNewEndElement(i);
					//not available: same as 5' end
					if(threeprimeend == null){
						threeprimeend = fiveprimeend;
						threeprimeend.setIsEndElement(true,fiveprimeend.getStartAngle(),1);
					}
				}
				//detaching ss which is not start/end, and 5' not equal 3'
				if(element.getType() == EditorGui.SINGLE_TYPE && fiveprimeend != threeprimeend){
					//ss connects 3' with rest: move 3' to rest
					if(this.threeprimeend == ((SingleShape)element).getThreePrimeEnd()){
						threeprimeend.setIsEndElement(false, threeprimeend.getEndAngle(),1);
						threeprimeend.setIsStartElement(false, threeprimeend.getEndAngle(),1);
						threeprimeend = ((SingleShape)element).getFivePrimeEnd();
						threeprimeend.setIsEndElement(true,(degree+((SingleShape)element).getFivePrimeAngle())%360,1);
						if(furtherstarts == null){
							furtherstarts = new Vector<RnaShape>();
						}
						furtherstarts.add(((SingleShape)element).getThreePrimeEnd());
					}
					//ss connects 5' with rest: move 5' to rest
					else if(this.fiveprimeend == ((SingleShape)element).getFivePrimeEnd()){
						fiveprimeend.setIsStartElement(false, fiveprimeend.getStartAngle(), 1);
						fiveprimeend = ((SingleShape)element).getThreePrimeEnd();
						fiveprimeend.setIsStartElement(true,(degree+((SingleShape)element).getThreePrimeAngle())%360,1);
						if(furtherstarts == null){
							furtherstarts = new Vector<RnaShape>();
						}
						furtherstarts.add(((SingleShape)element).getFivePrimeEnd());
					}
				}
				//detach element which is further start element
				if(furtherstarts != null && furtherstarts.contains(selectedshape)){
					//must be removed from furtherstarts and potential new start is searched
					furtherstarts.remove(selectedshape);
					RnaShape newfurtherstart = selectedshape.findNewStartElement(i);
					if(newfurtherstart != null){
						newfurtherstart.setIsStartElement(false,newfurtherstart.getStartAngle(),1);
						newfurtherstart.setIsEndElement(false,newfurtherstart.getStartAngle(),1);
						furtherstarts.add(newfurtherstart);
					}
					if(furtherstarts.isEmpty()){
						furtherstarts = null;
					}
				}
				//in all cases, element must remove neighbors, is no longer start/end
				element.removeNeighbors();
				element.setIsStartElement(false,-1,1);
				element.setIsEndElement(false, -1,1);
				adjustShapeField();
			}
			adjustSingleStrand();
			adjustHairpinDockable();
			adjustClosedStruct();
			unselectShape();
			updateMotifHead();
			if(elementtype == EditorGui.MULTI_TYPE || elementtype == EditorGui.SINGLE_TYPE){
				eg.disableRotation();
			}
			element.resetHPAccessability();
			repaint();
		}
		else{
			JOptionPane.showMessageDialog(null,"You can only detach end elements of the structure.","Impossible",JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * This method removes a selected element from the RnaStructure
	 */
	public void removeSelected(){
		int i = selectedshape.getEndType();
		boolean isend = selectedshape.getIsStartElement() || selectedshape.getIsEndElement();
		//element must be end element
		if(isend || i != -1){
			if(selectedshape.getType() != EditorGui.SINGLE_TYPE){
				//only parts not connected to ss can be removed
				if(rnastruct.containsSS() && selectedshape.startFindSS()){
					JOptionPane.showMessageDialog(null,"You cannot remove this element as long as it is connected to any single strands.","Impossible",JOptionPane.ERROR_MESSAGE);
					return;
					//TODO: alt: Do you want to remove all single strands? YES -> in rnastruct und magnets alle single strands loeschen plus buffern?
				}
			}
			else{
				//only ss connected to 5' or 3' end can be removed
				if(!((SingleShape)selectedshape).removalAllowed()){
					JOptionPane.showMessageDialog(null,"You can only remove single strands from the 5' or 3' end of the structure.","Impossible",JOptionPane.ERROR_MESSAGE);
					return;
					//TODO: alt: Do you want to remove all single strands? YES -> in rnastruct und magnets alle single strands loeschen plus buffern?
				}
			}
			//update free magnets and rnastructure
			magnets.remove(selectedshape);
			rnastruct.remove(selectedshape);
			//deleting the last element from the structure
			if(rnastruct.isEmpty()){
				eg.setShapefieldText("");
				first = true;
				selectedshape.removeNeighbors();
				selectedshape.setIsStartElement(false,-1,1);
				selectedshape.setIsEndElement(false,-1,1);
				fiveprimeend = null;
				threeprimeend = null;
				motifhead = null;
			}
			//deleting hairpin/closedend which is start element
			else if((selectedshape.getType() == EditorGui.HAIRPIN_TYPE || selectedshape.getType() == EditorGui.MULTIEND_TYPE) && selectedshape.getIsStartElement()){
				//get new start from further starts
				fiveprimeend = furtherstarts.remove(0);
				threeprimeend = fiveprimeend;
				fiveprimeend.setIsStartElement(true,fiveprimeend.getStartAngle(),1);
				threeprimeend.setIsEndElement(true,fiveprimeend.getStartAngle(),1);
				adjustShapeField();
			}
			else{
				//deleting start element (not hairpin/closedend)
				if(selectedshape.getIsStartElement()){
					//find new start in main motif part
					fiveprimeend = selectedshape.findNewStartElement(i);
					//not available: furtherstarts provides new start for new main part
					if(fiveprimeend == null && furtherstarts != null){
						fiveprimeend = furtherstarts.remove(0);
						fiveprimeend.setIsStartElement(true,fiveprimeend.getStartAngle(),1);
						if(furtherstarts.isEmpty()){
							furtherstarts = null;
						}
					}
				}
				//deleting end element
				if(selectedshape.getIsEndElement()){
					//find new end in main part
					threeprimeend = selectedshape.findNewEndElement(i);
					//not available: use fiveprimeend
					if(threeprimeend == null){
						threeprimeend = fiveprimeend;
						threeprimeend.setIsEndElement(true,fiveprimeend.getStartAngle(),1);
					}
				}
				//delete ss which is not 5' or 3'
				if(selectedshape.getType() == EditorGui.SINGLE_TYPE && fiveprimeend != threeprimeend){
					//ss connects 3' with rest: move 3' to rest
					if(this.threeprimeend == ((SingleShape)selectedshape).getThreePrimeEnd()){
						threeprimeend.setIsEndElement(false, threeprimeend.getEndAngle(),1);
						threeprimeend.setIsStartElement(false, threeprimeend.getEndAngle(),1);
						threeprimeend = ((SingleShape)selectedshape).getFivePrimeEnd();
						threeprimeend.setIsEndElement(true,(degree+((SingleShape)selectedshape).getFivePrimeAngle())%360,1);
						if(furtherstarts == null){
							furtherstarts = new Vector<RnaShape>();
						}
						furtherstarts.add(((SingleShape)selectedshape).getThreePrimeEnd());
					}
					//ss connects 5' with rest: move 5' to rest
					else if(this.fiveprimeend == ((SingleShape)selectedshape).getFivePrimeEnd()){
						fiveprimeend.setIsStartElement(false, fiveprimeend.getStartAngle(), 1);
						fiveprimeend = ((SingleShape)selectedshape).getThreePrimeEnd();
						fiveprimeend.setIsStartElement(true,(degree+((SingleShape)selectedshape).getThreePrimeAngle())%360,1);
						if(furtherstarts == null){
							furtherstarts = new Vector<RnaShape>();
						}
						furtherstarts.add(((SingleShape)selectedshape).getFivePrimeEnd());
					}
				}
				//delete further start element
				if(furtherstarts != null && furtherstarts.contains(selectedshape)){
					furtherstarts.remove(selectedshape);
					//find a new start connected to it and add it to furtherstarts if available
					RnaShape newfurtherstart = selectedshape.findNewStartElement(i);
					if(newfurtherstart != null){
						newfurtherstart.setIsStartElement(false,newfurtherstart.getStartAngle(),1);
						newfurtherstart.setIsEndElement(false,newfurtherstart.getStartAngle(),1);
						furtherstarts.add(newfurtherstart);
					}
					if(furtherstarts.isEmpty()){
						furtherstarts = null;
					}
				}
				selectedshape.removeNeighbors();
				selectedshape.setIsStartElement(false,-1,1);
				selectedshape.setIsEndElement(false, -1,1);
				adjustShapeField();
			}
			eg.resetAllButtons();
			adjustSingleStrand();
			adjustHairpinDockable();
			adjustClosedStruct();
			unselectShape();
			updateMotifHead();
			repaint();
		}
		else{
			JOptionPane.showMessageDialog(null,"You can only remove end elements of the structure.","Impossible",JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * undo any selection
	 */
	public void unselectShape(){
		if(selectedshape != null){
			selectedshape.setSelected(false);
			selectedshape = null;
		}
	}

	/**
	 * This method increases the zoom factor we're using
	 */
	public void increaseZoomFactor(){
		zoomfactor += 0.1;
		updateMotifHead();
		repaint();
	}

	/**
	 * This method decreases the zoom factor we're using
	 */
	public void decreaseZoomFactor(){
		if(zoomfactor > 0){
			zoomfactor -= 0.1;
			updateMotifHead();
		}
		repaint();
	}

	/**
	 * Mouse entered the DrawingSurface
	 * Adjust location of the current element
	 *
	 * @param me the MouseEvent
	 */
	public void mouseEntered(MouseEvent me){
		mousecontained = true;
		if(sselement != null){
			sselement.adjustCurrentLine(me.getX(),me.getY());
		}
		else{
			switch(elementtype){
			case 0: 
				if(!fixed){
					rnastruct.changeLocation(me.getX(),me.getY());
				}
				break;
			default: element.changeLocation(me.getX(),me.getY());
			}
			repaint();
		}
	}

	/** 
	 * Mouse exited the DrawingSurface
	 * No element has to be drawn
	 * 
	 * @param me the MouseEvent
	 */
	public void mouseExited(MouseEvent me){
		mousecontained = false;
		switch(elementtype){
		case 0: 
			if(!fixed){
				rnastruct.changeLocation(me.getX(),me.getY());
				repaint();
			}
			break;
		default: repaint();
		}
	}

	public void mousePressed(MouseEvent me){}
	public void mouseReleased(MouseEvent me){}
	public void mouseDragged(MouseEvent me){}

	/**
	 * If an element is present, its location is adjusted to the 
	 * mouse' movement
	 *
	 * @param me the MouseEvent
	 */
	public void mouseMoved(MouseEvent me){
		double mex = me.getX();
		double mey = me.getY();
		//mouse location must be updated according to zoomfactor
		if(zoomfactor != 1){
			mex = (mex / zoomfactor);
			mey = (mey / zoomfactor);
		}
		//mouse represents end of ss line: move line around; potential snap to magnet
		if(sselement != null){
			sselement.adjustCurrentLine(mex,mey);
			currentmagnet = magnets.inRange(mex,mey);
			if(currentmagnet != null && currentmagnet != sspartner){
				sselement.lineSnapTo(currentmagnet);
			}
			repaint();
		}
		//move hairpin/closedend aroun
		else if(elementtype == EditorGui.HAIRPIN_TYPE || elementtype == EditorGui.MULTIEND_TYPE){
			element.changeLocation(mex,mey);
			//snap if hairpin can generally dock
			if(hairpindockable){
				//checks for hairpin accessability
				currentmagnet = magnets.inRange(element);
				if(currentmagnet != null){
					element.restore();
					element.snapTo(currentmagnet);
				}
				else{
					element.restore();
				}
			}
			repaint();
		}
		//other building block is moved (can be unconnected ss)
		else if(elementtype>0){
			element.changeLocation(mex,mey);
			//in range also checks for ss addition to both sides of a magnet: currentmagnet is then half magnet
			currentmagnet = magnets.inRange(element);
			if(currentmagnet != null){
				element.restore();
				element.snapTo(currentmagnet);
			}
			else{
				element.restore();
			}
			repaint();
		} 
		//move structure around
		if(!fixed){
			rnastruct.changeLocation(mex,mey);
			repaint();
		}
	}

	/**
	 * ActionListener for the popup menu buttons of each shown type of shape
	 * 
	 * @param ae, the ActionEvent that occured
	 */
	public void actionPerformed(ActionEvent ae){
		Object src = ae.getSource();
		if(src == size){
			selectedshape.openEditWindow(this,1);
		}
		if(src == sequence){
			selectedshape.openEditWindow(this,2);
		}
		if(src == loc){
			selectedshape.openEditWindow(this,3);
		}
		if(src == loop53){
			selectedshape.openEditWindow(this,4);
		}
		if(src == loop35){
			selectedshape.openEditWindow(this,5);
		}
		if(src == exits){
			selectedshape.openEditWindow(this,6);
		}
		if(src == folding){
			selectedshape.openEditWindow(this,7);
		}
		if(src == continuity){
			selectedshape.openEditWindow(this,8);
		}
		if(src == globalsize){
			selectedshape.openEditWindow(this,9);
		}
	}


	/**
	 * Method for updating the motif head which is obtained from the 5' end using the zoom factor
	 *
	 */
	public void updateMotifHead(){
		if(fiveprimeend != null){
			motifhead = fiveprimeend.getMotifHead(zoomfactor);
		}
		else{
			motifhead = null;
		}
	}

	/**
	 * This method checks whether the RnaStructure contains the given point under the
	 * current zoomfactor and updates the relevant tool tip text.
	 * This method is called automatically when the mouse hovers somewhere and is overwriting
	 * the contains method of JComponent (parent class of JPanel)
	 * 
	 * @param x, the x coordinate of the point
	 * @param y, the y coordinate of the point
	 * @return true
	 */
	public boolean contains(int x, int y){
		RnaShape shape = rnastruct.getShape(new Point2D.Double(x,y),zoomfactor);
		if(motifhead != null && motifhead.contains(x,y)){
			setToolTipText("Click to specify global search options");
		}
		else if(shape != null){
			setToolTipText(shape.getToolTipText());
		}
		else{
			setToolTipText(null);
		}
		return true;
	}

	/**
	 * Method for writing the graphics to an image file instead of the screen
	 * 
	 * @param imagefile, the File of the image
	 * @return BufferedImage holding the Graphics2D output
	 */
	public BufferedImage saveImage(File imagefile){
		Dimension dim = getSize();
		BufferedImage bi = new BufferedImage((int)dim.getWidth(),(int)dim.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bi.createGraphics();

		g2.setPaint(Color.white);
		g2.fillRect(0,0,(int)dim.getWidth(),(int)dim.getHeight());

		AffineTransform zoom = new AffineTransform();
		zoom.setToScale(zoomfactor,zoomfactor);
		g2.transform(zoom);
		if(sselement != null){
			sselement.showCurrentLine(g2);
		}
		rnastruct.drawStructure(g2,fixed);

		if(selectedshape != null){
			AffineTransform at = selectedshape.getTransform();
			g2.transform(at);
			Color transparentgray = new Color(204,204,204,150);
			g2.setPaint(transparentgray);
			g2.fill(selectedshape.getArea());
			g2.setPaint(new Color(153,0,0));
			g2.setStroke(new BasicStroke(2.0f));
			g2.draw(selectedshape.getArea());
		}

		return bi;

	}

	/**
	 * This method contains calls to write the graphics to an SVG file instead of the screen
	 * 
	 * @param svgfile, the File to store the svg output
	 * @return File same as parameter after written into file
	 */
	public File saveSVG(File svgfile){
		DOMImplementation domImpl;
		Document document;
		SVGGraphics2D sg2;
		FileOutputStream fos;
		Writer out;


		domImpl = GenericDOMImplementation.getDOMImplementation();
		document = domImpl.createDocument(null, "svg", null);
		sg2 = new SVGGraphics2D(document);
		Dimension dim = getSize();

		sg2.setSVGCanvasSize(dim);
		sg2.setPaint(Color.white);
		sg2.fillRect(0,0,(int)dim.getWidth(),(int)dim.getHeight());

		AffineTransform zoom = new AffineTransform();
		zoom.setToScale(zoomfactor,zoomfactor);
		sg2.transform(zoom);
		if(sselement != null){
			sselement.showCurrentLine(sg2);
		}
		rnastruct.drawStructure(sg2,fixed);

		if(selectedshape != null){
			AffineTransform at = selectedshape.getTransform();
			sg2.transform(at);
			Color transparentgray = new Color(204,204,204,150);
			sg2.setPaint(transparentgray);
			sg2.fill(selectedshape.getArea());
			sg2.setPaint(new Color(153,0,0));
			sg2.setStroke(new BasicStroke(2.0f));
			sg2.draw(selectedshape.getArea());
		}

		try{
			fos = new FileOutputStream(svgfile);
			out = new OutputStreamWriter(fos, "UTF-8");
			sg2.stream(out, false);
			out.close();
			fos.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return svgfile; 
	}

	/**
	 * Main drawing method of the DrawingSurface.
	 * Shows both the RnaStructure and if present the current element
	 * This method is called whenever the mouse is moved
	 *
	 * @param g the Graphics object that performs the drawing
	 */
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;

		Dimension dim = getSize();

		g2.setPaint(Color.white);
		g2.fillRect(0,0,(int)dim.getWidth(),(int)dim.getHeight());

		//enlarge the viewing port
		if(rnastruct.getWidth() > (int)(this.getSize()).getWidth()){
			setPreferredSize(new Dimension(rnastruct.getWidth(),(int)(this.getSize()).getHeight()));
			eg.adjustScrollPane(this);
		}
		//enlarge the viewing port
		if(rnastruct.getHeight() > (int)(this.getSize()).getHeight()){
			setPreferredSize(new Dimension((int)(this.getSize()).getWidth(),rnastruct.getHeight()));
			eg.adjustScrollPane(this);
		}

		AffineTransform oldat = g2.getTransform();
		AffineTransform zoom = new AffineTransform();
		zoom.setToScale(zoomfactor,zoomfactor);
		g2.transform(zoom);
		if(sselement != null){
			sselement.showCurrentLine(g2);
		}
		rnastruct.drawStructure(g2,fixed);

		if(selectedshape != null){
			AffineTransform at = selectedshape.getTransform();
			g2.transform(at);
			Color transparentgray = new Color(204,204,204,150);
			g2.setPaint(transparentgray);
			g2.fill(selectedshape.getArea());
			g2.setPaint(new Color(153,0,0));
			g2.setStroke(new BasicStroke(2.0f));
			g2.draw(selectedshape.getArea());
		}

		if(mousecontained){
			switch(elementtype){
			case 0: break;
			case EditorGui.SINGLE_TYPE: 
				if(sselement != null){ break;}
			default: element.show(g2,false);
			break;
			}
		}
		g2.setTransform(oldat);

	}
}
