package com.topbloc.codechallenge.constants;

/**
 * Application-wide constants for error messages, endpoints, and configuration
 */
public final class AppConstants {
    
    // Private constructor to prevent instantiation
    private AppConstants() {}
    
    // API Response Messages
    public static final class ErrorMessages {
        public static final String INVALID_ITEM_ID = "{\"error\": \"Invalid item ID format\"}";
        public static final String INVALID_DISTRIBUTOR_ID = "{\"error\": \"Invalid distributor ID format\"}";
        public static final String ITEM_NAME_REQUIRED = "{\"error\": \"Item name is required\"}";
        public static final String DISTRIBUTOR_NAME_REQUIRED = "{\"error\": \"Distributor name is required\"}";
        public static final String INVALID_INVENTORY_PARAMS = "{\"error\": \"Invalid parameters. itemId, stock, and capacity must be integers\"}";
        public static final String INVALID_DISTRIBUTOR_PARAMS = "{\"error\": \"Invalid parameters. distributorId, itemId must be integers, cost must be a number\"}";
        public static final String INVALID_STOCK_CAPACITY = "{\"error\": \"Invalid parameters. stock and capacity must be integers\"}";
        public static final String INVALID_COST = "{\"error\": \"Invalid parameters. cost must be a number\"}";
        public static final String TABLE_NAME_REQUIRED = "{\"error\": \"Table name is required. Use ?table=tablename\"}";
        public static final String INVALID_TABLE_NAME = "{\"error\": \"Invalid table name. Valid tables: items, inventory, distributors, distributor_prices\"}";
        public static final String INVALID_QUANTITY = "{\"error\": \"Invalid parameters. quantity must be an integer\"}";
    }
    
    // HTTP Headers
    public static final class Headers {
        public static final String CONTENT_TYPE_JSON = "application/json";
        public static final String CONTENT_TYPE_HTML = "text/html";
        public static final String CONTENT_TYPE_CSV = "text/csv";
        public static final String CONTENT_TYPE_SSE = "text/event-stream";
        public static final String CACHE_CONTROL = "Cache-Control";
        public static final String CONNECTION = "Connection";
        public static final String CORS_ORIGIN = "Access-Control-Allow-Origin";
        public static final String CORS_METHODS = "Access-Control-Allow-Methods";
        public static final String CORS_HEADERS = "Access-Control-Allow-Headers";
    }
    
    // HTTP Methods and Values
    public static final class Http {
        public static final String ALL_ORIGINS = "*";
        public static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS";
        public static final String ALLOWED_HEADERS = "Content-Type, Authorization";
        public static final String NO_CACHE = "no-cache";
        public static final String KEEP_ALIVE = "keep-alive";
    }
    
    // API Endpoints
    public static final class Endpoints {
        public static final String VERSION = "/version";
        public static final String RESET = "/reset";
        public static final String ITEMS = "/items";
        public static final String INVENTORY = "/inventory";
        public static final String DISTRIBUTORS = "/distributors";
        public static final String STREAM = "/stream";
        public static final String STREAM_EVENTS = "/stream/events";
        public static final String EXPORT_CSV = "/export/csv";
    }
    
    // Database Tables
    public static final class Tables {
        public static final String ITEMS = "items";
        public static final String INVENTORY = "inventory";
        public static final String DISTRIBUTORS = "distributors";
        public static final String DISTRIBUTOR_PRICES = "distributor_prices";
    }
    
    // Application Configuration
    public static final class Config {
        public static final int DEFAULT_PORT = 4567;
        public static final String VERSION_STRING = "TopBloc Code Challenge v1.0";
        public static final long CLIENT_TIMEOUT_MS = 60000; // 1 minute
        public static final long CLEANUP_INTERVAL_MS = 30000; // 30 seconds
        public static final long HEARTBEAT_INTERVAL_MS = 15000; // 15 seconds
        public static final int MAX_EVENTS_DISPLAY = 50;
    }
}
