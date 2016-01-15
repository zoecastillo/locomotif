package rnaeditor;
/*
 * Created on 12.11.2005
 *
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;


/**
 * @author Janina
 *
 */
public class RunPanel extends JFrame implements ActionListener, CaretListener{
	
	private static final long serialVersionUID = -2363731830879624005L;
	
	private JPanel mainpanel, sequencepanel, midpanel, emailpanel, buttonpanel;
	private JPanel sequploadpanel, midchooserpanel, middirectpanel, miduploadpanel;
	private JPanel slp1,slp2,mlp1,mlp2,elp1,elp2,elp3;
	private JPanel pane;
	private JButton runbutton, cancelbutton, browseseqbutton, browsemidbutton;
	private JTextArea textarea;
	private JLabel seql1, seql2, midl1, midname, midl2, midl3, el1,el2;
	private JTextField seqtf, midtf, midtf2, etf;
	private JComboBox midcombobox;
	private JScrollPane scrollpane;
	
	final JFileChooser fc = new JFileChooser();
	private File seqfile;
	private File matcherfile;
	private String matcherid;
	private String[] ids;
	private String[] names;
	
	private EditorGui eg;
	
	
	/**
	 * Constructor
	 */
	public RunPanel(Vector<String[]> obtainedids, EditorGui eg){
		
		this.eg = eg;
		
		//nice window decorations
		setDefaultLookAndFeelDecorated(true);
		
		setTitle("Locomotif: Run Matcher");
		setSize(new Dimension(600,450));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
		setContentPane(this.makeContentPane(obtainedids));
		pack();
		setVisible(true);
	}
	
	
	/**
	 * This method builds the content pane of the program
	 *
	 * @return the contentpane as a Container
	 */
	public Container makeContentPane(Vector<String[]> obtainedids){

		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);	

		//the overall panel
		pane = new JPanel();
		pane.setLayout(new BorderLayout());

		//contains the ok and save buttons
		buttonpanel = new JPanel();

		//contains the sequence input fields
		sequencepanel = new JPanel();

		//contains the mid input fields
		midpanel = new JPanel();
		
		//contains the email input field
		emailpanel = new JPanel();
		
