package rnaeditor;
/*
 * Created on 12.11.2005
 *
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;


/**
 * @author Janina
 *
 */
public class TranslatePresenter extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = -2363731830879624005L;
	
	private JPanel mainpanel, buttonpanel;
	private JScrollPane scrollpane;
	private JButton okbutton, savebutton;
	private JTextArea textarea;
	private JPanel pane;
		
	private File outputfile;
	private String geruest;
	private EditorGui eg;
	
	final JFileChooser fc = new JFileChooser();
	
	
	
	/**
	 * Constructor
	 */
	public TranslatePresenter(String title, String content, EditorGui eg){
		//ADPC GERUEST
		geruest = "algebratype{\n\ntype FS_Algebra base comp = (\n\t\tbase -> comp -> comp ,  -- sadd\n\t\tcomp -> comp -> comp ,  -- cadd\n\t\tcomp -> comp -> comp ,  -- mlcons\n\t\tbase -> comp   -> base -> comp, --dlr\n\t\tbase -> comp   -> base -> comp, --sr\n\t\tbase -> (Int,Int) -> base -> comp, --hl\n\t\tbase -> (Int,Int) -> comp -> base -> comp, --bl\n\t\tbase -> comp -> (Int,Int) -> base -> comp, --br\n\t\tbase -> (Int,Int) -> comp -> (Int,Int) -> base -> comp, --il\n\t\tbase -> comp -> base -> comp, --ml\n\t\tcomp -> comp -> comp,     -- append\n\t\tcomp -> comp,	       -- ul\n\t\tcomp -> (Int,Int) -> comp,   -- addss\n\t\t(Int,Int) -> comp -> comp,   -- ssadd\n\t\t(Int,Int) -> comp ,	       -- ss\n\t\tbase -> comp,  --nil\n\t\t[comp] -> [comp]  --h\n\t\t) \n}\n\n\n";
		geruest += "algebra[mfe]{\n\n mfe :: FS_Algebra Int Int ;\n mfe = (sadd, cadd, mlcons, dlr, sr, hl, bl, br, il,ml,append, ul, addss, ssadd, ss, nil, h) where\n\tsadd  lb e = e; \n\tcadd  e1 e = e1 + e;\n\tmlcons e1 e = e1 + e + 40;\n\tdlr dl    e    dr  = e + dl_energy (dl+1,dr) + dr_energy (dl+1,dr) + termaupenalty (dl+1,dr);\n\tsr  lb        e    rb  = e + sr_energy  (lb,rb);\n\thl  lb        _    rb  =     hl_energy  (lb,rb) ;\n\tbl  lb  (i,j) e    rb  = e + bl_energy  (lb,i,j, rb);\n\tbr  lb        e (i,j)  rb  = e + br_energy (lb,i,j, rb);\n\til  lb (i,j) e (k,l) rb= e + il_energy (i,j,k,l) ;\n\tml  lb       e     rb  = 380 + e + termaupenalty (lb,rb) + dli_energy (lb,rb) + dri_energy (lb,rb);\n\tappend  e1 e   = e1 + e;\n\taddss    e (i,j) = 40 + e + ss_energy (i,j);\n\tul       e   = 40 + e;\n\tssadd (i,j)  e   = e + ss_energy (i,j);\n\tss    (i,j)	     = ss_energy (i,j);\n\tnil _ = 0;\n\th   es = [minimum es];\n}\n\n\n";
		geruest += "algebra[pp]{\n\n    pp :: FS_Algebra Int String ;\n\n    pp =  (sadd, cadd, mlcons, dlr, sr, hl, bl, br, il,ml,append, ul, addss, ssadd, ss, nil, h) where\n\tsadd  lb  e = \".\" ++ e;\n\tcadd   x  e =  x  ++ e;\n\tmlcons x  e =  x  ++ e;\n\tdlr  _    x    _   =              x;\n\tsr  lb    x    rb  = \"(\"  ++      x ++ \")\";	\n\thl  lb    (i,j)    rb  = \"(\" ++ dots (i,j) ++\")\";	\n\tbl  bl (i,j)   e  br  = \"(\" ++ dots (i,j) ++ e ++\")\";\n\tbr  bl  e (i,j)    br  = \"(\" ++ e ++ dots (i,j) ++\")\";\n\til  lb (i,j) x (k,l) rb  = \"(\" ++ dots (i,j) ++ x ++ dots (k,l) ++ \")\";\n\tml  bl    x    br  = \"(\" ++ x ++ \")\" ;\n\tappend  c1 c = c1 ++ c;\n\tul  c1 = c1;\n\taddss  c1 (i,j)   = c1 ++ dots (i,j) ;\n\tssadd     (i,j) x =       dots (i,j) ++ x;\n\tss        (i,j)	  =       dots (i,j) ;	\n\tnil _ = \"\";\n\th es = [id es];\n}\n\n\n";
		geruest += "#Grammar ex4 rnastruct (Int,Int) (sadd, cadd, mlcons, dlr, sr, hl, bl, br, il,ml,append, ul, addss, ssadd, ss, nil, h);\ngrammar[rnastruct]{\n\n";

		//GHC GERUEST
		//geruest = "> module Main where\n\n> import Array\n> import System(getArgs)\n> import Foldingspace\n> import ADPTriCombinators\n> import RNAAddCombinators\n> import Algebras_editor\n\n";
		//geruest += "> main ::  IO()\n> main  = do\n>	  [arg1,arg2] <- getArgs\n>	  let input	 = arg1\n>	      result     = case  arg2 of\n>			         \"1\"  ->  foldr (++)[] (map format (match input (energyalg *** prettyprintalg)))\n>				 \"3\"  ->  show (match input (basepairalg *** prettyprintalg))\n>				 \"5\"  ->  show (match input (energyalg))\n\n\n>				 otherwise -> error \"select 1..7\\n\" in\n\n>	      putStr (result)\n\n> format:: (Int, String) -> String\n> format (e,p) = show e ++\"\\t\"++ p++\"\\n\"\n";
		//geruest += "> match :: [Char] -> (RNAInput -> FS_Algebra Int a a) -> [a]\n> match sequence  algebra = axiom rnastruct where\n\n>   tabulated		= table  n\n>   listed		= table1 n\n>   n			= length sequence\n>   axiom		= axiom' n\n>   inp			= mk (rna sequence)\n>   basepairing (i,j)   = basepair'  (inp,(i,j))\n>   stackpairing (i,j)  = stackpair' (inp,(i,j))\n>   region_contains s (i,j)= region_contains' s (inp,(i,j))\n>   iupac_base x (i,j)	= iupac_base' x (inp,(i,j))\n\n";
		//geruest += "\n\n>   (sadd, cadd, mlcons, dlr, sr, hl, bl, br, il,ml,\n>      append, ul, addss, ssadd, ss, nil, h, h_l, h_s)  = algebra inp\n\n>   infixl 7 ~~!,!~~\n>   (~~!) = (~~<) 30\n>   (!~~) = (<~~) 32 \n\n";

		this.eg = eg;
		
		//nice window decorations
		setDefaultLookAndFeelDecorated(true);
		
		setTitle(title);
		setSize(new Dimension(550,400));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
		setContentPane(this.makeContentPane(content));
		pack();
		setVisible(true);
	}
	
	
	/**
	 * This method builds the content pane of the program
	 *
	 * @return the contentpane as a Container
	 */
	public Container makeContentPane(String content){
		
		pane = new JPanel();
		pane.setLayout(new BorderLayout());
		
		//contains the Output content
		mainpanel = new JPanel();
		mainpanel.setPreferredSize(new Dimension(500,300));
		mainpanel.setLayout(new GridLayout(1,1));
		
		//contains the ok and save buttons
		buttonpanel = new JPanel();
		
		//putting contents into the textarea
		textarea = new JTextArea(content);
		textarea.setEnabled(true);
		textarea.setEditable(false);
		scrollpane = new JScrollPane(textarea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollpane.setPreferredSize(new Dimension(500,300));
		scrollpane.setViewportBorder(BorderFactory.createLineBorder(Color.black));
		scrollpane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		mainpanel.add(scrollpane);
		pane.add(mainpanel, BorderLayout.CENTER);
		
		//setting up the panel for the buttons
		buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.LINE_AXIS));
		buttonpanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),BorderFactory.createEmptyBorder(5,5,5,5)));
		this.setUpButtonPanel(buttonpanel);
		pane.add(buttonpanel, BorderLayout.PAGE_END);
		
		//initializing the FileChooser
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);	
		fc.setFileFilter(eg.getADPFilter());
		fc.setAcceptAllFileFilterUsed(false);

		return pane;
	}

	/**
	 * Removes any input form the text area
	 *
	 */
	public void clear(){
		textarea.setText(null);
	}
	
	/**
	 * This method sets up the shape field for presenting the online
	 * translation into shape notation
	 *
	 * @param shapepanel the panel holding the shape notation
	 */
	public void setUpButtonPanel(JPanel buttonpanel){
		okbutton = new JButton("OK");
		savebutton = new JButton("Save");
		okbutton.setEnabled(true);
		okbutton.setSelected(true);
		okbutton.addActionListener(this);
		savebutton.setEnabled(true);
		savebutton.addActionListener(this);
		buttonpanel.add(Box.createHorizontalGlue());
		buttonpanel.add(okbutton);
		buttonpanel.add(savebutton);
	}
	

	
	/**
	 * ActionListener
	 */
	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();
		if(src == okbutton){
			dispose();
		}
		else if(src == savebutton){
			try{
				EditorIO.saveOutputFile(this,outputfile,fc,geruest,textarea.getText());
				dispose();
			}
			catch(IOException ioe){
				JOptionPane.showMessageDialog(null,"ADP code could not be saved:\n"+ioe.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	
	
}
