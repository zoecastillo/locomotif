package rnaeditor;
/*
 * 
 * Created on 07.12.2005
 */

import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;

/**
 * This class is responsible for translating the XML document into ADP code
 * 
 * 
 * @author Janina Reeder
 */
public class ADPTranslator {
	
	/*
	 * Created on 13.10.2005
	 */
	
	//strings storing the ADP code
	private static String motifstruct = "";
	private static String motifstring = "";
	//index variables used for unique naming of building blocks
	private static int motifindex = 0;
	private static int tailindex = 0;
	private static boolean structincluded = false;
	private static boolean closedincluded = false;
	private static boolean stacklenincluded = false;
	//Namespace: must be changed according to server
	public static Namespace ns = Namespace.getNamespace("de:unibi:techfak:bibiserv:rnaeditor");

	
	/**
	 * This method returns the ADP code in String format
	 * 
	 * @return String holding the ADP code
	 */
	public static String getResult(){
		return motifstruct;
	}
	
	/**
	 * This method includes the necessary header algebra information on the server side.
	 * For the client side, it is not necessary to display the ADP motif grammar alone.
	 * 
	 * @return String holding the header
	 */
	public static String initMotifStruct(){
		//SERVER
		String header = "> #algebratype{\n\n>  type FS_Algebra base comp = (\n>  \t\tbase -> comp -> comp ,  -- sadd\n>  \t\tcomp -> comp -> comp ,  -- cadd\n>  \t\tcomp -> comp -> comp ,  -- mlcons\n>  \t\tbase -> comp   -> base -> comp, --dlr\n>  \t\tbase -> comp   -> base -> comp, --sr\n>  \t\tbase -> (Int,Int) -> base -> comp, --hl\n>  \t\tbase -> (Int,Int) -> comp -> base -> comp, --bl\n>  \t\tbase -> comp -> (Int,Int) -> base -> comp, --br\n>  \t\tbase -> (Int,Int) -> comp -> (Int,Int) -> base -> comp, --il\n>  \t\tbase -> comp -> base -> comp, --ml\n>  \t\tcomp -> comp -> comp,     -- append\n>  \t\tcomp -> comp,	       -- ul\n>  \t\tcomp -> (Int,Int) -> comp,   -- addss\n>  \t\tcomp -> (Int,Int) -> comp,   -- addssml\n>  \t\t(Int,Int) -> comp -> comp,   -- ssadd\n>  \t\t(Int,Int) -> comp ,	       -- ss\n>  \t\tbase -> comp,  --nil\n>  \t\tInt -> (Int,Int) -> comp -> (Int,Int) -> comp -> (Int,Int) -> comp -> (Int,Int) -> comp,  --pk\n>  \t\tcomp -> comp,           -- pul\n>  \t\t(Int,Int) -> comp,       -- pss\n\n>  \t\tbase -> (comp,Int) -> base -> (comp,Int),  --sum\n>  \t\tbase -> (Int,Int) -> base -> (comp,Int),  --sumend\n>  \t\t[comp] -> [comp]  --h\n>  \t\t) \n>  }\n\n\n";
		header += "> #algebra[mfe]{\n\n>   mfe :: FS_Algebra Int Int \n>   mfe = (sadd, cadd, mlcons, dlr, sr, hl, bl, br, il,ml,append, ul, addss, addssml, ssadd, ss, nil, pk, pul, pss, sum, sumend, h) where\n>  \tsadd  lb e = e \n>  \tcadd  e1 e = e1 + e\n>  \tmlcons e1 e = e1 + e + 40\n>  \tdlr dl    e    dr  = e + dl_energy (dl+1,dr) + dr_energy (dl+1,dr) + termaupenalty (dl+1,dr)\n>  \tsr  lb        e    rb  = e + sr_energy  (lb,rb)\n>  \thl  lb        _    rb  =     hl_energy  (lb,rb) \n>  \tbl  lb  (i,j) e    rb  = e + bl_energy  (lb,i,j, rb)\n>  \tbr  lb        e (i,j)  rb  = e + br_energy (lb,i,j, rb)\n>  \til  lb (i,j) e (k,l) rb= e + il_energy (i,j,k,l) \n>  \tml  lb       e     rb  = 380 + e + termaupenalty (lb,rb) + dli_energy (lb,rb) + dri_energy (lb,rb)\n>  \tappend  e1 e   = e1 + e\n>  \taddss    e (i,j) = e + ss_energy (i,j)\n>  \taddssml    e (i,j) = 40 + e + ss_energy (i,j)\n>  \tul       e   = 40 + e\n>  \tssadd (i,j)  e   = e + ss_energy (i,j)\n>  \tss    (i,j)	     = ss_energy (i,j)\n>  \tnil _ = 0\n>  \tpk e a fro (i',j') mid (k,l) bac b = pkinit + e + 3*10 + fro + mid + bac\n>  \tpul e = e\n>  \tpss (i,j) = sspenalty (j-i)\n>  \tsum lb (c,k) rb = (c + sr_energy (lb,rb),k+1)\n>  \tsumend lb _ rb = (0,1)\n>  \th   es = [minimum es]\n>  }\n\n\n";
		header += "> #algebra[pp]{\n\n>      pp :: FS_Algebra Int String \n\n>      pp =  (sadd, cadd, mlcons, dlr, sr, hl, bl, br, il,ml,append, ul, addss, addssml, ssadd, ss, nil, pk, pul, pss, sum, sumend, h) where\n>  \tsadd  lb  e = \".\" ++ e\n>  \tcadd   x  e =  x  ++ e\n>  \tmlcons x  e =  x  ++ e\n>  \tdlr  _    x    _   =              x\n>  \tsr  lb    x    rb  = \"(\"  ++      x ++ \")\"	\n>  \thl  lb    (i,j)    rb  = \"(\" ++ dots (i,j) ++\")\"	\n>  \tbl  bl (i,j)   e  br  = \"(\" ++ dots (i,j) ++ e ++\")\"\n>  \tbr  bl  e (i,j)    br  = \"(\" ++ e ++ dots (i,j) ++\")\"\n>  \til  lb (i,j) x (k,l) rb  = \"(\" ++ dots (i,j) ++ x ++ dots (k,l) ++ \")\"\n>  \tml  bl    x    br  = \"(\" ++ x ++ \")\" \n>  \tappend  c1 c = c1 ++ c\n>  \tul  c1 = c1\n>  \taddss  c1 (i,j)   = c1 ++ dots (i,j) \n>  \taddssml  c1 (i,j)   = c1 ++ dots (i,j) \n>  \tssadd     (i,j) x =       dots (i,j) ++ x\n>  \tss        (i,j)	  =       dots (i,j) 	\n>  \tnil _ = \"\"\n>  \tpk _ (a1,a2) u (b1,b2) v (a1',a2') w (b1',b2') = open1 (a1,a2) ++ \".\" ++ u ++ open2 (b1,b2) ++v ++ close1 (a1',a2') ++ w ++ \"..\" ++ close2 (b1',b2')\n>  \tpul c1 = c1\n>  \tpss (i,j) = dots (i,j)\n>  \tsum _ k _ = \"\"\n>  \tsumend _ _ _ = \"\"\n>  \th es = [id es]\n>  }\n\n\n";
		header += "> #grammar {\n\n>  tdm alg f = axiom rnastruct where\n>     (sadd, cadd, mlcons, dlr, sr, hl, bl, br, il,ml,append, ul, addss, addssml, ssadd, ss, nil, pk, pul, pss, sum, sumend, h) = alg\n\n";
		return header;
		//CLIENT
		//String header = "";
		//return header;
	}
	
