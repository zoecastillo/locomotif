package rnaeditor;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.output.DOMOutputter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;



/**
 * @author Janina
 *
 * This class implements the language output for the RnaSTructures.
 * Both Shape representation as well as XML and JDOM are possible.
 * 
 */
public class Translator {

	public static final int SHAPE_LANG = 0;
	public static final int XML_LANG = 1;
	public static final int JDOM_LANG = 2;
	public static final int INFO_LANG = 3;
	
	public static Document doc;
	public static Namespace ns = Namespace.getNamespace("de:unibi:techfak:bibiserv:rnaeditor");
	public static Element rootelement;
	public static Element lastelement;
	
	/**
	 * This method gives the representation of the 5' part of a structure element
	 * 
	 * @param bb, the building block that is to be translated
	 * @param languagetype, the language (0=shapes, 1=xml, 2=jdom)
	 * @param index, which part of a multiloop
	 * @return the String represenation
	 */
	public static String getLeftContent(BuildingBlock bb, int languagetype, int index)
	{
		//SHAPES
		if(languagetype == SHAPE_LANG)
		{
			if(bb instanceof Stem)
			{
				return "[";
			}
			else if(bb instanceof Bulge)
			{
				if(((Bulge)bb).getBulgeLoc())
				{
					return "[_[";
				}
				else
				{
					return "[[";
				}
			}
			else if(bb instanceof InternalLoop)
			{
				return "[_[";
			}
			else if(bb instanceof HairpinLoop)
			{
				return "[_]";
			}
			else if(bb instanceof MultiLoop)
			{
				return "[_";
			}
			else if(bb instanceof SingleStrand)
			{
				return "_";
			}
			else if(bb instanceof ClosedStruct)
			{
				return " {- ";
			}
			else if(bb instanceof ClosedMultiEnd)
			{
				return " { --- } ";
			}
			else if(bb instanceof Pseudoknot)
			{
			    return "\u251C_\u255F_\u2524_\u2562";
			}
		}
		//XML
		else if(languagetype == XML_LANG)
		{
			String xmlstring ="";
			if(bb instanceof Stem)
			{
				xmlstring += "<neighbor>\n<stem";
				if(!bb.getIsDefaultLength() && bb.getIsExactLength()){
					xmlstring += " length='";
					xmlstring += String.valueOf(bb.getLength());
					xmlstring += "'";
				}
				else if(!bb.getIsExactLength()){
					if(!bb.getIsDefaultMin()){
						xmlstring += " minlength='";
						xmlstring += String.valueOf(bb.getMinLength());
						xmlstring += "'";
					}
					if(!bb.getIsDefaultMax()){
						xmlstring += " maxlength='";
						xmlstring += String.valueOf(bb.getMaxLength());
						xmlstring += "'";
					}
				}
				xmlstring += ">\n";
			}
			else if(bb instanceof Bulge)
			{
				xmlstring += "<neighbor>\n<bulge";
				if(!bb.getIsDefaultLength() && bb.getIsExactLength()){
					xmlstring += " length='";
					xmlstring += String.valueOf(bb.getLength());
					xmlstring += "'";
				}
				else if(!bb.getIsExactLength()){
					if(!bb.getIsDefaultMin()){
						xmlstring += " minlength='";
						xmlstring += String.valueOf(bb.getMinLength());
						xmlstring += "'";
					}
					if(!bb.getIsDefaultMax()){
						xmlstring += " maxlength='";
						xmlstring += String.valueOf(bb.getMaxLength());
						xmlstring += "'";
					}
				}
				xmlstring += " bulge-pos='";
				if(((Bulge)bb).getBulgeLoc()){
					xmlstring += "5prime'";
				}
				else{
					xmlstring += "3prime'";
				}
				xmlstring += ">\n";
			}
			else if(bb instanceof InternalLoop)
			{
				InternalLoop il = (InternalLoop)bb;
				xmlstring += "<neighbor>\n<internalloop";
				if(!il.getIsDefault53Length() && il.getIsExact53Length()){
					xmlstring += " length5ploop='";
					xmlstring += String.valueOf(il.get53Length());
					xmlstring += "'";
				}
				else if(!il.getIsExact53Length()){
					if(!il.getIsDefault53Min()){
						xmlstring += " minlength5ploop='";
						xmlstring += String.valueOf(il.getMin53Length());
						xmlstring += "'";
					}
					if(!il.getIsDefault53Max()){
						xmlstring += " maxlength5ploop='";
						xmlstring += String.valueOf(il.getMax53Length());
						xmlstring += "'";
					}
				}
				if(!il.getIsDefault35Length() && il.getIsExact35Length()){
					xmlstring += " length3ploop='";
					xmlstring += String.valueOf(il.get35Length());
					xmlstring += "'";
				}
				else if(!il.getIsExact35Length()){
					if(!il.getIsDefault35Min()){
						xmlstring += " minlength3ploop='";
						xmlstring += String.valueOf(il.getMin35Length());
						xmlstring += "'";
					}
					if(!il.getIsDefault35Max()){
						xmlstring += " maxlength3ploop='";
						xmlstring += String.valueOf(il.getMax35Length());
						xmlstring += "'";
					}
				}
				xmlstring += ">\n";
			}
			else if(bb instanceof HairpinLoop)
			{
				String motif = ((HairpinLoop)bb).getLoopMotif();
				String bp = ((HairpinLoop)bb).getBasepairString();
				xmlstring += "<neighbor>\n<hairpinloop";
				if(!bb.getIsDefaultLength() && bb.getIsExactLength()){
					xmlstring += " length='";
					xmlstring += String.valueOf(bb.getLength());
					xmlstring += "'";
				}
				else if(!bb.getIsExactLength()){
					if(!bb.getIsDefaultMin()){
						xmlstring += " minlength='";
						xmlstring += String.valueOf(bb.getMinLength());
						xmlstring += "'";
					}
					if(!bb.getIsDefaultMax()){
						xmlstring += " maxlength='";
						xmlstring += String.valueOf(bb.getMaxLength());
						xmlstring += "'";
					}
				}
				xmlstring += ">\n";
				if(motif != null){
					xmlstring += "<seqmotif>";
					xmlstring += motif;
					xmlstring += "</seqmotif>\n";
				}
				if(bp != null){
					xmlstring += "<basepair bp='";
					xmlstring += bp;
					xmlstring += "'/>\n";
				}
				xmlstring += "</hairpinloop>\n</neighbor>\n";
			}
			else if(bb instanceof MultiLoop)
			{
				xmlstring += "<neighbor>\n<multiloop";
				if(!bb.getIsDefaultLength() && bb.getIsExactLength()){
					xmlstring += " length='";
					xmlstring += String.valueOf(bb.getLength());
					xmlstring += "'";
				}
				else if(!bb.getIsExactLength()){
					if(!bb.getIsDefaultMin()){
						xmlstring += " minlength='";
						xmlstring += String.valueOf(bb.getMinLength());
						xmlstring += "'";
					}
					if(!bb.getIsDefaultMax()){
						xmlstring += " maxlength='";
						xmlstring += String.valueOf(bb.getMaxLength());
						xmlstring += "'";
					}
				}
				xmlstring += ">\n";
			}
			else if(bb instanceof SingleStrand)
			{
				String motif = ((SingleStrand)bb).getStrandMotif();
				xmlstring += "<single";
				if(!bb.getIsDefaultLength() && bb.getIsExactLength()){
					xmlstring += " length='";
					xmlstring += String.valueOf(bb.getLength());
					xmlstring += "'";
				}
				else if(!bb.getIsExactLength()){
					if(!bb.getIsDefaultMin()){
						xmlstring += " minlength='";
						xmlstring += String.valueOf(bb.getMinLength());
						xmlstring += "'";
					}
					if(!bb.getIsDefaultMax()){
						xmlstring += " maxlength='";
						xmlstring += String.valueOf(bb.getMaxLength());
						xmlstring += "'";
					}
				}
				xmlstring += ">\n";
				if(motif != null){
					xmlstring += "<seqmotif>";
					xmlstring += motif;
					xmlstring += "</seqmotif>\n";
				}
				xmlstring += "</single>\n";
			}
			else if(bb instanceof ClosedStruct)
			{
				xmlstring += "<neighbor>\n<closedstruct>\n";
			}	
			return xmlstring;
		}
		//JDOM
		else if(languagetype == JDOM_LANG)
		{
			Element nb = new Element("neighbor",ns);
			if(bb instanceof Stem)
			{
				lastelement.addContent(nb);
				Element st = new Element("stem",ns);
				nb.addContent(st);
				if(!bb.getIsDefaultLength() && bb.getIsExactLength()){
					st.setAttribute("length",String.valueOf(bb.getLength()));
				}
				else if(!bb.getIsExactLength()){
					if(!bb.getIsDefaultMin()){	
						st.setAttribute("minlength",String.valueOf(bb.getMinLength()));
					}
					if(!bb.getIsDefaultMax()){	
						st.setAttribute("maxlength",String.valueOf(bb.getMaxLength()));
					}
				}
				if(bb.hasGlobalLength()){
					String gminsize = bb.getMinGlobalLength();
					String gmaxsize = bb.getMaxGlobalLength();
					if(gminsize != null){
						st.setAttribute("gminsize",gminsize);
					}
					if(gmaxsize != null){
						st.setAttribute("gmaxsize",gmaxsize);
					}
				}
				st.setAttribute("allowinterrupt",String.valueOf(((Stem)bb).getAllowInterrupt()));
				lastelement = st;
			}
			else if(bb instanceof Bulge)
			{
				lastelement.addContent(nb);
				Element bl = new Element("bulge",ns);
				nb.addContent(bl);
				if(!bb.getIsDefaultLength() && bb.getIsExactLength()){
					bl.setAttribute("length",String.valueOf(bb.getLength()-4));
				}
				else if(!bb.getIsExactLength()){
					if(!bb.getIsDefaultMin()){	
						bl.setAttribute("minlength",String.valueOf(bb.getMinLength()-4));
						
					}
					if(!bb.getIsDefaultMax()){	
						bl.setAttribute("maxlength",String.valueOf(bb.getMaxLength()-4));
						
					}
				}
				if(((Bulge)bb).getBulgeLoc()){
					bl.setAttribute("bulge-pos","5prime");
				}
				else{
					bl.setAttribute("bulge-pos","3prime");
				}
				if(bb.hasGlobalLength()){
					String gminsize = bb.getMinGlobalLength();
					String gmaxsize = bb.getMaxGlobalLength();
					if(gminsize != null){
						bl.setAttribute("gminsize",gminsize);
					}
					if(gmaxsize != null){
						bl.setAttribute("gmaxsize",gmaxsize);
					}
				}
				lastelement = bl;
			}
			else if(bb instanceof InternalLoop)
			{
				lastelement.addContent(nb);
				InternalLoop il = (InternalLoop)bb;
				Element ile = new Element("internalloop",ns);
				nb.addContent(ile);
				if(!il.getIsDefault53Length() && il.getIsExact53Length()){
					ile.setAttribute("length5ploop",String.valueOf(il.get53Length()));
				}
				else if(!il.getIsExact53Length()){
					if(!il.getIsDefault53Min()){
						ile.setAttribute("minlength5ploop",String.valueOf(il.getMin53Length()));
					}
					if(!il.getIsDefault53Max()){
						ile.setAttribute("maxlength5ploop",String.valueOf(il.getMax53Length()));
					}
				}
				if(!il.getIsDefault35Length() && il.getIsExact35Length()){
					ile.setAttribute("length3ploop",String.valueOf(il.get35Length()));
				}
				else if(!il.getIsExact35Length()){
					if(!il.getIsDefault35Min()){
						ile.setAttribute("minlength3ploop",String.valueOf(il.getMin35Length()));
					}
					if(!il.getIsDefault35Max()){
						ile.setAttribute("maxlength3ploop",String.valueOf(il.getMax35Length()));
					}
				}
				if(bb.hasGlobalLength()){
					String gminsize = bb.getMinGlobalLength();
					String gmaxsize = bb.getMaxGlobalLength();
					if(gminsize != null){
						ile.setAttribute("gminsize",gminsize);
					}
					if(gmaxsize != null){
						ile.setAttribute("gmaxsize",gmaxsize);
					}
				}
				lastelement = ile;
			}
			else if(bb instanceof HairpinLoop)
			{
				lastelement.addContent(nb);
				String motif = ((HairpinLoop)bb).getLoopMotif();
				String bp = ((HairpinLoop)bb).getBasepairString();
				Element hp = new Element("hairpinloop",ns);
				nb.addContent(hp);
				if(!bb.getIsDefaultLength() && bb.getIsExactLength()){
					hp.setAttribute("length",String.valueOf(bb.getLength()));
				}
				else if(!bb.getIsExactLength()){
					if(!bb.getIsDefaultMin()){
						hp.setAttribute("minlength",String.valueOf(bb.getMinLength()));
					}
					if(!bb.getIsDefaultMax()){
						hp.setAttribute("maxlength",String.valueOf(bb.getMaxLength()));
					}
				}
				if(motif != null){
					Element sm = new Element("seqmotif",ns);
					sm.addContent(motif);
					hp.addContent(sm);
				}
				if(bp != null){
					Element bpe = new Element("basepair",ns);
					bpe.setAttribute("bp",bp);
					hp.addContent(bpe);
				}
				lastelement = (Element)(hp.getParent()).getParent();
			}
			else if(bb instanceof MultiLoop)
			{
				lastelement.addContent(nb);
				Element ml = new Element("multiloop",ns);
				nb.addContent(ml);
				MultiLoop multi = (MultiLoop) bb;
				if(!multi.getOrientation()){
					index = multi.getNextAvailable(index);
				}
				if(multi.getExactLoopLength(index) != -1){
					ml.setAttribute("length",String.valueOf(multi.getExactLoopLength(index)));
				}
				else{
					if(multi.getMinLoopLength(index) != -1){
						ml.setAttribute("minlength",String.valueOf(multi.getMinLoopLength(index)));
					}
					if(multi.getMaxLoopLength(index) != -1){
						ml.setAttribute("maxlength",String.valueOf(multi.getMaxLoopLength(index)));
					}
				}
				if(bb.hasGlobalLength()){
					String gminsize = bb.getMinGlobalLength();
					String gmaxsize = bb.getMaxGlobalLength();
					if(gminsize != null){
						ml.setAttribute("gminsize",gminsize);
					}
					if(gmaxsize != null){
						ml.setAttribute("gmaxsize",gmaxsize);
					}
				}
				lastelement = ml;
			}
			else if(bb instanceof SingleStrand)
			{
				String motif = ((SingleStrand)bb).getStrandMotif();
				Element s;
				if(((SingleStrand)bb).getIsConnector()){
				    s = new Element("singleconnect",ns);
				}
				else{
				    s = new Element("single",ns);
				}
				lastelement.addContent(s);
				if(!bb.getIsDefaultLength() && bb.getIsExactLength()){
					s.setAttribute("length",String.valueOf(bb.getLength()));
				}
				else if(!bb.getIsExactLength()){
					if(!bb.getIsDefaultMin()){
						s.setAttribute("minlength",String.valueOf(bb.getMinLength()));
					}
					if(!bb.getIsDefaultMax()){
						s.setAttribute("maxlength",String.valueOf(bb.getMaxLength()));
					}
				}
				if(motif != null){
					Element sm = new Element("seqmotif",ns);
					sm.addContent(motif);
					s.addContent(sm);
				}
				s.setAttribute("straight", String.valueOf(((SingleStrand)bb).getIsStraight()));
			}
			else if(bb instanceof ClosedStruct)
			{
				lastelement.addContent(nb);
				Element cs = new Element("closedstruct",ns);
				nb.addContent(cs);
				if(bb.hasGlobalLength()){
					String gminsize = bb.getMinGlobalLength();
					String gmaxsize = bb.getMaxGlobalLength();
					if(gminsize != null){
						cs.setAttribute("gminsize",gminsize);
					}
					if(gmaxsize != null){
						cs.setAttribute("gmaxsize",gmaxsize);
					}
				}
				lastelement = cs;
			}	
			else if(bb instanceof ClosedMultiEnd)
			{
				lastelement.addContent(nb);
				Element cme = new Element("closedmultiend",ns);
				nb.addContent(cme);
				if(!bb.getIsDefaultLength() && bb.getIsExactLength()){
					cme.setAttribute("length",String.valueOf(bb.getLength()));
				}
				else if(!bb.getIsExactLength()){
					if(!bb.getIsDefaultMin()){	
						cme.setAttribute("minlength",String.valueOf(bb.getMinLength()));
					}
					if(!bb.getIsDefaultMax()){	
						cme.setAttribute("maxlength",String.valueOf(bb.getMaxLength()));
					}
				}
				lastelement = (Element)(cme.getParent()).getParent();
			}		
			else if(bb instanceof Pseudoknot)
			{
				lastelement.addContent(nb);
				Element ps = new Element("pseudoknot",ns);
				nb.addContent(ps);
				Pseudoknot pknot = (Pseudoknot) bb;
				if(!bb.getIsDefaultLength() && bb.getIsExactLength()){
					ps.setAttribute("gsize",String.valueOf(bb.getLength()));
				}
				else if(!bb.getIsExactLength()){
					if(!bb.getIsDefaultMin()){	
						ps.setAttribute("gminsize",String.valueOf(bb.getMinLength()));
					}
					if(!bb.getIsDefaultMax()){	
						ps.setAttribute("gmaxsize",String.valueOf(bb.getMaxLength()));
					}
				}
				if(!pknot.isStem1default() && pknot.isStem1exact()){
				    ps.setAttribute("lenstem1",String.valueOf(pknot.getStem1len()));
				}
				else if(!pknot.isStem1exact()){
				    if(pknot.getStem1min() > 0){
				        ps.setAttribute("minstem1",String.valueOf(pknot.getStem1min()));
				    }
				    if(pknot.getStem1max() > 0){
				        ps.setAttribute("maxstem1",String.valueOf(pknot.getStem1max()));
				    }
				}
				if(!pknot.isStem2default() && pknot.isStem2exact()){
				    ps.setAttribute("lenstem2",String.valueOf(pknot.getStem2len()));
				}
				else if(!pknot.isStem2exact()){
				    if(pknot.getStem2min() > 0){
				        ps.setAttribute("minstem2",String.valueOf(pknot.getStem2min()));
				    }
				    if(pknot.getStem2max() > 0){
				        ps.setAttribute("maxstem2",String.valueOf(pknot.getStem2max()));
				    }
				}
				if(pknot.isLoop1default()){
				    ps.setAttribute("straightloop1",String.valueOf(pknot.isLoop1straight()));
				}
				else if(pknot.isLoop1exact()){
				    ps.setAttribute("lenloop1",String.valueOf(pknot.getLoop1len()));
				}
				else{
				    if(pknot.getLoop1min() > 0){
				        ps.setAttribute("minloop1",String.valueOf(pknot.getLoop1min()));
				    }
				    if(pknot.getLoop1max() > 0){
				        ps.setAttribute("maxloop1",String.valueOf(pknot.getLoop1max()));
				    }
				}
				if(pknot.isLoop2default()){
				    ps.setAttribute("straightloop2",String.valueOf(pknot.isLoop2straight()));
				}
				else if(pknot.isLoop2exact()){
				    ps.setAttribute("lenloop2",String.valueOf(pknot.getLoop2len()));
				}
				else{
				    if(pknot.getLoop2min() > 0){
				        ps.setAttribute("minloop2",String.valueOf(pknot.getLoop2min()));
				    }
				    if(pknot.getLoop2max() > 0){
				        ps.setAttribute("maxloop2",String.valueOf(pknot.getLoop2max()));
				    }
				}
				if(pknot.isLoop3default()){
				    ps.setAttribute("straightloop3",String.valueOf(pknot.isLoop3straight()));
				}
				else if(pknot.isLoop3exact()){
				    ps.setAttribute("lenloop3",String.valueOf(pknot.getLoop3len()));
				}
				else{
				    if(pknot.getLoop3min() > 0){
				        ps.setAttribute("minloop3",String.valueOf(pknot.getLoop3min()));
				    }
				    if(pknot.getLoop3max() > 0){
				        ps.setAttribute("maxloop3",String.valueOf(pknot.getLoop3max()));
				    }
				}
			}	
			return "";
		}
		if(languagetype == INFO_LANG){
			return bb.getInfo() + "<br><br>";
		}
		return null;
	}
	
