package rnaeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;

import org.w3c.dom.Document;





/**
 * Main class that contains all methods necessary to construct
 * the user interface and handle button interactions
 *
 * @author Janina Reeder
 */
public class EditorGui extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = -2013213358883342532L;
	
	//GUI variables
	private static JFrame frame;
	private JPanel menupanel, shapebuttonpanel, drawingpanel, mainpanel, shapepanel;
	private DrawingSurface ds;
	private JScrollPane dspane;
	private JButton buttonstem, buttonhairpin, buttonbulge, buttoninternal, buttonmultiple, buttonsingle, buttonclosed, buttonclosedmulti, buttonpseudo;
	private JButton buttonnew, buttonopen, buttonsave, buttonprint,buttonorient, buttonrotright, buttonrotleft, buttonselect, buttondetach, buttonmove, buttonremove, buttonrestore, buttontext, buttonzoomin, buttonzoomout;
	private JTextField shapefield;
	private JMenuBar menuBar;
	private JMenu menu, submenu;
	private JMenuItem menuNew, menuOpen, menuSave, menuPrint, menuShapes, menuXML, menuMotifInfo, menuADP, menuQuit, menuSend;
	private JMenuItem menuSequence, menuSubmit, menuDownload, menuHelp;
	private JEditorPane submitlabel, downloadlabel, idpane;
	private JPanel pane;
	
	//storing all current ids
	private String id;
	private Vector<String[]> obtainedids;
	
	//File IO variables 
	private File rnafile;
	private String structname;
	private boolean localsearch = true;
	private int motifheaddone = 0;
	final JFileChooser fc = new JFileChooser();
	final JFileChooser adpchooser = new JFileChooser();
	final JFileChooser matcherchooser = new JFileChooser();
	final JFileChooser imagechooser = new JFileChooser();
	SwingWorker runWorker;
	
	//Building block types
	public static final int STEM_TYPE = 1;
	public static final int HAIRPIN_TYPE = 2;
	public static final int BULGE_TYPE = 3;
	public static final int INTERNAL_TYPE = 4;
	public static final int MULTI_TYPE = 5;
	public static final int SINGLE_TYPE = 6;
	public static final int SS_DOUBLE = 7;
	public static final int CLOSED_STRUCT_TYPE = 8;
	public static final int MULTIEND_TYPE = 9;
	public static final int PSEUDO_TYPE = 10;
	
	//Editing operation types
	public static final int SELECT_TYPE = 1;
	public static final int DETACH_TYPE = 2;
	public static final int REMOVE_TYPE = 3;
	public static final int MOVE_TYPE = 4;
	public static final int ZOOM_TYPE = 5;
	
	
	
	/**
	 * This method implements the menu bar of the program
	 *
	 * @return the JMenuBar created in the method
	 */
	public JMenuBar createMenuBar(){
		
		//create the menu bar
		menuBar = new JMenuBar();
		
		//Build the File menu
		menu = new JMenu("File");
		menu.getAccessibleContext().setAccessibleDescription("Organisation");
		
		//a group of JMenuItems
		menuNew = new JMenuItem("New");
		menuNew.setToolTipText("Generate a new project");
		menuNew.addActionListener(this);
		menu.add(menuNew);
		
		menuOpen = new JMenuItem("Open");
		menuOpen.setToolTipText("Open an existing project");
		menuOpen.addActionListener(this);
		menu.add(menuOpen);
		
		menuSave = new JMenuItem("Save");
		menuSave.setToolTipText("Save the current project");
		menuSave.addActionListener(this);
		menu.add(menuSave);
		
		menuPrint = new JMenuItem("Print");
		menuPrint.setToolTipText("Create an image from the current motif definition");
		menuPrint.addActionListener(this);
		menu.add(menuPrint);
		
		
		menu.addSeparator();
		submenu = new JMenu("Export");
		
		menuShapes = new JMenuItem("Shapes");
		menuShapes.addActionListener(this);
		menuShapes.setToolTipText("Translate the structure into a shape string");
		submenu.add(menuShapes);
		menuXML = new JMenuItem("XML");
		menuXML.addActionListener(this);
		menuXML.setToolTipText("Translate the structure into XML code");
		submenu.add(menuXML);
		menuADP = new JMenuItem("ADP");
		menuADP.addActionListener(this);
		menuADP.setToolTipText("Translate the structure into ADP code");
		submenu.add(menuADP);
		menuMotifInfo = new JMenuItem("Motif Info");
		menuMotifInfo.addActionListener(this);
		menuMotifInfo.setToolTipText("Gives an information text on the motif and its contraints.");
		submenu.add(menuMotifInfo);
		menu.add(submenu);
		
		
		menu.addSeparator();
		menuQuit = new JMenuItem("Quit");
		menuQuit.addActionListener(this);
		menuQuit.setToolTipText("Exit the program");
		menu.add(menuQuit);
		
		menuBar.add(menu);
		
		
		menu = new JMenu("Translation");
		menuSend = new JMenuItem("Send XML");
		menuSend.addActionListener(this);
		menuSend.setToolTipText("Send the motif description to the server");
		menu.add(menuSend);
		menuBar.add(menu);
		
		menu = new JMenu("Use Matcher");
		menuSequence = new JMenuItem("Run Now");
		menuSequence.addActionListener(this);
		menuSequence.setToolTipText("Specify an input sequence to run the matcher online via this editor");
		menu.add(menuSequence);
		menuSubmit = new JMenuItem("Submit");
		menuSubmit.addActionListener(this);
		menuSubmit.setToolTipText("Use the web submission form for longer sequences");
		menu.add(menuSubmit);
		menuDownload = new JMenuItem("Download");
		menuDownload.addActionListener(this);
		menuDownload.setToolTipText("Download the C-sources to compile and run the matcher on your system");
		menu.add(menuDownload);
		menuBar.add(menu);
		
		menu = new JMenu("Help");
		menuHelp = new JMenuItem("HowTo");
		menuHelp.setToolTipText("Short manual on how to use Locomotif");
		menuHelp.addActionListener(this);
		submenu.add(menuHelp);
		menu.add(menuHelp);
		menuBar.add(menu);
		
		menu = new JMenu("Locomotif => http://bibiserv.techfak.uni-bielefeld.de/locomotif");
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(menu);
		
		return menuBar;
		
	}
	
	
	/**
	 * This method builds the content pane of the program
	 *
	 * @return the contentpane as a Container
	 */
	public Container makeContentPane(){
		
		pane = new JPanel();
		pane.setLayout(new BorderLayout());
		
		//contains the editing buttons
		menupanel = new JPanel();
		
		//contains the RNA buttons
		shapebuttonpanel = new JPanel();
		pane.add(shapebuttonpanel, BorderLayout.LINE_START);
		
		//contains the DrawingSurface
		drawingpanel = new JPanel();
		drawingpanel.setPreferredSize(new Dimension(700,500));
		drawingpanel.setLayout(new GridLayout(1,1));
		
		//contains the online shape notation
		shapepanel = new JPanel();
		
		//putting contents into the DrawingSurface
		ds = new DrawingSurface(this);
		dspane = new JScrollPane(ds,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		dspane.setPreferredSize(new Dimension(700,500));
		dspane.setViewportBorder(BorderFactory.createLineBorder(Color.black));
		dspane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		drawingpanel.add(dspane);
		
		//contains the subpanels
		mainpanel = new JPanel();
		mainpanel.setPreferredSize(new Dimension(800,500));
		mainpanel.setLayout(new BorderLayout());
		mainpanel.add(menupanel, BorderLayout.PAGE_START);
		mainpanel.add(drawingpanel, BorderLayout.CENTER);
		mainpanel.add(shapepanel, BorderLayout.PAGE_END);
		pane.add(mainpanel, BorderLayout.CENTER);
		
		//setting up the panel for the editing buttons
		menupanel.setLayout(new BoxLayout(menupanel, BoxLayout.X_AXIS));
		menupanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),BorderFactory.createEmptyBorder(5,5,5,5)));
		this.setUpMenuPanel(menupanel);
		
		//setting up the panel for the online shape notation
		shapepanel.setLayout(new GridLayout(1,1));
		shapepanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),BorderFactory.createEmptyBorder(5,5,5,5)));
		this.setUpShapePanel(shapepanel);
		
		//setting up the panel for the shape buttons
		shapebuttonpanel.setLayout(new FlowLayout());
		shapebuttonpanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),BorderFactory.createEmptyBorder(5,5,5,5)));
		shapebuttonpanel.setPreferredSize(new Dimension(80,700));
		shapebuttonpanel.setMinimumSize(new Dimension(50,500));
		this.setUpShapebuttonpanel(shapebuttonpanel);
		
		//initializing the FileChooser
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new RNAFilter());
		fc.setAcceptAllFileFilterUsed(false);
		adpchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		adpchooser.setFileFilter(new ADPFilter());
		adpchooser.setAcceptAllFileFilterUsed(false);
		matcherchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		matcherchooser.setFileFilter(new MatcherFilter());
		matcherchooser.setAcceptAllFileFilterUsed(false);
		imagechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		setImageFilters();
		imagechooser.setAcceptAllFileFilterUsed(false);
		
		
		return pane;
	}
	
	/**
	 * This method is used generate a file filter for image formats supported by
	 * the users system. Additionally, svg output is enabled.
	 *
	 */
	public void setImageFilters(){
		String[] extnames = ImageIO.getWriterFormatNames();
		for(int i=0;i<extnames.length;i++){
			final String currentext = extnames[i];
			imagechooser.setFileFilter(new FileFilter() {
				public boolean accept(File f) {
					return f.getName().toLowerCase().endsWith("."+currentext) || f.isDirectory();
				}
				public String getDescription() {
					return currentext;
				}
			});
		}
		imagechooser.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".svg") || f.isDirectory();
			}
			public String getDescription() {
				return "svg";
			}
		});
	}
	
	/**
	 * Method for adjusting the ScrollPane if the DrawingSurface's is changed
	 *
	 * @param the DrawingSurface of the ScrollPane
	 */
	public void adjustScrollPane(DrawingSurface ds){
		dspane.setViewportView(ds);
		dspane.revalidate();
	}
	
	/**
	 * This method sets up the editing button Panel
	 *
	 * @param the panel which is created
	 */
	public void setUpMenuPanel(JPanel menupanel){
		ImageIcon newicon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/stock_new.png")));
		ImageIcon openicon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/stock_open.png")));
		ImageIcon saveicon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/stock_save.png")));
		ImageIcon printicon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/stock_print.png")));
		
		ImageIcon orienticon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/stock_orient.png")));
		
		ImageIcon righticon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/stock_right.png")));
		ImageIcon lefticon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/stock_left.png")));
		
		ImageIcon selecticon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/stock_select.png")));
		ImageIcon detachicon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/stock_detach.png")));
		ImageIcon removeicon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/stock_remove.png")));
		ImageIcon restoreicon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/stock_restore.png")));
		ImageIcon moveicon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/stock_move.png")));
		
		ImageIcon texticon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/stock_text.png")));
		
		ImageIcon zoominicon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/stock_zoomin.png")));
		ImageIcon zoomouticon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/stock_zoomout.png")));
		
		
		//FileIO plus printing
		buttonnew = new JButton(newicon);
		buttonnew.setAlignmentY(Component.CENTER_ALIGNMENT);
		buttonnew.setMinimumSize(new Dimension(24,20));
		buttonnew.setToolTipText("Create a new project");
		buttonnew.addActionListener(this);
		menupanel.add(buttonnew); 
		
		buttonopen = new JButton(openicon);
		buttonopen.setAlignmentY(Component.CENTER_ALIGNMENT);
		buttonopen.setMinimumSize(new Dimension(24,20));
		buttonopen.setToolTipText("Open an existing project");
		buttonopen.addActionListener(this);
		menupanel.add(buttonopen); 
		
		buttonsave = new JButton(saveicon);
		buttonsave.setAlignmentY(Component.CENTER_ALIGNMENT);
		buttonsave.setMinimumSize(new Dimension(24,20));
		buttonsave.setToolTipText("Save the current project");
		buttonsave.addActionListener(this);
		menupanel.add(buttonsave); 
		 
		buttonrestore = new JButton(restoreicon);
		buttonrestore.setAlignmentY(Component.CENTER_ALIGNMENT);
		buttonrestore.setMinimumSize(new Dimension(24,20));
		buttonrestore.setToolTipText("Restore to a saved version of the project");
		buttonrestore.addActionListener(this);
		menupanel.add(buttonrestore);
		
		menupanel.add(Box.createHorizontalStrut(5));
		menupanel.add(new JSeparator(SwingConstants.VERTICAL));
		menupanel.add(Box.createHorizontalStrut(5));
		
		
		buttonprint = new JButton(printicon);
		buttonprint.setAlignmentY(Component.CENTER_ALIGNMENT);
		buttonprint.setMinimumSize(new Dimension(24,20));
		buttonprint.setToolTipText("Create an image from the current motif defintion");
		buttonprint.addActionListener(this);
		buttonprint.setEnabled(true);
		menupanel.add(buttonprint); 
		
		menupanel.add(Box.createHorizontalStrut(5));
		menupanel.add(new JSeparator(SwingConstants.VERTICAL));
		menupanel.add(Box.createHorizontalStrut(5));
		
		//Orientation
		buttonorient = new JButton(orienticon);
		buttonorient.setAlignmentY(Component.CENTER_ALIGNMENT);
		buttonorient.setMinimumSize(new Dimension(24,20));
		buttonorient.setToolTipText("Exchange 5' and 3' end");
		buttonorient.addActionListener(this);
		menupanel.add(buttonorient);   
		
		menupanel.add(Box.createHorizontalStrut(5));
		menupanel.add(new JSeparator(SwingConstants.VERTICAL));
		menupanel.add(Box.createHorizontalStrut(5));
		
		//Rotation
		buttonrotright = new JButton(righticon);
		buttonrotright.setAlignmentY(Component.CENTER_ALIGNMENT);
		buttonrotright.setMinimumSize(new Dimension(24,20));
		buttonrotright.setToolTipText("Rotate by 45° to the right");
		buttonrotright.addActionListener(this);
		menupanel.add(buttonrotright);    
		
		buttonrotleft = new JButton(lefticon);
		buttonrotleft.setAlignmentY(Component.CENTER_ALIGNMENT);
		buttonrotleft.setMinimumSize(new Dimension(24,20));
		buttonrotleft.setToolTipText("Rotate by 45° to the left");
		buttonrotleft.addActionListener(this);
		menupanel.add(buttonrotleft);    
		
		menupanel.add(Box.createHorizontalStrut(5));
		menupanel.add(new JSeparator(SwingConstants.VERTICAL));
		menupanel.add(Box.createHorizontalStrut(5));
		
		//Transformation
		buttonselect = new JButton(selecticon);
		buttonselect.setAlignmentY(Component.CENTER_ALIGNMENT);
		buttonselect.setMinimumSize(new Dimension(24,20));
		buttonselect.setToolTipText("Select a building block for editing operations");
		buttonselect.addActionListener(this);
		menupanel.add(buttonselect);
		
		buttondetach = new JButton(detachicon);
		buttondetach.setAlignmentY(Component.CENTER_ALIGNMENT);
		buttondetach.setMinimumSize(new Dimension(24,20));
		buttondetach.setToolTipText("Detach a building block from the structure");
		buttondetach.addActionListener(this);
		menupanel.add(buttondetach);
		
		buttonremove = new JButton(removeicon);
		buttonremove.setAlignmentY(Component.CENTER_ALIGNMENT);
		buttonremove.setMinimumSize(new Dimension(24,20));
		buttonremove.setToolTipText("Delete a building block from the structure");
		buttonremove.addActionListener(this);
		menupanel.add(buttonremove);
		
		buttonmove = new JButton(moveicon);
		buttonmove.setAlignmentY(Component.CENTER_ALIGNMENT);
		buttonmove.setMinimumSize(new Dimension(24,20));
		buttonmove.setToolTipText("Move the structure");
		buttonmove.addActionListener(this);
		menupanel.add(buttonmove);
		
		menupanel.add(Box.createHorizontalStrut(5));
		menupanel.add(new JSeparator(SwingConstants.VERTICAL));
		menupanel.add(Box.createHorizontalStrut(5));
		
		//Text: currently not supported!
		buttontext = new JButton(texticon);
		buttontext.setAlignmentY(Component.CENTER_ALIGNMENT);
		buttontext.setMinimumSize(new Dimension(24,20));
		buttontext.setToolTipText("Add comment");
		buttontext.addActionListener(this);
		buttontext.setEnabled(false);
		//menupanel.add(buttontext);
		
		//menupanel.add(Box.createHorizontalStrut(5));
		//menupanel.add(new JSeparator(SwingConstants.VERTICAL));
		//menupanel.add(Box.createHorizontalStrut(5));
		
		//Zoom
		buttonzoomin = new JButton(zoominicon);
		buttonzoomin.setAlignmentY(Component.CENTER_ALIGNMENT);
		buttonzoomin.setMinimumSize(new Dimension(24,20));
		buttonzoomin.setToolTipText("Zoom in");
		buttonzoomin.addActionListener(this);
		menupanel.add(buttonzoomin);
		
		buttonzoomout = new JButton(zoomouticon);
		buttonzoomout.setAlignmentY(Component.CENTER_ALIGNMENT);
		buttonzoomout.setMinimumSize(new Dimension(24,20));
		buttonzoomout.setToolTipText("Zoom out");
		buttonzoomout.addActionListener(this);
		menupanel.add(buttonzoomout);
		
	}
	
	/**
	 * This method sets up the shape field for presenting the online
	 * translation into shape notation
	 *
	 * @param shapepanel the panel holding the shape notation
	 */
	public void setUpShapePanel(JPanel shapepanel){
		shapefield = new JTextField();
		shapefield.setEnabled(false);
		shapefield.setBackground(new Color(250,240,230));
		shapefield.setToolTipText("Translation of the current structure into a shape string");
		shapepanel.add(shapefield);
	}
	
	
	/**
	 * This method sets the text into the shapefield
	 *
	 * @param text the String holding the shape notation
	 */
	public void setShapefieldText(String text){
		shapefield.setText(text);
		shapefield.repaint();
	}
	
	/**
	 * This methods sets up the RNA button panel
	 *
	 * @param the shapebuttonpanel which is created
	 */
	public void setUpShapebuttonpanel(JPanel shapebuttonpanel){
		ImageIcon stem = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/FineStemTiny.jpg")));
		ImageIcon hairpin = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/FineHairpinTiny.jpg")));
		ImageIcon bulge = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/FineBulgeTiny.jpg")));
		ImageIcon internal = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/FineInternalTiny.jpg")));
		ImageIcon multiloop = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/FineMultiTiny.jpg")));
		ImageIcon singlestrand = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/FineSingleTiny.jpg")));
		ImageIcon closedstruct = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/FineClosedTiny.jpg")));
		ImageIcon closedmulti = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/FineMultiClosedTiny.jpg")));
		ImageIcon pseudoicon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/FinePseudoTiny.jpg")));
		
		buttonstem = new JButton(stem);
		buttonstem.setVerticalTextPosition(AbstractButton.BOTTOM);
		buttonstem.setHorizontalTextPosition(AbstractButton.CENTER);
		buttonstem.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonstem.setToolTipText("Stem");
		buttonstem.setMargin(new Insets(4,4,4,4));
		buttonstem.addActionListener(this);
		shapebuttonpanel.add(buttonstem); 
		
		buttonhairpin = new JButton(hairpin);
		buttonhairpin.setVerticalTextPosition(AbstractButton.BOTTOM);
		buttonhairpin.setHorizontalTextPosition(AbstractButton.CENTER);
		buttonhairpin.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonhairpin.setToolTipText("Hairpin Loop");
		buttonhairpin.setMargin(new Insets(4,4,4,4));
		buttonhairpin.addActionListener(this);
		shapebuttonpanel.add(buttonhairpin);   
		
		buttonbulge = new JButton(bulge);
		buttonbulge.setVerticalTextPosition(AbstractButton.BOTTOM);
		buttonbulge.setHorizontalTextPosition(AbstractButton.CENTER);
		buttonbulge.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonbulge.setToolTipText("Bulge Loop");
		buttonbulge.setMargin(new Insets(4,4,4,4));
		buttonbulge.addActionListener(this);
		shapebuttonpanel.add(buttonbulge);    
		
		buttoninternal = new JButton(internal);
		buttoninternal.setVerticalTextPosition(AbstractButton.BOTTOM);
		buttoninternal.setHorizontalTextPosition(AbstractButton.CENTER);
		buttoninternal.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttoninternal.setToolTipText("Internal Loop");
		buttoninternal.setMargin(new Insets(4,4,4,4));
		buttoninternal.addActionListener(this);
		shapebuttonpanel.add(buttoninternal);    
		
		buttonmultiple = new JButton(multiloop);
		buttonmultiple.setVerticalTextPosition(AbstractButton.BOTTOM);
		buttonmultiple.setHorizontalTextPosition(AbstractButton.CENTER);
		buttonmultiple.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonmultiple.setToolTipText("Multiloop");
		buttonmultiple.setMargin(new Insets(4,4,4,4));
		buttonmultiple.addActionListener(this);
		shapebuttonpanel.add(buttonmultiple);  
		
		buttonclosed = new JButton(closedstruct);
		buttonclosed.setVerticalTextPosition(AbstractButton.BOTTOM);
		buttonclosed.setHorizontalTextPosition(AbstractButton.CENTER);
		buttonclosed.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonclosed.setToolTipText("Closed Structure: contains Stems, Bulges and Internal Loops");
		buttonclosed.setMargin(new Insets(4,4,4,4));
		buttonclosed.addActionListener(this);
		shapebuttonpanel.add(buttonclosed);   

		buttonclosedmulti = new JButton(closedmulti);
		buttonclosedmulti.setVerticalTextPosition(AbstractButton.BOTTOM);
		buttonclosedmulti.setHorizontalTextPosition(AbstractButton.CENTER);
		buttonclosedmulti.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonclosedmulti.setToolTipText("Closed End: contains all building block except single strands");
		buttonclosedmulti.setMargin(new Insets(4,4,4,4));
		buttonclosedmulti.addActionListener(this);
		shapebuttonpanel.add(buttonclosedmulti); 
		
		buttonpseudo = new JButton(pseudoicon);
		buttonpseudo.setVerticalTextPosition(AbstractButton.BOTTOM);
		buttonpseudo.setHorizontalTextPosition(AbstractButton.CENTER);
		buttonpseudo.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonpseudo.setToolTipText("Simple Pseudoknot");
		buttonpseudo.setMargin(new Insets(4,4,4,4));
		buttonpseudo.addActionListener(this);
		shapebuttonpanel.add(buttonpseudo);  
		
		buttonsingle = new JButton(singlestrand);
		buttonsingle.setVerticalTextPosition(AbstractButton.BOTTOM);
		buttonsingle.setHorizontalTextPosition(AbstractButton.CENTER);
		buttonsingle.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonsingle.setToolTipText("Single Strand");
		buttonsingle.setMargin(new Insets(4,4,4,4));
		buttonsingle.addActionListener(this);
		buttonsingle.setEnabled(false);
		shapebuttonpanel.add(buttonsingle);  	
	}
	
	
	/**
	 * Method to enable/disable the SingleStrand button
	 *
	 * @param flag, boolean value
	 */
	public void setSingleStrandEnabled(boolean flag){
		buttonsingle.setEnabled(flag);
	}
	
	/**
	 * Method to enable/disable the ClosedStruct button
	 *
	 * @param flag, boolean value
	 */
	public void setClosedStructEnabled(boolean flag){
		buttonclosed.setEnabled(flag);
	}
	
	/**
	 * Disables all Shape Buttons
	 */
	public void disableAllButtons(){
		buttonstem.setEnabled(false);
		buttonhairpin.setEnabled(false);
		buttonbulge.setEnabled(false);
		buttoninternal.setEnabled(false);
		buttonmultiple.setEnabled(false);
		buttonsingle.setEnabled(false);
		buttonclosed.setEnabled(false);
		buttonclosedmulti.setEnabled(false);
		buttonpseudo.setEnabled(false);
	}
	
	/**
	 * Enables all Shape Buttons (except for single strands and closedstruct)
	 */
	public void resetAllButtons(){
		buttonstem.setEnabled(true);
		buttonhairpin.setEnabled(true);
		buttonbulge.setEnabled(true);
		buttoninternal.setEnabled(true);
		buttonmultiple.setEnabled(true);
		buttonsingle.setEnabled(false);
		buttonclosed.setEnabled(true);
		buttonclosedmulti.setEnabled(true);
		buttonpseudo.setEnabled(true);
	}
	
	/**
	 * This method disables the rotation buttons
	 */
	public void disableRotation(){
		buttonrotleft.setEnabled(false);
		buttonrotright.setEnabled(false);
	}
	
	/**
	 * This method enables the rotation buttons
	 */
	public void enableRotation(){
		buttonrotleft.setEnabled(true);
		buttonrotright.setEnabled(true);
	}
	
	/**
	 * This method disables all user interaction buttons
	 *
	 */
	public void disableFunctionalButtons(){
		buttonnew.setEnabled(false);
		buttonopen.setEnabled(false);
		buttonsave.setEnabled(false);
		buttonprint.setEnabled(false);
		buttonorient.setEnabled(false);
		buttonrotright.setEnabled(false);
		buttonrotleft.setEnabled(false);
		buttonselect.setEnabled(false);
		buttondetach.setEnabled(false);
		buttonmove.setEnabled(false);
		buttonremove.setEnabled(false);
		buttonrestore.setEnabled(false);
		buttontext.setEnabled(false);
		buttonzoomin.setEnabled(false);
		buttonzoomout.setEnabled(false);
	}
	
	/**
	 * This method enables all user interaction buttons
	 *
	 */
	public void enableFunctionalButtons(){
		buttonnew.setEnabled(true);
		buttonopen.setEnabled(true);
		buttonsave.setEnabled(true);
		buttonprint.setEnabled(true);
		buttonorient.setEnabled(true);
		buttonrotright.setEnabled(true);
		buttonrotleft.setEnabled(true);
		buttonselect.setEnabled(true);
		buttondetach.setEnabled(true);
		buttonmove.setEnabled(true);
		buttonremove.setEnabled(true);
		buttonrestore.setEnabled(true);
		//buttontext.setEnabled(true);
		buttonzoomin.setEnabled(true);
		buttonzoomout.setEnabled(true);
	}
	
	/** 
	 * This method initiates a new ADPFilter and returns it
	 * 
	 * @return a new ADPFilter
	 */
	public ADPFilter getADPFilter(){
		return new ADPFilter();
	}
	
	/**
	 * This method returns a reference on itself (this). Used to obtain the
	 * EditorGui from within the internal SwingWorker classes
	 * 
	 * @return this
	 */
	public EditorGui getThis(){
		return this;
	}
	
	/**
	 * This methods sets the title of the frame and ensures the motifhead was used
	 * 
	 * @param structname String containing the current project name
	 */
	public void setStructName(String structname){
		this.structname = structname;
		frame.setTitle("Locomotif: "+this.structname);
		motifheaddone++;
	}
	
	/**
	 * This method returns the current project name
	 * 
	 * @return project name as String
	 */
	public String getStructName(){
		return this.structname;
	}
	
	/**
	 * This method stores the chosen search type
	 * 
	 * @param localsearch true for local searches, false for global folding
	 */
	public void setSearchType(boolean localsearch){
		this.localsearch = localsearch;
		motifheaddone++;
	}
	
	/**
	 * Returns the current search type
	 * 
	 * @return true, if local search, else false
	 */
	public boolean getSearchType(){
		return this.localsearch;
	}
	
	/**
	 * Returns the global minimum of the motif
	 * 
	 * @return int value holding the global minimum
	 */
	public int getGlobalMin(){
		return ds.getGlobalMin();
	}
	
	/**
	 * Returns the global maximum of the motif
	 * 
	 * @return int value holding the global maximum
	 */
	public int getGlobalMax(){
		return ds.getGlobalMax();
	}
	
	/**
	 * Storing the global size within the DrawingSurface
	 * 
	 * @param minsize, overall minimum size of the motif
	 * @param maxsize, overall maximum size of the motif
	 */
	public void storeGlobalSize(int minsize, int maxsize){
		ds.storeGlobalSize(minsize,maxsize);
	}
	
	public void headClosed(){
	    ds.headClosed();
	}
	
	/**
	 * Method for running a matcher from the Locomotif editor.
	 * A SwingWorker is employed to keep the editor interface active during client-server
	 * communications
	 * 
	 * @param em, the users email adress if given
	 * @param seq, the sequence in string format
	 * @param mid, the id of the matcher to be run
	 */
	public void runMatcher(String em, String seq, String mid){
		final String email = em;
		final String sequence = seq;
		final String matcherid = mid;
		final ProgressFrame pf = new ProgressFrame(this, "Running Matcher","Running matcher",matcherid,true);
		//run only one at a time to avoid confusing the results
		menuSequence.setEnabled(false);
		
		runWorker = new SwingWorker(){
			String answer = null;
			
			public Object construct() {
				//doWork method initiates the client server communication
				answer = doWork(email,sequence,matcherid);
				return answer;
			}
			
			public void finished() {
				pf.dispose();
				menuSequence.setEnabled(true);
				if(answer != null){
					new ResultPresenter(answer);
				}
			}
		};
		runWorker.start();	
	}
	
	/**
	 * Method for doing the work necessary in running a matcher on the server
	 * 
	 * @param email, the users email adress if given
	 * @param sequence, the fasta sequence to search with the matcher
	 * @param matcherid, the BiBiServ id of the matcher
	 * @return the results of the matcher run as a String
	 */
	public String doWork(String email, String sequence, String matcherid){
		try{
			String answer = null;
			String id = RNAEditorClient.rnaeditorRun(email,sequence,matcherid);
			if(id.equals("compilation ongoing")){
				JOptionPane.showMessageDialog(null,"Matcher not ready yet!","Compilation not finished",JOptionPane.WARNING_MESSAGE);
				return null;
			}
			else if(id.startsWith("Exception")){
				JOptionPane.showMessageDialog(null,id,"Exception occured",JOptionPane.ERROR_MESSAGE);
				return null;
			}
			while(true){
				answer = RNAEditorClient.rnaeditorMatchresult(id);
				if(answer.equals("computation ongoing")){
					Thread.sleep(500);
				}
				else if(answer.startsWith("Exception")){
					//this should be improved!!!!
					JOptionPane.showMessageDialog(null,answer,"Exception occured",JOptionPane.ERROR_MESSAGE);
					return null;
				}
				else{
					break;
				}
			}
			return answer;
		} catch(InterruptedException ie){
			return null;
		}
	}
	
	/**
	 * This method is called to check upon an ongoing matcher compilation. It continuously checks for 
	 * results and goes to sleep in between checking. Upon success the id is returned again.
	 * 
	 * @param id, the id of the matcher which is compiled
	 * @return id, once compilation is finished
	 */
	public String checkForResult(String id){
		try{
			String answer = null;
			while(true){
				answer = RNAEditorClient.rnaeditorResponse(id);
				if(answer.equals("compilation ongoing")){
					Thread.sleep(500);
				}
				else if(answer.startsWith("Exception")){
					JOptionPane.showMessageDialog(null,"Error during compilation. \nThis may be due to conflicting global size restrictions.\nSuch conflicts can occur either with other global/local restrictions \nor a restriction is incompatible with the motif structure.\n\nOtherwise, please contact us for a solution.","Matcher Compilation aborted",JOptionPane.ERROR_MESSAGE);
					id = "Compilation error";
					return id;
				}
				else{
					return id;
				}
			}
		} catch(InterruptedException ie){
			JOptionPane.showMessageDialog(null,"An exception occured in client-server communication","Client-Server exception",JOptionPane.ERROR_MESSAGE);
			id = "Compilation interruption";
			return id;
		}
	}
	
	/**
	 * This method interrupts the local checking upon a matcher which is currently run.
	 * However, it does NOT interrupt the matcher on the BiBiServ!!
	 *
	 */
	public void interruptMatcher(){
		runWorker.interrupt();
		menuSequence.setEnabled(true);
	}
	
	public void invokeBrowser(String url) {
	     boolean flag = operatingSystem();
	      String s1 = null;
	        try {
	            if(flag) {
	                s1 = "rundll32 url.dll,FileProtocolHandler " + url;
	                Runtime.getRuntime().exec(s1);
	            } else {
	                s1 = "firefox -remote openURL(" + url + ")";
	                Process process1 = Runtime.getRuntime().exec(s1);
	                try {
	                    int i = process1.waitFor();
	                    if(i != 0) {
	                        s1 = "firefox " + url;
	                        Runtime.getRuntime().exec(s1);
	                    }
	                } catch(InterruptedException ex) {
	                    JOptionPane.showMessageDialog(null,"Error bringing up browser, cmd='" + s1 + "'");
	                    System.err.println("Caught: " + ex);
	                }
	            }
	        } catch(IOException ex) {
	            JOptionPane.showMessageDialog(null,"Could not invoke browser, command=" + s1);
	            System.err.println("Caught: " + ex);
	        }
	    }
	 
	    private static boolean operatingSystem() {
	        String name = System.getProperty("os.name");
	        return ((name != null) && (name.startsWith("Windows")));
	    }
	
	/**
	 * Main action listener for the program
	 *
	 * @param ae the ActionEvent
	 */
	public void actionPerformed(ActionEvent ae){
		Object src = ae.getSource();
		//shape buttons
		if(src == buttonstem){
			enableRotation();
			ds.setElementType(STEM_TYPE);
			ds.setButtonType(0);
		}
		if(src == buttonhairpin){
			enableRotation();
			ds.setElementType(HAIRPIN_TYPE);
			ds.setButtonType(0);
		}
		if(src == buttonbulge){
			enableRotation();
			ds.setElementType(BULGE_TYPE);
			ds.setButtonType(0);
		}
		if(src == buttoninternal){
			enableRotation();
			ds.setElementType(INTERNAL_TYPE);
			ds.setButtonType(0);
		}
		if(src == buttonmultiple){
			disableRotation();
			ds.setElementType(MULTI_TYPE);
			ds.setButtonType(0);
		}
		if(src == buttonsingle){
			disableRotation();
			ds.setElementType(SINGLE_TYPE);
			ds.setButtonType(0);
		}
		if(src == buttonclosed){
			enableRotation();
			ds.setElementType(CLOSED_STRUCT_TYPE);
			ds.setButtonType(0);
		}
		if(src == buttonclosedmulti){
			enableRotation();
			ds.setElementType(MULTIEND_TYPE);
			ds.setButtonType(0);
		}
		if(src == buttonpseudo){
		    enableRotation();
		    ds.setElementType(PSEUDO_TYPE);
		    ds.setButtonType(0);
		}
		//file io buttons
		if(src == buttonnew || src == menuNew){
			if(ds.containsStruct()){
				int retval = JOptionPane.showConfirmDialog(this,"Do you wish to save the current project?","Save project",JOptionPane.YES_NO_CANCEL_OPTION);
				if(retval == JOptionPane.CANCEL_OPTION){
					return;
				}
				else if(retval == JOptionPane.YES_OPTION){
					File returnVal = EditorIO.saveFile(this,rnafile,fc);
					if(returnVal != null){
						rnafile = returnVal;
						frame.setTitle("Locomotif: "+rnafile.getPath());
						structname = rnafile.getName();
						ds.saveFile(rnafile);
					}
				}
			}
			rnafile = null;
			ds.removeAll();
			resetAllButtons();
			ds.setButtonType(0);
			frame.setTitle("Locomotif");
			structname = null;
			localsearch = true;
			motifheaddone = 0;
		}
		if(src == buttonopen || src == menuOpen){
			File returnVal = EditorIO.openFile(this,fc);
			if(returnVal != null){
				rnafile = returnVal;
				frame.setTitle("Locomotif: "+rnafile.getPath());
				structname = rnafile.getName();
				localsearch = true;
				motifheaddone = 1;
				ds.setElementType(0);
				ds.setButtonType(0);
				ds.openFile(rnafile);
			}
		}
		if(src == buttonsave || src == menuSave){
			File returnVal = EditorIO.saveFile(this,rnafile,fc);
			if(returnVal != null){
				rnafile = returnVal;
				frame.setTitle("Locomotif: "+rnafile.getPath());
				structname = rnafile.getName();
				ds.setElementType(0);
				ds.setButtonType(0);
				ds.saveFile(rnafile);
			}
		}
		//printing
		if(src == buttonprint || src == menuPrint){
			File returnVal = EditorIO.saveImage(this,imagechooser);
			if(returnVal != null){
				ds.setElementType(0);
				ds.setButtonType(0);
				String imagefilename = returnVal.getPath();
				String extension = "svg";
				if(imagefilename.endsWith(extension)){
					ds.saveSVG(returnVal);
				}
				else{
					BufferedImage bi = ds.saveImage(returnVal);
					String filename = returnVal.getName();
					int i = filename.lastIndexOf('.');
					String ext = "png";
					if(i>0 && i<filename.length()-1) {
						ext = filename.substring(i+1).toLowerCase();
					}
					EditorIO.storeImage(bi, ext, returnVal, this);
				}
			}
		}
		//editing buttons
		if(src == buttonorient){
		    if(!ds.containsStruct()){
		        JOptionPane.showMessageDialog(this,"Please begin a motif design before changing its orientation!","No motif available",JOptionPane.YES_OPTION);
		        return;
		    }
			ds.setElementType(0);
			int retval = JOptionPane.showConfirmDialog(this,"Do you wish to reverse any stored sequence information?","Orientation change",JOptionPane.YES_NO_OPTION);
			if(retval == JOptionPane.YES_OPTION){
				ds.changeOrientation(true);
			}
			else{
				ds.changeOrientation(false);
			}
			ds.setButtonType(0);
		}
		if(src == buttonrotright){ 
		    if(!ds.containsStruct()){
		        JOptionPane.showMessageDialog(this,"Please begin a motif design before performing any rotations!","No motif available",JOptionPane.YES_OPTION);
		        return;
		    }
			ds.setRotation(0);
			ds.setButtonType(0);
		}
		if(src == buttonrotleft){ 
		    if(!ds.containsStruct()){
		        JOptionPane.showMessageDialog(this,"Please begin a motif design before performing any rotations!","No motif available",JOptionPane.YES_OPTION);
		        return;
		    }
			ds.setRotation(1);
			ds.setButtonType(0);
		}
		if(src == buttonselect){
			ds.setElementType(0);
			ds.setButtonType(SELECT_TYPE);
		}
		if(src == buttondetach){
			if(ds.getIsSingleSelected()){
				ds.detachSelected();
			}
			else{
				ds.setElementType(0);
				ds.setButtonType(DETACH_TYPE);
			}
		}
		if(src == buttonmove){
			ds.setElementType(0);
			ds.setButtonType(MOVE_TYPE);
		}
		if(src == buttonremove){
			if(ds.getIsSingleSelected()){
				ds.removeSelected();
			}
			else{
				ds.setElementType(0);
				ds.setButtonType(REMOVE_TYPE);
			}
		}
		if(src == buttonrestore){
			if(rnafile == null){
				JOptionPane.showMessageDialog(null,"You cannot restore your project as you never saved it.","Work not saved",JOptionPane.ERROR_MESSAGE);
			}
			else{
				ds.setElementType(0);
				ds.setButtonType(0);
				ds.openFile(rnafile);
			}
		}
		//N/A at the moment
		if(src == buttontext){
			ds.setElementType(0);
		}
		if(src == buttonzoomin){
			ds.setElementType(0);
			ds.setButtonType(ZOOM_TYPE);
			ds.increaseZoomFactor();
		}
		if(src == buttonzoomout){
			ds.setElementType(0);
			ds.setButtonType(ZOOM_TYPE);
			ds.decreaseZoomFactor();
		}
		//menu actions
		if(src == menuShapes){
			String shapenotation = ds.traverseStructure(Translator.SHAPE_LANG);
			if(shapenotation.startsWith("ERROR")){
				JOptionPane.showMessageDialog(frame, "The structure is not finished yet. Only one open 5' end is allowed!", "Error", JOptionPane.ERROR_MESSAGE);
			}
			else{
				JOptionPane.showMessageDialog(frame, shapenotation, "Result", JOptionPane.PLAIN_MESSAGE);
			}
		}
		if(src == menuXML){
			String xmloutput = ds.traverseStructure(Translator.JDOM_LANG);
			if(xmloutput.startsWith("ERROR")){
				JOptionPane.showMessageDialog(frame, "The structure is not finished yet. Only one open 5' end is allowed!", "Error", JOptionPane.ERROR_MESSAGE);
			}
			else{
				try{
					DOMChecker.checkSizes();
					xmloutput = Translator.getPrettyPrint();
					new OutputPresenter("Result",xmloutput,"text/plain",false);
				}
				catch(SizeConflictException sce){
					JOptionPane.showMessageDialog(frame, sce.getMessage(),"Conflicting size restrictions", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		if(src == menuADP){
			String xmloutput = ds.traverseStructure(Translator.JDOM_LANG);
			if(xmloutput.startsWith("ERROR")){
				JOptionPane.showMessageDialog(frame, "The structure is not finished yet. Only one open 5' end is allowed!", "Error", JOptionPane.ERROR_MESSAGE);
			}
			else{
				try{
					DOMChecker.checkSizes();
					Document doc = Translator.getDOMOutput();
					String adpcode = ADPTranslator.translateIntoADP(doc);
					new TranslatePresenter("ADP Grammar",adpcode,this);
				}
				catch(SizeConflictException sce){
					JOptionPane.showMessageDialog(frame, sce.getMessage(),"Conflicting size restrictions", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		if(src == menuMotifInfo){
			String motifinfo = ds.traverseStructure(Translator.INFO_LANG);
			if(motifinfo.startsWith("ERROR")){
				JOptionPane.showMessageDialog(frame, "The structure is not finished yet. Only one open 5' end is allowed!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			motifinfo = motifinfo.replace("</html>","");
			motifinfo += "</html>";
			new OutputPresenter("Motif information",motifinfo,"text/html",true);
		}
		if(src == menuSend){
			if(motifheaddone < 2){
				JOptionPane.showMessageDialog(frame,"You must specify required motif search parameters.\nClick on the circle on the 5' end to input search parameters","Missing search parameters",JOptionPane.ERROR_MESSAGE);	
				return;
			}
			final SwingWorker worker = new SwingWorker(){
				Document doc = null;
				String email = null;
				
				public Object construct() {
					String xmloutput = ds.traverseStructure(Translator.JDOM_LANG);
					if(xmloutput.startsWith("ERROR")){
						JOptionPane.showMessageDialog(frame, "The structure is not finished yet. Only one open 5' end is allowed!", "Error", JOptionPane.ERROR_MESSAGE);
						return "unfinished structure";
					}
					else{
						try{
							DOMChecker.checkSizes();
						}
						catch(SizeConflictException sce){
							JOptionPane.showMessageDialog(frame, sce.getMessage(),"Conflicting size restrictions", JOptionPane.ERROR_MESSAGE);
							return "conflicting sizes";
						}
						email = JOptionPane.showInputDialog(frame,"If you wish to be notified upon completion, enter an email address.\nOtherwise, leave the space blank!","Email notification",JOptionPane.INFORMATION_MESSAGE);
						if(email == null){
							return null;
						}
						else if(!email.equals("")){
							Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
							Matcher m = p.matcher(email);
							boolean matchFound = m.matches();
							if (!matchFound){
								JOptionPane.showMessageDialog(frame, "The email address you specified is not valid!\nPlease specify a valid address or use without notification.","Invalid email address",JOptionPane.ERROR_MESSAGE);
								return "invalid email";
							}
						}
						doc = Translator.getDOMOutput();
						ProgressFrame pf = new ProgressFrame(getThis(), "Client-Server-Communication","Motif data is send to server", "",false);
						id = RNAEditorClient.rnaeditorRequest(email,doc);
						pf.dispose();
						if(id != null && !id.startsWith("Exception")){
							String answer = RNAEditorClient.rnaeditorResponse(id);
							if(!answer.equals("finished")){
								ProgressFrame pf2 = new ProgressFrame(getThis(), "Compilation","Matcher compilation ongoing", "",false);
								id = checkForResult(id);
								pf2.dispose();
							}
						}
						return id;
					}
				}
				
				public void finished() {
					if(id != null){
						if(id.startsWith("Exception")){
							JOptionPane.showMessageDialog(null, "An error occured: "+id);
						}
						else if(id.startsWith("Compilation")){
							return;
						}
						else{
							if(obtainedids == null){
								obtainedids = new Vector<String[]>();
							}
							for( String[] sa : obtainedids){
								if(id.equals(sa[1])){
								    return;
								}
							}
							obtainedids.add(new String[]{structname,id});
							idpane = new JEditorPane("text/html","<HTML>You obtained the following id: <br><p>"+id+"</p><br>Do you wish to store the id for future use?</html>");
						    idpane.setBackground(getBackground());
							int retval = JOptionPane.showConfirmDialog(null,idpane,"ID Storage",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
							if(retval == JOptionPane.YES_OPTION){
								try{
									EditorIO.storeID(structname,id);
								}
								catch(IOException ioe){
									JOptionPane.showMessageDialog(null,"ID could not be saved:\n"+ioe.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
								}
							}
						}
					}
				}
			};
			worker.start();	
		}
		if(src == menuSequence){
			final SwingWorker worker = new SwingWorker(){
				
				public Object construct() {
					new RunPanel(obtainedids,getThis());
					return null;
				}
				
			};
			worker.start();	
		}
		if(src == menuSubmit){
		    submitlabel = new JEditorPane("text/html","<HTML>To submit your sequences go to <br><br><u><i>http://bibiserv.techfak.uni-bielefeld.de/locomotif/submission.html</i></u>");
		    submitlabel.setBackground(this.getBackground());
		    int retval = JOptionPane.showConfirmDialog(null, submitlabel, "Locomotif Submission", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
		    if(retval == JOptionPane.OK_OPTION){
		        invokeBrowser("http://bibiserv.techfak.uni-bielefeld.de/locomotif/submission.html");
		    }
		}
		if(src == menuDownload){
		    downloadlabel = new JEditorPane("text/html","<HTML>To download your matcher (c - sources) go to <br><br><u><i>http://bibiserv.techfak.uni-bielefeld.de/locomotif/download.html</i></u>");
		    downloadlabel.setBackground(this.getBackground());
		    int retval = JOptionPane.showConfirmDialog(null, downloadlabel, "Locomotif Download", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
		    if(retval == JOptionPane.OK_OPTION){
		        invokeBrowser("http://bibiserv.techfak.uni-bielefeld.de/locomotif/download.html");
		    }
		}
		if(src == menuHelp){
			String text = "To generate and use motif search programs:\n\nMotif construction:\n- Use the left buttons to choose a building block. Place the first building block on the canvas by a mouse click.\n  Add subsequent ones to available open ends or use them to start new structure parts.\n";
			text += "  Single strands can only be used in motifs with only one open end left per structure part. \n  Then, they can be used to prolong the 5' or 3' end or to connect either of these with different structure parts.\n- Double-click on any building block for an editing interface to include size or sequence annotations.\n- Use the top buttons for editing operations on the motif.\n- For matcher generation, you must open the motifhead by clicking on the red circle around the 5' end.\n  Then, you must choose a project name and the search type. It is also recommended to give a maximum motif size.\n\n";
			text += "Matcher generation:\n- Matchers can be generated as soon as a motif with the only open ends being 5' and 3' is available.\n- Using the menu 'Translation->Send XML', send the XML code to our server for translation on the server. \n  You will obtain a bibiserv ID once compilation is finished. This may take some time!\n\n";
			text += "Running a matcher:\n- You can run the generated matcher program directly via Locomotif using the menu 'Use Matcher->Run Now'.\n- Alternatively, you can use the submission page on http://bibitest.techfak.uni-bielefeld.de/locomotif\n  accessible via the menu 'Use Matcher->Submit'.\n- In both cases, you must choose the matcher ID and provide a target sequence.\n- Results will be presented in a popup window once available.\n";
			text += "- Matcher runtime depends on sequence length and maximum motif size.\n  For matchers with a restricted maximum motif size, target sequences up to 10000 bases can be processed on the BiBiServ\n- A third option is to download the matcher sources to your own system via 'Use Matcher->Download'.";
			JOptionPane.showMessageDialog(frame,text,"Locomotif Short Manual",JOptionPane.INFORMATION_MESSAGE);
		}
		if(src == menuQuit){
			System.exit(0);
		}
	}
	
	
	/**
	 * Main Method of the program
	 */
	public static void createAndShowGUI(){
		Locale.setDefault(Locale.ENGLISH);
		//nice window decorations
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		frame = new JFrame("Locomotif");
		frame.setSize(new Dimension(800,600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		EditorGui eg = new EditorGui();
		
		frame.setJMenuBar(eg.createMenuBar());
		frame.setContentPane(eg.makeContentPane());
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * Main Method of the program
	 * @param args none
	 */
	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	
	/**
	 * This class defines a filter such that only .rna - files will be 
	 * available
	 */
	private class RNAFilter extends javax.swing.filechooser.FileFilter{
		private String RNA_FILE = "rna";
		
		/**
		 * determines wether a file should appear as choosable
		 *
		 * @param f the file to test for choosability
		 * @return <i>true</i> if choosable, else <i>false</i>
		 */
		public boolean accept(File f) {
			if(f != null){
				if( f.isDirectory()) {
					return true;
				}
				else{
					String extension = getExtension(f);
					if(extension != null && extension.equals(RNA_FILE))
					{
						return true;
					}
				}
			}
			return false;
		}
		
		/**
		 * String to describe .rna-files
		 */
		public String getDescription(){
			return "rna structure edit files";
		}
		
		/**
		 * returns the extension of a file
		 */
		public String getExtension(File f) {
			if(f != null) {
				String filename = f.getName();
				int i = filename.lastIndexOf('.');
				if(i>0 && i<filename.length()-1) {
					return filename.substring(i+1).toLowerCase();
				};
			}
			return null;
		}
	}
	
	
	/**
	 * This class defines a filter such that only .lhs - files will be 
	 * available
	 */
	private class ADPFilter extends javax.swing.filechooser.FileFilter{
		private String ADP_FILE = "lhs";
		
		/**
		 * determines wether a file should appear as choosable
		 *
		 * @param f the file to test for choosability
		 * @return <i>true</i> if choosable, else <i>false</i>
		 */
		public boolean accept(File f) {
			if(f != null){
				if( f.isDirectory()) {
					return true;
				}
				else{
					String extension = getExtension(f);
					if(extension != null && extension.equals(ADP_FILE))
					{
						return true;
					}
				}
			}
			return false;
		}
		
		/**
		 * String to describe .rna-files
		 */
		public String getDescription(){
			return "haskell file";
		}
		
		/**
		 * returns the extension of a file
		 */
		public String getExtension(File f) {
			if(f != null) {
				String filename = f.getName();
				int i = filename.lastIndexOf('.');
				if(i>0 && i<filename.length()-1) {
					return filename.substring(i+1).toLowerCase();
				};
			}
			return null;
		}
	}
	
	/**
	 * This class defines a filter such that only matcher - files will be 
	 * available
	 */
	private class MatcherFilter extends javax.swing.filechooser.FileFilter{
		private String MATCHER_FILE = "matcher";
		
		/**
		 * determines wether a file should appear as choosable
		 *
		 * @param f the file to test for choosability
		 * @return <i>true</i> if choosable, else <i>false</i>
		 */
		public boolean accept(File f) {
			if(f != null){
				if( f.isDirectory()) {
					return true;
				}
				else{
					String extension = getExtension(f);
					if(extension != null && extension.equals(MATCHER_FILE))
					{
						return true;
					}
				}
			}
			return false;
		}
		
		/**
		 * String to describe .rna-files
		 */
		public String getDescription(){
			return "Matcher file";
		}
		
		/**
		 * returns the extension of a file
		 */
		public String getExtension(File f) {
			if(f != null) {
				String filename = f.getName();
				int i = filename.lastIndexOf('_');
				if(i>0 && i<filename.length()-1) {
					return filename.substring(i+1);
				};
			}
			return null;
		}
	}
}

