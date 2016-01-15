package rnaeditor;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 * This class defines the user interface for editing a bulge element
 *
 * @author Janina Reeder
 */
public class BulgeEdit extends JFrame implements ActionListener, CaretListener{
	
	private static final long serialVersionUID = 6446651382426172019L;
	private Bulge bulge;
	private BulgeShape bs;
	private JPanel pane;
	private JTabbedPane tabbedPane;
	private JRadioButton deflen, exact, range;
	private JRadioButton gdeflen, grange;
	private JRadioButton fivethree, threefive;
	private JButton ok, apply, cancel;
	private JLabel l1,l2,l3,sl1,bp5,bp3;
	private JLabel gl2, gl3;
	private JLabel loclabel;
	private JTextField t1, lowerend, upperend, seqm, bp5tf, bp3tf;
	private JTextField glowerend, gupperend;
	private String t1buf, lowerendbuf, upperendbuf, seqmbuf, bp5tfbuf, bp3tfbuf;
	private String glowerendbuf, gupperendbuf;
	private DrawingSurface ds;
	private boolean initial53, changedloc;
	private int type;
	
	/**
	 * Constructor for the BulgeEdit GUI
	 *
	 * @param bs the parent BulgeShape
	 * @param ds the parent DrawingSurface
	 * @param type determines which tab is shown at first
	 */
	public BulgeEdit(BulgeShape bs, DrawingSurface ds, int type){
		super("Bulge Options");
		
		this.bs = bs;
		this.bulge = bs.getBulge();
		this.ds = ds;
		this.initial53 = bulge.getBulgeLoc();
		this.type = type;
		
		JFrame.setDefaultLookAndFeelDecorated(true);
		//setSize(new Dimension(300,200));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		setContentPane(makeContentPane());
		setResizable(false);
		pack();
		setVisible(true);
	}
	