	/**
	 * This method gives the representation of the 3' part of a structure element
	 * 
	 * @param bb, the building block that is to be translated
	 * @param languagetype, the language (0=shapes, 1=xml, 2=jdom)
	 * @param index, which part of a multiloop
	 * @return the String represenation
	 */
	public static String getRightContent(BuildingBlock bb, int languagetype, int index)
	{
		//SHAPES
		if(languagetype == SHAPE_LANG)
		{
			if(bb instanceof Stem)
			{
				return "]";
			}
			else if(bb instanceof Bulge)
			{
				if(((Bulge)bb).getBulgeLoc())
				{
					return "]]";
				}
				else
				{
					return "]_]";
				}	
			}
			else if(bb instanceof InternalLoop)
			{
				return "]_]";
			}
			else if(bb instanceof MultiLoop)
			{
				return "]";
			}
			else if(bb instanceof ClosedStruct)
			{
				return " -} ";
			}	
		}
		//XML
		else if(languagetype == XML_LANG)
		{
			String xmlstring = "";
			if(bb instanceof Stem)
			{
				String motif = ((Stem)bb).getSeqMotif();
				Basepair[] bps = ((Stem)bb).getBasepairs();

				if(motif != null){
					xmlstring += "<seqmotif>";
					xmlstring += motif;
					xmlstring += "</seqmotif>\n";
					xmlstring += "<motifloc>";
					if(((Stem)bb).getIsMotifOn53Strand()){
						xmlstring += "5prime";
					}
					else{
						xmlstring += "3prime";
					}
					xmlstring += "</motifloc>\n";
				}
				else if(bps != null){
					for(int i=0;i<bps.length;i++){
						xmlstring += "<basepair bp='";
						xmlstring += bps[i].getString();
						xmlstring += "'/>\n";
					}
				}
				xmlstring += "</stem>\n</neighbor>\n";
			}
			else if(bb instanceof Bulge)
			{
				String motif = ((Bulge)bb).getLoopMotif();
				String bp1 = ((Bulge)bb).getBasepairString(0);
				String bp2 = ((Bulge)bb).getBasepairString(1);
				if(motif != null){
					xmlstring += "<seqmotif>";
					xmlstring += motif;
					xmlstring += "</seqmotif>\n";
				}
				if(bp1 != null){
					xmlstring += "<basepair bp='";
					xmlstring += bp1;
					xmlstring += "' bp-pos='5primeend' />\n";
				}
				if(bp2 != null){
					xmlstring += "<basepair bp='";
					xmlstring += bp2;
					xmlstring += "' bp-pos='3primeend' />\n";
				}
				xmlstring += "</bulge>\n</neighbor>\n";
			}
			else if(bb instanceof InternalLoop)
			{
				String motif5ploop = ((InternalLoop)bb).getLoop53Motif();
				String motif3ploop = ((InternalLoop)bb).getLoop35Motif();
				String bp1 = ((InternalLoop)bb).getBasepairString(0);
				String bp2 = ((InternalLoop)bb).getBasepairString(1);
				if(motif5ploop != null){
					xmlstring += "<seqmotif5ploop>";
					xmlstring += motif5ploop;
					xmlstring += "</seqmotif5ploop>\n";
				}
				if(motif3ploop != null){
					xmlstring += "<seqmotif3ploop>";
					xmlstring += motif3ploop;
					xmlstring += "</seqmotif3ploop>\n";
				}
				if(bp1 != null){
					xmlstring += "<basepair bp='";
					xmlstring += bp1;
					xmlstring += "' bp-pos='5primeend' />\n";
				}
				if(bp2 != null){
					xmlstring += "<basepair bp='";
					xmlstring += bp2;
					xmlstring += "' bp-pos='3primeend' />\n";
				}
				xmlstring += "</internalloop>\n</neighbor>\n";
			}
			else if(bb instanceof MultiLoop)
			{
				String motif = ((MultiLoop)bb).getLoopMotif(index);
				String bp = ((MultiLoop)bb).getBasepairString(index);
				if(motif != null){
					xmlstring += "<seqmotif>";
					xmlstring += motif;
					xmlstring += "</seqmotif>\n";
				}
				if(bp != null){
					xmlstring += "<basepair bp='";
					xmlstring += bp;
					xmlstring += "'/>\n";
				}
				xmlstring += "</multiloop>\n</neighbor>\n";
			}
			else if(bb instanceof ClosedStruct)
			{
				xmlstring += "</closedstruct>\n</neighbor>\n";
			}	
			return xmlstring;
		}
		//JDOM
		else if(languagetype == JDOM_LANG)
		{
			if(bb instanceof Stem)
			{
				String motif = ((Stem)bb).getSeqMotif();
				Basepair[] bps = ((Stem)bb).getBasepairs();

				if(motif != null){
					Element sm = new Element("seqmotif",ns);
					sm.addContent(motif);
					lastelement.addContent(sm);
					Element ml = new Element("motifloc",ns);
					if(((Stem)bb).getIsMotifOn53Strand()){
						ml.addContent("5prime");
					}
					else{
						ml.addContent("3prime");
					}
					lastelement.addContent(ml);
				}
				else if(bps != null){
					for(int i=0;i<bps.length;i++){
						Element bpe = new Element("basepair",ns);
						bpe.setAttribute("bp",bps[i].getString());
						lastelement.addContent(bpe);
					}
				}
				lastelement = (Element)(lastelement.getParent()).getParent();
			}
			else if(bb instanceof Bulge)
			{
				String motif = ((Bulge)bb).getLoopMotif();
				String bp1 = ((Bulge)bb).getBasepairString(0);
				String bp2 = ((Bulge)bb).getBasepairString(1);
				if(motif != null){
					Element sm = new Element("seqmotif",ns);
					sm.addContent(motif);
					lastelement.addContent(sm);
				}
				if(bp1 != null){
					Element bpe = new Element("basepair",ns);
					bpe.setAttribute("bp",bp1);
					bpe.setAttribute("bp-pos","5primeend");
					lastelement.addContent(bpe);
				}
				if(bp2 != null){
					Element bpe = new Element("basepair",ns);
					bpe.setAttribute("bp",bp2);
					bpe.setAttribute("bp-pos","3primeend");
					lastelement.addContent(bpe);
				}
				lastelement = (Element)(lastelement.getParent()).getParent();
			}
			else if(bb instanceof InternalLoop)
			{
				String motif5ploop = ((InternalLoop)bb).getLoop53Motif();
				String motif3ploop = ((InternalLoop)bb).getLoop35Motif();
				String bp1 = ((InternalLoop)bb).getBasepairString(0);
				String bp2 = ((InternalLoop)bb).getBasepairString(1);
				if(motif5ploop != null){
					Element sm = new Element("seqmotif5ploop",ns);
					sm.addContent(motif5ploop);
					lastelement.addContent(sm);
				}
				if(motif3ploop != null){
					Element sm = new Element("seqmotif3ploop",ns);
					sm.addContent(motif3ploop);
					lastelement.addContent(sm);
				}
				if(bp1 != null){
					Element bpe = new Element("basepair",ns);
					bpe.setAttribute("bp",bp1);
					bpe.setAttribute("bp-pos","5primeend");
					lastelement.addContent(bpe);
				}
				if(bp2 != null){
					Element bpe = new Element("basepair",ns);
					bpe.setAttribute("bp",bp2);
					bpe.setAttribute("bp-pos","3primeend");
					lastelement.addContent(bpe);
				}
				lastelement = (Element)(lastelement.getParent()).getParent();
			}
			else if(bb instanceof MultiLoop)
			{
				String bp = ((MultiLoop)bb).getBasepairString(index);
				if(!bb.getOrientation()){
					index = ((MultiLoop)bb).getNextAvailable(index);
				}
				String motif = ((MultiLoop)bb).getLoopMotif(index);
				if(motif != null){
					Element sm = new Element("seqmotif",ns);
					sm.addContent(motif);
					lastelement.addContent(sm);
				}
				if(bp != null){
					Element bpe = new Element("basepair",ns);
					bpe.setAttribute("bp",bp);
					lastelement.addContent(bpe);
				}
				lastelement = (Element)(lastelement.getParent()).getParent();
			}
			else if(bb instanceof ClosedStruct)
			{
				lastelement = (Element)(lastelement.getParent()).getParent();
			}
			return "";
		}
		if(languagetype == INFO_LANG){
			return "";
		}
		return null;
	}
	