	/**
	 * This method translates the given DOM document into ADP code
	 * 
	 * @param doc, the org.w3c.dom.Document holding the XML code
	 * @return String holding the ADP code
	 */
	public static String translateIntoADP(org.w3c.dom.Document doc){
		motifstruct = initMotifStruct();
		motifstring = "";
		motifindex = 0;
		tailindex = 0;
		structincluded = false;	
		closedincluded = false;
		stacklenincluded = false;
		boolean includedbuf = false;
		DOMBuilder builder = new DOMBuilder();
		Document domdoc = builder.build(doc);
		Element rnamotif = domdoc.getRootElement();
		Iterator<?> itr = (rnamotif.getChildren()).iterator();
		int toplevellength = (rnamotif.getChildren()).size()*2;
		String[] toplevel = new String[(rnamotif.getChildren()).size()*2];
		int i = 0;
		Element nextchild = (Element)itr.next();
		Element extractednext = null;
		String searchtype = rnamotif.getAttributeValue("searchtype");
		String gsizestring = "";
		String globalmin = rnamotif.getAttributeValue("gminsize");
		String globalmax = rnamotif.getAttributeValue("gmaxsize");
		if(globalmax != null && globalmin != null){
			gsizestring = (" `with` size("+globalmin+","+globalmax+")");
		}
		else if(globalmax != null){
			gsizestring = (" `with` maxsize "+globalmax);
		}
		else if(globalmin != null){
			gsizestring = (" `with` minsize "+globalmin);
		}
		
		//A local search is computed: occurence of motif within longer sequence
		if(searchtype.equals("local")){
			toplevel[0] = ">     rnastruct = listed ( sadd <<< lbase -~~ rnastruct |||\n>                                      ";
			toplevel[1] = ">     motif0 = ";
			//starting with a single strand
			if(!(nextchild.getName()).equals("neighbor")){
			    includedbuf = structincluded;
				String singlestring = processSingle(nextchild, false);
				//single strand with annotations
				if(!singlestring.equals("ss <<< region\t... h")){
					toplevel[1] += singlestring;
					motifindex++;
				}
				//a nonrestricted single strand must be omitted for ambiguity reasons. 
				//it is already encoded in the first option of rnastruct rules!
				else{
				    structincluded = includedbuf;
					nextchild = (Element)itr.next();
					toplevellength -= 2;
					motifstring = "";
					processNeighbor(nextchild, true);
					toplevel[1] += motifstring;
				}
			}
			//starting with anthing but a single strand
			else{
				motifstring = "";
				processNeighbor(nextchild, true);
				toplevel[1] += motifstring;
			}
			//there are several structure parts
			if(itr.hasNext()){			   
			    //checkNextSS: if len = 0, omit it, drop next und mache mit struct weiter
		        extractednext = (Element) itr.next();
		        if(!(extractednext.getName()).equals("neighbor")){
		            String singlestring = processSingle(extractednext, true);
		            if((extractednext.getName()).equals("singleconnect") && singlestring.equals("ss <<< (uregion `with` (size (0,0)))\t... h")){
		                extractednext = null;
						toplevellength -= 2;	
						toplevel[0] += "addss <<< structstart ~~~ uregion\t... h)\n>     structstart = tabulated (( cadd <<< motif0";
		            }
		            else if((extractednext.getName()).equals("single") && singlestring.equals("ss <<< region\t... h")){
		                extractednext = null;
						toplevellength -= 2;
						toplevel[0] += "addss <<< structstart ~~~ uregion\t... h)\n>     structstart = motif0";
		            }
		            else{
		        		toplevel[0] += "addss <<< structstart ~~~ uregion\t... h)\n>     structstart = tabulated (( cadd <<< motif0";
		            }
		        }
		        else{
		            toplevel[0] += "addss <<< structstart ~~~ uregion\t... h)\n>     structstart = tabulated (( cadd <<< motif0";
		        }
			}
			//only one structure part (cannot start with a single strand)
			else{
				toplevel[0] += "addss <<< structstart ~~~ uregion";
				toplevel[0] += "\t... h)";
				toplevel[0] += "\n>     structstart = motif0";
				toplevel[0] += gsizestring;
			}
		}
		//a global search is performed: the overall sequence is folded according to the motif defintion
		else{
			if(itr.hasNext()){ 
			    extractednext = (Element) itr.next();
			    if(!(extractednext.getName()).equals("neighbor")){
			        String singlestring = processSingle(extractednext, true);
			        if((extractednext.getName()).equals("singleconnect") && singlestring.equals("ss <<< (uregion `with` (size (0,0)))\t... h")){
			            extractednext = null;
			            toplevellength -= 2;	
			        }
			    }
		        toplevel[0] = ">     rnastruct = listed (( cadd <<< motif0";
			}
			else{
				toplevel[0] = ">     rnastruct = motif0";
			}
			toplevel[1] = ">     motif0 = ";
			//starting with a single strand
			if(!(nextchild.getName()).equals("neighbor")){
				toplevel[1] += processSingle(nextchild, false);
				motifindex++;
			}
			//starting with anthing but a single strand
			else{
				motifstring = "";
				processNeighbor(nextchild, true);
				toplevel[1] += motifstring;
			}
		}
		//as long as there are other motif parts (i.e. connecting single strands or new helix)

		while(itr.hasNext()){
		    if(extractednext != null){
		        nextchild = extractednext;
		        extractednext = null;
		    }
		    else{
		        nextchild = (Element)itr.next();
		    }
			//single strand
			if(!(nextchild.getName()).equals("neighbor")){
				String singlestring = processSingle(nextchild,false);
				toplevel[i] += " ~~~ tail";
				toplevel[i] += String.valueOf(tailindex);
				toplevel[i] += ")";
				if(searchtype.equals("local")){
					toplevel[i] += gsizestring;
				}
				toplevel[i] += "\t... h )";
				i = i+2;
				toplevel[i] = ">     tail" + String.valueOf(tailindex++);
				//connecting single strand
				if(itr.hasNext()){
					toplevel[i]+= (" = tabulated (( cadd <<< motif" + String.valueOf(motifindex));
					toplevel[i+1] = (">     motif" + String.valueOf(motifindex++) + " = ");
					toplevel[i+1] += singlestring;
				}
				//extending single strand
				else{
					toplevel[i] += " = ";
					toplevel[i] += singlestring;
					toplevel[i+1] = "";
				}
				extractednext = null;
			}
			//new helix beginning
			else{
				toplevel[i] += " ~~~ tail";
				toplevel[i] += String.valueOf(tailindex);
				toplevel[i] += ")";
				if(searchtype.equals("local")){
					toplevel[i] += gsizestring;
				}
				toplevel[i] += "\t... h )";
				i = i+2;
				toplevel[i] = ">     tail" + String.valueOf(tailindex++);
				//followed by another single strand
				if(itr.hasNext()){			   
				    //checkNextSS: if len = 0, omit it, drop next und mache mit struct weiter
			        extractednext = (Element) itr.next();
			        if(!(extractednext.getName()).equals("neighbor")){
			            String singlestring = processSingle(extractednext, true);
			            if((extractednext.getName()).equals("singleconnect") && singlestring.equals("ss <<< (uregion `with` (size (0,0)))\t... h")){
			                extractednext = null;
							toplevellength -= 2;	
							toplevel[i]+= (" = tabulated (( cadd <<< motif" + String.valueOf(motifindex));
			            }
			            else if((extractednext.getName()).equals("single") && searchtype.equals("local") && singlestring.equals("ss <<< region\t... h")){
			                extractednext = null;
							toplevellength -= 2;
							toplevel[i] += (" = motif" + String.valueOf(motifindex));
			            }
			            else{
			            	toplevel[i]+= (" = tabulated (( cadd <<< motif" + String.valueOf(motifindex));
			            }
			        }
			        else{
			            toplevel[i]+= (" = tabulated (( cadd <<< motif" + String.valueOf(motifindex));
			        }
				}
				//final part of the motif
				else{ 
					toplevel[i] += (" = motif" + String.valueOf(motifindex));
				}
				toplevel[i+1] = "\n>     motif" + String.valueOf(motifindex) + " = ";
				motifstring = "";
				processNeighbor(nextchild, true);
				toplevel[i+1] += motifstring;
			}
		}
	    if(extractednext != null){
	        nextchild = extractednext;
	        //	      single strand
			if(!(nextchild.getName()).equals("neighbor")){
				toplevel[i] += " ~~~ tail";
				toplevel[i] += String.valueOf(tailindex);
				toplevel[i] += ")";
				if(searchtype.equals("local")){
					toplevel[i] += gsizestring;
				}
				toplevel[i] += "\t... h )";
				i = i+2;
				toplevel[i] = ">     tail" + String.valueOf(tailindex++);
				toplevel[i] += " = ";
				toplevel[i] += processSingle(nextchild, false);
				toplevel[i+1] = "";
			}
			//new helix beginning
			else{
				toplevel[i] += " ~~~ tail";
				toplevel[i] += String.valueOf(tailindex);
				toplevel[i] += ")";
				if(searchtype.equals("local")){
					toplevel[i] += gsizestring;
				}
				toplevel[i] += "\t... h )";
				i = i+2;
				toplevel[i] = ">     tail" + String.valueOf(tailindex++);
				toplevel[i] += (" = motif" + String.valueOf(motifindex));
				toplevel[i+1] = "\n>     motif" + String.valueOf(motifindex) + " = ";
				motifstring = "";
				processNeighbor(nextchild, true);
				toplevel[i+1] += motifstring;
			}
	    }
		for(int j = 0;j<toplevellength;j++){
		    if(toplevel[j] != null){
		        motifstruct += toplevel[j];
		        motifstruct += "\n\n\n";
		    }
		}
		//nur fuer SERVER!
		motifstruct += ">  }";
		return motifstruct;
	}
	
	/**
	 * This method processes a single strand element
	 * 
	 * @param single a DOM Element which is a single strand
	 * @return String containing the relevant ADP code
	 */
	public static String processSingle(Element single, boolean checkonly){
		String ssstring = "ss <<< ";
		String len = single.getAttributeValue("length");
		String minlen = single.getAttributeValue("minlength");
		String maxlen = single.getAttributeValue("maxlength");
		String straightness = single.getAttributeValue("straight");
		String region = "region";
		if((single.getName()).equals("singleconnect")){
		    region = "uregion";
		}
		Element seqmotif = single.getChild("seqmotif",ns);
		String seqstart = "";
		String regionstring = "";
		String sizestring = "";
		if(seqmotif != null){
			seqstart = "( ";
		}
		if(len != null){
			ssstring += "(";
			ssstring += seqstart;
			ssstring += region;
			sizestring = " `with` (size ("+len+","+len+"))";
			ssstring += sizestring;
			ssstring += ")";
		}
		else if(minlen != null && maxlen != null){
			ssstring += "(";
			ssstring += seqstart;
			ssstring += region;
			sizestring += " `with` (size ("+minlen+","+maxlen+"))";
			ssstring += sizestring;
			ssstring += ")";
		}
		else if(minlen != null){
			ssstring += "(";
			ssstring += seqstart;
			ssstring += region;
			sizestring += " `with` (minsize " + minlen + ")";
			ssstring += sizestring;
			ssstring += ")";
		}
		else if(maxlen != null){
			ssstring += "(";
			ssstring += seqstart;
			ssstring += region;
			sizestring += " `with` (maxsize " + maxlen + ")";
			ssstring += sizestring;
			ssstring += ")";
		}
		else{
			ssstring += seqstart;
			ssstring += region;
		}
		if(seqmotif != null){
			regionstring += " `with` contains_region \"";
			regionstring += seqmotif.getText();
			regionstring += "\"";
			ssstring += regionstring;
			ssstring += ")";
		}
		if(straightness == null || Boolean.parseBoolean(straightness)){
			ssstring += "\t... h";
		}
		else{
			ssstring += " |||\n>                                  struct";
			ssstring += sizestring;
			ssstring += "\t...h";
			if(!checkonly){
			    ssstring += processExtraStruct(regionstring,maxlen);
			}
		}
		return ssstring;
	}
	
	/**
	 * This method is called to process a building block which is NOT a single strand
	 * 
	 * @param next, the DOM Element which represents the next building block
	 * @param adddangle, boolean flag indicating whether this is the first building block of a helix. Then, dangling bases are taken into account.
	 */
	public static void processNeighbor(Element next, boolean adddangle){
		Iterator<?> itr = (next.getChildren()).iterator();
		Element child = (Element)itr.next();
		String name = child.getName();
		//Hairpin: end of recursion
		if(name.equals("hairpinloop")){
			processHairpinLoop(child,adddangle);
		}
		//ClosedEnd: end of recursion
		else if(name.equals("closedmultiend")){
			processClosedMultiEnd(child, adddangle);
		}
		//Pseudoknot: end of recursion
		else if(name.equals("pseudoknot")){
		    processPseudoknot(child);
		}
		//Multiloop: several recursions
		else if(name.equals("multiloop")){
		    //initial part of the multiloop
			String maxsize = processMultiLoopStart(child, adddangle);
			Element seqmotif = child.getChild("seqmotif",ns);
			String len = child.getAttributeValue("length");
			String minl = child.getAttributeValue("minlength");
			String maxl = child.getAttributeValue("maxlength");
			itr = (child.getChildren()).iterator(); //gives multiloop-branch
			//processing all brances but last
			for(int i=0;i<(child.getChildren("multiloop-branch",ns)).size()-1;i++){
				if(itr.hasNext()){
					Element mlbranch = (Element)itr.next();
					if((mlbranch.getName()).equals("multiloop-branch")){
						processMultiLoopBranch(mlbranch,seqmotif,len,minl,maxl,maxsize);
						Iterator<?> itr2 = (mlbranch.getChildren()).iterator(); //gives neighbor
						processNeighbor((Element)itr2.next(), false);
						seqmotif = mlbranch.getChild("seqmotif",ns);
						len = mlbranch.getAttributeValue("length");
						minl = mlbranch.getAttributeValue("minlength");
						maxl = mlbranch.getAttributeValue("maxlength");
					}
				}
			}
			//processing the last multiloop branch
			if(itr.hasNext()){
				Element mlbranch = (Element)itr.next();
				if((mlbranch.getName()).equals("multiloop-branch")){
					processMultiLoopLastBranch(mlbranch,seqmotif,len,minl,maxl,maxsize);
					Iterator<?> itr2 = (mlbranch.getChildren()).iterator(); //gives neighbor
					processNeighbor((Element)itr2.next(), false);
				}
			}
		}
		//all other building blocks have exactly 1 neighbor: 1 recursion
		else{
			Element neighbor = (Element)((child.getChildren()).iterator()).next();
			//Stem
			if(name.equals("stem")){
				processStem(child, adddangle);
				processNeighbor(neighbor, false);
			}
			//Bulge loop
			else if(name.equals("bulge")){
				processBulge(child, adddangle);
				processNeighbor(neighbor, false);
			}
			//Internal loop
			else if(name.equals("internalloop")){
				processInternalLoop(child, adddangle);
				processNeighbor(neighbor, false);
			}
			//Closed struct
			else if(name.equals("closedstruct")){
				processClosedStruct(child, adddangle);
				processNeighbor(neighbor, false);
			}
		}
	}
	
