package rnaeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
 * This class defines the user interface for editing a ClosedStruct element
 *
 * @author Janina Reeder
 */
public class PseudoEdit extends JFrame implements ActionListener, CaretListener{
	
	private static final long serialVersionUID = 1220256870405335075L;
    private Pseudoknot pknot;
	private PseudoShape ps;
	private JPanel pane;
	private JTabbedPane tabbedPane;
	private JRadioButton deflen, range;
	private JRadioButton deflen2, exact2, range2, deflen3, exact3, range3;
	private JRadioButton deflen4, exact4, range4, deflen5, exact5, range5;
	private JRadioButton deflen6, exact6, range6;
	private JCheckBox folding4, folding5, folding6;
	private JButton ok, apply, cancel;
	private JLabel l2,l3;
	private JTextField lowerend, upperend;
	private JLabel l5,l6,l7,l8,l9,l10;
	private JLabel l11,l12,l13,l14,l15,l16,l17,l18,l19;
	private JTextField t2,lowerend2, upperend2, t3, lowerend3, upperend3;
	private JTextField t4,lowerend4, upperend4, t5, lowerend5, upperend5;
	private JTextField t6,lowerend6, upperend6;
	private String t2buf,lowerendbuf2, upperendbuf2, t3buf, lowerendbuf3, upperendbuf3;
	private String t4buf,lowerendbuf4, upperendbuf4, t5buf, lowerendbuf5, upperendbuf5;
	private String t6buf,lowerendbuf6, upperendbuf6;
	private String lowerendbuf, upperendbuf;
	private DrawingSurface ds;
	private boolean orientation;
	
	/**
	 * Constructor for the ClosedStructEdit GUI
	 *
	 * @param cs the parent ClosedStructShape
	 * @param ds the parent DrawingSurface
	 * @param type determines which tab is shown at first
	 */
	public PseudoEdit(PseudoShape ps, DrawingSurface ds, int type){
		super("Pseudoknot Options");
		
		this.ps = ps;
		this.pknot = ps.getPseudo();
		this.ds = ds;
		this.orientation = pknot.getOrientation();
		
		JFrame.setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		setContentPane(makeContentPane());
		setResizable(false);
		pack();
		setVisible(true);
	}
	
	/**
	 * main method that generates the gui
	 *
	 * @return the container of the GUI
	 */
	public Container makeContentPane(){
		pane = new JPanel();
		//pane.setSize(new Dimension(300,200));
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		//change scroll pane layout (top, left) here!
		tabbedPane = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);
		
		//Global SIZE PANEL
		JPanel card1 = new JPanel();
		
		ButtonGroup bg = new ButtonGroup();

		JPanel defaultpanel = new JPanel();
		deflen = new JRadioButton("No size restriction");
		deflen.setToolTipText("Unrestricted Pseudoknot contains at least 2 bp and connecting loops");
		
		JPanel rangepanel = new JPanel();
		range = new JRadioButton("Size range");
		range.setToolTipText("Specify a global size range for the Pseudoknot");
		l2 = new JLabel("Minimum number of bases:  ");
		l2.setToolTipText("Specify the minimum number of bases contained in the building block");
		l3 = new JLabel("Maximum number of bases: ");
		l3.setToolTipText("Specify the maximum number of bases contained in the building block");
		JPanel rangevaluepanel1 = new JPanel();
		JPanel rangevaluepanel2 = new JPanel();

		if(pknot.getIsDefaultLength()){
			lowerend = new JTextField(3);
			upperend = new JTextField(3);
			l2.setEnabled(false);
			lowerend.setEnabled(false);
			l3.setEnabled(false);
			upperend.setEnabled(false);
			deflen.setSelected(true);
			range.setSelected(false);
		}
		else{
			lowerend = new JTextField(3);
			upperend = new JTextField(3);
			if(!pknot.getIsDefaultMin()){
				lowerend.setText(String.valueOf(pknot.getMinLength()));
			}
			if(!pknot.getIsDefaultMax()){
				upperend.setText(String.valueOf(pknot.getMaxLength()));
			}
			range.setSelected(true);
			l2.setEnabled(true);
			lowerend.setEnabled(true);
			l3.setEnabled(true);
			upperend.setEnabled(true);
		}
		
		defaultpanel.setLayout(new BoxLayout(defaultpanel, BoxLayout.PAGE_AXIS));
		deflen.setAlignmentX(Component.LEFT_ALIGNMENT);
		deflen.addActionListener(this);
		defaultpanel.add(deflen);
		
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
		bg.add(range);
		

		defaultpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangepanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		card1.setLayout(new BoxLayout(card1,BoxLayout.PAGE_AXIS));
		card1.add(defaultpanel);
		card1.add(Box.createRigidArea(new Dimension(0,10)));
		card1.add(rangepanel);
		card1.add(Box.createRigidArea(new Dimension(0,10)));
		
		//END OF GLOBAL SIZE PANEL
		
		//pseudoknot1		SIZE PANEL
		JPanel card2 = new JPanel();
		
		ButtonGroup bg2 = new ButtonGroup();
		
		JPanel defaultpanel2 = new JPanel();
		deflen2 = new JRadioButton("No size restriction");
		deflen2.setToolTipText("A stem within the pseudoknot contains at least 1 basepair");
		
		JPanel exactpanel2 = new JPanel();
		exact2 = new JRadioButton("Exact size");
		exact2.setToolTipText("Specify an exact size for the pseudoknot stem");
		JPanel exactnumpanel2 = new JPanel();
		l5 = new JLabel("Number of basepairs: ");
		l5.setToolTipText("Give the exact number of basepairs for the pseudoknot stem");
		
		JPanel rangepanel2 = new JPanel();
		range2 = new JRadioButton("Size range");
		range2.setToolTipText("Specify a size range for the pseudoknot stem");
		l6 = new JLabel("Minimum number of basepairs:  ");
		l6.setToolTipText("Give a minimum number of basepairs for the pseudoknot stem");
		l7 = new JLabel("Maximum number of basepairs: ");
		l7.setToolTipText("Give a maximum number of basepairs for the pseudoknot stem");
		JPanel rangevaluepanel3 = new JPanel();
		JPanel rangevaluepanel4 = new JPanel();
		
		if(pknot.isStem1default()){
			t2 = new JTextField(3);
			lowerend2 = new JTextField(3);
			upperend2 = new JTextField(3);
			l5.setEnabled(false);
			t2.setEnabled(false);
			l6.setEnabled(false);
			lowerend2.setEnabled(false);
			l7.setEnabled(false);
			upperend2.setEnabled(false);
			deflen2.setSelected(true);
			exact2.setSelected(false);
			range2.setSelected(false);
		}
		else if(pknot.isStem1exact()){
			t2 = new JTextField(3);
			if(pknot.getStem1len() > 0){
			    t2.setText(String.valueOf(pknot.getStem1len()));
			}
			lowerend2 = new JTextField(3);
			upperend2 = new JTextField(3);
			deflen2.setSelected(false);
			exact2.setSelected(true);
			range2.setSelected(false);
			l5.setEnabled(true);
			t2.setEnabled(true);
			l6.setEnabled(false);
			lowerend2.setEnabled(false);
			l7.setEnabled(false);
			upperend2.setEnabled(false);
		}
		else{
			t2 = new JTextField(3);
			lowerend2 = new JTextField(3);
			upperend2 = new JTextField(3);
			if(pknot.getStem1min() != -1){
				lowerend2.setText(String.valueOf(pknot.getStem1min()));
			}
			if(pknot.getStem1max() != -1){
				upperend2.setText(String.valueOf(pknot.getStem1max()));
			}
			exact2.setSelected(false);
			range2.setSelected(true);
			l5.setEnabled(false);
			t2.setEnabled(false);
			l6.setEnabled(true);
			lowerend2.setEnabled(true);
			l7.setEnabled(true);
			upperend2.setEnabled(true);
		}
		
		JPanel labelpanel2 = new JPanel();
		JLabel info2 = new JLabel("   Stem1 refers to first stem from 5' end                                                    ");
		labelpanel2.setLayout(new BoxLayout(labelpanel2, BoxLayout.PAGE_AXIS));
		info2.setAlignmentX(Component.LEFT_ALIGNMENT);
		labelpanel2.add(info2);
		
		defaultpanel2.setLayout(new BoxLayout(defaultpanel2, BoxLayout.PAGE_AXIS));
		deflen2.setAlignmentX(Component.LEFT_ALIGNMENT);
		deflen2.addActionListener(this);
		defaultpanel2.add(deflen2);
		
		exactpanel2.setLayout(new BoxLayout(exactpanel2, BoxLayout.PAGE_AXIS));
		exact2.setAlignmentX(Component.LEFT_ALIGNMENT);
		exact2.addActionListener(this);
		exactpanel2.add(exact2);
		
		exactnumpanel2.setLayout(new BoxLayout(exactnumpanel2, BoxLayout.LINE_AXIS));
		exactnumpanel2.add(Box.createRigidArea(new Dimension(20,0)));
		exactnumpanel2.add(l5);
		
		t2.setMaximumSize(t2.getPreferredSize());
		t2.addCaretListener(this);
		t2buf = t2.getText();
		exactnumpanel2.add(t2);
		exactnumpanel2.add(Box.createHorizontalGlue());
		exactnumpanel2.setAlignmentX(Component.LEFT_ALIGNMENT);
		exactpanel2.add(exactnumpanel2);
		
		
		rangepanel2.setLayout(new BoxLayout(rangepanel2, BoxLayout.PAGE_AXIS));
		range2.setAlignmentX(Component.LEFT_ALIGNMENT);
		range2.addActionListener(this);
		rangepanel2.add(range2);
		
		lowerend2.setMaximumSize(lowerend2.getPreferredSize());
		lowerend2.addCaretListener(this);
		lowerendbuf2 = lowerend2.getText();
		upperend2.setMaximumSize(upperend2.getPreferredSize());
		upperend2.addCaretListener(this);
		upperendbuf2 = upperend2.getText();
		rangevaluepanel3.setLayout(new BoxLayout(rangevaluepanel3, BoxLayout.LINE_AXIS));
		rangevaluepanel3.add(Box.createRigidArea(new Dimension(20,0)));
		rangevaluepanel3.add(l6);
		rangevaluepanel3.add(lowerend2);
		rangevaluepanel3.add(Box.createHorizontalGlue());
		rangevaluepanel3.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangevaluepanel4.setLayout(new BoxLayout(rangevaluepanel4, BoxLayout.LINE_AXIS));
		rangevaluepanel4.add(Box.createRigidArea(new Dimension(20,0)));
		rangevaluepanel4.add(l7);
		rangevaluepanel4.add(upperend2);
		rangevaluepanel4.add(Box.createHorizontalGlue());
		rangevaluepanel4.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangepanel2.add(rangevaluepanel3);
		rangepanel2.add(rangevaluepanel4);
		
		bg2.add(deflen2);
		bg2.add(exact2);
		bg2.add(range2);
		
		defaultpanel2.setAlignmentX(Component.LEFT_ALIGNMENT);
		exactpanel2.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangepanel2.setAlignmentX(Component.LEFT_ALIGNMENT);
		card2.setLayout(new BoxLayout(card2,BoxLayout.PAGE_AXIS));
		card2.add(defaultpanel2);
		card2.add(Box.createRigidArea(new Dimension(0,10)));
		card2.add(exactpanel2);
		card2.add(Box.createRigidArea(new Dimension(0,10)));
		card2.add(rangepanel2);
		card2.add(Box.createRigidArea(new Dimension(0,20)));
		//card2.add(labelpanel2);
		//card2.add(Box.createRigidArea(new Dimension(0,10)));
		
		
		PseudoPanel stem1view = new PseudoPanel(0);
		stem1view.setAlignmentX(Component.LEFT_ALIGNMENT);
		JPanel stem1panel = new JPanel();
		stem1panel.setLayout(new BoxLayout(stem1panel,BoxLayout.LINE_AXIS));
		card2.setAlignmentX(Component.LEFT_ALIGNMENT);
		card2.setAlignmentY(Component.TOP_ALIGNMENT);
		stem1panel.add(card2);
		stem1panel.add(Box.createRigidArea(new Dimension(10,0)));
		stem1panel.add(stem1view);
		stem1panel.add(Box.createHorizontalGlue());
		
