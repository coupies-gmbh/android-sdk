package de.coupies.framework.services.content;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.coupies.framework.CoupiesServiceException;

public class DocumentProcessor {
	public interface Handler {	
	}
	public interface DocumentHandler extends Handler {
		Object handleDocument(Document doc) throws CoupiesServiceException;
	}
	public interface InputStreamHandler extends Handler {
		Object read(InputStream inStream) throws CoupiesServiceException;
	}
	public Object execute(InputStream inStream, Handler handler) throws CoupiesServiceException {
		if(handler instanceof InputStreamHandler) {
			return execute(inStream, (InputStreamHandler) handler);
		}
		else if (handler instanceof DocumentHandler) {
			return execute(inStream, (DocumentHandler) handler);
		}
		else {
			throw new IllegalStateException("cannot exceute handler class: " + 
					handler.getClass().getName());
		}
	}
	
	public Object execute(InputStream inStream, InputStreamHandler handler) throws CoupiesServiceException {
		try {
			return handler.read(inStream);
		}
		finally {
			try {
				inStream.close();
			} catch (IOException e) {
				throw new DocumentParseException(e);
			}
		}
	}
	
	public Object execute(InputStream inStream, DocumentHandler handler) throws CoupiesServiceException {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            doc = db.parse(inStream);
            doc.getDocumentElement().normalize(); 
        } catch (ParserConfigurationException e) {
        	throw new DocumentParseException(e);
        } catch (SAXException e) {
        	throw new DocumentParseException(e);
        } catch (IOException e) {
        	throw new DocumentParseException(e);
		}
        
		try {
			try {
				return handler.handleDocument(doc);
			}
			finally {
				inStream.close();
			}
        }
		catch (CoupiesServiceException e) {
			throw e;
		}
        catch (Exception e) {
        	throw new DocumentParseException(e);
		} 
	}
	
}
