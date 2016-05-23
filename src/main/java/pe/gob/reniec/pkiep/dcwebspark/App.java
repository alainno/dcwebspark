/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.reniec.pkiep.dcwebspark;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.MultipartConfigElement;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;
import static spark.Spark.*;
import spark.servlet.SparkApplication;
/**
 *
 * @author aalain
 */
public class App implements SparkApplication{
	
	private static final Map<String, Object> settings = new HashMap<String, Object>();
	
	
	public void init(){

		staticFileLocation("/css");
		
		get("/", (req, res) -> {
			/*res.header("FOO", "bar");
			res.body("Hello");
			return "<html><head></head><body></body></html>";*/
			
			
			set("jnlp", "dcdelivery.jnlp");
			
			String file = App.class.getClassLoader().getResource("index.html").getPath();
			return render(file, settings);
		});
		
		post("/upload", (req, res) -> {
			MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/tmp");
			req.raw().setAttribute("org.eclipse.multipartConfig", multipartConfigElement);
			
			Part file = req.raw().getPart("jarfile");
			
			return "OK";
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
		return parseFile(App.class.getClassLoader().getResource("layout.html").getPath(), "@\\{(content)\\}", layout);
//		return parseFile("layout.html", "@\\{(content)\\}", layout);
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