	/**
	 * This Method translates a part of the Multiloop (internal exit opening after 5')
	 * 
	 * @param languagetype, 0=shapes, 1=xml, 2=jdom
	 * @param index, which part of the multiloop
	 * @return the String representation
	 */
	public static String getMLExitOpen(MultiLoop multi, int languagetype, int index)
	{
		if(languagetype == SHAPE_LANG)
		{
			return "[";
		}
		else if(languagetype == XML_LANG)
		{
			return "<multiloop-branch>\n";
		}
		else if(languagetype == JDOM_LANG){
			Element mlb = new Element("multiloop-branch",ns);
			if(!multi.getOrientation()){
				index = multi.getNextAvailable(index);
			}
			if(multi.getExactLoopLength(index) != -1){
				mlb.setAttribute("length",String.valueOf(multi.getExactLoopLength(index)));
			}
			else{
				if(multi.getMinLoopLength(index) != -1){
					mlb.setAttribute("minlength",String.valueOf(multi.getMinLoopLength(index)));
				}
				if(multi.getMaxLoopLength(index) != -1){
					mlb.setAttribute("maxlength",String.valueOf(multi.getMaxLoopLength(index)));
				}
			}
			lastelement.addContent(mlb);
			lastelement = mlb;
			return "";
		}
		if(languagetype == INFO_LANG){
			return "";
		}
		return null;
	}
	
