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
 * This class defines the user interface for editing a Hairpin loop element
 *
 * @author Janina Reeder
 */
public class HairpinEdit extends JFrame implements ActionListener, CaretListener{
	
	private static final long serialVersionUID = 5303831535477370442L;
	private HairpinLoop hairpin;
	private HairpinShape hs;
	private JPanel pane;
	private JTabbedPane tabbedPane;
	private JRadioButton deflen, exact, range;
	private JButton ok, apply, cancel;
	private JLabel l1,l2,l3,sl1,bp;
	private JTextField t1, lowerend, upperend, seqm, bptf;
	private String t1buf, lowerendbuf, upperendbuf, seqmbuf, bptfbuf;
	private DrawingSurface ds;
	private int type;
	
	/**
	 * Constructor for the HairpinEdit GUI
	 *
	 * @param hs the parent HairpinShape
	 * @param ds the parent DrawingSurface
	 * @param type determines which tab is shown at first
	 */
	public HairpinEdit(HairpinShape hs, DrawingSurface ds, int type){
		super("Hairpin Options");
		
		this.hs = hs;
		this.hairpin = hs.getHairpin();
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
		deflen.setToolTipText("The hairpin loop is unrestricted, but always has a minimum number of 3 bases in the loop segment");
		
		JPanel exactpanel = new JPanel();
		exact = new JRadioButton("Exact size");
		exact.setToolTipText("Specify an exact number of bases contained in the loop region of the hairpin loop");
		JPanel exactnumpanel = new JPanel();
		l1 = new JLabel("Number of bases in loop segment: ");
		l1.setToolTipText("Specify an exact number of bases contained in the loop region of the hairpin loop");

		JPanel rangepanel = new JPanel();
		range = new JRadioButton("Size range");
		range.setToolTipText("Specify a minimum and/or maximum length");
		l2 = new JLabel("Minimum number of bases:  ");
		l2.setToolTipText("Specify a minimum number of bases for the loop region");
		l3 = new JLabel("Maximum number of bases: ");
		l3.setToolTipText("Specify a maximum number of bases for the loop region");
		JPanel rangevaluepanel1 = new JPanel();
		JPanel rangevaluepanel2 = new JPanel();

		if(hairpin.getIsDefaultLength()){
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
		else if(hairpin.getIsExactLength()){
			t1 = new JTextField(String.valueOf(hairpin.getLength()-2),3);
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
			if(!hairpin.getIsDefaultMin()){
				lowerend.setText(String.valueOf(hairpin.getMinLength()-2));
			}
			if(!hairpin.getIsDefaultMax()){
				upperend.setText(String.valueOf(hairpin.getMaxLength()-2));
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
		card1.add(Box.createRigidArea(new Dimension(0,10)));
		//END OF BOTTOM BUTTONS PLUS SIZE PANEL
		
		//SEQUENCE MOTIF PANEL
		JPanel card2 = new JPanel();
		
		JPanel sequencepanel = new JPanel();
		JPanel sequencelabelpanel = new JPanel();
		JPanel sequencemotifpanel = new JPanel();
		
		if(hairpin.getIsExactLength()){
			sl1 = new JLabel("Enter a loop sequence motif:");
			seqm = new JTextField(hairpin.getLoopMotif(),15);
		}
		else{
			sl1 = new JLabel("Enter a loop sequence motif:");
			seqm = new JTextField(hairpin.getLoopMotif(),15);
		}
		sl1.setToolTipText("Add a sequence motif: can contain all Iupac bases");
		
		sl1.setAlignmentX(Component.LEFT_ALIGNMENT);
		seqm.setAlignmentX(Component.LEFT_ALIGNMENT);
		seqm.setToolTipText("<html>Matcher will check for motif anywhere in the loop.<br>Use \"N\" to enforce a specific location!</html>");
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
		
		
		JPanel bppanel = new JPanel();
		bppanel.setLayout(new BoxLayout(bppanel, BoxLayout.LINE_AXIS));
		
		bp = new JLabel("Exit Basepair:");
		bp.setToolTipText("Format: 'ag'; first on 5', second on 3' strand");
		bp.setAlignmentX(Component.LEFT_ALIGNMENT);
		bptf = new JTextField(hairpin.getBasepairString(),2);
		bptf.setAlignmentX(Component.LEFT_ALIGNMENT);
		bptf.setMaximumSize(bptf.getPreferredSize());
		bptf.addCaretListener(this);
		bptfbuf = bptf.getText();
		
		bppanel.add(Box.createRigidArea(new Dimension(20,0)));
		bppanel.add(bp);
		bppanel.add(Box.createRigidArea(new Dimension(10,0)));
		bppanel.add(bptf);
		
		bppanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		sequencepanel.add(Box.createRigidArea(new Dimension(0,20)));
		sequencepanel.add(bppanel);
		
		sequencepanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		card2.setLayout(new BoxLayout(card2,BoxLayout.PAGE_AXIS));
		card2.add(sequencepanel);
		//END OF SEQUENCE MOTIF PANEL
		
		
		tabbedPane.addTab("Size", card1);
		tabbedPane.addTab("Sequence", card2);
		
		switch(type){
		case 2: tabbedPane.setSelectedIndex(1); break;	//Sequence
		default: tabbedPane.setSelectedIndex(0);		//Size
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
		}
		if(src == range){
			l1.setEnabled(false);
			t1.setText(null);
			t1buf = t1.getText();
			t1.setEnabled(false);
			l2.setEnabled(true);
			if(!hairpin.getIsDefaultMin() && hairpin.getMinLength()>0){
				lowerend.setText(String.valueOf(hairpin.getMinLength()-2));
				lowerendbuf = lowerend.getText();
			}
			lowerend.setEnabled(true);
			l3.setEnabled(true);
			if(!hairpin.getIsDefaultMax() && hairpin.getMaxLength()>0){
				upperend.setText(String.valueOf(hairpin.getMaxLength()-2));
				upperendbuf = upperend.getText();
			}
			upperend.setEnabled(true);
		}
		if(src == exact){
			l1.setEnabled(true);
			t1.setText(String.valueOf(hairpin.getLength()-2));
			t1buf = t1.getText();
			t1.setEnabled(true);
			l2.setEnabled(false);
			lowerend.setEnabled(false);
			l3.setEnabled(false);
			upperend.setEnabled(false);
		}
		//save changes and close window
		if(src == ok){ //save changes
		    //no size restriction
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
						hairpin.setLoopMotif(seqm.getText());
					}
				}
				//check and store bp
				if(bptf.getText() != null && !bptfbuf.equals(bptf.getText())){
					if(!Iupac.isCorrectBPSeq(bptf.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified is not in the correct format 'GC;NN;-;AU'\n or contains non-iupac characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else{
						hairpin.setBasepair(bptf.getText());
					}
				}
				hairpin.setToDefaultLength();
				hs.changeSize(hairpin.getLength());
			}
			//exact size
			if(exact.isSelected()){
				try{
				    //no value given: return to default length
					if(t1.getText() == null || (t1.getText()).equals("")){
						deflen.doClick();
						return;
					}
					//parse and check size value
					int l = Integer.parseInt(t1.getText());
					if(l<(HairpinLoop.MIN-2)){
						throw new NumberFormatException("A Hairpin loop must contain at least "+(HairpinLoop.MIN-2)+" bases.");
					}
					if(l>(HairpinLoop.MAX-2) && !t1buf.equals(t1.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(HairpinLoop.MAX-2)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					//check and store motif
					if(seqm.getText() != null){
						if((seqm.getText()).length()>l){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							else{
								hairpin.setLoopMotif(null);
							}
						}
						else if(!seqmbuf.equals(seqm.getText()) && !Iupac.isCorrectMotif(seqm.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
						else{
							hairpin.setLoopMotif(seqm.getText());
						}
					}
					//check and store bp
					if(bptf.getText() != null && !bptfbuf.equals(bptf.getText())){
						if(!((bptf.getText()).length()==2 || (bptf.getText()).length()==0)){
							int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							else{
								hairpin.setBasepair(null);
							}
						}
						else if(!Iupac.isCorrectMotif(bptf.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
						else{
							hairpin.setBasepair(bptf.getText());
						}
					}
					hairpin.setLength(l+2);
					hs.changeSize(hairpin.getLength());
				}
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
					return;
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
						if(low<(HairpinLoop.MIN-2)){
							throw new NumberFormatException("A Hairpin loop must contain at least "+(HairpinLoop.MIN-2)+" bases.");
						}
						if(low>(HairpinLoop.MAX-2) && !lowerendbuf.equals(lowerend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(HairpinLoop.MAX-2)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
					}
					//parse and check maximum
					if(upperend.getText() != null && !(upperend.getText()).equals("")){
						up = Integer.parseInt(upperend.getText());
						if(up<(HairpinLoop.MIN-2)){
							throw new NumberFormatException("A Hairpin loop must contain at least "+(HairpinLoop.MIN-2)+" bases.");
						}
						if(up>(HairpinLoop.MAX-2) && !upperendbuf.equals(upperend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(HairpinLoop.MAX-2)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
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
				//no values given: default length
				if(low == 0 && up == 0){
					deflen.doClick();
					return;
				}
				//check and store motif
				if(seqm.getText() != null){
					if(up != 0 && (seqm.getText()).length()>up){
						int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
						else{
							hairpin.setLoopMotif(null);
						}
					}
					else if(!seqmbuf.equals(seqm.getText()) && !Iupac.isCorrectMotif(seqm.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else{
						hairpin.setLoopMotif(seqm.getText());
					}
				}
				//check and store bp
				if(bptf.getText() != null && !bptfbuf.equals(bptf.getText())){
					if(!((bptf.getText()).length()==2 || (bptf.getText()).length()==0)){
						int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else if(!Iupac.isCorrectMotif(bptf.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else{
						hairpin.setBasepair(bptf.getText());
					}
				}
				if(low != 0){
					hairpin.setMinLength(2+low);
				}
				else{
					hairpin.setToDefaultMin();
				}
				if(up != 0){
					hairpin.setMaxLength(2+up);
				}
				else{
					hairpin.setToDefaultMax();
				}
				hs.changeSize(hairpin.getMinLength(),hairpin.getMaxLength());
			}
			hs.setSelected(false);
			ds.unselectShape();
			ds.repaint();
			dispose();
		}
		//store changes, keep window open
		if(src == apply){
			//apply changes (same as above, except windows doesn't close 
			//and values are adjusted
		    
		    //no size restriction
			if(deflen.isSelected()){
			    //Sequence pane
				if(tabbedPane.isEnabledAt(1)){
					if(seqm.getText() != null && !seqmbuf.equals(seqm.getText())){
						if(!Iupac.isCorrectMotif(seqm.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							seqm.setText(null);
						}
						else{
							hairpin.setLoopMotif(seqm.getText());
						}
						seqmbuf = seqm.getText();
					}
					if(bptf.getText() != null && !bptfbuf.equals(bptf.getText())){
						if(!Iupac.isCorrectBPSeq(bptf.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified is not in the correct format 'GC;NN;-;AU'\n or contains non-iupac characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							bptf.setText(null);
						}
						else{
							hairpin.setBasepair(bptf.getText());
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
					hairpin.setToDefaultLength();
					hs.changeSize(hairpin.getLength());
				}
			}
			//exact value
			else if(exact.isSelected()){
				try{
					if(t1.getText() == null || (t1.getText()).equals("")){
						deflen.doClick();
						return;
					}
					int l = Integer.parseInt(t1.getText());
					if(l<(HairpinLoop.MIN-2)){
						throw new NumberFormatException("A Hairpin loop must contain at least "+(HairpinLoop.MIN-2)+" bases.");
					}
					if(l>(HairpinLoop.MAX-2) && !t1buf.equals(t1.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(HairpinLoop.MAX-2)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
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
							else{
								hairpin.setLoopMotif(seqm.getText());
							}
							seqmbuf = seqm.getText();
						}
						if(bptf.getText() != null && !bptfbuf.equals(bptf.getText())){
							if(!((bptf.getText()).length()==2 || (bptf.getText()).length()==0)){
								int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								bptf.setText(null);
							}
							else if(!Iupac.isCorrectMotif(bptf.getText())){
								int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								bptf.setText(null);
							}
							else{
								hairpin.setBasepair(bptf.getText());
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
								seqm.setText(null);
								hairpin.setLoopMotif(seqm.getText());
								seqmbuf = seqm.getText();
							}
						}
						lowerend.setText(null);
						upperend.setText(null);
						t1buf = t1.getText();
						lowerendbuf = lowerend.getText();
						upperendbuf = upperend.getText();
						hairpin.setLength(2+l);
						hs.changeSize(hairpin.getLength());
					}
				}
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
				}
			}
			//range values
			else{
				int low=0;
				int up=0;
				try{
					if(lowerend.getText() != null && !(lowerend.getText()).equals("")){
						low = Integer.parseInt(lowerend.getText());	
						if(low<(HairpinLoop.MIN-2)){
							throw new NumberFormatException("A Hairpin loop must contain at least "+(HairpinLoop.MIN-2)+" bases.");
						}
						if(low>(HairpinLoop.MAX-2) && !lowerendbuf.equals(lowerend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(HairpinLoop.MAX-2)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
					}
					if(upperend.getText() != null && !(upperend.getText()).equals("")){
						up = Integer.parseInt(upperend.getText());
						if(up<(HairpinLoop.MIN-2)){
							throw new NumberFormatException("A Hairpin loop must contain at least "+(HairpinLoop.MIN-2)+" bases.");
						}
						if(up>(HairpinLoop.MAX-2) && !upperendbuf.equals(upperend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(HairpinLoop.MAX-2)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
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
					deflen.doClick();
					return;
				}
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
						else{
							hairpin.setLoopMotif(seqm.getText());
						}
						seqmbuf = seqm.getText();
					}
					if(bptf.getText() != null && !bptfbuf.equals(bptf.getText())){
						if(!((bptf.getText()).length()==2 || (bptf.getText()).length()==0)){
							int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							bptf.setText(null);
						}
						else if(!Iupac.isCorrectMotif(bptf.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							bptf.setText(null);
						}
						else{
							hairpin.setBasepair(bptf.getText());
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
							seqm.setText(null);
							hairpin.setLoopMotif(null);
							seqmbuf = seqm.getText();
						}
					}
					t1.setText(null);
					t1buf = t1.getText();
					lowerendbuf = lowerend.getText();
					upperendbuf = upperend.getText();
					if(low != 0){
						hairpin.setMinLength(2+low);
					}
					else{
						hairpin.setToDefaultMin();
					}
					if(up != 0){
						hairpin.setMaxLength(2+up);
					}
					else{
						hairpin.setToDefaultMax();
					}
					hs.changeSize(hairpin.getMinLength(),hairpin.getMaxLength());
				}
			}
			apply.setEnabled(false);
			tabbedPane.setEnabledAt(0,true);
			tabbedPane.setEnabledAt(1,true);
			ds.repaint();
		}
		//quit without changes
		if(src == cancel){
			//no changes
			hs.setSelected(false);
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
			}
			else if(lowerendbuf.equals(lowerend.getText()) && upperendbuf.equals(upperend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(1,true);
			}
		}
		if(src == lowerend){
			if(!lowerendbuf.equals(lowerend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(1,false);
			}
			else if(t1buf.equals(t1.getText()) && upperendbuf.equals(upperend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(1,true);
			}
		}
		if(src == upperend){
			if(!upperendbuf.equals(upperend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(1,false);
			}
			else if(lowerendbuf.equals(lowerend.getText()) && t1buf.equals(t1.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(1,true);
			}
		}
		if(src == seqm){
			if(!seqmbuf.equals(seqm.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
			}
			else if(bptfbuf.equals(bptf.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
			}
		}
		if(src == bptf){
			if(!bptfbuf.equals(bptf.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
			}
			else if(seqmbuf.equals(seqm.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
			}
		}
	}
	
	public void dispose(){
	    hs.editClosed();
	    super.dispose();
	}
	
} 