	//HERE ARE THE METHODS FOR PROCESSING THE INDIVIDUAL BUILDING BLOCKS
	
	/**
	 * Processing a stem element
	 * 
	 * @param stem, the relevant DOM element
	 * @param addgangle, boolean flag indicating whether dangling bases must be included
	 */
	public static void processStem(Element stem, boolean adddangle){
		String len = stem.getAttributeValue("length");
		String minlen = stem.getAttributeValue("minlength");
		String maxlen = stem.getAttributeValue("maxlength");
		String minsize = stem.getAttributeValue("gminsize");
		String maxsize = stem.getAttributeValue("gmaxsize");
		String gsizestring = "";
		String allowinterrupt = stem.getAttributeValue("allowinterrupt");
		String[] leftbases = null;
		String[] rightbases = null;
		Element seqmotif = stem.getChild("seqmotif",ns);
		Element motifloc = stem.getChild("motifloc",ns);
		java.util.List<?> bps = stem.getChildren("basepair",ns);
		
		//add dangling bases
		if(adddangle){
			motifstring += ("(dlr <<< loc ~~~ stem" + String.valueOf(motifindex) + " ~~~ loc)");
		}
		else{
			motifstring += ("stem" + String.valueOf(motifindex));
		}
		
		//store global size restrictions
		if(minsize != null && maxsize != null){
			motifstring += " `with` size ("+minsize+","+maxsize+")";
			gsizestring = " `with` maxsize "+maxsize;
		}
		else if(minsize != null){
			motifstring += " `with` minsize "+minsize;
		}
		else if(maxsize != null){
			motifstring += " `with` maxsize "+maxsize;
			gsizestring = " `with` maxsize "+maxsize;
		}
		
		//add minimum global size restriction
		motifstring += ("\t... h\n>     stem" + String.valueOf(motifindex) + " = ");
		
		//stem can be interrupted by small loops
		if(allowinterrupt != null && Boolean.parseBoolean(allowinterrupt)){
			motifstring += "tabulated (( sr <<< lbase -~~ stem";
			motifstring += String.valueOf(motifindex);
			motifstring += " ~~- lbase |||\n>                                          bl <<< lbase -~~ (region `with` maxsize 2) ~~~ stembp";
			motifstring += String.valueOf(motifindex);
			motifstring += " ~~- lbase |||\n>                                          br <<< lbase -~~ stembp";
			motifstring += String.valueOf(motifindex);
			motifstring += " ~~~ (region `with` maxsize 2) ~~- lbase |||\n>                                           il <<< lbase -~~ (region `with` maxsize 2) ~~~ stembp";
			motifstring += String.valueOf(motifindex);
			motifstring += " ~~~ (region `with` maxsize 2) ~~- lbase |||\n>                                          sr <<< lbase -~~ motif";
			motifstring += String.valueOf(motifindex+1);
			motifstring += " ~~- lbase) `with` basepairing\t...h)\n\n";
			motifstring += ">     stembp";
			motifstring += String.valueOf(motifindex);
			motifstring += " = tabulated ( (sr <<< lbase -~~ stem";
			motifstring += String.valueOf(motifindex);
			motifstring += " ~~- lbase |||\n>                                              sr <<< lbase -~~ motif";
			motifstring += String.valueOf(++motifindex);
			motifstring += " ~~- lbase) `with` basepairing";
			//adding maximum global size restriction (can be empty string if none given!)
			motifstring += gsizestring;
			motifstring += "\t...h)";
		}
		
		//no local size restrictions
		else if(len == null && minlen == null && maxlen == null){
			motifstring += "tabulated ( ";
			
			//Stem contains a sequence motif
			if(seqmotif != null){
				String motif = seqmotif.getText();
				int l = motif.length();
				leftbases = new String[l];
				rightbases = new String[l];
				for(int i = 0;i < l; i++){
					leftbases[i] = "lbase";
					rightbases[i] = "lbase";
				}
				
				//motif located on five prime strand: store all bases in array
				if((motifloc.getText()).equals("5prime")){
					for(int i=0;i<motif.length();i++){
						leftbases[i] = "iupac_base '" + motif.substring(i,i+1) + "'";
					}
				}
				//motif located on three prime strand: store all bases in array
				else{
					for(int i=0;i<motif.length();i++){
						rightbases[i] = "iupac_base '" + motif.substring(i,i+1) + "'";
					}
				}
				//explicitly include all stored bases for the 5 prime strand
				for(int i = 0; i < l; i++){
					motifstring += "((sr <<< ";
					motifstring += leftbases[i];
					motifstring += " -~~ ";
				}
				//then follows an unrestricted stem definition
				motifstring += "maxstem";
				motifstring += String.valueOf(motifindex);
				//then, explicitly include all stored bases for 3 prime strand
				for(int i = 0; i < l; i++){
					motifstring += " ~~- ";
					motifstring += rightbases[i];
					motifstring += ") `with` basepairing)";
				}
				motifstring += "\t... h)";
				//maxstem corresponds to stem definition without any restrictions
				motifstring += "\n>     maxstem";
				motifstring += String.valueOf(motifindex);
				motifstring += " = ";
				motifstring += "tabulated ( ";
				motifstring += "(sr <<< lbase -~~ maxstem";
				motifstring += String.valueOf(motifindex);
				motifstring += " ~~- lbase) `with` basepairing";
				//add maximum global size restriction on recursive definition
				motifstring += gsizestring;
				motifstring += " |||\n>                                                   motif";
				motifstring += String.valueOf(++motifindex);
				motifstring += "\t... h)";
			}
			
			//instead of sequence motif, a basepair sequence is given
			else if(!bps.isEmpty()){
				Iterator<?> itr = bps.iterator();
				int l = bps.size();
				leftbases = new String[l];
				rightbases = new String[l];
				int j = 0;
				//store bases on both strands in individual arrays
				while(itr.hasNext()){
					Element basepair = (Element) itr.next();
					String bptext = basepair.getAttributeValue("bp");
					leftbases[j] = "iupac_base '" + bptext.substring(0,1) + "'";
					rightbases[j] = "iupac_base '" + bptext.substring(1,2) + "'";
					j++;
				}
				//explicitly include all stored bases for 5 prime strand
				for(int i = 0; i < l; i++){
					motifstring += "((sr <<< ";
					motifstring += leftbases[i];
					motifstring += " -~~ ";
				}
				//unrestricted maxstem definition
				motifstring += "maxstem";
				motifstring += String.valueOf(motifindex);
				//explicitly include all stored bases for 3 prime strand
				for(int i = l-1; i >= 0; i--){
					motifstring += " ~~- ";
					motifstring += rightbases[i];
					motifstring += ") `with` basepairing)";
				}
				motifstring += "\t... h)";
				motifstring += "\n>     maxstem";
				motifstring += String.valueOf(motifindex);
				motifstring += " = ";
				motifstring += "tabulated ( ";
				motifstring += "(sr <<< lbase -~~ maxstem";
				motifstring += String.valueOf(motifindex);
				motifstring += " ~~- lbase) `with` basepairing";
				//add maximum global size restriction on recursive maxstem definition
				motifstring += gsizestring;
				motifstring += " |||\n>                                                   motif";
				motifstring += String.valueOf(++motifindex);
				motifstring += "\t... h)";
			}
			
			//no sequence restrictions: unrestricted stem
			else{
				motifstring += "(sr <<< lbase -~~ stem";
				motifstring += String.valueOf(motifindex++);
				motifstring += " ~~- lbase |||\n>                                          sr <<< lbase -~~ motif";
				motifstring += String.valueOf(motifindex);
				motifstring += " ~~- lbase) `with` basepairing";
				//add maximum global size restriction
				motifstring += gsizestring;
				motifstring += "\t... h)";
			}
		}
		
		//an exact length is given for the stem
		else if(len != null){
			motifstring += "tabulated ( ";
			int l = Integer.parseInt(len);
			leftbases = new String[l];
			rightbases = new String[l];
			
			//initialize arrays to standard lbase parsers
			for(int i = 0;i < l; i++){
				leftbases[i] = "lbase";
				rightbases[i] = "lbase";
			}
			
			//a sequence motif is given
			if(seqmotif != null){
				String motif = seqmotif.getText();
				//replace the relevant array with specific base parsers for the motif
				if((motifloc.getText()).equals("5prime")){
					for(int i=0;i<motif.length();i++){
						leftbases[i] = "iupac_base '" + motif.substring(i,i+1) + "'";
					}
				}
				else{
					for(int i=0,j=motif.length()-1;i<motif.length();i++,j--){
						rightbases[j] = "iupac_base '" + motif.substring(i,i+1) + "'";
					}
				}
			}
			//a basepair sequence is given
			else if(!bps.isEmpty()){
				Iterator<?> itr = bps.iterator();
				int i = 0;
				//replace both arrays with specific base parsers according to the bp sequence
				while(itr.hasNext()){
					Element basepair = (Element) itr.next();
					String bptext = basepair.getAttributeValue("bp");
					leftbases[i] = "iupac_base '" + bptext.substring(0,1) + "'";
					rightbases[i] = "iupac_base '" + bptext.substring(1,2) + "'";
					i++;
				}
			}
			
			//explicitly enumerate all bases for the 5' strand of the stem (can be specific or lbase parsers)
			for(int i = 0; i < l; i++){
				motifstring += "((sr <<< ";
				motifstring += leftbases[i];
				motifstring += " -~~ ";
			}
			//next motif building block
			motifstring += "motif";
			motifstring += String.valueOf(++motifindex);
			//explicitly enumerate all bases for the 3' strand of the stem (lbase or specific parsers)
			for(int i = l-1; i >= 0; i--){
				motifstring += " ~~- ";
				motifstring += rightbases[i];
				motifstring += ") `with` basepairing) ";
			}
			motifstring += "\t... h)";
		}
		
		//a minimum and a maximum length are given
		else if(minlen != null && maxlen != null){
			motifstring += "tabulated ( ";
			int minl = Integer.parseInt(minlen);
			int l = Integer.parseInt(maxlen);
			leftbases = new String[l];
			rightbases = new String[l];
			//initialize base parser arrays
			for(int i = 0;i < l; i++){
				leftbases[i] = "lbase";
				rightbases[i] = "lbase";
			}
			//replace the relevant array with the sequence motif base parsers
			if(seqmotif != null){
				String motif = seqmotif.getText();
				if(motif.length() > minl){
					minl = motif.length();
				}
				if((motifloc.getText()).equals("5prime")){
					for(int i=0;i<motif.length();i++){
						leftbases[i] = "iupac_base '" + motif.substring(i,i+1) + "'";
					}
				}
				else{
					for(int i=0,j=motif.length()-1;i<motif.length();i++,j--){
						rightbases[j] = "iupac_base '" + motif.substring(i,i+1) + "'";
					}
				}
			}
			//replace both arrays with specific parsers according to bp sequence
			else if(!bps.isEmpty()){
				Iterator<?> itr = bps.iterator();
				int i = 0;
				if(bps.size() > minl){
					minl = bps.size();
				}
				while(itr.hasNext()){
					Element basepair = (Element) itr.next();
					String bptext = basepair.getAttributeValue("bp");
					leftbases[i] = "iupac_base '" + bptext.substring(0,1) + "'";
					rightbases[i] = "iupac_base '" + bptext.substring(1,2) + "'";
					i++;
				}
			}
			int j = 0;
			//explicitly enumerate the minimum number of bases for 5' strand (lbase or specific)
			for(int i = 0; i < minl-1; i++,j++){
				motifstring += "((sr <<< ";
				motifstring += leftbases[i];
				motifstring += " -~~ ";
			}
			//add a recursive maxstem for maximum number
			motifstring += "maxstem";
			motifstring += String.valueOf(motifindex);
			//explicitly enumerate minimum number of bases for 3' strand
			for(int i = minl-2; i >= 0; i--){
				motifstring += " ~~- ";
				motifstring += rightbases[i];
				motifstring += ") `with` basepairing) ";
			}
			motifstring += "\t... h)";
			motifstring += "\n>     maxstem";
			motifstring += String.valueOf(motifindex);
			motifstring += " = ";
			++motifindex;
			//spacer string used for better output formatting
			String spacer = "   ";
			//explicitly enumerate the remaining bases until maximum in maxstem definitions
			//for each base the recursion can either go to the next building block or the next basepair!
			for(int i = j;i < l-1; i++){
				motifstring += "(sr <<< ";
				motifstring += leftbases[i];
				motifstring += " -~~ ( motif";
				motifstring += String.valueOf(motifindex);
				motifstring += " |||\n>                               ";
				motifstring += spacer;
				spacer += "   ";
			}
			spacer = spacer.substring(3);
			motifstring += "(sr <<< ";
			motifstring += leftbases[l-1];
			motifstring += " -~~ motif";
			motifstring += String.valueOf(motifindex);
			//same as above for bases on 3' strand
			for(int i=l-1,k = 0; i > j;i--,k++){
				motifstring += " ~~- ";
				motifstring += rightbases[i];
				motifstring += ") `with` basepairing)\n>                               ";
				motifstring += spacer.substring(k*3);
			}
			motifstring += " ~~- ";
			motifstring += rightbases[j];
			motifstring += ") `with` basepairing\t ... h";
		}
		
		//only a minimum size restriction if given
		else if(minlen != null){
			motifstring += "tabulated ( ";
			int l = Integer.parseInt(minlen);
			leftbases = new String[l];
			rightbases = new String[l];
			//base parser array initialization
			for(int i = 0;i < l; i++){
				leftbases[i] = "lbase";
				rightbases[i] = "lbase";
			}
			if(seqmotif != null){
				String motif = seqmotif.getText();
				//if motif length is larger than minlength, arrays must be reinitialized
				if(motif.length() > l){
					l = motif.length();
					leftbases = new String[l];
					rightbases = new String[l];
					for(int i = 0;i < l; i++){
						leftbases[i] = "lbase";
						rightbases[i] = "lbase";
					}
				}
				//replace 5' strand parsers
				if((motifloc.getText()).equals("5prime")){
					for(int i=0;i<motif.length();i++){
						leftbases[i] = "iupac_base '" + motif.substring(i,i+1) + "'";
					}
				}
				//replace 3' strand parsers
				else{
					for(int i=0,j=motif.length()-1;i<motif.length();i++,j--){
						rightbases[j] = "iupac_base '" + motif.substring(i,i+1) + "'";
					}
				}
			}
			else if(!bps.isEmpty()){
				Iterator<?> itr = bps.iterator();
				int i = bps.size();
				//more basepairs than minlength: reinitialize
				if(i > l){
					l = i;
					leftbases = new String[l];
					rightbases = new String[l];
					for(int k = 0;k < l; k++){
						leftbases[k] = "lbase";
						rightbases[k] = "lbase";
					}
				}
				int k = 0;
				//replace parser arrays
				while(itr.hasNext()){
					Element basepair = (Element) itr.next();
					String bptext = basepair.getAttributeValue("bp");
					leftbases[k] = "iupac_base '" + bptext.substring(0,1) + "'";
					rightbases[k] = "iupac_base '" + bptext.substring(1,2) + "'";
					k++;
				}
			}
			//enumerate minimum number of bases for 5' strand (according to minlength or motif/bp seq length)
			for(int i = 0; i < l; i++){
				motifstring += "((sr <<< ";
				motifstring += leftbases[i];
				motifstring += " -~~ ";
			}
			//unrestricted maxstem follows
			motifstring += "maxstem";
			motifstring += String.valueOf(motifindex);
			//enumerate minimum number of bases for 3' strand
			for(int i = l-1; i >= 0; i--){
				motifstring += " ~~- ";
				motifstring += rightbases[i];
				motifstring += ") `with` basepairing)";
			}
			motifstring += "\t... h)";
			motifstring += "\n>     maxstem";
			motifstring += String.valueOf(motifindex);
			motifstring += " = ";
			motifstring += "tabulated ( ";
			motifstring += "(sr <<< lbase -~~ maxstem";
			motifstring += String.valueOf(motifindex);
			motifstring += " ~~- lbase) `with` basepairing";
			//add maximum global size restriction on unrestricted maxstem
			motifstring += gsizestring;
			motifstring += " |||\n>                                                   motif";
			motifstring += String.valueOf(++motifindex);
			motifstring += "\t... h)";
		}
		//only a maximum size is given
		else{
			++motifindex;
			String spacer = "   ";
			int l = Integer.parseInt(maxlen);
			int minl = 0;
			leftbases = new String[l];
			rightbases = new String[l];
			//parser array initialization
			for(int i = 0;i < l; i++){
				leftbases[i] = "lbase";
				rightbases[i] = "lbase";
			}
			if(seqmotif != null){
				String motif = seqmotif.getText();
				//motif automatically leads to a minimum length
				minl = motif.length();
				//replace parsers for 5' strand
				if((motifloc.getText()).equals("5prime")){
					for(int i=0;i<motif.length();i++){
						leftbases[i] = "iupac_base '" + motif.substring(i,i+1) + "'";
					}
				}
				//replace parsers for 3' strand
				else{
					for(int i=0,j=motif.length()-1;i<motif.length();i++,j--){
						rightbases[j] = "iupac_base '" + motif.substring(i,i+1) + "'";
					}
				}
			}
			else if(!bps.isEmpty()){
				Iterator<?> itr = bps.iterator();
				//bp seq automatically requires minimum size
				minl = bps.size();
				int i = 0;
				//replace parser arrays
				while(itr.hasNext()){
					Element basepair = (Element) itr.next();
					String bptext = basepair.getAttributeValue("bp");
					leftbases[i] = "iupac_base '" + bptext.substring(0,1) + "'";
					rightbases[i] = "iupac_base '" + bptext.substring(1,2) + "'";
					i++;
				}
			}
			int j = 0;
			//if motif or bp seq is given, a minimum size restrition is imposed and must be included
			if(minl != 0){
			    //this code is the same as the one for min and max size restriction
			    motifstring += "tabulated ( ";
			    //enumerate minimum number of bases for 5' strand
				for(int i = 0; i < minl-1; i++,j++){
					motifstring += "((sr <<< ";
					motifstring += leftbases[i];
					motifstring += " -~~ ";
				}
				//if minimum size is given, we need maxstem restriction
				motifstring += "maxstem";
				motifstring += String.valueOf(motifindex);
				//enumerate minimum number of bases for 3' strand
				for(int i = minl-2; i >= 0; i--){
					motifstring += " ~~- ";
					motifstring += rightbases[i];
					motifstring += ") `with` basepairing) ";
				}
				motifstring += "\t... h)";
				motifstring += "\n>     maxstem";
				motifstring += String.valueOf(motifindex);
				motifstring += " = ";
			}
			//either maxstem or direct definition of stem: list maximum number of basepairs with option to interrupt after each bp
			//5'strand
			for(int i = j;i < l-1; i++){
				motifstring += "(sr <<< ";
				motifstring += leftbases[i];
				motifstring += " -~~ ( motif";
				motifstring += String.valueOf(motifindex);
				motifstring += " |||\n>                               ";
				motifstring += spacer;
				spacer += "   ";
			}
			//space used for output formatting
			spacer = spacer.substring(3);
			//final basepair must lead to next motif building block
			motifstring += "(sr <<< ";
			motifstring += leftbases[l-1];
			motifstring += " -~~ motif";
			motifstring += String.valueOf(motifindex);
			//3'strand
			for(int i=l-1,k = 0; i > j;i--,k++){
				motifstring += " ~~- ";
				motifstring += rightbases[i];
				motifstring += ") `with` basepairing)\n>                               ";
				motifstring += spacer.substring(k*3);
			}
			motifstring += " ~~- ";
			motifstring += rightbases[j];
			motifstring += ") `with` basepairing\t... h";
		}
		motifstring += "\n\n>     motif";
		motifstring += String.valueOf(motifindex);
		motifstring += " = ";
	}
	
