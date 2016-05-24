/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.reniec.pkiep.dcwebspark;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.MultipartConfigElement;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import static spark.Spark.*;
import spark.servlet.SparkApplication;
/**
 *
 * @author aalain
 */

public class App implements SparkApplication{
	
	private static final Map<String, Object> settings = new HashMap<String, Object>();
	private static final boolean IS_WINDOWS = System.getProperty( "os.name" ).contains( "indow" );
	static String staticDir = App.class.getClassLoader().getResource("").getPath().replace("WEB-INF/classes/", "");
	
	public void init(){
		//staticFileLocation("/public");
		//staticFiles.location("../webapp");
		
		staticDir = IS_WINDOWS ? staticDir.substring(1) : staticDir;
		
		staticFiles.externalLocation(staticDir);
		
		get("/form", (req, res) -> {
			
			System.out.println("**************");
			
			/*res.header("FOO", "bar");
			res.body("Hello");
			return "<html><head></head><body></body></html>";*/
			set("jnlp", "dcdelivery.jnlp");
			
//			String file = App.class.getClassLoader().getResource("index.html").getPath();
			//String file = "index.html";
			//return render(file, settings);
			return "";
		});
		
		post("/upload", "multipart/form-data", (req, res) -> {
			
			//System.out.println("Servlet Path" + req.servletPath());
			
			//ServletHolder sh = new ServletHolder();
			//sh.getRegistration().setMultipartConfig(new MultipartConfigElement(System.getProperty("java.io.tmpdir"), 1048576, 1048576, 262144));
			
			/*
			String location = "image";          // the directory location where files will be stored
			long maxFileSize = 100000000;       // the maximum size allowed for uploaded files
			long maxRequestSize = 100000000;    // the maximum size allowed for multipart/form-data requests
			int fileSizeThreshold = 1024;       // the size threshold after which files will be written to disk

			MultipartConfigElement multipartConfigElement = new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
			req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
			
			Collection<Part> parts = req.raw().getParts();
			for (Part part : parts) {
				System.out.println("Name: " + part.getName());
				System.out.println("Size: " + part.getSize());
				System.out.println("Filename: " + part.getName());
			}*/
			
//			if (req.raw().getAttribute("org.eclipse.jetty.multipartConfig") == null) {
//				System.out.println("nulo");
//			}
			

			try{
//				MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/tmp");
				
				MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
				req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
					
				Part file = req.raw().getPart("jarfile");
				
				/*String targetDir = App.class.getClassLoader().getResource("").getPath() + "public/wizard/";
				targetDir = IS_WINDOWS ? targetDir.substring(1) : targetDir;*/
				
				Path filePath = Paths.get(staticDir + "java/" + file.getSubmittedFileName());
				Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				
				System.out.println("Name: " + file.getSubmittedFileName());
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			/*Path out = Paths.get("/tmp/" + file.getName());
			
			file.getInputStream()*/
			
			/*MultiMap<String> params = new MultiMap<String>();
			UrlEncoded.decodeTo(req.body(), params, "UTF-8", -1);*/
			
			
			return "OK ";// + params.getString("demo");
		});
	}
	
    public static void main(String[] args)
    {
        new App().init();
    }	

	public static String render(String file, Map<String, Object> locals) {
		return layout(file, parseFile(file, "\\$\\{(\\w.*?)\\}", locals));
	}
	
	public static String layout(String file, String content) {
		HashMap<String, Object> layout = new HashMap<String, Object>();
		layout.put("content", content);
//		return parseFile(App.class.getClassLoader().getResource("layout.html").getPath(), "@\\{(content)\\}", layout);
		return parseFile("layout.html", "@\\{(content)\\}", layout);
	}
	
	/* ... */
	public static String parseFile(String file, String pattern, Map<String, Object> locals) {
		StringBuffer content = new StringBuffer("");
		try {
			BufferedReader buffer = new BufferedReader(new FileReader(file));
			String line = null;

			while ((line = buffer.readLine()) != null) {
				content.append(parse(pattern, line, locals) + "\n");
			}

			buffer.close();
		} catch (Exception exception) {
			System.out.printf("ERROR: %s\n", exception.getMessage());
		} finally {
			return content.toString();
		}
	}
	
	public static String parse(String pattern, String text, Map<String, Object> locals) {
		Matcher regexp = Pattern.compile(pattern).matcher(text);
		while (regexp.find()) {
			text = regexp.replaceFirst(locals.get(regexp.group(1)).toString());
		}
		return text;
	}
	
	public static void set(String key, Object value) {
		settings.put(key, (String) value);
	}

	public static Object settings(String key) {
		return settings.get(key);
	}	
}
