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
public class ClosedMultiEdit extends JFrame implements ActionListener, CaretListener{
	
	private static final long serialVersionUID = -7878908122784053681L;
	private ClosedMultiEnd multiend;
	private ClosedMultiShape ms;
	private JPanel pane;
	private JTabbedPane tabbedPane;
	private JRadioButton deflen, exact, range;
	private JButton ok, apply, cancel;
	private JLabel l1,l2,l3;
	private JTextField t1, lowerend, upperend;
	private String t1buf, lowerendbuf, upperendbuf;
	private DrawingSurface ds;
	
	/**
	 * Constructor for the multiendEdit GUI
	 *
	 * @param ms the parent multiendShape
	 * @param ds the parent DrawingSurface
	 * @param type determines which tab is shown at first
	 */
	public ClosedMultiEdit(ClosedMultiShape ms, DrawingSurface ds, int type){
		super("multiend Options");
		
		this.ms = ms;
		this.multiend = ms.getMultiEnd();
		this.ds = ds;
		
		JFrame.setDefaultLookAndFeelDecorated(true);
		//setSize(new Dimension(300,200));
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
		
		tabbedPane = new JTabbedPane();
		
		//SIZE PANEL
		JPanel card1 = new JPanel();
		
		ButtonGroup bg = new ButtonGroup();

		JPanel defaultpanel = new JPanel();
		deflen = new JRadioButton("No size restriction");
		deflen.setToolTipText("Unrestricted ClosedEnd contains at least 1 hairpin loop and thus at least 5 bases");
		
		JPanel exactpanel = new JPanel();
		exact = new JRadioButton("Exact size");
		exact.setToolTipText("Specify an exact number of bases for the ClosedEnd");
		JPanel exactnumpanel = new JPanel();
		l1 = new JLabel("Number of bases in compound block: ");
		l1.setToolTipText("Specify the exact number of bases contained in the building block");

		JPanel rangepanel = new JPanel();
		range = new JRadioButton("Size range");
		range.setToolTipText("Specify a size range for the ClosedEnd");
		l2 = new JLabel("Minimum number of bases:  ");
		l2.setToolTipText("Specify the minimum number of bases contained in the building block");
		l3 = new JLabel("Maximum number of bases: ");
		l3.setToolTipText("Specify the maximum number of bases contained in the building block");
		JPanel rangevaluepanel1 = new JPanel();
		JPanel rangevaluepanel2 = new JPanel();

		if(multiend.getIsDefaultLength()){
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
		else if(multiend.getIsExactLength()){
			t1 = new JTextField(String.valueOf(multiend.getLength()),3);
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
			if(!multiend.getIsDefaultMin()){
				lowerend.setText(String.valueOf(multiend.getMinLength()));
			}
			if(!multiend.getIsDefaultMax()){
				upperend.setText(String.valueOf(multiend.getMaxLength()));
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
		
		tabbedPane.addTab("Size", card1);
		
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
			if(!multiend.getIsDefaultMin() && multiend.getMinLength()>0){
				lowerend.setText(String.valueOf(multiend.getMinLength()));
				lowerendbuf = lowerend.getText();
			}
			lowerend.setEnabled(true);
			l3.setEnabled(true);
			if(!multiend.getIsDefaultMax() && multiend.getMaxLength()>0){
				upperend.setText(String.valueOf(multiend.getMaxLength()));
				upperendbuf = upperend.getText();
			}
			upperend.setEnabled(true);
		}
		if(src == exact){
			l1.setEnabled(true);
			t1.setText(String.valueOf(multiend.getLength()));
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
				multiend.setToDefaultLength();
				ms.changeSize(multiend.getLength());
			}
			//exact size restriction
			if(exact.isSelected()){
				try{
				    //no value given: no size restriction
					if(t1.getText() == null || (t1.getText()).equals("")){
						deflen.doClick();
						return;
					}
					//parse and check size value
					int l = Integer.parseInt(t1.getText());
					if(l<(ClosedMultiEnd.MIN)){
						throw new NumberFormatException("A ClosedMultiEnd must contain at least "+(ClosedMultiEnd.MIN)+" bases.");
					}
					if(l>(ClosedMultiEnd.MAX) && !t1buf.equals(t1.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"ClosedMultiEnds with more than "+(ClosedMultiEnd.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					multiend.setLength(l);
					ms.changeSize(multiend.getLength());
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
						if(low<(ClosedMultiEnd.MIN)){
							throw new NumberFormatException("A ClosedMultiEnd must contain at least "+(ClosedMultiEnd.MIN)+" bases.");
						}
						if(low>(ClosedMultiEnd.MAX) && !lowerendbuf.equals(lowerend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"ClosedMultiEnds with more than "+(ClosedMultiEnd.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
					}
					//parse and check maximum
					if(upperend.getText() != null && !(upperend.getText()).equals("")){
						up = Integer.parseInt(upperend.getText());
						if(up<(ClosedMultiEnd.MIN)){
							throw new NumberFormatException("A ClosedMultiEnd must contain at least "+(ClosedMultiEnd.MIN)+" bases.");
						}
						if(up>(ClosedMultiEnd.MAX) && !upperendbuf.equals(upperend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"ClosedMultiEnd with more than "+(ClosedMultiEnd.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
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
				//no values given: default size
				if(low == 0 && up == 0){
					deflen.doClick();
					return;
				}
				//store size values
				if(low != 0){
					multiend.setMinLength(low);
				}
				else{
					multiend.setToDefaultMin();
				}
				if(up != 0){
					multiend.setMaxLength(up);
				}
				else{
					multiend.setToDefaultMax();
				}
				ms.changeSize(multiend.getMinLength(),multiend.getMaxLength());
			}
			ms.setSelected(false);
			ds.unselectShape();
			ds.repaint();
			dispose();
		}
		//store changes, but keep window open
		if(src == apply){
			//apply changes (same as above, except windows doesn't close 
			//and values are adjusted
		    
		    //no size restriction
			if(deflen.isSelected()){
			    //pane is always enabled at 0: only 1 pane!
				if(tabbedPane.isEnabledAt(0)){
					lowerend.setText(null);
					upperend.setText(null);
					t1.setText(null);
					t1buf = t1.getText();
					lowerendbuf = lowerend.getText();
					upperendbuf = upperend.getText();
					multiend.setToDefaultLength();
					ms.changeSize(multiend.getLength());
				}
			}
			//exact size
			else if(exact.isSelected()){
				try{
					if(t1.getText() == null || (t1.getText()).equals("")){
						deflen.doClick();
						return;
					}
					int l = Integer.parseInt(t1.getText());
					if(l<(ClosedMultiEnd.MIN)){
						throw new NumberFormatException("A ClosedMultiEnd must contain at least "+(ClosedMultiEnd.MIN)+" bases.");
					}
					if(l>(ClosedMultiEnd.MAX) && !t1buf.equals(t1.getText())){
						int retval = JOptionPane.showConfirmDialog(null,"ClosedMultiEnds with more than "+(ClosedMultiEnd.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
						if(retval == JOptionPane.CANCEL_OPTION){
							return;
						}
					}
					if(tabbedPane.isEnabledAt(0)){
						lowerend.setText(null);
						upperend.setText(null);
						t1buf = t1.getText();
						lowerendbuf = lowerend.getText();
						upperendbuf = upperend.getText();
						multiend.setLength(l);
						ms.changeSize(multiend.getLength());
					}
				}
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
				}
			}
			//size range
			else{
				int low=0;
				int up=0;
				try{
					if(lowerend.getText() != null && !(lowerend.getText()).equals("")){
						low = Integer.parseInt(lowerend.getText());	
						if(low<(ClosedMultiEnd.MIN)){
							throw new NumberFormatException("A ClosedMultiEnd must contain at least "+(ClosedMultiEnd.MIN)+" bases.");
						}
						if(low>(ClosedMultiEnd.MAX) && !lowerendbuf.equals(lowerend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"ClosedMultiEnds with more than "+(ClosedMultiEnd.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
							if(retval == JOptionPane.CANCEL_OPTION){
								return;
							}
						}
					}
					if(upperend.getText() != null && !(upperend.getText()).equals("")){
						up = Integer.parseInt(upperend.getText());
						if(up<(ClosedMultiEnd.MIN)){
							throw new NumberFormatException("A ClosedMultiEnd must contain at least "+(ClosedMultiEnd.MIN)+" bases.");
						}
						if(up>(ClosedMultiEnd.MAX) && !upperendbuf.equals(upperend.getText())){
							int retval = JOptionPane.showConfirmDialog(null,"ClosedMultiEnds with more than "+(ClosedMultiEnd.MAX)+" bases are unlikely.\nDo you wish to continue?","Long Loop", JOptionPane.OK_CANCEL_OPTION);
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
				if(tabbedPane.isEnabledAt(0)){
					t1.setText(null);
					t1buf = t1.getText();
					lowerendbuf = lowerend.getText();
					upperendbuf = upperend.getText();
					if(low != 0){
						multiend.setMinLength(low);
					}
					else{
						multiend.setToDefaultMin();
					}
					if(up != 0){
						multiend.setMaxLength(up);
					}
					else{
						multiend.setToDefaultMax();
					}
					ms.changeSize(multiend.getMinLength(),multiend.getMaxLength());
				}
			}
			apply.setEnabled(false);
			tabbedPane.setEnabledAt(0,true);
			ds.repaint();
		}
		//no changes and close window
		if(src == cancel){
			//no changes
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
			}
			else if(lowerendbuf.equals(lowerend.getText()) && upperendbuf.equals(upperend.getText())){
				apply.setEnabled(false);
			}
		}
		if(src == lowerend){
			if(!lowerendbuf.equals(lowerend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
			}
			else if(t1buf.equals(t1.getText()) && upperendbuf.equals(upperend.getText())){
				apply.setEnabled(false);
			}
		}
		if(src == upperend){
			if(!upperendbuf.equals(upperend.getText())){
				apply.setEnabled(true);
				cancel.setEnabled(true);
			}
			else if(lowerendbuf.equals(lowerend.getText()) && t1buf.equals(t1.getText())){
				apply.setEnabled(false);
			}
		}
	}
	
	public void dispose(){
	    ms.editClosed();
	    super.dispose();
	}
} 
