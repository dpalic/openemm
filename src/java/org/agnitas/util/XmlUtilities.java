/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
package org.agnitas.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlUtilities {
	private static final transient Logger logger = Logger.getLogger(XmlUtilities.class);

	public static Document parseXmlString(String xmlInput) throws Exception {
		boolean validation = false;
		boolean ignoreWhitespace = true;
		boolean ignoreComments = true;
		boolean putCDATAIntoText = true;
		boolean createEntityRefs = false;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		// set the configuration options
		dbf.setValidating(validation);
		dbf.setIgnoringComments(ignoreComments);
		dbf.setIgnoringElementContentWhitespace(ignoreWhitespace);
		dbf.setCoalescing(putCDATAIntoText);
		// The opposite of creating entity ref nodes is expanding them inline
		dbf.setExpandEntityReferences(!createEntityRefs);

		DocumentBuilder db = null;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(new InputSource(new StringReader(xmlInput)));
			return doc;

		} catch (Exception e) {
			logger.error("Error while parsing xml", e);
			throw e;
		}
	}

	/**
	 * Ignores DTD for better performance. This works faster for XHTML, because it doesn't download the dtd from any website like "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
	 *
	 * @param xmlInput
	 * @return
	 * @throws Exception
	 */
	public static Document parseXmlStringIngoringDtd(String xmlInput) throws Exception {
		if (xmlInput == null) {
			throw new Exception("XML-Data is null");
		} else if (StringUtils.isBlank(xmlInput)) {
			throw new Exception("XML-Data is empty");
		}

		boolean validation = false;
		boolean ignoreWhitespace = true;
		boolean ignoreComments = true;
		boolean putCDATAIntoText = true;
		boolean createEntityRefs = false;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		// set the configuration options
		dbf.setValidating(validation);
		dbf.setIgnoringComments(ignoreComments);
		dbf.setIgnoringElementContentWhitespace(ignoreWhitespace);
		dbf.setCoalescing(putCDATAIntoText);
		// The opposite of creating entity ref nodes is expanding them inline
		dbf.setExpandEntityReferences(!createEntityRefs);

		DocumentBuilder db = null;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();

			db.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					return new InputSource(new StringReader(""));
				}
			});

			doc = db.parse(new InputSource(new StringReader(xmlInput)));
			return doc;
		} catch (Exception e) {
			logger.error("Error while parsing xHtml", e);
			throw e;
		}
	}

	/**
	 * Returns the content of a simple text tag If there are more than one text node or other nodetypes it will return null
	 *
	 * @param node
	 * @return
	 */
	public static String getSimpleTextValueFromNode(Node node) {
		if (node != null && node.getChildNodes().getLength() == 1 && node.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE) {
			return node.getChildNodes().item(0).getTextContent();
		} else {
			return null;
		}
	}

	public static String getAttributeValue(Node pNode, String pAttributeName) {
		String returnString = null;

		NamedNodeMap attributes = pNode.getAttributes();
		if (attributes != null) {
			for (int i = 0; i < attributes.getLength(); i++) {
				if (attributes.item(i).getNodeName().equalsIgnoreCase(pAttributeName)) {
					returnString = attributes.item(i).getNodeValue();
					break;
				}
			}
		}

		return returnString;
	}

	public static String getNodeValue(Node pNode) {
		if (pNode.getNodeValue() != null) {
			return pNode.getNodeValue();
		} else if (pNode.getFirstChild() != null) {
			return getNodeValue(pNode.getFirstChild());
		} else {
			return null;
		}
	}

	public static String getNodeAsString(Node pNode, String encoding, boolean pRemoveXmlLine) throws Exception {
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformNode(pNode, encoding, pRemoveXmlLine, result);
		return writer.toString();
	}

	public static byte[] getNodeAsRaw(Node pNode, String encoding, boolean pRemoveXmlLine) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(outputStream);
		transformNode(pNode, encoding, pRemoveXmlLine, result);
		return outputStream.toByteArray();
	}

	private static void transformNode(Node node, String encoding, boolean removeXmlHeader, StreamResult result) throws Exception {
		TransformerFactory transformerFactory = null;
		Transformer transformer = null;
		DOMSource source = null;

		try {
			transformerFactory = TransformerFactory.newInstance();
			if (transformerFactory == null) {
				throw new Exception("TransformerFactory error");
			}

			transformer = transformerFactory.newTransformer();
			if (transformer == null) {
				throw new Exception("Transformer error");
			}

			transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			if (removeXmlHeader) {
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			} else {
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			}

			source = new DOMSource(node);

			transformer.transform(source, result);
		} catch (TransformerFactoryConfigurationError e) {
			throw new Exception("TransformerFactoryConfigurationError", e);
		} catch (TransformerConfigurationException e) {
			throw new Exception("TransformerConfigurationException", e);
		} catch (TransformerException e) {
			throw new Exception("TransformerException", e);
		}
	}

	public static Document parseXMLDataAndXSDVerifyByDOM(byte[] pData, String byteEncoding, String xsdFileName) throws Exception, Exception {
		try {
			if (pData == null) {
				return null;
			}

			if (pData[pData.length - 1] == 0) {
				pData = new String(pData, "UTF-8").trim().getBytes("UTF-8");
			}

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			if (docBuilderFactory == null) {
				throw new Exception("DocumentBuilderFactory error");
			}
			docBuilderFactory.setNamespaceAware(true);

			if (xsdFileName != null) {
				String schemaURI = xsdFileName;
				docBuilderFactory.setValidating(true);
				docBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
				docBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", schemaURI);
			}

			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			if (docBuilder == null) {
				throw new Exception("DocumentBuilder error");
			}

			InputSource inputSource = new InputSource(new ByteArrayInputStream(pData));
			if (byteEncoding != null) {
				inputSource.setEncoding(byteEncoding);
			}
			ParseErrorHandler errorHandler = new ParseErrorHandler();
			docBuilder.setErrorHandler(errorHandler);

			Document xmlDocument = docBuilder.parse(inputSource);

			if (errorHandler.problemsOccurred()) {
				throw new Exception("ErrorConstGlobals.XML_SCHEMA_ERROR " + xsdFileName + " " + errorHandler.getMessage());
			} else {
				return xmlDocument;
			}
		} catch (ParserConfigurationException e) {
			logger.error(e.getClass().getSimpleName(), e);
			throw new Exception("ErrorConstException.XML_PROCESSING " + e.getClass().getSimpleName() + " " + e.getMessage(), e);
		} catch (SAXException e) {
			logger.error(e.getClass().getSimpleName(), e);
			throw new Exception("ErrorConstException.XML_PROCESSING " + e.getClass().getSimpleName() + " " + e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getClass().getSimpleName() + " während der XML-Verarbeitung", e);
			throw new Exception("ErrorConstException.XML_PROCESSING " + e.getClass().getSimpleName() + " " + e.getMessage(), e);
		}
	}

	private static class ParseErrorHandler implements ErrorHandler {
		ArrayList<SAXParseException> warnings = null;
		ArrayList<SAXParseException> errors = null;
		ArrayList<SAXParseException> fatalErrors = null;

		boolean problems = false;

		public boolean problemsOccurred() {
			return problems;
		}

		public void warning(SAXParseException exception) throws SAXException {
			problems = true;
			if (warnings == null) {
				warnings = new ArrayList<SAXParseException>();
			}
			warnings.add(exception);
		}

		public void error(SAXParseException exception) throws SAXException {
			problems = true;
			if (errors == null) {
				errors = new ArrayList<SAXParseException>();
			}
			errors.add(exception);
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			problems = true;
			if (fatalErrors == null) {
				fatalErrors = new ArrayList<SAXParseException>();
			}
			fatalErrors.add(exception);
		}

		public String getMessage() {
			if (fatalErrors != null && fatalErrors.size() > 0) {
				return fatalErrors.get(0).getMessage();
			} else if (errors != null && errors.size() > 0) {
				return errors.get(0).getMessage();
			} else if (warnings != null && warnings.size() > 0) {
				return warnings.get(0).getMessage();
			} else {
				return "No ParserErrors occured";
			}
		}
	}

	public static String convertXML2String(Document pDocument, String encoding) throws Exception {
		TransformerFactory transformerFactory = null;
		Transformer transformer = null;
		DOMSource domSource = null;
		StringWriter writer = new java.io.StringWriter();
		StreamResult result = null;

		try {
			transformerFactory = TransformerFactory.newInstance();
			if (transformerFactory == null) {
				throw new Exception("TransformerFactory error");
			}

			transformer = transformerFactory.newTransformer();
			if (transformer == null) {
				throw new Exception("Transformer error");
			}

			if (encoding != null) {
				transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			} else {
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			}

			domSource = new DOMSource(pDocument);
			result = new StreamResult(writer);

			transformer.transform(domSource, result);

			return writer.toString();
		} catch (TransformerFactoryConfigurationError e) {
			throw new Exception("TransformerFactoryConfigurationError", e);
		} catch (TransformerConfigurationException e) {
			throw new Exception("TransformerConfigurationException", e);
		} catch (TransformerException e) {
			throw new Exception("TransformerException", e);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException ex) {
				throw new Exception("IO error", ex);
			}
		}
	}

	public static byte[] convertXML2ByteArray(Node pDocument, String encoding) throws Exception {
		TransformerFactory transformerFactory = null;
		Transformer transformer = null;
		DOMSource domSource = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		StreamResult result = null;

		try {
			transformerFactory = TransformerFactory.newInstance();
			if (transformerFactory == null) {
				throw new Exception("TransformerFactory error");
			}

			transformer = transformerFactory.newTransformer();
			if (transformer == null) {
				throw new Exception("Transformer error");
			}

			if (encoding != null) {
				transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			} else {
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			}

			domSource = new DOMSource(pDocument);
			result = new StreamResult(outputStream);

			transformer.transform(domSource, result);

			return outputStream.toByteArray();
		} catch (TransformerFactoryConfigurationError e) {
			throw new Exception("TransformerFactoryConfigurationError", e);
		} catch (TransformerConfigurationException e) {
			throw new Exception("TransformerConfigurationException", e);
		} catch (TransformerException e) {
			throw new Exception("TransformerException", e);
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException ex) {
				throw new Exception("IO error", ex);
			}
		}
	}

	// public static void parseXMLDataAndXSDVerifyBySAX(byte[] pUploadXmlDocumentArray, UploadXMLContentHandler pContentHandler) throws Exception, Exception {
	// try {
	// if (pUploadXmlDocumentArray == null || pUploadXmlDocumentArray.length == 0) {
	// throw new Exception(ErrorConstAtsGlobals.UPLOAD_XML_EMPTY);
	// }
	// if (pUploadXmlDocumentArray[0] != "<".getBytes("UTF-8")[0]) {
	// throw new Exception(ErrorConstAtsGlobals.INVALID_XML_DATA, "XML data is invalid");
	// }
	//
	// SAXParser parser = new SAXParser();
	// String schemaFileName = pContentHandler.getSchemaFileName();
	// if (schemaFileName != null) {
	// URI schemaFileURI = Utilities.getResource(schemaFileName);
	// if (schemaFileURI == null) {
	// throw new Exception("XML-Schema was not found: " + schemaFileName);
	// }
	// parser.setFeature("http://xml.org/sax/features/validation", true);
	// parser.setFeature("http://apache.org/xml/features/validation/schema", true);
	// parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
	// parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", schemaFileURI.toString());
	// }
	//
	// parser.setContentHandler(pContentHandler);
	//
	// ParseErrorHandler errorHandler = new ParseErrorHandler();
	// parser.setErrorHandler(errorHandler);
	//
	// InputSource inputSource = new InputSource(new ByteArrayInputStream(pUploadXmlDocumentArray));
	// parser.parse(inputSource);
	//
	// if (errorHandler.problemsOccurred()) {
	// throw new Exception(ErrorConstAtsGlobals.XML_SCHEMA_ERROR, pContentHandler.getSchemaFileName(), errorHandler.getMessage());
	// }
	//
	// /*
	// * String parserClass = "org.apache.xerces.parsers.SAXParser"; String validationFeature = "http://xml.org/sax/features/validation"; String schemaFeature =
	// * "http://apache.org/xml/features/validation/schema";
	// *
	// * XMLReader r = XMLReaderFactory.createXMLReader(parserClass); r.setFeature(validationFeature,true); r.setFeature(schemaFeature,true);
	// *
	// * InputSource inputSourcSAX = new InputSource(pDataStream);
	// * if (byteEncoding != null) {
	// * inputSourcSAX.setEncoding(byteEncoding);
	// * }
	// * ParseErrorHandler errorHandlerSax = new ParseErrorHandler();
	// * r.setErrorHandler(errorHandlerSax); r.parse(inputSourcSAX);
	// */
	// } catch (SAXException e) {
	// logger.error(e.getClass().getSimpleName(), e);
	// throw new Exception(ErrorConstException.XML_PROCESSING, e.getClass().getSimpleName(), e.getMessage());
	// } catch (IOException e) {
	// logger.error(e.getClass().getSimpleName() + " while XML processing", e);
	// throw new Exception(ErrorConstException.XML_PROCESSING, e.getClass().getSimpleName(), e.getMessage());
	// }
	// }

	public static Element createRootTagNode(Document baseDocument, String rootTagName) {
		Element newNode = baseDocument.createElement(rootTagName);
		baseDocument.appendChild(newNode);
		return newNode;
	}

	public static Element appendNode(Node baseNode, String tagName) {
		Element newNode = baseNode.getOwnerDocument().createElement(tagName);
		baseNode.appendChild(newNode);
		return newNode;
	}

	public static Element appendTextValueNode(Node baseNode, String tagName, int tagValue) {
		return appendTextValueNode(baseNode, tagName, Integer.toString(tagValue));
	}

	public static Element appendTextValueNode(Node baseNode, String tagName, String tagValue) {
		Element newNode = appendNode(baseNode, tagName);
		if (tagValue == null) {
			newNode.appendChild(baseNode.getOwnerDocument().createTextNode("<null>"));
		} else {
			newNode.appendChild(baseNode.getOwnerDocument().createTextNode(tagValue));
		}
		return newNode;
	}

	public static void appendAttribute(Element baseNode, String attributeName, boolean attributeValue) {
		appendAttribute(baseNode, attributeName, attributeValue ? "true" : "false");
	}

	public static void appendAttribute(Element baseNode, String attributeName, int attributeValue) {
		appendAttribute(baseNode, attributeName, Integer.toString(attributeValue));
	}

	public static void appendAttribute(Element baseNode, String attributeName, String attributeValue) {
		Attr typeAttribute = baseNode.getOwnerDocument().createAttribute(attributeName);
		if (attributeValue == null) {
			typeAttribute.setNodeValue("<null>");
		} else {
			typeAttribute.setNodeValue(attributeValue);
		}
		baseNode.setAttributeNode(typeAttribute);
	}

	public static void removeAttribute(Element baseNode, String attributeName) {
		baseNode.getAttributes().removeNamedItem(attributeName);
	}

	public static Document addCommentToDocument(Document pDocument, String pComment) {
		pDocument.getFirstChild().appendChild(pDocument.createComment(pComment));
		return pDocument;
	}

	public static String getEncodingOfXmlByteArray(byte[] xmlData) throws Exception {
		String encodingAttributeName = "ENCODING";
		try {
			String first50CharactersInUtf8UpperCase = new String(xmlData, "UTF-8").substring(0, 50).toUpperCase();
			if (first50CharactersInUtf8UpperCase.contains(encodingAttributeName)) {
				int encodingstart = first50CharactersInUtf8UpperCase.indexOf(encodingAttributeName) + encodingAttributeName.length();

				int contentStartEinfach = first50CharactersInUtf8UpperCase.indexOf("'", encodingstart);
				int contentStartDoppelt = first50CharactersInUtf8UpperCase.indexOf("\"", encodingstart);
				int contentStart = Math.min(contentStartEinfach, contentStartDoppelt);
				if (contentStartEinfach < 0) {
					contentStart = contentStartDoppelt;
				}
				if (contentStart < 0) {
					throw new Exception("XmlByteArray-Encoding nicht ermittelbar");
				}
				contentStart = contentStart + 1;

				int contentEndSingle = first50CharactersInUtf8UpperCase.indexOf("'", contentStart);
				int contentEndDouble = first50CharactersInUtf8UpperCase.indexOf("\"", contentStart);
				int contentEnd = Math.min(contentEndSingle, contentEndDouble);
				if (contentEndSingle < 0) {
					contentEnd = contentEndDouble;
				}
				if (contentEnd < 0) {
					throw new Exception("XmlByteArray-Encoding nicht ermittelbar");
				}

				String encodingString = first50CharactersInUtf8UpperCase.substring(contentStart, contentEnd);
				return encodingString;
			} else {
				throw new Exception("XmlByteArray-Encoding nicht ermittelbar");
			}
		} catch (UnsupportedEncodingException e) {
			throw new Exception("XmlByteArray-Encoding nicht ermittelbar");
		}
	}

	/**
	 * Returns all direct simple text value subnodes and their values for a dataNode
	 *
	 * Example XML:
	 * 	<dataNode>
	 * 		<a>1</a>
	 *      <b>2</b>
	 *      <c>3</c>
	 * 	</dataNode>
	 * Returns: a=1, b=2, c=3
	 *
	 * @param dataNode
	 * @return
	 */
	public static Map<String, String> getSimpleValuesOfNode(Node dataNode) {
		Map<String, String> returnMap = new HashMap<String, String>();

		NodeList list = dataNode.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getFirstChild() != null && node.getFirstChild().getNodeType() == Node.TEXT_NODE && node.getChildNodes().getLength() == 1) {
				returnMap.put(node.getNodeName(), node.getFirstChild().getNodeValue());
			}
		}

		return returnMap;
	}

	public static List<Node> getSubNodes(Node dataNode) {
		List<Node> returnList = new ArrayList<Node>();

		NodeList list = dataNode.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			returnList.add(node);
		}

		return returnList;
	}

	public static List<String> getNodenamesOfChilds(Node dataNode) {
		List<String> returnList = new ArrayList<String>();

		NodeList list = dataNode.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeType() != Node.TEXT_NODE) {
				returnList.add(node.getNodeName());
			}
		}

		return returnList;
	}
}
