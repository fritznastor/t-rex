package com.topbloc.codechallenge;

import com.topbloc.codechallenge.db.DatabaseManager;
import com.topbloc.codechallenge.utils.TemplateLoader;
import com.topbloc.codechallenge.utils.ResponseUtils;
import com.topbloc.codechallenge.constants.AppConstants;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        // Set port
        port(AppConstants.Config.DEFAULT_PORT);
        
        // Enable CORS globally
        before("*", (req, res) -> {
            res.header(AppConstants.Headers.CORS_ORIGIN, AppConstants.Http.ALL_ORIGINS);
            res.header(AppConstants.Headers.CORS_METHODS, AppConstants.Http.ALLOWED_METHODS);
            res.header(AppConstants.Headers.CORS_HEADERS, AppConstants.Http.ALLOWED_HEADERS);
        });
        
        DatabaseManager.connect();
        // Don't change this - required for GET and POST requests with the header 'content-type'
        options("/*",
                (req, res) -> {
                    
                    res.header("Access-Control-Allow-Headers", "content-type");
                    res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
                    return "OK";
                });

        // Don't change - if required you can reset your database by hitting this endpoint at localhost:4567/reset
        get("/reset", (req, res) -> {
            DatabaseManager.resetDatabase();
            return "OK";
        });

        //TODO: Add your routes here. a couple of examples are below
        get(AppConstants.Endpoints.ITEMS, (req, res) -> {
            ResponseUtils.setJsonHeaders(res);
            return DatabaseManager.getItems();
        });
        get(AppConstants.Endpoints.VERSION, (req, res) -> {
            return AppConstants.Config.VERSION_STRING;
        });

        // ================ INVENTORY GET ROUTES ================
        
        // Get all items in inventory with name, ID, stock, and capacity
        get("/inventory", (req, res) -> {
            res.header("Content-Type", "application/json");
            res.status(200);
            return DatabaseManager.getAllInventory();
        });

        // Get all items that are out of stock (stock = 0)
        get("/inventory/out-of-stock", (req, res) -> {
            res.header("Content-Type", "application/json");
            res.status(200);
            return DatabaseManager.getOutOfStockItems();
        });

        // Get all items that are overstocked (stock > capacity)
        get("/inventory/overstocked", (req, res) -> {
            res.header("Content-Type", "application/json");
            res.status(200);
            return DatabaseManager.getOverstockedItems();
        });

        // Get all items that are low on stock (< 35% of capacity)
        get("/inventory/low-stock", (req, res) -> {
            res.header("Content-Type", "application/json");
            res.status(200);
            return DatabaseManager.getLowStockItems();
        });

        // Get specific item by ID from inventory
        get("/inventory/:id", (req, res) -> {
            res.header("Content-Type", "application/json");
            try {
                int itemId = Integer.parseInt(req.params(":id"));
                res.status(200);
                return DatabaseManager.getInventoryItemById(itemId);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid item ID format\"}";
            }
        });

        // ================ DISTRIBUTOR GET ROUTES ================
        // Get all distributors with ID and name
        get("/distributors", (req, res) -> {
            res.header("Content-Type", "application/json");
            res.status(200);
            return DatabaseManager.getAllDistributors();
        });

        // Get items distributed by a specific distributor
        get("/distributors/:id/items", (req, res) -> {
            res.header("Content-Type", "application/json");
            try {
                int distributorId = Integer.parseInt(req.params(":id"));
                res.status(200);
                return DatabaseManager.getItemsByDistributor(distributorId);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid distributor ID format\"}";
            }
        });

        // Get all distributor offerings for a specific item
        get("/items/:id/distributors", (req, res) -> {
            res.header("Content-Type", "application/json");
            try {
                int itemId = Integer.parseInt(req.params(":id"));
                res.status(200);
                return DatabaseManager.getDistributorsByItem(itemId);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid item ID format\"}";
            }
        });

        // ================ POST ROUTES ================
        // Add a new item to the database
        post(AppConstants.Endpoints.ITEMS, (req, res) -> {
            ResponseUtils.setJsonHeaders(res);
            try {
                String name = ResponseUtils.validateStringParam(req.queryParams("name"), "Item name");
                String result = DatabaseManager.addItem(name);
                ResponseUtils.setStatusFromResult(res, result);
                return result;
            } catch (IllegalArgumentException e) {
                res.status(400);
                return AppConstants.ErrorMessages.ITEM_NAME_REQUIRED;
            }
        });

        // Add a new item to inventory
        post("/inventory", (req, res) -> {
            res.header("Content-Type", "application/json");
            try {
                int itemId = Integer.parseInt(req.queryParams("itemId"));
                int stock = Integer.parseInt(req.queryParams("stock"));
                int capacity = Integer.parseInt(req.queryParams("capacity"));
                String result = DatabaseManager.addInventoryItem(itemId, stock, capacity);
                if (result.contains("\"success\": false")) {
                    res.status(400);
                } else {
                    res.status(200);
                }
                return result;
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid parameters. itemId, stock, and capacity must be integers\"}";
            }
        });

        // Add a new distributor
        post("/distributors", (req, res) -> {
            res.header("Content-Type", "application/json");
            String name = req.queryParams("name");
            if (name == null || name.trim().isEmpty()) {
                res.status(400);
                return "{\"error\": \"Distributor name is required\"}";
            }
            String result = DatabaseManager.addDistributor(name.trim());
            if (result.contains("\"success\": false")) {
                res.status(400);
            } else {
                res.status(200);
            }
            return result;
        });

        // Add items to a distributor's catalog with cost
        post("/distributors/:id/items", (req, res) -> {
            
            res.header("Content-Type", "application/json");
            try {
                int distributorId = Integer.parseInt(req.params(":id"));
                int itemId = Integer.parseInt(req.queryParams("itemId"));
                double cost = Double.parseDouble(req.queryParams("cost"));
                String result = DatabaseManager.addDistributorPrice(distributorId, itemId, cost);
                if (result.contains("\"success\": false")) {
                    res.status(400);
                } else {
                    res.status(200);
                }
                return result;
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid parameters. distributorId, itemId must be integers, cost must be a number\"}";
            }
        });

        // ================ PUT ROUTES ================
        // Modify existing item in inventory
        put("/inventory/:id", (req, res) -> {
            
            res.header("Content-Type", "application/json");
            try {
                int itemId = Integer.parseInt(req.params(":id"));
                Integer stock = null;
                Integer capacity = null;
                
                if (req.queryParams("stock") != null) {
                    stock = Integer.parseInt(req.queryParams("stock"));
                }
                if (req.queryParams("capacity") != null) {
                    capacity = Integer.parseInt(req.queryParams("capacity"));
                }
                
                String result = DatabaseManager.updateInventoryItem(itemId, stock, capacity);
                if (result.contains("\"success\": false")) {
                    res.status(400);
                } else {
                    res.status(200);
                }
                return result;
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid parameters. stock and capacity must be integers\"}";
            }
        });

        // Modify the price of an item in a distributor's catalog
        put("/distributors/:distributorId/items/:itemId", (req, res) -> {
            
            res.header("Content-Type", "application/json");
            try {
                int distributorId = Integer.parseInt(req.params(":distributorId"));
                int itemId = Integer.parseInt(req.params(":itemId"));
                double cost = Double.parseDouble(req.queryParams("cost"));
                String result = DatabaseManager.updateDistributorPrice(distributorId, itemId, cost);
                if (result.contains("\"success\": false")) {
                    res.status(400);
                } else {
                    res.status(200);
                }
                return result;
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid parameters. cost must be a number\"}";
            }
        });

        // ================ DELETE ROUTES ================
        // Delete an existing item from inventory
        delete("/inventory/:id", (req, res) -> {
            
            res.header("Content-Type", "application/json");
            try {
                int itemId = Integer.parseInt(req.params(":id"));
                String result = DatabaseManager.deleteInventoryItem(itemId);
                if (result.contains("\"success\": false")) {
                    res.status(400);
                } else {
                    res.status(200);
                }
                return result;
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid item ID format\"}";
            }
        });

        // Delete an existing distributor
        delete("/distributors/:id", (req, res) -> {
            
            res.header("Content-Type", "application/json");
            try {
                int distributorId = Integer.parseInt(req.params(":id"));
                String result = DatabaseManager.deleteDistributor(distributorId);
                if (result.contains("\"success\": false")) {
                    res.status(400);
                } else {
                    res.status(200);
                }
                return result;
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid distributor ID format\"}";
            }
        });

        // Delete an item from a distributor's catalog
        delete("/distributors/:distributorId/items/:itemId", (req, res) -> {
            
            res.header("Content-Type", "application/json");
            try {
                int distributorId = Integer.parseInt(req.params(":distributorId"));
                int itemId = Integer.parseInt(req.params(":itemId"));
                String result = DatabaseManager.deleteDistributorPrice(distributorId, itemId);
                if (result.contains("\"success\": false")) {
                    res.status(400);
                } else {
                    res.status(200);
                }
                return result;
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid parameters. distributorId and itemId must be integers\"}";
            }
        });

        // ================ SPECIAL ROUTES ================
        // Get the cheapest price for restocking an item at a given quantity
        get("/items/:id/cheapest", (req, res) -> {
            
            res.header("Content-Type", "application/json");
            try {
                int itemId = Integer.parseInt(req.params(":id"));
                int quantity = Integer.parseInt(req.queryParams("quantity"));
                res.status(200);
                return DatabaseManager.getCheapestRestockPrice(itemId, quantity);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid parameters. quantity must be an integer\"}";
            }
        });

        // Export any table to CSV format
        get("/export/csv", (req, res) -> {
            
            String tableName = req.queryParams("table");
            if (tableName == null || tableName.trim().isEmpty()) {
                res.status(400);
                return "{\"error\": \"Table name is required. Use ?table=tablename\"}";
            }
            
            String csvData = DatabaseManager.exportTableToCsv(tableName.trim());
            if (csvData == null) {
                res.status(400);
                return "{\"error\": \"Invalid table name. Valid tables: items, inventory, distributors, distributor_prices\"}";
            }
            
            res.status(200);
            res.type("text/csv");
            res.header("Content-Disposition", "attachment; filename=" + tableName + ".csv");
            return csvData;
        });
        
        // ================ STREAMING ROUTES ================
        // Server-Sent Events (SSE) endpoint for real-time database updates
        get("/stream/events", (req, res) -> {
            res.header("Content-Type", "text/event-stream");
            res.header("Cache-Control", "no-cache");
            res.header("Connection", "keep-alive");
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Cache-Control");
            
            // Generate unique client ID
            String clientId = "client_" + System.currentTimeMillis() + "_" + Math.random();
            
            try {
                java.io.PrintWriter writer = new java.io.PrintWriter(res.raw().getOutputStream());
                
                // Send initial connection event
                writer.write("event: connected\n");
                writer.write("data: {\"clientId\": \"" + clientId + "\", \"message\": \"Connected to TopBloc live updates\"}\n\n");
                writer.flush();
                
                // Create streaming client with simplified sender
                DatabaseManager.StreamingClient client = new DatabaseManager.StreamingClient(
                    clientId,
                    java.util.concurrent.CompletableFuture.completedFuture(null),
                    (data) -> {
                        try {
                            writer.write("event: update\n");
                            writer.write(data);
                            writer.flush();
                        } catch (Exception e) {
                            System.out.println("Failed to write to client " + clientId + ": " + e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                );
                
                // Add client to the streaming pool
                DatabaseManager.addStreamingClient(client);
                
                // Send periodic heartbeat to keep connection alive and detect disconnects
                Thread heartbeatThread = new Thread(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            Thread.sleep(15000); // 15 seconds
                            try {
                                writer.write("event: heartbeat\n");
                                writer.write("data: {\"timestamp\": " + System.currentTimeMillis() + "}\n\n");
                                writer.flush();
                                
                                // Update client activity to prevent stale cleanup
                                client.updateActivity();
                                
                            } catch (Exception e) {
                                // Client disconnected
                                DatabaseManager.removeStreamingClient(clientId);
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                        DatabaseManager.removeStreamingClient(clientId);
                        Thread.currentThread().interrupt();
                    }
                });
                heartbeatThread.setDaemon(true);
                heartbeatThread.start();
                
                // This will keep the connection open indefinitely
                // The client disconnect will be handled by the heartbeat failure
                while (!heartbeatThread.isInterrupted()) {
                    Thread.sleep(5000);
                    // Check if heartbeat thread is still alive
                    if (!heartbeatThread.isAlive()) {
                        break;
                    }
                }
                
            } catch (Exception e) {
                DatabaseManager.removeStreamingClient(clientId);
                res.status(500);
                return "{\"error\": \"Failed to establish streaming connection: " + e.getMessage() + "\"}";
            } finally {
                DatabaseManager.removeStreamingClient(clientId);
            }
            
            return "";
        });
        
        // Simple streaming dashboard endpoint
        get("/stream", (req, res) -> {
            res.header("Content-Type", "text/html");
            return TemplateLoader.getStreamingDashboard();
        });
        
        // Wait for initialization and start server
        awaitInitialization();
        System.out.println("TopBloc server started on http://localhost:4567");
        System.out.println("Live updates dashboard: http://localhost:4567/stream");
        
        // Start periodic cleanup task for streaming clients
        Thread cleanupTask = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(30000); // Clean up every 30 seconds
                    DatabaseManager.cleanupStaleClients();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        cleanupTask.setDaemon(true);
        cleanupTask.start();
        System.out.println("Started periodic client cleanup task");
    }
}