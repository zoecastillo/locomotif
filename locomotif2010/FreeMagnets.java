package rnaeditor;

import java.awt.geom.Ellipse2D;
import java.io.Serializable;
import java.util.Vector;

/**
 * This class provides methods for storing and managing the magnets of a
 * rna structure
 *
 * @author Janina Reeder
 */
public class FreeMagnets implements Serializable{
	
	private static final long serialVersionUID = 2110872779464299982L;
	//Vector data structure storing all available magnets
	private Vector<Magnet> magnets;
	
	/**
	 * Constructor which initializes the internal storage (a Magnet Vector)
	 */
	public FreeMagnets(){
		magnets = new Vector<Magnet>();
	}
	
	/**
	 * removes the given Magnet from the storage vector
	 *
	 * @param cmp, the Magnet to be removed
	 * @return null or the other half of the full Magnet, if cmp is only a single strand Magnet
	 */
	public Magnet removeSS(Magnet cmp){
		Magnet rest = null;
		for(Magnet m : magnets){
			if(m.equalsWithLine(cmp)){
				magnets.remove(m);
				break;
			}
			else if(m.contains(cmp)){
				rest = m.getRest(cmp);
				magnets.add(rest);
				magnets.remove(m);
				break;
			}
		}
		return rest;
	}
	
	/**
	 * Adjusts a Magnet with an attached single strand according to a size or location change.
	 * While full Magnets are given, only the appropriate available half is stored!
	 * 
	 * @param cmp, the original location of the full Magnet
	 * @param full, the new location of the full Magnet
	 */
	public void adjustMagnet(Magnet cmp, Magnet full){
		Magnet cmp1 = cmp.getFirstHalf();
		Magnet cmp2 = cmp.getSecondHalf();
		for (Magnet m : magnets){ //looking for the Magnet
			if(cmp1.contains(m)){ //only the first half is available
				Magnet half = full.getFirstHalf();
				half.setIsFull(false);
				magnets.add(half);
				magnets.remove(m);
				break;
			}
			else if(cmp2.contains(m)){ //only the second half is available
				Magnet half = full.getSecondHalf();
				half.setIsFull(false);
				magnets.add(half);
				magnets.remove(m);
				break;
			}
		}
	}
	
	
	/**
	 * removes the given Magnet from the storage vector
	 *
	 * @param cmp, the Magnet to be removed
	 */
	public void remove(Magnet cmp){
		for(Magnet m : magnets){
			if(m.equals(cmp)){
				magnets.remove(m);
				return;
			}
		}
	}
	
	/**
	 * Removes any Magnet belonging to the given RnaShape from the set of
	 * free magnets
	 *
	 * @param s, the RnaShape whose magnets are to be removed
	 */
	public void remove(RnaShape s){
		Vector<Magnet> buffer = new Vector<Magnet>();
		for(Magnet m : magnets){
			if(m.getParent() == s){
				buffer.add(m);
			}
		}
		magnets.removeAll(buffer);
	}
	
	/**
	 * This method checks wether the given Magnet is contained in this 
	 * FreeMagnets object
	 *
	 * @param cmp the Magnet to look for
	 * @return true, if contained; else false.
	 */
	public boolean contains(Magnet cmp){
		for(Magnet m : magnets){
			if(m.contains(cmp)){
				return true;
			}
		}
		return false;
	}
	

	
	/**
	 * Checks wether the set of free magnets contains a standard magnet
	 * 
	 * @return true, if a standard magnet is available, else false.
	 */
	public boolean containsStandard(){
		for (Magnet m : magnets){
			RnaShape s = m.getParent();
			if(s instanceof StemShape || s instanceof InternalShape || s instanceof BulgeShape || s instanceof MultiShape || s instanceof HairpinShape){
				return true;
			}
		}
		return false;
	}
	
	
	
	/**
	 * This method gives the number of magnets currently stored.
	 * 
	 * @return the number of magnets in storage
	 */
	public int getNumberOfMagnets(){
		return magnets.size();
	}
	
	/**
	 * Counts the number of full magnets in the set of free magnets
	 *
	 * @return the number of full magnets
	 */
	public int getNumberOfFullMagnets(){
		int num = 0;
		for(Magnet m : magnets){
			if(m.getIsFull()){
				num++;
			}
		}
		return num;
	} 
	
	/**
	 * Counts the number of magnets belonging to Pseudoknots in the set of free magnets
	 * 
	 * @return the number of pseudoknot magnets
	 */
	public int getNumberOfPseudoMagnets(){
	    int num = 0;
	    for(Magnet m : magnets){
	        if((m.getParent()).getType() == EditorGui.PSEUDO_TYPE){
	            num++;
	        }
	    }
	    return num;
	}
	
