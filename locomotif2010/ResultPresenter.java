package rnaeditor;
/*
 * Created on 12.11.2005
 *
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;


/**
 * @author Janina
 *
 */
public class ResultPresenter extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = -2363731830879624005L;
	
	private JPanel mainpanel, buttonpanel;
	private JScrollPane scrollpane;
	private JButton okbutton, savebutton, moviesbutton;
	private JTextArea textarea;
	private JPanel pane;
	private String content;
		
	private File outputfile;
	final JFileChooser fc = new JFileChooser();
	
	
	
	/**
	 * Constructor
	 */
	public ResultPresenter(String content){
		//nice window decorations
		setDefaultLookAndFeelDecorated(true);
		
		setTitle("Results");
		setSize(new Dimension(550,400));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
		this.content = content;
		setContentPane(this.makeContentPane(content));
		pack();
		setVisible(true);
	}
	
	
	/**
	 * This method builds the content pane of the program
	 *
	 * @return the contentpane as a Container
	 */
	public Container makeContentPane(String content){
		
		pane = new JPanel();
		pane.setLayout(new BorderLayout());
		
		//contains the Result content
		mainpanel = new JPanel();
		mainpanel.setPreferredSize(new Dimension(650,300));
		mainpanel.setLayout(new GridLayout(1,1));
		
		//contains the ok and save buttons
		buttonpanel = new JPanel();
		
		if(content == null || content.equals("")){
			content = "Motif was not found in specified sequence.\n\nYou could use a less refined motif definition to obtain results.\nMake sure that any specified sequence motifs are contained within the sequence!";
		}
		
		//putting contents into the textarea
		textarea = new JTextArea(content);
		textarea.setEnabled(true);
		textarea.setEditable(true);
		textarea.setFont(new Font("Monospaced",Font.PLAIN,12));
		textarea.setCaretPosition(0);
		scrollpane = new JScrollPane(textarea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollpane.setPreferredSize(new Dimension(650,300));
		scrollpane.setViewportBorder(BorderFactory.createLineBorder(Color.black));
		scrollpane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		mainpanel.add(scrollpane);
		pane.add(mainpanel, BorderLayout.CENTER);
		
		//setting up the panel for the buttons
		buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.LINE_AXIS));
		buttonpanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),BorderFactory.createEmptyBorder(5,5,5,5)));
		this.setUpButtonPanel(buttonpanel);
		pane.add(buttonpanel, BorderLayout.PAGE_END);
		
		//initializing the FileChooser
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		return pane;
	}

	/**
	 * Removes any content from the textarea
	 *
	 */
	public void clear(){
		textarea.setText(null);
	}
	
  

	/**
	 * This method sets up the shape field for presenting the online
	 * translation into shape notation
	 *
	 * @param shapepanel the panel holding the shape notation
	 */
	public void setUpButtonPanel(JPanel buttonpanel){
		okbutton = new JButton("OK");
		savebutton = new JButton("Save");
		moviesbutton = new JButton("Visualize");
		okbutton.setEnabled(true);
		okbutton.setSelected(true);
		okbutton.addActionListener(this);
		savebutton.setEnabled(true);
		savebutton.addActionListener(this);
		moviesbutton.setEnabled(true);
		moviesbutton.addActionListener(this);
		buttonpanel.add(Box.createHorizontalGlue());
		buttonpanel.add(okbutton);
		buttonpanel.add(savebutton);
		buttonpanel.add(moviesbutton);
	}
	

	
	/**
	 * ActionListener
	 */
	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();
		if(src == okbutton){
			dispose();
		}
		else if(src == savebutton){
			try{
				String title = EditorIO.saveOutputFile(this,outputfile,fc,textarea.getText());
				setTitle("Locomotif Output: "+title);
			}
			catch(IOException ioe){
				JOptionPane.showMessageDialog(null,"Output could not be saved:\n"+ioe.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(src == moviesbutton){
		    new MoviesFrame(content);
		}
	}
	
}
