package com.sg.bbit.generate.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

@Service
public class VelocityService {
	@Value("${velocity.generate-path}")
	private String generatePath;
	@Value("${velocity.charset}")
	private String charset;

	@Autowired
	private VelocityEngine velocityEngine;
	
	public String getTemplate(final String templateName, final VelocityContext velocityContext) {
		final Template template = velocityEngine.getTemplate(generatePath + templateName, charset);
		final StringWriter stringWriter = new StringWriter();
		template.merge(velocityContext, stringWriter);
		return stringWriter.toString();
	}
	
	public static String prettyPrintByTransformer(String xmlString, int indent, boolean ignoreDeclaration) {
		try {
			InputSource src = new InputSource(new StringReader(xmlString));
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);
		
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", indent);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, ignoreDeclaration ? "yes" : "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
			Writer out = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(out));
			return out.toString();
		} catch (Exception e) {
			throw new RuntimeException("Error occurs when pretty-printing xml:\n" + xmlString, e);
		}
	}
}
