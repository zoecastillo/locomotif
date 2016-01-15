package rnaeditor;
/*
 * Created on 21.03.2007
 *
 */


/**
 * @author Janina
 *
 */
public class SizeConflictException extends Exception {

	private static final long serialVersionUID = -1870403848053068412L;

	/**
	 * Default constructor
	 */
	public SizeConflictException() {
		super();
	}

	/**
	 * Default constructure with parameter
	 * @param arg0
	 */
	public SizeConflictException(String arg0) {
		super(arg0);
	}

	/**
	 * Default constructure
	 * @param arg0
	 * @param arg1
	 */
	public SizeConflictException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Default constructor
	 * @param arg0
	 */
	public SizeConflictException(Throwable arg0) {
		super(arg0);
	}

}