	/**
	 * Method for processing a bulge element
	 * 
	 * @param bulge, The DOM element to be processed
	 * @param adddangle, boolean flag indicated whether dangling bases must be taken into account
	 */
	public static void processBulge(Element bulge, boolean adddangle){
		String pos = bulge.getAttributeValue("bulge-pos");
		String len = bulge.getAttributeValue("length");
		String minlen = bulge.getAttributeValue("minlength");
		String maxlen = bulge.getAttributeValue("maxlength");
		String minsize = bulge.getAttributeValue("gminsize");
		String maxsize = bulge.getAttributeValue("gmaxsize");
		java.util.List<?> list = bulge.getChildren("basepair",ns);
		//initialization of bulge bases: lbase parsers
		String[] lbases = {"lbase","lbase","lbase","lbase"};
		
		//specific basepairs are defined
		if(list != null){
			Iterator<?> itr = (bulge.getChildren("basepair",ns)).iterator();
			//replace the relevant base parser with a specific one
			while(itr.hasNext()){
				Element basepair = (Element) itr.next();
				String bptext = basepair.getAttributeValue("bp");
				if((basepair.getAttributeValue("bp-pos")).equals("5primeend")){
					lbases[0] = "iupac_base '"+bptext.substring(0,1)+"'";
					lbases[1] = "iupac_base '"+bptext.substring(1,2)+"'";
				}
				else{
					lbases[2] = "iupac_base '"+bptext.substring(0,1)+"'";
					lbases[3] = "iupac_base '"+bptext.substring(1,2)+"'";
				}
			}
		}
		String region = "";
		Element seqmotif = bulge.getChild("seqmotif",ns);
		String seqstart = "";
		
		//a sequence motif was defined
		if(seqmotif != null){
			seqstart = "(";
		}
		//exact length restriction for the region
		if(len != null){
			region += "(";
			region += seqstart;
			region += "region";
			region += (" `with` size ("+len+","+len+")");
			region += ")";
		}
		//minimum and maximum restriction for the region
		else if(minlen != null && maxlen != null){
			region += "(";
			region += seqstart;
			region += "region";
			region += (" `with` size ("+minlen+","+maxlen+")");
			region += ")";
		}
		//minimum restriction for  the region
		else if(minlen != null){
			region += "(";
			region += seqstart;
			region += "region";
			region += (" `with` size ("+minlen+",30)");
			region += ")";
		}
		//maximum restriction for the region
		else if(maxlen != null){
			region += "(";
			region += seqstart;
			region += "region";
			region += (" `with` maxsize " + maxlen);
			region += ")";
		}
		//no restriction: use default maxsize of 30 for bulge loop
		else{
			region += seqstart;
			region += "region `with` maxsize 30";
		}
		//add sequence motif to region
		if(seqmotif != null){
			region += " `with` contains_region \"";
			region += seqmotif.getText();
			region += "\" )";
		}
		
		//dangling bases added
		if(adddangle){
			motifstring += ("(dlr <<< loc ~~~ bulge" + String.valueOf(motifindex) + " ~~~ loc)");
		}
		else{
			motifstring += ("bulge" + String.valueOf(motifindex));
		}
		
		//add global size restrictions
		if(minsize != null && maxsize != null){
			motifstring += " `with` size ("+minsize+","+maxsize+")";
		}
		else if(minsize != null){
			motifstring += " `with` minsize "+minsize;
		}
		else if(maxsize != null){
			motifstring += " `with` maxsize "+maxsize;
		}
		
		//bulge definition
		motifstring += ("\t... h\n>     bulge" + String.valueOf(motifindex) + " = ");
		if(pos != null){
		    //bulge region located on 5' strand: first region, then motif
			if(pos.equals("5prime")){
				motifstring += "tabulated ( (bl <<< ";
				motifstring += lbases[0];
				motifstring += " -~~ ";
				motifstring += region;
				motifstring += " ~~~ ";
				motifstring += "motif_b";
				motifstring += String.valueOf(++motifindex);
			}
			//bulge located on 3' strand
			else{
				motifstring += "tabulated ( (br <<< ";
				motifstring += lbases[0];
				motifstring += " -~~ ";
				motifstring += "motif_b";
				motifstring += String.valueOf(++motifindex);
				motifstring += " ~~~ ";
				motifstring += region;
			}
		}
		//default value: bulge loop located on 5' strand
		else{
			motifstring += "tabulated ( (bl <<< ";
			motifstring += lbases[0];
			motifstring += " -~~ ";
			motifstring += region;
			motifstring += " ~~~ ";
			motifstring += "motif_b";
			motifstring += String.valueOf(++motifindex);
		}
		motifstring += " ~~- ";
		//closes first basepair of bulge
		motifstring += lbases[1];
		motifstring += ") `with` basepairing\t... h)";
		//motif_b defines second basepair of bulge
		motifstring += "\n>     motif_b";
		motifstring += String.valueOf(motifindex);
		motifstring += " = (sr <<< ";
		motifstring += lbases[2];
		motifstring += " -~~ motif";
		motifstring += String.valueOf(motifindex);
		motifstring += " ~~- ";
		motifstring += lbases[3];
		motifstring += ") `with` basepairing\t... h\n\n>     motif";
		motifstring += String.valueOf(motifindex);
		motifstring += " = ";
	}
	
