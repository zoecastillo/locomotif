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
 * This class defines the user interface for editing an internal loop element
 *
 * @author Janina Reeder
 */
public class InternalEdit extends JFrame implements ActionListener, CaretListener{
	
	private static final long serialVersionUID = 1957108096519228516L;
	private InternalLoop internal;
	private InternalShape is;
	private JPanel pane;
	private JTabbedPane tabbedPane;
	private JRadioButton loop53_deflen, loop53_exact, loop53_range;
	private JRadioButton loop35_deflen, loop35_exact, loop35_range;
	private JRadioButton gdeflen, grange;
	private JButton ok, apply, cancel;
	private JLabel loop53_l1,loop53_l2,loop53_l3,loop53_sl1;
	private JLabel loop35_l1,loop35_l2,loop35_l3,loop35_sl1;
	private JLabel gl2,gl3;
	private JLabel bp5, bp3;
	private JTextField loop53_t1, loop53_lowerend, loop53_upperend, loop53_seqm;
	private JTextField loop35_t1, loop35_lowerend, loop35_upperend, loop35_seqm;
	private JTextField glowerend, gupperend;
	private JTextField bp5tf, bp3tf;
	private String loop53_t1buf, loop53_lowerendbuf, loop53_upperendbuf, loop53_seqmbuf;
	private String loop35_t1buf, loop35_lowerendbuf, loop35_upperendbuf, loop35_seqmbuf;
	private String glowerendbuf, gupperendbuf;
	private String bp5tfbuf, bp3tfbuf;
	private DrawingSurface ds;
	private int type;
	