		//END OF pseudoknot 1 PANEL
		
		//pseudoknot 2 PANEL
		JPanel card3 = new JPanel();
		ButtonGroup bg3 = new ButtonGroup();
		
		JPanel defaultpanel3 = new JPanel();
		deflen3 = new JRadioButton("No size restriction");
		deflen3.setToolTipText("Unrestricted pseudoknot contains at least ???");
		
		
		JPanel exactpanel3 = new JPanel();
		exact3 = new JRadioButton("Exact size");
		exact3.setToolTipText("Specify an exact size for the pseudoknot");
		JPanel exactnumpanel3 = new JPanel();
		l8 = new JLabel("Number of basepairs: ");
		l8.setToolTipText("Give the exact number of basepairs for the pseudoknot");
		
		JPanel rangepanel3 = new JPanel();
		range3 = new JRadioButton("Size range");
		range3.setToolTipText("Specify a size range for the pseudoknot");
		l9 = new JLabel("Minimum number of basepairs:  ");
		l9.setToolTipText("Give a minimum number of basepairs for the pseudoknot");
		l10 = new JLabel("Maximum number of basepairs: ");
		l10.setToolTipText("Give a maximum number of basepairs for the pseudoknot");
		JPanel rangevaluepanel5 = new JPanel();
		JPanel rangevaluepanel6 = new JPanel();
		
		if(pknot.isStem2default()){
			t3 = new JTextField(3);
			lowerend3 = new JTextField(3);
			upperend3 = new JTextField(3);
			l8.setEnabled(false);
			t3.setEnabled(false);
			l9.setEnabled(false);
			lowerend3.setEnabled(false);
			l10.setEnabled(false);
			upperend3.setEnabled(false);
			deflen3.setSelected(true);
			exact3.setSelected(false);
			range3.setSelected(false);
		}
		else if(pknot.isStem2exact()){
			t3 = new JTextField(3);
			if(pknot.getStem2len() > 0){
			    t3.setText(String.valueOf(pknot.getStem2len()));
			}
			lowerend3 = new JTextField(3);
			upperend3 = new JTextField(3);
			deflen3.setSelected(false);
			exact3.setSelected(true);
			range3.setSelected(false);
			l8.setEnabled(true);
			t3.setEnabled(true);
			l9.setEnabled(false);
			lowerend3.setEnabled(false);
			l10.setEnabled(false);
			upperend3.setEnabled(false);
		}
		else{
			t3 = new JTextField(3);
			lowerend3 = new JTextField(3);
			upperend3 = new JTextField(3);
			if(pknot.getStem2min() != -1){
				lowerend3.setText(String.valueOf(pknot.getStem2min()));
			}
			if(pknot.getStem2max() != -1){
				upperend3.setText(String.valueOf(pknot.getStem2max()));
			}
			exact3.setSelected(false);
			range3.setSelected(true);
			l8.setEnabled(false);
			t3.setEnabled(false);
			l9.setEnabled(true);
			lowerend3.setEnabled(true);
			l10.setEnabled(true);
			upperend3.setEnabled(true);
		}
		JPanel labelpanel3 = new JPanel();
		JLabel info3 = new JLabel("   Stem2 refers to second stem from 5' end                                                    ");
		labelpanel3.setLayout(new BoxLayout(labelpanel3, BoxLayout.PAGE_AXIS));
		info3.setAlignmentX(Component.LEFT_ALIGNMENT);
		labelpanel3.add(info3);
		
		defaultpanel3.setLayout(new BoxLayout(defaultpanel3, BoxLayout.PAGE_AXIS));
		deflen3.setAlignmentX(Component.LEFT_ALIGNMENT);
		deflen3.addActionListener(this);
		defaultpanel3.add(deflen3);
		
		exactpanel3.setLayout(new BoxLayout(exactpanel3, BoxLayout.PAGE_AXIS));
		exact3.setAlignmentX(Component.LEFT_ALIGNMENT);
		exact3.addActionListener(this);
		exactpanel3.add(exact3);
		
		exactnumpanel3.setLayout(new BoxLayout(exactnumpanel3, BoxLayout.LINE_AXIS));
		exactnumpanel3.add(Box.createRigidArea(new Dimension(20,0)));
		exactnumpanel3.add(l8);
		
		t3.setMaximumSize(t3.getPreferredSize());
		t3.addCaretListener(this);
		t3buf = t3.getText();
		exactnumpanel3.add(t3);
		exactnumpanel3.add(Box.createHorizontalGlue());
		exactnumpanel3.setAlignmentX(Component.LEFT_ALIGNMENT);
		exactpanel3.add(exactnumpanel3);
		
		
		rangepanel3.setLayout(new BoxLayout(rangepanel3, BoxLayout.PAGE_AXIS));
		range3.setAlignmentX(Component.LEFT_ALIGNMENT);
		range3.addActionListener(this);
		rangepanel3.add(range3);
		
		lowerend3.setMaximumSize(lowerend3.getPreferredSize());
		lowerend3.addCaretListener(this);
		lowerendbuf3 = lowerend3.getText();
		upperend3.setMaximumSize(upperend3.getPreferredSize());
		upperend3.addCaretListener(this);
		upperendbuf3 = upperend3.getText();
		rangevaluepanel5.setLayout(new BoxLayout(rangevaluepanel5, BoxLayout.LINE_AXIS));
		rangevaluepanel5.add(Box.createRigidArea(new Dimension(20,0)));
		rangevaluepanel5.add(l9);
		rangevaluepanel5.add(lowerend3);
		rangevaluepanel5.add(Box.createHorizontalGlue());
		rangevaluepanel5.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangevaluepanel6.setLayout(new BoxLayout(rangevaluepanel6, BoxLayout.LINE_AXIS));
		rangevaluepanel6.add(Box.createRigidArea(new Dimension(20,0)));
		rangevaluepanel6.add(l10);
		rangevaluepanel6.add(upperend3);
		rangevaluepanel6.add(Box.createHorizontalGlue());
		rangevaluepanel6.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangepanel3.add(rangevaluepanel5);
		rangepanel3.add(rangevaluepanel6);
		
		bg3.add(deflen3);
		bg3.add(exact3);
		bg3.add(range3);
		
		defaultpanel3.setAlignmentX(Component.LEFT_ALIGNMENT);
		exactpanel3.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangepanel3.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		card3.setLayout(new BoxLayout(card3,BoxLayout.PAGE_AXIS));
		card3.add(defaultpanel3);
		card3.add(Box.createRigidArea(new Dimension(0,10)));
		card3.add(exactpanel3);
		card3.add(Box.createRigidArea(new Dimension(0,10)));
		card3.add(rangepanel3);
		card3.add(Box.createRigidArea(new Dimension(0,20)));
		//card3.add(labelpanel3);
		//card3.add(Box.createRigidArea(new Dimension(0,10)));
		
		
		PseudoPanel stem2view = new PseudoPanel(2);
		stem2view.setAlignmentX(Component.LEFT_ALIGNMENT);
		JPanel stem2panel = new JPanel();
		stem2panel.setLayout(new BoxLayout(stem2panel,BoxLayout.LINE_AXIS));
		card3.setAlignmentX(Component.LEFT_ALIGNMENT);
		card3.setAlignmentY(Component.TOP_ALIGNMENT);
		stem2panel.add(card3);
		stem2panel.add(Box.createRigidArea(new Dimension(10,0)));
		stem2panel.add(stem2view);
		stem2panel.add(Box.createHorizontalGlue());
		
		//		LOOP 1	 PANEL
		JPanel card4 = new JPanel();
		ButtonGroup bg4 = new ButtonGroup();
		
		JPanel defaultpanel4 = new JPanel();
		deflen4 = new JRadioButton("No size restriction");
		deflen4.setToolTipText("Unrestricted pseudoknot contains at least ????");
		folding4 = new JCheckBox("Allow internal folding of loop");
		
		JPanel foldpanel4 = new JPanel();
		
		JPanel exactpanel4 = new JPanel();
		exact4 = new JRadioButton("Exact size");
		exact4.setToolTipText("Specify an exact size for the pseudoknot");
		JPanel exactnumpanel4 = new JPanel();
		l11 = new JLabel("Number of basepairs: ");
		l11.setToolTipText("Give the exact number of basepairs for the pseudoknot");
		
		JPanel rangepanel4 = new JPanel();
		range4 = new JRadioButton("Size range");
		range4.setToolTipText("Specify a size range for the pseudoknot");
		l12 = new JLabel("Minimum number of basepairs:  ");
		l12.setToolTipText("Give a minimum number of basepairs for the pseudoknot");
		l13 = new JLabel("Maximum number of basepairs: ");
		l13.setToolTipText("Give a maximum number of basepairs for the pseudoknot");
		JPanel rangevaluepanel7 = new JPanel();
		JPanel rangevaluepanel8 = new JPanel();
		
		if(pknot.isLoop1default()){
			t4 = new JTextField(3);
			lowerend4 = new JTextField(3);
			upperend4 = new JTextField(3);
			l11.setEnabled(false);
			t4.setEnabled(false);
			l12.setEnabled(false);
			lowerend4.setEnabled(false);
			l13.setEnabled(false);
			upperend4.setEnabled(false);
			deflen4.setSelected(true);
			exact4.setSelected(false);
			range4.setSelected(false);
			folding4.setEnabled(true);
		}
		else if(pknot.isLoop1exact()){
			t4 = new JTextField(3);
			if(pknot.getLoop1len() > 0){
			    t4.setText(String.valueOf(pknot.getLoop1len()));
			}
			lowerend4 = new JTextField(3);
			upperend4 = new JTextField(3);
			deflen4.setSelected(false);
			exact4.setSelected(true);
			range4.setSelected(false);
			l11.setEnabled(true);
			t4.setEnabled(true);
			l12.setEnabled(false);
			lowerend4.setEnabled(false);
			l13.setEnabled(false);
			upperend4.setEnabled(false);
			folding4.setEnabled(false);
		}
		else{
			t4 = new JTextField(3);
			lowerend4 = new JTextField(3);
			upperend4 = new JTextField(3);
			if(pknot.getLoop1min() != -1){
				lowerend4.setText(String.valueOf(pknot.getLoop1min()));
			}
			if(pknot.getLoop1max() != -1){
				upperend4.setText(String.valueOf(pknot.getLoop1max()));
			}
			exact4.setSelected(false);
			range4.setSelected(true);
			l11.setEnabled(false);
			t4.setEnabled(false);
			l12.setEnabled(true);
			lowerend4.setEnabled(true);
			l13.setEnabled(true);
			upperend4.setEnabled(true);
			folding4.setEnabled(false);
		}
		folding4.setSelected(!pknot.isLoop1straight());
		folding4.addActionListener(this);
		
		JPanel labelpanel4 = new JPanel();
		JLabel info4 = new JLabel("   Loop1 refers to first loop connecting Stem1 and Stem2                         ");
		labelpanel4.setLayout(new BoxLayout(labelpanel4, BoxLayout.PAGE_AXIS));
		info4.setAlignmentX(Component.LEFT_ALIGNMENT);
		labelpanel4.add(info4);
		
