package rnaeditor;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/**
 * Class performing any file io operations
 *
 * @author Janina Reeder
 */
public class EditorIO{
	
		
	/**
	 * Method to open a file using the given JFileChooser
	 *
	 * @param eg, the parent GUI
	 * @param fc, the JFileChooser to choose the file with
	 * @return the given FILE
	 */
	public static File openFile(EditorGui eg,JFileChooser fc){
		int returnVal = fc.showOpenDialog(eg);
		if(returnVal == JFileChooser.APPROVE_OPTION){
			File rnafile = fc.getSelectedFile();
			return rnafile;
		}
		return null;
	}
	
	/**
	 * Method to save to a file using the given JFileChooser
	 *
	 * @param eg, the parent GUI
	 * @param fc, the JFileChooser to choose the file with
	 * @param rnafile 
	 * @return the given FILE
	 */
	public static File saveFile(EditorGui eg, File rnafile, JFileChooser fc){
		if(rnafile != null){
			fc.setSelectedFile(rnafile);
		}
		int returnVal = fc.showSaveDialog(eg);
		if(returnVal == JFileChooser.APPROVE_OPTION){
			rnafile = fc.getSelectedFile();
			String rnafilename = rnafile.getPath();
			if(!rnafilename.endsWith(".rna")){
				rnafilename = rnafilename.concat(".rna");
				rnafile = new File(rnafilename);
			}
			return rnafile;
		}
		return null;
	}
	
	/**
	 * Method to save to a file using the given JFileChooser
	 *
	 * @param eg, the parent GUI
	 * @param fc, the JFileChooser to choose the file with 
	 * @return the given FILE
	 */
	public static File saveImage(EditorGui eg, JFileChooser fc){
		int returnVal = fc.showSaveDialog(eg);
		if(returnVal == JFileChooser.APPROVE_OPTION){
			File imagefile = fc.getSelectedFile();
			String imagefilename = imagefile.getPath();
			String extension = (fc.getFileFilter()).getDescription();
			if(!imagefilename.endsWith(extension)){
				imagefilename = imagefilename.concat("."+extension);
				imagefile = new File(imagefilename);
			}
			return imagefile;
		}
		return null;
	}
	
	/**
	 * This method performs the IO operations for writing a given BufferedImage to the specified file
	 * 
	 * @param bi, the BufferedImage to be stored
	 * @param ext, the extension, i.e. the image format
	 * @param imagefile, the File where the image is to be stored
	 * @param eg, reference back to the EditorGui for showing an error message if needed
	 */
	public static void storeImage(BufferedImage bi, String ext, File imagefile, EditorGui eg){
		try{
			ImageIO.write(bi, ext, imagefile);
		}
		catch (IOException ioe){
			JOptionPane.showMessageDialog(eg,"Image could not be stored","IOException",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Method to save to a file using the given JFileChooser
	 *
	 * @param op, the parent GUI
	 * @param fc, the JFileChooser to choose the file with
	 * @param rnafile 
	 * @return the given FILE
	 */
	public static String saveOutputFile(OutputPresenter op, File outputfile, JFileChooser fc, String text) throws IOException{
		if(outputfile != null){
			fc.setSelectedFile(outputfile);
		}
		int returnVal = fc.showSaveDialog(op);
		if(returnVal == JFileChooser.APPROVE_OPTION){
			outputfile = fc.getSelectedFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputfile));
			bw.write(text);
			bw.flush();
			return outputfile.getPath();
		}
		return null;
	}
	
		/**
	 * Method to save to a file using the given JFileChooser
	 *
	 * @param op, the parent GUI
	 * @param fc, the JFileChooser to choose the file with
	 * @param rnafile 
	 * @return the given FILE
	 */
	public static String saveOutputFile(ResultPresenter rp, File outputfile, JFileChooser fc, String text) throws IOException{
		if(outputfile != null){
			fc.setSelectedFile(outputfile);
		}
		int returnVal = fc.showSaveDialog(rp);
		if(returnVal == JFileChooser.APPROVE_OPTION){
			outputfile = fc.getSelectedFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputfile));
			bw.write(text);
			bw.flush();
			return outputfile.getPath();
		}
		return null;
	}

	/**
	 * Method to save to a file using the given JFileChooser
	 *
	 * @param tp, the parent GUI
	 * @param fc, the JFileChooser to choose the file with
	 * @param rnafile 
	 * @return the given FILE
	 */
	public static void saveOutputFile(TranslatePresenter tp, File adpfile, JFileChooser fc, String geruest, String text) throws IOException{
		int returnVal = fc.showSaveDialog(tp);
		if(returnVal == JFileChooser.APPROVE_OPTION){
			adpfile = fc.getSelectedFile();
			String adpfilename = adpfile.getPath();
			if(!adpfilename.endsWith(".lhs")){
				adpfilename = adpfilename.concat(".lhs");
				adpfile = new File(adpfilename);
				BufferedWriter bw = new BufferedWriter(new FileWriter(adpfile));
				bw.write(geruest);
				bw.write(text);
				bw.flush();
			}
		}
	}
	
	
	
	/**
	 * This method opens the Object Input Stream from which to read
	 * 
	 * @param rnafile, the File that the stream should lead into
	 * @return the ObjectInputStream
	 */
	public static ObjectInputStream openObjectInputStream(File rnafile){
		try{
			FileInputStream in = new FileInputStream(rnafile);
			return new ObjectInputStream(in);
		}
		catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		return null;
	}
	
	/**
	 * This method opens the ObjectOutputStream on which to write
	 * 
	 * @param rnafile, the File the Stream leads into
	 * @return the ObjectOutputStream
	 */
	public static ObjectOutputStream openObjectOutputStream(File rnafile){
		try{
			FileOutputStream out = new FileOutputStream(rnafile);
			return new ObjectOutputStream(out);
		}
		catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Method for storing the id received from the BibiServ
	 * 
	 * @param structname, current project name
	 * @param id, the BiBiServ ID
	 * @throws IOException
	 */
	public static void storeID(String structname, String id) throws IOException{
		File idfile = new File(structname+".lid");
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFile(idfile);
		int returnVal = fc.showSaveDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION){
			idfile = fc.getSelectedFile();
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(idfile));
		bw.write(id);
		bw.flush();
	}
	
	/**
	 * Method for reading a BiBiServ id from a file
	 * 
	 * @param idfile, the File containing the id
	 * @return the id in String format
	 * @throws IOException
	 */
	public static String getIDFromFile(File idfile) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(idfile));
		return br.readLine();
	}
	
	/**
	 * Method for loading a DNA/RNA sequence from file
	 * 
	 * @param pathname, the path of the File containing the target sequence
	 * @return the target sequence in String format
	 * @throws IOException
	 */
	public static String parseSeqFile(String pathname) throws IOException{
		String sequence = "";
		String buf;
		File seqfile = new File(pathname);
		BufferedReader br = new BufferedReader(new FileReader(seqfile));
		while((buf = br.readLine()) != null){
			sequence += buf;
			sequence += "\n";
		}
		return sequence;
	}
	
	
}
