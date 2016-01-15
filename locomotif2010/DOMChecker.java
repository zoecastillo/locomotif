package rnaeditor;
/*
 * Created on 15.03.2007
 *
 */


import java.util.Iterator;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * @author Janina
 *
 *	This class checks size restrictions within the DOM document translated from a 
 *	graphical motif definition. Global size restrictions are transported to each building block within
 *	the relevant substructure to produce more efficient matcher programs in the compilation phase.
 */
public class DOMChecker {

	private static int MAX = 10000000;
	private static int cum_min = 0;
	private static int cum_max = 0;
	private static int minbuf = 0;
	private static int maxbuf = 0;
	private static int multicount = 0;
	private static int localmin = 0;
	private static int localmax = 0;
	//must be changed for bibiserv project
	public static Namespace ns = Namespace.getNamespace("de:unibi:techfak:bibiserv:rnaeditor");
	
	
	/**
	 * Main method for parsing the XML document and checking all global/local size restrictions
	 * within for potential conflicts. Also, any global maxima are passed to the children.
	 * 
	 * @throws SizeConflictException, if a conflicting defintion of gobal/global or global/local sizes occurs
	 */
	public static void checkSizes() throws SizeConflictException{
		if(Translator.doc == null){
			throw new SizeConflictException("Empty DOM document");
		}
		Iterator<?> itr = (Translator.rootelement.getChildren()).iterator();
		String globalmin = Translator.rootelement.getAttributeValue("gminsize");
		String globalmax = Translator.rootelement.getAttributeValue("gmaxsize");
		
		int totalmin = -1;
		int totalmax = -1;
		cum_min = 0;
		cum_max = 0;
		minbuf = 0;
		maxbuf = 0;
		if(globalmin != null){
			totalmin = Integer.parseInt(globalmin);
		}
		if(globalmax != null){
			totalmax = Integer.parseInt(globalmax);
		}
		
		if(globalmin != null || globalmax != null){
			while(itr.hasNext()){
				Element motifpart = (Element) itr.next();
				if((motifpart.getName()).equals("neighbor")){//motif part
					Iterator<?> itr2 = (motifpart.getChildren()).iterator();
					motifpart = (Element)itr2.next();
					getAndCheckSize(motifpart, totalmin, totalmax);
				}
				else{//SingleStrand
					String sslen = motifpart.getAttributeValue("length");
					String ssmin = motifpart.getAttributeValue("minlength");
					String ssmax = motifpart.getAttributeValue("maxlength");
					if(sslen != null){
						//Check and Add to global restrictions
						cum_min += Integer.parseInt(sslen);
						cum_max += Integer.parseInt(sslen);
					}
					else if(ssmin != null || ssmax != null){
						if(ssmin != null){
							//CHekc and Add to global restrictions
							cum_min += Integer.parseInt(ssmin);
						}
						if(ssmax != null){
							//Check and Add to global restrictions
							cum_max += Integer.parseInt(ssmax);
						}
						else{
							cum_max = MAX;
						}
					}
					else{
						if(globalmax != null){
							motifpart.setAttribute("maxlength",globalmax);
						}
						cum_min += 0;
						cum_max = MAX;
					}
				}
				if(totalmax != -1){
					if(cum_min > totalmax){
						throw new SizeConflictException("Conflicting size restrictions at "+motifpart.getName()+": implicit minimum size larger than global max");
					}
				}
			}
			if(totalmin != -1){
				if(cum_max < totalmin){
					throw new SizeConflictException("Conflicting size restrictions: implicit maximum size smaller than global min");
				}
			}
		}
		else{
			while(itr.hasNext()){
				Element motifpart = (Element)itr.next();
				if((motifpart.getName()).equals("neighbor")){
					Iterator<?> itr2 = (motifpart.getChildren()).iterator();
					Element firstblock = (Element)itr2.next();
					getAndCheckSize(firstblock, -1, -1);
				}
			}
		}
	}

