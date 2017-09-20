package com.fantasy.app.objects;

import com.fantasy.app.notification.Notification;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Created by Djelu on 13.09.2017.
 */
public class XmlManager {
    private Notification notification = new Notification();

    private String fullPathToFirstXml;
    private String fullPathToSecondXml;
    private String fullPathToXslXml;

    public XmlManager() {
    }

    public XmlManager(String fullPathToFirstXml, String fullPathToSecondXml, String fullPathToXslXml) {
        this.fullPathToFirstXml = fullPathToFirstXml;
        this.fullPathToSecondXml = fullPathToSecondXml;
        this.fullPathToXslXml = fullPathToXslXml;
    }

    public XmlManager(String currentDir, String pathToFirstXml, String pathToSecondXml, String pathToXslXml) {
        this.fullPathToFirstXml = currentDir + pathToFirstXml;
        this.fullPathToSecondXml = currentDir + pathToSecondXml;
        this.fullPathToXslXml = currentDir + pathToXslXml;
    }

    public Notification getNotification() {
        return notification;
    }

    public String getFullPathToFirstXml() {
        return fullPathToFirstXml;
    }
    public String getFullPathToSecondXml() {
        return fullPathToSecondXml;
    }
    public String getFullPathToXslXml() {
        return fullPathToXslXml;
    }

    public void createXml(ResultSet resultSet){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element entries = document.createElement("entries");
            document.appendChild(entries);

            while (resultSet.next()) {
                Element entry = document.createElement("entry");
                Element field = document.createElement("field");
                Text text = document.createTextNode(resultSet.getString("FIELD"));

                field.appendChild(text);
                entry.appendChild(field);
                entries.appendChild(entry);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(document), new StreamResult(new FileOutputStream(fullPathToFirstXml)));
        }catch (Exception e){
            notification.addError(String.format("Error during creating xml. Path(%s)",fullPathToFirstXml), e);
        }
    }

    public void transformXml(){
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer(new StreamSource(fullPathToXslXml));
            transformer.transform(new StreamSource(fullPathToFirstXml), new StreamResult(fullPathToSecondXml));
        } catch (TransformerException e) {
            String message = String.format("Error during transformation xml1 path(%s) to xml2 path(%s) by xsl path(%s)"
                    ,fullPathToFirstXml, fullPathToSecondXml, fullPathToXslXml);
            notification.addError(message,e);
        }
    }

    public long getArithmeticMeanFromFields(){
        int count = 0;
        long sum = 0;

        String elementName = "entry";
        String attributeName = "field";

        try {
            XMLInputFactory inFactory = XMLInputFactory.newInstance();
            XMLEventReader reader = inFactory.createXMLEventReader(new FileInputStream(fullPathToSecondXml));

            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();

                switch (event.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT: {
                        if (event.asStartElement().getName().toString().equalsIgnoreCase(elementName)) {
                            Iterator iterator = event.asStartElement().getAttributes();
                            while (iterator.hasNext()){
                                Attribute attribute = (Attribute)iterator.next();
                                if(attribute.getName().toString().equalsIgnoreCase(attributeName)){
                                    sum+=Integer.parseInt(attribute.getValue());
                                    count++;
                                }
                            }
                            continue;
                        }
                        break;
                    }
                }
            }
        }catch (FileNotFoundException e) {
            notification.addError(String.format("Can not find xml path(%s)",fullPathToSecondXml), e);
        } catch (XMLStreamException e) {
            String message = String.format("Error with enumeration of elements in xml. " +
                    "Necessary in xml: element(%s) and his attribute(%s)",elementName,attributeName);
            notification.addError(message, e);
        }

        return sum/count;
    }

    public void createFullPaths(String currentDir, String pathToFirstXml, String pathToSecondXml, String pathToXslXml){
        this.fullPathToFirstXml = currentDir + pathToFirstXml;
        this.fullPathToSecondXml = currentDir + pathToSecondXml;
        this.fullPathToXslXml = currentDir + pathToXslXml;
    }
}