	/**
	 * Main method that generates the gui
	 *
	 * @return the container of the GUI
	 */
	private Container makeContentPane(){
		pane = new JPanel();
		//pane.setSize(new Dimension(300,400));
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		tabbedPane = new JTabbedPane();
		
		
		//SIZE PANEL
		JPanel card1 = new JPanel();
		
		ButtonGroup bg = new ButtonGroup();
		JPanel defaultpanel = new JPanel();
		deflen = new JRadioButton("No size restriction");
		deflen.setToolTipText("Unrestricted bulge has at least 2 basepairs and 1 base in the loop");
		
		
		JPanel exactpanel = new JPanel();
		exact = new JRadioButton("Exact size");
		exact.setToolTipText("Specify an exact size for the bulge loop");
		JPanel exactnumpanel = new JPanel();
		l1 = new JLabel("Number of bases in loop segment: ");
		l1.setToolTipText("Specify the number of bases contained in the loop region of the bulge");
		
		JPanel rangepanel = new JPanel();
		range = new JRadioButton("Size range");
		range.setToolTipText("Specifiy a size range for the bulge loop");
		l2 = new JLabel("Minimum number of bases in loop segment:  ");
		l2.setToolTipText("Specify the minimum number of bases contained in the loop region of the bulge");
		l3 = new JLabel("Maximum number of bases in loop segment: ");
		l3.setToolTipText("Specify the maximum number of bases contained in the loop region of the bulge");
		JPanel rangevaluepanel1 = new JPanel();
		JPanel rangevaluepanel2 = new JPanel();
		
		if(bulge.getIsDefaultLength()){
			t1 = new JTextField(3);
			lowerend = new JTextField(3);
			upperend = new JTextField(3);
			l1.setEnabled(false);
			t1.setEnabled(false);
			l2.setEnabled(false);
			lowerend.setEnabled(false);
			l3.setEnabled(false);
			upperend.setEnabled(false);
			deflen.setSelected(true);
			exact.setSelected(false);
			range.setSelected(false);
		}
		else if(bulge.getIsExactLength()){
			t1 = new JTextField(String.valueOf(bulge.getLength()-4),3);
			lowerend = new JTextField(3);
			upperend = new JTextField(3);
			deflen.setSelected(false);
			exact.setSelected(true);
			range.setSelected(false);
			l1.setEnabled(true);
			t1.setEnabled(true);
			l2.setEnabled(false);
			lowerend.setEnabled(false);
			l3.setEnabled(false);
			upperend.setEnabled(false);
		}
		else{
			t1 = new JTextField(3);
			lowerend = new JTextField(3);
			upperend = new JTextField(3);
			if(!bulge.getIsDefaultMin()){
				lowerend.setText(String.valueOf(bulge.getMinLength()-4));
			}
			if(!bulge.getIsDefaultMax()){
				upperend.setText(String.valueOf(bulge.getMaxLength()-4));
			}
			exact.setSelected(false);
			range.setSelected(true);
			l1.setEnabled(false);
			t1.setEnabled(false);
			l2.setEnabled(true);
			lowerend.setEnabled(true);
			l3.setEnabled(true);
			upperend.setEnabled(true);
		}
		
		defaultpanel.setLayout(new BoxLayout(defaultpanel, BoxLayout.PAGE_AXIS));
		deflen.setAlignmentX(Component.LEFT_ALIGNMENT);
		deflen.addActionListener(this);
		defaultpanel.add(deflen);
		
		exactpanel.setLayout(new BoxLayout(exactpanel, BoxLayout.PAGE_AXIS));
		exact.setAlignmentX(Component.LEFT_ALIGNMENT);
		exact.addActionListener(this);
		exactpanel.add(exact);
		
		exactnumpanel.setLayout(new BoxLayout(exactnumpanel, BoxLayout.LINE_AXIS));
		exactnumpanel.add(Box.createRigidArea(new Dimension(20,0)));
		exactnumpanel.add(l1);
		
		t1.setMaximumSize(t1.getPreferredSize());
		t1.addCaretListener(this);
		t1buf = t1.getText();
		exactnumpanel.add(t1);
		exactnumpanel.add(Box.createHorizontalGlue());
		exactnumpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		exactpanel.add(exactnumpanel);
		
		
		rangepanel.setLayout(new BoxLayout(rangepanel, BoxLayout.PAGE_AXIS));
		range.setAlignmentX(Component.LEFT_ALIGNMENT);
		range.addActionListener(this);
		rangepanel.add(range);
		
		lowerend.setMaximumSize(lowerend.getPreferredSize());
		lowerend.addCaretListener(this);
		lowerendbuf = lowerend.getText();
		upperend.setMaximumSize(upperend.getPreferredSize());
		upperend.addCaretListener(this);
		upperendbuf = upperend.getText();
		rangevaluepanel1.setLayout(new BoxLayout(rangevaluepanel1, BoxLayout.LINE_AXIS));
		rangevaluepanel1.add(Box.createRigidArea(new Dimension(20,0)));
		rangevaluepanel1.add(l2);
		rangevaluepanel1.add(lowerend);
		rangevaluepanel1.add(Box.createHorizontalGlue());
		rangevaluepanel1.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangevaluepanel2.setLayout(new BoxLayout(rangevaluepanel2, BoxLayout.LINE_AXIS));
		rangevaluepanel2.add(Box.createRigidArea(new Dimension(20,0)));
		rangevaluepanel2.add(l3);
		rangevaluepanel2.add(upperend);
		rangevaluepanel2.add(Box.createHorizontalGlue());
		rangevaluepanel2.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangepanel.add(rangevaluepanel1);
		rangepanel.add(rangevaluepanel2);
		
		bg.add(deflen);
		bg.add(exact);
		bg.add(range);
		
		//END OF SIZE PANEL
		
		//BOTTOM BUTTONS ON ALL PANELS
		JPanel buttonpanel = new JPanel();
		buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.LINE_AXIS));
		ok = new JButton("OK");
		apply = new JButton("apply");
		cancel = new JButton("cancel");
		ok.setEnabled(true);
		ok.setSelected(true);
		ok.addActionListener(this);
		apply.setEnabled(false);
		apply.addActionListener(this);
		cancel.setEnabled(true);
		cancel.addActionListener(this);
		buttonpanel.add(Box.createHorizontalGlue());
		buttonpanel.add(ok);
		buttonpanel.add(apply);
		buttonpanel.add(cancel);
		
		defaultpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		exactpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangepanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		buttonpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		card1.setLayout(new BoxLayout(card1,BoxLayout.PAGE_AXIS));
		card1.add(defaultpanel);
		card1.add(Box.createRigidArea(new Dimension(0,10)));
		card1.add(exactpanel);
		card1.add(Box.createRigidArea(new Dimension(0,10)));
		card1.add(rangepanel);
		//END OF BOTTOM BUTTONS
		
		//BULGE LOCATION PANEL
		JPanel card2 = new JPanel();
		
		JPanel locpanel = new JPanel();
		locpanel.setLayout(new BoxLayout(locpanel, BoxLayout.PAGE_AXIS));
		
		ButtonGroup bg2 = new ButtonGroup();
		
		loclabel = new JLabel("Bulge is located on");
		loclabel.setToolTipText("Choose the location of the bulge loop");
		loclabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		locpanel.add(loclabel);
		fivethree = new JRadioButton("5' - 3' sequence");
		fivethree.setToolTipText("Bulge located on 5' strand");
		fivethree.setAlignmentX(Component.LEFT_ALIGNMENT);
		threefive = new JRadioButton("3' - 5' sequence");
		threefive.setToolTipText("Bulge located on 3' strand");
		threefive.setAlignmentX(Component.LEFT_ALIGNMENT);
		if(initial53){
			fivethree.setSelected(true);
			threefive.setSelected(false);
		}
		else{
			fivethree.setSelected(false);
			threefive.setSelected(true);
		}
		fivethree.addActionListener(this);
		threefive.addActionListener(this);
		locpanel.add(fivethree);
		locpanel.add(threefive);
		bg2.add(fivethree);
		bg2.add(threefive);
		
		card2.setLayout(new BoxLayout(card2,BoxLayout.PAGE_AXIS));
		card2.add(locpanel);
		//END OF BULGE LOCATION PANEL
		
		//SEQUENCE MOTIFS PANEL
		JPanel card3 = new JPanel();
		
		
		JPanel sequencepanel = new JPanel();
		JPanel sequencelabelpanel = new JPanel();
		JPanel sequencemotifpanel = new JPanel();
		
		sl1 = new JLabel("Enter a loop sequence motif:");
		seqm = new JTextField(bulge.getLoopMotif(),15);
		seqm.setToolTipText("<html>Matcher will check for motif anywhere in the loop.<br>Use \"N\" at the front and/or back of the motif to enforce a specific location!</html>");
		
		sl1.setAlignmentX(Component.LEFT_ALIGNMENT);
		seqm.setAlignmentX(Component.LEFT_ALIGNMENT);
		seqm.setMaximumSize(seqm.getPreferredSize());
		seqm.addCaretListener(this);
		seqmbuf = seqm.getText();
		
		sequencepanel.setLayout(new BoxLayout(sequencepanel, BoxLayout.PAGE_AXIS));
		
		sequencelabelpanel.setLayout(new BoxLayout(sequencelabelpanel, BoxLayout.LINE_AXIS));
		sequencelabelpanel.add(Box.createRigidArea(new Dimension(20,0)));
		sequencelabelpanel.add(sl1);
		
		sequencemotifpanel.setLayout(new BoxLayout(sequencemotifpanel, BoxLayout.LINE_AXIS));
		sequencemotifpanel.add(Box.createRigidArea(new Dimension(40,0)));
		sequencemotifpanel.add(seqm);
		
		sequencelabelpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		sequencemotifpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		sequencepanel.add(Box.createRigidArea(new Dimension(0,10)));
		sequencepanel.add(sequencelabelpanel);
		sequencepanel.add(Box.createRigidArea(new Dimension(0,10)));
		sequencepanel.add(sequencemotifpanel);
		
		JPanel bp5panel = new JPanel();
		bp5panel.setLayout(new BoxLayout(bp5panel, BoxLayout.LINE_AXIS));
		
		bp5 = new JLabel("5' Basepair:");
		bp5.setToolTipText("<html>Basepair closer to the 5' end<br>Format: 'ag'; first base on 5' strand, second on 3' strand</html>");
		bp5.setAlignmentX(Component.LEFT_ALIGNMENT);
		bp5tf = new JTextField(bulge.getBasepairString(0),2);
		bp5tf.setAlignmentX(Component.LEFT_ALIGNMENT);
		bp5tf.setMaximumSize(bp5tf.getPreferredSize());
		bp5tf.addCaretListener(this);
		bp5tfbuf = bp5tf.getText();
		
		bp5panel.add(Box.createRigidArea(new Dimension(20,0)));
		bp5panel.add(bp5);
		bp5panel.add(Box.createRigidArea(new Dimension(10,0)));
		bp5panel.add(bp5tf);
		
		JPanel bp3panel = new JPanel();
		bp3panel.setLayout(new BoxLayout(bp3panel, BoxLayout.LINE_AXIS));
		
		bp3 = new JLabel("3' Basepair:");
		bp3.setToolTipText("<html>Basepair further from the 5' end<br>Format: 'ag'; first base on 5' strand, second on 3' strand</html>");
		bp3.setAlignmentX(Component.LEFT_ALIGNMENT);
		bp3tf = new JTextField(bulge.getBasepairString(1),2);
		bp3tf.setAlignmentX(Component.LEFT_ALIGNMENT);
		bp3tf.setMaximumSize(bp3tf.getPreferredSize());
		bp3tf.addCaretListener(this);
		bp3tfbuf = bp3tf.getText();
		
		bp3panel.add(Box.createRigidArea(new Dimension(20,0)));
		bp3panel.add(bp3);
		bp3panel.add(Box.createRigidArea(new Dimension(10,0)));
		bp3panel.add(bp3tf);
		
		bp5panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		bp3panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		sequencepanel.add(Box.createRigidArea(new Dimension(0,20)));
		sequencepanel.add(bp5panel);
		sequencepanel.add(Box.createRigidArea(new Dimension(0,10)));
		sequencepanel.add(bp3panel);
		
		sequencepanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		card3.setLayout(new BoxLayout(card3,BoxLayout.PAGE_AXIS));
		card3.add(sequencepanel);
		//END OF SEQUENCE MOTIFS PANEL
		
		//GLOBAL SIZE PANEL
		JPanel card4 = new JPanel();
		
		ButtonGroup bg3 = new ButtonGroup();
		JPanel gdefaultpanel = new JPanel();
		gdeflen = new JRadioButton("No size restriction");
		gdeflen.setToolTipText("Substructure not restricted");
		
		JPanel grangepanel = new JPanel();
		grange = new JRadioButton("Global size range");
		grange.setToolTipText("Specify a size range for the substructure rooted at this bulge");
		gl2 = new JLabel("Minimum number of bases in substructure:  ");
		gl2.setToolTipText("<html>Specify a minimum number of bases that must be contained in the structure<br> rooted at the bulge including the bases within the bulge!</html>");
		gl3 = new JLabel("Maximum number of bases in substructure: ");
		gl3.setToolTipText("<html>Specify a minimum number of bases that must be contained in the structure<br> rooted at the bulge including the bases within the bulge!</html>");
		JPanel grangevaluepanel1 = new JPanel();
		JPanel grangevaluepanel2 = new JPanel();
		
		if(!bulge.hasGlobalLength()){
			glowerend = new JTextField(3);
			gupperend = new JTextField(3);
			gl2.setEnabled(false);
			glowerend.setEnabled(false);
			gl3.setEnabled(false);
			gupperend.setEnabled(false);
			gdeflen.setSelected(true);
			grange.setSelected(false);
		}
		else{
			glowerend = new JTextField(3);
			gupperend = new JTextField(3);
			glowerend.setText(bulge.getMinGlobalLength());
			gupperend.setText(bulge.getMaxGlobalLength());
			grange.setSelected(true);
			gl2.setEnabled(true);
			glowerend.setEnabled(true);
			gl3.setEnabled(true);
			gupperend.setEnabled(true);
		}
		
		gdefaultpanel.setLayout(new BoxLayout(gdefaultpanel, BoxLayout.PAGE_AXIS));
		gdeflen.setAlignmentX(Component.LEFT_ALIGNMENT);
		gdeflen.addActionListener(this);
		gdefaultpanel.add(gdeflen);
		
		grangepanel.setLayout(new BoxLayout(grangepanel, BoxLayout.PAGE_AXIS));
		grange.setAlignmentX(Component.LEFT_ALIGNMENT);
		grange.addActionListener(this);
		grangepanel.add(grange);
		
		glowerend.setMaximumSize(glowerend.getPreferredSize());
		glowerend.addCaretListener(this);
		glowerendbuf = glowerend.getText();
		gupperend.setMaximumSize(gupperend.getPreferredSize());
		gupperend.addCaretListener(this);
		gupperendbuf = gupperend.getText();
		grangevaluepanel1.setLayout(new BoxLayout(grangevaluepanel1, BoxLayout.LINE_AXIS));
		grangevaluepanel1.add(Box.createRigidArea(new Dimension(20,0)));
		grangevaluepanel1.add(gl2);
		grangevaluepanel1.add(glowerend);
		grangevaluepanel1.add(Box.createHorizontalGlue());
		grangevaluepanel1.setAlignmentX(Component.LEFT_ALIGNMENT);
		grangevaluepanel2.setLayout(new BoxLayout(grangevaluepanel2, BoxLayout.LINE_AXIS));
		grangevaluepanel2.add(Box.createRigidArea(new Dimension(20,0)));
		grangevaluepanel2.add(gl3);
		grangevaluepanel2.add(gupperend);
		grangevaluepanel2.add(Box.createHorizontalGlue());
		grangevaluepanel2.setAlignmentX(Component.LEFT_ALIGNMENT);
		grangepanel.add(grangevaluepanel1);
		grangepanel.add(grangevaluepanel2);
		
		bg3.add(gdeflen);
		bg3.add(grange);
		
		gdefaultpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		grangepanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		card4.setLayout(new BoxLayout(card4,BoxLayout.PAGE_AXIS));
		card4.add(gdefaultpanel);
		card4.add(Box.createRigidArea(new Dimension(0,10)));
		card4.add(grangepanel);
		card4.add(Box.createRigidArea(new Dimension(0,10)));
		
		//END OF SIZE PANEL
	
		tabbedPane.addTab("Bulge Size", card1);
		tabbedPane.addTab("Location", card2);
		tabbedPane.addTab("Sequence", card3);
		tabbedPane.addTab("Global Size", card4);
		
		switch(type){
		case 2: tabbedPane.setSelectedIndex(2); break;	//Sequence
		case 3: tabbedPane.setSelectedIndex(1); break;	//Location	
		case 9: tabbedPane.setSelectedIndex(3); break;	//Global Size
		default: tabbedPane.setSelectedIndex(0); 		//Bulge Size
		}
		
		
		pane.add(tabbedPane);
		pane.add(Box.createRigidArea(new Dimension(0,5)));
		pane.add(buttonpanel);
		return pane;
	} 
	
	/**
	 * Button actionlistener
	 *
	 * @param ae the ActionEvent which ocurred
	 */
	public void actionPerformed(ActionEvent ae){
		Object src = ae.getSource();
		if(src == deflen){
			l1.setEnabled(false);
			t1.setText(null);
			t1buf = t1.getText();
			t1.setEnabled(false);
			l2.setEnabled(false);
			lowerend.setText(null);
			lowerendbuf = lowerend.getText();
			lowerend.setEnabled(false);
			l3.setEnabled(false);
			upperend.setText(null);
			upperendbuf = upperend.getText();
			upperend.setEnabled(false);
		}
		if(src == range){
			l1.setEnabled(false);
			t1.setText(null);
			t1buf = t1.getText();
			t1.setEnabled(false);
			l2.setEnabled(true);
			if(!bulge.getIsDefaultMin() && bulge.getMinLength()>0){
				lowerend.setText(String.valueOf(bulge.getMinLength()-4));
				lowerendbuf = lowerend.getText();
			}
			lowerend.setEnabled(true);
			l3.setEnabled(true);
			if(!bulge.getIsDefaultMax() && bulge.getMaxLength()>0){
				upperend.setText(String.valueOf(bulge.getMaxLength()-4));
				upperendbuf = upperend.getText();
			}
			upperend.setEnabled(true);
		}
		if(src == exact){
			l1.setEnabled(true);
			t1.setText(String.valueOf(bulge.getLength()/2));
			t1buf = t1.getText();
			t1.setEnabled(true);
			l2.setEnabled(false);
			lowerend.setEnabled(false);
			l3.setEnabled(false);
			upperend.setEnabled(false);
		}
		if(src == fivethree){ // change bulge location to 5'-3'
			if(initial53){
				changedloc = false;
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(2,true);
			}
			else{
				changedloc = true;
				apply.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(2,false);
			}
		}
		if(src == threefive){ // change bulge location to 3'-5'
			if(initial53){
				changedloc = true;
				apply.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(2,false);
			}
			else{
				changedloc = false;
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(2,true);
			}
		}
		if(src == gdeflen){
			gl2.setEnabled(false);
			glowerend.setText(null);
			glowerendbuf = glowerend.getText();
			glowerend.setEnabled(false);
			gl3.setEnabled(false);
			gupperend.setText(null);
			gupperendbuf = gupperend.getText();
			gupperend.setEnabled(false);
		}
		if(src == grange){
			gl2.setEnabled(true);
			glowerend.setText(bulge.getMinGlobalLength());
			glowerendbuf = glowerend.getText();
			glowerend.setEnabled(true);
			gl3.setEnabled(true);
			gupperend.setText(bulge.getMaxGlobalLength());
			gupperendbuf = gupperend.getText();
			gupperend.setEnabled(true);
		}
		//save changes and close
		if(src == ok){
		    //default length: no length restriction
			if(deflen.isSelected()){
			    //check and store sequence motif
				if(seqm.getText() != null && !seqmbuf.equals(seqm.getText())){
					if(!Iupac.isCorrectMotif(seqm.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else{
						bulge.setLoopMotif(seqm.getText());
					}
				}
				//check and store 5' basepair
				if(bp5tf.getText() != null && !bp5tfbuf.equals(bp5tf.getText())){
					if(!((bp5tf.getText()).length()==2 || (bp5tf.getText()).length()==0)){
						int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length at 5' end", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else if(!Iupac.isCorrectMotif(bp5tf.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code at 5' end", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else{
						bulge.setBasepair(bp5tf.getText(),0);
					}
				}
				//check and store 3' basepair
				if(bp3tf.getText() != null && !bp3tfbuf.equals(bp3tf.getText())){
					if(!((bp3tf.getText()).length()==2 || (bp3tf.getText()).length()==0)){
						int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length at 3' end", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else if(!Iupac.isCorrectMotif(bp3tf.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code at 3' end", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else{
						bulge.setBasepair(bp3tf.getText(),1);
					}
				}
				bulge.setToDefaultLength();
				bs.changeSize(bulge.getLength());
			}
			//exact length restriction
			if(exact.isSelected()){
				try{
				    //no value given: length restriction was removed
					if(t1.getText() == null || (t1.getText()).equals("")){
						deflen.doClick();
						return;
					}
					//parse and check length value
					int l = Integer.parseInt(t1.getText());
					if(l<(Bulge.MIN-4)){
						throw new NumberFormatException("A Bulge loop must contain at least "+(Bulge.MIN-4)+" base.");
					}
					if(l>(Bulge.MAX-4) && !t1buf.equals(t1.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(Bulge.MAX-4)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					//check and store sequence motif
					if(seqm.getText() != null){
						if((seqm.getText()).length()>l){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							else{
								bulge.setLoopMotif(null);
							}
						}
						else if(!seqmbuf.equals(seqm.getText()) && !Iupac.isCorrectMotif(seqm.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
						else{
							bulge.setLoopMotif(seqm.getText());
						}
					}
					//check and store 5' basepair
					if(bp5tf.getText() != null && !bp5tfbuf.equals(bp5tf.getText())){
						if(!((bp5tf.getText()).length()==2 || (bp5tf.getText()).length()==0)){
							int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length at 5' end", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
						else if(!Iupac.isCorrectMotif(bp5tf.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code at 5' end", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
						else{
							bulge.setBasepair(bp5tf.getText(),0);
						}
					}
					//check and store 3' basepair
					if(bp3tf.getText() != null && !bp3tfbuf.equals(bp3tf.getText())){
						if(!((bp3tf.getText()).length()==2 || (bp3tf.getText()).length()==0)){
							int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length at 3' end", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
						else if(!Iupac.isCorrectMotif(bp3tf.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code at 3' end", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
						else{
							bulge.setBasepair(bp3tf.getText(),1);
						}
					}
					bulge.setLength(l+4);
					bs.changeSize(bulge.getLength()); //change bulge size
				}
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			//a size range is given
			else if(range.isSelected()){
				int low=0;
				int up=0;
				try{
				    //parse and check minimum value
					if(lowerend.getText() != null && !(lowerend.getText()).equals("")){
						low = Integer.parseInt(lowerend.getText());	
						if(low<(Bulge.MIN-4)){
							throw new NumberFormatException("A Bulge loop must contain at least "+(Bulge.MIN-4)+" base.");
						}
						if(low>(Bulge.MAX-4) && !lowerendbuf.equals(lowerend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(Bulge.MAX-4)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
					}
					//parse and check maximum value
					if(upperend.getText() != null && !(upperend.getText()).equals("")){
						up = Integer.parseInt(upperend.getText());
						if(up<(Bulge.MIN-4)){
							throw new NumberFormatException("A Bulge loop must contain at least "+(Bulge.MIN-4)+" base.");
						}
						if(up>(Bulge.MAX-4) && !upperendbuf.equals(upperend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(Bulge.MAX-4)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
						else if(up < low){
							throw new NumberFormatException("Upper bound must be larger than lower bound.");
						}
					}
				}
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
					return;
				}
				//no range values were given: set to default length
				if(low == 0 && up == 0){
					deflen.doClick();
					return;
				}
				//check and store sequence motif
				if(seqm.getText() != null){
					if(up != 0 && (seqm.getText()).length()>up){
						int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
						else{
							bulge.setLoopMotif(null);
						}
					}
					else if(!seqmbuf.equals(seqm.getText()) && !Iupac.isCorrectMotif(seqm.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else{
						bulge.setLoopMotif(seqm.getText());
					}
				}
				//check and store 5' basepair
				if(bp5tf.getText() != null && !bp5tfbuf.equals(bp5tf.getText())){
					if(!((bp5tf.getText()).length()==2 || (bp5tf.getText()).length()==0)){
						int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length at 5' end", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else if(!Iupac.isCorrectMotif(bp5tf.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code at 5' end", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else{
						bulge.setBasepair(bp5tf.getText(),0);
					}
				}
				//check and store 3' basepair
				if(bp3tf.getText() != null && !bp3tfbuf.equals(bp3tf.getText())){
					if(!((bp3tf.getText()).length()==2 || (bp3tf.getText()).length()==0)){
						int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length at 3' end", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else if(!Iupac.isCorrectMotif(bp3tf.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code at 3' end", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else{
						bulge.setBasepair(bp3tf.getText(),1);
					}
				}
				//storing length restrictions
				if(low != 0){
					bulge.setMinLength(low+4);
				}
				else{
					bulge.setToDefaultMin();
				}
				if(up != 0){
					bulge.setMaxLength(up+4);
				}
				else{
					bulge.setToDefaultMax();
				}
				bs.changeSize(bulge.getMinLength(),bulge.getMaxLength());
			}
			// bulge location was changed
			if(changedloc){
				bs.changeBulgeLoc();
				bs.switchsides();
				ds.adjustShapeField();
			}
			//no global size restriction
			if(gdeflen.isSelected()){
				bulge.removeGlobalLength();
			}
			//global size restriction
			else if(grange.isSelected()){
				int glow=0;
				int gup=0;
				try{
				    //parse and check minimum global size
					if(glowerend.getText() != null && !(glowerend.getText()).equals("")){
						glow = Integer.parseInt(glowerend.getText());	
						if(glow<HairpinLoop.MIN){
							throw new NumberFormatException("The substructure of this Bulge must contain at least\na HairpinLoop with "+(HairpinLoop.MIN)+" bases.");
						}
					}
					//parse and check maximum global size
					if(gupperend.getText() != null && !(gupperend.getText()).equals("")){
						gup = Integer.parseInt(gupperend.getText());
						if(gup<(HairpinLoop.MIN)){
							throw new NumberFormatException("The substructure of this Bulge must contain at least\na HairpinLoop with "+(HairpinLoop.MIN)+" bases.");
						}
						else if(gup < glow){
							throw new NumberFormatException("Upper bound must be larger than lower bound.");
						}
					}
				}
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
					return;
				}
				//no global size values given: default global size
				if(glow == 0 && gup == 0){
					gdeflen.doClick();
					return;
				}
				//store global size values
				if(glow != 0){
					bulge.setMinGlobalLength(glow);
				}
				else{
					bulge.removeGlobalMin();
				}
				if(gup != 0){
					bulge.setMaxGlobalLength(gup);
				}
				else{
					bulge.removeGlobalMax();
				}
			}
			bs.setSelected(false); 
			ds.unselectShape();
			ds.repaint();
			dispose();
		}
		//apply any changes made: do not close edit window!
		if(src == apply){
			//apply changes (same as above, except windows doesn't close 
			//and values are adjusted
		    
		    //no size restriction
			if(deflen.isSelected()){
			    //check which pane is active: Sequence : store motif/bp
				if(tabbedPane.isEnabledAt(2)){
					if(seqm.getText() != null && !seqmbuf.equals(seqm.getText())){
						if(!Iupac.isCorrectMotif(seqm.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							seqm.setText(null);
						}
						bulge.setLoopMotif(seqm.getText());
						seqmbuf = seqm.getText();
					}
					if(bp5tf.getText() != null && !bp5tfbuf.equals(bp5tf.getText())){
						if(!((bp5tf.getText()).length()==2 || (bp5tf.getText()).length()==0)){
							int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length at 5' end", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							bp5tf.setText(null);
						}
						else if(!Iupac.isCorrectMotif(bp5tf.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code at 5' end", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							bp5tf.setText(null);
						}
						else{
							bulge.setBasepair(bp5tf.getText(),0);
						}
						bp5tfbuf = bp5tf.getText();
					}
					if(bp3tf.getText() != null && !bp3tfbuf.equals(bp3tf.getText())){
						if(!((bp3tf.getText()).length()==2 || (bp3tf.getText()).length()==0)){
							int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length at 3' end", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							bp3tf.setText(null);
						}
						else if(!Iupac.isCorrectMotif(bp3tf.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code at 3' end", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							bp3tf.setText(null);
						}
						else{
							bulge.setBasepair(bp3tf.getText(),1);
						}
						bp3tfbuf = bp3tf.getText();
					}
				}
				//pane Size: remove any stored sizes
				else if(tabbedPane.isEnabledAt(0)){
					lowerend.setText(null);
					upperend.setText(null);
					t1.setText(null);
					t1buf = t1.getText();
					lowerendbuf = lowerend.getText();
					upperendbuf = upperend.getText();
					bulge.setToDefaultLength();
					bs.changeSize(bulge.getLength());
					sl1.setText("Enter a loop sequence motif:");
				}
			}
			//exact length
			if(exact.isSelected()){
				try{
				    //return to default length
					if(t1.getText() == null || (t1.getText()).equals("")){
						deflen.doClick();
						return;
					}
					//parse and check size value
					int l = Integer.parseInt(t1.getText());
					if(l<(Bulge.MIN-4)){
						throw new NumberFormatException("A Bulge loop must contain at least "+(Bulge.MIN-4)+" base.");
					}
					if(l>(Bulge.MAX-4) && !t1buf.equals(t1.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(Bulge.MAX-4)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					//Sequence pane: store motif/bps
					if(tabbedPane.isEnabledAt(2)){
						if(seqm.getText() != null && !seqmbuf.equals(seqm.getText())){
							if((seqm.getText()).length()>l){
								int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								seqm.setText(null);
							}
							else if(!Iupac.isCorrectMotif(seqm.getText())){
								int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								seqm.setText(null);
							}
							bulge.setLoopMotif(seqm.getText());
							seqmbuf = seqm.getText();
						}
						if(bp5tf.getText() != null && !bp5tfbuf.equals(bp5tf.getText())){
							if(!((bp5tf.getText()).length()==2 || (bp5tf.getText()).length()==0)){
								int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length at 5' end", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								bp5tf.setText(null);
							}
							else if(!Iupac.isCorrectMotif(bp5tf.getText())){
								int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code at 5' end", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								bp5tf.setText(null);
							}
							else{
								bulge.setBasepair(bp5tf.getText(),0);
							}
							bp5tfbuf = bp5tf.getText();
						}
						if(bp3tf.getText() != null && !bp3tfbuf.equals(bp3tf.getText())){
							if(!((bp3tf.getText()).length()==2 || (bp3tf.getText()).length()==0)){
								int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length at 3' end", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								bp3tf.setText(null);
							}
							else if(!Iupac.isCorrectMotif(bp3tf.getText())){
								int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code at 3' end", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								bp3tf.setText(null);
							}
							else{
								bulge.setBasepair(bp3tf.getText(),1);
							}
							bp3tfbuf = bp3tf.getText();
						}
					}
					//Size panel
					else if(tabbedPane.isEnabledAt(0)){
					    //check whether stored motif fits to new size restriction
						if(seqm.getText() != null){
							if((seqm.getText()).length()>l){
								int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								else{
									seqm.setText(null);
									bulge.setLoopMotif(null);
									seqmbuf = seqm.getText();
								}
							}
						}
						lowerend.setText(null);
						upperend.setText(null);
						t1buf = t1.getText();
						lowerendbuf = lowerend.getText();
						upperendbuf = upperend.getText();
						bulge.setLength(l+4);
						bs.changeSize(bulge.getLength());
						sl1.setText("Enter a loop sequence motif:");
					}
				}
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
				}
			}
			//range values
			else if(range.isSelected()){
				int low=0;
				int up=0;
				try{
				    //parse and check minimum
					if(lowerend.getText() != null && !(lowerend.getText()).equals("")){
						low = Integer.parseInt(lowerend.getText());	
						if(low<(Bulge.MIN-4)){
							throw new NumberFormatException("A Bulge loop must contain at least "+(Bulge.MIN-4)+" base.");
						}
						if(low>(Bulge.MAX-4) && !lowerendbuf.equals(lowerend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(Bulge.MAX-4)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
					}
					//parse and check maximum
					if(upperend.getText() != null && !(upperend.getText()).equals("")){
						up = Integer.parseInt(upperend.getText());
						if(up<(Bulge.MIN-4)){
							throw new NumberFormatException("A Bulge loop must contain at least "+(Bulge.MIN-4)+" base.");
						}
						if(up>(Bulge.MAX-4) && !upperendbuf.equals(upperend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(Bulge.MAX-4)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
						else if(up < low){
							throw new NumberFormatException("Upper bound must be larger than lower bound.");
						}
					}
				}
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
					return;
				}
				//no values given: return to default size
				if(low == 0 && up == 0){
					deflen.doClick();
					return;
				}
				//Sequence pane: store motif/bps
				if(tabbedPane.isEnabledAt(2)){
					if(seqm.getText() != null && !seqmbuf.equals(seqm.getText())){
						if(up != 0 && (seqm.getText()).length()>up){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							seqm.setText(null);
						}
						else if(!Iupac.isCorrectMotif(seqm.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							seqm.setText(null);
						}
						bulge.setLoopMotif(seqm.getText());
						seqmbuf = seqm.getText();
					}
					if(bp5tf.getText() != null && !bp5tfbuf.equals(bp5tf.getText())){
						if(!((bp5tf.getText()).length()==2 || (bp5tf.getText()).length()==0)){
							int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length at 5' end", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							bp5tf.setText(null);
						}
						else if(!Iupac.isCorrectMotif(bp5tf.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code at 5' end", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							bp5tf.setText(null);
						}
						else{
							bulge.setBasepair(bp5tf.getText(),0);
						}
						bp5tfbuf = bp5tf.getText();
					}
					if(bp3tf.getText() != null && !bp3tfbuf.equals(bp3tf.getText())){
						if(!((bp3tf.getText()).length()==2 || (bp3tf.getText()).length()==0)){
							int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length at 3' end", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							bp3tf.setText(null);
						}
						else if(!Iupac.isCorrectMotif(bp3tf.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code at 3' end", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							bp3tf.setText(null);
						}
						else{
							bulge.setBasepair(bp3tf.getText(),1);
						}
						bp3tfbuf = bp3tf.getText();
					}
				}
				//Size pane
				else if(tabbedPane.isEnabledAt(0)){
				    //if motif is given: check whether it fits to new size restricion
					if(seqm.getText() != null){
						if(up != 0 && (seqm.getText()).length()>up){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							else{
								seqm.setText(null);
								bulge.setLoopMotif(null);
								seqmbuf = seqm.getText();
							}
						}
					}
					t1.setText(null);
					t1buf = t1.getText();
					lowerendbuf = lowerend.getText();
					upperendbuf = upperend.getText();
					//store size values
					if(low != 0){
						bulge.setMinLength(low+4);
					}
					else{
						bulge.setToDefaultMin();
					}
					if(up != 0){
						bulge.setMaxLength(up+4);
					}
					else{
						bulge.setToDefaultMax();
					}
					bs.changeSize(bulge.getMinLength(),bulge.getMaxLength());
					sl1.setText("Enter a loop sequence motif:");
				}
			}
			//Bulge location pane
			if(tabbedPane.isEnabledAt(1)){
			    //bulge location was changed: store and update shape field
			    if(changedloc){
			        bs.changeBulgeLoc();
			        bs.switchsides();
			        changedloc = false;
			        if(fivethree.isSelected()){
			            initial53 = true;
			        }
			        else{
			            initial53 = false;
			        }
			        ds.adjustShapeField();
			    }
			}
			if(tabbedPane.isEnabledAt(3)){
			    //default global length: no restriction
			    if(gdeflen.isSelected()){
			        bulge.removeGlobalLength();
			    }
			    //global size restriction
			    else if(grange.isSelected()){
			        int glow=0;
			        int gup=0;
			        try{
			            //parse and check minimum
			            if(glowerend.getText() != null && !(glowerend.getText()).equals("")){
			                glow = Integer.parseInt(glowerend.getText());	
			                if(glow<HairpinLoop.MIN){
			                    throw new NumberFormatException("The substructure of this Bulge must contain at least\na HairpinLoop with "+(HairpinLoop.MIN)+" bases.");
			                }
			            }
			            //parse and check maximum
			            if(gupperend.getText() != null && !(gupperend.getText()).equals("")){
			                gup = Integer.parseInt(gupperend.getText());
			                if(gup<(HairpinLoop.MIN)){
			                    throw new NumberFormatException("The substructure of this Bulge must contain at least\na HairpinLoop with "+(HairpinLoop.MIN)+" bases.");
			                }
			                else if(gup < glow){
			                    throw new NumberFormatException("Upper bound must be larger than lower bound.");
			                }
			            }
			        }
			        catch(NumberFormatException nfe){
			            JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
			            return;
			        }
			        //no values given: return to no global restriction
			        if(glow == 0 && gup == 0){
			            gdeflen.doClick();
			            return;
			        }
			        //store global size values
			        if(glow != 0){
			            bulge.setMinGlobalLength(glow);
			        }
			        else{
			            bulge.removeGlobalMin();
			        }
			        if(gup != 0){
			            bulge.setMaxGlobalLength(gup);
			        }
			        else{
			            bulge.removeGlobalMax();
			        }
			    }
			}
			apply.setEnabled(false);
			//enable all panes
			tabbedPane.setEnabledAt(0,true);
			tabbedPane.setEnabledAt(1,true);
			tabbedPane.setEnabledAt(2,true);
			tabbedPane.setEnabledAt(3,true);
			ds.repaint();
		}
		//do nothing and close window
		if(src == cancel){
			//no changes
			bs.setSelected(false);
			ds.unselectShape();
			ds.repaint();
			dispose();
		}
	}

	/**
	 * Listener for text input fields
	 *
	 * @param ce the CaretEvent which ocurred
	 */
	public void caretUpdate(CaretEvent ce){
		Object src = ce.getSource();
		if(src == t1){
			if(!t1buf.equals(t1.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(2,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(lowerendbuf.equals(lowerend.getText()) && upperendbuf.equals(upperend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(1,true);
				tabbedPane.setEnabledAt(2,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == lowerend){
			if(!lowerendbuf.equals(lowerend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(2,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(t1buf.equals(t1.getText()) && upperendbuf.equals(upperend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(1,true);
				tabbedPane.setEnabledAt(2,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == upperend){
			if(!upperendbuf.equals(upperend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(2,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(lowerendbuf.equals(lowerend.getText()) && t1buf.equals(t1.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(1,true);
				tabbedPane.setEnabledAt(2,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == seqm){
			if(!seqmbuf.equals(seqm.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(bp5tfbuf.equals(bp5tf.getText()) && bp3tfbuf.equals(bp3tf.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(1,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == bp5tf){
			if(!bp5tfbuf.equals(bp5tf.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(seqmbuf.equals(seqm.getText()) && bp3tfbuf.equals(bp3tf.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(1,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == bp3tf){
			if(!bp3tfbuf.equals(bp3tf.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(seqmbuf.equals(seqm.getText()) && bp5tfbuf.equals(bp5tf.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(1,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == glowerend){
			if(!glowerendbuf.equals(glowerend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(2,false);
				tabbedPane.setEnabledAt(0,false);
			}
			else if(gupperendbuf.equals(gupperend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(1,true);
				tabbedPane.setEnabledAt(2,true);
				tabbedPane.setEnabledAt(0,true);
			}
		}
		if(src == gupperend){
			if(!gupperendbuf.equals(gupperend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(2,false);
				tabbedPane.setEnabledAt(0,false);
			}
			else if(glowerendbuf.equals(glowerend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(1,true);
				tabbedPane.setEnabledAt(2,true);
				tabbedPane.setEnabledAt(0,true);
			}
		}
	}
	
	public void dispose(){
	    bs.editClosed();
	    super.dispose();
	}
} 