		defaultpanel4.setLayout(new BoxLayout(defaultpanel4, BoxLayout.PAGE_AXIS));
		deflen4.setAlignmentX(Component.LEFT_ALIGNMENT);
		deflen4.addActionListener(this);
		defaultpanel4.add(deflen4);
		
		foldpanel4.setLayout(new BoxLayout(foldpanel4, BoxLayout.LINE_AXIS));
		foldpanel4.add(Box.createRigidArea(new Dimension(20,0)));
		foldpanel4.add(folding4);
		foldpanel4.add(Box.createHorizontalGlue());
		foldpanel4.setAlignmentX(Component.LEFT_ALIGNMENT);
		defaultpanel4.add(foldpanel4);
		
		exactpanel4.setLayout(new BoxLayout(exactpanel4, BoxLayout.PAGE_AXIS));
		exact4.setAlignmentX(Component.LEFT_ALIGNMENT);
		exact4.addActionListener(this);
		exactpanel4.add(exact4);
		
		exactnumpanel4.setLayout(new BoxLayout(exactnumpanel4, BoxLayout.LINE_AXIS));
		exactnumpanel4.add(Box.createRigidArea(new Dimension(20,0)));
		exactnumpanel4.add(l11);
		
		t4.setMaximumSize(t4.getPreferredSize());
		t4.addCaretListener(this);
		t4buf = t4.getText();
		exactnumpanel4.add(t4);
		exactnumpanel4.add(Box.createHorizontalGlue());
		exactnumpanel4.setAlignmentX(Component.LEFT_ALIGNMENT);
		exactpanel4.add(exactnumpanel4);
		
		
		rangepanel4.setLayout(new BoxLayout(rangepanel4, BoxLayout.PAGE_AXIS));
		range4.setAlignmentX(Component.LEFT_ALIGNMENT);
		range4.addActionListener(this);
		rangepanel4.add(range4);
		
		lowerend4.setMaximumSize(lowerend4.getPreferredSize());
		lowerend4.addCaretListener(this);
		lowerendbuf4 = lowerend4.getText();
		upperend4.setMaximumSize(upperend4.getPreferredSize());
		upperend4.addCaretListener(this);
		upperendbuf4 = upperend4.getText();
		rangevaluepanel7.setLayout(new BoxLayout(rangevaluepanel7, BoxLayout.LINE_AXIS));
		rangevaluepanel7.add(Box.createRigidArea(new Dimension(20,0)));
		rangevaluepanel7.add(l12);
		rangevaluepanel7.add(lowerend4);
		rangevaluepanel7.add(Box.createHorizontalGlue());
		rangevaluepanel7.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangevaluepanel8.setLayout(new BoxLayout(rangevaluepanel8, BoxLayout.LINE_AXIS));
		rangevaluepanel8.add(Box.createRigidArea(new Dimension(20,0)));
		rangevaluepanel8.add(l13);
		rangevaluepanel8.add(upperend4);
		rangevaluepanel8.add(Box.createHorizontalGlue());
		rangevaluepanel8.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangepanel4.add(rangevaluepanel7);
		rangepanel4.add(rangevaluepanel8);
		
		bg4.add(deflen4);
		bg4.add(exact4);
		bg4.add(range4);
		
		defaultpanel4.setAlignmentX(Component.LEFT_ALIGNMENT);
		exactpanel4.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangepanel4.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		card4.setLayout(new BoxLayout(card4,BoxLayout.PAGE_AXIS));
		card4.add(defaultpanel4);
		card4.add(Box.createRigidArea(new Dimension(0,10)));
		card4.add(exactpanel4);
		card4.add(Box.createRigidArea(new Dimension(0,10)));
		card4.add(rangepanel4);
		card4.add(Box.createRigidArea(new Dimension(0,20)));
		//card4.add(labelpanel4);
		//card4.add(Box.createRigidArea(new Dimension(0,10)));
	
		
		PseudoPanel loop1view = new PseudoPanel(1);
		loop1view.setAlignmentX(Component.LEFT_ALIGNMENT);
		JPanel loop1panel = new JPanel();
		loop1panel.setLayout(new BoxLayout(loop1panel,BoxLayout.LINE_AXIS));
		card4.setAlignmentX(Component.LEFT_ALIGNMENT);
		card4.setAlignmentY(Component.TOP_ALIGNMENT);
		loop1panel.add(card4);
		loop1panel.add(Box.createRigidArea(new Dimension(10,0)));
		loop1panel.add(loop1view);
		loop1panel.add(Box.createHorizontalGlue());
		
		//		LOOP 2	 PANEL
		JPanel card5 = new JPanel();
		ButtonGroup bg5 = new ButtonGroup();
		
		JPanel defaultpanel5 = new JPanel();
		deflen5 = new JRadioButton("No size restriction");
		deflen5.setToolTipText("Unrestricted pseudoknot contains at least ????");
		folding5 = new JCheckBox("Allow internal folding of loop");
		
		JPanel foldpanel5 = new JPanel();
		
		JPanel exactpanel5 = new JPanel();
		exact5 = new JRadioButton("Exact size");
		exact5.setToolTipText("Specify an exact size for the pseudoknot");
		JPanel exactnumpanel5 = new JPanel();
		l14 = new JLabel("Number of basepairs: ");
		l14.setToolTipText("Give the exact number of basepairs for the pseudoknot");
		
		JPanel rangepanel5 = new JPanel();
		range5 = new JRadioButton("Size range");
		range5.setToolTipText("Specify a size range for the pseudoknot");
		l15 = new JLabel("Minimum number of basepairs:  ");
		l15.setToolTipText("Give a minimum number of basepairs for the pseudoknot");
		l16 = new JLabel("Maximum number of basepairs: ");
		l16.setToolTipText("Give a maximum number of basepairs for the pseudoknot");
		JPanel rangevaluepanel9 = new JPanel();
		JPanel rangevaluepanel10 = new JPanel();
		
		if(pknot.isLoop2default()){
			t5 = new JTextField(3);
			lowerend5 = new JTextField(3);
			upperend5 = new JTextField(3);
			l14.setEnabled(false);
			t5.setEnabled(false);
			l15.setEnabled(false);
			lowerend5.setEnabled(false);
			l16.setEnabled(false);
			upperend5.setEnabled(false);
			deflen5.setSelected(true);
			exact5.setSelected(false);
			range5.setSelected(false);
			folding5.setEnabled(true);
		}
		else if(pknot.isLoop2exact()){
			t5 = new JTextField(3);
			if(pknot.getLoop2len() > 0){
			    t5.setText(String.valueOf(pknot.getLoop2len()));
			}
			lowerend5 = new JTextField(3);
			upperend5 = new JTextField(3);
			deflen5.setSelected(false);
			exact5.setSelected(true);
			range5.setSelected(false);
			l14.setEnabled(true);
			t5.setEnabled(true);
			l15.setEnabled(false);
			lowerend5.setEnabled(false);
			l16.setEnabled(false);
			upperend5.setEnabled(false);
			folding5.setEnabled(false);
		}
		else{
			t5 = new JTextField(3);
			lowerend5 = new JTextField(3);
			upperend5 = new JTextField(3);
			if(pknot.getLoop2min() != -1){
				lowerend5.setText(String.valueOf(pknot.getLoop2min()));
			}
			if(pknot.getLoop2max() != -1){
				upperend5.setText(String.valueOf(pknot.getLoop2max()));
			}
			exact5.setSelected(false);
			range5.setSelected(true);
			l14.setEnabled(false);
			t5.setEnabled(false);
			l15.setEnabled(true);
			lowerend5.setEnabled(true);
			l16.setEnabled(true);
			upperend5.setEnabled(true);
			folding5.setEnabled(false);
		}
		folding5.setSelected(!pknot.isLoop2straight());
		folding5.addActionListener(this);
		
		JPanel labelpanel5 = new JPanel();
		JLabel info5 = new JLabel("   Loop2 refers to second loop connecting Stem2 and Stem1                         ");
		labelpanel5.setLayout(new BoxLayout(labelpanel5, BoxLayout.PAGE_AXIS));
		info5.setAlignmentX(Component.LEFT_ALIGNMENT);
		labelpanel5.add(info5);
		
		defaultpanel5.setLayout(new BoxLayout(defaultpanel5, BoxLayout.PAGE_AXIS));
		deflen5.setAlignmentX(Component.LEFT_ALIGNMENT);
		deflen5.addActionListener(this);
		defaultpanel5.add(deflen5);
		
		foldpanel5.setLayout(new BoxLayout(foldpanel5, BoxLayout.LINE_AXIS));
		foldpanel5.add(Box.createRigidArea(new Dimension(20,0)));
		foldpanel5.add(folding5);
		foldpanel5.add(Box.createHorizontalGlue());
		foldpanel5.setAlignmentX(Component.LEFT_ALIGNMENT);
		defaultpanel5.add(foldpanel5);
		
		exactpanel5.setLayout(new BoxLayout(exactpanel5, BoxLayout.PAGE_AXIS));
		exact5.setAlignmentX(Component.LEFT_ALIGNMENT);
		exact5.addActionListener(this);
		exactpanel5.add(exact5);
		
		exactnumpanel5.setLayout(new BoxLayout(exactnumpanel5, BoxLayout.LINE_AXIS));
		exactnumpanel5.add(Box.createRigidArea(new Dimension(20,0)));
		exactnumpanel5.add(l14);
		
		t5.setMaximumSize(t5.getPreferredSize());
		t5.addCaretListener(this);
		t5buf = t5.getText();
		exactnumpanel5.add(t5);
		exactnumpanel5.add(Box.createHorizontalGlue());
		exactnumpanel5.setAlignmentX(Component.LEFT_ALIGNMENT);
		exactpanel5.add(exactnumpanel5);
		
		
		rangepanel5.setLayout(new BoxLayout(rangepanel5, BoxLayout.PAGE_AXIS));
		range5.setAlignmentX(Component.LEFT_ALIGNMENT);
		range5.addActionListener(this);
		rangepanel5.add(range5);
		
		lowerend5.setMaximumSize(lowerend5.getPreferredSize());
		lowerend5.addCaretListener(this);
		lowerendbuf5 = lowerend5.getText();
		upperend5.setMaximumSize(upperend5.getPreferredSize());
		upperend5.addCaretListener(this);
		upperendbuf5 = upperend5.getText();
		rangevaluepanel9.setLayout(new BoxLayout(rangevaluepanel9, BoxLayout.LINE_AXIS));
		rangevaluepanel9.add(Box.createRigidArea(new Dimension(20,0)));
		rangevaluepanel9.add(l15);
		rangevaluepanel9.add(lowerend5);
		rangevaluepanel9.add(Box.createHorizontalGlue());
		rangevaluepanel9.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangevaluepanel10.setLayout(new BoxLayout(rangevaluepanel10, BoxLayout.LINE_AXIS));
		rangevaluepanel10.add(Box.createRigidArea(new Dimension(20,0)));
		rangevaluepanel10.add(l16);
		rangevaluepanel10.add(upperend5);
		rangevaluepanel10.add(Box.createHorizontalGlue());
		rangevaluepanel10.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangepanel5.add(rangevaluepanel9);
		rangepanel5.add(rangevaluepanel10);
		
		bg5.add(deflen5);
		bg5.add(exact5);
		bg5.add(range5);
		
		defaultpanel5.setAlignmentX(Component.LEFT_ALIGNMENT);
		exactpanel5.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangepanel5.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		card5.setLayout(new BoxLayout(card5,BoxLayout.PAGE_AXIS));
		card5.add(defaultpanel5);
		card5.add(Box.createRigidArea(new Dimension(0,10)));
		card5.add(exactpanel5);
		card5.add(Box.createRigidArea(new Dimension(0,10)));
		card5.add(rangepanel5);
		card5.add(Box.createRigidArea(new Dimension(0,20)));
		//card5.add(labelpanel5);
		//card5.add(Box.createRigidArea(new Dimension(0,10)));

