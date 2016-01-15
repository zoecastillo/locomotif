package rnaeditor;
/*
 * Created on 12.11.2005
 *
 */


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;


/**
 * @author Janina
 *
 */
public class ProgressFrame extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = -2363731830879624005L;
	
	private JPanel mainpanel,buttonpanel;
	private JPanel pane;
	private JLabel pblabel, pblabel2;
	private JProgressBar pb;
	private JButton cancelbutton;
	private EditorGui eg;
	
	
	
	/**
	 * Constructor
	 */
	public ProgressFrame(EditorGui eg, String title, String content, String content2, boolean showcancel){
		
		this.eg = eg;
		
		//nice window decorations
		setDefaultLookAndFeelDecorated(true);
		
		setTitle(title);
		setSize(new Dimension(250,150));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
		setContentPane(this.makeContentPane(content,content2,showcancel));
		setLocation(300,200);
		pack();
		setVisible(true);
	}
	
	
	/**
	 * This method builds the content pane of the program
	 *
	 * @return the contentpane as a Container
	 */
	public Container makeContentPane(String content,String content2,boolean showcancel){
		
		pane = new JPanel();
		pane.setLayout(new BorderLayout());
		
		//contains the Output content
		mainpanel = new JPanel();
		mainpanel.setPreferredSize(new Dimension(250,150));
		mainpanel.setLayout(new BoxLayout(mainpanel,BoxLayout.PAGE_AXIS));
		
		pblabel = new JLabel(content);
		pblabel2 = new JLabel(content2);
		


		pb = new JProgressBar();
		pb.setIndeterminate(true);		
		pb.setPreferredSize(new Dimension(150,20));
		pb.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		cancelbutton = new JButton("Close");
		cancelbutton.addActionListener(this);
		buttonpanel = new JPanel();
		buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.LINE_AXIS));
		buttonpanel.add(Box.createRigidArea(new Dimension(100,0)));
		buttonpanel.add(cancelbutton);
		buttonpanel.add(Box.createHorizontalGlue());
		
		mainpanel.add(Box.createRigidArea(new Dimension(0,10)));
		mainpanel.add(pblabel);
		mainpanel.add(pblabel2);
		mainpanel.add(Box.createRigidArea(new Dimension(0,10)));
		mainpanel.add(pb);
		if(showcancel){
			mainpanel.add(Box.createRigidArea(new Dimension(0,10)));
			mainpanel.add(buttonpanel);
		}
		pane.add(mainpanel, BorderLayout.CENTER);
		
		return pane;
	}
	
	/**
	 * ActionListener
	 */
	public void actionPerformed(ActionEvent event) {
        eg.interruptMatcher();
        dispose();
	}
	
	
}
