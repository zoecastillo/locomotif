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
 * This class defines the user interface for editing a stem element
 *
 * @author Janina Reeder
 */
public class StemEdit extends JFrame implements ActionListener, CaretListener{
	
	private static final long serialVersionUID = 1289055532107057947L;
	private Stem stem;
	private StemShape ss;
	private JPanel pane;
	private JTabbedPane tabbedPane;
	private JRadioButton deflen, exact, range, mloc53, mloc35, motif, basepair;
	private JRadioButton gdeflen, grange;
	private JRadioButton continuous, interrupted;
	private JButton ok, apply, cancel;
	private JLabel l1,l2,l3,sl1,mloc,bpl;
	private JLabel gl2,gl3;
	private JTextField t1, lowerend, upperend, seqm,bptf;
	private JTextField glowerend, gupperend;
	private String t1buf, lowerendbuf, upperendbuf, seqmbuf,bptfbuf;
	private String glowerendbuf, gupperendbuf;
	private DrawingSurface ds;
	private int type;
	
	/**
	 * Constructor for the StemEdit GUI
	 *
	 * @param ss the parent StemShape
	 * @param ds the parent DrawingSurface
	 * @param type determines which tab is shown at first
	 */
	public StemEdit(StemShape ss, DrawingSurface ds, int type){
		super("Stem Options");
		
		this.ss = ss;
		this.stem = ss.getStem();
		this.ds = ds;
		this.type = type;
		
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
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		tabbedPane = new JTabbedPane();
		
		
		//SIZE PANEL
		JPanel card1 = new JPanel();
		
		ButtonGroup bg = new ButtonGroup();
		
		JPanel defaultpanel = new JPanel();
		deflen = new JRadioButton("No size restriction");
		deflen.setToolTipText("Unrestricted stem contains at least 1 basepair");
		
		
		JPanel exactpanel = new JPanel();
		exact = new JRadioButton("Exact size");
		exact.setToolTipText("Specify an exact size for the stem");
		JPanel exactnumpanel = new JPanel();
		l1 = new JLabel("Number of basepairs: ");
		l1.setToolTipText("Give the exact number of basepairs for the stem");
		
		JPanel rangepanel = new JPanel();
		range = new JRadioButton("Size range");
		range.setToolTipText("Specify a size range for the stem");
		l2 = new JLabel("Minimum number of basepairs:  ");
		l2.setToolTipText("Give a minimum number of basepairs for the stem");
		l3 = new JLabel("Maximum number of basepairs: ");
		l3.setToolTipText("Give a maximum number of basepairs for the stem");
		JPanel rangevaluepanel1 = new JPanel();
		JPanel rangevaluepanel2 = new JPanel();
		
		if(stem.getIsDefaultLength()){
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
		else if(stem.getIsExactLength()){
			t1 = new JTextField(String.valueOf(stem.getLength()),3);
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
			if(!stem.getIsDefaultMin()){
				lowerend.setText(String.valueOf(stem.getMinLength()));
			}
			if(!stem.getIsDefaultMax()){
				upperend.setText(String.valueOf(stem.getMaxLength()));
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
		//END OF BOTTOM BUTTONS
		
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
		//END OF SIZE PANEL
		
		//SEQUENCE PANEL
		JPanel card2 = new JPanel();
		
		JPanel sequencepanel = new JPanel();
		JPanel sequencelabelpanel = new JPanel();
		JPanel sequencemotifpanel = new JPanel();
		JPanel sequencelocpanel = new JPanel();
		JPanel bplabelpanel = new JPanel();
		JPanel bpmotifpanel = new JPanel();
		
		motif = new JRadioButton("Sequence Motif");
		motif.setToolTipText("You can EITHER specify a motif on one strand");
		motif.setAlignmentX(Component.LEFT_ALIGNMENT);
		motif.setSelected(stem.getIsMotifGiven());
		motif.addActionListener(this);
		basepair = new JRadioButton("Basepairs");
		basepair.setToolTipText("OR you can specify a sequence of basepairs");
		basepair.setAlignmentX(Component.LEFT_ALIGNMENT);
		basepair.setSelected(!stem.getIsMotifGiven());
		basepair.addActionListener(this);
		
		ButtonGroup seqtype = new ButtonGroup();
		seqtype.add(motif);
		seqtype.add(basepair);
		
		sl1 = new JLabel("Enter a loop sequence motif:");
		sl1.setToolTipText("Motif must be Iupac characters. First character is first base in stem");
		sl1.setEnabled(stem.getIsMotifGiven());
		seqm = new JTextField(stem.getSeqMotif(),15);
		seqm.setEnabled(stem.getIsMotifGiven());
		bpl = new JLabel("Enter a string of basepairs:");
		bpl.setToolTipText("Format: \"GC;AU;NN;CG\". First basepair is first in stem");
		bpl.setEnabled(!stem.getIsMotifGiven());
		bptf = new JTextField(stem.getBasepairString(),15);
		bptf.setEnabled(!stem.getIsMotifGiven());
		
		sl1.setAlignmentX(Component.LEFT_ALIGNMENT);
		seqm.setAlignmentX(Component.LEFT_ALIGNMENT);
		seqm.setMaximumSize(seqm.getPreferredSize());
		seqm.addCaretListener(this);
		seqmbuf = seqm.getText();
		
		mloc = new JLabel("Motif location on:");
		mloc.setEnabled(stem.getIsMotifOn53Strand());
		mloc53 = new JRadioButton("5'-3' strand");
		mloc53.setSelected(stem.getIsMotifOn53Strand());
		mloc53.setEnabled(stem.getIsMotifGiven());
		mloc53.addActionListener(this);
		mloc35 = new JRadioButton("3'-5' strand");
		mloc35.setSelected(!stem.getIsMotifOn53Strand());
		mloc35.setEnabled(stem.getIsMotifGiven());
		mloc35.addActionListener(this);
		ButtonGroup mbg = new ButtonGroup();
		mbg.add(mloc53);
		mbg.add(mloc35);
		
		bpl.setAlignmentX(Component.LEFT_ALIGNMENT);
		bptf.setAlignmentX(Component.LEFT_ALIGNMENT);
		bptf.setMaximumSize(bptf.getPreferredSize());
		bptf.addCaretListener(this);
		bptfbuf = bptf.getText();
		
		sequencepanel.setLayout(new BoxLayout(sequencepanel, BoxLayout.PAGE_AXIS));
		
		sequencelabelpanel.setLayout(new BoxLayout(sequencelabelpanel, BoxLayout.LINE_AXIS));
		sequencelabelpanel.add(Box.createRigidArea(new Dimension(20,0)));
		sequencelabelpanel.add(sl1);
		
		sequencemotifpanel.setLayout(new BoxLayout(sequencemotifpanel, BoxLayout.LINE_AXIS));
		sequencemotifpanel.add(Box.createRigidArea(new Dimension(40,0)));
		sequencemotifpanel.add(seqm);
		
		sequencelocpanel.setLayout(new BoxLayout(sequencelocpanel, BoxLayout.LINE_AXIS));
		sequencelocpanel.add(Box.createRigidArea(new Dimension(20,0)));
		sequencelocpanel.add(mloc);
		sequencelocpanel.add(Box.createRigidArea(new Dimension(10,0)));
		sequencelocpanel.add(mloc53);
		sequencelocpanel.add(Box.createRigidArea(new Dimension(10,0)));
		sequencelocpanel.add(mloc35);
		
		bplabelpanel.setLayout(new BoxLayout(bplabelpanel, BoxLayout.LINE_AXIS));
		bplabelpanel.add(Box.createRigidArea(new Dimension(20,0)));
		bplabelpanel.add(bpl);
		
		bpmotifpanel.setLayout(new BoxLayout(bpmotifpanel, BoxLayout.LINE_AXIS));
		bpmotifpanel.add(Box.createRigidArea(new Dimension(40,0)));
		bpmotifpanel.add(bptf);
		
		sequencelabelpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		sequencemotifpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		sequencelocpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		bplabelpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		bpmotifpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		
		sequencepanel.add(Box.createRigidArea(new Dimension(0,10)));
		sequencepanel.add(motif);
		sequencepanel.add(Box.createRigidArea(new Dimension(0,10)));
		sequencepanel.add(sequencelabelpanel);
		sequencepanel.add(Box.createRigidArea(new Dimension(0,10)));
		sequencepanel.add(sequencemotifpanel);
		sequencepanel.add(Box.createRigidArea(new Dimension(0,10)));
		sequencepanel.add(sequencelocpanel);
		sequencepanel.add(Box.createRigidArea(new Dimension(0,20)));
		sequencepanel.add(basepair);
		sequencepanel.add(Box.createRigidArea(new Dimension(0,10)));
		sequencepanel.add(bplabelpanel);
		sequencepanel.add(Box.createRigidArea(new Dimension(0,10)));
		sequencepanel.add(bpmotifpanel);
		sequencepanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		sequencepanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		card2.setLayout(new BoxLayout(card2,BoxLayout.PAGE_AXIS));
		card2.add(sequencepanel);
		//END OF SEQUENCE PANEL
		
		//CONTINUITY PANEL
		JPanel card3 = new JPanel();
		
		JPanel continuitypanel = new JPanel();
		
		continuous = new JRadioButton("Enforce continuity");
		continuous.setToolTipText("Stem contains only consecutive basepairs");
		interrupted = new JRadioButton("Allow small loop interruptions");
		interrupted.setToolTipText("Interruptions of 1-2 unpaired bases on one or both strands allowed");
		
		if(!stem.getAllowInterrupt()){
			continuous.setSelected(true);
			interrupted.setSelected(false);
		}
		else{
			continuous.setSelected(false);
			interrupted.setSelected(true);
		}
		ButtonGroup bg2 = new ButtonGroup();
		bg2.add(continuous);
		bg2.add(interrupted);

		continuitypanel.setLayout(new BoxLayout(continuitypanel, BoxLayout.PAGE_AXIS));
		continuous.setAlignmentX(Component.LEFT_ALIGNMENT);
		continuous.addActionListener(this);
		continuitypanel.add(continuous);
		interrupted.setAlignmentX(Component.LEFT_ALIGNMENT);
		interrupted.addActionListener(this);
		continuitypanel.add(interrupted);
		
		continuitypanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		card3.setLayout(new BoxLayout(card3,BoxLayout.PAGE_AXIS));
		card3.add(continuitypanel);
		
//		GLOBAL SIZE PANEL
		JPanel card4 = new JPanel();
		
		ButtonGroup bg3 = new ButtonGroup();
		JPanel gdefaultpanel = new JPanel();
		gdeflen = new JRadioButton("No size restriction");
		gdeflen.setToolTipText("No global restrictions effective");
		
		JPanel grangepanel = new JPanel();
		grange = new JRadioButton("Global size range");
		gl2 = new JLabel("Minimum number of bases in substructure:  ");
		gl2.setToolTipText("Restrict the substructure rooted at this stem to a minimum number of bases");
		gl3 = new JLabel("Maximum number of bases in substructure: ");
		gl3.setToolTipText("Restrict the subtstructure rooted at this stem to a maximum number of bases");
		JPanel grangevaluepanel1 = new JPanel();
		JPanel grangevaluepanel2 = new JPanel();
		
		if(!stem.hasGlobalLength()){
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
			glowerend.setText(stem.getMinGlobalLength());
			gupperend.setText(stem.getMaxGlobalLength());
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
		
		
		
		
		tabbedPane.addTab("Stem Size", card1);
		tabbedPane.addTab("Sequence", card2);
		tabbedPane.addTab("Continuity", card3);
		tabbedPane.addTab("Global Size", card4);
		
		
		switch(type){
		case 2: tabbedPane.setSelectedIndex(1); break;		//Sequence
		case 8: tabbedPane.setSelectedIndex(2); break;		//Continuity
		case 9: tabbedPane.setSelectedIndex(3); break;		//Global Size
		default: tabbedPane.setSelectedIndex(0);			//Size
		}

		if(!stem.getIsDefaultLength() || stem.getSeqMotif() != null || stem.getBasepairs() != null){
			if(tabbedPane.getSelectedIndex() == 2){
				tabbedPane.setSelectedIndex(0);
			}
			tabbedPane.setEnabledAt(2,false);
		}
		
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
			apply.setEnabled(true);
		}
		if(src == range){
			if(stem.getAllowInterrupt()){
				int answer = JOptionPane.showConfirmDialog(null,"It is not possible to restrict the length of a stem with interruptions (at the moment).\nDo you wish to continue and enforce continuity on this stem?","Continuity conflict",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(answer == JOptionPane.NO_OPTION){
					return;
				}
				else{
					stem.setAllowInterrupt(false);
					continuous.setSelected(true);
				}
			}
			l1.setEnabled(false);
			t1.setText(null);
			t1buf = t1.getText();
			t1.setEnabled(false);
			l2.setEnabled(true);
			if(!stem.getIsDefaultMin() && stem.getMinLength()>0){
				lowerend.setText(String.valueOf(stem.getMinLength()));
				lowerendbuf = lowerend.getText();
			}
			lowerend.setEnabled(true);
			l3.setEnabled(true);
			if(!stem.getIsDefaultMax() && stem.getMaxLength()>0){
				upperend.setText(String.valueOf(stem.getMaxLength()));
				upperendbuf = upperend.getText();
			}
			upperend.setEnabled(true);
		}
		if(src == exact){
			if(stem.getAllowInterrupt()){
				int answer = JOptionPane.showConfirmDialog(null,"It is not possible to restrict the length of a stem with interruptions (at the moment).\nDo you wish to continue and enforce continuity on this stem?","Continuity conflict",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(answer == JOptionPane.NO_OPTION){
					return;
				}
				else{
					stem.setAllowInterrupt(false);
					continuous.setSelected(true);
				}
			}
			l1.setEnabled(true);
			t1.setText(String.valueOf(stem.getLength()));
			t1buf = t1.getText();
			t1.setEnabled(true);
			l2.setEnabled(false);
			lowerend.setEnabled(false);
			lowerend.setText(null);
			lowerendbuf = lowerend.getText();
			l3.setEnabled(false);
			upperend.setEnabled(false);
			upperend.setText(null);
			upperendbuf = upperend.getText();
		}
		
		if(src == motif){
			sl1.setEnabled(true);
			seqm.setEnabled(true);
			seqm.setText(stem.getSeqMotif());
			seqmbuf = seqm.getText();
			mloc.setEnabled(true);
			mloc53.setEnabled(stem.getIsMotifOn53Strand());
			mloc35.setEnabled(!stem.getIsMotifOn53Strand());
			bpl.setEnabled(false);
			bptf.setEnabled(false);
			bptf.setText(null);
			bptfbuf = bptf.getText();
		}
		if(src == basepair){
			sl1.setEnabled(false);
			seqm.setEnabled(false);
			seqm.setText(null);
			seqmbuf = seqm.getText();
			mloc.setEnabled(false);
			mloc53.setEnabled(false);
			mloc35.setEnabled(false);
			bpl.setEnabled(true);
			bptf.setEnabled(true);
			bptf.setText(stem.getBasepairString());
			bptfbuf = bptf.getText();
		}
		if(src == mloc53){
			stem.setIsMotifOn53Strand(true);
		}
		if(src == mloc35){
			stem.setIsMotifOn53Strand(false);
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
			glowerend.setText(stem.getMinGlobalLength());
			glowerendbuf = glowerend.getText();
			glowerend.setEnabled(true);
			gl3.setEnabled(true);
			gupperend.setText(stem.getMaxGlobalLength());
			gupperendbuf = gupperend.getText();
			gupperend.setEnabled(true);
		}
		if(src == continuous){
			if(stem.getAllowInterrupt()){
				apply.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else{
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(1,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == interrupted){
			if(!stem.getAllowInterrupt()){
				apply.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else{
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		//save changes and close window
		if(src == ok){
			//delete motif
			if(motif.isSelected() && (seqm.getText() == null || (seqm.getText()).length() == 0)){
				stem.setSeqMotif(null,true);
			}
			//delete basepairs
			else if(basepair.isSelected() && (bptf.getText() == null || (bptf.getText()).length() == 0)){
				stem.setBasepairs(null);
			}
			//no size restriction
			if(deflen.isSelected()){
			    //check and store motif
				if(seqm.getText() != null && !seqmbuf.equals(seqm.getText()) && (seqm.getText()).length() > 0){
					if(!Iupac.isCorrectMotif(seqm.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else{			
						JOptionPane.showMessageDialog(null, "Please note that the motif will automatically set to the 5' end of the stem.\nIf you do not wish for the motif to be placed there, please use two distinct stems!");
						stem.setSeqMotif(seqm.getText(),mloc53.isSelected());
					}
				}
				//check and store bps
				if(bptf.getText() != null && !bptfbuf.equals(bptf.getText()) && (bptf.getText()).length() > 0){
					if(!Iupac.isCorrectBPSeq(bptf.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The basepair sequence you specified is not in the correct format 'GC;NN;-;AU'\n or contains non-iupac characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else{	
						JOptionPane.showMessageDialog(null, "Please note that the motif will automatically set to the 5' end of the stem.\nIf you do not wish for the motif to be placed there, please use two distinct stems!");
						stem.setBasepairs(bptf.getText());
					}
				}
				stem.setToDefaultLength();
				ss.changeSize(stem.getLength());
				ss.beginnShift();
				ds.updateMotifHead();
			}
			//exact size
			else if(exact.isSelected()){
				try{
				    //none given: default
					if(t1.getText() == null || (t1.getText()).equals("")){
						deflen.doClick();
						return;
					}
					//parse and check size value
					int l = Integer.parseInt(t1.getText());
					if(l<(Stem.MIN)){
						throw new NumberFormatException("A Stem must contain at least "+(Stem.MIN)+" basepair.");
					}
					if(l>(Stem.MAX) && !t1buf.equals(t1.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"Stacked regions with more than "+(Stem.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Stem", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					//check and store motif
					if(seqm.getText() != null && (seqm.getText()).length() > 0){
						if((seqm.getText()).length()>l){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							else{
								stem.setSeqMotif(null,true);
							}
						}
						else if(!seqmbuf.equals(seqm.getText()) && !Iupac.isCorrectMotif(seqm.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
						else if(!seqmbuf.equals(seqm.getText()) && (seqm.getText()).length() < l){
							int retval = JOptionPane.showConfirmDialog(null, "Please note that the motif will automatically set to the 5' end of the stem.\nIf you do not wish for the motif to be placed there, please use two distinct stems!","Motif location",JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							else{
								stem.setSeqMotif(seqm.getText(),mloc53.isSelected());
							}
						}
						else{
							stem.setSeqMotif(seqm.getText(),mloc53.isSelected());
						}
					}
					//check and store bps
					if(bptf.getText() != null && (bptf.getText()).length() > 0){
						if(stem.checkBPLength(bptf.getText(),l) == 1){
							int retval = JOptionPane.showConfirmDialog(null,"Please specify only a maximum of "+l+" basepairs in the format 'GC;NN;-;AU'\nThe basepair sequence will be discarded if you continue.\nDo you wish to continue?", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							else{
								stem.setBasepairs(null);
							}
						}
						else if(!bptfbuf.equals(bptf.getText()) && !Iupac.isCorrectBPSeq(bptf.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The basepair sequence you specified is not in the correct format 'GC;NN;-;AU'\n or contains non-iupac characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
						else if(!bptfbuf.equals(bptf.getText()) && stem.checkBPLength(bptf.getText(),l) == -1){
							int retval = JOptionPane.showConfirmDialog(null, "Please note that the basepair sequence will automatically set to the 5' end of the stem.\nIf you do not wish for the sequence to be placed there, please use two distinct stems!","Motif location",JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							else{
								stem.setBasepairs(bptf.getText());
							}
						}
						else{
							stem.setBasepairs(bptf.getText());
						}
					}
					stem.setLength(l);
					ss.changeSize(stem.getLength());
					ss.beginnShift();
					ds.updateMotifHead();
				}
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			//size range
			else if(range.isSelected()){
				int low=0;
				int up=0;
				//parse and check values
				try{
					if(lowerend.getText() != null && !(lowerend.getText()).equals("")){
						low = Integer.parseInt(lowerend.getText());	
						if(low<(Stem.MIN)){
							throw new NumberFormatException("A Stem must contain at least "+(Stem.MIN)+" basepair.");
						}
						if(low>(Stem.MAX) && !lowerendbuf.equals(lowerend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Stacked regions with more than "+(Stem.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Stem", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
					}
					if(upperend.getText() != null && !(upperend.getText()).equals("")){
						up = Integer.parseInt(upperend.getText());
						if(up<(Stem.MIN)){
							throw new NumberFormatException("A Stem must contain at least "+(Stem.MIN)+" basepair.");
						}
						if(up>(Stem.MAX) && !upperendbuf.equals(upperend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Stacked regions with more than "+(Stem.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Stem", JOptionPane.OK_CANCEL_OPTION);
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
				//none given: default
				if(low == 0 && up == 0){
					deflen.doClick();
					return;
				}
				//check and store motif
				if(seqm.getText() != null && (seqm.getText()).length() > 0){
					if(up != 0 && (seqm.getText()).length()>up){
						int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
						else{
							stem.setSeqMotif(null,true);
						}
					}
					else if(!seqmbuf.equals(seqm.getText()) && !Iupac.isCorrectMotif(seqm.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else if(!seqmbuf.equals(seqm.getText()) && (up == 0 || (seqm.getText()).length() < up)){
						int retval = JOptionPane.showConfirmDialog(null, "Please note that the motif will automatically set to the 5' end of the stem.\nIf you do not wish for the motif to be placed there, please use two distinct stems!","Motif location",JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
						else{
							stem.setSeqMotif(seqm.getText(),mloc53.isSelected());
						}
					}
					else{
						stem.setSeqMotif(seqm.getText(),mloc53.isSelected());
					}
				}
				//check and store bps
				if(bptf.getText() != null && (bptf.getText()).length() > 0){
					if(up != 0 && (stem.checkBPLength(bptf.getText(),up) == 1)){
						int retval = JOptionPane.showConfirmDialog(null,"Please specify only a maximum of "+up+" basepairs in the format 'GC;NN;-;AU'\nThe basepair sequence will be discarded if you continue.\nDo you wish to continue?", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
						else{
							stem.setBasepairs(null);
						}
					}
					else if(!bptfbuf.equals(bptf.getText()) && !Iupac.isCorrectBPSeq(bptf.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The basepair sequence you specified is not in the correct format 'GC;NN;-;AU'\n or contains non-iupac characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else if(!bptfbuf.equals(bptf.getText()) && (up == 0 || stem.checkBPLength(bptf.getText(),up) == -1)){
						int retval = JOptionPane.showConfirmDialog(null, "Please note that the basepair sequence will automatically set to the 5' end of the stem.\nIf you do not wish for the sequence to be placed there, please use two distinct stems!","Motif location",JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
						else{
							stem.setBasepairs(bptf.getText());
						}
					}
					else{
						stem.setBasepairs(bptf.getText());
					}
				}
				//store size values
				if(low != 0){
					stem.setMinLength(low);
				}
				else{
					stem.setToDefaultMin();
				}
				if(up != 0){
					stem.setMaxLength(up);
				}
				else{
					stem.setToDefaultMax();
				}
				ss.changeSize(stem.getMinLength(),stem.getStemsMaxLength());
				ss.beginnShift();
				ds.updateMotifHead();
			}
			//no global size restriction
			if(gdeflen.isSelected()){
				stem.removeGlobalLength();
			}
			//global size
			else if(grange.isSelected()){
				int glow=0;
				int gup=0;
				//parse and check values
				try{
					if(glowerend.getText() != null && !(glowerend.getText()).equals("")){
						glow = Integer.parseInt(glowerend.getText());	
						if(glow<HairpinLoop.MIN){
							throw new NumberFormatException("The substructure of this Bulge must contain at least\na HairpinLoop with "+(HairpinLoop.MIN)+" bases.");
						}
					}
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
				//none given: default
				if(glow == 0 && gup == 0){
					gdeflen.doClick();
					return;
				}
				//store values
				if(glow != 0){
					stem.setMinGlobalLength(glow);
				}
				else{
					stem.removeGlobalMin();
				}
				if(gup != 0){
					stem.setMaxGlobalLength(gup);
				}
				else{
					stem.removeGlobalMax();
				}
			}
			ss.setSelected(false);
			//store interrupt value
			stem.setAllowInterrupt(interrupted.isSelected());
			ds.unselectShape();
			ds.repaint();
			dispose();
		}
		//save changes, keep window open
		if(src == apply){
			//delete motif
			if(motif.isSelected() && (seqm.getText() == null || (seqm.getText()).length() == 0)){
				stem.setSeqMotif(null,true);
			}
			//delete bps
			else if(basepair.isSelected() && (bptf.getText() == null || (bptf.getText()).length() == 0)){
				stem.setBasepairs(null);
			}
			//no size restriction
			if(deflen.isSelected()){
			    //Sequence pane
				if(tabbedPane.isEnabledAt(1)){
					if(seqm.getText() != null && !seqmbuf.equals(seqm.getText()) && (seqm.getText()).length() > 0){
						if(!Iupac.isCorrectMotif(seqm.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							seqm.setText(null);
						}
						else{	
							JOptionPane.showMessageDialog(null, "Please note that the motif will automatically set to the 5' end of the stem.\nIf you do not wish for the motif to be placed there, please use two distinct stems!","Motif location", JOptionPane.OK_CANCEL_OPTION);
							stem.setSeqMotif(seqm.getText(),mloc53.isSelected());
						}
						seqmbuf = seqm.getText();
					}
					if(bptf.getText() != null && !bptfbuf.equals(bptf.getText()) && (bptf.getText()).length() > 0){
						if(!Iupac.isCorrectBPSeq(bptf.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The basepair sequence you specified is not in the correct format 'GC;NN;-;AU'\n or contains non-iupac characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							bptf.setText(null);
						}
						else{	
							JOptionPane.showMessageDialog(null, "Please note that the motif will automatically set to the 5' end of the stem.\nIf you do not wish for the motif to be placed there, please use two distinct stems!","Motif location",JOptionPane.OK_CANCEL_OPTION);
							stem.setBasepairs(bptf.getText());
						}
						bptfbuf = bptf.getText();
					}
				}
				//Size pane
				else if(tabbedPane.isEnabledAt(0)){
					lowerend.setText(null);
					upperend.setText(null);
					t1.setText(null);
					t1buf = t1.getText();
					lowerendbuf = lowerend.getText();
					upperendbuf = upperend.getText();
					stem.setToDefaultLength();
					ss.changeSize(stem.getLength());
					ss.beginnShift();
					ds.updateMotifHead();
					sl1.setText("Enter a loop sequence motif:");
					bpl.setText("Enter a string of basepairs (use '-' for gaps):");
				}
				//enable interrupt pane, if no motif given
				if(stem.getSeqMotif() == null && stem.getBasepairs() == null){
					tabbedPane.setEnabledAt(2,true);
				}
			}
			//exact size
			else if(exact.isSelected()){
				try{
				    //non given: default
					if(t1.getText() == null || (t1.getText()).equals("")){
						deflen.doClick();
						return;
					}
					int l = Integer.parseInt(t1.getText());
					if(l<(Stem.MIN)){
						throw new NumberFormatException("A Stem must contain at least "+(Stem.MIN)+" basepair.");
					}
					if(l>(Stem.MAX) && !t1buf.equals(t1.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"Stacked regions with more than "+(Stem.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Stem", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					//disable interrupt pane
					tabbedPane.setEnabledAt(2,false);
					//Sequence pane
					if(tabbedPane.isEnabledAt(1)){
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
							stem.setSeqMotif(seqm.getText(),mloc53.isSelected());
							seqmbuf = seqm.getText();
						}
						if(bptf.getText() != null && !bptfbuf.equals(bptf.getText())){
							if(stem.checkBPLength(bptf.getText(),l) == 1){
								int retval = JOptionPane.showConfirmDialog(null,"Please specify only a maximum of "+l+" basepairs in the format 'GC;NN;-;AU'\nThe basepair sequence will be discarded if you continue.\nDo you wish to continue?", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								bptf.setText(null);
							}
							else if(!Iupac.isCorrectBPSeq(bptf.getText())){
								int retval = JOptionPane.showConfirmDialog(null,"The basepair sequence you specified is not in the correct format 'GC;NN;-;AU'\n or contains non-iupac characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								bptf.setText(null);
							}
							else{
								stem.setBasepairs(bptf.getText());
							}
							bptfbuf = bptf.getText();
						}
					}
					//Size pane
					else if(tabbedPane.isEnabledAt(0)){
						if(seqm.getText() != null){
							if((seqm.getText()).length()>l){
								int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								else{
									seqm.setText(null);
									stem.setSeqMotif(null,true);
									seqmbuf = seqm.getText();
								}
							}
						}
						if(bptf.getText() != null){
							if(stem.checkBPLength(bptf.getText(),l) == 1){
								int retval = JOptionPane.showConfirmDialog(null,"Please specify only a maximum of "+l+" basepairs in the format 'GC;NN;-;AU'\nThe basepair sequence will be discarded if you continue.\nDo you wish to continue?", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								else{
									bptf.setText(null);
									stem.setBasepairs(null);
									bptfbuf = bptf.getText();
								}
							}
						}
						lowerend.setText(null);
						upperend.setText(null);
						t1buf = t1.getText();
						lowerendbuf = lowerend.getText();
						upperendbuf = upperend.getText();
						stem.setLength(l);
						ss.changeSize(stem.getLength());
						ss.beginnShift();
						ds.updateMotifHead();
						sl1.setText("Enter a loop sequence motif:");
						bpl.setText("Enter a string of basepairs (use '-' for gaps):");
					}
				}
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			//Size range
			else if(range.isSelected()){
				int low=0;
				int up=0;
				try{
					if(lowerend.getText() != null && !(lowerend.getText()).equals("")){
						low = Integer.parseInt(lowerend.getText());	
						if(low<(Stem.MIN)){
							throw new NumberFormatException("A Stem must contain at least "+(Stem.MIN)+" basepair.");
						}
						if(low>(Stem.MAX) && !lowerendbuf.equals(lowerend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Stacked regions with more than "+(Stem.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Stem", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
					}
					if(upperend.getText() != null && !(upperend.getText()).equals("")){
						up = Integer.parseInt(upperend.getText());
						if(up<(Stem.MIN)){
							throw new NumberFormatException("A Stem must contain at least "+(Stem.MIN)+" basepair.");
						}
						if(up>(Stem.MAX) && ! upperendbuf.equals(upperend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Stacked regions with more than "+(Stem.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Stem", JOptionPane.OK_CANCEL_OPTION);
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
				//none given: default
				if(low == 0 && up == 0){
					deflen.doClick();
					return;
				}
				//disable interrupt pane
				tabbedPane.setEnabledAt(2,false);
				//Sequence pane
				if(tabbedPane.isEnabledAt(1)){
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
						stem.setSeqMotif(seqm.getText(),mloc53.isSelected());
						seqmbuf = seqm.getText();
					}
					if(bptf.getText() != null && !bptfbuf.equals(bptf.getText())){
						if(up != 0 && (stem.checkBPLength(bptf.getText(),up) == 1)){
							int retval = JOptionPane.showConfirmDialog(null,"Please specify only a maximum of "+up+" basepairs in the format 'GC;NN;-;AU'\nThe basepair sequence will be discarded if you continue.\nDo you wish to continue?", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							bptf.setText(null);
						}
						else if(!Iupac.isCorrectBPSeq(bptf.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The basepair sequence you specified is not in the correct format 'GC;NN;-;AU'\n or contains non-iupac characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
						else{
							stem.setBasepairs(bptf.getText());
						}
						bptfbuf = bptf.getText();
					}
				}
				//Size pane
				else if(tabbedPane.isEnabledAt(0)){
					if(seqm.getText() != null){
						if(up != 0 && (seqm.getText()).length()>up){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							else{
								seqm.setText(null);
								stem.setSeqMotif(null,true);
								seqmbuf = seqm.getText();
							}
						}
					}
					if(bptf.getText() != null){
						if(up != 0 && stem.checkBPLength(bptf.getText(),up) == 1){
							int retval = JOptionPane.showConfirmDialog(null,"Please specify only a maximum of "+up+" basepairs in the format 'GC;NN;-;AU'\nThe basepair sequence will be discarded if you continue.\nDo you wish to continue?", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							else{
								bptf.setText(null);
								stem.setBasepairs(null);
								bptfbuf = bptf.getText();
							}
						}
					}
					t1.setText(null);
					t1buf = t1.getText();
					lowerendbuf = lowerend.getText();
					upperendbuf = upperend.getText();
					if(low != 0){
						stem.setMinLength(low);
					}
					else{
						stem.setToDefaultMin();
					}
					if(up != 0){
						stem.setMaxLength(up);
					}
					else{
						stem.setToDefaultMax();
					}
					ss.changeSize(stem.getMinLength(),stem.getStemsMaxLength());
					ss.beginnShift();
					ds.updateMotifHead();
					sl1.setText("Enter a loop sequence motif:");
					bpl.setText("Enter a string of basepairs (use '-' for gaps):");
				}
			}
			//Global Size pane
			if(tabbedPane.isEnabledAt(3)){
			    if(gdeflen.isSelected()){
			        stem.removeGlobalLength();
			    }
			    else if(grange.isSelected()){
			        int glow=0;
			        int gup=0;
			        try{
			            if(glowerend.getText() != null && !(glowerend.getText()).equals("")){
			                glow = Integer.parseInt(glowerend.getText());	
			                if(glow<HairpinLoop.MIN){
			                    throw new NumberFormatException("The substructure of this Bulge must contain at least\na HairpinLoop with "+(HairpinLoop.MIN)+" bases.");
			                }
			            }
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
			        if(glow == 0 && gup == 0){
			            gdeflen.doClick();
			            return;
			        }
			        if(glow != 0){
			            stem.setMinGlobalLength(glow);
			        }
			        else{
			            stem.removeGlobalMin();
			        }
			        if(gup != 0){
			            stem.setMaxGlobalLength(gup);
			        }
			        else{
			            stem.removeGlobalMax();
			        }
			    }
			}
			stem.setAllowInterrupt(interrupted.isSelected());
			apply.setEnabled(false);
			tabbedPane.setEnabledAt(0,true);
			if(continuous.isSelected()){
				tabbedPane.setEnabledAt(1,true);
			}
			if(stem.getIsDefaultLength() && stem.getSeqMotif() == null && stem.getBasepairs() == null){
				tabbedPane.setEnabledAt(2,true);
			}
			tabbedPane.setEnabledAt(3,true);
			ds.repaint();
		}
		if(src == cancel){
			//no changes
			ss.setSelected(false);
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
				tabbedPane.setEnabledAt(2,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(bptfbuf.equals(bptf.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(2,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == bptf){
			if(!bptfbuf.equals(bptf.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(2,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(seqmbuf.equals(seqm.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(2,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == glowerend){
			if(!glowerendbuf.equals(glowerend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(2,false);
			}
			else if(gupperendbuf.equals(gupperend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(1,true);
				tabbedPane.setEnabledAt(2,true);
			}
		}
		if(src == gupperend){
			if(!gupperendbuf.equals(gupperend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(2,false);
			}
			else if(glowerendbuf.equals(glowerend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(1,true);
				tabbedPane.setEnabledAt(2,true);
			}
		}
	}
	
	public void dispose(){
	    ss.editClosed();
	    super.dispose();
	}
} 



