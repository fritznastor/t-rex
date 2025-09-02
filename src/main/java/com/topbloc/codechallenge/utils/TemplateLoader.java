package com.topbloc.codechallenge.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for loading HTML templates and other resource files
 */
public class TemplateLoader {
    
    /**
     * Load an HTML template from the resources/templates directory
     * @param templateName The name of the template file (e.g., "streaming-dashboard.html")
     * @return The HTML content as a string
     * @throws IOException if the template file cannot be read
     */
    public static String loadTemplate(String templateName) throws IOException {
        String templatePath = "/templates/" + templateName;
        
        try (InputStream inputStream = TemplateLoader.class.getResourceAsStream(templatePath)) {
            if (inputStream == null) {
                throw new IOException("Template not found: " + templatePath);
            }
            
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            
            return content.toString();
        }
    }
    
    /**
     * Load the streaming dashboard HTML template
     * @return The streaming dashboard HTML content
     */
    public static String getStreamingDashboard() {
        try {
            return loadTemplate("streaming-dashboard.html");
        } catch (IOException e) {
            // Fallback to a simple error page if template loading fails
            return "<!DOCTYPE html><html><head><title>Error</title></head><body>" +
                   "<h1>Template Loading Error</h1>" +
                   "<p>Could not load streaming dashboard template: " + e.getMessage() + "</p>" +
                   "</body></html>";
        }
    }
}
