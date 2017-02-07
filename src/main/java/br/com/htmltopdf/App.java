package br.com.htmltopdf;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

public class App {
	private static App INSTANCE = new App();
	
	private App() {	}
	
	public static App getInstance() {
		return INSTANCE;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, DocumentException {
		Map<String, String> params = new HashMap<>();
		params.put("name", "Stranger");
		params.put("imgSrc", App.getInstance().getResourceAbsoluteFilePath("i-am-a-programmer.jpg"));
		
		App.getInstance().generatePDF("helloworld.html", "style.css", params);
	}
	
	private VelocityEngine initVelocityEngine() {
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init();
		
		return ve;
	}

	public StringWriter getTemplateStringWriter(String templateName, Map<String, String> params) {
		VelocityEngine ve = initVelocityEngine();

		Template template = ve.getTemplate(templateName);

		final VelocityContext context = new VelocityContext();
		
		params.forEach((key, value) -> {
			context.put(key, value);
		});

		StringWriter writer = new StringWriter();
		
		template.merge(context, writer);
		
		return writer;
	}

	
	public void generatePDF(String htmlTemplateName, String cssName, Map<String, String> params) throws IOException, DocumentException {
		
		StringWriter stringWriter = getTemplateStringWriter(htmlTemplateName, params);

		String htmlContent = stringWriter.toString();
		
		String cssContent = new String(Files.readAllBytes(Paths.get(getResourceAbsoluteFilePath(cssName))));
		
		Document document = new Document();

		PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream("generated.pdf"));

		document.open();

		ByteArrayInputStream bis = new ByteArrayInputStream(htmlContent.toString().getBytes());
		
		ByteArrayInputStream cis = new ByteArrayInputStream(cssContent.toString().getBytes());
		
		XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, document, bis, cis);
		
		document.close();
	}
	
	public String getResourceAbsoluteFilePath(String resourceName) {
		return this.getClass().getClassLoader().getResource(resourceName).getFile();
	}
}