		PseudoPanel loop2view = new PseudoPanel(3);
		loop2view.setAlignmentX(Component.LEFT_ALIGNMENT);
		JPanel loop2panel = new JPanel();
		loop2panel.setLayout(new BoxLayout(loop2panel,BoxLayout.LINE_AXIS));
		card5.setAlignmentX(Component.LEFT_ALIGNMENT);
		card5.setAlignmentY(Component.TOP_ALIGNMENT);
		loop2panel.add(card5);
		loop2panel.add(Box.createRigidArea(new Dimension(10,0)));
		loop2panel.add(loop2view);
		loop2panel.add(Box.createHorizontalGlue());
		
		//		LOOP 3	 PANEL
		JPanel card6 = new JPanel();
		ButtonGroup bg6 = new ButtonGroup();
		
		JPanel defaultpanel6 = new JPanel();
		deflen6 = new JRadioButton("No size restriction");
		deflen6.setToolTipText("Unrestricted pseudoknot contains at least ????");
		folding6 = new JCheckBox("Allow internal folding of loop");
		
		JPanel foldpanel6 = new JPanel();
		
		JPanel exactpanel6 = new JPanel();
		exact6 = new JRadioButton("Exact size");
		exact6.setToolTipText("Specify an exact size for the pseudoknot");
		JPanel exactnumpanel6 = new JPanel();
		l17 = new JLabel("Number of basepairs: ");
		l17.setToolTipText("Give the exact number of basepairs for the pseudoknot");
		
		JPanel rangepanel6 = new JPanel();
		range6 = new JRadioButton("Size range");
		range6.setToolTipText("Specify a size range for the pseudoknot");
		l18 = new JLabel("Minimum number of basepairs:  ");
		l18.setToolTipText("Give a minimum number of basepairs for the pseudoknot");
		l19 = new JLabel("Maximum number of basepairs: ");
		l19.setToolTipText("Give a maximum number of basepairs for the pseudoknot");
		JPanel rangevaluepanel11 = new JPanel();
		JPanel rangevaluepanel12 = new JPanel();
		
		if(pknot.isLoop3default()){
			t6 = new JTextField(3);
			lowerend6 = new JTextField(3);
			upperend6 = new JTextField(3);
			l17.setEnabled(false);
			t6.setEnabled(false);
			l18.setEnabled(false);
			lowerend6.setEnabled(false);
			l19.setEnabled(false);
			upperend6.setEnabled(false);
			deflen6.setSelected(true);
			exact6.setSelected(false);
			range6.setSelected(false);
			folding6.setEnabled(true);
		}
		else if(pknot.isLoop3exact()){
			t6 = new JTextField(3);
			if(pknot.getLoop3len() > 0){
			    t6.setText(String.valueOf(pknot.getLoop3len()));
			}
			lowerend6 = new JTextField(3);
			upperend6 = new JTextField(3);
			deflen6.setSelected(false);
			exact6.setSelected(true);
			range6.setSelected(false);
			l17.setEnabled(true);
			t6.setEnabled(true);
			l18.setEnabled(false);
			lowerend6.setEnabled(false);
			l19.setEnabled(false);
			upperend6.setEnabled(false);
			folding6.setEnabled(false);
		}
		else{
			t6 = new JTextField(3);
			lowerend6 = new JTextField(3);
			upperend6 = new JTextField(3);
			if(pknot.getLoop3min() != -1){
				lowerend6.setText(String.valueOf(pknot.getLoop3min()));
			}
			if(pknot.getLoop3max() != -1){
				upperend6.setText(String.valueOf(pknot.getLoop3max()));
			}
			exact6.setSelected(false);
			range6.setSelected(true);
			l17.setEnabled(false);
			t6.setEnabled(false);
			l18.setEnabled(true);
			lowerend6.setEnabled(true);
			l19.setEnabled(true);
			upperend6.setEnabled(true);
			folding6.setEnabled(false);
		}
		folding6.setSelected(!pknot.isLoop3straight());
		folding6.addActionListener(this);
		
		JPanel labelpanel6 = new JPanel();
		JLabel info6 = new JLabel("   Loop3 refers to third loop connecting Stem1 and Stem2                         ");
		labelpanel6.setLayout(new BoxLayout(labelpanel6, BoxLayout.PAGE_AXIS));
		info6.setAlignmentX(Component.LEFT_ALIGNMENT);
		labelpanel6.add(info6);
		
		defaultpanel6.setLayout(new BoxLayout(defaultpanel6, BoxLayout.PAGE_AXIS));
		deflen6.setAlignmentX(Component.LEFT_ALIGNMENT);
		deflen6.addActionListener(this);
		defaultpanel6.add(deflen6);
		
		foldpanel6.setLayout(new BoxLayout(foldpanel6, BoxLayout.LINE_AXIS));
		foldpanel6.add(Box.createRigidArea(new Dimension(20,0)));
		foldpanel6.add(folding6);
		foldpanel6.add(Box.createHorizontalGlue());
		foldpanel6.setAlignmentX(Component.LEFT_ALIGNMENT);
		defaultpanel6.add(foldpanel6);
		
		exactpanel6.setLayout(new BoxLayout(exactpanel6, BoxLayout.PAGE_AXIS));
		exact6.setAlignmentX(Component.LEFT_ALIGNMENT);
		exact6.addActionListener(this);
		exactpanel6.add(exact6);
		
		exactnumpanel6.setLayout(new BoxLayout(exactnumpanel6, BoxLayout.LINE_AXIS));
		exactnumpanel6.add(Box.createRigidArea(new Dimension(20,0)));
		exactnumpanel6.add(l17);
		
		t6.setMaximumSize(t6.getPreferredSize());
		t6.addCaretListener(this);
		t6buf = t6.getText();
		exactnumpanel6.add(t6);
		exactnumpanel6.add(Box.createHorizontalGlue());
		exactnumpanel6.setAlignmentX(Component.LEFT_ALIGNMENT);
		exactpanel6.add(exactnumpanel6);
		
		
		rangepanel6.setLayout(new BoxLayout(rangepanel6, BoxLayout.PAGE_AXIS));
		range6.setAlignmentX(Component.LEFT_ALIGNMENT);
		range6.addActionListener(this);
		rangepanel6.add(range6);
		
		lowerend6.setMaximumSize(lowerend6.getPreferredSize());
		lowerend6.addCaretListener(this);
		lowerendbuf6 = lowerend6.getText();
		upperend6.setMaximumSize(upperend6.getPreferredSize());
		upperend6.addCaretListener(this);
		upperendbuf6 = upperend6.getText();
		rangevaluepanel11.setLayout(new BoxLayout(rangevaluepanel11, BoxLayout.LINE_AXIS));
		rangevaluepanel11.add(Box.createRigidArea(new Dimension(20,0)));
		rangevaluepanel11.add(l18);
		rangevaluepanel11.add(lowerend6);
		rangevaluepanel11.add(Box.createHorizontalGlue());
		rangevaluepanel11.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangevaluepanel12.setLayout(new BoxLayout(rangevaluepanel12, BoxLayout.LINE_AXIS));
		rangevaluepanel12.add(Box.createRigidArea(new Dimension(20,0)));
		rangevaluepanel12.add(l19);
		rangevaluepanel12.add(upperend6);
		rangevaluepanel12.add(Box.createHorizontalGlue());
		rangevaluepanel12.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangepanel6.add(rangevaluepanel11);
		rangepanel6.add(rangevaluepanel12);
		
		bg6.add(deflen6);
		bg6.add(exact6);
		bg6.add(range6);
		
		defaultpanel6.setAlignmentX(Component.LEFT_ALIGNMENT);
		exactpanel6.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangepanel6.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		card6.setLayout(new BoxLayout(card6,BoxLayout.PAGE_AXIS));
		card6.add(defaultpanel6);
		card6.add(Box.createRigidArea(new Dimension(0,10)));
		card6.add(exactpanel6);
		card6.add(Box.createRigidArea(new Dimension(0,10)));
		card6.add(rangepanel6);
		card6.add(Box.createRigidArea(new Dimension(0,20)));
		//card6.add(labelpanel6);
		//card6.add(Box.createRigidArea(new Dimension(0,10)));

		PseudoPanel loop3view = new PseudoPanel(4);
		loop3view.setAlignmentX(Component.LEFT_ALIGNMENT);
		JPanel loop3panel = new JPanel();
		loop3panel.setLayout(new BoxLayout(loop3panel,BoxLayout.LINE_AXIS));
		card6.setAlignmentX(Component.LEFT_ALIGNMENT);
		card6.setAlignmentY(Component.TOP_ALIGNMENT);
		loop3panel.add(card6);
		loop3panel.add(Box.createRigidArea(new Dimension(10,0)));
		loop3panel.add(loop3view);
		loop3panel.add(Box.createHorizontalGlue());
		
		//BOTTOM BUTTONS
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
		buttonpanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		//END OF BOTTOM BUTTONs
		
		tabbedPane.addTab("Global Size", card1);
		tabbedPane.addTab("Stem1", stem1panel);
		tabbedPane.addTab("Stem2", stem2panel);
		tabbedPane.addTab("Loop1", loop1panel);
		tabbedPane.addTab("Loop2", loop2panel);
		tabbedPane.addTab("Loop3", loop3panel);
		
