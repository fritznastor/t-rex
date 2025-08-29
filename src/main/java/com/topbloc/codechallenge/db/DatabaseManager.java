package com.topbloc.codechallenge.db;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DatabaseManager {
    private static final String jdbcPrefix = "jdbc:sqlite:";
    private static final String dbName = "challenge.db";
    private static String connectionString;
    private static Connection conn;

    static {
        File dbFile = new File(dbName);
        connectionString = jdbcPrefix + dbFile.getAbsolutePath();
    }

    public static void connect() {
        try {
            Connection connection = DriverManager.getConnection(connectionString);
            System.out.println("Connection to SQLite has been established.");
            conn = connection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // Schema function to reset the database if needed - do not change
    public static void resetDatabase() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        File dbFile = new File(dbName);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        connectionString = jdbcPrefix + dbFile.getAbsolutePath();
        connect();
        applySchema();
        seedDatabase();
    }

    // Schema function to reset the database if needed - do not change
    private static void applySchema() {
        String itemsSql = "CREATE TABLE IF NOT EXISTS items (\n"
                + "id integer PRIMARY KEY,\n"
                + "name text NOT NULL UNIQUE\n"
                + ");";
        String inventorySql = "CREATE TABLE IF NOT EXISTS inventory (\n"
                + "id integer PRIMARY KEY,\n"
                + "item integer NOT NULL UNIQUE references items(id) ON DELETE CASCADE,\n"
                + "stock integer NOT NULL,\n"
                + "capacity integer NOT NULL\n"
                + ");";
        String distributorSql = "CREATE TABLE IF NOT EXISTS distributors (\n"
                + "id integer PRIMARY KEY,\n"
                + "name text NOT NULL UNIQUE\n"
                + ");";
        String distributorPricesSql = "CREATE TABLE IF NOT EXISTS distributor_prices (\n"
                + "id integer PRIMARY KEY,\n"
                + "distributor integer NOT NULL references distributors(id) ON DELETE CASCADE,\n"
                + "item integer NOT NULL references items(id) ON DELETE CASCADE,\n"
                + "cost float NOT NULL\n" +
                ");";

        try {
            System.out.println("Applying schema");
            conn.createStatement().execute(itemsSql);
            conn.createStatement().execute(inventorySql);
            conn.createStatement().execute(distributorSql);
            conn.createStatement().execute(distributorPricesSql);
            System.out.println("Schema applied");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Schema function to reset the database if needed - do not change
    private static void seedDatabase() {
        String itemsSql = "INSERT INTO items (id, name) VALUES (1, 'Licorice'), (2, 'Good & Plenty'),\n"
            + "(3, 'Smarties'), (4, 'Tootsie Rolls'), (5, 'Necco Wafers'), (6, 'Wax Cola Bottles'), (7, 'Circus Peanuts'), (8, 'Candy Corn'),\n"
            + "(9, 'Twix'), (10, 'Snickers'), (11, 'M&Ms'), (12, 'Skittles'), (13, 'Starburst'), (14, 'Butterfinger'), (15, 'Peach Rings'), (16, 'Gummy Bears'), (17, 'Sour Patch Kids')";
        String inventorySql = "INSERT INTO inventory (item, stock, capacity) VALUES\n"
                + "(1, 22, 25), (2, 4, 20), (3, 15, 25), (4, 30, 50), (5, 14, 15), (6, 8, 10), (7, 10, 10), (8, 30, 40), (9, 17, 70), (10, 43, 65),\n" +
                "(11, 32, 55), (12, 25, 45), (13, 8, 45), (14, 10, 60), (15, 20, 30), (16, 15, 35), (17, 14, 60)";
        String distributorSql = "INSERT INTO distributors (id, name) VALUES (1, 'Candy Corp'), (2, 'The Sweet Suite'), (3, 'Dentists Hate Us')";
        String distributorPricesSql = "INSERT INTO distributor_prices (distributor, item, cost) VALUES \n" +
                "(1, 1, 0.81), (1, 2, 0.46), (1, 3, 0.89), (1, 4, 0.45), (2, 2, 0.18), (2, 3, 0.54), (2, 4, 0.67), (2, 5, 0.25), (2, 6, 0.35), (2, 7, 0.23), (2, 8, 0.41), (2, 9, 0.54),\n" +
                "(2, 10, 0.25), (2, 11, 0.52), (2, 12, 0.07), (2, 13, 0.77), (2, 14, 0.93), (2, 15, 0.11), (2, 16, 0.42), (3, 10, 0.47), (3, 11, 0.84), (3, 12, 0.15), (3, 13, 0.07), (3, 14, 0.97),\n" +
                "(3, 15, 0.39), (3, 16, 0.91), (3, 17, 0.85)";

        try {
            System.out.println("Seeding database");
            conn.createStatement().execute(itemsSql);
            conn.createStatement().execute(inventorySql);
            conn.createStatement().execute(distributorSql);
            conn.createStatement().execute(distributorPricesSql);
            System.out.println("Database seeded");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Helper methods to convert ResultSet to JSON - change if desired, but should not be required
    @SuppressWarnings("unchecked")
    private static JSONArray convertResultSetToJson(ResultSet rs) throws SQLException{
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<String> colNames = IntStream.range(0, columns)
                .mapToObj(i -> {
                    try {
                        return md.getColumnName(i + 1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());

        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            jsonArray.add(convertRowToJson(rs, colNames));
        }
        return jsonArray;
    }

    @SuppressWarnings("unchecked")
    private static JSONObject convertRowToJson(ResultSet rs, List<String> colNames) throws SQLException {
        JSONObject obj = new JSONObject();
        for (String colName : colNames) {
            obj.put(colName, rs.getObject(colName));
        }
        return obj;
    }

    // Controller functions - add your routes here. getItems is provided as an example
    public static JSONArray getItems() {
        String sql = "SELECT * FROM items";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // ================ INVENTORY GET METHODS ================
    public static JSONArray getAllInventory() {
        String sql = "SELECT i.id, i.name, inv.stock, inv.capacity " +
                    "FROM items i " +
                    "JOIN inventory inv ON i.id = inv.item " +
                    "ORDER BY i.id";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new JSONArray();
        }
    }

    public static JSONArray getOutOfStockItems() {
        String sql = "SELECT i.id, i.name, inv.stock, inv.capacity " +
                    "FROM items i " +
                    "JOIN inventory inv ON i.id = inv.item " +
                    "WHERE inv.stock = 0 " +
                    "ORDER BY i.id";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new JSONArray();
        }
    }

    public static JSONArray getOverstockedItems() {
        String sql = "SELECT i.id, i.name, inv.stock, inv.capacity " +
                    "FROM items i " +
                    "JOIN inventory inv ON i.id = inv.item " +
                    "WHERE inv.stock > inv.capacity " +
                    "ORDER BY i.id";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new JSONArray();
        }
    }

    public static JSONArray getLowStockItems() {
        String sql = "SELECT i.id, i.name, inv.stock, inv.capacity " +
                    "FROM items i " +
                    "JOIN inventory inv ON i.id = inv.item " +
                    "WHERE inv.stock < (inv.capacity * 0.35) " +
                    "ORDER BY i.id";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new JSONArray();
        }
    }

    public static JSONArray getInventoryItemById(int itemId) {
        String sql = "SELECT i.id, i.name, inv.stock, inv.capacity " +
                    "FROM items i " +
                    "JOIN inventory inv ON i.id = inv.item " +
                    "WHERE i.id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, itemId);
            ResultSet set = pstmt.executeQuery();
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new JSONArray();
        }
    }

    // ================ DISTRIBUTOR GET METHODS ================
    public static JSONArray getAllDistributors() {
        String sql = "SELECT id, name FROM distributors ORDER BY id";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new JSONArray();
        }
    }

    public static JSONArray getItemsByDistributor(int distributorId) {
        String sql = "SELECT i.id, i.name, dp.cost " +
                    "FROM items i " +
                    "JOIN distributor_prices dp ON i.id = dp.item " +
                    "WHERE dp.distributor = ? " +
                    "ORDER BY i.id";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, distributorId);
            ResultSet set = pstmt.executeQuery();
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new JSONArray();
        }
    }

    public static JSONArray getDistributorsByItem(int itemId) {
        String sql = "SELECT d.id, d.name, dp.cost " +
                    "FROM distributors d " +
                    "JOIN distributor_prices dp ON d.id = dp.distributor " +
                    "WHERE dp.item = ? " +
                    "ORDER BY dp.cost ASC";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, itemId);
            ResultSet set = pstmt.executeQuery();
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new JSONArray();
        }
    }

    // ================ POST METHODS ================
    public static String addItem(String name) {
        String sql = "INSERT INTO items (name) VALUES (?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // For SQLite, we can use a simpler approach to get the last ID
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()");
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        return "{\"success\": true, \"message\": \"Item added successfully\", \"id\": " + id + "}";
                    }
                } catch (SQLException e) {
                    // If getting the ID fails, still return success since the insert worked
                    System.out.println("Warning: Could not get inserted ID: " + e.getMessage());
                    return "{\"success\": true, \"message\": \"Item added successfully\"}";
                }
            }
            return "{\"success\": false, \"message\": \"Failed to add item\"}";
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                return "{\"success\": false, \"message\": \"Item with this name already exists\"}";
            }
            System.out.println(e.getMessage());
            return "{\"success\": false, \"message\": \"Database error: " + e.getMessage() + "\"}";
        }
    }

    public static String addInventoryItem(int itemId, int stock, int capacity) {
        // First check if item exists
        String checkItemSql = "SELECT id FROM items WHERE id = ?";
        String insertSql = "INSERT INTO inventory (item, stock, capacity) VALUES (?, ?, ?)";
        
        try {
            PreparedStatement checkStmt = conn.prepareStatement(checkItemSql);
            checkStmt.setInt(1, itemId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                return "{\"success\": false, \"message\": \"Item with ID " + itemId + " does not exist\"}";
            }
            
            PreparedStatement pstmt = conn.prepareStatement(insertSql);
            pstmt.setInt(1, itemId);
            pstmt.setInt(2, stock);
            pstmt.setInt(3, capacity);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                Statement stmt = conn.createStatement();
                ResultSet lastIdRs = stmt.executeQuery("SELECT last_insert_rowid()");
                if (lastIdRs.next()) {
                    int id = lastIdRs.getInt(1);
                    return "{\"success\": true, \"message\": \"Inventory item added successfully\", \"id\": " + id + "}";
                }
            }
            return "{\"success\": false, \"message\": \"Failed to add inventory item\"}";
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                return "{\"success\": false, \"message\": \"Inventory item for this product already exists\"}";
            }
            System.out.println(e.getMessage());
            return "{\"success\": false, \"message\": \"Database error: " + e.getMessage() + "\"}";
        }
    }

    public static String addDistributor(String name) {
        String sql = "INSERT INTO distributors (name) VALUES (?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()");
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return "{\"success\": true, \"message\": \"Distributor added successfully\", \"id\": " + id + "}";
                }
            }
            return "{\"success\": false, \"message\": \"Failed to add distributor\"}";
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                return "{\"success\": false, \"message\": \"Distributor with this name already exists\"}";
            }
            System.out.println(e.getMessage());
            return "{\"success\": false, \"message\": \"Database error: " + e.getMessage() + "\"}";
        }
    }

    public static String addDistributorPrice(int distributorId, int itemId, double cost) {
        // Check if distributor and item exist
        String checkDistributorSql = "SELECT id FROM distributors WHERE id = ?";
        String checkItemSql = "SELECT id FROM items WHERE id = ?";
        String insertSql = "INSERT INTO distributor_prices (distributor, item, cost) VALUES (?, ?, ?)";
        
        try {
            PreparedStatement checkDistStmt = conn.prepareStatement(checkDistributorSql);
            checkDistStmt.setInt(1, distributorId);
            ResultSet rs1 = checkDistStmt.executeQuery();
            
            if (!rs1.next()) {
                return "{\"success\": false, \"message\": \"Distributor with ID " + distributorId + " does not exist\"}";
            }
            
            PreparedStatement checkItemStmt = conn.prepareStatement(checkItemSql);
            checkItemStmt.setInt(1, itemId);
            ResultSet rs2 = checkItemStmt.executeQuery();
            
            if (!rs2.next()) {
                return "{\"success\": false, \"message\": \"Item with ID " + itemId + " does not exist\"}";
            }
            
            PreparedStatement pstmt = conn.prepareStatement(insertSql);
            pstmt.setInt(1, distributorId);
            pstmt.setInt(2, itemId);
            pstmt.setDouble(3, cost);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()");
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return "{\"success\": true, \"message\": \"Distributor price added successfully\", \"id\": " + id + "}";
                }
            }
            return "{\"success\": false, \"message\": \"Failed to add distributor price\"}";
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "{\"success\": false, \"message\": \"Database error: " + e.getMessage() + "\"}";
        }
    }

    // ================ PUT METHODS ================
    public static String updateInventoryItem(int itemId, Integer stock, Integer capacity) {
        StringBuilder sql = new StringBuilder("UPDATE inventory SET ");
        boolean hasStock = stock != null;
        boolean hasCapacity = capacity != null;
        
        if (!hasStock && !hasCapacity) {
            return "{\"success\": false, \"message\": \"At least one parameter (stock or capacity) must be provided\"}";
        }
        
        if (hasStock) {
            sql.append("stock = ?");
        }
        if (hasCapacity) {
            if (hasStock) sql.append(", ");
            sql.append("capacity = ?");
        }
        sql.append(" WHERE item = ?");
        
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            
            if (hasStock) {
                pstmt.setInt(paramIndex++, stock);
            }
            if (hasCapacity) {
                pstmt.setInt(paramIndex++, capacity);
            }
            pstmt.setInt(paramIndex, itemId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "{\"success\": true, \"message\": \"Inventory item updated successfully\"}";
            } else {
                return "{\"success\": false, \"message\": \"Inventory item with ID " + itemId + " not found\"}";
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "{\"success\": false, \"message\": \"Database error: " + e.getMessage() + "\"}";
        }
    }

    public static String updateDistributorPrice(int distributorId, int itemId, double cost) {
        String sql = "UPDATE distributor_prices SET cost = ? WHERE distributor = ? AND item = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, cost);
            pstmt.setInt(2, distributorId);
            pstmt.setInt(3, itemId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "{\"success\": true, \"message\": \"Distributor price updated successfully\"}";
            } else {
                return "{\"success\": false, \"message\": \"No price record found for distributor " + distributorId + " and item " + itemId + "\"}";
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "{\"success\": false, \"message\": \"Database error: " + e.getMessage() + "\"}";
        }
    }

    // ================ DELETE METHODS ================
    public static String deleteInventoryItem(int itemId) {
        String sql = "DELETE FROM inventory WHERE item = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, itemId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "{\"success\": true, \"message\": \"Inventory item deleted successfully\"}";
            } else {
                return "{\"success\": false, \"message\": \"Inventory item with ID " + itemId + " not found\"}";
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "{\"success\": false, \"message\": \"Database error: " + e.getMessage() + "\"}";
        }
    }

    public static String deleteDistributor(int distributorId) {
        String sql = "DELETE FROM distributors WHERE id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, distributorId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "{\"success\": true, \"message\": \"Distributor deleted successfully\"}";
            } else {
                return "{\"success\": false, \"message\": \"Distributor with ID " + distributorId + " not found\"}";
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "{\"success\": false, \"message\": \"Database error: " + e.getMessage() + "\"}";
        }
    }

    // ================ SPECIAL METHODS ================
    @SuppressWarnings("unchecked")
    public static JSONObject getCheapestRestockPrice(int itemId, int quantity) {
        String sql = "SELECT d.id, d.name, dp.cost, (dp.cost * ?) as total_cost " +
                    "FROM distributors d " +
                    "JOIN distributor_prices dp ON d.id = dp.distributor " +
                    "WHERE dp.item = ? " +
                    "ORDER BY dp.cost ASC " +
                    "LIMIT 1";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, itemId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                JSONObject result = new JSONObject();
                result.put("item_id", itemId);
                result.put("quantity", quantity);
                result.put("distributor_id", rs.getInt("id"));
                result.put("distributor_name", rs.getString("name"));
                result.put("unit_cost", rs.getDouble("cost"));
                result.put("total_cost", rs.getDouble("total_cost"));
                return result;
            } else {
                JSONObject error = new JSONObject();
                error.put("success", false);
                error.put("message", "No distributors found for item ID " + itemId);
                return error;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            JSONObject error = new JSONObject();
            error.put("success", false);
            error.put("message", "Database error: " + e.getMessage());
            return error;
        }
    }
}
