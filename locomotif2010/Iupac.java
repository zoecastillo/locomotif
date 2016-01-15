package rnaeditor;



/**
 * 
 * @author Janina Reeder
 *
 * This class defines the Iupac rules for String representation of RNA secondary
 * structures.
 */
public class Iupac{
	
	/**
	 * The array contains all allowed characters used in IUPAC code
	 */
    private static final char[] iupaccodes = {'A','C','G','U','R','Y','M','K','S','W','H','B','V','D','N','X'}; //X = N oder keine base
	private static final char[] nucleicacids = {'a','A','c','C','g','G','u','U','N','n','t','T'};
    
	/**
	 * This method checks wether the given String is correct according to 
	 * the IUPAC rules, i.e. contains only IUPAC characters and starts with
	 * an opening "[" bracket and ends with a closing "]" bracket.
	 * 
	 * @param motif, the String to be analyzed
	 * @return true, if correct; else false.
	 */
	public static boolean isCorrectMotif(String motif){
		motif = motif.toUpperCase();
		motif = motif.replace('T','U');
		String iupacregexpr = "[";
		iupacregexpr = iupacregexpr.concat(new String(iupaccodes));
		iupacregexpr = iupacregexpr.concat("]*");
		return motif.matches(iupacregexpr);
	}
	
	/**
	 * This method checks whether the given basepair conforms to the rules for basepair formation
	 * 
	 * @param bptext, String holding a basepair
	 * @return false, if length not equals to 2; else true. NO CHECK YET!!!
	 */
	public static boolean isCorrectBasepair(String bptext){
		if(bptext.length() != 2){
			return false;
		}
		return true;
	}
	
	/**
	 * This method checks wether the given String contains a correct Basepair
	 * sequence. Thus, it only contains IUPAC characters or "-" indicating no
	 * specific basepair at the location, is contained within
	 * square brackets and each basepair is separated from another with ";".
	 * 
	 * @param bpseq, the String to be checked
	 * @return true, if correct; else false.
	 */
	public static boolean isCorrectBPSeq(String bpseq){
		if(bpseq.length() == 0){
			return true;
		}
		String iupacregexpr = "[";
		iupacregexpr = iupacregexpr.concat(new String(iupaccodes));
		iupacregexpr = iupacregexpr.concat("]*");
		bpseq = bpseq.toUpperCase();
		bpseq = bpseq.replace('T','U');
		String[] bpsarray = bpseq.split(";");
		for(int i=0;i<bpsarray.length;i++){
			if(!((bpsarray[i].length() == 2 && bpsarray[i].matches(iupacregexpr)) || (bpsarray[i].length() == 1 && bpsarray[i].matches("-")))){
				return false;
			}
		}
		return true;
	}

	/**
	 * This method parses a target sequence. All non iupac characters are removed, t is replaced by u and all upper case letters are turned to lower case.
	 * 
	 * @param sequence, the target sequence
	 * @return, an updated target sequence containing only those letters contained in RNA sequences
	 */
	public static String parseSequence(String sequence){
		String retsequence = "";
		String nacregexpr = "[";
		nacregexpr = nacregexpr.concat(new String(nucleicacids));
		nacregexpr = nacregexpr.concat("]*");
		String notnac = "[^";
		notnac = notnac.concat(new String(nucleicacids));
		notnac = notnac.concat("]*");
		String[] seqs = sequence.split("\n");
		//FASTA
		if(sequence.startsWith(">")){
			retsequence += seqs[0];
			retsequence += "\n";
			for(int i=1;i<seqs.length;i++){
				String line = seqs[i];
				if(!line.equals("")){
					if(line.startsWith(">")){
						if(i+1<seqs.length){
							String nextline = seqs[++i];
							nextline = nextline.replaceAll(notnac,"");
							nextline = nextline.toLowerCase();
							nextline = nextline.replaceAll("t","u");
							retsequence += "\n";
							retsequence += line;
							retsequence += "\n";
							retsequence += nextline;
						}
						else{
							return ("FASTA header without body: "+line);
						}
					}
					else{
						line = line.replaceAll(notnac,"");
					    line = line.toLowerCase();
					    line = line.replaceAll("t","u");
						retsequence += line;
					}
				}
				else{
					retsequence += "\n";
				}
			}
		}
		else{
		    retsequence += "> input sequence\n";
			sequence = sequence.replaceAll("[ \t\n\r\f]","");
			sequence = sequence.replaceAll(notnac,"");
			sequence = sequence.toLowerCase();
			sequence = sequence.replaceAll("t","u");
			retsequence += sequence;
		}
		return retsequence;
	}
	
}
