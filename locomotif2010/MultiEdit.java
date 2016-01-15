package rnaeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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
 * This class defines the user interface for editing a multiloop element
 *
 * @author Janina Reeder
 */
public class MultiEdit extends JFrame implements ActionListener, CaretListener{
	
	private static final long serialVersionUID = -5785219565064945476L;
	private MultiLoop multi;
	private MultiShape ms;
	private JPanel pane;
	private JTabbedPane tabbedPane;
	private JRadioButton deflen, exact, range, none;
	private JRadioButton gdeflen, grange;
	private JButton ok, apply, cancel;
	private JLabel l1,l2,l3,seql1,seql2;
	private JLabel gl2,gl3;
	private JTextField t1, lowerend, upperend, seqtf1, seqtf2;
	private JTextField glowerend, gupperend;
	private String t1buf, lowerendbuf, upperendbuf, seqtf1buf, seqtf2buf;
	private String glowerendbuf, gupperendbuf;
	private SelectorPanel selectorpanel;
	private SeqSelectPanel seqselectpanel;
	private SizeSelectPanel sizeselectpanel;
	private int numofexits;
	private int[] exitstructbuf;
	private DrawingSurface ds;
	private int type;
	private int rotation;
	
	/**
	 * Constructor for the MultiEdit GUI
	 *
	 * @param ms the parent MultiShape
	 * @param ds the parent DrawingSurface
	 * @param type determines which tab is shown at first
	 */
	public MultiEdit(MultiShape ms, DrawingSurface ds, int type){
		super("MultiLoop Options");
		
		this.ms = ms;
		this.multi = ms.getMultiLoop();
		this.ds = ds;
		this.numofexits = ms.getNumberOfExits();
		this.type = type;
		this.rotation = ms.getRotation();
		this.exitstructbuf = new int[8];
		
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
		
		//EXIT LOCATION AND NUMBER PANEL
		JPanel card1 = new JPanel();
		
		JPanel exitpanel = new JPanel();
		JLabel exitlabel = new JLabel("Klick on the exits to select/unselect them");
		exitlabel.setToolTipText("<html>A red dot indicates an exit that can be removed<br>A grey dot indicates a present exit that cannot be removed<br>No dot indicates an exit that can be added</html>");
		
		selectorpanel = new SelectorPanel();
		selectorpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		exitpanel.setLayout(new BoxLayout(exitpanel, BoxLayout.PAGE_AXIS));
		exitpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		exitpanel.add(exitlabel);
		exitpanel.add(Box.createRigidArea(new Dimension(20,0)));
		exitpanel.add(selectorpanel);
		//END OF EXIT PANEL
		
		//SIZE PANEL
		JPanel card2 = new JPanel();
		
		JPanel mainsizepanel = new JPanel();
		mainsizepanel.setLayout(new BoxLayout(mainsizepanel, BoxLayout.PAGE_AXIS));
		mainsizepanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JLabel sizelabel = new JLabel("Klick on the loops to choose");
		sizelabel.setToolTipText("Once a loop is selected, you alter its size restrictions");
		mainsizepanel.add(sizelabel);
		mainsizepanel.add(Box.createRigidArea(new Dimension(20,0)));
		
		JPanel sizepanel = new JPanel();
		
		JPanel defaultpanel = new JPanel();
		deflen = new JRadioButton("No size restriction");
		deflen.setToolTipText("No size restrictions for the chosen loop");
		
		JPanel exactpanel = new JPanel();
		exact = new JRadioButton("Exact size");
		exact.setToolTipText("Specify an exact number of bases for the chosen loop");
		JPanel exactnumpanel = new JPanel();
		l1 = new JLabel("Number of bases: ");
		l1.setToolTipText("Specify an exact number of bases for the chosen loop");
		
		JPanel rangepanel = new JPanel();
		range = new JRadioButton("Size range");
		range.setToolTipText("Specify a minimum and/or maximum number of bases for the chosen loop");
		l2 = new JLabel("Minimum number of bases:  ");
		l2.setToolTipText("Specify minimum for chosen loop");
		l3 = new JLabel("Maximum number of bases: ");
		l3.setToolTipText("Specify maximum for chosen loop");
		JPanel rangevaluepanel1 = new JPanel();
		JPanel rangevaluepanel2 = new JPanel();
		
		none = new JRadioButton("Not visible");

		ButtonGroup bg = new ButtonGroup();
		
		bg.add(deflen);
		bg.add(exact);
		bg.add(range);
		bg.add(none);
		
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
		
		t1 = new JTextField(3);
		t1.setMaximumSize(t1.getPreferredSize());
		t1.addCaretListener(this);
		l1.setEnabled(false);
		t1.setEnabled(false);
		t1.setText(null);
		t1buf = t1.getText();
		exactnumpanel.add(t1);
		exactnumpanel.add(Box.createHorizontalGlue());
		exactnumpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		exactpanel.add(exactnumpanel);

		range.setEnabled(false);
		deflen.setEnabled(false);
		exact.setEnabled(false);
		none.setEnabled(false);
		
		rangepanel.setLayout(new BoxLayout(rangepanel, BoxLayout.PAGE_AXIS));
		range.setAlignmentX(Component.LEFT_ALIGNMENT);
		range.addActionListener(this);
		rangepanel.add(range);
		
		lowerend = new JTextField(3);
		lowerend.setMaximumSize(lowerend.getPreferredSize());
		lowerend.addCaretListener(this);
		l2.setEnabled(false);
		lowerend.setEnabled(false);
		lowerend.setText(null);
		lowerendbuf = lowerend.getText();
		upperend = new JTextField(3);
		upperend.setMaximumSize(upperend.getPreferredSize());
		upperend.addCaretListener(this);
		upperendbuf = upperend.getText();
		l3.setEnabled(false);
		upperend.setEnabled(false);
		upperend.setText(null);
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

		
		defaultpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		exactpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangepanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JPanel sizeinfopanel = new JPanel();
		sizeinfopanel.setLayout(new BoxLayout(sizeinfopanel, BoxLayout.PAGE_AXIS));
		sizeinfopanel.setLayout(new BoxLayout(sizeinfopanel, BoxLayout.PAGE_AXIS));
		sizeinfopanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		sizeinfopanel.add(defaultpanel);
		sizeinfopanel.add(Box.createRigidArea(new Dimension(20,0)));
		sizeinfopanel.add(exactpanel);
		sizeinfopanel.add(Box.createRigidArea(new Dimension(20,0)));
		sizeinfopanel.add(rangepanel);
		
		sizeselectpanel = new SizeSelectPanel();
		sizeselectpanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		sizepanel.setLayout(new BoxLayout(sizepanel, BoxLayout.LINE_AXIS));
		sizepanel.add(sizeselectpanel);
		sizepanel.add(sizeinfopanel);
		
		mainsizepanel.add(sizepanel);
		
		card2.setLayout(new BoxLayout(card2,BoxLayout.PAGE_AXIS));
		card2.add(mainsizepanel);
		card2.add(Box.createRigidArea(new Dimension(0,10)));
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
		
		exitpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		card1.add(exitpanel);
		card1.add(Box.createRigidArea(new Dimension(0,10)));

		
		//SEQUENCE MOTIF PANEL
		JPanel card3 = new JPanel();
		
		JPanel seqlabelpanel = new JPanel();
		//JLabel sequencelabel = new JLabel("Klick on the exits or loops to choose");
		JLabel sequencelabel = new JLabel("Klick on the loops or exits to choose");
		sequencelabel.setToolTipText("Select an exit or loop to add/remove a sequence motif for it");
		seql1 = new JLabel("Please specify a basepair for the chosen exit:");
		seql1.setToolTipText("Format: 'ag'; first on 5' strand, second on 3' strand");
		seql1.setEnabled(false);
		seql2 = new JLabel("Please specify a sequence motif for the chosen loop:");
		seql2.setToolTipText("<html>Specify a motif contained somewhere within the loop<br>Use \"N\" to enforce a specific position</html>");
		seql2.setEnabled(false);
		seqtf1 = new JTextField(5);
		seqtf1.setMaximumSize(seqtf1.getPreferredSize());
		seqtf1.setEnabled(false);
		seqtf1.addCaretListener(this);
		seqtf1buf = seqtf1.getText();
		seqtf2 = new JTextField(12);
		seqtf2.setMaximumSize(seqtf2.getPreferredSize());
		seqtf2.setToolTipText("<html>Matcher will check for motif anywhere in the loop.<br>Use \"N\" to enforce a specific location!</html>");
		seqtf2.setEnabled(false);
		seqtf2.addCaretListener(this);
		seqtf2buf = seqtf2.getText();
		
		seqlabelpanel.setLayout(new BoxLayout(seqlabelpanel, BoxLayout.PAGE_AXIS));
		seqlabelpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		seqlabelpanel.add(sequencelabel);
		seqlabelpanel.add(Box.createRigidArea(new Dimension(0,20)));
		seqlabelpanel.add(seql1);
		seqlabelpanel.add(Box.createRigidArea(new Dimension(0,10)));
		seqlabelpanel.add(seqtf1);
		seqlabelpanel.add(Box.createRigidArea(new Dimension(0,20)));
		seqlabelpanel.add(seql2);
		seqlabelpanel.add(Box.createRigidArea(new Dimension(0,10)));
		seqlabelpanel.add(seqtf2);
		
		
		seqselectpanel = new SeqSelectPanel();
		seqselectpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		card3.setLayout(new BoxLayout(card3, BoxLayout.LINE_AXIS));
		card3.add(seqselectpanel);
		card3.add(Box.createRigidArea(new Dimension(20,0)));
		card3.add(seqlabelpanel);
		card3.add(Box.createRigidArea(new Dimension(0,10)));
		//END OF SEQUENCE MOTIF PANEL
		
		
//		GLOBAL SIZE PANEL
		JPanel card4 = new JPanel();
		
		ButtonGroup bg3 = new ButtonGroup();
		JPanel gdefaultpanel = new JPanel();
		gdeflen = new JRadioButton("No size restriction");
		gdeflen.setToolTipText("No global size restriction effective upon the substructure rooted at this multiloop");
		
		JPanel grangepanel = new JPanel();
		grange = new JRadioButton("Global size range");
		grange.setToolTipText("<html>Specify minimum and/or maximum number of bases contained in the substructure<br>rooted at and including this building block</html>");
		gl2 = new JLabel("Minimum number of bases in substructure:  ");
		gl3 = new JLabel("Maximum number of bases in substructure: ");
		JPanel grangevaluepanel1 = new JPanel();
		JPanel grangevaluepanel2 = new JPanel();
		
		if(!multi.hasGlobalLength()){
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
			glowerend.setText(multi.getMinGlobalLength());
			gupperend.setText(multi.getMaxGlobalLength());
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
		
	
		
		
		tabbedPane.addTab("Exits", card1);
		tabbedPane.addTab("Multiloop Size",card2);
		tabbedPane.addTab("Sequence", card3);
		tabbedPane.addTab("Global Size", card4);
		
		switch(type){
		case 6: tabbedPane.setSelectedIndex(0); break;		//Exits
		case 2: tabbedPane.setSelectedIndex(2); break;		//Sequence
		case 9: tabbedPane.setSelectedIndex(3); break;		//Global Size
		default: tabbedPane.setSelectedIndex(1);			//Size
		}
		
		buttonpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
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
			lowerend.setText(null);
			l3.setEnabled(true);
			upperend.setText(null);
			int chosenexit = sizeselectpanel.getChosenExitNum() - (rotation/45);
            if(chosenexit < 0){
                chosenexit = 8 + chosenexit;
            }
			int minl = multi.getMinLoopLength(chosenexit);
			int maxl = multi.getMaxLoopLength(chosenexit);
			if(minl != -1){
				lowerend.setText(String.valueOf(minl));
			}
			if(maxl != -1){
				upperend.setText(String.valueOf(maxl));
			}
			lowerendbuf = lowerend.getText();
			lowerend.setEnabled(true);
			upperendbuf = lowerend.getText();
			upperend.setEnabled(true);
		}
		if(src == exact){
			l1.setEnabled(true);
			t1.setText(null);
			int chosenexit = sizeselectpanel.getChosenExitNum() - (rotation/45);
            if(chosenexit < 0){
                chosenexit = 8 + chosenexit;
            }
			int l = multi.getExactLoopLength(chosenexit);
			if(l != -1){
				t1.setText(String.valueOf(l));
			}
			t1buf = t1.getText();
			t1.setEnabled(true);
			l2.setEnabled(false);
			lowerend.setEnabled(false);
			l3.setEnabled(false);
			upperend.setEnabled(false);
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
			glowerend.setText(multi.getMinGlobalLength());
			glowerendbuf = glowerend.getText();
			glowerend.setEnabled(true);
			gl3.setEnabled(true);
			gupperend.setText(multi.getMaxGlobalLength());
			gupperendbuf = gupperend.getText();
			gupperend.setEnabled(true);
		}
		//save changes and close window
		if(src == ok){  
		    //no size restriction
			if(deflen.isSelected()){
			    //set the chosen loop to default size
			    int chosenexit = sizeselectpanel.getChosenExitNum() - (rotation/45);
			    if(chosenexit < 0){
			        chosenexit = 8 + chosenexit;
			    }
				multi.setToDefaultLength(chosenexit);
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
					if(l<(MultiLoop.MIN)){
						throw new NumberFormatException("Negative loop lengths are not possible.");
					}
					if(l>(MultiLoop.MAX) && !t1buf.equals(t1.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(MultiLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					//check stored motif for compatibility with size restriction
					int chosenexit = sizeselectpanel.getChosenExitNum() - (rotation/45);
	                if(chosenexit < 0){
	                    chosenexit = 8 + chosenexit;
	                }
					String motif = multi.getLoopMotif(chosenexit);
					if(motif != null && motif.length() > l){
						int retval = JOptionPane.showConfirmDialog(null,"The length of the motif ("+motif.length()+") is larger than allowed by length restrictions ("+l+").\nIf you continue, the stored motif will be discarded.\nDo you want to continue?", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
						else{
							multi.setLoopMotif(chosenexit,null);
						}
					}
					multi.setExactLoopLength(chosenexit,l);
				}
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			//range value
			else if(range.isSelected()){
				int low=-1;
				int up=-1;
				try{
				    //check and parse lower value
					if(lowerend.getText() != null && !(lowerend.getText()).equals("")){
						low = Integer.parseInt(lowerend.getText());	
						if(low<(MultiLoop.MIN)){
							throw new NumberFormatException("Negative loop lengths are not possible.");
						}
						if(low>(MultiLoop.MAX) && !lowerendbuf.equals(lowerend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(MultiLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
					}
					//parse and check upper value
					if(upperend.getText() != null && !(upperend.getText()).equals("")){
						up = Integer.parseInt(upperend.getText());
						if(up<(MultiLoop.MIN)){
							throw new NumberFormatException("Negative loop lengths are not possible.");
						}
						if(up>(MultiLoop.MAX) && !upperendbuf.equals(upperend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(MultiLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
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
				if(low == -1 && up == -1){
					deflen.doClick();
					return;
				}
				//check stored motif for compatibility with size restriction
				int chosenexit = sizeselectpanel.getChosenExitNum() - (rotation/45);
                if(chosenexit < 0){
                    chosenexit = 8 + chosenexit;
                }
				String motif = multi.getLoopMotif(chosenexit);
				if(up != -1 && motif != null && motif.length() > up){
					int retval = JOptionPane.showConfirmDialog(null,"The length of the motif ("+motif.length()+" is larger than allowed by length restrictions ("+up+").\nIf you continue, the stored motif will be discarded.\nDo you want to continue?", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
					if(retval == JOptionPane.CANCEL_OPTION){
						return;
					}
					else{
						multi.setLoopMotif(chosenexit,null);
					}
				}
				multi.setRangeLoopLength(chosenexit,low,up);
			}
			//obtain current (possibly modified) exit structure
			int[] exitstructure = selectorpanel.getExitStructure();
			if(numofexits <= 2){
				JOptionPane.showMessageDialog(null, "A multiloop structure must have at least 3 loop exits.", "alert", JOptionPane.ERROR_MESSAGE);
				return;
			}
			//modification in exit structure
			else if(!selectorpanel.testExitStructure(exitstructbuf)){
				if(selectorpanel.getNumberOfAvailableExits() == 0){
					if(ms.getIsStartElement()){
						if(ds.getNumberOfAvailableMagnets(ms) == 0){
							JOptionPane.showMessageDialog(null, "The RNA motif must have at least one open exit. You cannot remove all available exits!\n Please add at least one open exit.", "alert", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
				}
				//remove motifs and length restrictions for deleted loops
				for(int i=0;i<8;i++){
					if(exitstructbuf[i] != exitstructure[i]){
						if(exitstructure[i] != 1){
							multi.setLoopMotif(i,null);
							multi.setToDefaultLength(i);
						}
					}
				}
				ds.adjustEnds(ms.adjustAll(exitstructure));
				ds.adjustShapeField();
				ds.adjustHairpinDockable();
			}
			//bp available: check and store
			else if(seqtf1.isEnabled()){
				String bptext = seqtf1.getText();
				if(bptext != null){
				    int chosenexit = seqselectpanel.getChosenExitNum() - (rotation/45);
	                if(chosenexit < 0){
	                    chosenexit = 8 + chosenexit;
	                }
					if(!Iupac.isCorrectMotif(bptext)){
						int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else if(bptext.length() == 2){
						multi.setBasepair(chosenexit,seqtf1.getText());
					}
					else if(bptext.length() == 0){
						multi.setBasepair(chosenexit,null);
					}
					else{
						int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
				}
			}
			//loop motif available: check and store
			else if(seqtf2.isEnabled()){
				String looptext = seqtf2.getText();
				int chosenexit = seqselectpanel.getChosenExitNum() - (rotation/45);
                if(chosenexit < 0){
                    chosenexit = 8 + chosenexit;
                }
				if(looptext != null){
					if(!Iupac.isCorrectMotif(looptext)){
						int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					else if(multi.motifAdditionPossible(chosenexit,seqtf2.getText())){
						multi.setLoopMotif(chosenexit,seqtf2.getText());
					}
					else{
						int retval = JOptionPane.showConfirmDialog(null,"The length of the motif is larger than allowed by length restrictions.\nIf you continue, the currently selected motif will be discarded.\nDo you want to continue?", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
				}
			}
			//no global size restriction
			if(gdeflen.isSelected()){
				multi.removeGlobalLength();
			}
			//global size range
			else if(grange.isSelected()){
				int glow=0;
				int gup=0;
				//parse and check size values
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
				//store size values
				if(glow != 0){
					multi.setMinGlobalLength(glow);
				}
				else{
					multi.removeGlobalMin();
				}
				if(gup != 0){
					multi.setMaxGlobalLength(gup);
				}
				else{
					multi.removeGlobalMax();
				}
			}
			ms.setSelected(false);
			ds.unselectShape();
			ds.repaint();
			dispose();
		}
		//store changes; keep window open
		if(src == apply){
		    //Size pane
			if(tabbedPane.isEnabledAt(1)){
			    //no restriction: set to default
				if(deflen.isSelected()){
					lowerend.setText(null);
					upperend.setText(null);
					t1.setText(null);
					t1buf = t1.getText();
					lowerendbuf = lowerend.getText();
					upperendbuf = upperend.getText();
					int chosenexit = sizeselectpanel.getChosenExitNum() - (rotation/45);
	                if(chosenexit < 0){
	                    chosenexit = 8 + chosenexit;
	                }
					multi.setToDefaultLength(chosenexit);
				}
				//exact size value
				else if(exact.isSelected()){
					try{
					    //none given: default
						if(t1.getText() == null || (t1.getText()).equals("")){
							deflen.doClick();
							return;
						}
					    //Parse and check value
						int l = Integer.parseInt(t1.getText());
						if(l<(MultiLoop.MIN)){
							throw new NumberFormatException("Negative loop lengths are not possible.");
						}
						if(l>(MultiLoop.MAX) && !t1buf.equals(t1.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(MultiLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
						//check if stored motif is compatible
						int chosenexit = sizeselectpanel.getChosenExitNum() - (rotation/45);
		                if(chosenexit < 0){
		                    chosenexit = 8 + chosenexit;
		                }
						String motif = multi.getLoopMotif(chosenexit);
						if(motif != null && motif.length() > l){
							int retval = JOptionPane.showConfirmDialog(null,"The length of the motif ("+motif.length()+") is larger than allowed by length restrictions ("+l+").\nIf you continue, the stored motif will be discarded.\nDo you want to continue?", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							else{
								multi.setLoopMotif(chosenexit,null);
								seqtf2.setText(null);
								seqtf2buf = seqtf2.getText();
							}
						}
						lowerend.setText(null);
						upperend.setText(null);
						lowerendbuf = lowerend.getText();
						upperendbuf = upperend.getText();
						t1buf = t1.getText();
						multi.setExactLoopLength(chosenexit,l);
					}
					catch(NumberFormatException nfe){
						JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				//range values
				else if(range.isSelected()){
					int low=-1;
					int up=-1;
					//parse and check values
					try{
						if(lowerend.getText() != null && !(lowerend.getText()).equals("")){
							low = Integer.parseInt(lowerend.getText());	
							if(low<(MultiLoop.MIN)){
								throw new NumberFormatException("Negative loop lengths are not possible.");
							}
							if(low>(MultiLoop.MAX) && !lowerendbuf.equals(lowerend.getText())){
								int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(MultiLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
								if(retval == JOptionPane.CANCEL_OPTION){
									return;
								}
							}
						}
						if(upperend.getText() != null && !(upperend.getText()).equals("")){
							up = Integer.parseInt(upperend.getText());
							if(up<(MultiLoop.MIN)){
								throw new NumberFormatException("Negative loop lengths are not possible.");
							}
							if(up>(MultiLoop.MAX) && !upperendbuf.equals(upperend.getText())){
								int retval = JOptionPane.showConfirmDialog(null,"Loop regions with more than "+(MultiLoop.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
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
					if(low == -1 && up == -1){
						deflen.doClick();
						return;
					}
					//check motif for compatibility
					int chosenexit = sizeselectpanel.getChosenExitNum() - (rotation/45);
	                if(chosenexit < 0){
	                    chosenexit = 8 + chosenexit;
	                }
					String motif = multi.getLoopMotif(chosenexit);
					if(up != -1 && motif != null && motif.length() > up){
						int retval = JOptionPane.showConfirmDialog(null,"The length of the motif ("+motif.length()+") is larger than allowed by length restrictions ("+up+").\nIf you continue, the stored motif will be discarded.\nDo you want to continue?", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
						else{
							multi.setLoopMotif(chosenexit,null);
							seqtf2.setText(null);
							seqtf2buf = seqtf2.getText();
						}
					}
					t1.setText(null);
					t1buf = t1.getText();
					lowerendbuf = lowerend.getText();
					upperendbuf = upperend.getText();
					multi.setRangeLoopLength(chosenexit,low,up);
				}
			}
			//Exit pane
			else if(tabbedPane.isEnabledAt(0)){
				int[] exitstructure = selectorpanel.getExitStructure();
				if(numofexits <= 2){
					JOptionPane.showMessageDialog(null, "A multiloop structure must have at least 3 loop exits.", "alert", JOptionPane.ERROR_MESSAGE);
					return;
				}
				//compare new with stored exit structure
				else if(!selectorpanel.testExitStructure(exitstructbuf)){
					if(selectorpanel.getNumberOfAvailableExits() == 0){
						if(ms.getIsStartElement()){
							if(ds.getNumberOfAvailableMagnets(ms) == 0){
								JOptionPane.showMessageDialog(null, "The RNA motif must have at least one open exit. You cannot remove all available exits!\n Please add at least one open exit.", "alert", JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
					}
					for(int i=0;i<8;i++){
						if(exitstructbuf[i] != exitstructure[i]){
							//an exit was added at i
							if(exitstructure[i] == 1){
								seqselectpanel.removeSelection();
								sizeselectpanel.removeSelection();
							}
							//an exit was removed at i
							else{
								multi.setToDefaultLength(i);
								multi.setLoopMotif(i,null);
								seqselectpanel.removeSelection();
								sizeselectpanel.removeSelection();
							}
						}
					}
					ds.adjustEnds(ms.adjustAll(exitstructure));
					exitstructbuf = exitstructure;
					ds.adjustShapeField();
					ds.adjustHairpinDockable();
					seqselectpanel.adjustShape();
					sizeselectpanel.adjustShape();
					selectorpanel.update();
				}
			}
			//Sequence pane
			else if(tabbedPane.isEnabledAt(2)){
			    //basepair
			    int chosenexit = seqselectpanel.getChosenExitNum() - (rotation/45);
                if(chosenexit < 0){
                    chosenexit = 8 + chosenexit;
                }
				if(seqtf1.isEnabled()){
					String bptext = seqtf1.getText();
					if(bptext != null){
						if(!Iupac.isCorrectMotif(bptext)){
							int retval = JOptionPane.showConfirmDialog(null,"The basepair you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							seqtf1.setText(null);
						}
						else if(bptext.length() == 2){
							multi.setBasepair(chosenexit,seqtf1.getText());
						}
						else if(bptext.length() == 0){
							multi.setBasepair(chosenexit,null);
						}
						else{
							int retval = JOptionPane.showConfirmDialog(null,"Please specify only one basepair in the format 'GC'", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							seqtf1.setText(multi.getBasepairString(chosenexit));
						}
					}
					seqtf1buf = seqtf1.getText();
				}
				//loop motif
				else if(seqtf2.isEnabled()){
					String looptext = seqtf2.getText();
					if(looptext != null){
						if(!Iupac.isCorrectMotif(looptext)){
							int retval = JOptionPane.showConfirmDialog(null,"The motif you specified contains non-IUPAC characters.\nIt will be discarded if you continue.\nDo you wish to continue?", "Wrong Code", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							seqtf2.setText(null);
						}
						else if(multi.motifAdditionPossible(chosenexit,seqtf2.getText())){
							multi.setLoopMotif(chosenexit,seqtf2.getText());
						}
						else{
							int retval = JOptionPane.showConfirmDialog(null,"The length of the motif is larger than allowed by length restrictions.\nIf you continue, the currently selected motif will be discarded.\nDo you want to continue?", "Wrong Length", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
							seqtf2.setText(multi.getLoopMotif(chosenexit));
						}
					}
					seqtf2buf = seqtf2.getText();
				}
			}
			//Global size pane
			else if(tabbedPane.isEnabledAt(3)){
			    if(gdeflen.isSelected()){
			        multi.removeGlobalLength();
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
			            multi.setMinGlobalLength(glow);
			        }
			        else{
			            multi.removeGlobalMin();
			        }
			        if(gup != 0){
			            multi.setMaxGlobalLength(gup);
			        }
			        else{
			            multi.removeGlobalMax();
			        }
			    }
			}
			apply.setEnabled(false);
			tabbedPane.setEnabledAt(0,true);
			tabbedPane.setEnabledAt(1,true);
			tabbedPane.setEnabledAt(2,true);
			tabbedPane.setEnabledAt(3,true);
			ds.repaint();
			repaint();
		}
		//exit without changes
		if(src == cancel){
			ms.setSelected(false);
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
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(2,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(lowerendbuf.equals(lowerend.getText()) && upperendbuf.equals(upperend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(2,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == lowerend){
			if(!lowerendbuf.equals(lowerend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(2,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(t1buf.equals(t1.getText()) && upperendbuf.equals(upperend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(2,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == upperend){
			if(!upperendbuf.equals(upperend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(2,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(lowerendbuf.equals(lowerend.getText()) && t1buf.equals(t1.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(2,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == seqtf1){
			if(!(seqtf1.getText()).equals(seqtf1buf) && !((seqtf1.getText()).equals("") && seqtf1buf == null)){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(seqtf2buf.equals(seqtf2.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(1,true);
				tabbedPane.setEnabledAt(3,true);
			}
		}
		if(src == seqtf2){
			if(!(seqtf2.getText()).equals(seqtf2buf) && !((seqtf2.getText()).equals("") && seqtf2buf == null)){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
				tabbedPane.setEnabledAt(1,false);
				tabbedPane.setEnabledAt(3,false);
			}
			else if(seqtf1buf.equals(seqtf1.getText())){
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
	    ms.editClosed();
	    super.dispose();
	}
	
	/**
	 * This class describes method for the exit selector panel of a multiloop
	 * 
	 * @author Janina Reeder
	 */
	private class SelectorPanel extends JPanel implements MouseListener{
		
		private static final long serialVersionUID = -3397206811691279577L;
		private Area area;
		private double xloc; //origin x-coordinate
		private double yloc; //origin y-coordinate
		private double width; //total width of the rectangle
		private double circleheight; //height of the imposed circle
		private double circlewidth;
		private double rheight;
		private Shape[] selectors;
		private Shape[] selectcircles;
		private int[] exitstructure;
		private boolean foundSS;
		private boolean removalok;
		
		/**
		 * Constructor for a SelectorPanel
		 */
		public SelectorPanel(){
			addMouseListener(this);
			xloc = 110;
			yloc = 5;
			width = 40;
			circlewidth = 145;
			circleheight = 145;
			rheight = 15;
			foundSS = ms.findAllSS();
			//Exit selectors
			selectors = new Shape[8];
			selectcircles = new Shape[8];
			exitstructure = new int[8];
			createArea();
			setPreferredSize(new Dimension(200,200));
			//TODO: abfrage pruefen
			removalok = true;//ms.exitRemovalPossible();
		}
		
		/**
		 * Repaints the selector panel
		 *
		 */
		public void update(){
		    //TODO: abfrage pruefen!!!
			removalok = true;//ms.exitRemovalPossible();
			MultiEdit.this.repaint();
		}
		
		/**
		 * returns an array that encodes the exits of the multiloop
		 *
		 * @return int-array with 1 for exit and 0 for no exit
		 */
		public int[] getExitStructure(){
			int[] es = new int[8];
			for(int i=0;i<8;i++){
				if(exitstructure[i] == 2){
					es[i]=1;
				}
				else{
					es[i]=exitstructure[i];
				}
			}
			return es;
		}
		
		/**
		 * Returns the number of available exits: i.e. those where a building block can be added
		 * 
		 * @return, the number of available exits
		 */
		public int getNumberOfAvailableExits(){
			int noae = 0;
			for(int i=0;i<8;i++){
				if(exitstructure[i] == 2){
					noae++;
				}
			}
			return noae;
		}
		
		/**
		 * This method checks wether the current exitstructure is different from
		 * the given one.
		 * 
		 * @param cmpexits, the exit structure to compare with
		 * @return true, if they represent the same exits; else false.
		 */
		public boolean testExitStructure(int[] cmpexits){
			for(int i=0;i<8;i++){
				if(exitstructure[i] > 0 && cmpexits[i] == 0){
					return false;
				}
				else if(exitstructure[i] == 0 && cmpexits[i] == 1){
					return false;
				}
			}
			return true;
		}
		
		/**
		 * creates the Area of the Multiloop which is to be drawn
		 */
		public void createArea(){
			area = new Area(new Ellipse2D.Double(xloc-((circlewidth-width)/2),yloc+((rheight/3)*2),circlewidth, circleheight));
			Rectangle2D base = new Rectangle2D.Double(xloc,yloc,width,rheight);
			Rectangle2D selectbase = new Rectangle2D.Double(xloc,yloc,width,rheight*2);
			Ellipse2D circle = new Ellipse2D.Double(xloc+15,yloc+5,10,10);
			AffineTransform atexits = new AffineTransform();
			for(int i=0;i<8;i++){
				Shape transarea = atexits.createTransformedShape(base);
				area.add(new Area(transarea));
				Shape transselector = atexits.createTransformedShape(selectbase);
				selectors[i] = transselector;
				Shape transcircle = atexits.createTransformedShape(circle);
				selectcircles[i] = transcircle;
				exitstructure[i] = ms.getExit(i);
				exitstructbuf[i] = exitstructure[i];
				atexits.rotate(StrictMath.toRadians(45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
			}
		}
		
		//not needed, but required by MouseListener
		public void mousePressed(MouseEvent me){}
		public void mouseReleased(MouseEvent me){}
		public void mouseExited(MouseEvent me){}
		public void mouseEntered(MouseEvent me){}
		
		/**
		 * Mouse Listener reacts to user clicks: exits are selected or 
		 * deselected
		 *
		 * @param me the MouseEvent which ocurred
		 */
		public void mouseClicked(MouseEvent me){
		    //TODO: not satisfying
			if(foundSS){
				JOptionPane.showMessageDialog(null,"You cannot change any exits as long as a single strand is connected to the Multiloop");
			}
			else{
				for(int i=0;i<8;i++){
				    //mouse within basepair selector
					if(selectors[i].contains(me.getPoint())){
					    //add a new exit
						if(exitstructure[i] == 0){
							exitstructure[i]=2;
							numofexits++;
							apply.setEnabled(true);
							tabbedPane.setEnabledAt(2,false);
							tabbedPane.setEnabledAt(1,false);
						}
						//remove an exit
						else if(exitstructure[i]==2 && removalok){
							exitstructure[i]=0;
							numofexits--;
							apply.setEnabled(true);
							tabbedPane.setEnabledAt(2,false);
							tabbedPane.setEnabledAt(1,false);
						}
						//removal not possible: warning
						else if(exitstructure[i] == 1){
							JOptionPane.showMessageDialog(null, "You cannot remove an exit with an appending RNA structure.\nPlease remove the appended structure first", "alert", JOptionPane.ERROR_MESSAGE);
						}
						else if(exitstructure[i] == 2){
							JOptionPane.showMessageDialog(null, "You cannot remove this exit as it is the last one", "alert", JOptionPane.ERROR_MESSAGE);
						}
						break;
					}
				}
			}
			MultiEdit.this.repaint();
		}
		
		/**
		 * Method that does the job of painting a SelectorPanel
		 *
		 * @param g the Graphics environment that does the painting
		 */
		public void paintComponent(Graphics g){
			Graphics2D g2 = (Graphics2D) g;
			
			for(int i=0;i<8;i++){
				g2.setPaint(Color.lightGray);
				g2.draw(selectors[i]);
				if(exitstructure[i] == 2){
					if(removalok){
						g2.setPaint(Color.red);
					}
					else{
						g2.setPaint(Color.lightGray);
					}
					g2.fill(selectcircles[i]);
				}
				else if(exitstructure[i] == 1){
					g2.setPaint(Color.lightGray);
					g2.fill(selectcircles[i]);
				}
			}
			g2.setPaint(Color.black);
			g2.draw(area);
		}
		
	}
	
	/**
	 * This class describes method for the sequence selector panel of a multiloop
	 * 
	 * @author Janina Reeder
	 */
	private class SeqSelectPanel extends JPanel implements MouseListener{
		
		private static final long serialVersionUID = 8741921178113980542L;
		private Area area;
		private double xloc; //origin x-coordinate
		private float x2,x3;
		private double yloc; //origin y-coordinate
		private float y2,y3;
		private double width; //total width of the rectangle
		private double circleheight; //height of the imposed circle
		private double circlewidth;
		private double rheight;
		private Shape[] selectors;
		private Area[] loopselectors;
		private int[] exitstructure;
		private Shape selectedshape;
		private int chosenindex;
		
		
		/**
		 * Constructor for a SelectorPanel
		 */
		public SeqSelectPanel(){
			addMouseListener(this);
			xloc = 80;
			yloc = 10;
			width = 40;
			circlewidth = 145;
			circleheight = 145;
			rheight = 15;
			Point2D.Double p2 = new Point2D.Double(xloc+1,yloc);
			Point2D.Double p3 = new Point2D.Double(xloc+1,yloc+rheight*2);
			AffineTransform at = new AffineTransform();
			at.rotate(StrictMath.toRadians(45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
			at.transform(p2,p2);
			at.transform(p3,p3);
			x2 = (float) p2.getX();
			y2 = (float) p2.getY();
			x3 = (float) p3.getX();
			y3 = (float) p3.getY();
			//basepair selectors
			selectors = new Shape[8];
			//loop selectors
			loopselectors = new Area[8];
			exitstructure = new int[8];
			selectedshape = null;
			chosenindex = -1;
			createArea();
			setPreferredSize(new Dimension(200,200));
		}
		
		/**
		 * This method adjust the Sequence Selector panel according to a change
		 * in the exit structure (via the exit selector panel).
		 *
		 */
		public void adjustShape(){
			selectors = new Shape[8];
			loopselectors = new Area[8];
			exitstructure = new int[8];
			createArea();
			repaint();
		}
		
		/**
		 * This method removes any selection and text inputs from the
		 * sequence selector panel.
		 *
		 */
		public void removeSelection(){
			selectedshape = null;
			chosenindex = -1;
			seql1.setEnabled(false);
			seqtf1.setEnabled(false);
			seqtf1.setText(null);
			seqtf1buf = seqtf1.getText();
			seql2.setEnabled(false);
			seqtf2.setEnabled(false);
			seqtf2.setText(null);
			seqtf2buf = seqtf2.getText();
		}
		
		/**
		 * creates the Area of the Multiloop which is to be drawn
		 */
		public void createArea(){
			area = new Area(new Ellipse2D.Double(xloc-((circlewidth-width)/2),yloc+((rheight/3)*2),circlewidth, circleheight));
			Rectangle2D base = new Rectangle2D.Double(xloc,yloc,width,rheight);
			Rectangle2D selectbase = new Rectangle2D.Double(xloc,yloc,width,rheight*2);
			GeneralPath looppath = new GeneralPath();
			looppath.moveTo(x2,y2);
			looppath.lineTo((float)(xloc+width-1),(float)(yloc));
			looppath.lineTo((float)(xloc+width-1),(float)(yloc+rheight*2));
			looppath.lineTo(x3,y3);
			looppath.lineTo(x2,y2);
			
			AffineTransform atexits = new AffineTransform();
			Shape[] transloops = new Shape[8];
			Shape[] transselectors = new Shape[8];
			for(int i=0;i<8;i++){
				exitstructure[i] = ms.getExit(i);
				Shape transarea = atexits.createTransformedShape(base);
				//base selectors
				transselectors[i] = atexits.createTransformedShape(selectbase);
				if(exitstructure[i] > 0){
					area.add(new Area(transarea));
					selectors[i] = transselectors[i];
				}
				//loop selectors
				transloops[i] = atexits.createTransformedShape(looppath);
				atexits.rotate(StrictMath.toRadians(45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
			}
			//loop selectors
			int ll = 0;
			for(int i=0;i<8;i++){
				if(exitstructure[i] > 0){
					loopselectors[i] = new Area(transloops[i]);
					ll = i;
					for(int j=i+1;j<8;j++){
						if(exitstructure[j] == 0){
							loopselectors[i].add(new Area(transselectors[j]));
							loopselectors[i].add(new Area(transloops[j]));
						}
						else{
							break;
						}
						atexits.rotate(StrictMath.toRadians(45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
					}
				}
				atexits.rotate(StrictMath.toRadians(45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
			}
			if(exitstructure[0] == 0){
				loopselectors[ll].add(new Area(transselectors[0]));
				loopselectors[ll].add(new Area(transloops[0]));
				for(int j=1;j<6;j++){
					if(exitstructure[j] == 0){
						loopselectors[ll].add(new Area(transselectors[j]));
						loopselectors[ll].add(new Area(transloops[j]));
					}
					else{
						break;
					}
				}
			}
		}
		
		//not needed, but required by MouseListener
		public void mousePressed(MouseEvent me){}
		public void mouseReleased(MouseEvent me){}
		public void mouseExited(MouseEvent me){}
		public void mouseEntered(MouseEvent me){}
		
		/**
		 * Mouse Listener reacts to user clicks: exits are selected or 
		 * deselected
		 *
		 * @param me the MouseEvent which ocurred
		 */
		public void mouseClicked(MouseEvent me){
			for(int i=0;i<8;i++){
			    //mouse within bp selector
				if(selectors[i] != null && selectors[i].contains(me.getPoint())){
				    //bp not yet stored
					if(seqtf1.isEnabled() && apply.isEnabled()){
						int retval = JOptionPane.showConfirmDialog(null,"The basepair of the currently selected exit was altered.\nIt will be discarded if you continue.\nDo you wish to save it before continueing?", "Changes not saved", JOptionPane.YES_NO_OPTION);
						if(retval == JOptionPane.YES_OPTION){
							apply.doClick();
						}
					}
					//motif not yet stored
					else if(seqtf2.isEnabled() && apply.isEnabled()){
						int retval = JOptionPane.showConfirmDialog(null,"The motif of the currently selected loop was altered.\nIt will be discarded if you continue.\nDo you wish to save it before continueing?", "Changes not saved", JOptionPane.YES_NO_OPTION);
						if(retval == JOptionPane.YES_OPTION){
							apply.doClick();
						}
					}
					//store selectedshape for visualization
					selectedshape = selectors[i];
					//remember chosenindex for storage
					chosenindex = i;

                    int roti = i - (rotation/45);
                    if(roti < 0){
                        roti = 8 + roti;
                    }
					//enabled input fields
					seql1.setEnabled(true);
					seqtf1.setEnabled(true);
					seqtf1.setText(multi.getBasepairString(roti));
					seqtf1buf = seqtf1.getText();
					seql2.setEnabled(false);
					seqtf2.setEnabled(false);
					seqtf2.setText(null);
					seqtf2buf = seqtf2.getText();
					MultiEdit.this.repaint();
					tabbedPane.setEnabledAt(0,true);
					tabbedPane.setEnabledAt(1,true);
					apply.setEnabled(false);
					return;
				}
				//mouse within loop selector
				else if(loopselectors[i] != null && loopselectors[i].contains(me.getPoint())){
				    //bp or motif not stored yet
					if(seqtf1.isEnabled() && apply.isEnabled()){
						int retval = JOptionPane.showConfirmDialog(null,"The basepair of the currently selected exit was altered.\nIt will be discarded if you continue.\nDo you wish to save it before continueing?", "Changes not saved", JOptionPane.YES_NO_OPTION);
						if(retval == JOptionPane.YES_OPTION){
							apply.doClick();
						}
					}
					else if(seqtf2.isEnabled() && apply.isEnabled()){
						int retval = JOptionPane.showConfirmDialog(null,"The motif of the currently selected loop was altered.\nIt will be discarded if you continue.\nDo you wish to save it before continueing?", "Changes not saved", JOptionPane.YES_NO_OPTION);
						if(retval == JOptionPane.YES_OPTION){
							apply.doClick();
						}
					}
					//update selectedshape and chosenindex, enable input fields
					selectedshape = loopselectors[i];
					chosenindex = i;

                    int roti = i - (rotation/45);
                    if(roti < 0){
                        roti = 8 + roti;
                    }
					seql2.setEnabled(true);
					seqtf2.setEnabled(true);
					seqtf2.setText(multi.getLoopMotif(roti));
					seqtf2buf = seqtf2.getText();
					seql1.setEnabled(false);
					seqtf1.setEnabled(false);
					seqtf1.setText(null);
					seqtf1buf = seqtf1.getText();
					MultiEdit.this.repaint();
					apply.setEnabled(false);
					tabbedPane.setEnabledAt(0,true);
					tabbedPane.setEnabledAt(1,true);
					return;
				}
			}
			//mouse not within select field: remove selection
			if(!area.contains(me.getPoint())){
				if(seqtf1.isEnabled() && apply.isEnabled()){
					int retval = JOptionPane.showConfirmDialog(null,"The basepair of the currently selected exit was altered.\nIt will be discarded if you continue.\nDo you wish to save it before continueing?", "Changes not saved", JOptionPane.YES_NO_OPTION);
					if(retval == JOptionPane.YES_OPTION){
						apply.doClick();
					}
				}
				else if(seqtf2.isEnabled() && apply.isEnabled()){
					int retval = JOptionPane.showConfirmDialog(null,"The motif of the currently selected loop was altered.\nIt will be discarded if you continue.\nDo you wish to save it before continueing?", "Changes not saved", JOptionPane.YES_NO_OPTION);
					if(retval == JOptionPane.YES_OPTION){
						apply.doClick();
					}
				}
				selectedshape = null;
				chosenindex = -1;
				seql1.setEnabled(false);
				seqtf1.setEnabled(false);
				seqtf1.setText(null);
				seqtf1buf = seqtf1.getText();
				seql2.setEnabled(false);
				seqtf2.setEnabled(false);
				seqtf2.setText(null);
				seqtf2buf = seqtf2.getText();
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(1,true);
			}
			MultiEdit.this.repaint();
		}
		
		/**
		 * This method returns the index of the exit that is currently selected.
		 * 
		 * @return the index of the chosen exit
		 */
		public int getChosenExitNum(){
			return chosenindex;
		}
		
		/**
		 * Method that does the job of painting a SelectorPanel
		 *
		 * @param g the Graphics environment that does the painting
		 */
		public void paintComponent(Graphics g){
			Graphics2D g2 = (Graphics2D) g;
			
			if(selectedshape != null){
				g2.setPaint(new Color(153,0,0,50));
				g2.fill(selectedshape);
			}
			for(int i=0;i<8;i++){
				if(selectors[i] != null){
					g2.setPaint(Color.lightGray);
					g2.draw(selectors[i]);
				}
				if(loopselectors[i] != null){
					g2.setPaint(Color.lightGray);
					g2.draw(loopselectors[i]);
				}
			}
			g2.setPaint(Color.black);
			g2.draw(area);
		}
		
	}
	
	
	/**
	 * This class describes method for the size selector panel of a multiloop
	 * 
	 * @author Janina Reeder
	 */
	private class SizeSelectPanel extends JPanel implements MouseListener{
		
		private static final long serialVersionUID = 8741921178113980542L;
		private Area area;
		private double xloc; //origin x-coordinate
		private float x2,x3;
		private double yloc; //origin y-coordinate
		private float y2,y3;
		private double width; //total width of the rectangle
		private double circleheight; //height of the imposed circle
		private double circlewidth;
		private double rheight;
		private Shape[] selectors;
		private Area[] loopselectors;
		private int[] exitstructure;
		private Shape selectedshape;
		private int chosenindex;
		
		
		/**
		 * Constructor for a SelectorPanel
		 */
		public SizeSelectPanel(){
			addMouseListener(this);
			xloc = 80;
			yloc = 10;
			width = 40;
			circlewidth = 145;
			circleheight = 145;
			rheight = 15;
			Point2D.Double p2 = new Point2D.Double(xloc+1,yloc);
			Point2D.Double p3 = new Point2D.Double(xloc+1,yloc+rheight*2);
			AffineTransform at = new AffineTransform();
			at.rotate(StrictMath.toRadians(45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
			at.transform(p2,p2);
			at.transform(p3,p3);
			x2 = (float) p2.getX();
			y2 = (float) p2.getY();
			x3 = (float) p3.getX();
			y3 = (float) p3.getY();
			//basepair selectors
			selectors = new Shape[8];
			//loop selectors
			loopselectors = new Area[8];
			exitstructure = new int[8];
			selectedshape = null;
			chosenindex = -1;
			createArea();
			setPreferredSize(new Dimension(200,200));
		}
		
		/**
		 * This method adjust the Sequence Selector panel according to a change
		 * in the exit structure (via the exit selector panel).
		 *
		 */
		public void adjustShape(){
			selectors = new Shape[8];
			loopselectors = new Area[8];
			exitstructure = new int[8];
			createArea();
			repaint();
		}
		
		/**
		 * This method removes any selection and text inputs from the
		 * sequence selector panel.
		 *
		 */
		public void removeSelection(){
			selectedshape = null;
			chosenindex = -1;
			l1.setEnabled(false);
			t1.setEnabled(false);
			t1.setText(null);
			t1buf = seqtf1.getText();
			l2.setEnabled(false);
			lowerend.setEnabled(false);
			lowerend.setText(null);
			lowerendbuf = lowerend.getText();
			l3.setEnabled(false);
			upperend.setEnabled(false);
			upperend.setText(null);
			upperendbuf = upperend.getText();
			range.setEnabled(false);
			deflen.setEnabled(false);
			exact.setEnabled(false);
			none.setSelected(true);
		}
		
		/**
		 * creates the Area of the Multiloop which is to be drawn
		 */
		public void createArea(){
			area = new Area(new Ellipse2D.Double(xloc-((circlewidth-width)/2),yloc+((rheight/3)*2),circlewidth, circleheight));
			Rectangle2D base = new Rectangle2D.Double(xloc,yloc,width,rheight);
			Rectangle2D selectbase = new Rectangle2D.Double(xloc,yloc,width,rheight*2);
			GeneralPath looppath = new GeneralPath();
			looppath.moveTo(x2,y2);
			looppath.lineTo((float)(xloc+width-1),(float)(yloc));
			looppath.lineTo((float)(xloc+width-1),(float)(yloc+rheight*2));
			looppath.lineTo(x3,y3);
			looppath.lineTo(x2,y2);
			
			AffineTransform atexits = new AffineTransform();
			Shape[] transloops = new Shape[8];
			Shape[] transselectors = new Shape[8];
			for(int i=0;i<8;i++){
				exitstructure[i] = ms.getExit(i);
				Shape transarea = atexits.createTransformedShape(base);
				//base selectors
				transselectors[i] = atexits.createTransformedShape(selectbase);
				if(exitstructure[i] > 0){
					area.add(new Area(transarea));
					selectors[i] = transselectors[i];
				}
				//loop selectors
				transloops[i] = atexits.createTransformedShape(looppath);
				atexits.rotate(StrictMath.toRadians(45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
			}
			//loop selectors
			int ll = 0;
			for(int i=0;i<8;i++){
				if(exitstructure[i] > 0){
					loopselectors[i] = new Area(transloops[i]);
					ll = i;
					for(int j=i+1;j<8;j++){
						if(exitstructure[j] == 0){
							loopselectors[i].add(new Area(transselectors[j]));
							loopselectors[i].add(new Area(transloops[j]));
						}
						else{
							break;
						}
						atexits.rotate(StrictMath.toRadians(45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
					}
				}
				atexits.rotate(StrictMath.toRadians(45),xloc+width/2,yloc+((rheight/3)*2)+circleheight/2);
			}
			if(exitstructure[0] == 0){
				loopselectors[ll].add(new Area(transselectors[0]));
				loopselectors[ll].add(new Area(transloops[0]));
				for(int j=1;j<6;j++){
					if(exitstructure[j] == 0){
						loopselectors[ll].add(new Area(transselectors[j]));
						loopselectors[ll].add(new Area(transloops[j]));
					}
					else{
						break;
					}
				}
			}
		}
		
		//not needed, but required by MouseListener
		public void mousePressed(MouseEvent me){}
		public void mouseReleased(MouseEvent me){}
		public void mouseExited(MouseEvent me){}
		public void mouseEntered(MouseEvent me){}
		
		/**
		 * Mouse Listener reacts to user clicks: exits are selected or 
		 * deselected
		 *
		 * @param me the MouseEvent which ocurred
		 */
		public void mouseClicked(MouseEvent me){
			for(int i=0;i<8;i++){
			    //mouse within loop selector
				if(loopselectors[i] != null && loopselectors[i].contains(me.getPoint())){
				    //previous size not stored yet
					if((t1.isEnabled() || lowerend.isEnabled() || upperend.isEnabled()) && apply.isEnabled()){
						int retval = JOptionPane.showConfirmDialog(null,"The size of the currently selected loop was altered.\nIt will be discarded if you continue.\nDo you wish to save it before continueing?", "Changes not saved", JOptionPane.YES_NO_OPTION);
						if(retval == JOptionPane.YES_OPTION){
							apply.doClick();
						}
					}
					//update selection
					removeSelection();
					selectedshape = loopselectors[i];
					chosenindex = i;
					deflen.setEnabled(true);
					exact.setEnabled(true);
					range.setEnabled(true);
					
					int roti = i - (rotation/45);
	                if(roti < 0){
	                    roti = 8 + roti;
	                }
					
					//enable required input fields
					if(multi.getExactLoopLength(roti) != -1){
						exact.setSelected(true);
						l1.setEnabled(true);
						t1.setEnabled(true);
						t1.setText(String.valueOf(multi.getExactLoopLength(roti)));
						t1buf = t1.getText();
					}
					else if(multi.getMinLoopLength(roti) != -1 || multi.getMaxLoopLength(roti) != -1){
						range.setSelected(true);
						l2.setEnabled(true);
						l3.setEnabled(true);
						lowerend.setEnabled(true);
						upperend.setEnabled(true);
						int minl = multi.getMinLoopLength(roti);
						int maxl = multi.getMaxLoopLength(roti);
						if(minl != -1){
							lowerend.setText(String.valueOf(minl));
						}
						if(maxl != -1){
							upperend.setText(String.valueOf(maxl));
						}
						lowerendbuf = lowerend.getText();
						upperendbuf = upperend.getText();
					}
					else{
						deflen.setSelected(true);
					}
									
					MultiEdit.this.repaint();
					apply.setEnabled(false);
					tabbedPane.setEnabledAt(0,true);
					tabbedPane.setEnabledAt(1,true);
					tabbedPane.setEnabledAt(2,true);
					return;
				}
			}
			//mouse not within selectors: remove any selection
			if(!area.contains(me.getPoint())){
				if((t1.isEnabled() || lowerend.isEnabled() || upperend.isEnabled()) && apply.isEnabled()){
					int retval = JOptionPane.showConfirmDialog(null,"The size of the currently selected loop was altered.\nIt will be discarded if you continue.\nDo you wish to save it before continueing?", "Changes not saved", JOptionPane.YES_NO_OPTION);
					if(retval == JOptionPane.YES_OPTION){
						apply.doClick();
					}
				}
				removeSelection();
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
				tabbedPane.setEnabledAt(1,true);
				tabbedPane.setEnabledAt(2,true);
			}
			MultiEdit.this.repaint();
		}
		
		/**
		 * This method returns the index of the exit that is currently selected.
		 * 
		 * @return the index of the chosen exit
		 */
		public int getChosenExitNum(){
			return chosenindex;
		}
		
		/**
		 * Method that does the job of painting a SelectorPanel
		 *
		 * @param g the Graphics environment that does the painting
		 */
		public void paintComponent(Graphics g){
			Graphics2D g2 = (Graphics2D) g;
			
			if(selectedshape != null){
				g2.setPaint(new Color(153,0,0,50));
				g2.fill(selectedshape);
			}
			for(int i=0;i<8;i++){
				if(selectors[i] != null){
					g2.setPaint(Color.lightGray);
					g2.draw(selectors[i]);
				}
				if(loopselectors[i] != null){
					g2.setPaint(Color.lightGray);
					g2.draw(loopselectors[i]);
				}
			}
			g2.setPaint(Color.black);
			g2.draw(area);
		}
		
	}
} 