	/**
	 * This Method translates a part of the Multiloop (internal exit closing after 5')
	 * 
	 * @param languagetype, 0=shapes, 1=xml, 2=jdom
	 * @param index, which part of the multiloop
	 * @return the String representation
	 */
	public static String getMLExitClose(MultiLoop ml, int languagetype, int index)
	{
		if(languagetype == SHAPE_LANG)
		{
			return "]_";
		}
		else if(languagetype == XML_LANG)
		{
			String bp = ml.getBasepairString(index);
			if(!ml.getOrientation()){
				index += 1;
				index = index % 8;
			}
			String motif = ml.getLoopMotif(index);
			String xmlstring = "";
			if(motif != null){
				xmlstring += "<seqmotif>";
				xmlstring += motif;
				xmlstring += "</seqmotif>\n";
			}
			if(bp != null){
				xmlstring += "<basepair bp='";
				xmlstring += bp;
				xmlstring += "'/>\n";
			}
			xmlstring += "</multiloop-branch>\n";
			return xmlstring;
		}
		else if(languagetype == JDOM_LANG)
		{
			String bp = ml.getBasepairString(index);
			if(!ml.getOrientation()){
				index = ml.getNextAvailable(index);
			}
			String motif = ml.getLoopMotif(index);
			if(motif != null){
				Element sm = new Element("seqmotif",ns);
				sm.addContent(motif);
				lastelement.addContent(sm);
			}
			if(bp != null){
				Element bpe = new Element("basepair",ns);
				bpe.setAttribute("bp",bp);
				lastelement.addContent(bpe);
			}
			lastelement = (Element)lastelement.getParent();
			return "";
		}
		if(languagetype == INFO_LANG){
			return "";
		}
		return null;
	}
	
