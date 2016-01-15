package rnaeditor;
/*
 * Created on 12.11.2005
 *
 */


import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import de.unibi.bibiserv.rnamovies.RNAMovies;


/**
 * @author Janina
 *
 */
public class MoviesFrame extends JFrame{
	
    private static final long serialVersionUID = 883981987387913900L;
    private String content;
    
	/**
	 * Constructor
	 */
	public MoviesFrame(String content){
		//nice window decorations
		setDefaultLookAndFeelDecorated(true);
		
		setTitle("RNAMovies: Locomotif Results");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
		this.content = prepareContent(content);
		if(this.content == null) return;
		RNAMovies movie = new RNAMovies();
		movie.setData(this.content);
		setContentPane(movie);
		pack();
		setVisible(true);
		
	}
	
	public String prepareContent(String content){
	    String cont = ">";
	    String seq = "";
	    Vector<String> allinputs, fastaheader, structstrings;
	    String dotbracket;
	    //get each fasta-input
	    StringTokenizer st = new StringTokenizer(content,">");
	    if(st.hasMoreTokens()){
	        st.nextToken(); //skip intro text before first
	    }
	    //more than one input file
	    if(st.countTokens() > 1){
	        fastaheader = new Vector<String>();
	        allinputs = new Vector<String>();
	        structstrings = new Vector<String>();
	        while(st.hasMoreTokens()){
	            cont = ">";
	            //get one fasta input
	            cont += st.nextToken(); //fasta header, sequences and dotbracket + next intro
	            //get each line of one fasta input
	            StringTokenizer st2 = new StringTokenizer(cont,"\n");
	            if(st2.hasMoreTokens()){
	                cont = st2.nextToken(); // fasta header
	                fastaheader.add(cont);
	            }
	            if(st2.hasMoreTokens()){
	                seq = st2.nextToken(); //sequence
	            }
	            int seqlen = seq.length();
	            int globali = seqlen;
	            int globalj = 0;
	            boolean structfound = false;
	            //add all structure strings
	            while(st2.hasMoreTokens()){
	                dotbracket = st2.nextToken();
	                if(dotbracket != null && (dotbracket.startsWith(".") || dotbracket.startsWith("("))){
	                    StringTokenizer st3 = new StringTokenizer(dotbracket," ");
	                    String struct = st3.nextToken();
	                    structfound = true;
	                    structstrings.add(struct);
	                }
	            }
	            if(!structfound){
	                fastaheader.remove(fastaheader.lastElement());
	            }
	            else{
	                for(String s : structstrings){
	                    int i = s.indexOf("(");
	                    int j = s.lastIndexOf(")");
	                    if(i < globali) globali = i;
	                    if(j > globalj) globalj = j;
	                }
	                int ibuf = 0;
	                if(globali > 15){
	                    seq = seq.substring(globali-10);
	                    ibuf = globali-10;
	                }
	                if(globalj < seqlen - 15){
	                    seq = seq.substring(0,globalj+10-ibuf);
	                }
	                cont += "\n";
	                cont += seq;
	                for(String s : structstrings){
	                    cont += "\n";
	                    if(globali > 15){
	                        s = s.substring(globali-10);
	                    }
	                    if(globalj < seqlen - 15){
	                        s = s.substring(0,globalj+10-ibuf);
	                    }
	                    cont += s;
	                }
	                allinputs.add(cont);
	                structstrings = new Vector<String>();
	            }
	        }
	        String s = (String) JOptionPane.showInputDialog(null, "Choose target sequence to be displayed\n       with RNAMovies","Result Visualization", JOptionPane.QUESTION_MESSAGE,null,fastaheader.toArray(),fastaheader.firstElement());
	        int j = fastaheader.indexOf(s);
	        cont = allinputs.elementAt(j);
	    }
	    else{
	        structstrings = new Vector<String>();
	        if(st.hasMoreTokens()){
	            cont += st.nextToken(); //get fasta header, sequences and dotbracket
	        }
	        StringTokenizer st2 = new StringTokenizer(cont,"\n");
	        if(st2.hasMoreTokens()){
	            cont = st2.nextToken(); // fasta header
	        }
	        if(st2.hasMoreTokens()){
	            seq = st2.nextToken(); // sequence
	        }
	        boolean structfound = false;
	        int seqlen = seq.length();
	        int globali = seqlen;
	        int globalj = 0;
	        while(st2.hasMoreTokens()){
	            dotbracket = st2.nextToken();
	            if(dotbracket != null && (dotbracket.startsWith(".") || dotbracket.startsWith("("))){
	                StringTokenizer st3 = new StringTokenizer(dotbracket," ");
	                String struct = st3.nextToken();
	                structstrings.add(struct);
	                structfound = true;
	            }
	        }
	        if(!structfound){
	            return null;
	        }
            else{
                for(String s : structstrings){
                    int i = s.indexOf("(");
                    int j = s.lastIndexOf(")");
                    if(i < globali) globali = i;
                    if(j > globalj) globalj = j;
                }
                int ibuf = 0;
                if(globali > 15){
                    seq = seq.substring(globali-10);
                    ibuf = globali-10;
                }
                if(globalj < seqlen - 15){
                    seq = seq.substring(0,globalj+10-ibuf);
                }
                cont += "\n";
                cont += seq;
                for(String s : structstrings){
                    cont += "\n";
                    if(globali > 15){
                        s = s.substring(globali-10);
                    }
                    if(globalj < seqlen - 15){
                        s = s.substring(0,globalj+10-ibuf);
                    }
                    cont += s;
                }
            }
	    }
	    return cont;
	}
	
}