	public static int getAndCheckSize(Element bblock, int min, int max) throws SizeConflictException{
		String lmin = bblock.getAttributeValue("minlength");
		String lmax = bblock.getAttributeValue("maxlength");
		String exact = bblock.getAttributeValue("length");
		String gmin = bblock.getAttributeValue("gminsize");
		String gmax = bblock.getAttributeValue("gmaxsize");
		localmin = 0;
		localmax = 0;
		int globalmin = 0, globalmax = 0;
		
		
		if((bblock.getName()).equals("internalloop")){
			//5ploop
			lmin = bblock.getAttributeValue("minlength5ploop");
			lmax = bblock.getAttributeValue("maxlength5ploop");
			exact = bblock.getAttributeValue("length5ploop");
			checkIloop(bblock,lmin,lmax,exact,max,true);
			
			//3ploop
			lmin = bblock.getAttributeValue("minlength3ploop");
			lmax = bblock.getAttributeValue("maxlength3ploop");
			exact = bblock.getAttributeValue("length3ploop");
			checkIloop(bblock,lmin,lmax,exact,max,false);			
		}
		else{
			checkbblock(bblock,lmin,lmax,exact,gmax,max);
		}

		if(max != -1){
			if(cum_min > max){
				throw new SizeConflictException("Conflicting size restrictions at "+bblock.getName()+": implicit minimum size larger than global max");
			}
		}
		
		//für den Baustein wurden neue globale größen gegeben
		if(gmin != null){
			globalmin = Integer.parseInt(gmin);
			if(max != -1 && (cum_min + globalmin) > max){
				throw new SizeConflictException("Conflicting global size restrictions at "+bblock.getName());
			}
			min = globalmin;
			if(minbuf == 0){
				minbuf = cum_min;
			}
			if(maxbuf == 0){
			    maxbuf = cum_max;
			}
			cum_max = localmax;
			cum_min = localmin;
		}
		if(gmax != null){
			globalmax = Integer.parseInt(gmax);
			max = globalmax;
			if(maxbuf == 0){
				maxbuf = cum_max;
			}
			if(minbuf == 0){
			    minbuf = cum_min;
			}
			cum_min = localmin;
			cum_max = localmax;
		}
			
		
		if((bblock.getName()).equals("multiloop")){
			Iterator<?> itr = (bblock.getChildren("multiloop-branch",ns)).iterator();//multiloop-branches
			int multimin = 0;
			int countbuf = multicount;
			multicount = 0;
			int num = 0;
			while(itr.hasNext()){
				num++;
				Element multibranch = (Element) itr.next();
				Iterator<?> itr2 = (multibranch.getChildren("neighbor",ns)).iterator();
				Element neighbor = (Element) itr2.next();
				Iterator<?> itr3 = (neighbor.getChildren()).iterator();
				Element nextblock = (Element)itr3.next();
				multimin = getAndCheckSize(nextblock, min, max);
			}
			multimin += 2+2*num;
			countbuf -= 2+2*num;
			if(max != -1){
				if(multimin > max){
					throw new SizeConflictException("Conflicting size restrictions at "+bblock.getName()+": implicit minimum size larger than global max");
				}
			}
			return multimin+countbuf;
		}
		else{
			Iterator<?> itr = (bblock.getChildren("neighbor",ns)).iterator();//neighbor
			if(itr.hasNext()){
				Element neighbor = (Element) itr.next();
				Iterator<?> itr2 = (neighbor.getChildren()).iterator();
				Element nextblock = (Element)itr2.next();
				return getAndCheckSize(nextblock, min, max);
			}
			else{
				if(minbuf != 0){
					cum_min = minbuf;
				}
				if(maxbuf != 0){
					cum_max = maxbuf;
					maxbuf = 0;
				}
				return multicount;
			}
			
		}
	}
	
