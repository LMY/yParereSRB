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
		return emails.get(address);
	}

	public String getPhysicalAddress(String address) {
		return physical.get(address);
	}

	public void add(String name, String email) {
		add(name, email, null);
	}
	
	public void add(String name, String email, String physical_address) {
		emails.put(name, email);
		if (!Utils.IsNullOrEmpty(physical_address))
			physical.put(name, physical_address);
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
					book.emails.put(name, address);
				
				if (!Utils.IsNullOrEmpty(physical))
					book.physical.put(name, physical);
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
}
