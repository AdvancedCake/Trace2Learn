package com.trace2learn.TraceLibrary.Database;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

/**
 * Singleton XML parser
 */
public class Parser {
    public static DocumentBuilder builder = null;
    
    private static void createBuilder() {
        if (builder == null) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            try {
                builder = dbFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                Log.e("XML Parser", e.getMessage());
            }
        }
    }
    
    public static Document parse(File f) throws SAXException, IOException {
        createBuilder();
        return builder.parse(f);
    }
    
    public static Document parse(String xml) throws SAXException, IOException {
        createBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
 
    /**
     * @param node an XML node
     * @return the String contained within the node
     */
    public static String getNodeValue(Node node) {
        StringBuffer buf = new StringBuffer();
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node textChild = children.item(i);
            if (textChild.getNodeType() != Node.TEXT_NODE) {
                System.err.println("Mixed content! Skipping child element " + textChild.getNodeName());
                continue;
            }
            buf.append(textChild.getNodeValue());
        }
        return buf.toString();
    }

}
