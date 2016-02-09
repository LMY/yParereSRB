package y.utils;

import java.io.File;
import java.util.Map;
import java.util.function.Function;

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

public class GeneralPropertiesExporter {
	public final static String TAG_ROOT = "config";
	public final static String TAG_ENTRY = "entry";
	public final static String TAG_KEY = "key";
	public final static String TAG_VALUE = "value";
	public final static String TAG_TYPE = "type";
	
	public static <KeyType> GeneralProperties<KeyType> read(String filename, Function<String, KeyType> converter) throws Exception {
		final File file = new File(filename);
		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		final Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();

		final Element root = doc.getDocumentElement();
		final NodeList nodes = root.getChildNodes();
		
		final GeneralProperties<KeyType> conf = new GeneralProperties<KeyType>();
		
		for (int i = 0; i < nodes.getLength(); i++) {
			final Node node = nodes.item(i);
		
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				final String nodename = node.getNodeName();
				
				if (!nodename.equals(TAG_ENTRY))
					continue;

				final NamedNodeMap attrs = node.getAttributes();
				
				KeyType key = null;
				String value = "";
				String type = "";
				
				for (int k=0; k<attrs.getLength(); k++) {
					final Attr attr = (Attr) attrs.item(k);
					final String attr_value = attr.getValue();
					final String attr_name = attr.getName();
					
					if (attr_name.equals(TAG_TYPE))
						type = attr_value;
					else if (attr_name.equals(TAG_KEY))
						key = converter.apply(attr_value);
					else if (attr_name.equals(TAG_VALUE))
						value = attr_value;
				}
				
				if (key == null || value.isEmpty() || type.isEmpty())
					continue;
				
				final Class<?> cType = Class.forName(type);
				conf.set(cType, key, toObject(cType, value));
			}
		}
		
		return conf;
	}
	
	public static Object toObject(Class<?> type, String value) {
	    if (Boolean.class == type) return Boolean.parseBoolean(value);
	    if (Byte.class == type) return Byte.parseByte(value);
	    if (Short.class == type) return Short.parseShort(value);
	    if (Integer.class == type) return Integer.parseInt(value);
	    if (Long.class == type) return Long.parseLong(value);
	    if (Float.class == type) return Float.parseFloat(value);
	    if (Double.class == type) return Double.parseDouble(value);
	    return value;
	}
	
	
	public static <KeyType> void save(GeneralProperties<KeyType> props, String filename) throws Exception {
		final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		final Document doc = docBuilder.newDocument();
		final Element root = doc.createElement(TAG_ROOT);
		doc.appendChild(root);
		
		for (Class<?> type : props.maps.keySet()) {
			final Map<KeyType, ?> themap = props.maps.get(type);
			
			for (KeyType key : themap.keySet()) {
				final String value = themap.get(key).toString();
				final Element e = doc.createElement(TAG_ENTRY);
				
				e.setAttribute(TAG_TYPE, type.getName());
				e.setAttribute(TAG_KEY, key.toString());
				e.setAttribute(TAG_VALUE, value);
				root.appendChild(e);
			}
		}
		
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Transformer transformer = transformerFactory.newTransformer();
		final DOMSource source = new DOMSource(doc);
		final StreamResult result = new StreamResult(new File(filename));
		transformer.transform(source, result);
	}
}
