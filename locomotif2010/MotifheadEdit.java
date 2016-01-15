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
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

/**
 * This class defines the user interface for editing a ClosedStruct element
 *
 * @author Janina Reeder
 */
public class MotifheadEdit extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 7062603644738528528L;
	private JPanel pane;
	private JRadioButton global, local;
	private JButton ok, cancel;
	private JLabel l1,l2,l3,l4,l5;
	private JTextField t1,t2,t3;
	private EditorGui eg;
	
	/**
	 * Constructor for the ClosedStructEdit GUI
	 *
	 * @param cs the parent ClosedStructShape
	 * @param ds the parent DrawingSurface
	 * @param type determines which tab is shown at first
	 */
	public MotifheadEdit(EditorGui eg){
		super("Search Parameters");
		
		this.eg = eg;
		
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
	public Container makeContentPane(){
		pane = new JPanel();
		//pane.setSize(new Dimension(300,200));
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
				
		ButtonGroup bg = new ButtonGroup();
		
		JPanel namepanel = new JPanel();
		namepanel.setLayout(new BoxLayout(namepanel, BoxLayout.LINE_AXIS));
		l1 = new JLabel("Project name: ");
		l1.setToolTipText("Specify a project name of your choice");
		l1.setAlignmentX(Component.LEFT_ALIGNMENT);
		namepanel.add(l1);
		t1 = new JTextField(25);
		if(eg.getStructName() != null){
			t1.setText(eg.getStructName());
		}
		namepanel.add(t1);
		
		JPanel searchpanelframe = new JPanel();
		JPanel searchpanel = new JPanel();
		searchpanel.setLayout(new BoxLayout(searchpanel, BoxLayout.PAGE_AXIS));
		searchpanelframe.setLayout(new BoxLayout(searchpanelframe, BoxLayout.LINE_AXIS));
		l2 = new JLabel("Search type:");
		l2.setAlignmentX(Component.LEFT_ALIGNMENT);
		searchpanel.add(l2);
		local = new JRadioButton("Local");
		local.setToolTipText("The motif is searched somewhere within a longer sequence");
		local.setAlignmentX(Component.LEFT_ALIGNMENT);
		local.addActionListener(this);
		global = new JRadioButton("Global");
		global.setToolTipText("The total given sequence is folded according to the motif definition");
		global.setAlignmentX(Component.LEFT_ALIGNMENT);
		global.addActionListener(this);
		if(eg.getSearchType()){
			local.setSelected(true);
		}
		else{
			global.setSelected(true);
		}
		searchpanel.add(local);
		searchpanel.add(global);
		searchpanelframe.add(searchpanel);
		searchpanelframe.add(Box.createHorizontalGlue());
		
		bg.add(global);
		bg.add(local);
		
		JPanel sizepanelframe = new JPanel();
		JPanel sizepanel = new JPanel();
		JPanel minsizepanel = new JPanel();
		JPanel maxsizepanel = new JPanel();
		sizepanelframe.setLayout(new BoxLayout(sizepanelframe, BoxLayout.PAGE_AXIS));
		sizepanel.setLayout(new BoxLayout(sizepanel, BoxLayout.LINE_AXIS));
		minsizepanel.setLayout(new BoxLayout(minsizepanel, BoxLayout.LINE_AXIS));
		maxsizepanel.setLayout(new BoxLayout(maxsizepanel, BoxLayout.LINE_AXIS));
		
		l3 = new JLabel("Motif Size: ");
		l3.setToolTipText("Specify a size restriction for the entire motif");
		l3.setAlignmentX(Component.LEFT_ALIGNMENT);
		sizepanel.add(l3);
		sizepanel.add(Box.createHorizontalGlue());
		sizepanelframe.add(sizepanel);
		l4 = new JLabel("Minimum size:  ");
		l4.setToolTipText("Ensure a minimum size for the overall motif");
		l4.setAlignmentX(Component.LEFT_ALIGNMENT);
		minsizepanel.add(l4);
		t2 = new JTextField(3);
		t2.setMaximumSize(t2.getPreferredSize());
		int min = eg.getGlobalMin();
		if(min > 0){
			t2.setText(String.valueOf(min));
		}
		minsizepanel.add(t2);
		minsizepanel.add(Box.createHorizontalGlue());
		sizepanelframe.add(Box.createRigidArea(new Dimension(0,1)));
		sizepanelframe.add(minsizepanel);
		l5 = new JLabel("Maximum size: ");
		l5.setToolTipText("Restricting the maximum size is highly recommended for efficient matcher generation");
		l5.setAlignmentX(Component.LEFT_ALIGNMENT);
		maxsizepanel.add(l5);
		t3 = new JTextField(3);
		t3.setMaximumSize(t3.getPreferredSize());
		int max = eg.getGlobalMax();
		if(max > 0){
			t3.setText(String.valueOf(max));
		}
		maxsizepanel.add(t3);
		maxsizepanel.add(Box.createHorizontalGlue());
		sizepanelframe.add(Box.createRigidArea(new Dimension(0,1)));
		sizepanelframe.add(maxsizepanel);
		if(global.isSelected()){
			enableSize(false);
		}
		
		JPanel buttonpanel = new JPanel();
		buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.LINE_AXIS));
		ok = new JButton("OK");
		cancel = new JButton("Cancel");
		ok.setEnabled(true);
		ok.setSelected(true);
		ok.addActionListener(this);
		cancel.setEnabled(true);
		cancel.addActionListener(this);
		buttonpanel.add(Box.createHorizontalGlue());
		buttonpanel.add(ok);
		buttonpanel.add(cancel);
		
		
		namepanel.setAlignmentX(Component.LEFT_ALIGNMENT);		
		namepanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),BorderFactory.createEmptyBorder(5,5,5,5)));
		searchpanelframe.setAlignmentX(Component.LEFT_ALIGNMENT);	
		searchpanelframe.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),BorderFactory.createEmptyBorder(5,5,5,5)));
		sizepanelframe.setAlignmentX(Component.LEFT_ALIGNMENT);	
		sizepanelframe.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),BorderFactory.createEmptyBorder(5,5,5,5)));
		buttonpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		buttonpanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),BorderFactory.createEmptyBorder(5,5,5,5)));
		pane.add(namepanel);
		pane.add(Box.createRigidArea(new Dimension(0,5)));
		pane.add(searchpanelframe);
		pane.add(Box.createRigidArea(new Dimension(0,5)));
		pane.add(sizepanelframe);
		pane.add(Box.createRigidArea(new Dimension(0,10)));
		pane.add(buttonpanel);
				
		return pane;
	} 
	
	/**
	 * Method to enable the size interface: disabled for global search!
	 * 
	 * @param enable, true, if enabled; else false
	 */
	public void enableSize(boolean enable){
		if(enable){
			l3.setEnabled(true);
			l4.setEnabled(true);
			l5.setEnabled(true);
			t2.setEnabled(true);
			t3.setEnabled(true);
		}
		else{
			l3.setEnabled(false);
			l4.setEnabled(false);
			l5.setEnabled(false);
			t2.setEnabled(false);
			t3.setEnabled(false);
		}
	}
	
	/**
	 * Button actionlistener
	 *
	 * @param ae the ActionEvent which ocurred
	 */
	public void actionPerformed(ActionEvent ae){
		Object src = ae.getSource();
		int gmin = 0, gmax = 0;
		if(src == local){
			enableSize(true);
		}
		if(src == global){
			enableSize(false);
		}
		if(src == ok){
			if(local.isSelected()){
				eg.setSearchType(true);
			}
			else{
				eg.setSearchType(false);
			}
			if(t2.getText() != null && !(t2.getText()).equals("")){
				try{
					gmin = Integer.parseInt(t2.getText());
					if(gmin < 0){
						throw new NumberFormatException();
					}
				}
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null,"You can only use nonnegative number values for the minimum size","Wrong input format",JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			if(t3.getText() != null && !(t3.getText()).equals("")){
				try{
					gmax = Integer.parseInt(t3.getText());
					if(gmax < 1){
						throw new NumberFormatException();
					}
				}
				catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null,"You can only use positive number values for the maximum size","Wrong input format",JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			if(t1.getText() != null && !(t1.getText()).equals("")){
				eg.setStructName(t1.getText());
			}
			else{
				JOptionPane.showMessageDialog(null,"You must specify a name for your project!","Missing project name",JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(local.isSelected()){
				eg.storeGlobalSize(gmin,gmax);
			}
			else{
				eg.storeGlobalSize(0,0);
			}
			dispose();
		}
		if(src == cancel){
			//no changes
			dispose();
		}
	}
	
	public void dispose(){
	    eg.headClosed();
	    super.dispose();
	}
} 