	public static void checkbblock(Element bblock, String lmin, String lmax, String exact, String gmax, int max){
		int exactlen = 0;

		String blocktype = bblock.getName();

		if(exact != null){ //der Baustein hat eine exakte Größe
			exactlen = Integer.parseInt(exact);
			if(blocktype.equals("bulge")){
				exactlen+=4;
			}
			cum_min += exactlen;
			cum_max += exactlen;
			multicount += exactlen;
		}
		else{
			if(lmin != null){ //der Baustein hat eine minimale größe
				localmin = Integer.parseInt(lmin);
				if(blocktype.equals("bulge")){
					localmin+=4;
				}
				cum_min += localmin;
				multicount += localmin;
			}
			else if(lmax == null){ //der Baustein hat kein minimum oder maximum
				if(blocktype.equals("stem")){
					cum_min += 2;
					localmin = 2;
					multicount += localmin;
				}
				else if(blocktype.equals("bulge")){
					cum_min += 5;
					localmin = 5;
					multicount += localmin;
				}
				else if(blocktype.equals("multiloop")){
					int num = (bblock.getChildren()).size();
					cum_min += (2+(2*num));
					localmin = (2+(2*num));
					multicount += localmin;
				}
				else if(blocktype.equals("hairpinloop")){
					cum_min += 5;
					localmin = 5;
					multicount += localmin;
				}
				else if(blocktype.equals("closedstruct")){
					cum_min += 2;
					localmin = 2;
					multicount += localmin;
				}
				else if(blocktype.equals("closedmultiend")){
					cum_min += 5;
					localmin = 5;
					multicount += localmin;
				}
				//TODO: austesten, ob das passt
				else if(blocktype.equals("pseudoknot")){
				    cum_min += 11;
				    localmin = 11;
				    multicount += localmin;
				}
			}
			if(lmax != null){ //der baustein hat ein maximum
				localmax = Integer.parseInt(lmax);
				if(blocktype.equals("bulge")){
					localmax+=4;
				}
				cum_max += localmax;
			}
			else if(max != -1){ //der baustein hat kein maximum, aber es gibt ein globales maximum
				if(blocktype.equals("internalloop")){
					cum_max += 64;
				}
				else{
					cum_max += max;
				}
				if(!blocktype.equals("hairpinloop") && !blocktype.equals("bulge") && !blocktype.equals("closedmultiend") && !blocktype.equals("pseudoknot")){
					if(gmax == null || Integer.parseInt(gmax) > max){
						bblock.setAttribute("gmaxsize",String.valueOf(max)); //speichere globales maximum als lokales max ab
					}
				}
				else if(blocktype.equals("bulge")){
					if(max < 30){
						bblock.setAttribute("maxlength",String.valueOf(max));
					}
				}
				else{ //Hairpin loop etc
					bblock.setAttribute("maxlength",String.valueOf(max));
				}
			}
		}
	}
	
	public static void checkIloop(Element bblock, String lmin, String lmax, String exact, int max, boolean five){
		int exactlen = 0;
		
		if(exact != null){ //der Baustein hat eine exakte Größe
			exactlen = Integer.parseInt(exact);
			cum_min += (exactlen+2); //1 loop + 1 bp
			cum_max += (exactlen+2);
			multicount += (exactlen+2);
		}
		else{
			if(lmin != null){ //der Baustein hat eine minimale größe
				localmin += Integer.parseInt(lmin) + 2;
				cum_min += (Integer.parseInt(lmin)+2);
				multicount += (Integer.parseInt(lmin)+2);
			}
			else if(lmax == null){ //der Baustein hat kein minimum oder maximum
					cum_min += 3;
					localmin += 3;
					multicount += 3;
			}
			if(lmax != null){ //der baustein hat ein maximum
				localmax += Integer.parseInt(lmax);
				cum_max += Integer.parseInt(lmax);
			}
			else if(max != -1 && max < 30){ //der baustein hat kein maximum, aber es gibt ein globales maximum
				cum_max += 32;
				if(five){
					bblock.setAttribute("maxlength5ploop",String.valueOf(max));
				}
				else{
					bblock.setAttribute("maxlength3ploop",String.valueOf(max));
				}
			}
		}
	}
}
