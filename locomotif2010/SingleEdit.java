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
 * This class defines the user interface for editing a single element
 *
 * @author Janina Reeder
 */
public class SingleEdit extends JFrame implements ActionListener, CaretListener{
	
	private static final long serialVersionUID = -4660129922952244320L;
	private SingleStrand single;
	private SingleShape ss;
	private JPanel pane;
	private JTabbedPane tabbedPane;
	private JRadioButton deflen, exact, range;
	private JRadioButton basepairs, straight;
	private JButton ok, apply, cancel;
	private JLabel l1,l2,l3,sl1;
	private JTextField t1, lowerend, upperend, seqm;
	private String t1buf, lowerendbuf, upperendbuf, seqmbuf;
	private DrawingSurface ds;
	private int type;
	
	/**
	 * Constructor for the SingleEdit GUI
	 *
	 * @param ss the parent SingleShape
	 * @param ds the parent DrawingSurface
	 * @param type determines which tab is shown at first
	 */
	public SingleEdit(SingleShape ss, DrawingSurface ds, int type){
		super("Single Options");
		
		this.ss = ss;
		this.single = ss.getSingleStrand();
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
		deflen.setToolTipText("Unrestricted single strand contains at least 1 base");
	
		JPanel exactpanel = new JPanel();
		exact = new JRadioButton("Exact size");
		exact.setToolTipText("Specify an exact number of bases contained in the single strand");
		JPanel exactnumpanel = new JPanel();
		l1 = new JLabel("Number of bases: ");
		l1.setToolTipText("Give the exact number of bases");

		JPanel rangepanel = new JPanel();
		range = new JRadioButton("Size range");
		range.setToolTipText("Specify a minimum and/or maximum number of bases for the single strand");
		l2 = new JLabel("Minimum number of bases:  ");
		l2.setToolTipText("Give the minimum number of bases");
		l3 = new JLabel("Maximum number of bases: ");
		l3.setToolTipText("Give the maximum number of bases");
		JPanel rangevaluepanel1 = new JPanel();
		JPanel rangevaluepanel2 = new JPanel();
		
		/*
		if(ss.getFivePrimeEnd() == null || ss.getThreePrimeEnd() == null){ //elongation element
			exact.setEnabled(false);
			l1.setEnabled(false);
			l3.setEnabled(false);
			t1 = new JTextField(3);
			t1.setEnabled(false);
			upperend = new JTextField(3);
			upperend.setEnabled(false);
			if(single.getIsDefaultLength()){
				lowerend = new JTextField(3);
				l2.setEnabled(false);
				lowerend.setEnabled(false);
				deflen.setSelected(true);
				exact.setSelected(false);
				range.setSelected(false);
			}
			else{
				lowerend = new JTextField(3);
				l2.setEnabled(true);
				if(!single.getIsDefaultMin()){
					lowerend.setText(String.valueOf(single.getMinLength()));
				}
				deflen.setSelected(false);
				exact.setSelected(false);
				range.setSelected(true);
			}
		}*/
		//else 
		if(single.getIsDefaultLength()){
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
		else if(single.getIsExactLength()){
			t1 = new JTextField(String.valueOf(single.getLength()),3);
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
			if(!single.getIsDefaultMin()){
				lowerend.setText(String.valueOf(single.getMinLength()));
			}
			if(!single.getIsDefaultMax()){
				upperend.setText(String.valueOf(single.getMaxLength()));
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
		//END OF BOTTOM BUTTONS
		
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
		
		//SEQUENCE PANEL
		JPanel card2 = new JPanel();
		
		JPanel sequencepanel = new JPanel();
		JPanel sequencelabelpanel = new JPanel();
		JPanel sequencemotifpanel = new JPanel();
		
		if(single.getIsExactLength() && !single.getIsDefaultLength()){
			sl1 = new JLabel("Enter a sequence motif of max. "+single.getLength()+" bases:");
			seqm = new JTextField(single.getStrandMotif(),15);
		}
		else if(!single.getIsDefaultMax() && single.getMaxLength() > 0){
			sl1 = new JLabel("Enter a sequence motif of max. "+single.getMaxLength()+" bases:");
			seqm = new JTextField(single.getStrandMotif(),15);
		}
		else{
			sl1 = new JLabel("Enter a sequence motif:");
			seqm = new JTextField(single.getStrandMotif(),15);
		}
		sl1.setToolTipText("<html>Specify a sequence motif contained somewhere within the single strand<br>Use \"N\" to specify the exact location of the motif</html>");
		sl1.setAlignmentX(Component.LEFT_ALIGNMENT);
		seqm.setAlignmentX(Component.LEFT_ALIGNMENT);
		seqm.setMaximumSize(seqm.getPreferredSize());
		seqm.setToolTipText("<html>Matcher will check for motif anywhere in the strand.<br>Use \"N\" to enforce a specific location!</html>");
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
		
		
		sequencepanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		card2.setLayout(new BoxLayout(card2,BoxLayout.PAGE_AXIS));
		card2.add(sequencepanel);
		card2.add(Box.createRigidArea(new Dimension(0,10)));
		//END OF SEQUENCE PANEL
		
		//FOLDING PANEL
		JPanel card3 = new JPanel();
		
		JPanel foldingpanel = new JPanel();
		
		basepairs = new JRadioButton("Allow internal basepairing");
		basepairs.setToolTipText("The single strand connector can fold into other motif parts");
		straight = new JRadioButton("No basepairing allowed");
		straight.setToolTipText("Single strand must be straight. No internal structure part allowed");
		
		if(single.getIsStraight()){
			straight.setSelected(true);
			basepairs.setSelected(false);
		}
		else{
			straight.setSelected(false);
			basepairs.setSelected(true);
		}
		ButtonGroup bg2 = new ButtonGroup();
		bg2.add(straight);
		bg2.add(basepairs);

		foldingpanel.setLayout(new BoxLayout(foldingpanel, BoxLayout.PAGE_AXIS));
		straight.setAlignmentX(Component.LEFT_ALIGNMENT);
		straight.addActionListener(this);
		foldingpanel.add(straight);
		basepairs.setAlignmentX(Component.LEFT_ALIGNMENT);
		basepairs.addActionListener(this);
		foldingpanel.add(basepairs);
		
		foldingpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		card3.setLayout(new BoxLayout(card3,BoxLayout.PAGE_AXIS));
		card3.add(foldingpanel);
		card3.add(Box.createRigidArea(new Dimension(0,10)));
		
		tabbedPane.addTab("Size", card1);
		tabbedPane.addTab("Sequence", card2);
		tabbedPane.addTab("Folding",card3);
		
		switch(type){
		case 2: tabbedPane.setSelectedIndex(1); break;		//Sequence
		case 7: tabbedPane.setSelectedIndex(2); break;		//Folding
		default: tabbedPane.setSelectedIndex(0);			//Size
		}
		
		if(single.getIsConnector()){
			tabbedPane.setEnabledAt(2,true);
		}
		else{
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
		}
		if(src == range){
		    if(!single.getIsConnector() && ds.getSearchType()){
		        JOptionPane.showMessageDialog(null, "Please bear in mind that a range\nfor single strand size can cause ambiguity issues (duplicate results).", "Possible ambiguity", JOptionPane.OK_OPTION);
		    }
		    l1.setEnabled(false);
			t1.setText(null);
			t1buf = t1.getText();
			t1.setEnabled(false);
			l2.setEnabled(true);
			if(!single.getIsDefaultMin() && single.getMinLength()>0){
				lowerend.setText(String.valueOf(single.getMinLength()));
				lowerendbuf = lowerend.getText();
			}
			lowerend.setEnabled(true);
			l3.setEnabled(true);
			if(!single.getIsDefaultMax() && single.getMaxLength()>0){
			    upperend.setText(String.valueOf(single.getMaxLength()));
			    upperendbuf = upperend.getText();
			}
			upperend.setEnabled(true);
		}
		if(src == exact){
			l1.setEnabled(true);
			t1.setText(String.valueOf(single.getLength()));
			t1buf = t1.getText();
			t1.setEnabled(true);
			l2.setEnabled(false);
			lowerend.setEnabled(false);
			l3.setEnabled(false);
			upperend.setEnabled(false);
		}
		if(src == basepairs){
			if(single.getIsStraight()){
				apply.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(1,false);
			}
			else{
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(1,true);
			}
		}
		if(src == straight){
			if(!single.getIsStraight()){
				apply.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(1,false);
			}
			else{
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(1,true);
			}
		}
		//save changes and close window
		if(src == ok){
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
						single.setStrandMotif(seqm.getText());
					}
				}
				single.setToDefaultLength();
				ss.changeSize(single.getLength());
			}
			//exact size 
			if(exact.isSelected()){
				try{
				    //none given: default
					if(t1.getText() == null || (t1.getText()).equals("")){
						deflen.doClick();
						return;
					}
					//parse and check size
					int l = Integer.parseInt(t1.getText());
					if(l<SingleStrand.MIN){
						throw new NumberFormatException("Negative single stranded regions are not possible.");
					}
					else{
					    //check motif for size compatibility and store
						if(seqm.getText() != null){
							if((seqm.getText()).length()>l){
								int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								else{
									single.setStrandMotif(null);
								}
							}
							else if(!seqmbuf.equals(seqm.getText()) && !Iupac.isCorrectMotif(seqm.getText())){
								int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
								else{
									single.setStrandMotif(null);
								}
							}
							else{
								single.setStrandMotif(seqm.getText());
							}
						}
						single.setLength(l);
						ss.changeSize(single.getLength());
					}
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
						if(low<SingleStrand.MIN){
							throw new NumberFormatException("Negative single stranded regions are not possible.");
						}
					}
					if(upperend.getText() != null && !(upperend.getText()).equals("")){
						up = Integer.parseInt(upperend.getText());
						if(up<SingleStrand.MIN){
							throw new NumberFormatException("Negative single stranded regions are not possible.");
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
				//check motif for compatibility and store
				if(seqm.getText() != null){
					if(up != 0 && (seqm.getText()).length()>up){
						int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
						else{
							single.setStrandMotif(null);
						}
					}
					else if(!seqmbuf.equals(seqm.getText()) && !Iupac.isCorrectMotif(seqm.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
						else{
							single.setStrandMotif(null);
						}
					}
					else{
						single.setStrandMotif(seqm.getText());
					}
				}
				//store size values
				if(low != 0){
					single.setMinLength(low);
				}
				else{
					single.setToDefaultMin();
				}
				if(up != 0){
					single.setMaxLength(up);
				}
				else{
					single.setToDefaultMax();
				}
				ss.changeSize(single.getMinLength(),single.getMaxLength());
			}
			//store foldability if ss connects 2 parts
			if(single.getIsConnector()){
				if(straight.isSelected()){
					single.setIsStraight(true);
				}
				else{
					single.setIsStraight(false);
				}
			}
			ss.setSelected(false);
			ds.unselectShape();
			ds.repaint();
			dispose();
		}
		//store changes, keep window open
		if(src == apply){
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
							single.setStrandMotif(seqm.getText());
						}
						seqmbuf = seqm.getText();
					}
				}
				//size pane
				else if(tabbedPane.isEnabledAt(0)){
					lowerend.setText(null);
					upperend.setText(null);
					t1.setText(null);
					t1buf = t1.getText();
					lowerendbuf = lowerend.getText();
					upperendbuf = upperend.getText();
					single.setToDefaultLength();
					ss.changeSize(single.getLength());
				}
			}
			//exact size
			if(exact.isSelected()){
				try{
				    //none given: default
					if(t1.getText() == null || (t1.getText()).equals("")){
						deflen.doClick();
						return;
					}
					int l = Integer.parseInt(t1.getText());
					if(l<SingleStrand.MIN){
						throw new NumberFormatException("Negative single stranded regions are not possible.");
					}
					else{
					    //Sequence pane
						if(tabbedPane.isEnabledAt(1)){
							if(seqm.getText() != null && !seqmbuf.equals(seqm.getText())){
								if((seqm.getText()).length()>l){
									int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
									if(retval == JOptionPane.CANCEL_OPTION){
										return;
									}
									else{
										single.setStrandMotif(null);
										seqm.setText(null);
									}
								}
								else if(!Iupac.isCorrectMotif(seqm.getText())){
									int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
									if(retval == JOptionPane.CANCEL_OPTION){
										return;
									}
									else{
										single.setStrandMotif(null);
										seqm.setText(null);
									}	
								}
								else{
									single.setStrandMotif(seqm.getText());
								}
								seqmbuf = seqm.getText();
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
										single.setStrandMotif(null);
										seqm.setText(null);
										seqmbuf = seqm.getText();
									}
								}
							}
							lowerend.setText(null);
							upperend.setText(null);
							t1buf = t1.getText();
							lowerendbuf = lowerend.getText();
							upperendbuf = upperend.getText();
							single.setLength(l);
							ss.changeSize(single.getLength());
						}
					}
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
				try{
					if(lowerend.getText() != null && !(lowerend.getText()).equals("")){
						low = Integer.parseInt(lowerend.getText());	
						if(low<SingleStrand.MIN){
							throw new NumberFormatException("Negative single stranded regions are not possible.");
						}
					}
					if(upperend.getText() != null && !(upperend.getText()).equals("")){
						up = Integer.parseInt(upperend.getText());
						if(up<SingleStrand.MIN){
							throw new NumberFormatException("Negative single stranded regions are not possible.");
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
				//Sequence pane
				if(tabbedPane.isEnabledAt(1)){
					if(seqm.getText() != null && !seqmbuf.equals(seqm.getText())){
						if(up != 0 && (seqm.getText()).length()>up){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							else{
								single.setStrandMotif(null);
								seqm.setText(null);
							}
						}
						else if(!Iupac.isCorrectMotif(seqm.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							else{
								single.setStrandMotif(null);
								seqm.setText(null);
							}
						}
						else{
							single.setStrandMotif(seqm.getText());
						}
						seqmbuf = seqm.getText();
					}
				}
				//Size pane
				else if(tabbedPane.isEnabledAt(0)){
					if(seqm.getText() != null && up != 0){
						if((seqm.getText()).length()>up){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified is longer than allowed by length restrictions.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Motif", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							else{
								single.setStrandMotif(null);
								seqm.setText(null);
								seqmbuf = seqm.getText();
							}
						}
					}
					t1.setText(null);
					t1buf = t1.getText();
					lowerendbuf = lowerend.getText();
					upperendbuf = upperend.getText();
					if(low != 0){
						single.setMinLength(low);
					}
					else{
						single.setToDefaultMin();
					}
					if(up != 0){
						single.setMaxLength(up);
					}
					else{
						single.setToDefaultMax();
					}
					ss.changeSize(single.getMinLength(),single.getMaxLength());
				}
			}
			//Foldability pane
			if(tabbedPane.isEnabledAt(2)){
			    if(single.getIsConnector()){
			        if(straight.isSelected()){
			            single.setIsStraight(true);
			        }
			        else{
			            single.setIsStraight(false);
			        }
			    }
			}
			apply.setEnabled(false);
			tabbedPane.setEnabledAt(0,true);
			tabbedPane.setEnabledAt(1,true);
			if(single.getIsConnector()){
				tabbedPane.setEnabledAt(2,true);
			}
			ds.repaint();
		}
		//exit without changes
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
			}
			else if(lowerendbuf.equals(lowerend.getText()) && upperendbuf.equals(upperend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(1,true);
				if(single.getIsConnector()){
					tabbedPane.setEnabledAt(2,true);
				}
			}
		}
		if(src == lowerend){
			if(!lowerendbuf.equals(lowerend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(2,false);
			}
			else if(t1buf.equals(t1.getText()) && upperendbuf.equals(upperend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(1,true);
				if(single.getIsConnector()){
					tabbedPane.setEnabledAt(2,true);
				}
			}
		}
		if(src == upperend){
			if(!upperendbuf.equals(upperend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(2,false);
			}
			else if(lowerendbuf.equals(lowerend.getText()) && t1buf.equals(t1.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(1,true);
				if(single.getIsConnector()){
					tabbedPane.setEnabledAt(2,true);
				}
			}
		}
		if(src == seqm){
			if(!seqmbuf.equals(seqm.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(2,false);
			}
			else{
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				if(single.getIsConnector()){
					tabbedPane.setEnabledAt(2,true);
				}
			}
		}
	}
	
	public void dispose(){
	    ss.editClosed();
	    super.dispose();
	}
} 