	/**
	 * Method for processing an Internal loop
	 * 
	 * @param internalloop, the DOM element represent the internal loop
	 * @param adddangle, boolean flag indicating whether dangling bases must be taken into account
	 */
	public static void processInternalLoop(Element internalloop, boolean adddangle){
		String len3p = internalloop.getAttributeValue("length3ploop");
		String len5p = internalloop.getAttributeValue("length5ploop");
		String minlen3p = internalloop.getAttributeValue("minlength3ploop");
		String minlen5p = internalloop.getAttributeValue("minlength5ploop");
		String maxlen3p = internalloop.getAttributeValue("maxlength3ploop");
		String maxlen5p = internalloop.getAttributeValue("maxlength5ploop");
		String minsize = internalloop.getAttributeValue("gminsize");
		String maxsize = internalloop.getAttributeValue("gmaxsize");
		Element seqmotif5p = internalloop.getChild("seqmotif5ploop",ns);
		Element seqmotif3p = internalloop.getChild("seqmotif3ploop",ns);
		java.util.List<?> list = internalloop.getChildren("basepair",ns);
		//four bases of the internal loop are initialized to lbase parsers
		String[] lbases = {"lbase","lbase","lbase","lbase"};
		
		//specific basepairs were given: lbases array must be updated!
		if(list != null){
			Iterator<?> itr = (internalloop.getChildren("basepair",ns)).iterator();
			while(itr.hasNext()){
				Element basepair = (Element) itr.next();
				String bptext = basepair.getAttributeValue("bp");
				if((basepair.getAttributeValue("bp-pos")).equals("5primeend")){
					lbases[0] = "iupac_base '"+bptext.substring(0,1)+"'";
					lbases[1] = "iupac_base '"+bptext.substring(1,2)+"'";
				}
				else{
					lbases[2] = "iupac_base '"+bptext.substring(0,1)+"'";
					lbases[3] = "iupac_base '"+bptext.substring(1,2)+"'";
				}
			}
		}
		String region = "";
		
		//include dangling bases
		if(adddangle){
			motifstring += ("(dlr <<< loc ~~~ iloop" + String.valueOf(motifindex) + " ~~~ loc)");
		}
		else{
			motifstring += ("iloop" + String.valueOf(motifindex));
		}
		
		//global size restrictions
		if(minsize != null && maxsize != null){
			motifstring += " `with` size ("+minsize+","+maxsize+")";
		}
		else if(minsize != null){
			motifstring += " `with` minsize "+minsize;
		}
		else if(maxsize != null){
			motifstring += " `with` maxsize "+maxsize;
		}
		
		//start of iloop definitions
		motifstring += ("\t... h\n>     iloop" + String.valueOf(motifindex) + " = ");
		motifstring += "tabulated ( (il <<< ";
		motifstring += lbases[0];
		motifstring += " -~~ ";
		String seqstart = "";
		
		//add a sequence motif on 5' strand
		if(seqmotif5p != null){
			seqstart = "( ";
		}
		//exact length restriction
		if(len5p != null){
			region += "(";
			region += seqstart;
			region += "region";
			region += (" `with` size ("+len5p+","+len5p+")");
			region += ")";
		}
		//min and max length
		else if(minlen5p != null && maxlen5p != null){
			region += "(";
			region += seqstart;
			region += "region";
			region += (" `with` size ("+minlen5p+","+maxlen5p+")");
			region += ")";
		}
		//only min length
		else if(minlen5p != null){
			region += "(";
			region += seqstart;
			region += "region";
			region += (" `with` size ("+ minlen5p+", 30)");
			region += ")";
		}
		//only max length
		else if(maxlen5p != null){
			region += "(";
			region += seqstart;
			region += "region";
			region += (" `with` maxsize " + maxlen5p);
			region += ")";
		}
		//no size restriction for 5' loop: overall maximum loop size of 30 included
		else{
			region += seqstart;
			region += "region `with` maxsize 30";
		}
		//add the sequence motif
		if(seqmotif5p != null){
			region += " `with` contains_region \"";
			region += seqmotif5p.getText();
			region += "\" )";
		}
		motifstring += region;
		motifstring += " ~~~ ";
		//motif_il defines the second basepair of the internal loop
		motifstring += "motif_il";
		motifstring += String.valueOf(++motifindex);

		region = "";
		seqstart = "";
		//sequence motif given for 3' strand?
		if(seqmotif3p != null){
			seqstart = "( ";
		}
		//exact length
		if(len3p != null){
			region += "(";
			region += seqstart;
			region += "region";
			region += (" `with` size ("+len3p+","+len3p+")");
			region += ")";
		}
		//min and max length
		else if(minlen3p != null && maxlen3p != null){
			region += "(";
			region += seqstart;
			region += "region";
			region += (" `with` size ("+minlen3p+","+maxlen3p+")");
			region += ")";
		}
		//only min length
		else if(minlen3p != null){
			region += "(";
			region += seqstart;
			region += "region";
			region += (" `with` size ("+minlen3p+", 30)");
			region += ")";
		}
		//only max length
		else if(maxlen3p != null){
			region += "(";
			region += seqstart;
			region += "region";
			region += (" `with` maxsize " + maxlen3p);
			region += ")";
		}
		//no length restriction: use global maxsize of 30 for 3' loop
		else{
			region += seqstart;
			region += "region `with` maxsize 30";
		}
		//add sequence motif for 3' loop
		if(seqmotif3p != null){
			region += " `with` contains_region \"";
			region += seqmotif3p.getText();
			region += "\" )";
		}
		motifstring += " ~~~ ";
		motifstring += region;
		motifstring += " ~~- ";
		motifstring += lbases[1];
		motifstring += ") `with` basepairing\t... h)";
		//definition of second basepair
		motifstring += "\n>     motif_il";
		motifstring += String.valueOf(motifindex);
		motifstring += " = (sr <<< ";
		motifstring += lbases[2];
		motifstring += " -~~ motif";
		motifstring += String.valueOf(motifindex);
		motifstring += " ~~- ";
		motifstring += lbases[3];
		motifstring += ") `with` basepairing\t... h\n\n>     motif";
		motifstring += String.valueOf(motifindex);
		motifstring += " = ";
	}
	
