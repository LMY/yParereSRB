package y.utils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AddressBook {
	
	private Map<String, String> emails;
	private Map<String, String> physical;
	
	public AddressBook() {
		emails = new LinkedHashMap<String, String>();
		physical = new LinkedHashMap<String, String>();
	}
	
	public String getEmail(String address) {
		return emails.get(address.toLowerCase());
	}

	public String getPhysicalAddress(String address) {
		return physical.get(address.toLowerCase());
	}

	public void add(String name, String email) {
		add(name, email, null);
	}
	
	public void add(String name, String email, String physical_address) {
		emails.put(name, email);
		if (!Utils.IsNullOrEmpty(physical_address))
			physical.put(name, physical_address);
	}
	
	
	public String getCloserName(String name) {
		
		int min = Integer.MAX_VALUE;
		String best = "";
		
		for (String s : emails.keySet()) {
			final int cur = LevenshteinDistance(s, name, false);
			
			if (min == Integer.MAX_VALUE || cur < min) {
				min = cur;
				best = s;
			}
		}
		
		return best;
	}
	
	
	public final static String DEFAULT_FILENAME = "addresses.xml";
	
	public static AddressBook read() throws Exception { return read(DEFAULT_FILENAME); }
	public void save() throws Exception { save(DEFAULT_FILENAME); }
	
	public final static String TAG_ROOT = "addresses";
	public final static String TAG_ENTRY = "address";
	public final static String TAG_NAME = "name";
	public final static String TAG_EMAIL = "email";
	public final static String TAG_PHYSICAL = "physical";

	
	public static AddressBook read(String filename) throws Exception {
		final File file = new File(filename);
		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		final Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();

		final Element root = doc.getDocumentElement();
		final NodeList nodes = root.getChildNodes();

		final AddressBook book = new AddressBook();

		for (int i = 0; i < nodes.getLength(); i++) {
			final Node node = nodes.item(i);
		
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				final String nodename = node.getNodeName();
				
				if (!nodename.equals(TAG_ENTRY))
					continue;

				final NamedNodeMap attrs = node.getAttributes();
				
				String name = "";
				String address = "";
				String physical = "";
				
				for (int k=0; k<attrs.getLength(); k++) {
					final Attr attr = (Attr) attrs.item(k);
					final String attr_value = attr.getValue();
					final String attr_name = attr.getName();
					
					if (attr_name.equals(TAG_NAME))
						name = attr_value;
					else if (attr_name.equals(TAG_EMAIL))
						address = attr_value;
					else if (attr_name.equals(TAG_PHYSICAL))
						physical = attr_value;
				}
				

				if (Utils.IsNullOrEmpty(name))
					continue;
				
				if (!Utils.IsNullOrEmpty(address))
					book.emails.put(name.toLowerCase(), address);
				
				if (!Utils.IsNullOrEmpty(physical))
					book.physical.put(name.toLowerCase(), physical);
			}
		}
		
		return book;
	}
	
	public void save(String filename) throws Exception {
		final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		final Document doc = docBuilder.newDocument();
		final Element root = doc.createElement(TAG_ROOT);
		doc.appendChild(root);
		
		for (String name : emails.keySet()) {
			final String address = emails.get(name);
			final String phis = physical.get(name);
			
			final Element e = doc.createElement(TAG_ENTRY);
			e.setAttribute(TAG_NAME, name);
			e.setAttribute(TAG_EMAIL, address);
			
			if (!Utils.IsNullOrEmpty(phis))
				e.setAttribute(TAG_PHYSICAL, phis);
			
			root.appendChild(e);
		}
		
		for (String name : physical.keySet())
			if (!emails.containsKey(name)) {
				final String phis = physical.get(name);
				
				final Element e = doc.createElement(TAG_ENTRY);
				e.setAttribute(TAG_NAME, name);
				e.setAttribute(TAG_PHYSICAL, phis);
				
				root.appendChild(e);
			}
		
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Transformer transformer = transformerFactory.newTransformer();
		final DOMSource source = new DOMSource(doc);
		final StreamResult result = new StreamResult(new File(filename));
		transformer.transform(source, result);
	}
	
	
	// these arrays will grow
	private static int[] levenshtein_v0 = null;
	private static int[] levenshtein_v1 = null;
	
	public static int LevenshteinDistance(String s, String t) {
		return LevenshteinDistance(s, t, true);
	}
	
	// modified version of: https://en.wikipedia.org/wiki/Levenshtein_distance
	// reduce number of new int[] for consecutive calls. but never releases working memory (that shouldn't be that big anyway)
	public static int LevenshteinDistance(String s, String t, boolean case_sensitive)
	{
		final int slen = s.length();
		final int tlen = t.length();
		
	    // degenerate cases
	    if (s == t) return 0;
	    if (s.length() == 0) return tlen;
	    if (t.length() == 0) return slen;

	    // create two work vectors of integer distances
	    // LMY: reallocate only when not big enough
	    final int actual_len = tlen+1;
	    
	    if (levenshtein_v0 == null || levenshtein_v0.length < actual_len) {
		    levenshtein_v0 = new int[actual_len];
		    levenshtein_v1 = new int[actual_len];
	    }
	    
	    // initialize v0 (the previous row of distances)
	    // this row is A[0][i]: edit distance for an empty s
	    // the distance is just the number of characters to delete from t
	    for (int i = 0; i < actual_len; i++)
	        levenshtein_v0[i] = i;

	    for (int i = 0; i < slen; i++) {
	        // calculate v1 (current row distances) from the previous row v0

	        // first element of v1 is A[i+1][0]
	        //   edit distance is delete (i+1) chars from s to match empty t
	        levenshtein_v1[0] = i + 1;

	        // use formula to fill in the rest of the row
	        for (int j = 0; j < tlen; j++) {
	            final int cost = case_sensitive ?
	            					(s.charAt(i) == t.charAt(j) ? 0 : 1) :
	            					(Character.toLowerCase(s.charAt(i)) == Character.toLowerCase(t.charAt(j)) ? 0 : 1);
	            					
	            levenshtein_v1[j + 1] = Math.min(levenshtein_v1[j] + 1, Math.min(levenshtein_v0[j + 1] + 1, levenshtein_v0[j] + cost));
	        }

	        // copy v1 (current row) to v0 (previous row) for next iteration
	        for (int j = 0; j < actual_len; j++)
	            levenshtein_v0[j] = levenshtein_v1[j];
	    }

	    return levenshtein_v1[tlen];
	}
}
