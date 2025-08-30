package com.topbloc.codechallenge;

import com.topbloc.codechallenge.db.DatabaseManager;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        // Set port
        port(4567);
        
        // Enable CORS globally
        before("*", (req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
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
        get("/items", (req, res) -> {
            res.header("Content-Type", "application/json");
            return DatabaseManager.getItems();
        });
        get("/version", (req, res) -> {
            return "TopBloc Code Challenge v1.0";
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
        post("/items", (req, res) -> {
            res.header("Content-Type", "application/json");
            String name = req.queryParams("name");
            if (name == null || name.trim().isEmpty()) {
                res.status(400);
                return "{\"error\": \"Item name is required\"}";
            }
            String result = DatabaseManager.addItem(name.trim());
            if (result.contains("\"success\": false")) {
                res.status(400);
            } else {
                res.status(200);
            }
            return result;
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
        
        // Wait for initialization and start server
        awaitInitialization();
        System.out.println("TopBloc server started on http://localhost:4567");
    }
}