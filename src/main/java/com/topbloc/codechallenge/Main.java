package com.topbloc.codechallenge;

import com.topbloc.codechallenge.db.DatabaseManager;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
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
        get("/items", (req, res) -> DatabaseManager.getItems());
        get("/version", (req, res) -> "TopBloc Code Challenge v1.0");

        // ================ INVENTORY GET ROUTES ================
        // Get all items in inventory with name, ID, stock, and capacity
        get("/inventory", (req, res) -> {
            res.type("application/json");
            return DatabaseManager.getAllInventory();
        });

        // Get all items that are out of stock (stock = 0)
        get("/inventory/out-of-stock", (req, res) -> {
            res.type("application/json");
            return DatabaseManager.getOutOfStockItems();
        });

        // Get all items that are overstocked (stock > capacity)
        get("/inventory/overstocked", (req, res) -> {
            res.type("application/json");
            return DatabaseManager.getOverstockedItems();
        });

        // Get all items that are low on stock (< 35% of capacity)
        get("/inventory/low-stock", (req, res) -> {
            res.type("application/json");
            return DatabaseManager.getLowStockItems();
        });

        // Get specific item by ID from inventory
        get("/inventory/:id", (req, res) -> {
            res.type("application/json");
            try {
                int itemId = Integer.parseInt(req.params(":id"));
                return DatabaseManager.getInventoryItemById(itemId);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid item ID format\"}";
            }
        });

        // ================ DISTRIBUTOR GET ROUTES ================
        // Get all distributors with ID and name
        get("/distributors", (req, res) -> {
            res.type("application/json");
            return DatabaseManager.getAllDistributors();
        });

        // Get items distributed by a specific distributor
        get("/distributors/:id/items", (req, res) -> {
            res.type("application/json");
            try {
                int distributorId = Integer.parseInt(req.params(":id"));
                return DatabaseManager.getItemsByDistributor(distributorId);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid distributor ID format\"}";
            }
        });

        // Get all distributor offerings for a specific item
        get("/items/:id/distributors", (req, res) -> {
            res.type("application/json");
            try {
                int itemId = Integer.parseInt(req.params(":id"));
                return DatabaseManager.getDistributorsByItem(itemId);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid item ID format\"}";
            }
        });

        // ================ POST ROUTES ================
        // Add a new item to the database
        post("/items", (req, res) -> {
            res.type("application/json");
            String name = req.queryParams("name");
            if (name == null || name.trim().isEmpty()) {
                res.status(400);
                return "{\"error\": \"Item name is required\"}";
            }
            return DatabaseManager.addItem(name.trim());
        });

        // Add a new item to inventory
        post("/inventory", (req, res) -> {
            res.type("application/json");
            try {
                int itemId = Integer.parseInt(req.queryParams("itemId"));
                int stock = Integer.parseInt(req.queryParams("stock"));
                int capacity = Integer.parseInt(req.queryParams("capacity"));
                return DatabaseManager.addInventoryItem(itemId, stock, capacity);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid parameters. itemId, stock, and capacity must be integers\"}";
            }
        });

        // Add a new distributor
        post("/distributors", (req, res) -> {
            res.type("application/json");
            String name = req.queryParams("name");
            if (name == null || name.trim().isEmpty()) {
                res.status(400);
                return "{\"error\": \"Distributor name is required\"}";
            }
            return DatabaseManager.addDistributor(name.trim());
        });

        // Add items to a distributor's catalog with cost
        post("/distributors/:id/items", (req, res) -> {
            res.type("application/json");
            try {
                int distributorId = Integer.parseInt(req.params(":id"));
                int itemId = Integer.parseInt(req.queryParams("itemId"));
                double cost = Double.parseDouble(req.queryParams("cost"));
                return DatabaseManager.addDistributorPrice(distributorId, itemId, cost);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid parameters. distributorId, itemId must be integers, cost must be a number\"}";
            }
        });

        // ================ PUT ROUTES ================
        // Modify existing item in inventory
        put("/inventory/:id", (req, res) -> {
            res.type("application/json");
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
                
                return DatabaseManager.updateInventoryItem(itemId, stock, capacity);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid parameters. stock and capacity must be integers\"}";
            }
        });

        // Modify the price of an item in a distributor's catalog
        put("/distributors/:distributorId/items/:itemId", (req, res) -> {
            res.type("application/json");
            try {
                int distributorId = Integer.parseInt(req.params(":distributorId"));
                int itemId = Integer.parseInt(req.params(":itemId"));
                double cost = Double.parseDouble(req.queryParams("cost"));
                return DatabaseManager.updateDistributorPrice(distributorId, itemId, cost);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid parameters. cost must be a number\"}";
            }
        });

        // ================ DELETE ROUTES ================
        // Delete an existing item from inventory
        delete("/inventory/:id", (req, res) -> {
            res.type("application/json");
            try {
                int itemId = Integer.parseInt(req.params(":id"));
                return DatabaseManager.deleteInventoryItem(itemId);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid item ID format\"}";
            }
        });

        // Delete an existing distributor
        delete("/distributors/:id", (req, res) -> {
            res.type("application/json");
            try {
                int distributorId = Integer.parseInt(req.params(":id"));
                return DatabaseManager.deleteDistributor(distributorId);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid distributor ID format\"}";
            }
        });

        // ================ SPECIAL ROUTES ================
        // Get the cheapest price for restocking an item at a given quantity
        get("/items/:id/cheapest", (req, res) -> {
            res.type("application/json");
            try {
                int itemId = Integer.parseInt(req.params(":id"));
                int quantity = Integer.parseInt(req.queryParams("quantity"));
                return DatabaseManager.getCheapestRestockPrice(itemId, quantity);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid parameters. quantity must be an integer\"}";
            }
        });
    }
}