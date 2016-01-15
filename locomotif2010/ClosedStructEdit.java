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
 * This class defines the user interface for editing a ClosedStruct element
 *
 * @author Janina Reeder
 */
public class ClosedStructEdit extends JFrame implements ActionListener, CaretListener{
	
	private static final long serialVersionUID = 7062603644738528528L;
	private ClosedStruct closedstruct;
	private ClosedStructShape cs;
	private JPanel pane;
	private JTabbedPane tabbedPane;
	private JRadioButton deflen, gdeflen,grange;
	private JButton ok, apply, cancel;
	private JLabel gl2, gl3;
	private JTextField glowerend, gupperend;
	private String glowerendbuf, gupperendbuf;
	private DrawingSurface ds;
	
	/**
	 * Constructor for the ClosedStructEdit GUI
	 *
	 * @param cs the parent ClosedStructShape
	 * @param ds the parent DrawingSurface
	 * @param type determines which tab is shown at first
	 */
	public ClosedStructEdit(ClosedStructShape cs, DrawingSurface ds, int type){
		super("ClosedStruct Options");
		
		this.cs = cs;
		this.closedstruct = cs.getClosedStruct();
		this.ds = ds;
		
		JFrame.setDefaultLookAndFeelDecorated(true);
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
	public Container makeContentPane(){
		pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		tabbedPane = new JTabbedPane();
		
		JPanel card1 = new JPanel();
		
		ButtonGroup bg = new ButtonGroup();
		
		//SIZE PANEL
		JPanel defaultpanel = new JPanel();
		deflen = new JRadioButton("No size restriction");
		
		deflen.setSelected(true);
			
		defaultpanel.setLayout(new BoxLayout(defaultpanel, BoxLayout.PAGE_AXIS));
		deflen.setAlignmentX(Component.LEFT_ALIGNMENT);
		deflen.addActionListener(this);
		defaultpanel.add(deflen);
		
		bg.add(deflen);
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
		
		//add size panel: not used in tabbed pane: 
		defaultpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		buttonpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		card1.setLayout(new BoxLayout(card1,BoxLayout.PAGE_AXIS));
		card1.add(defaultpanel);
		
//		GLOBAL SIZE PANEL
		JPanel card4 = new JPanel();
		
		ButtonGroup bg3 = new ButtonGroup();
		JPanel gdefaultpanel = new JPanel();
		gdeflen = new JRadioButton("No size restriction");
		gdeflen.setToolTipText("No global size restriction effective upon the substructure rooted at this closed struct");
		
		JPanel grangepanel = new JPanel();
		grange = new JRadioButton("Global size range");
		grange.setToolTipText("<html>Specify minimum and/or maximum number of bases for the substructure<br> rooted at and including this building block.</html>");
		gl2 = new JLabel("Minimum number of bases in substructure:  ");
		gl3 = new JLabel("Maximum number of bases in substructure: ");
		JPanel grangevaluepanel1 = new JPanel();
		JPanel grangevaluepanel2 = new JPanel();
		
		if(!closedstruct.hasGlobalLength()){
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
			glowerend.setText(closedstruct.getMinGlobalLength());
			gupperend.setText(closedstruct.getMaxGlobalLength());
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
		
		tabbedPane.addTab("Global Size", card4);
		
		
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
			glowerend.setText(closedstruct.getMinGlobalLength());
			glowerendbuf = glowerend.getText();
			glowerend.setEnabled(true);
			gl3.setEnabled(true);
			gupperend.setText(closedstruct.getMaxGlobalLength());
			gupperendbuf = gupperend.getText();
			gupperend.setEnabled(true);
		}
		//save changes and close window
		if(src == ok){
		    //no global size restriction
			if(gdeflen.isSelected()){
				closedstruct.removeGlobalLength();
			}
			//store global size restriction
			else if(grange.isSelected()){
				int glow=0;
				int gup=0;
				try{
				    //parse and chekc minimum
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
				//no values given: no restriction
				if(glow == 0 && gup == 0){
					gdeflen.doClick();
					return;
				}
				//store values
				if(glow != 0){
					closedstruct.setMinGlobalLength(glow);
				}
				else{
					closedstruct.removeGlobalMin();
				}
				if(gup != 0){
					closedstruct.setMaxGlobalLength(gup);
				}
				else{
					closedstruct.removeGlobalMax();
				}
			}
			cs.setSelected(false);
			ds.unselectShape();
			ds.repaint();
			dispose();
		}
		//same as above, but don't close window!
		if(src == apply){
			if(gdeflen.isSelected()){
				closedstruct.removeGlobalLength();
			}
			else if(grange.isSelected()){
				int glow=0;
				int gup=0;
				try{
					if(glowerend.getText() != null && !(glowerend.getText()).equals("")){
						glow = Integer.parseInt(glowerend.getText());	
						if(glow<HairpinLoop.MIN){
							throw new NumberFormatException("The substructure of this closedstruct must contain at least\na HairpinLoop with "+(HairpinLoop.MIN)+" bases.");
						}
					}
					if(gupperend.getText() != null && !(gupperend.getText()).equals("")){
						gup = Integer.parseInt(gupperend.getText());
						if(gup<(HairpinLoop.MIN)){
							throw new NumberFormatException("The substructure of this closedstruct must contain at least\na HairpinLoop with "+(HairpinLoop.MIN)+" bases.");
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
					closedstruct.setMinGlobalLength(glow);
				}
				else{
					closedstruct.removeGlobalMin();
				}
				if(gup != 0){
					closedstruct.setMaxGlobalLength(gup);
				}
				else{
					closedstruct.removeGlobalMax();
				}
			}
			apply.setEnabled(false);
			tabbedPane.setEnabledAt(0,true);
			tabbedPane.setEnabledAt(1,true);
			ds.repaint();
		}
		//no changes stored, close window
		if(src == cancel){
			//no changes
			cs.setSelected(false);
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
		if(src == glowerend){
			if(!glowerendbuf.equals(glowerend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
			}
			else if(gupperendbuf.equals(gupperend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
			}
		}
		if(src == gupperend){
			if(!gupperendbuf.equals(gupperend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
				tabbedPane.setEnabledAt(0,false);
			}
			else if(glowerendbuf.equals(glowerend.getText())){
				apply.setEnabled(false);
				tabbedPane.setEnabledAt(0,true);
			}
		}
	}
	
	public void dispose(){
	    cs.editClosed();
	    super.dispose();
	}
} 