	/**
	 * Processing a hairpin loop
	 * @param hairpinloop, the DOM element describing the hairpin loop
	 * @param adddangle, boolean flag indicating whether dangling bases must be taken into account
	 */
	public static void processHairpinLoop(Element hairpinloop, boolean adddangle){
		String len = hairpinloop.getAttributeValue("length");
		String minlen = hairpinloop.getAttributeValue("minlength");
		String maxlen = hairpinloop.getAttributeValue("maxlength");
		Element seqmotif = hairpinloop.getChild("seqmotif",ns);
		java.util.List<?> list = hairpinloop.getChildren("basepair",ns);
		//standard basepair parsers
		String[] lbases = {"lbase","lbase"};
		//replace with specific parsers if basepair is given
		if(list != null){
			Element basepair = (hairpinloop.getChild("basepair",ns));
			if(basepair != null){
				String bptext = basepair.getAttributeValue("bp");
				lbases[0] = "iupac_base '"+bptext.substring(0,1)+"'";
				lbases[1] = "iupac_base '"+bptext.substring(1,2)+"'";
			}
		}
		//incorporate dangling bases
		if(adddangle){
			motifstring += ("(dlr <<< loc ~~~ hairpin" + String.valueOf(motifindex) + " ~~~ loc)");
		}
		else{
			motifstring += ("hairpin" + String.valueOf(motifindex));
		}
		
		//definition of hairpin loop commences
		motifstring += ("\t... h\n>     hairpin" + String.valueOf(motifindex) + " = ");
		motifstring += "(hl <<< ";
		motifstring += lbases[0];
		motifstring += " -~~ ";
		//exact length restriction
		if(len != null){
			int l = Integer.parseInt(len)-2;
			//no sequence motif given
			if(seqmotif == null){
				motifstring += "(region `with` size ("+l+","+l+"))";
			}
			//add sequence motif
			else{
				motifstring += "((region `with` size ("+l+","+l+")) `with` contains_region \"";
				motifstring += seqmotif.getText();
				motifstring += "\")";
			}
		}
		//either minimum and/or maximum length is given
		else if(minlen != null || maxlen != null){
		    //min and max
			if(minlen != null && maxlen != null){
				int minl = Integer.parseInt(minlen)-2;
				int maxl = Integer.parseInt(maxlen)-2;
				if(seqmotif == null){
					motifstring += "(region `with` size ("+minl+","+maxl+"))";
				}
				else{
					motifstring += "((region `with` size ("+minl+","+maxl+")) `with` contains_region \"";
					motifstring += seqmotif.getText();
					motifstring += "\")";
				}
			}
			//only min
			else if(minlen != null){
				int l = Integer.parseInt(minlen)-2;
				if(seqmotif == null){
					motifstring += "(region `with` minsize "+l+")";
				}
				else{
					motifstring += "((region `with` minsize "+l+") `with` contains_region \"";
					motifstring += seqmotif.getText();
					motifstring += "\")";
				}
			}
			//only max: minimum size of 3 required
			else{
				int l = Integer.parseInt(maxlen)-2;
				if(seqmotif == null){
					motifstring += "(region `with` size (3,"+l+"))";
				}
				else{
					motifstring += "((region `with` size (3,"+l+")) `with` contains_region \"";
					motifstring += seqmotif.getText();
					motifstring += "\")";
				}
			}
		}
		//no size restrictions given: must have minimum size of 3
		else{
			if(seqmotif == null){
				motifstring += "(region `with` minsize 3)";
			}
			else{
				motifstring += "((region `with` minsize 3) `with` contains_region \"";
				motifstring += seqmotif.getText();
				motifstring += "\")";
			}
		}
		motifstring += " ~~- ";
		motifstring += lbases[1];
		motifstring += ") `with` basepairing\t... h";
		motifindex++;
	}
	
	/**
	 * Processing a ClosedStruct building block
	 * @param closedstruct, the DOM element representing the ClosedStruct
	 * @param adddangle, boolean flag indicating whether dangling bases must be taken into account
	 */
	public static void processClosedStruct(Element closedstruct, boolean adddangle){
		String minsize = closedstruct.getAttributeValue("gminsize");
		String maxsize = closedstruct.getAttributeValue("gmaxsize");
		String gsizestring = "30";
		String closedsize = "";
		//store global size , twice if smaller than 30 for internal definitions
		if(maxsize != null){
			closedsize = (" `with` maxsize " + maxsize);
			if(Integer.parseInt(maxsize) < 30){
				gsizestring = maxsize;
			}
		}
		
		//incorporate dangling bases
		if(adddangle){
			motifstring += ("(dlr <<< loc ~~~ closed" + String.valueOf(motifindex) + " ~~~ loc)");
		}
		else{
			motifstring += ("closed" + String.valueOf(motifindex));
		}
		
		//add global size restrictions
		if(minsize != null && maxsize != null){
			motifstring += " `with` size ("+minsize+","+maxsize+")";
		}
		else if(minsize != null){
			motifstring += " `with` minsize "+minsize;
		}
		else if(maxsize != null){
			motifstring += " `with` maxsize "+maxsize;
		}
		
		//definition of closedstruct
		motifstring += ("\t... h\n>     closed" + String.valueOf(motifindex) + " = ");
		motifstring += "tabulated (( stack";
		motifstring += String.valueOf(motifindex);
		motifstring += " ||| iloop";
		motifstring += String.valueOf(motifindex);
		motifstring += " ||| bulgeR";
		motifstring += String.valueOf(motifindex);
		motifstring += " ||| bulgeL";
		motifstring += String.valueOf(motifindex);
		motifstring += " |||\n>                                            (sr <<< lbase -~~ motif";
		motifstring += String.valueOf(++motifindex);
		//add overall global maximum on recursive definition
		motifstring += " ~~- lbase) `with` basepairing)" + closedsize + "\t... h )\n";
		//add maximum global size or maximum of 30 to all loop regions (depending which is smaller)
		motifstring += (">     stack" + String.valueOf(motifindex-1) + " = (sr <<< lbase -~~ closed" + String.valueOf(motifindex-1) + " ~~- lbase) `with` basepairing\t... h\n");
		motifstring += (">     iloop" + String.valueOf(motifindex-1) + " = (il <<< lbase -~~ region `with` maxsize " + gsizestring + " ~~~ stack" + String.valueOf(motifindex-1) + " ~~~ region `with` maxsize 30 ~~- lbase) `with` basepairing\t... h\n");
		motifstring += (">     bulgeR" + String.valueOf(motifindex-1) + " = (br <<< lbase -~~ stack" + String.valueOf(motifindex-1) + " ~~~ region `with` maxsize " + gsizestring + " ~~-lbase) `with` basepairing\t... h\n");
		motifstring += (">     bulgeL" + String.valueOf(motifindex-1) + " = (bl <<< lbase -~~ region `with` maxsize " + gsizestring + " ~~~ stack" + String.valueOf(motifindex-1) + " ~~-lbase) `with` basepairing\t... h\n");
		motifstring += "\n>     motif";
		motifstring += String.valueOf(motifindex);
		motifstring += " = ";
	}
	
	public static void processPseudoknot(Element pseudoknot){
	    motifstring += " tabulated ( pknot";
	    motifstring += motifindex;
	    //global size restrictions
	    String gmin = pseudoknot.getAttributeValue("gminsize");
	    String gmax = pseudoknot.getAttributeValue("gmaxsize");
	    if(gmin != null && gmax != null){
	        motifstring += " `with` size ("+gmin+","+gmax+")";
	    }
	    else if(gmin != null){
	        motifstring += " `with` minsize ";
	        motifstring += gmin;
	    }
	    else if(gmax != null){
	        motifstring += " `with` maxsize ";
	        motifstring += gmax;
	    }
	    motifstring += "\t...h)\n";
	    
	    //main pknot definition
	    motifstring += ">     pknot";
	    motifstring += motifindex;
	    motifstring += " = \\(i,j) -> [pk energy a u b v a' w b' | l <- [i+2 .. j-1], k <- [l+1 .. j-2],\n";
	    motifstring += ">     \t\t\tlet (_,alphalen) = stacklen (i,k),\n";
	    motifstring += ">     \t\t\tlet h = min(alphalen, l-i-1),\n";
	    //size of stem1
	    String lenstem1 = pseudoknot.getAttributeValue("lenstem1");
	    String minstem1 = pseudoknot.getAttributeValue("minstem1");
	    String maxstem1 = pseudoknot.getAttributeValue("maxstem1");
	    if(lenstem1 != null){
		    motifstring += ">     \t\t\th ";
	        motifstring += "== ";
	        motifstring += lenstem1;
	        motifstring += ",\n";
	    }
	    else if(minstem1 != null){
		    motifstring += ">     \t\t\th ";
	        motifstring += ">= ";
	        motifstring += minstem1;
	        motifstring += ",\n";
	        if(maxstem1 != null){
	            motifstring += ">     \t\t\th <= ";
	            motifstring += maxstem1;
	            motifstring += ",\n";
	        }
	    }
	    else if(maxstem1 != null){
		    motifstring += ">     \t\t\th ";
	        motifstring += "<= ";
	        motifstring += maxstem1;
	        motifstring += ",\n";
	    }
        if(lenstem1 == null && minstem1 == null){
            motifstring += ">     \t\t\th >= 2,\n";
        }
	    //end of size of stem1
	    motifstring += ">     \t\t\tlet (_,betalen) = stacklen (l,j),\n";
	    motifstring += ">     \t\t\tlet tmph' = min(betalen, j-k-2),\n";
	    motifstring += ">     \t\t\tlet h' = if (betalen + h) > (k-l) then k-l-h else betalen,\n";
	    //size of stem2
	    String lenstem2 = pseudoknot.getAttributeValue("lenstem2");
	    String minstem2 = pseudoknot.getAttributeValue("minstem2");
	    String maxstem2 = pseudoknot.getAttributeValue("maxstem2");
	    if(lenstem2 != null){
		    motifstring += ">     \t\t\th' ";
	        motifstring += "== ";
	        motifstring += lenstem2;
	        motifstring += ",\n";
	    }
	    else if(minstem2 != null){
		    motifstring += ">     \t\t\th' ";
	        motifstring += ">= ";
	        motifstring += minstem2;
	        motifstring += ",\n";
	        if(maxstem2 != null){
	            motifstring += ">     \t\t\th' <= ";
	            motifstring += maxstem2;
	            motifstring += ",\n";
	        }
	    }
	    else if(maxstem2 != null){
		    motifstring += ">     \t\t\th' ";
	        motifstring += "<= ";
	        motifstring += maxstem2;
	        motifstring += ",\n";
	    }
	    if(lenstem2 == null && minstem2 == null){
            motifstring += ">     \t\t\th' >= 2,\n";
        }
	    //end of size of stem2
	    //size of loop1
	    String lenloop1 = pseudoknot.getAttributeValue("lenloop1");
	    String minloop1 = pseudoknot.getAttributeValue("minloop1");
	    String maxloop1 = pseudoknot.getAttributeValue("maxloop1");
	    if(lenloop1 != null){
		    motifstring += ">     \t\t\tl - (i+h) ";
	        motifstring += "== ";
	        motifstring += lenloop1;
	        motifstring += ",\n";
	    }
	    else if(minloop1 != null){
		    motifstring += ">     \t\t\tl - (i+h) ";
	        motifstring += ">= ";
	        motifstring += minloop1;
	        motifstring += ",\n";
	        if(maxloop1 != null){
	            motifstring += ">     \t\t\tl- (i+h) <= ";
	            motifstring += maxloop1;
	            motifstring += ",\n";
	        }
	    }
	    else if(maxloop1 != null){
		    motifstring += ">     \t\t\tl - (i+h) ";
	        motifstring += "<= ";
	        motifstring += maxloop1;
	        motifstring += ",\n";
	    }
	    //end of size of loop1
	    //size of loop2
	    String lenloop2 = pseudoknot.getAttributeValue("lenloop2");
	    String minloop2 = pseudoknot.getAttributeValue("minloop2");
	    String maxloop2 = pseudoknot.getAttributeValue("maxloop2");
	    if(lenloop2 != null){
		    motifstring += ">     \t\t\t(k-h) - (l+h') ";
	        motifstring += "== ";
	        motifstring += lenloop2;
	        motifstring += ",\n";
	    }
	    else if(minloop2 != null){
		    motifstring += ">     \t\t\t(k-h) - (l+h') ";
	        motifstring += ">= ";
	        motifstring += minloop2;
	        motifstring += ",\n";
	        if(maxloop2 != null){
	            motifstring += ">     \t\t\t(k-h) - (l+h') <= ";
	            motifstring += maxloop2;
	            motifstring += ",\n";
	        }
	    }
	    else if(maxloop2 != null){
		    motifstring += ">     \t\t\t(k-h) - (l+h') ";
	        motifstring += "<= ";
	        motifstring += maxloop2;
	        motifstring += ",\n";
	    }
	    //end of size of loop2
	    //size of loop3
	    String lenloop3 = pseudoknot.getAttributeValue("lenloop3");
	    String minloop3 = pseudoknot.getAttributeValue("minloop3");
	    String maxloop3 = pseudoknot.getAttributeValue("maxloop3");
	    if(lenloop3 != null){
		    motifstring += ">     \t\t\t(j-h') - k ";
	        motifstring += "== ";
	        motifstring += lenloop3;
	        motifstring += ",\n";
	    }
	    else if(minloop3 != null){
		    motifstring += ">     \t\t\t(j-h') - k ";
	        motifstring += ">= ";
	        motifstring += minloop3;
	        motifstring += ",\n";
	        if(maxloop3 != null){
	            motifstring += ">     \t\t\t(j-h') - k <= ";
	            motifstring += maxloop3;
	            motifstring += ",\n";
	        }
	    }
	    else if(maxloop3 != null){
		    motifstring += ">     \t\t\t(j-h') - k ";
	        motifstring += "<= ";
	        motifstring += maxloop3;
	        motifstring += ",\n";
	    }
	    String folding1 = pseudoknot.getAttributeValue("straightloop1");
	    String folding2 = pseudoknot.getAttributeValue("straightloop2");
	    String folding3 = pseudoknot.getAttributeValue("straightloop3");
	    //end of size of loop3
	    motifstring += ">     \t\t\tlet (betanrg, _) = stacklen (l,j),\n";
	    motifstring += ">     \t\t\tlet (alphanrg,_) = stacklen (i,k),\n";
	    motifstring += ">     \t\t\tlet (acorrectionterm, _) = stacklen (max(0,i+h-1),min(k-h+1,n)),\n";
	    motifstring += ">     \t\t\tlet (bcorrectionterm, _) = stacklen (max(0,l+h'-1),min(j-h'+1,n)),\n";
	    motifstring += ">     \t\t\tlet middle_k = j-h',\n";
	    motifstring += ">     \t\t\tlet middle_l = i+h,\n";
	    motifstring += ">     \t\t\tlet energy = alphanrg + betanrg - acorrectionterm - bcorrectionterm,\n";
	    motifstring += ">     \t\t\ta <- region (i,i+h),\n";
	    if(folding1 == null || folding1.equals("true")){
	        motifstring += ">     \t\t\tu <- front (i+h+1,l),\n";
	    }
	    else{
	        motifstring += ">     \t\t\tu <- front";
	        motifstring += motifindex;
	        motifstring += " (i+h+1,l),\n";
	    }
	    motifstring += ">     \t\t\tb <- region (l, l+h'),\n";
	    if(folding2 == null || folding2.equals("true")){
	        motifstring += ">     \t\t\tv <- middle (l+h',k-h),\n";
	    }
	    else{
	        motifstring += ">     \t\t\tv <- middle";
	        motifstring += motifindex;
	        motifstring += " (l+h',k-h),\n";
	    }
	    motifstring += ">     \t\t\ta'<- region (k-h, k),\n";
	    if(folding3 == null || folding3.equals("true")){
	        motifstring += ">     \t\t\tw <- back   (k, j-h'-2),\n";
	    }
	    else{
	        motifstring += ">     \t\t\tw <- back";
	        motifstring += motifindex;
	        motifstring += " (k, j-h'-2),\n";
	        
	    }
	    motifstring += ">     \t\t\tb'<- region (j-h',j)\n";
	    motifstring += ">     \t\t\t]\n\n\n";
	    if(!(folding1 == null || folding1.equals("true"))){
	        motifstring += ">     front";
	        motifstring += motifindex;
	        motifstring += " = pss <<< uregion |||\n>     \tstruct\t... h\n";
	        motifstring += processExtraStruct("",null);
	    }
	    if(!(folding2 == null || folding2.equals("true"))){
	        motifstring += ">     middle";
	        motifstring += motifindex;
	        motifstring += " = pss <<< uregion |||\n>     \tstruct\t... h\n";
	        motifstring += processExtraStruct("",null);
	    }
	    if(!(folding3 == null || folding3.equals("true"))){
	        motifstring += ">     back";
	        motifstring += motifindex;
	        motifstring += " = pss <<< uregion |||\n>     \tstruct\t... h\n";
	        motifstring += processExtraStruct("",null);
	    }
	    motifindex++;
	    addStacklen();
	    motifstring += "\n\n";
	}
	
