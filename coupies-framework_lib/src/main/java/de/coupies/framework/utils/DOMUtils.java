/**
 * 
 */
package de.coupies.framework.utils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author thomas.volk@denkwerk.com
 * @since 27.08.2010
 *
 */
public class DOMUtils {

	private DOMUtils() {
	}

	/**
	 * gibt text inhalt einer Node zurück
	 * 
	 * @param subNode Node
	 * @return Text
	 */
	public static String getNodeContent(Node subNode) {
		if(subNode != null) {
			Node firstChild = subNode.getFirstChild();
			if(firstChild != null) {				
				return firstChild.getNodeValue();
			}
		}
		return null;
	}

	/**
	 * gibt den Inhalt der ersten Node unterhalb von inElment zurück
	 * 
	 * @param inElement root elemnt
	 * @return Text
	 */
	public static String getContentOfFirstNode(Element inElement, String nodeName) {
		return getNodeContent(getSubNode(inElement, nodeName, 0));
	}
	
	public static String getContentOfFirstNode(Node inElement, String nodeName) {
		return getNodeContent(getSubNode(inElement, nodeName, 0));
	}

	/**
	 * Node unterhalb von inElment zurück
	 * 
	 * @param inElement root Element
	 * @param nodeName name der subNode
	 * @param index index
	 * @return subNode
	 */
	public static Node getSubNode(Element inElement, String nodeName, int index) {
		NodeList elementsByTagName = inElement.getElementsByTagName(nodeName);
		if(elementsByTagName.getLength() > 0) {
			return elementsByTagName.item(index);
		}
		else {
			return null;
		}
	}
	
	public static Node getSubNode(Node node, String nodeName, int index) {
		if(node == null || !(node instanceof Element)) {
			return null;
		}
		else {
			return getSubNode((Element) node, nodeName, index);
		}	
	}

	/**
	 * @param inItemElement
	 * @param inString
	 * @param inI
	 * @return
	 */
	public static Element getSubElement(Element element, String nodeName,
			int index) {
		Node node = getSubNode(element, nodeName, index);
		if(node == null || !(node instanceof Element)) {
			return null;
		}
		else {
			return (Element) node;
		}
	}
	
	public static String getAttribute(Node node, String name) {
		if(node == null || !(node instanceof Element)) {
			return null;
		}
		else {
			return ((Element) node).getAttribute(name);
		}
	}

	public static Integer getInteger(String input) {
		try {
			return Integer.parseInt(input);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public static Float getFloat(String input) {
		try {
			return Float.parseFloat(input);
		}
		catch (Exception e) {
			return null;
		}
	}
}
