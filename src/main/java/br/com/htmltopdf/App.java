package br.com.htmltopdf;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class App {
	private static App INSTANCE = new App();
	
	private App() {	}
	
	public static App getInstance() {
		return INSTANCE;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, com.lowagie.text.DocumentException {
		Map<String, String> params = new HashMap<>();
		params.put("name", "Stranger");
		params.put("imgSrc", App.getInstance().getResourceAbsoluteFilePath("i-am-a-programmer.jpg"));
		
		App.getInstance().generatePDF("helloworld.html", params);
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

	
	public void generatePDF(String htmlTemplateName, Map<String, String> params) throws com.lowagie.text.DocumentException, IOException {
		StringWriter stringWriter = getTemplateStringWriter(htmlTemplateName, params);

		String htmlContent = stringWriter.toString();
		
		OutputStream out = new FileOutputStream("generated.pdf");

		ITextRenderer renderer = new ITextRenderer();

		renderer.setDocumentFromString(htmlContent);
		renderer.layout();
		renderer.createPDF(out);

		out.close();
	}
	
	public String getResourceAbsoluteFilePath(String resourceName) {
		return this.getClass().getClassLoader().getResource(resourceName).getFile();
	}
}