	/**
	 * Constructor for the InternalEdit GUI
	 *
	 * @param is the parent InternalShape
	 * @param ds the parent DrawingSurface
	 * @param type determines which tab is selected at first
	 */
	public InternalEdit(InternalShape is, DrawingSurface ds, int type){
		super("Internal Options");
		
		this.is = is;
		this.internal = is.getInternal();
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
		
		//SIZE PANEL 5'-3' LOOP
		JPanel card1 = new JPanel();
		
		ButtonGroup bg = new ButtonGroup();
		
		JPanel loop53_defaultpanel = new JPanel();
		loop53_deflen = new JRadioButton("No size restriction");
		loop53_deflen.setToolTipText("No size restriction stored for the loop on the 5' strand");
		
		JPanel loop53_exactpanel = new JPanel();
		loop53_exact = new JRadioButton("Exact size");
		loop53_exact.setToolTipText("Specify an exact size restriction for 5' strand loop");
		JPanel loop53_exactnumpanel = new JPanel();
		loop53_l1 = new JLabel("Number of bases in 5'-3' loop: ");
		loop53_l1.setToolTipText("Specify the number of bases contained in the 5' strand loop");
		
		JPanel loop53_rangepanel = new JPanel();
		loop53_range = new JRadioButton("Size range");
		loop53_range.setToolTipText("Specify a minimum and/or maximum number of bases for the 5' strand");
		loop53_l2 = new JLabel("Minimum number of bases in 5'-3' loop:  ");
		loop53_l2.setToolTipText("Specify the minimum number of bases in the loop on the 5' strand");
		loop53_l3 = new JLabel("Maximum number of bases in 5'-3' loop: ");
		loop53_l3.setToolTipText("Specify the maximum number of bases in the loop on the 5' strand");
		JPanel loop53_rangevaluepanel1 = new JPanel();
		JPanel loop53_rangevaluepanel2 = new JPanel();
		
		if(internal.getIsDefault53Length()){
			loop53_t1 = new JTextField(3);
			loop53_lowerend = new JTextField(3);
			loop53_upperend = new JTextField(3);
			loop53_l1.setEnabled(false);
			loop53_t1.setEnabled(false);
			loop53_l2.setEnabled(false);
			loop53_lowerend.setEnabled(false);
			loop53_l3.setEnabled(false);
			loop53_upperend.setEnabled(false);
			loop53_deflen.setSelected(true);
			loop53_exact.setSelected(false);
			loop53_range.setSelected(false);
		}
		else if(internal.getIsExact53Length()){
			loop53_t1 = new JTextField(String.valueOf(internal.get53Length()),3);
			loop53_lowerend = new JTextField(3);
			loop53_upperend = new JTextField(3);
			loop53_deflen.setSelected(false);
			loop53_exact.setSelected(true);
			loop53_range.setSelected(false);
			loop53_l1.setEnabled(true);
			loop53_t1.setEnabled(true);
			loop53_l2.setEnabled(false);
			loop53_lowerend.setEnabled(false);
			loop53_l3.setEnabled(false);
			loop53_upperend.setEnabled(false);
		}
		else{
			loop53_t1 = new JTextField(3);
			loop53_lowerend = new JTextField(3);
			loop53_upperend = new JTextField(3);
			if(!internal.getIsDefault53Min()){
				loop53_lowerend.setText(String.valueOf(internal.getMin53Length()));
			}
			if(!internal.getIsDefault53Max()){
				loop53_upperend.setText(String.valueOf(internal.getMax53Length()));
			}
			loop53_exact.setSelected(false);
			loop53_range.setSelected(true);
			loop53_l1.setEnabled(false);
			loop53_t1.setEnabled(false);
			loop53_l2.setEnabled(true);
			loop53_lowerend.setEnabled(true);
			loop53_l3.setEnabled(true);
			loop53_upperend.setEnabled(true);
		}
		
		loop53_defaultpanel.setLayout(new BoxLayout(loop53_defaultpanel, BoxLayout.PAGE_AXIS));
		loop53_deflen.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop53_deflen.addActionListener(this);
		loop53_defaultpanel.add(loop53_deflen);
		
		loop53_exactpanel.setLayout(new BoxLayout(loop53_exactpanel, BoxLayout.PAGE_AXIS));
		loop53_exact.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop53_exact.addActionListener(this);
		loop53_exactpanel.add(loop53_exact);
		
		loop53_exactnumpanel.setLayout(new BoxLayout(loop53_exactnumpanel, BoxLayout.LINE_AXIS));
		loop53_exactnumpanel.add(Box.createRigidArea(new Dimension(20,0)));
		loop53_exactnumpanel.add(loop53_l1);
		
		loop53_t1.setMaximumSize(loop53_t1.getPreferredSize());
		loop53_t1.addCaretListener(this);
		loop53_t1buf = loop53_t1.getText();
		loop53_exactnumpanel.add(loop53_t1);
		loop53_exactnumpanel.add(Box.createHorizontalGlue());
		loop53_exactnumpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop53_exactpanel.add(loop53_exactnumpanel);
		
		
		loop53_rangepanel.setLayout(new BoxLayout(loop53_rangepanel, BoxLayout.PAGE_AXIS));
		loop53_range.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop53_range.addActionListener(this);
		loop53_rangepanel.add(loop53_range);
		
		loop53_lowerend.setMaximumSize(loop53_lowerend.getPreferredSize());
		loop53_lowerend.addCaretListener(this);
		loop53_lowerendbuf = loop53_lowerend.getText();
		loop53_upperend.setMaximumSize(loop53_upperend.getPreferredSize());
		loop53_upperend.addCaretListener(this);
		loop53_upperendbuf = loop53_upperend.getText();
		loop53_rangevaluepanel1.setLayout(new BoxLayout(loop53_rangevaluepanel1, BoxLayout.LINE_AXIS));
		loop53_rangevaluepanel1.add(Box.createRigidArea(new Dimension(20,0)));
		loop53_rangevaluepanel1.add(loop53_l2);
		loop53_rangevaluepanel1.add(loop53_lowerend);
		loop53_rangevaluepanel1.add(Box.createHorizontalGlue());
		loop53_rangevaluepanel1.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop53_rangevaluepanel2.setLayout(new BoxLayout(loop53_rangevaluepanel2, BoxLayout.LINE_AXIS));
		loop53_rangevaluepanel2.add(Box.createRigidArea(new Dimension(20,0)));
		loop53_rangevaluepanel2.add(loop53_l3);
		loop53_rangevaluepanel2.add(loop53_upperend);
		loop53_rangevaluepanel2.add(Box.createHorizontalGlue());
		loop53_rangevaluepanel2.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop53_rangepanel.add(loop53_rangevaluepanel1);
		loop53_rangepanel.add(loop53_rangevaluepanel2);
		

		bg.add(loop53_deflen);
		bg.add(loop53_exact);
		bg.add(loop53_range);
		
		loop53_defaultpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop53_exactpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop53_rangepanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		card1.setLayout(new BoxLayout(card1,BoxLayout.PAGE_AXIS));
		card1.add(loop53_defaultpanel);
		card1.add(Box.createRigidArea(new Dimension(0,10)));
		card1.add(loop53_exactpanel);
		card1.add(Box.createRigidArea(new Dimension(0,10)));
		card1.add(loop53_rangepanel);
		//END OF SIZE PANEL 5'-3' LOOP
		
		//SIZE PANEL 3'-5' LOOP
		JPanel card2 = new JPanel();
		
		ButtonGroup bg2 = new ButtonGroup();

		JPanel loop35_defaultpanel = new JPanel();
		loop35_deflen = new JRadioButton("No size restriction");
		loop35_deflen.setToolTipText("No size restriction stored for the loop on the 3' strand");
		
		JPanel loop35_exactpanel = new JPanel();
		loop35_exact = new JRadioButton("Exact size");
		loop35_exact.setToolTipText("Specify an exact size restriction for the 3' strand loop");
		JPanel loop35_exactnumpanel = new JPanel();
		loop35_l1 = new JLabel("Number of bases in 3'-5' loop: ");
		loop35_l1.setToolTipText("Specify the number of bases contained in the 3' strand loop");
		
		JPanel loop35_rangepanel = new JPanel();
		loop35_range = new JRadioButton("Size range");
		loop35_range.setToolTipText("Specify a minimum and/or maximum number of bases for 3' strand loop");
		loop35_l2 = new JLabel("Minimum number of bases in 3'-5' loop:  ");
		loop35_l2.setToolTipText("Specify the minimum number of bases in the 3' loop");
		loop35_l3 = new JLabel("Maximum number of bases in 3'-5' loop: ");
		loop35_l3.setToolTipText("Specify the maximum number of bases in the 3' loop");
		JPanel loop35_rangevaluepanel1 = new JPanel();
		JPanel loop35_rangevaluepanel2 = new JPanel();
		
		if(internal.getIsDefault35Length()){
			loop35_t1 = new JTextField(3);
			loop35_lowerend = new JTextField(3);
			loop35_upperend = new JTextField(3);
			loop35_l1.setEnabled(false);
			loop35_t1.setEnabled(false);
			loop35_l2.setEnabled(false);
			loop35_lowerend.setEnabled(false);
			loop35_l3.setEnabled(false);
			loop35_upperend.setEnabled(false);
			loop35_deflen.setSelected(true);
			loop35_exact.setSelected(false);
			loop35_range.setSelected(false);
		}
		else if(internal.getIsExact35Length()){
			loop35_t1 = new JTextField(String.valueOf(internal.get35Length()),3);
			loop35_lowerend = new JTextField(3);
			loop35_upperend = new JTextField(3);
			loop35_deflen.setSelected(false);
			loop35_exact.setSelected(true);
			loop35_range.setSelected(false);
			loop35_l1.setEnabled(true);
			loop35_t1.setEnabled(true);
			loop35_l2.setEnabled(false);
			loop35_lowerend.setEnabled(false);
			loop35_l3.setEnabled(false);
			loop35_upperend.setEnabled(false);
		}
		else{
			loop35_t1 = new JTextField(3);
			loop35_lowerend = new JTextField(3);
			loop35_upperend = new JTextField(3);
			if(!internal.getIsDefault35Min()){
				loop35_lowerend.setText(String.valueOf(internal.getMin35Length()));
			}
			if(!internal.getIsDefault35Max()){
				loop35_upperend.setText(String.valueOf(internal.getMax35Length()));
			}
			loop35_exact.setSelected(false);
			loop35_range.setSelected(true);
			loop35_l1.setEnabled(false);
			loop35_t1.setEnabled(false);
			loop35_l2.setEnabled(true);
			loop35_lowerend.setEnabled(true);
			loop35_l3.setEnabled(true);
			loop35_upperend.setEnabled(true);
		}
		
		loop35_defaultpanel.setLayout(new BoxLayout(loop35_defaultpanel, BoxLayout.PAGE_AXIS));
		loop35_deflen.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop35_deflen.addActionListener(this);
		loop35_defaultpanel.add(loop35_deflen);
		
		loop35_exactpanel.setLayout(new BoxLayout(loop35_exactpanel, BoxLayout.PAGE_AXIS));
		loop35_exact.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop35_exact.addActionListener(this);
		loop35_exactpanel.add(loop35_exact);
		
		loop35_exactnumpanel.setLayout(new BoxLayout(loop35_exactnumpanel, BoxLayout.LINE_AXIS));
		loop35_exactnumpanel.add(Box.createRigidArea(new Dimension(20,0)));
		loop35_exactnumpanel.add(loop35_l1);
		
		loop35_t1.setMaximumSize(loop35_t1.getPreferredSize());
		loop35_t1.addCaretListener(this);
		loop35_t1buf = loop35_t1.getText();
		loop35_exactnumpanel.add(loop35_t1);
		loop35_exactnumpanel.add(Box.createHorizontalGlue());
		loop35_exactnumpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop35_exactpanel.add(loop35_exactnumpanel);
		
		
		loop35_rangepanel.setLayout(new BoxLayout(loop35_rangepanel, BoxLayout.PAGE_AXIS));
		loop35_range.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop35_range.addActionListener(this);
		loop35_rangepanel.add(loop35_range);
		
		loop35_lowerend.setMaximumSize(loop35_lowerend.getPreferredSize());
		loop35_lowerend.addCaretListener(this);
		loop35_lowerendbuf = loop35_lowerend.getText();
		loop35_upperend.setMaximumSize(loop35_upperend.getPreferredSize());
		loop35_upperend.addCaretListener(this);
		loop35_upperendbuf = loop35_upperend.getText();
		loop35_rangevaluepanel1.setLayout(new BoxLayout(loop35_rangevaluepanel1, BoxLayout.LINE_AXIS));
		loop35_rangevaluepanel1.add(Box.createRigidArea(new Dimension(20,0)));
		loop35_rangevaluepanel1.add(loop35_l2);
		loop35_rangevaluepanel1.add(loop35_lowerend);
		loop35_rangevaluepanel1.add(Box.createHorizontalGlue());
		loop35_rangevaluepanel1.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop35_rangevaluepanel2.setLayout(new BoxLayout(loop35_rangevaluepanel2, BoxLayout.LINE_AXIS));
		loop35_rangevaluepanel2.add(Box.createRigidArea(new Dimension(20,0)));
		loop35_rangevaluepanel2.add(loop35_l3);
		loop35_rangevaluepanel2.add(loop35_upperend);
		loop35_rangevaluepanel2.add(Box.createHorizontalGlue());
		loop35_rangevaluepanel2.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop35_rangepanel.add(loop35_rangevaluepanel1);
		loop35_rangepanel.add(loop35_rangevaluepanel2);
		

		bg2.add(loop35_deflen);
		bg2.add(loop35_exact);
		bg2.add(loop35_range);
		
		loop35_defaultpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop35_exactpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop35_rangepanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		card2.setLayout(new BoxLayout(card2,BoxLayout.PAGE_AXIS));
		card2.add(loop35_defaultpanel);
		card2.add(Box.createRigidArea(new Dimension(0,10)));
		card2.add(loop35_exactpanel);
		card2.add(Box.createRigidArea(new Dimension(0,10)));
		card2.add(loop35_rangepanel);
		//END OF SIZE PANEL 3'-5' LOOP
			
		//SEQUENCE MOTIF PANEL
		JPanel card3 = new JPanel();
		
		JPanel sequencepanel = new JPanel();
		JPanel loop53_sequencelabelpanel = new JPanel();
		JPanel loop53_sequencemotifpanel = new JPanel();
		
		loop53_sl1 = new JLabel("Enter a sequence motif for 5'-3' loop:");
		loop53_sl1.setToolTipText("Specify a sequence motif located in the loop on the 5' strand");
		loop53_seqm = new JTextField(internal.getLoop53Motif(),15);
		
		loop53_sl1.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop53_seqm.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop53_seqm.setMaximumSize(loop53_seqm.getPreferredSize());
		loop53_seqm.setToolTipText("<html>Matcher will check for motif anywhere in the loop.<br>Use \"N\" to enforce a specific location!</html>");
		loop53_seqm.addCaretListener(this);
		loop53_seqmbuf = loop53_seqm.getText();
		
		sequencepanel.setLayout(new BoxLayout(sequencepanel, BoxLayout.PAGE_AXIS));
		
		loop53_sequencelabelpanel.setLayout(new BoxLayout(loop53_sequencelabelpanel, BoxLayout.LINE_AXIS));
		loop53_sequencelabelpanel.add(Box.createRigidArea(new Dimension(20,0)));
		loop53_sequencelabelpanel.add(loop53_sl1);
		
		loop53_sequencemotifpanel.setLayout(new BoxLayout(loop53_sequencemotifpanel, BoxLayout.LINE_AXIS));
		loop53_sequencemotifpanel.add(Box.createRigidArea(new Dimension(40,0)));
		loop53_sequencemotifpanel.add(loop53_seqm);
		
		loop53_sequencelabelpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop53_sequencemotifpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		sequencepanel.add(Box.createRigidArea(new Dimension(0,10)));
		sequencepanel.add(loop53_sequencelabelpanel);
		sequencepanel.add(Box.createRigidArea(new Dimension(0,10)));
		sequencepanel.add(loop53_sequencemotifpanel);
		
		JPanel loop35_sequencelabelpanel = new JPanel();
		JPanel loop35_sequencemotifpanel = new JPanel();
		
		loop35_sl1 = new JLabel("Enter a sequence motif for 3'-5' loop:");
		loop35_sl1.setToolTipText("Specify a sequence motif located in the loop on the 3' strand");
		loop35_seqm = new JTextField(internal.getLoop35Motif(),15);
		
		loop35_sl1.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop35_seqm.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop35_seqm.setMaximumSize(loop35_seqm.getPreferredSize());
		loop35_seqm.setToolTipText("<html>Matcher will check for motif anywhere in the loop.<br>Use \"N\" to enforce a specific location!</html>");
		loop35_seqm.addCaretListener(this);
		loop35_seqmbuf = loop35_seqm.getText();
		
		sequencepanel.setLayout(new BoxLayout(sequencepanel, BoxLayout.PAGE_AXIS));
		
		loop35_sequencelabelpanel.setLayout(new BoxLayout(loop35_sequencelabelpanel, BoxLayout.LINE_AXIS));
		loop35_sequencelabelpanel.add(Box.createRigidArea(new Dimension(20,0)));
		loop35_sequencelabelpanel.add(loop35_sl1);
		
		loop35_sequencemotifpanel.setLayout(new BoxLayout(loop35_sequencemotifpanel, BoxLayout.LINE_AXIS));
		loop35_sequencemotifpanel.add(Box.createRigidArea(new Dimension(40,0)));
		loop35_sequencemotifpanel.add(loop35_seqm);
		
		loop35_sequencelabelpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		loop35_sequencemotifpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		sequencepanel.add(Box.createRigidArea(new Dimension(0,10)));
		sequencepanel.add(loop35_sequencelabelpanel);
		sequencepanel.add(Box.createRigidArea(new Dimension(0,10)));
		sequencepanel.add(loop35_sequencemotifpanel);
		
		
		JPanel bp5panel = new JPanel();
		bp5panel.setLayout(new BoxLayout(bp5panel, BoxLayout.LINE_AXIS));
		
		bp5 = new JLabel("5' Basepair:");
		bp5.setToolTipText("<html>Basepair closer to the 5' end<br>Format: 'ag'; first base on 5' strand, second on 3' strand</html>");
		bp5.setAlignmentX(Component.LEFT_ALIGNMENT);
		bp5tf = new JTextField(internal.getBasepairString(0),2);
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
		bp3tf = new JTextField(internal.getBasepairString(1),2);
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
		card3.add(Box.createRigidArea(new Dimension(0,10)));
		//END OF SEQUENCE MOTIF PANEL
		
//		GLOBAL SIZE PANEL
		JPanel card4 = new JPanel();
		
		ButtonGroup bg3 = new ButtonGroup();
		JPanel gdefaultpanel = new JPanel();
		gdeflen = new JRadioButton("No size restriction");
		gdeflen.setToolTipText("No global size restriction effective upon the substructure rooted at this internal loop");
		
		JPanel grangepanel = new JPanel();
		grange = new JRadioButton("Global size range");
		grange.setToolTipText("Specify a size restriction for the substructure rooted at this building block");
		gl2 = new JLabel("Minimum number of bases in substructure:  ");
		gl2.setToolTipText("Minimum for substructure including this building block");
		gl3 = new JLabel("Maximum number of bases in substructure: ");
		gl3.setToolTipText("Maximum for substructure including this building block");
		JPanel grangevaluepanel1 = new JPanel();
		JPanel grangevaluepanel2 = new JPanel();
		
		if(!internal.hasGlobalLength()){
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
			glowerend.setText(internal.getMinGlobalLength());
			gupperend.setText(internal.getMaxGlobalLength());
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
		
		//END OF SIZE PANEL
		
		
		tabbedPane.addTab("5'-3' loop", card1);
		tabbedPane.addTab("3'-5' loop", card2);
		tabbedPane.addTab("Sequence", card3);
		tabbedPane.addTab("Global size", card4);
		
		switch(type){
		case 2: tabbedPane.setSelectedIndex(2); break;		//Sequence
		case 5: tabbedPane.setSelectedIndex(1); break;		//35 Size
		case 9: tabbedPane.setSelectedIndex(3); break;		//global size
		default: tabbedPane.setSelectedIndex(0);			//53 Size
		}
		
		pane.add(tabbedPane);
		
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
		buttonpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		buttonpanel.add(Box.createHorizontalGlue());
		buttonpanel.add(ok);
		buttonpanel.add(apply);
		buttonpanel.add(cancel);
		//END OF BOTTOM BUTTONS
		
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
		if(src == loop53_deflen){
			loop53_l1.setEnabled(false);
			loop53_t1.setText(null);
			loop53_t1buf = loop53_t1.getText();
			loop53_t1.setEnabled(false);
			loop53_l2.setEnabled(false);
			loop53_lowerend.setText(null);
			loop53_lowerendbuf = loop53_lowerend.getText();
			loop53_lowerend.setEnabled(false);
			loop53_l3.setEnabled(false);
			loop53_upperend.setText(null);
			loop53_upperendbuf = loop53_upperend.getText();
			loop53_upperend.setEnabled(false);
		}
		if(src == loop53_range){
			loop53_l1.setEnabled(false);
			loop53_t1.setText(null);
			loop53_t1buf = loop53_t1.getText();
			loop53_t1.setEnabled(false);
			loop53_l2.setEnabled(true);
			if(!internal.getIsDefault53Min() && internal.getMin53Length()>0){
				loop53_lowerend.setText(String.valueOf(internal.getMin53Length()));
				loop53_lowerendbuf = loop53_lowerend.getText();
			}
			loop53_lowerend.setEnabled(true);
			loop53_l3.setEnabled(true);
			if(!internal.getIsDefault53Max() && internal.getMax53Length()>0){
				loop53_upperend.setText(String.valueOf(internal.getMax53Length()));
				loop53_upperendbuf = loop53_upperend.getText();
			}
			loop53_upperend.setEnabled(true);
		}
		if(src == loop53_exact){
			loop53_l1.setEnabled(true);
			loop53_t1.setText(String.valueOf(internal.get53Length()));
			loop53_t1buf = loop53_t1.getText();
			loop53_t1.setEnabled(true);
			loop53_l2.setEnabled(false);
			loop53_lowerend.setEnabled(false);
			loop53_l3.setEnabled(false);
			loop53_upperend.setEnabled(false);
		}
		if(src == loop35_deflen){
			loop35_l1.setEnabled(false);
			loop35_t1.setText(null);
			loop35_t1buf = loop35_t1.getText();
			loop35_t1.setEnabled(false);
			loop35_l2.setEnabled(false);
			loop35_lowerend.setText(null);
			loop35_lowerendbuf = loop35_lowerend.getText();
			loop35_lowerend.setEnabled(false);
			loop35_l3.setEnabled(false);
			loop35_upperend.setText(null);
			loop35_upperendbuf = loop35_upperend.getText();
			loop35_upperend.setEnabled(false);
		}
		if(src == loop35_range){
			loop35_l1.setEnabled(false);
			loop35_t1.setText(null);
			loop35_t1buf = loop35_t1.getText();
			loop35_t1.setEnabled(false);
			loop35_l2.setEnabled(true);
			if(!internal.getIsDefault35Min() && internal.getMin35Length()>0){
				loop35_lowerend.setText(String.valueOf(internal.getMin35Length()));
				loop35_lowerendbuf = loop35_lowerend.getText();
			}
			loop35_lowerend.setEnabled(true);
			loop35_l3.setEnabled(true);
			if(!internal.getIsDefault35Max() && internal.getMax35Length()>0){
				loop35_upperend.setText(String.valueOf(internal.getMax35Length()));
				loop35_upperendbuf = loop35_upperend.getText();
			}
			loop35_upperend.setEnabled(true);
		}
		if(src == loop35_exact){
			loop35_l1.setEnabled(true);
			loop35_t1.setText(String.valueOf(internal.get35Length()));
			loop35_t1buf = loop35_t1.getText();
			loop35_t1.setEnabled(true);
			loop35_l2.setEnabled(false);
			loop35_lowerend.setEnabled(false);
			loop35_l3.setEnabled(false);
			loop35_upperend.setEnabled(false);
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
			glowerend.setText(internal.getMinGlobalLength());
			glowerendbuf = glowerend.getText();
			glowerend.setEnabled(true);
			gl3.setEnabled(true);
			gupperend.setText(internal.getMaxGlobalLength());
			gupperendbuf = gupperend.getText();
			gupperend.setEnabled(true);
		}
		//save changes and close window
		if(src == ok){
			//save changes
		    
		    //default length for 5' loop
			if(loop53_deflen.isSelected()){
			    //parse and store sequence motif
				if(loop53_seqm.getText() != null && !loop53_seqmbuf.equals(loop53_seqm.getText())){
					if(!Iupac.isCorrectMotif(loop53_seqm.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 5'-3' strand", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else{
						internal.setLoop53Motif(loop53_seqm.getText());
					}
				}
				internal.setToDefault53Length();
				is.change53Size(internal.get53Length());
			}
			
			//exact value for 5' loop
			else if(loop53_exact.isSelected()){
				try{
				    //none given: default
					if(loop53_t1.getText() == null || (loop53_t1.getText()).equals("")){
						loop53_deflen.doClick();
						return;
					}
					//parse and check value
					int l = Integer.parseInt(loop53_t1.getText());
					if(l<(InternalLoop.MIN)){
						throw new NumberFormatException("An Internalloop must contain at least "+(InternalLoop.MIN)+" base.");
					}
					if(l>(InternalLoop.MAX) && !loop53_t1buf.equals(loop53_t1.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(InternalLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					//sequence motif
					if(loop53_seqm.getText() != null){
						if((loop53_seqm.getText()).length()>l){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 5'-3' strand", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							else{
								internal.setLoop53Motif(null);
							}
						}
						else if(!loop53_seqmbuf.equals(loop53_seqm.getText()) && !Iupac.isCorrectMotif(loop53_seqm.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 5'-3' strand", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
						else{
							internal.setLoop53Motif(loop53_seqm.getText());
						}
					}
					internal.set53Length(l);
					is.change53Size(internal.get53Length()); //change bulge size
				}
				
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			//range values for 5' loop
			else if(loop53_range.isSelected()){
				int low=0;
				int up=0;
				//parse and check values
				try{
					if(loop53_lowerend.getText() != null && !(loop53_lowerend.getText()).equals("")){
						low = Integer.parseInt(loop53_lowerend.getText());	
						if(low<(InternalLoop.MIN)){
							throw new NumberFormatException("An Internalloop must contain at least "+(InternalLoop.MIN)+" base.");
						}
						if(low>(InternalLoop.MAX) && !loop53_lowerendbuf.equals(loop53_lowerend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(InternalLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
					}
					if(loop53_upperend.getText() != null && !(loop53_upperend.getText()).equals("")){
						up = Integer.parseInt(loop53_upperend.getText());
						if(up<(InternalLoop.MIN)){
							throw new NumberFormatException("An Internalloop must contain at least "+(InternalLoop.MIN)+" base.");
						}
						if(up>(InternalLoop.MAX) && !loop53_upperendbuf.equals(loop53_upperend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(InternalLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
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
				//non given: default
				if(low == 0 && up == 0){
					loop53_deflen.doClick();
					return;
				}
				//sequence motif
				if(loop53_seqm.getText() != null){
					if(up != 0 && (loop53_seqm.getText()).length()>up){
						int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 5'-3' strand", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
						else{
							internal.setLoop53Motif(null);
						}
					}
					else if(!loop53_seqmbuf.equals(loop53_seqm.getText()) && !Iupac.isCorrectMotif(loop53_seqm.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 5'-3' strand", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else{
						internal.setLoop53Motif(loop53_seqm.getText());
					}
				}
				if(low != 0){
					internal.setMin53Length(low);
				}
				else{
					internal.setToDefault53Min();
				}
				if(up != 0){
					internal.setMax53Length(up);
				}
				else{
					internal.setToDefault53Max();
				}
				is.change53Size(internal.getMin53Length(),internal.getMax53Length());
			}
			
			//no restriction for 3' loop
			if(loop35_deflen.isSelected()){
				if(loop35_seqm.getText() != null && !loop35_seqmbuf.equals(loop35_seqm.getText())){
					if(!Iupac.isCorrectMotif(loop35_seqm.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 3'-5' strand", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else{
						internal.setLoop35Motif(loop35_seqm.getText());
					}
				}
				internal.setToDefault35Length();
				is.change35Size(internal.get35Length());
			}
			//exact value for 3' loop
			else if(loop35_exact.isSelected()){
				try{
					if(loop35_t1.getText() == null || (loop35_t1.getText()).equals("")){
						loop35_deflen.doClick();
						return;
					}
					int l = Integer.parseInt(loop35_t1.getText());
					if(l<(InternalLoop.MIN)){
						throw new NumberFormatException("An Internalloop must contain at least "+(InternalLoop.MIN)+" base.");
					}
					if(l>(InternalLoop.MAX) && !loop35_t1buf.equals(loop35_t1.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(InternalLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					if(loop35_seqm.getText() != null){
						if((loop35_seqm.getText()).length()>l){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 3'-5' strand", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							else{
								internal.setLoop35Motif(null);
							}
						}
						else if(!loop35_seqmbuf.equals(loop35_seqm.getText()) && !Iupac.isCorrectMotif(loop35_seqm.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 3'-5' strand", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
						else{
							internal.setLoop35Motif(loop35_seqm.getText());
						}
					}
					internal.set35Length(l);
					is.change35Size(internal.get35Length()); //change bulge size
				}
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			//range values for 3' loop
			else if(loop35_range.isSelected()){
				int low=0;
				int up=0;
				try{
					if(loop35_lowerend.getText() != null && !(loop35_lowerend.getText()).equals("")){
						low = Integer.parseInt(loop35_lowerend.getText());
						if(low<(InternalLoop.MIN)){
							throw new NumberFormatException("An Internalloop must contain at least "+(InternalLoop.MIN)+" base.");
						}
						if(low>(InternalLoop.MAX) && !loop35_lowerendbuf.equals(loop35_lowerend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(InternalLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}	
					}
					if(loop35_upperend.getText() != null && !(loop35_upperend.getText()).equals("")){
						up = Integer.parseInt(loop35_upperend.getText());
						if(up<(InternalLoop.MIN)){
							throw new NumberFormatException("An Internalloop must contain at least "+(InternalLoop.MIN)+" base.");
						}
						if(up>(InternalLoop.MAX) && !loop35_upperendbuf.equals(loop35_upperend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(InternalLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
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
				if(low == 0 && up == 0){
					loop35_deflen.doClick();
					return;
				}
				//sequence motif
				if(loop35_seqm.getText() != null && !loop35_seqmbuf.equals(loop35_seqm.getText())){
					if(up != 0 && (loop35_seqm.getText()).length()>up){
						int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 3'-5' strand", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
						else{
							internal.setLoop35Motif(null);
						}
					}
					else if(!loop35_seqmbuf.equals(loop35_seqm.getText()) && !Iupac.isCorrectMotif(loop35_seqm.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 3'-5' strand", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else{
						internal.setLoop35Motif(loop35_seqm.getText());
					}
				}
				if(low != 0){
					internal.setMin35Length(low);
				}
				else{
					internal.setToDefault35Min();
				}
				if(up != 0){
					internal.setMax35Length(up);
				}
				else{
					internal.setToDefault35Max();
				}
				is.change35Size(internal.getMin35Length(),internal.getMax35Length());
			}
			//5' basepair
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
					internal.setBasepair(bp5tf.getText(),0);
				}
			}
			//3' basepair
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
					internal.setBasepair(bp3tf.getText(),1);
				}
			}
			
			//no global size restriction
			if(gdeflen.isSelected()){
				internal.removeGlobalLength();
			}
			//global size range
			else if(grange.isSelected()){
				int glow=0;
				int gup=0;
				//check and parse values
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
				//non given: default
				if(glow == 0 && gup == 0){
					gdeflen.doClick();
					return;
				}
				//store values
				if(glow != 0){
					internal.setMinGlobalLength(glow);
				}
				else{
					internal.removeGlobalMin();
				}
				if(gup != 0){
					internal.setMaxGlobalLength(gup);
				}
				else{
					internal.removeGlobalMax();
				}
			}
			is.setSelected(false); 
			ds.unselectShape();
			ds.repaint();
			dispose();
		}
		
		//store changes and keep window open
		if(src == apply){
			//no size restriction for 5' loop
			if(loop53_deflen.isSelected()){
			    //Sequence pane
				if(tabbedPane.isEnabledAt(2)){
					if(loop53_seqm.getText() != null && !loop53_seqmbuf.equals(loop53_seqm.getText())){
						if(!Iupac.isCorrectMotif(loop53_seqm.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 5'-3' strand", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							loop53_seqm.setText(null);
						}
						else{
							internal.setLoop53Motif(loop53_seqm.getText());
						}
						loop53_seqmbuf = loop53_seqm.getText();
					}
				}
				//5' loop size pane
				else if(tabbedPane.isEnabledAt(0)){
					loop53_lowerend.setText(null);
					loop53_upperend.setText(null);
					loop53_t1.setText(null);
					loop53_t1buf = loop53_t1.getText();
					loop53_lowerendbuf = loop53_lowerend.getText();
					loop53_upperendbuf = loop53_upperend.getText();
					internal.setToDefault53Length();
					is.change53Size(internal.get53Length());
				}
			}
			//exact size for 5' loop
			else if(loop53_exact.isSelected()){
				try{
				    //none given: default
					if(loop53_t1.getText() == null || (loop53_t1.getText()).equals("")){
						loop53_deflen.doClick();
						return;
					}
					int l = Integer.parseInt(loop53_t1.getText());
					if(l<(InternalLoop.MIN)){
						throw new NumberFormatException("An Internalloop must contain at least "+(InternalLoop.MIN)+" base.");
					}
					if(l>(InternalLoop.MAX) && !loop53_t1buf.equals(loop53_t1.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(InternalLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					//Sequence pane
					if(tabbedPane.isEnabledAt(2)){
						if(loop53_seqm.getText() != null && !loop53_seqmbuf.equals(loop53_seqm.getText())){
							if((loop53_seqm.getText()).length()>l){
								int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 5'-3' strand", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								loop53_seqm.setText(null);
							}
							else if(!Iupac.isCorrectMotif(loop53_seqm.getText())){
								int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 5'-3' strand", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								loop53_seqm.setText(null);
							}
							else{
								internal.setLoop53Motif(loop53_seqm.getText());
							}
							loop53_seqmbuf = loop53_seqm.getText();
						}
					}
					//5' loop size pane
					else if(tabbedPane.isEnabledAt(0)){
						if(loop53_seqm.getText() != null){
							if((loop53_seqm.getText()).length()>l){
								int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 5'-3' strand", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								loop53_seqm.setText(null);
								internal.setLoop53Motif(null);
								loop53_seqmbuf = loop53_seqm.getText();
							}
						}
						loop53_lowerend.setText(null);
						loop53_upperend.setText(null);
						loop53_t1buf = loop53_t1.getText();
						loop53_lowerendbuf = loop53_lowerend.getText();
						loop53_upperendbuf = loop53_upperend.getText();
						internal.set53Length(l);
						is.change53Size(internal.get53Length());
					}
				}
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			//5' loop size range
			else if(loop53_range.isSelected()){
				int low=0;
				int up=0;
				try{
					if(loop53_lowerend.getText() != null && !(loop53_lowerend.getText()).equals("")){
						low = Integer.parseInt(loop53_lowerend.getText());	
						if(low<(InternalLoop.MIN)){
							throw new NumberFormatException("An Internalloop must contain at least "+(InternalLoop.MIN)+" base.");
						}
						if(low>(InternalLoop.MAX) && !loop53_lowerendbuf.equals(loop53_lowerend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(InternalLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
					}
					if(loop53_upperend.getText() != null && !(loop53_upperend.getText()).equals("")){
						up = Integer.parseInt(loop53_upperend.getText());
						if(up<(InternalLoop.MIN)){
							throw new NumberFormatException("An Internalloop must contain at least "+(InternalLoop.MIN)+" base.");
						}
						if(up>(InternalLoop.MAX) && !loop53_upperendbuf.equals(loop53_upperend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(InternalLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
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
					loop53_deflen.doClick();
					return;
				}
				//Sequence pane
				if(tabbedPane.isEnabledAt(2)){
					if(loop53_seqm.getText() != null && !loop53_seqmbuf.equals(loop53_seqm.getText())){
						if(up != 0 && (loop53_seqm.getText()).length()>up){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 5'-3' strand", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							loop53_seqm.setText(null);
						}
						else if(!Iupac.isCorrectMotif(loop53_seqm.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 5'-3' strand", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							loop53_seqm.setText(null);
						}
						else{
							internal.setLoop53Motif(loop53_seqm.getText());
						}
						loop53_seqmbuf = loop53_seqm.getText();
					}
				}
				//5' loop size pane
				else if(tabbedPane.isEnabledAt(0)){
					if(loop53_seqm.getText() != null){
						if(up != 0 && (loop53_seqm.getText()).length()>up){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 5'-3' strand", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							loop53_seqm.setText(null);
							internal.setLoop53Motif(null);
							loop53_seqmbuf = loop53_seqm.getText();
						}
					}
					loop53_t1.setText(null);
					loop53_t1buf = loop53_t1.getText();
					loop53_lowerendbuf = loop53_lowerend.getText();
					loop53_upperendbuf = loop53_upperend.getText();
					if(low != 0){
						internal.setMin53Length(low);
					}
					else{
						internal.setToDefault53Min();
					}
					if(up != 0){
						internal.setMax53Length(up);
					}
					else{
						internal.setToDefault53Max();
					}
					is.change53Size(internal.getMin53Length(),internal.getMax53Length());
				}
			}
			
			//no size restrction for 3' loop
			if(loop35_deflen.isSelected()){
			    //Sequence pane
				if(tabbedPane.isEnabledAt(2)){
					if(loop35_seqm.getText() != null && !loop35_seqmbuf.equals(loop35_seqm.getText())){
						if(!Iupac.isCorrectMotif(loop35_seqm.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 3'-5' strand", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							loop35_seqm.setText(null);
						}
						else{
							internal.setLoop35Motif(loop35_seqm.getText());
						}
						loop35_seqmbuf = loop35_seqm.getText();
					}
				}
				//3' loop size pane
				else if(tabbedPane.isEnabledAt(1)){
					loop35_lowerend.setText(null);
					loop35_upperend.setText(null);
					loop35_t1.setText(null);
					loop35_t1buf = loop35_t1.getText();
					loop35_lowerendbuf = loop35_lowerend.getText();
					loop35_upperendbuf = loop35_upperend.getText();
					internal.setToDefault35Length();
					is.change35Size(internal.get35Length());
				}
			}
			//exact size for 3' loop
			else if(loop35_exact.isSelected()){
				try{
				    //none given: default
					if(loop35_t1.getText() == null || (loop35_t1.getText()).equals("")){
						loop35_deflen.doClick();
						return;
					}
					int l = Integer.parseInt(loop35_t1.getText());
					if(l<(InternalLoop.MIN)){
						throw new NumberFormatException("An Internalloop must contain at least "+(InternalLoop.MIN)+" base.");
					}
					if(l>(InternalLoop.MAX) && !loop35_t1buf.equals(loop35_t1.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(InternalLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					//sequence pane
					if(tabbedPane.isEnabledAt(2)){
						if(loop35_seqm.getText() != null && !loop35_seqmbuf.equals(loop35_seqm.getText())){
							if((loop35_seqm.getText()).length()>l){
								int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 3'-5' strand", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								loop35_seqm.setText(null);
							}
							else if(!Iupac.isCorrectMotif(loop35_seqm.getText())){
								int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 3'-5' strand", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								loop35_seqm.setText(null);
							}
							else{
								internal.setLoop35Motif(loop35_seqm.getText());
							}
							loop35_seqmbuf = loop35_seqm.getText();
						}
					}
					//3' loop pane
					else if(tabbedPane.isEnabledAt(1)){
						if(loop35_seqm.getText() != null){
							if((loop35_seqm.getText()).length()>l){
								int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 3'-5' strand", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								loop35_seqm.setText(null);
								internal.setLoop35Motif(null);
								loop35_seqmbuf = loop35_seqm.getText();
							}
						}
						loop35_lowerend.setText(null);
						loop35_upperend.setText(null);
						loop35_t1buf = loop35_t1.getText();
						loop35_lowerendbuf = loop35_lowerend.getText();
						loop35_upperendbuf = loop35_upperend.getText();
						internal.set35Length(l);
						is.change35Size(internal.get35Length());
					}
				}
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			//size range for 3' loop
			else if(loop35_range.isSelected()){
				int low=0;
				int up=0;
				try{
					if(loop35_lowerend.getText() != null && !(loop35_lowerend.getText()).equals("")){
						low = Integer.parseInt(loop35_lowerend.getText());	
						if(low<(InternalLoop.MIN)){
							throw new NumberFormatException("An Internalloop must contain at least "+(InternalLoop.MIN)+" base.");
						}
						if(low>(InternalLoop.MAX) && !loop35_lowerendbuf.equals(loop35_lowerend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(InternalLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
					}
					if(loop35_upperend.getText() != null && !(loop35_upperend.getText()).equals("")){
						up = Integer.parseInt(loop35_upperend.getText());
						if(up<(InternalLoop.MIN)){
							throw new NumberFormatException("An Internalloop must contain at least "+(InternalLoop.MIN)+" base.");
						}
						if(up>(InternalLoop.MAX) && !loop35_upperendbuf.equals(loop35_upperend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(InternalLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
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
				//non given: default
				if(low == 0 && up == 0){
					loop35_deflen.doClick();
					return;
				}
				//Sequence pane
				if(tabbedPane.isEnabledAt(2)){
					if(loop35_seqm.getText() != null && !loop35_seqmbuf.equals(loop35_seqm.getText())){
						if(up != 0 && (loop35_seqm.getText()).length()>up){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							loop35_seqm.setText(null);
						}
						else if(!Iupac.isCorrectMotif(loop35_seqm.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 3'-5' strand", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							loop35_seqm.setText(null);
						}
						else{
							internal.setLoop35Motif(loop35_seqm.getText());
						}
						loop35_seqmbuf = loop35_seqm.getText();
					}
				}
				//3' loop pane
				else if(tabbedPane.isEnabledAt(1)){
					if(loop35_seqm.getText() != null){
						if(up != 0 && (loop35_seqm.getText()).length()>up){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif on 3'-5' strand", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							loop35_seqm.setText(null);
							internal.setLoop35Motif(null);
							loop35_seqmbuf = loop35_seqm.getText();
						}
					}
					loop35_t1.setText(null);
					loop35_t1buf = loop35_t1.getText();
					loop35_lowerendbuf = loop35_lowerend.getText();
					loop35_upperendbuf = loop35_upperend.getText();
					if(low != 0){
						internal.setMin35Length(low);
					}
					else{
						internal.setToDefault35Min();
					}
					if(up != 0){
						internal.setMax35Length(up);
					}
					else{
						internal.setToDefault35Max();
					}
					is.change35Size(internal.getMin35Length(),internal.getMax35Length());
				}
			}
			//Sequence pane
			if(tabbedPane.isEnabledAt(2)){
			    //5' bp
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
			            internal.setBasepair(bp5tf.getText(),0);
			        }
			        bp5tfbuf = bp5tf.getText();
			    }
			    //3'bp
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
			            internal.setBasepair(bp3tf.getText(),1);
			        }
			        bp3tfbuf = bp3tf.getText();
			    }
			}
			//Global Size Pane
			if(tabbedPane.isEnabledAt(3)){
			    if(gdeflen.isSelected()){
			        internal.removeGlobalLength();
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
			            internal.setMinGlobalLength(glow);
			        }
			        else{
			            internal.removeGlobalMin();
			        }
			        if(gup != 0){
			            internal.setMaxGlobalLength(gup);
			        }
			        else{
			            internal.removeGlobalMax();
			        }
			    }
			}
			apply.setEnabled(false);
			tabbedPane.setEnabledAt(0,true);
			tabbedPane.setEnabledAt(1,true);
			tabbedPane.setEnabledAt(2,true);
			tabbedPane.setEnabledAt(3,true);
			ds.repaint();
		}
		if(src == cancel){
			//no changes
			is.setSelected(false);
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
		if(src == loop53_t1){
			if(!loop53_t1buf.equals(loop53_t1.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(2,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(loop53_lowerendbuf.equals(loop53_lowerend.getText()) && loop53_upperendbuf.equals(loop53_upperend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(1,true);
				tabbedPane.setEnabledAt(2,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == loop35_t1){
			if(!loop35_t1buf.equals(loop35_t1.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(2,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(loop35_lowerendbuf.equals(loop35_lowerend.getText()) && loop35_upperendbuf.equals(loop35_upperend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(2,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == loop53_lowerend){
			if(!loop53_lowerendbuf.equals(loop53_lowerend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(2,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(loop53_t1buf.equals(loop53_t1.getText()) && loop53_upperendbuf.equals(loop53_upperend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(1,true);
				tabbedPane.setEnabledAt(2,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == loop35_lowerend){
			if(!loop35_lowerendbuf.equals(loop35_lowerend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(2,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(loop35_t1buf.equals(loop35_t1.getText()) && loop35_upperendbuf.equals(loop35_upperend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(2,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == loop53_upperend){
			if(!loop53_upperendbuf.equals(loop53_upperend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(2,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(loop53_lowerendbuf.equals(loop53_lowerend.getText()) && loop53_t1buf.equals(loop53_t1.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(1,true);
				tabbedPane.setEnabledAt(2,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == loop35_upperend){
			if(!loop35_upperendbuf.equals(loop35_upperend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(2,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(loop35_t1buf.equals(loop35_t1.getText()) && loop35_lowerendbuf.equals(loop35_lowerend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(2,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == loop53_seqm){
			if(!loop53_seqmbuf.equals(loop53_seqm.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(bp5tfbuf.equals(bp5tf.getText()) && bp3tfbuf.equals(bp3tf.getText()) && loop35_seqmbuf.equals(loop35_seqm.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(1,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == loop35_seqm){
			if(!loop35_seqmbuf.equals(loop35_seqm.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(bp5tfbuf.equals(bp5tf.getText()) && bp3tfbuf.equals(bp3tf.getText()) && loop53_seqmbuf.equals(loop53_seqm.getText())){
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
			else if(loop35_seqmbuf.equals(loop35_seqm.getText()) && bp3tfbuf.equals(bp3tf.getText()) && loop53_seqmbuf.equals(loop53_seqm.getText())){
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
			else  if(loop35_seqmbuf.equals(loop35_seqm.getText()) && bp5tfbuf.equals(bp5tf.getText()) && loop53_seqmbuf.equals(loop53_seqm.getText())){
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
	    is.editClosed();
	    super.dispose();
	}
} 