	public static void addStacklen(){
	    if(!stacklenincluded){
		    motifstring += ">     front  = ul <<< singlestrand ... h\n";
		    motifstring += ">     middle = ul <<< emptystrand ... h\n";
		    motifstring += ">     back   = ul <<< singlestrand ... h\n";
		    motifstring += ">     singlestrand = pss <<< region\n";
		    motifstring += ">     emptystrand  = pss <<< uregion\n\n";
	        motifstring += ">     stacklen = tabulated (\n";
	        motifstring += ">     \t(sum    <<< lbase +~~ stacklen ~~+ lbase) `with` basepairing |||\n";
	        motifstring += ">     \t(sumend <<< lbase +~~ (region `with` (minsize 3)) ~~+ lbase) `with` basepairing\t... h)";
	    }
	    stacklenincluded = true;
	}
	
	/**
	 * Processing a ClosedEnd building block
	 * 
	 * @param closedmultiend, DOM Element representing the CLosedEnd
	 * @param adddangle, boolean flag indicating whether dangling bases must be taken into account
	 */
	public static void processClosedMultiEnd(Element closedmultiend, boolean adddangle){
		String len = closedmultiend.getAttributeValue("length");
		String minsize = closedmultiend.getAttributeValue("minlength");
		String maxsize = closedmultiend.getAttributeValue("maxlength");

		//incorporate dangling bases
		if(adddangle){
			motifstring += "(dlr <<< loc ~~~ closed ~~~ loc)";
		}
		else{
			motifstring += "closed";
		}
		
		//add size restrictions; here: global size = local size
		if(len != null){
			motifstring += " `with` size ("+len+","+len+")";
		}
		else if(minsize != null && maxsize != null){
			motifstring += " `with` size ("+minsize+","+maxsize+")";
		}
		else if(minsize != null){
			motifstring += " `with` minsize "+minsize;
		}
		else if(maxsize != null){
			motifstring += " `with` maxsize "+maxsize;
		}
		motifstring += "\t...h";
		motifindex++;
		//process all potential building blocks included in ClosedEnd using the maximum size
		motifstring += processExtraClosed(maxsize);
	}
	
	/**
	 * Processing an extra structure part obtained through free folding of a connecting single strand
	 * @param regionstring stores any sequence motif specified for the single strand
	 * @param maxsize stores the maximum length for the overall foldable single strand
	 * @return String containting the relevant ADP code
	 */
	public static String processExtraStruct(String regionstring,String maxsize){
		String returnstring = "\n\n\n";
		if(!structincluded){
			returnstring += ">     struct = tabulated ( sadd <<< lbase -~~ struct |||\n>                                    cadd <<< closed ~~~ struct |||\n>                              addss <<< closed ~~~ uregion";
			returnstring += regionstring;
			returnstring += "\t...h)";
			returnstring += processExtraClosed(maxsize);
			structincluded = true;
		}
		return returnstring;
	}
	
	/**
	 * Processing an additional structure part obtained by free folding of single strand or within ClosedEnd
	 * @param maxsize stores the maximum size restriction effective upon the structure part
	 * @return String containing the relevant ADP code
	 */
	public static String processExtraClosed(String maxsize){
		String gsizestring = "30";
		String hpstring = "minsize 3";
		String multisize = "";
		//replace variable with maximum size: gsize only if maxsize is smaller than 30
		if(maxsize != null){
			hpstring = "size (3,"+maxsize+")";
			multisize = " `with` maxsize "+maxsize;
			if(Integer.parseInt(maxsize) < 30){
				gsizestring = maxsize;
			}
		}
		String returnstring = "\n\n";
		//closed must be defined only once, stored in global variable closedincluded
		if(!closedincluded){
		    //define rules for general folding including specific size restrictions for loops, multiloop and hairpin
			returnstring += ">     closed = tabulated (( stack ||| hairpin ||| leftB ||| rightB ||| iloop ||| multiloop )" + multisize + "\t...h)\n\n";
			//TODO: global max auch für stack nötig???
			returnstring += ">     stack = (sr <<< lbase -~~ closed ~~- lbase) `with` basepairing\t... h\n";
			returnstring += (">     iloop = (il <<< lbase -~~ region `with` maxsize " + gsizestring + " ~~~ stack ~~~ region `with` maxsize " + gsizestring + " ~~- lbase) `with` basepairing\t... h\n");
			returnstring += (">     rightB = (br <<< lbase -~~ stack ~~~ region `with` maxsize " + gsizestring + " ~~-lbase) `with` basepairing\t... h\n");
			returnstring += (">     leftB = (bl <<< lbase -~~ region `with` maxsize " + gsizestring + " ~~~ stack ~~-lbase) `with` basepairing\t... h\n\n");
			returnstring += (">     hairpin = (hl <<< lbase -~~ (region `with` " + hpstring + ") ~~- lbase) `with` basepairing\t... h\n\n");
			returnstring += ">     multiloop = (ml <<< lbase -~~ ml_taila ~~- lbase) `with` basepairing\t...h\n";
			returnstring += (">     ml_taila = tabulated (( ssadd <<< uregion ~~~ ml_nexttaila)" + multisize + "\t...h)\n");
			returnstring += (">     ml_nexttaila = tabulated (( mlcons <<< (dlr <<< loc ~~~ stack ~~~ loc) ~~~ ml_tailb)" + multisize + "\t...h)\n\n");
			returnstring += (">     ml_tailb = tabulated (( ssadd <<< uregion ~~~ ml_nexttailb)" + multisize + "\t...h)\n");
			returnstring += (">     ml_nexttailb = tabulated (( mlcons <<< (dlr <<< loc ~~~ stack ~~~ loc) ~~~ ml_tailb |||\n>                                                addssml <<< (dlr <<< loc ~~~ stack ~~~ loc) ~~~ uregion)" + multisize + "\t...h)\n\n\n");
			closedincluded = true;
		}
		return returnstring;
	}
	
