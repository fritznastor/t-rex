package com.topbloc.codechallenge.utils;

import com.topbloc.codechallenge.constants.AppConstants;
import spark.Response;

/**
 * Utility class for handling HTTP responses consistently across the application
 */
public final class ResponseUtils {
    
    // Private constructor to prevent instantiation
    private ResponseUtils() {}
    
    /**
     * Set standard JSON response headers
     * @param res Spark Response object
     */
    public static void setJsonHeaders(Response res) {
        res.header("Content-Type", AppConstants.Headers.CONTENT_TYPE_JSON);
    }
    
    /**
     * Set standard HTML response headers
     * @param res Spark Response object
     */
    public static void setHtmlHeaders(Response res) {
        res.header("Content-Type", AppConstants.Headers.CONTENT_TYPE_HTML);
    }
    
    /**
     * Set CSV download headers
     * @param res Spark Response object
     * @param filename The filename for download
     */
    public static void setCsvHeaders(Response res, String filename) {
        res.type(AppConstants.Headers.CONTENT_TYPE_CSV);
        res.header("Content-Disposition", "attachment; filename=" + filename + ".csv");
    }
    
    /**
     * Set Server-Sent Events headers
     * @param res Spark Response object
     */
    public static void setSseHeaders(Response res) {
        res.header("Content-Type", AppConstants.Headers.CONTENT_TYPE_SSE);
        res.header(AppConstants.Headers.CACHE_CONTROL, AppConstants.Http.NO_CACHE);
        res.header(AppConstants.Headers.CONNECTION, AppConstants.Http.KEEP_ALIVE);
        res.header(AppConstants.Headers.CORS_ORIGIN, AppConstants.Http.ALL_ORIGINS);
        res.header(AppConstants.Headers.CORS_HEADERS, AppConstants.Headers.CACHE_CONTROL);
    }
    
    /**
     * Set response status based on result content
     * @param res Spark Response object
     * @param result JSON result string
     */
    public static void setStatusFromResult(Response res, String result) {
        if (result.contains("\"success\": false")) {
            res.status(400);
        } else {
            res.status(200);
        }
    }
    
    /**
     * Parse integer parameter with proper error handling
     * @param param Parameter string
     * @param paramName Name of parameter for error message
     * @return Parsed integer value
     * @throws NumberFormatException if parsing fails
     */
    public static int parseIntParam(String param, String paramName) throws NumberFormatException {
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid " + paramName + " format");
        }
    }
    
    /**
     * Parse double parameter with proper error handling
     * @param param Parameter string
     * @param paramName Name of parameter for error message
     * @return Parsed double value
     * @throws NumberFormatException if parsing fails
     */
    public static double parseDoubleParam(String param, String paramName) throws NumberFormatException {
        try {
            return Double.parseDouble(param);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid " + paramName + " format");
        }
    }
    
    /**
     * Validate and trim string parameter
     * @param param Parameter string
     * @param paramName Name of parameter for error message
     * @return Trimmed string
     * @throws IllegalArgumentException if parameter is null or empty
     */
    public static String validateStringParam(String param, String paramName) throws IllegalArgumentException {
        if (param == null || param.trim().isEmpty()) {
            throw new IllegalArgumentException(paramName + " is required");
        }
        return param.trim();
    }
}