		//contains the input panels
		mainpanel = new JPanel();
		mainpanel.setPreferredSize(new Dimension(600,450));
		mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.PAGE_AXIS));

		//setting up the panel for the sequence input
		sequencepanel.setLayout(new BoxLayout(sequencepanel, BoxLayout.PAGE_AXIS));
		sequencepanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),BorderFactory.createEmptyBorder(5,5,5,5)));
		this.setUpSequencePanel();

		//setting up the panel for the mid input
		midpanel.setLayout(new BoxLayout(midpanel, BoxLayout.PAGE_AXIS));
		midpanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),BorderFactory.createEmptyBorder(5,5,5,5)));
		this.setUpMidPanel(obtainedids);

		//setting up the panel for the email-address (optional)
		emailpanel.setLayout(new BoxLayout(emailpanel, BoxLayout.PAGE_AXIS));
		emailpanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),BorderFactory.createEmptyBorder(5,5,5,5)));
		this.setUpEmailPanel();
		
		//creating the main panel
		mainpanel.add(sequencepanel);
		mainpanel.add(Box.createRigidArea(new Dimension(0, 1)));
		mainpanel.add(midpanel);
		mainpanel.add(Box.createRigidArea(new Dimension(0, 1)));
		mainpanel.add(emailpanel);

		//setting up the panel for the buttons
		buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.LINE_AXIS));
		buttonpanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),BorderFactory.createEmptyBorder(5,5,5,5)));
		this.setUpButtonPanel();
		
		
		//putting everything together
		pane.add(mainpanel, BorderLayout.CENTER);
		pane.add(buttonpanel, BorderLayout.PAGE_END);
		
		return pane;
	}
	
	/**
	 * Building method for the sequence input panel
	 *
	 */
	public void setUpSequencePanel(){
		seql1 = new JLabel("Upload your sequence in plain text or FASTA format");
		seql1.setToolTipText("<html>Multiple FASTA files are possible;<br /> multiple sequences in plain text format are not supported</html>");
		slp1 = new JPanel();
		slp1.setLayout(new BoxLayout(slp1, BoxLayout.LINE_AXIS));
		slp1.add(seql1);
		slp1.add(Box.createHorizontalGlue());
		sequencepanel.add(slp1);
		sequencepanel.add(Box.createRigidArea(new Dimension(0,5)));
		
		seqtf = new JTextField(30);
		seqtf.setMaximumSize(seqtf.getPreferredSize());
		browseseqbutton = new JButton("Browse");
		browseseqbutton.addActionListener(this);
		sequploadpanel = new JPanel();
		sequploadpanel.setLayout(new BoxLayout(sequploadpanel, BoxLayout.LINE_AXIS));
		sequploadpanel.add(seqtf);
		sequploadpanel.add(Box.createRigidArea(new Dimension(10,0)));
		sequploadpanel.add(browseseqbutton);
		sequploadpanel.add(Box.createHorizontalGlue());
		sequencepanel.add(sequploadpanel);
		sequencepanel.add(Box.createRigidArea(new Dimension(0,15)));
		
		seql2 = new JLabel("or copy/paste your sequence here (plain text or FASTA format)");
		seql2.setToolTipText("<html>Multiple FASTA files are possible;<br /> multiple sequences in plain text format are not supported</html>");
		slp2 = new JPanel();
		slp2.setLayout(new BoxLayout(slp2, BoxLayout.LINE_AXIS));
		slp2.add(seql2);
		slp2.add(Box.createHorizontalGlue());
		sequencepanel.add(slp2);
		sequencepanel.add(Box.createRigidArea(new Dimension(0,5)));

		textarea = new JTextArea(5,40);
		scrollpane = new JScrollPane(textarea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollpane.setPreferredSize(textarea.getPreferredSize());
		scrollpane.setViewportBorder(BorderFactory.createLineBorder(Color.black));
		sequencepanel.add(scrollpane);
	}
	
	/**
	 * Building panel for the matcher id input panel
	 * @param obtainedids, the ids obtained in the current use of the editor
	 */
	public void setUpMidPanel(Vector<String[]> obtainedids){
		
		if(obtainedids == null){
			names = new String[]{"No ids stored"};
			ids = new String[]{"Locomotif ids"};
		}
		else{
			names = new String[obtainedids.size()];
			ids = new String[obtainedids.size()];
			int i = 0;
			for( String[] sa : obtainedids){
				names[i] = sa[0];
				ids[i] = sa[1];
				i++;
			}
			matcherid = ids[obtainedids.size()-1];
		}
		
		midl1 = new JLabel("Choose the Locomotif matcher you wish to run");
		mlp1 = new JPanel();
		mlp1.setLayout(new BoxLayout(mlp1, BoxLayout.LINE_AXIS));
		mlp1.add(midl1);
		mlp1.add(Box.createHorizontalGlue());
		midpanel.add(mlp1);
		midpanel.add(Box.createRigidArea(new Dimension(0,5)));

		
		midname = new JLabel(names[names.length-1]);
		midcombobox = new JComboBox(ids);
		midcombobox.setSelectedIndex(ids.length-1);
		midcombobox.addActionListener(this);
		midcombobox.setMaximumSize(new Dimension(50,20));
		midcombobox.setEditable(true);
		midchooserpanel = new JPanel();
		midchooserpanel.setLayout(new BoxLayout(midchooserpanel, BoxLayout.LINE_AXIS));
		midchooserpanel.add(midname);
		midchooserpanel.add(Box.createRigidArea(new Dimension(15,0)));
		midchooserpanel.add(Box.createHorizontalGlue());
		midchooserpanel.add(midcombobox);
		midpanel.add(midchooserpanel);

		midpanel.add(Box.createRigidArea(new Dimension(0,15)));
		
		midl2 = new JLabel("or upload a Locomotif matcher id file");
		midl2.setToolTipText("You can load a matcher id from file if you previously stored it");
		mlp2 = new JPanel();
		mlp2.setLayout(new BoxLayout(mlp2, BoxLayout.LINE_AXIS));
		mlp2.add(midl2);
		mlp2.add(Box.createHorizontalGlue());
		midpanel.add(mlp2);
		midpanel.add(Box.createRigidArea(new Dimension(0,5)));
		
		midtf = new JTextField(30);
		midtf.setMaximumSize(midtf.getPreferredSize());
		browsemidbutton = new JButton("Browse");
		browsemidbutton.addActionListener(this);
		miduploadpanel = new JPanel();
		miduploadpanel.setLayout(new BoxLayout(miduploadpanel, BoxLayout.LINE_AXIS));
		miduploadpanel.add(midtf);
		miduploadpanel.add(Box.createRigidArea(new Dimension(10,0)));
		miduploadpanel.add(browsemidbutton);
		miduploadpanel.add(Box.createHorizontalGlue());
		midpanel.add(miduploadpanel);	

		midl3 = new JLabel("or enter matcher id: ");
		midtf2 = new JTextField(30);
		midtf2.setMaximumSize(midtf2.getPreferredSize());
		midtf2.addCaretListener(this);
		middirectpanel = new JPanel();
		middirectpanel.setLayout(new BoxLayout(middirectpanel, BoxLayout.LINE_AXIS));
		middirectpanel.add(midl3);
		middirectpanel.add(Box.createRigidArea(new Dimension(10,0)));
		middirectpanel.add(midtf2);
		middirectpanel.add(Box.createHorizontalGlue());

		midpanel.add(Box.createRigidArea(new Dimension(0,10)));
		//midpanel.add(middirectpanel);	

	}
		
	/**
	 * Building method for the email input panel
	 *
	 */
	public void setUpEmailPanel(){
		el1 = new JLabel("This tool supports email notification, when your computation is done.");
		el2 = new JLabel("If you would like to be notified, enter your email adress here (optional):");
		elp1 = new JPanel();
		elp1.setLayout(new BoxLayout(elp1,BoxLayout.LINE_AXIS));
		elp1.add(el1);
		elp1.add(Box.createHorizontalGlue());
		elp2 = new JPanel();
		elp2.setLayout(new BoxLayout(elp2,BoxLayout.LINE_AXIS));
		elp2.add(el2);
		elp2.add(Box.createHorizontalGlue());
		
		etf = new JTextField(30);
		etf.setMaximumSize(etf.getPreferredSize());
		etf.setMinimumSize(etf.getPreferredSize());
		elp3 = new JPanel();
		elp3.setLayout(new BoxLayout(elp3,BoxLayout.LINE_AXIS));
		elp3.add(etf);
		elp3.add(Box.createHorizontalGlue());
		
		emailpanel.add(elp1);
		emailpanel.add(elp2);
		emailpanel.add(Box.createRigidArea(new Dimension(0,5)));
		emailpanel.add(elp3);
	}

	
	/**
	 * This method sets up the shape field for presenting the online
	 * translation into shape notation
	 *
	 * @param shapepanel the panel holding the shape notation
	 */
	public void setUpButtonPanel(){
		runbutton = new JButton("Run");
		cancelbutton = new JButton("Cancel");
		runbutton.setEnabled(true);
		runbutton.setSelected(true);
		runbutton.addActionListener(this);
		cancelbutton.setEnabled(true);
		cancelbutton.addActionListener(this);
		buttonpanel.add(Box.createHorizontalGlue());
		buttonpanel.add(runbutton);
		buttonpanel.add(cancelbutton);
	}
	

	
	/**
	 * ActionListener
	 */
	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();
		if(src == runbutton){
			if(matcherid == null || matcherid.equals("")){
				JOptionPane.showMessageDialog(this,"Your must specify a Locomotif matcher id!","No matcher available",JOptionPane.ERROR_MESSAGE);
				return;
			}
			//a file was specified
			String sequence = null;
			if(seqtf != null && !(seqtf.getText()).equals("")){
				if(!(textarea == null || (textarea.getText()).equals(""))){
					JOptionPane.showMessageDialog(this,"Please specify either a file to upload or copy/paste your sequence!","Duplicate Sequence Input",JOptionPane.ERROR_MESSAGE);
					return;
				}
				try{
					sequence = EditorIO.parseSeqFile(seqtf.getText());
				}
				catch(IOException ioe){
					JOptionPane.showMessageDialog(this,"Sequence could not be read from file:\n"+ioe.getMessage(),"Error in File IO",JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			else{
				if(textarea == null || (textarea.getText()).equals("")){
					JOptionPane.showMessageDialog(this,"Please specify either a file to upload or copy/paste your sequence!","Duplicate Sequence Input",JOptionPane.ERROR_MESSAGE);
					return;
				}
				sequence = textarea.getText();
			}
			String email = etf.getText();
			if(email != null){
				if(email.equals("")){
					email = null;
				}
				else{
					Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
					Matcher m = p.matcher(email);
					boolean matchFound = m.matches();
					if (!matchFound){
						JOptionPane.showMessageDialog(this, "The email address you specified is not valid!\nPlease specify a valid address or use without notification.","Invalid email address",JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
			sequence = Iupac.parseSequence(sequence);
			if(!sequence.startsWith(">")){
				JOptionPane.showMessageDialog(this,sequence,"Wrong sequence format",JOptionPane.ERROR_MESSAGE);
				return;
			}
			eg.runMatcher(email,sequence,matcherid);
			dispose();
		}
		else if(src == cancelbutton){
			dispose();
		}
		else if(src == browseseqbutton){
			int returnVal = fc.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				seqfile = fc.getSelectedFile();
				seqtf.setText(seqfile.getPath());
			}
		}
		else if(src == browsemidbutton){
			int returnVal = fc.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				matcherfile = fc.getSelectedFile();
				String idfromfile = null;
				try{
					idfromfile = EditorIO.getIDFromFile(matcherfile);
					midtf.setText(matcherfile.getName()+": "+idfromfile);
					midname.setText("");
					midcombobox.addItem("Locomotif ID:");
					midcombobox.setSelectedItem("Locomotif ID:");
					matcherid = idfromfile;
				}
				catch(IOException ioe){
					JOptionPane.showMessageDialog(this,"No Locomotif ID could be obtained from the file you specified:\n"+ioe.getMessage());
				}
			}
		}
		else if(src == midcombobox){
			if((midcombobox.getSelectedItem()).equals("Locomotif ID:")){
				return;
			}
			int index = midcombobox.getSelectedIndex();
			midname.setText(names[index]);
			midtf.setText(null);
			midcombobox.removeItem("Locomotif ID:");
			matcherid = (String)midcombobox.getSelectedItem();
		}
	}
	
	/**
	 * Listener for text input fields
	 *
	 * @param ce the CaretEvent which ocurred
	 */
	public void caretUpdate(CaretEvent ce){
	    matcherid = midtf2.getText();
	}
	
	
	
}
