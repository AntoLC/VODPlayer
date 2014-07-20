package com.caliente.android.vod;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

public class XMLParser 
{
	public Document getDomElement(String paramString) 
	{
		DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
			InputSource localInputSource = new InputSource();
			localInputSource.setCharacterStream(new StringReader(paramString));
			Document localDocument = localDocumentBuilder.parse(localInputSource);
			
			return localDocument;
		} catch (ParserConfigurationException localParserConfigurationException) {
			Log.e("Error: ", localParserConfigurationException.getMessage());
			return null;
		} catch (SAXException localSAXException) {
			Log.e("Error: ", localSAXException.getMessage());
			return null;
		} catch (IOException localIOException) {
			Log.e("Error: ", localIOException.getMessage());
		}
		
		return null;
	}

	public final String getElementValue(Node paramNode) 
	{
		if ((paramNode != null) && (paramNode.hasChildNodes()))
			for (Node localNode = paramNode.getFirstChild(); localNode != null; localNode = localNode.getNextSibling())
				if (localNode.getNodeType() == 3)
					return localNode.getNodeValue();
		return "";
	}

	public String getStringValue(Element paramElement, String paramString) {
		return getElementValue(paramElement.getElementsByTagName(paramString).item(0));
	}

	public String getXmlFromUrl(String paramString) {
		try {
			BasicHttpParams localBasicHttpParams = new BasicHttpParams();
			localBasicHttpParams.setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
			String str = EntityUtils.toString(new DefaultHttpClient(localBasicHttpParams).execute(new HttpPost(paramString)).getEntity(), Charset.defaultCharset().name());
			return str;
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
			localUnsupportedEncodingException.printStackTrace();
			return "";
		} catch (ClientProtocolException localClientProtocolException) {
			localClientProtocolException.printStackTrace();
			return "";
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
		return "";
	}
}