	/**
	 * The start of an XML document
	 * 
	 * @return String representing the starting line of the document.
	 */
	public static String beginXML()
	{
		return "<?xml version='1.0'?>\n<rnamotif>\n";
	}
	
	/**
	 * The start of an JDOM document: the document with its rootelement is
	 * initialized.
	 * 
	 */
	public static void beginJDOM(boolean localsearch,int minsize, int maxsize){
		rootelement = new Element("rnamotif");
		if(localsearch){
			rootelement.setAttribute("searchtype","local");
		}
		else{
			rootelement.setAttribute("searchtype","global");
		}
		if(minsize > 0){
			rootelement.setAttribute("gminsize",String.valueOf(minsize));
		}
		if(maxsize > 0){
			rootelement.setAttribute("gmaxsize",String.valueOf(maxsize));
		}
		doc = new Document(rootelement);
		rootelement.setNamespace(ns);
		lastelement = rootelement;
	}
	
	/**
	 * The end of an XML document
	 * 
	 * @return String representing the end line of the document.
	 */
	public static String endXML()
	{
		return "</rnamotif>";
	}
	
	/**
	 * This method gives the DomOutput as an Object 
	 * 
	 * @return Object holding the output of the DOMOutputter
	 */
	public static org.w3c.dom.Document getDOMOutput(){
		DOMOutputter out = new DOMOutputter("XercesDOMAdapter");
		org.w3c.dom.Document domdoc = null;
		try{
			domdoc = out.output(doc);
		}
		catch(JDOMException jde){
			jde.printStackTrace();
			System.exit(0);
		}
		return domdoc;
	}
	
	/**
	 * This method gives an indented and well-readable version of the current jdom
	 * tree 
	 * 
	 * @return the String holding the pretty print XML output.
	 */
	public static String getPrettyPrint(){
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		return out.outputString(doc);
	}
	
}