	/**
	 * Counts the number of full magnets in the set of free magnets
	 *
	 * @param ms, the MultiShape that cannot be the parent of the magnet in question
	 * @return the number of full magnets
	 */
	public int getNumberOfFullMagnets(MultiShape ms){
		int num = 0;
		for(Magnet m : magnets){
			if(m.getIsFull() && m.getParent() != ms){
				num++;
			}
		}
		return num;
	} 
	
	/**
	 * Counts the number of half magnets in the set of free magnets
	 *
	 * @return the number of half magnets
	 */
	public int getNumberOfSingleMagnets(){
		int num = 0;
		for(Magnet m : magnets){
			if(!m.getIsFull()){
				num++;
			}
		}
		return num;
	}
	
	/**
	 * Adds the given Magnet to the storage vector
	 * 
	 * @param m, the Magnet to be added
	 */
	public void add(Magnet m){
		magnets.add(m);
	}
	
	/**
	 * Adds a collection of Magnets to the storage vector
	 * 
	 * @param v, the vector containing the collection of magnets
	 */
	public void add(Vector<Magnet> v){
		for(Magnet m : v){
			magnets.add(m);
		}
	}
	
	/**
	 * Checks wether half of a full magnet or a half magnet is in close proximity
	 * of the given coordinates.
	 *
	 * @param double x, the x-coordinate
	 * @param double y, the y-coordinate
	 * @return Magnet, the magnet in range of x and y, or null, if none.
	 */
	public Magnet inRange(double x, double y){
		Ellipse2D.Double shape = new Ellipse2D.Double(x-5,y-5,10,10);
		for(Magnet m : magnets){
			if(m.getIsFull()){            
				Magnet m1 = m.getFirstHalf();
				Magnet m2 = m.getSecondHalf();
				if((shape.getBounds()).intersectsLine(m1.getLine())){
					return m1;
				}
				if((shape.getBounds()).intersectsLine(m2.getLine())){
					return m2;
				}
			}
			else{
				if((shape.getBounds()).intersectsLine(m.getLine())){
					return m;
				}
			}
		}
		return null;
	}
	
	/**
	 * Gives the first Magnet that lies in range of the given shape
	 *
	 * @param shape, the RnaShape the Magnet is to intersect with
	 * @return Magnet, the Magnet that lies in range of the shape
	 */
	public Magnet inRange(RnaShape shape){
		for(Magnet m : magnets){
			if(shape.getType() == EditorGui.SINGLE_TYPE){
				if((m.getParent()).getIsStartElement() || (m.getParent()).getIsEndElement()){
					if(m.getIsFull()){
						Magnet m1 = m.getFirstHalf();
						Magnet m2 = m.getSecondHalf();
						if((shape.getBounds2D()).intersectsLine(m1.getLine())){
							return m1;
						}
						if((shape.getBounds2D()).intersectsLine(m2.getLine())){
							return m2;
						}
					}
					else{
						if((shape.getBounds2D()).intersectsLine(m.getLine())){
							return m;
						}
					}
				}
			}
			else if(shape.getType() == EditorGui.HAIRPIN_TYPE){
				if(m.getIsFull() && m.getIsHairpinAccessible() && (shape.getBounds2D()).intersectsLine(m.getLine())){
					return m;
				}
			}
			else if(shape.getType() == EditorGui.MULTIEND_TYPE){
				if(m.getIsFull() && m.getIsHairpinAccessible() && (m.getParent()).getType() != EditorGui.CLOSED_STRUCT_TYPE && (shape.getBounds2D()).intersectsLine(m.getLine())){
					if(m.getIsClosedAccessible()){
						return m;
					}
				}
			}
			else if(shape.getType() == EditorGui.CLOSED_STRUCT_TYPE){
				if(m.getIsFull() && (m.getParent()).getType() != EditorGui.CLOSED_STRUCT_TYPE && (m.getParent()).getType() != EditorGui.MULTIEND_TYPE && (shape.getBounds2D()).intersectsLine(m.getLine())){
					if(m.getIsClosedAccessible()){
						return m;
					}
				}
			}
			else{
				if(m.getIsFull() && (shape.getBounds2D()).intersectsLine(m.getLine())){
					return m;
				}
			}
		}
		return null;
	}

	/**
	 * This method is used when checking magnets for ambiguity issues. It resets to the default values.
	 *
	 */
	public void resetAmbiguityCheck(){
		for(Magnet m : magnets){
			m.resetAmbiguityCheck();
		}
	}
	
}