		pane.add(tabbedPane);
		pane.add(Box.createRigidArea(new Dimension(0,5)));
		pane.add(buttonpanel);
		return pane;
	}
	
	/**
	 * button actionlistener
	 *
	 * @param ae the ActionEvent which ocurred
	 */
	public void actionPerformed(ActionEvent ae){
		Object src = ae.getSource();
		if(src == deflen){
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
			l2.setEnabled(true);
			if(!pknot.getIsDefaultMin() && pknot.getMinLength()>0){
				lowerend.setText(String.valueOf(pknot.getMinLength()));
				lowerendbuf = lowerend.getText();
			}
			lowerend.setEnabled(true);
			l3.setEnabled(true);
			if(!pknot.getIsDefaultMax() && pknot.getMaxLength()>0){
				upperend.setText(String.valueOf(pknot.getMaxLength()));
				upperendbuf = upperend.getText();
			}
			upperend.setEnabled(true);
		}
		if(src == deflen2){
		    l5.setEnabled(false);
		    t2.setText(null);
		    t2buf = t2.getText();
		    t2.setEnabled(false);
			l6.setEnabled(false);
			lowerend2.setText(null);
			lowerendbuf2 = lowerend2.getText();
			lowerend2.setEnabled(false);
			l7.setEnabled(false);
			upperend2.setText(null);
			upperendbuf2 = upperend2.getText();
			upperend2.setEnabled(false);
		}
		if(src == exact2){
		    l5.setEnabled(true);
		    if(pknot.isStem1exact() && pknot.getStem1len() > 0){
		        t2.setText(String.valueOf(pknot.getStem1len()));
		        t2buf = t2.getText();
		    }
		    t2.setEnabled(true);
			l6.setEnabled(false);
			lowerend2.setText(null);
			lowerendbuf2 = lowerend2.getText();
			lowerend2.setEnabled(false);
			l7.setEnabled(false);
			upperend2.setText(null);
			upperendbuf2 = upperend2.getText();
			upperend2.setEnabled(false);
		}
		if(src == range2){
		    l5.setEnabled(false);
		    t2.setText(null);
		    t2buf = t2.getText();
		    t2.setEnabled(false);
			l6.setEnabled(true);
			if(pknot.getStem1min()>0){
				lowerend2.setText(String.valueOf(pknot.getStem1min()));
				lowerendbuf2 = lowerend2.getText();
			}
			lowerend2.setEnabled(true);
			l7.setEnabled(true);
			if(pknot.getStem1max()>0){
				upperend2.setText(String.valueOf(pknot.getStem1max()));
				upperendbuf2 = upperend2.getText();
			}
			upperend2.setEnabled(true);
		}
		if(src == deflen3){
		    l8.setEnabled(false);
		    t3.setText(null);
		    t3buf = t3.getText();
		    t3.setEnabled(false);
			l9.setEnabled(false);
			lowerend3.setText(null);
			lowerendbuf3 = lowerend3.getText();
			lowerend3.setEnabled(false);
			l10.setEnabled(false);
			upperend3.setText(null);
			upperendbuf3 = upperend3.getText();
			upperend3.setEnabled(false);
		}
		if(src == exact3){
		    l8.setEnabled(true);
		    if(pknot.isStem2exact() && pknot.getStem2len() > 0){
		        t3.setText(String.valueOf(pknot.getStem2len()));
		        t3buf = t3.getText();
		    }
		    t3.setEnabled(true);
			l9.setEnabled(false);
			lowerend3.setText(null);
			lowerendbuf3 = lowerend3.getText();
			lowerend3.setEnabled(false);
			l10.setEnabled(false);
			upperend3.setText(null);
			upperendbuf3 = upperend3.getText();
			upperend3.setEnabled(false);
		}
		if(src == range3){
		    l8.setEnabled(false);
		    t3.setText(null);
		    t3buf = t3.getText();
		    t3.setEnabled(false);
			l9.setEnabled(true);
			if(pknot.getStem2min()>0){
				lowerend3.setText(String.valueOf(pknot.getStem2min()));
				lowerendbuf3 = lowerend3.getText();
			}
			lowerend3.setEnabled(true);
			l10.setEnabled(true);
			if(pknot.getStem2max()>0){
				upperend3.setText(String.valueOf(pknot.getStem2max()));
				upperendbuf3 = upperend3.getText();
			}
			upperend3.setEnabled(true);
		}
		if(src == deflen4){
		    folding4.setEnabled(true);
		    l11.setEnabled(false);
		    t4.setText(null);
		    t4buf = t4.getText();
		    t4.setEnabled(false);
			l12.setEnabled(false);
			lowerend4.setText(null);
			lowerendbuf4 = lowerend4.getText();
			lowerend4.setEnabled(false);
			l13.setEnabled(false);
			upperend4.setText(null);
			upperendbuf4 = upperend4.getText();
			upperend4.setEnabled(false);
		}
		if(src == exact4){
		    folding4.setEnabled(false);
		    l11.setEnabled(true);
		    if(pknot.isLoop1exact() && pknot.getLoop1len() > 0){
		        t4.setText(String.valueOf(pknot.getLoop1len()));
		        t4buf = t4.getText();
		    }
		    t4.setEnabled(true);
			l12.setEnabled(false);
			lowerend4.setText(null);
			lowerendbuf4 = lowerend4.getText();
			lowerend4.setEnabled(false);
			l13.setEnabled(false);
			upperend4.setText(null);
			upperendbuf4 = upperend4.getText();
			upperend4.setEnabled(false);
		}
		if(src == range4){
		    folding4.setEnabled(false);
		    l11.setEnabled(false);
		    t4.setText(null);
		    t4buf = t4.getText();
		    t4.setEnabled(false);
			l12.setEnabled(true);
			if(pknot.getLoop1min()>0){
				lowerend4.setText(String.valueOf(pknot.getLoop1min()));
				lowerendbuf4 = lowerend4.getText();
			}
			lowerend4.setEnabled(true);
			l13.setEnabled(true);
			if(pknot.getLoop1max()>0){
				upperend4.setText(String.valueOf(pknot.getLoop1max()));
				upperendbuf4 = upperend4.getText();
			}
			upperend4.setEnabled(true);
		}
		if(src == deflen5){
		    folding5.setEnabled(true);
		    l14.setEnabled(false);
		    t5.setText(null);
		    t5buf = t5.getText();
		    t5.setEnabled(false);
			l15.setEnabled(false);
			lowerend5.setText(null);
			lowerendbuf5 = lowerend5.getText();
			lowerend5.setEnabled(false);
			l16.setEnabled(false);
			upperend5.setText(null);
			upperendbuf5 = upperend5.getText();
			upperend5.setEnabled(false);
		}
		if(src == exact5){
		    folding5.setEnabled(false);
		    l14.setEnabled(true);
		    if(pknot.isLoop2exact() && pknot.getLoop2len() > 0){
		        t5.setText(String.valueOf(pknot.getLoop2len()));
		        t5buf = t5.getText();
		    }
		    t5.setEnabled(true);
			l15.setEnabled(false);
			lowerend5.setText(null);
			lowerendbuf5 = lowerend5.getText();
			lowerend5.setEnabled(false);
			l16.setEnabled(false);
			upperend5.setText(null);
			upperendbuf5 = upperend5.getText();
			upperend5.setEnabled(false);
		}
		if(src == range5){
		    folding5.setEnabled(false);
		    l14.setEnabled(false);
		    t5.setText(null);
		    t5buf = t5.getText();
		    t5.setEnabled(false);
			l15.setEnabled(true);
			if(pknot.getLoop2min()>0){
				lowerend5.setText(String.valueOf(pknot.getLoop2min()));
				lowerendbuf5 = lowerend5.getText();
			}
			lowerend5.setEnabled(true);
			l16.setEnabled(true);
			if(pknot.getLoop2max()>0){
				upperend5.setText(String.valueOf(pknot.getLoop2max()));
				upperendbuf5 = upperend5.getText();
			}
			upperend5.setEnabled(true);
		}
		if(src == deflen6){
		    folding6.setEnabled(true);
		    l17.setEnabled(false);
		    t6.setText(null);
		    t6buf = t6.getText();
		    t6.setEnabled(false);
			l18.setEnabled(false);
			lowerend6.setText(null);
			lowerendbuf6 = lowerend6.getText();
			lowerend6.setEnabled(false);
			l19.setEnabled(false);
			upperend6.setText(null);
			upperendbuf6 = upperend6.getText();
			upperend6.setEnabled(false);
		}
		if(src == exact6){
		    folding6.setEnabled(false);
		    l17.setEnabled(true);
		    if(pknot.isLoop3exact() && pknot.getLoop3len() > 0){
		        t6.setText(String.valueOf(pknot.getLoop3len()));
		        t6buf = t6.getText();
		    }
		    t6.setEnabled(true);
			l18.setEnabled(false);
			lowerend6.setText(null);
			lowerendbuf6 = lowerend6.getText();
			lowerend6.setEnabled(false);
			l19.setEnabled(false);
			upperend6.setText(null);
			upperendbuf6 = upperend6.getText();
			upperend6.setEnabled(false);
		}
		if(src == range6){
		    folding6.setEnabled(false);
		    l17.setEnabled(false);
		    t6.setText(null);
		    t6buf = t6.getText();
		    t6.setEnabled(false);
			l18.setEnabled(true);
			if(pknot.getLoop3min()>0){
				lowerend6.setText(String.valueOf(pknot.getLoop3min()));
				lowerendbuf6 = lowerend6.getText();
			}
			lowerend6.setEnabled(true);
			l19.setEnabled(true);
			if(pknot.getLoop3max()>0){
				upperend6.setText(String.valueOf(pknot.getLoop3max()));
				upperendbuf6 = upperend6.getText();
			}
			upperend6.setEnabled(true);
		}
		if(src == folding4){
		    if(folding4.isSelected()){
		        exact4.setEnabled(false);
		        range4.setEnabled(false);
		    }
		    else{
		        exact4.setEnabled(true);
		        range4.setEnabled(true);
		    }
		    if(pknot.isLoop1straight() ^ folding4.isSelected()){
		        apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(1,true);
		        tabbedPane.setEnabledAt(2,true);
		        tabbedPane.setEnabledAt(4,true);
		        tabbedPane.setEnabledAt(5,true);
		    }
		    else{
		        apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(1,false);
		        tabbedPane.setEnabledAt(2,false);
		        tabbedPane.setEnabledAt(4,false);
		        tabbedPane.setEnabledAt(5,false);
		    }
		}
		if(src == folding5){
		    if(folding5.isSelected()){
		        exact5.setEnabled(false);
		        range5.setEnabled(false);
		    }
		    else{
		        exact5.setEnabled(true);
		        range5.setEnabled(true);
		    }
		    if(pknot.isLoop2straight() ^ folding5.isSelected()){
		        apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(1,true);
		        tabbedPane.setEnabledAt(2,true);
		        tabbedPane.setEnabledAt(3,true);
		        tabbedPane.setEnabledAt(5,true);
		    }
		    else{
		        apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(1,false);
		        tabbedPane.setEnabledAt(2,false);
		        tabbedPane.setEnabledAt(3,false);
		        tabbedPane.setEnabledAt(5,false);
		    }
		}
		if(src == folding6){
		    if(folding6.isSelected()){
		        exact6.setEnabled(false);
		        range6.setEnabled(false);
		    }
		    else{
		        exact6.setEnabled(true);
		        range6.setEnabled(true);
		    }
		    if(pknot.isLoop3straight() ^ folding6.isSelected()){
		        apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(1,true);
		        tabbedPane.setEnabledAt(2,true);
		        tabbedPane.setEnabledAt(3,true);
		        tabbedPane.setEnabledAt(4,true);
		    }
		    else{
		        apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(1,false);
		        tabbedPane.setEnabledAt(2,false);
		        tabbedPane.setEnabledAt(3,false);
		        tabbedPane.setEnabledAt(4,false);
		    }
		}
		//save changes and close window
		if(src == ok){ //save changes
		    //no global size restriction
			if(deflen.isSelected()){
				pknot.setToDefaultLength();
			}
			//global range values
			else if(range.isSelected()){
				int low=0;
				int up=0;
				try{
				    //parse and check minimum
					if(lowerend.getText() != null && !(lowerend.getText()).equals("")){
						low = Integer.parseInt(lowerend.getText());	
						if(low<(Pseudoknot.MIN)){
							throw new NumberFormatException("A Pseudoknot must contain at least "+(Pseudoknot.MIN)+" bases.");
						}
					}
					//parse and check maximum
					if(upperend.getText() != null && !(upperend.getText()).equals("")){
						up = Integer.parseInt(upperend.getText());
						if(up<(Pseudoknot.MIN)){
							throw new NumberFormatException("A Pseudoknot must contain at least "+(Pseudoknot.MIN)+" bases.");
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
				//no values given: default size
				if(low == 0 && up == 0){
					deflen.doClick();
					return;
				}
				//store size values
				if(low != 0){
					pknot.setMinLength(low);
				}
				else{
					pknot.setToDefaultMin();
				}
				if(up != 0){
					pknot.setMaxLength(up);
				}
				else{
					pknot.setToDefaultMax();
				}
			}
			//stem1
			if(deflen2.isSelected()){
				pknot.setStem1default(true);
				pknot.setStem1exact(false);
			}
			else if(exact2.isSelected()){
			    int l=0;
			    try{
			        if(t2.getText() != null && !(t2.getText()).equals("")){
			            l = Integer.parseInt(t2.getText());
			            if(l<(Pseudoknot.MINSTEM)){
							throw new NumberFormatException("A Pseudoknot stem must contain at least "+(Pseudoknot.MINSTEM)+" basepair.");
			            }
			        }
			    }
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(l == 0){
				    deflen2.doClick();
				    return;
				}
				pknot.setStem1len(l);
			}
			//global range values
			else if(range2.isSelected()){
				int low=0;
				int up=0;
				try{
				    //parse and check minimum
					if(lowerend2.getText() != null && !(lowerend2.getText()).equals("")){
						low = Integer.parseInt(lowerend2.getText());	
						if(low<(Pseudoknot.MINSTEM)){
							throw new NumberFormatException("A Pseudoknot stem must contain at least "+(Pseudoknot.MINSTEM)+" basepairs.");
						}
					}
					//parse and check maximum
					if(upperend2.getText() != null && !(upperend2.getText()).equals("")){
						up = Integer.parseInt(upperend2.getText());
						if(up<(Pseudoknot.MINSTEM)){
							throw new NumberFormatException("A Pseudoknot stem must contain at least "+(Pseudoknot.MINSTEM)+" basepairs.");
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
				//no values given: default size
				if(low == 0 && up == 0){
					deflen2.doClick();
					return;
				}
				//store size values
				if(low != 0){
					pknot.setStem1min(low);
				}
				else{
					pknot.setStem1min(-1);
				}
				if(up != 0){
					pknot.setStem1max(up);
				}
				else{
					pknot.setStem1max(-1);
				}
			}
			//stem2
			if(deflen3.isSelected()){
				pknot.setStem2default(true);
				pknot.setStem2exact(false);
			}
			else if(exact3.isSelected()){
			    int l=0;
			    try{
			        if(t3.getText() != null && !(t3.getText()).equals("")){
			            l = Integer.parseInt(t3.getText());
			            if(l<(Pseudoknot.MINSTEM)){
							throw new NumberFormatException("A Pseudoknot stem must contain at least "+(Pseudoknot.MINSTEM)+" basepairs.");
			            }
			        }
			    }
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(l == 0){
				    deflen3.doClick();
				    return;
				}
				pknot.setStem2len(l);
			}
			//global range values
			else if(range3.isSelected()){
				int low=0;
				int up=0;
				try{
				    //parse and check minimum
					if(lowerend3.getText() != null && !(lowerend3.getText()).equals("")){
						low = Integer.parseInt(lowerend3.getText());	
						if(low<(Pseudoknot.MINSTEM)){
							throw new NumberFormatException("A Pseudoknot stem must contain at least "+(Pseudoknot.MINSTEM)+" basepairs.");
						}
					}
					//parse and check maximum
					if(upperend3.getText() != null && !(upperend3.getText()).equals("")){
						up = Integer.parseInt(upperend3.getText());
						if(up<(Pseudoknot.MINSTEM)){
							throw new NumberFormatException("A Pseudoknot stem must contain at least "+(Pseudoknot.MINSTEM)+" basepairs.");
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
				//no values given: default size
				if(low == 0 && up == 0){
					deflen3.doClick();
					return;
				}
				//store size values
				if(low != 0){
					pknot.setStem2min(low);
				}
				else{
					pknot.setStem2min(-1);
				}
				if(up != 0){
					pknot.setStem2max(up);
				}
				else{
					pknot.setStem2max(-1);
				}
			}
			//loop1
			if(deflen4.isSelected()){
				pknot.setLoop1default();
				pknot.setLoop1straight(!folding4.isSelected());
			}
			else if(exact4.isSelected()){
			    int l=0;
			    try{
			        if(t4.getText() != null && !(t4.getText()).equals("")){
			            l = Integer.parseInt(t4.getText());
			            if(l<(Pseudoknot.MINLOOP)){
							throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
			            }
			        }
			    }
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(l == 0){
				    deflen4.doClick();
				    return;
				}
				pknot.setLoop1len(l);
			}
			//global range values
			else if(range4.isSelected()){
				int low=0;
				int up=0;
				try{
				    //parse and check minimum
					if(lowerend4.getText() != null && !(lowerend4.getText()).equals("")){
						low = Integer.parseInt(lowerend4.getText());	
						if(low<(Pseudoknot.MINLOOP)){
							throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
						}
					}
					//parse and check maximum
					if(upperend4.getText() != null && !(upperend4.getText()).equals("")){
						up = Integer.parseInt(upperend4.getText());
						if(up<(Pseudoknot.MINLOOP)){
							throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
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
				//no values given: default size
				if(low == 0 && up == 0){
					deflen4.doClick();
					return;
				}
				//store size values
				if(low != 0){
					pknot.setLoop1min(low);
				}
				else{
					pknot.setLoop1min(-1);
				}
				if(up != 0){
					pknot.setLoop1max(up);
				}
				else{
					pknot.setLoop1max(-1);
				}
			}
			//loop2
			if(deflen5.isSelected()){
				pknot.setLoop2default();
				pknot.setLoop2straight(!folding5.isSelected());
			}
			else if(exact5.isSelected()){
			    int l=0;
			    try{
			        if(t5.getText() != null && !(t5.getText()).equals("")){
			            l = Integer.parseInt(t5.getText());
			            if(l<(Pseudoknot.MINLOOP)){
							throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
			            }
			        }
			    }
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(l == 0){
				    deflen5.doClick();
				    return;
				}
				pknot.setLoop2len(l);
			}
			//global range values
			else if(range5.isSelected()){
				int low=0;
				int up=0;
				try{
				    //parse and check minimum
					if(lowerend5.getText() != null && !(lowerend5.getText()).equals("")){
						low = Integer.parseInt(lowerend5.getText());	
						if(low<(Pseudoknot.MINLOOP)){
							throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
						}
					}
					//parse and check maximum
					if(upperend5.getText() != null && !(upperend5.getText()).equals("")){
						up = Integer.parseInt(upperend5.getText());
						if(up<(Pseudoknot.MINLOOP)){
							throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
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
				//no values given: default size
				if(low == 0 && up == 0){
					deflen5.doClick();
					return;
				}
				//store size values
				if(low != 0){
				    pknot.setLoop2min(low);
				}
				else{
				    pknot.setLoop2min(-1);
				}
				if(up != 0){
				    pknot.setLoop2max(up);
				}
				else{
				    pknot.setLoop2max(-1);
				}
			}
			//loop3
			if(deflen6.isSelected()){
			    pknot.setLoop3default();
			    pknot.setLoop3straight(!folding6.isSelected());
			}
			else if(exact6.isSelected()){
			    int l=0;
			    try{
			        if(t6.getText() != null && !(t6.getText()).equals("")){
			            l = Integer.parseInt(t6.getText());
			            if(l<(Pseudoknot.MINLOOP)){
			                throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
			            }
			        }
			    }
			    catch(NumberFormatException nfe){
			        JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
			        return;
			    }
			    if(l == 0){
			        deflen6.doClick();
			        return;
			    }
			    pknot.setLoop3len(l);
			}
			//global range values
			else if(range6.isSelected()){
			    int low=0;
			    int up=0;
			    try{
			        //parse and check minimum
			        if(lowerend6.getText() != null && !(lowerend6.getText()).equals("")){
			            low = Integer.parseInt(lowerend6.getText());	
			            if(low<(Pseudoknot.MINLOOP)){
			                throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
			            }
			        }
			        //parse and check maximum
			        if(upperend6.getText() != null && !(upperend6.getText()).equals("")){
			            up = Integer.parseInt(upperend6.getText());
			            if(up<(Pseudoknot.MINLOOP)){
			                throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
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
			    //no values given: default size
			    if(low == 0 && up == 0){
			        deflen6.doClick();
			        return;
			    }
			    //store size values
			    if(low != 0){
			        pknot.setLoop3min(low);
			    }
			    else{
			        pknot.setLoop3min(-1);
			    }
			    if(up != 0){
			        pknot.setLoop3max(up);
			    }
			    else{
			        pknot.setLoop3max(-1);
			    }
			}
			ps.setSelected(false);
			ds.unselectShape();
			ds.repaint();
			dispose();
		}
		//store changes, but keep window open
		if(src == apply){
		    //apply changes (same as above, except windows doesn't close 
		    //and values are adjusted
		    if(tabbedPane.isEnabledAt(0)){
		        //no global size restriction
		        if(deflen.isSelected()){
		            pknot.setToDefaultLength();
		        }
		        //global range values
		        else if(range.isSelected()){
		            int low=0;
		            int up=0;
		            try{
		                //parse and check minimum
		                if(lowerend.getText() != null && !(lowerend.getText()).equals("")){
		                    low = Integer.parseInt(lowerend.getText());	
		                    if(low<(Pseudoknot.MIN)){
		                        throw new NumberFormatException("A Pseudoknot must contain at least "+(Pseudoknot.MIN)+" bases.");
		                    }
		                }
		                //parse and check maximum
		                if(upperend.getText() != null && !(upperend.getText()).equals("")){
		                    up = Integer.parseInt(upperend.getText());
		                    if(up<(Pseudoknot.MIN)){
		                        throw new NumberFormatException("A Pseudoknot must contain at least "+(Pseudoknot.MIN)+" bases.");
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
		            //no values given: default size
		            if(low == 0 && up == 0){
		                deflen.doClick();
		                return;
		            }
		            //store size values
		            if(low != 0){
		                pknot.setMinLength(low);
		            }
		            else{
		                pknot.setToDefaultMin();
		            }
		            if(up != 0){
		                pknot.setMaxLength(up);
		            }
		            else{
		                pknot.setToDefaultMax();
		            }
		        }
		    }
		    else if(tabbedPane.isEnabledAt(1)){
		        //stem1
		        if(deflen2.isSelected()){
		            pknot.setStem1default(true);
		            pknot.setStem1exact(false);
		        }
		        else if(exact2.isSelected()){
		            int l=0;
		            try{
		                if(t2.getText() != null && !(t2.getText()).equals("")){
		                    l = Integer.parseInt(t2.getText());
		                    if(l<(Pseudoknot.MINSTEM)){
		                        throw new NumberFormatException("A Pseudoknot stem must contain at least "+(Pseudoknot.MINSTEM)+" basepairs.");
		                    }
		                }
		            }
		            catch(NumberFormatException nfe){
		                JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
		                return;
		            }
		            if(l == 0){
		                deflen2.doClick();
		                return;
		            }
		            pknot.setStem1len(l);
		        }
		        //global range values
		        else if(range2.isSelected()){
		            int low=0;
		            int up=0;
		            try{
		                //parse and check minimum
		                if(lowerend2.getText() != null && !(lowerend2.getText()).equals("")){
		                    low = Integer.parseInt(lowerend2.getText());	
		                    if(low<(Pseudoknot.MINSTEM)){
		                        throw new NumberFormatException("A Pseudoknot stem must contain at least "+(Pseudoknot.MINSTEM)+" basepairs.");
		                    }
		                }
		                //parse and check maximum
		                if(upperend2.getText() != null && !(upperend2.getText()).equals("")){
		                    up = Integer.parseInt(upperend2.getText());
		                    if(up<(Pseudoknot.MINSTEM)){
		                        throw new NumberFormatException("A Pseudoknot stem must contain at least "+(Pseudoknot.MINSTEM)+" basepairs.");
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
		            //no values given: default size
		            if(low == 0 && up == 0){
		                deflen2.doClick();
		                return;
		            }
		            //store size values
		            if(low != 0){
		                pknot.setStem1min(low);
		            }
		            else{
		                pknot.setStem1min(-1);
		            }
		            if(up != 0){
		                pknot.setStem1max(up);
		            }
		            else{
		                pknot.setStem1max(-1);
		            }
		        }
		    }
		    else if(tabbedPane.isEnabledAt(2)){
		        //stem2
		        if(deflen3.isSelected()){
		            pknot.setStem2default(true);
		            pknot.setStem2exact(false);
		        }
		        else if(exact3.isSelected()){
		            int l=0;
		            try{
		                if(t3.getText() != null && !(t3.getText()).equals("")){
		                    l = Integer.parseInt(t3.getText());
		                    if(l<(Pseudoknot.MINSTEM)){
		                        throw new NumberFormatException("A Pseudoknot stem must contain at least "+(Pseudoknot.MINSTEM)+" basepairs.");
		                    }
		                }
		            }
		            catch(NumberFormatException nfe){
		                JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
		                return;
		            }
		            if(l == 0){
		                deflen3.doClick();
		                return;
		            }
		            pknot.setStem2len(l);
		        }
		        //global range values
		        else if(range3.isSelected()){
		            int low=0;
		            int up=0;
		            try{
		                //parse and check minimum
		                if(lowerend3.getText() != null && !(lowerend3.getText()).equals("")){
		                    low = Integer.parseInt(lowerend3.getText());	
		                    if(low<(Pseudoknot.MINSTEM)){
		                        throw new NumberFormatException("A Pseudoknot stem must contain at least "+(Pseudoknot.MINSTEM)+" basepairs.");
		                    }
		                }
		                //parse and check maximum
		                if(upperend3.getText() != null && !(upperend3.getText()).equals("")){
		                    up = Integer.parseInt(upperend3.getText());
		                    if(up<(Pseudoknot.MINSTEM)){
		                        throw new NumberFormatException("A Pseudoknot stem must contain at least "+(Pseudoknot.MINSTEM)+" basepairs.");
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
		            //no values given: default size
		            if(low == 0 && up == 0){
		                deflen3.doClick();
		                return;
		            }
		            //store size values
		            if(low != 0){
		                pknot.setStem2min(low);
		            }
		            else{
		                pknot.setStem2min(-1);
		            }
		            if(up != 0){
		                pknot.setStem2max(up);
		            }
		            else{
		                pknot.setStem2max(-1);
		            }
		        }
		    }
		    else if(tabbedPane.isEnabledAt(3)){
		        //loop1
		        if(deflen4.isSelected()){
		            pknot.setLoop1default();
		            pknot.setLoop1straight(!folding4.isSelected());
		        }
		        else if(exact4.isSelected()){
		            int l=0;
		            try{
		                if(t4.getText() != null && !(t4.getText()).equals("")){
		                    l = Integer.parseInt(t4.getText());
		                    if(l<(Pseudoknot.MINLOOP)){
		                        throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
		                    }
		                }
		            }
		            catch(NumberFormatException nfe){
		                JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
		                return;
		            }
		            if(l == 0){
		                deflen4.doClick();
		                return;
		            }
		            pknot.setLoop1len(l);
		        }
		        //global range values
		        else if(range4.isSelected()){
		            int low=0;
		            int up=0;
		            try{
		                //parse and check minimum
		                if(lowerend4.getText() != null && !(lowerend4.getText()).equals("")){
		                    low = Integer.parseInt(lowerend4.getText());	
		                    if(low<(Pseudoknot.MINLOOP)){
		                        throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
		                    }
		                }
		                //parse and check maximum
		                if(upperend4.getText() != null && !(upperend4.getText()).equals("")){
		                    up = Integer.parseInt(upperend4.getText());
		                    if(up<(Pseudoknot.MINLOOP)){
		                        throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
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
		            //no values given: default size
		            if(low == 0 && up == 0){
		                deflen4.doClick();
		                return;
		            }
		            //store size values
		            if(low != 0){
		                pknot.setLoop1min(low);
		            }
		            else{
		                pknot.setLoop1min(-1);
		            }
		            if(up != 0){
		                pknot.setLoop1max(up);
		            }
		            else{
		                pknot.setLoop1max(-1);
		            }
		        }
		    }
		    else if(tabbedPane.isEnabledAt(4)){
		        //loop2
		        if(deflen5.isSelected()){
		            pknot.setLoop2default();
		            pknot.setLoop2straight(!folding5.isSelected());
		        }
		        else if(exact5.isSelected()){
		            int l=0;
		            try{
		                if(t5.getText() != null && !(t5.getText()).equals("")){
		                    l = Integer.parseInt(t5.getText());
		                    if(l<(Pseudoknot.MINLOOP)){
		                        throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
		                    }
		                }
		            }
		            catch(NumberFormatException nfe){
		                JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
		                return;
		            }
		            if(l == 0){
		                deflen5.doClick();
		                return;
		            }
		            pknot.setLoop2len(l);
		        }
		        //global range values
		        else if(range5.isSelected()){
		            int low=0;
		            int up=0;
		            try{
		                //parse and check minimum
		                if(lowerend5.getText() != null && !(lowerend5.getText()).equals("")){
		                    low = Integer.parseInt(lowerend5.getText());	
		                    if(low<(Pseudoknot.MINLOOP)){
		                        throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
		                    }
		                }
		                //parse and check maximum
		                if(upperend5.getText() != null && !(upperend5.getText()).equals("")){
		                    up = Integer.parseInt(upperend5.getText());
		                    if(up<(Pseudoknot.MINLOOP)){
		                        throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
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
		            //no values given: default size
		            if(low == 0 && up == 0){
		                deflen5.doClick();
		                return;
		            }
		            //store size values
		            if(low != 0){
		                pknot.setLoop2min(low);
		            }
		            else{
		                pknot.setLoop2min(-1);
		            }
		            if(up != 0){
		                pknot.setLoop2max(up);
		            }
		            else{
		                pknot.setLoop2max(-1);
		            }
		        }
		    }
		    else if(tabbedPane.isEnabledAt(5)){
		        //loop3
		        if(deflen6.isSelected()){
		            pknot.setLoop3default();
		            pknot.setLoop3straight(!folding6.isSelected());
		        }
		        else if(exact6.isSelected()){
		            int l=0;
		            try{
		                if(t6.getText() != null && !(t6.getText()).equals("")){
		                    l = Integer.parseInt(t6.getText());
		                    if(l<(Pseudoknot.MINLOOP)){
		                        throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
		                    }
		                }
		            }
		            catch(NumberFormatException nfe){
		                JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
		                return;
		            }
		            if(l == 0){
		                deflen6.doClick();
		                return;
		            }
		            pknot.setLoop3len(l);
		        }
		        //global range values
		        else if(range6.isSelected()){
		            int low=0;
		            int up=0;
		            try{
		                //parse and check minimum
		                if(lowerend6.getText() != null && !(lowerend6.getText()).equals("")){
		                    low = Integer.parseInt(lowerend6.getText());	
		                    if(low<(Pseudoknot.MINLOOP)){
		                        throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
		                    }
		                }
		                //parse and check maximum
		                if(upperend6.getText() != null && !(upperend6.getText()).equals("")){
		                    up = Integer.parseInt(upperend6.getText());
		                    if(up<(Pseudoknot.MINLOOP)){
		                        throw new NumberFormatException("A Pseudoknot loop must contain at least "+(Pseudoknot.MINLOOP)+" bases.");
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
		            //no values given: default size
		            if(low == 0 && up == 0){
		                deflen6.doClick();
		                return;
		            }
		            //store size values
		            if(low != 0){
		                pknot.setLoop3min(low);
		            }
		            else{
		                pknot.setLoop3min(-1);
		            }
		            if(up != 0){
		                pknot.setLoop3max(up);
		            }
		            else{
		                pknot.setLoop3max(-1);
		            }
		        }
		    }
			apply.setEnabled(false);
			tabbedPane.setEnabledAt(0,true);
			tabbedPane.setEnabledAt(1,true);
			tabbedPane.setEnabledAt(2,true);
			tabbedPane.setEnabledAt(3,true);
			tabbedPane.setEnabledAt(4,true);
			tabbedPane.setEnabledAt(5,true);
			ds.repaint();
		}
		//no changes and close window
		if(src == cancel){
			//no changes
			ps.setSelected(false);
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
		if(src == lowerend){
			if(!lowerendbuf.equals(lowerend.getText())){
				apply.setEnabled(true);
			}
			else if(upperendbuf.equals(upperend.getText())){
				apply.setEnabled(false);
			}
		}
		if(src == upperend){
			if(!upperendbuf.equals(upperend.getText())){
				apply.setEnabled(true);
			}
			else if(lowerendbuf.equals(lowerend.getText())){
				apply.setEnabled(false);
			}
		}
		if(src == t2){
		    if(!t2buf.equals(t2.getText())){
		        apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(2,false);
		        tabbedPane.setEnabledAt(3,false);
		        tabbedPane.setEnabledAt(4,false);
		        tabbedPane.setEnabledAt(5,false);
		    }
		    else if(lowerendbuf2.equals(lowerend2.getText()) && upperendbuf2.equals(upperend2.getText())){
		        apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(2,true);
		        tabbedPane.setEnabledAt(3,true);
		        tabbedPane.setEnabledAt(4,true);
		        tabbedPane.setEnabledAt(5,true);
		        
		    }
		}
		if(src == lowerend2){
			if(!lowerendbuf2.equals(lowerend2.getText())){
				apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(2,false);
		        tabbedPane.setEnabledAt(3,false);
		        tabbedPane.setEnabledAt(4,false);
		        tabbedPane.setEnabledAt(5,false);
			}
			else if(upperendbuf2.equals(upperend2.getText()) && t2buf.equals(t2.getText())){
				apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(2,true);
		        tabbedPane.setEnabledAt(3,true);
		        tabbedPane.setEnabledAt(4,true);
		        tabbedPane.setEnabledAt(5,true);
			}
		}
		if(src == upperend2){
			if(!upperendbuf2.equals(upperend2.getText())){
				apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(2,false);
		        tabbedPane.setEnabledAt(3,false);
		        tabbedPane.setEnabledAt(4,false);
		        tabbedPane.setEnabledAt(5,false);
			}
			else if(lowerendbuf2.equals(lowerend2.getText()) && t2buf.equals(t2.getText())){
				apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(2,true);
		        tabbedPane.setEnabledAt(3,true);
		        tabbedPane.setEnabledAt(4,true);
		        tabbedPane.setEnabledAt(5,true);
			}
		}
		if(src == t3){
		    if(!t3buf.equals(t3.getText())){
		        apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(1,false);
		        tabbedPane.setEnabledAt(3,false);
		        tabbedPane.setEnabledAt(4,false);
		        tabbedPane.setEnabledAt(5,false);
		    }
		    else if(lowerendbuf3.equals(lowerend3.getText()) && upperendbuf3.equals(upperend3.getText())){
		        apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(1,true);
		        tabbedPane.setEnabledAt(3,true);
		        tabbedPane.setEnabledAt(4,true);
		        tabbedPane.setEnabledAt(5,true);
		        
		    }
		}
		if(src == lowerend3){
			if(!lowerendbuf3.equals(lowerend3.getText())){
				apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(1,false);
		        tabbedPane.setEnabledAt(3,false);
		        tabbedPane.setEnabledAt(4,false);
		        tabbedPane.setEnabledAt(5,false);
			}
			else if(upperendbuf3.equals(upperend3.getText()) && t3buf.equals(t3.getText())){
				apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(1,true);
		        tabbedPane.setEnabledAt(3,true);
		        tabbedPane.setEnabledAt(4,true);
		        tabbedPane.setEnabledAt(5,true);
			}
		}
		if(src == upperend3){
			if(!upperendbuf3.equals(upperend3.getText())){
				apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(1,false);
		        tabbedPane.setEnabledAt(3,false);
		        tabbedPane.setEnabledAt(4,false);
		        tabbedPane.setEnabledAt(5,false);
			}
			else if(lowerendbuf3.equals(lowerend3.getText()) && t3buf.equals(t3.getText())){
				apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(1,true);
		        tabbedPane.setEnabledAt(3,true);
		        tabbedPane.setEnabledAt(4,true);
		        tabbedPane.setEnabledAt(5,true);
			}
		}
		if(src == t4){
		    if(!t4buf.equals(t4.getText())){
		        apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(2,false);
		        tabbedPane.setEnabledAt(1,false);
		        tabbedPane.setEnabledAt(4,false);
		        tabbedPane.setEnabledAt(5,false);
		    }
		    else if(lowerendbuf4.equals(lowerend4.getText()) && upperendbuf4.equals(upperend4.getText())){
		        apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(2,true);
		        tabbedPane.setEnabledAt(1,true);
		        tabbedPane.setEnabledAt(4,true);
		        tabbedPane.setEnabledAt(5,true);
		        
		    }
		}
		if(src == lowerend4){
			if(!lowerendbuf4.equals(lowerend4.getText())){
				apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(2,false);
		        tabbedPane.setEnabledAt(1,false);
		        tabbedPane.setEnabledAt(4,false);
		        tabbedPane.setEnabledAt(5,false);
			}
			else if(upperendbuf4.equals(upperend4.getText()) && t4buf.equals(t4.getText())){
				apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(2,true);
		        tabbedPane.setEnabledAt(1,true);
		        tabbedPane.setEnabledAt(4,true);
		        tabbedPane.setEnabledAt(5,true);
			}
		}
		if(src == upperend4){
			if(!upperendbuf4.equals(upperend4.getText())){
				apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(2,false);
		        tabbedPane.setEnabledAt(1,false);
		        tabbedPane.setEnabledAt(4,false);
		        tabbedPane.setEnabledAt(5,false);
			}
			else if(lowerendbuf4.equals(lowerend4.getText()) && t4buf.equals(t4.getText())){
				apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(2,true);
		        tabbedPane.setEnabledAt(1,true);
		        tabbedPane.setEnabledAt(4,true);
		        tabbedPane.setEnabledAt(5,true);
			}
		}
		if(src == t5){
		    if(!t5buf.equals(t5.getText())){
		        apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(2,false);
		        tabbedPane.setEnabledAt(3,false);
		        tabbedPane.setEnabledAt(1,false);
		        tabbedPane.setEnabledAt(5,false);
		    }
		    else if(lowerendbuf5.equals(lowerend5.getText()) && upperendbuf5.equals(upperend5.getText())){
		        apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(2,true);
		        tabbedPane.setEnabledAt(3,true);
		        tabbedPane.setEnabledAt(1,true);
		        tabbedPane.setEnabledAt(5,true);
		        
		    }
		}
		if(src == lowerend5){
			if(!lowerendbuf5.equals(lowerend5.getText())){
				apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(2,false);
		        tabbedPane.setEnabledAt(3,false);
		        tabbedPane.setEnabledAt(1,false);
		        tabbedPane.setEnabledAt(5,false);
			}
			else if(upperendbuf5.equals(upperend5.getText()) && t5buf.equals(t5.getText())){
				apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(2,true);
		        tabbedPane.setEnabledAt(3,true);
		        tabbedPane.setEnabledAt(1,true);
		        tabbedPane.setEnabledAt(5,true);
			}
		}
		if(src == upperend5){
			if(!upperendbuf5.equals(upperend5.getText())){
				apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(2,false);
		        tabbedPane.setEnabledAt(3,false);
		        tabbedPane.setEnabledAt(1,false);
		        tabbedPane.setEnabledAt(5,false);
			}
			else if(lowerendbuf5.equals(lowerend5.getText()) && t5buf.equals(t5.getText())){
				apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(2,true);
		        tabbedPane.setEnabledAt(3,true);
		        tabbedPane.setEnabledAt(1,true);
		        tabbedPane.setEnabledAt(5,true);
			}
		}
		if(src == t6){
		    if(!t6buf.equals(t6.getText())){
		        apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(2,false);
		        tabbedPane.setEnabledAt(3,false);
		        tabbedPane.setEnabledAt(4,false);
		        tabbedPane.setEnabledAt(1,false);
		    }
		    else if(lowerendbuf6.equals(lowerend6.getText()) && upperendbuf6.equals(upperend6.getText())){
		        apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(2,true);
		        tabbedPane.setEnabledAt(3,true);
		        tabbedPane.setEnabledAt(4,true);
		        tabbedPane.setEnabledAt(1,true);
		        
		    }
		}
		if(src == lowerend6){
			if(!lowerendbuf6.equals(lowerend6.getText())){
				apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(2,false);
		        tabbedPane.setEnabledAt(3,false);
		        tabbedPane.setEnabledAt(4,false);
		        tabbedPane.setEnabledAt(1,false);
			}
			else if(upperendbuf6.equals(upperend6.getText()) && t6buf.equals(t6.getText())){
				apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(2,true);
		        tabbedPane.setEnabledAt(3,true);
		        tabbedPane.setEnabledAt(4,true);
		        tabbedPane.setEnabledAt(1,true);
			}
		}
		if(src == upperend6){
			if(!upperendbuf6.equals(upperend6.getText())){
				apply.setEnabled(true);
		        tabbedPane.setEnabledAt(0,false);
		        tabbedPane.setEnabledAt(2,false);
		        tabbedPane.setEnabledAt(3,false);
		        tabbedPane.setEnabledAt(4,false);
		        tabbedPane.setEnabledAt(1,false);
			}
			else if(lowerendbuf6.equals(lowerend6.getText()) && t6buf.equals(t6.getText())){
				apply.setEnabled(false);
		        tabbedPane.setEnabledAt(0,true);
		        tabbedPane.setEnabledAt(2,true);
		        tabbedPane.setEnabledAt(3,true);
		        tabbedPane.setEnabledAt(4,true);
		        tabbedPane.setEnabledAt(1,true);
			}
		}
	}
	
	public void dispose(){
	    ps.editClosed();
	    super.dispose();
	}
	
	private class PseudoPanel extends JPanel{

        private static final long serialVersionUID = -2069356519408742266L;
		private double xloc = 60;//anpassen
		private double yloc = 25;//anpassen
		private double width = 20;
		private double rheight = 15;
		private double squarewidth = 50;
		private double height = 120;
		private Area area;
		private GeneralPath rnapath;
		private GeneralPath redpath;
		
		public PseudoPanel(int index){
		    setPreferredSize(new Dimension(200,50));
		    area = new Area(new Rectangle2D.Double(xloc,yloc+rheight,squarewidth+2*width,height));
		    area.add(new Area(new Rectangle2D.Double(xloc,yloc,width,rheight)));
		    area.add(new Area(new Rectangle2D.Double(xloc+width+squarewidth,yloc+rheight+height,width,rheight)));
		    rnapath = new GeneralPath();
		    redpath = new GeneralPath();
		    adjustPath(index);
		}
	    
		public void adjustPath(int index){
		    float x = (float) xloc;
			float y = (float) yloc;
			float w = (float) width;
			float h = (float) height;
			float rh = (float) rheight;
			float sw = (float) squarewidth;

				//stem 1
			if((index == 0 && orientation) || (index == 2 && !orientation)){
			    redpath.moveTo(x+w/2,y);
			    redpath.lineTo(x+w/2,y+h/2+rh);
			    redpath.moveTo(x+w/2,y+rh+h/4-7);
				redpath.lineTo(x+w/2+15,y+rh+h/4-7);
				redpath.moveTo(x+w/2,y+rh+h/4+1);
				redpath.lineTo(x+w/2+13,y+rh+h/4+1);
				redpath.moveTo(x+w/2,y+rh+h/4+9);
				redpath.lineTo(x+w/2+14,y+rh+h/4+9);
			}
			rnapath.moveTo(x+w/2,y);
			rnapath.lineTo(x+w/2,y+h/2+rh);
			
			//loop1
			if((index == 1 && orientation) || (index == 4 && !orientation)){
			    redpath.moveTo(x+w/2,y+h/2+rh);
				redpath.quadTo(x+5,y+rh+h-5,x+w+sw/2,y+h+6);
			}
			rnapath.quadTo(x+5,y+rh+h-5,x+w+sw/2,y+h+6);
			//loop2
			if(index == 3){
			    redpath.moveTo(x+w+sw/2,y+h+6);
				redpath.curveTo(x+w+2+sw,y+5+h,x+w+2+sw,y+rh+h/2+5,x+w+sw/2,y+h/2+rh);
				redpath.curveTo(x+w/2+6,y+rh+h/2-2,x+w/2+6,y+rh+10,x+w+sw/2,y+rh+10);
			}
			rnapath.curveTo(x+w+2+sw,y+5+h,x+w+2+sw,y+rh+h/2+5,x+w+sw/2,y+h/2+rh);
			rnapath.curveTo(x+w/2+6,y+rh+h/2-2,x+w/2+6,y+rh+10,x+w+sw/2,y+rh+10);
			//loop3
			if((index == 4 && orientation) || (index == 1 && !orientation)){
			    redpath.moveTo(x+w+sw/2,y+rh+10);
				redpath.quadTo(x+2*w+sw-5,y+rh+5,x+w+sw+w/2,y+h/2+rh);
			}
			rnapath.quadTo(x+2*w+sw-5,y+rh+5,x+w+sw+w/2,y+h/2+rh);
			
			//stem 2
			if((index == 2 && orientation) || (index == 0 && !orientation)){
				redpath.moveTo(x+w+sw+w/2,y+h/2+rh);
				redpath.lineTo(x+w+sw+w/2,y+2*rh+h);
				redpath.moveTo(x+sw+w/2+w,y+rh+h/4-7+h/2-4);
				redpath.lineTo(x+sw+15,y+rh+h/4-7+h/2-4);
				redpath.moveTo(x+w/2+sw+w,y+rh+h/4+1+h/2-4);
				redpath.lineTo(x+15+sw,y+rh+h/4+1+h/2-4);
				redpath.moveTo(x+w/2+sw+w,y+rh+h/4+9+h/2-4);
				redpath.lineTo(x+sw+14,y+rh+h/4+9+h/2-4);
			}
			rnapath.moveTo(x+w+sw+w/2,y+h/2+rh);
			rnapath.lineTo(x+w+sw+w/2,y+2*rh+h);
			
			//dashed
			rnapath.moveTo(x+w/2,y+rh+h/4-7);
			rnapath.lineTo(x+w/2+15,y+rh+h/4-7);
			rnapath.moveTo(x+w/2,y+rh+h/4+1);
			rnapath.lineTo(x+w/2+13,y+rh+h/4+1);
			rnapath.moveTo(x+w/2,y+rh+h/4+9);
			rnapath.lineTo(x+w/2+14,y+rh+h/4+9);
			
			rnapath.moveTo(x+sw+w/2+w,y+rh+h/4-7+h/2-4);
			rnapath.lineTo(x+sw+15,y+rh+h/4-7+h/2-4);
			rnapath.moveTo(x+w/2+sw+w,y+rh+h/4+1+h/2-4);
			rnapath.lineTo(x+15+sw,y+rh+h/4+1+h/2-4);
			rnapath.moveTo(x+w/2+sw+w,y+rh+h/4+9+h/2-4);
			rnapath.lineTo(x+sw+14,y+rh+h/4+9+h/2-4);
		}
		
		/**
		 * Method that does the job of painting a SelectorPanel
		 *
		 * @param g the Graphics environment that does the painting
		 */
		public void paintComponent(Graphics g){
			Graphics2D g2 = (Graphics2D) g;
			
			g2.setPaint(Color.darkGray);
			g2.draw(rnapath);
			g2.setPaint(Color.red);
			g2.draw(redpath);
			g2.setPaint(Color.black);
			g2.draw(area);
		}
	}
	
} 