	/**
	 * Processing the beginning of a multiloop building block
	 * @param multiloop, the DOM element representing the multiloop
	 * @param adddangle, boolean flag indicating whether dangling bases must be taken into account
	 * @return the global maximum size stored for the multiloop
	 */
	public static String processMultiLoopStart(Element multiloop, boolean adddangle){
		Element basepair = multiloop.getChild("basepair",ns);
		String minsize = multiloop.getAttributeValue("gminsize");
		String maxsize = multiloop.getAttributeValue("gmaxsize");
		String leftbase = "lbase";
		String rightbase = "lbase";
		//if an entrance basepair is given, specific parsers must be used
		if(basepair != null){
			leftbase = "iupac_base '" +(basepair.getAttributeValue("bp")).substring(0,1)+"'";
			rightbase = "iupac_base '"+(basepair.getAttributeValue("bp")).substring(1,2)+"'";
		}
		//incorporate dangling bases
		if(adddangle){
			motifstring += ("(dlr <<< loc ~~~ multiloop" + String.valueOf(motifindex) + " ~~~ loc)");
		}
		else{
			motifstring += ("multiloop" + String.valueOf(motifindex));
		}
		//add global size restrictions
		if(minsize != null && maxsize != null){
			motifstring += " `with` size ("+minsize+","+maxsize+")";
		}
		else if(minsize != null){
			motifstring += " `with` minsize "+minsize;
		}
		else if(maxsize != null){
			motifstring += " `with` maxsize "+maxsize;
		}
		//multiloop definition beginning
		motifstring += ("\t... h\n>     multiloop" + String.valueOf(motifindex) + " = ");
		motifstring += "(ml <<< ";
		motifstring += leftbase;
		motifstring += " -~~ ml_tail";
		motifstring += String.valueOf(tailindex);
		motifstring += " ~~- ";
		motifstring += rightbase;
		motifstring += ") `with` basepairing\t... h";
		return maxsize;
	}
	
	/**
	 * Processing a branch of a multiloop
	 * 
	 * @param mlbranch, the DOM element storing information on the branch
	 * @param seqmotif, the DOM element storing the sequence motif for the branch (can be null)
	 * @param len, String describing the exact length of the connecting loop (can be null)
	 * @param minl, String describing the minimum length of the loop (can be null)
	 * @param maxl, String describing the maximum length of the loop (can be null)
	 * @param maxsize, String describing the global maximum size effective (can be null)
	 */
	public static void processMultiLoopBranch(Element mlbranch,Element seqmotif, String len, String minl, String maxl, String maxsize){
		Element basepair = mlbranch.getChild("basepair",ns);
		String leftbase = "lbase";
		String rightbase = "lbase";
		String mlregion = "";
		String seqstart = "";
		String gsizestring = "";
		//storing the maximum size restriction for direct use in ADP
		if(maxsize != null){
			gsizestring = " `with` maxsize ";
			gsizestring += maxsize;
		}
		//include a sequence motif if given (opening bracket)
		if(seqmotif != null){
			seqstart = "( ";
		}
		//exact length for the loop
		if(len != null){
			mlregion += "(";
			mlregion += seqstart;
			mlregion += "uregion";
			mlregion += (" `with` size ("+len+","+len+")");
			mlregion += ")";
		}
		//minimum and maximum length for the loop
		else if(minl != null && maxl != null){
			mlregion += "(";
			mlregion += seqstart;
			mlregion += "uregion";
			mlregion += (" `with` size ("+minl+","+maxl+")");
			mlregion += ")";
		}
		//only minimum length
		else if(minl != null){
			mlregion += "(";
			mlregion += seqstart;
			mlregion += "uregion";
			mlregion += (" `with` minsize " + minl);
			mlregion += ")";
		}
		//only maximum length
		else if(maxl != null){
			mlregion += "(";
			mlregion += seqstart;
			mlregion += "uregion";
			mlregion += (" `with` maxsize " + maxl);
			mlregion += ")";
		}
		//no size restriction for the loop
		else{
			mlregion += seqstart;
			mlregion += "uregion";
		}
		//add the sequence motif if available
		if(seqmotif != null){
			mlregion += " `with` contains_region \"";
			mlregion += seqmotif.getText();
			mlregion += "\" )";
		}
		//use specific parser for the basepair if available
		if(basepair != null){
			leftbase = "iupac_base '" +(basepair.getAttributeValue("bp")).substring(0,1)+"'";
			rightbase = "iupac_base '" +(basepair.getAttributeValue("bp")).substring(1,2)+"'";
		}
		
		//begin multiloop branch definition
		motifstring += "\n\n>     ml_tail";
		motifstring += String.valueOf(tailindex);
		motifstring += " = tabulated ( ssadd <<< ";
		//a loop region
		motifstring += mlregion;
		motifstring += " ~~~ ";
		//next to another motif part
		motifstring += "ml_nexttail";
		motifstring += String.valueOf(++tailindex);
		motifstring += "\t... h)";

		motifstring += "\n>     ml_nexttail";
		motifstring += String.valueOf(tailindex);
		//for a new helix, dangling bases must be incorporated
		motifstring += " = tabulated (( mlcons <<< (dlr <<< loc ~~~ ml_motif_bp";
		motifstring += String.valueOf(++motifindex);
		//behind the motif part another multiloop branch follows
		motifstring += " ~~~ loc) ~~~ ml_tail";
		motifstring += String.valueOf(tailindex);
		motifstring += ")";
		//add global size restriction on new motif part
		motifstring += gsizestring;
		motifstring += "\t... h )\n>     ml_motif_bp";
		motifstring += String.valueOf(motifindex);
		//new motif part begins with exit basepair of the multiloop branch
		motifstring += " = (sr <<< ";
		motifstring += leftbase;
		motifstring += " -~~ ml_motif";
		motifstring += String.valueOf(motifindex);
		motifstring += " ~~- ";
		motifstring += rightbase;
		motifstring += ") `with` basepairing\t... h\n\n>     ml_motif";
		motifstring += String.valueOf(motifindex);
		motifstring += " = ";
	}
	
	/**
	 * Processing the last branch of a multiloop
	 * 
	 * @param mlbranch, the DOM element storing information on the branch
	 * @param seqmotif, the DOM element storing the sequence motif for the branch (can be null)
	 * @param len, String describing the exact length of the connecting loop (can be null)
	 * @param minl, String describing the minimum length of the loop (can be null)
	 * @param maxl, String describing the maximum length of the loop (can be null)
	 * @param maxsize, String describing the global maximum size effective (can be null)
	 */
	public static void processMultiLoopLastBranch(Element mlbranch,Element seqmotif, String len, String minl, String maxl, String maxsize){
		Element basepair = mlbranch.getChild("basepair",ns);
		String leftbase = "lbase";
		String rightbase = "lbase";
		String mlregion = "";		
		String seqstart = "";
		String gsizestring = "";
		//store global size restrictions
		if(maxsize != null){
			gsizestring = " `with` maxsize ";
			gsizestring += maxsize;
		}
		//bracket for sequence motif
		if(seqmotif != null){
			seqstart = "( ";
		}
		//exact length for loop region leading to the branch
		if(len != null){
			mlregion += "(";
			mlregion += seqstart;
			mlregion += "uregion";
			mlregion += (" `with` size ("+len+","+len+")");
			mlregion += ")";
		}
		//min and max length
		else if(minl != null && maxl != null){
			mlregion += "(";
			mlregion += seqstart;
			mlregion += "uregion";
			mlregion += (" `with` size ("+minl+","+maxl+")");
			mlregion += ")";
		}
		//only min length
		else if(minl != null){
			mlregion += "(";
			mlregion += seqstart;
			mlregion += "uregion";
			mlregion += (" `with` minsize " + minl);
			mlregion += ")";
		}
		//only max length
		else if(maxl != null){
			mlregion += "(";
			mlregion += seqstart;
			mlregion += "uregion";
			mlregion += (" `with` maxsize " + maxl);
			mlregion += ")";
		}
		//no size restriction for loop leading to the branch
		else{
			mlregion += seqstart;
			mlregion += "uregion";
		}
		//add sequence motif
		if(seqmotif != null){
			mlregion += " `with` contains_region \"";
			mlregion += seqmotif.getText();
			mlregion += "\" )";
		}
		//replace lbase parsers if needed
		if(basepair != null){
			leftbase = "iupac_base '" +(basepair.getAttributeValue("bp")).substring(0,1)+"'";
			rightbase = "iupac_base '" +(basepair.getAttributeValue("bp")).substring(1,2)+"'";
		}
		//definition of multiloop branch
		motifstring += "\n\n>     ml_tail";
		motifstring += String.valueOf(tailindex);
		motifstring += " = tabulated ( ssadd <<< ";
		//begins with a loop
		motifstring += mlregion;
		motifstring += " ~~~ ";
		//followed by the last motif part included in the multiloop
		motifstring += "ml_lasttail";
		motifstring += String.valueOf(++tailindex);
		motifstring += "\t... h)";
		
		motifstring += "\n>     ml_lasttail";
		motifstring += String.valueOf(tailindex);
		//the last motif part starts a new helix: dangling bases incorporated
		motifstring += " = tabulated (( addssml <<< (dlr <<< loc ~~~ ml_motif_bp";
		motifstring += String.valueOf(motifindex);
		motifstring += " ~~~ loc)";

		//behind the last motif part another loop follows closing the multiloop
		//information on this loop is stored in mlbranch element
		seqmotif = mlbranch.getChild("seqmotif",ns);
		len = mlbranch.getAttributeValue("length");
		minl = mlbranch.getAttributeValue("minlength");
		maxl = mlbranch.getAttributeValue("maxlength");
		mlregion = "";		
		seqstart = "";
		//add bracket for sequence motif
		if(seqmotif != null){
			seqstart = "( ";
		}
		//exact length for loop closing the multiloop
		if(len != null){
			mlregion += "(";
			mlregion += seqstart;
			mlregion += "uregion";
			mlregion += (" `with` size ("+len+","+len+")");
			mlregion += ")";
		}
		//min and max
		else if(minl != null && maxl != null){
			mlregion += "(";
			mlregion += seqstart;
			mlregion += "uregion";
			mlregion += (" `with` size ("+minl+","+maxl+")");
			mlregion += ")";
		}
		//only min
		else if(minl != null){
			mlregion += "(";
			mlregion += seqstart;
			mlregion += "uregion";
			mlregion += (" `with` minsize " + minl);
			mlregion += ")";
		}
		//only max
		else if(maxl != null){
			mlregion += "(";
			mlregion += seqstart;
			mlregion += "uregion";
			mlregion += (" `with` maxsize " + maxl);
			mlregion += ")";
		}
		//no size restriction for loop closing the multiloop
		else{
			mlregion += seqstart;
			mlregion += "uregion";
		}
		//add sequence motif for loop
		if(seqmotif != null){
			mlregion += " `with` contains_region \"";
			mlregion += seqmotif.getText();
			mlregion += "\" )";
		}
		motifstring += " ~~~ ";
		//the last motif part is followed by a loop region
		motifstring += mlregion;
		//upon the entire construct the global maximum is effective
		motifstring += ")" + gsizestring + "\t ... h )";
		
		//definition of the last motif part which begins with the exit basepair of the multiloop branch
		motifstring += "\n>     ml_motif_bp";
		motifstring += String.valueOf(motifindex);
		motifstring += " = (sr <<< ";
		motifstring += leftbase;
		motifstring += " -~~ ml_motif";
		motifstring += String.valueOf(motifindex);
		motifstring += " ~~- ";
		motifstring += rightbase;
		motifstring += ") `with` basepairing\t... h\n\n>     ml_motif";
		motifstring += String.valueOf(motifindex);
		motifstring += " = ";
	}
	
}
