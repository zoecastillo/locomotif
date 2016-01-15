package rnaeditor;
/**
 * The Basepair class is used to store a basepair.
 * 
 * @author Janina Reeder
 */

import java.io.Serializable;

/**
 * 
 * @author Janina Reeder
 *
 * This class defines a basepair which is used to store sequence information
 */
public class Basepair implements Serializable{
	
	private static final long serialVersionUID = -6506772089636036187L;
	/**
	 * firstinpair is the base on the 5' strand
	 */
	private char firstinpair;
	/**
	 * secondinpair is the base on the 3' strand
	 */
	private char secondinpair;
	
	/**
	 * Constructor for a Basepair
	 * @param f, the first letter of the basepair (5')
	 * @param s, the second letter of the basepair (3')
	 */
	public Basepair(char f, char s){
		this.firstinpair = f;
		this.secondinpair = s;
	}
	
	/**
	 * Sets the first base
	 * @param f, the letter of the first base
	 */
	public void setFirst(char f){
		this.firstinpair = f;
	}
	
	/**
	 * Sets the second base
	 * @param s, the letter of the second base
	 */
	public void setSecond(char s){
		this.secondinpair = s;
	}
	
	/**
	 * Sets the Basepair
	 * @param f, the letter of the first base
	 * @param s, the letter of the second base
	 */
	public void setPair(char f, char s){
		this.firstinpair = f;
		this.secondinpair = s;
	}
	
	/**
	 * 
	 * @return the letter of the first base
	 */
	public char getFirst(){
		return this.firstinpair;
	}
	
	/**
	 * 
	 * @return the letter of the second base
	 */
	public char getSecond(){
		return this.secondinpair;
	}
	
	/**
	 * Transforms the Basepair into a String
	 * @return the Basepair-String
	 */
	public String getString(){
		char[] bpchars = new char[2];
		bpchars[0] = this.firstinpair;
		bpchars[1] = this.secondinpair;
		
		return new String(bpchars);
	}
	
	/**
	 * This method exchanges the first and the second letter of the basepair
	 *
	 */
	public void switchSides(){
		char buf = this.firstinpair;
		this.firstinpair = this.secondinpair;
		this.secondinpair = buf;
	}
	
		
}
