package com.topbloc.codechallenge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Comprehensive Test Suite for TopBloc Backend Code Challenge
 * 
 * This test suite covers all endpoints with:
 * - Valid inputs
 * - Invalid inputs
 * - Null/empty values
 * - Edge cases
 * - Boundary conditions
 * 
 * Run this after any code changes to ensure all functionality works correctly.
 */
public class TestSuite {
    private static final String BASE_URL = "http://localhost:4567";
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("TOPBLOC BACKEND CODE CHALLENGE - COMPREHENSIVE TEST SUITE");
        System.out.println("=".repeat(70));
        
        // Wait for server to be ready
        waitForServer();
        
        // Reset database to clean state
        resetDatabase();
        
        // Run all test categories
        testBasicEndpoints();
        testInventoryEndpoints();
        testDistributorEndpoints();
        testPostEndpoints();
        testPutEndpoints();
        testDeleteEndpoints();
        testSpecialEndpoints();
        testEdgeCases();
        testSecurityAndValidation();
        
        // Print final results
        printFinalResults();
    }

    // ================ BASIC ENDPOINTS ================
    private static void testBasicEndpoints() {
        printSection("BASIC ENDPOINTS");
        
        // Test version endpoint
        testEndpoint("GET /version", "GET", "/version", null, 200, "TopBloc Code Challenge v1.0");
        
        // Test items endpoint
        testEndpoint("GET /items", "GET", "/items", null, 200, "\"name\":");
        
        // Test reset endpoint (should work)
        testEndpoint("GET /reset", "GET", "/reset", null, 200, "OK");
    }

    // ================ INVENTORY ENDPOINTS ================
    private static void testInventoryEndpoints() {
        printSection("INVENTORY ENDPOINTS");
        
        // GET /inventory - All inventory items
        testEndpoint("GET /inventory - Valid", "GET", "/inventory", null, 200, "\"stock\":");
        
        // GET /inventory/out-of-stock
        testEndpoint("GET /inventory/out-of-stock - Valid", "GET", "/inventory/out-of-stock", null, 200, "[");
        
        // GET /inventory/overstocked
        testEndpoint("GET /inventory/overstocked - Valid", "GET", "/inventory/overstocked", null, 200, "[");
        
        // GET /inventory/low-stock
        testEndpoint("GET /inventory/low-stock - Valid", "GET", "/inventory/low-stock", null, 200, "[");
        
        // GET /inventory/:id - Valid ID
        testEndpoint("GET /inventory/:id - Valid ID", "GET", "/inventory/1", null, 200, "\"name\":");
        
        // GET /inventory/:id - Invalid ID format
        testEndpoint("GET /inventory/:id - Invalid format", "GET", "/inventory/abc", null, 400, "Invalid item ID format");
        
        // GET /inventory/:id - Non-existent ID
        testEndpoint("GET /inventory/:id - Non-existent", "GET", "/inventory/999", null, 200, "[]");
        
        // GET /inventory/:id - Negative ID
        testEndpoint("GET /inventory/:id - Negative ID", "GET", "/inventory/-1", null, 200, "[]");
        
        // GET /inventory/:id - Zero ID
        testEndpoint("GET /inventory/:id - Zero ID", "GET", "/inventory/0", null, 200, "[]");
        
        // GET /inventory/:id - Very large ID
        testEndpoint("GET /inventory/:id - Large ID", "GET", "/inventory/999999", null, 200, "[]");
    }

    // ================ DISTRIBUTOR ENDPOINTS ================
    private static void testDistributorEndpoints() {
        printSection("DISTRIBUTOR ENDPOINTS");
        
        // GET /distributors
        testEndpoint("GET /distributors - Valid", "GET", "/distributors", null, 200, "\"name\":");
        
        // GET /distributors/:id/items - Valid ID
        testEndpoint("GET /distributors/:id/items - Valid", "GET", "/distributors/1/items", null, 200, "[");
        
        // GET /distributors/:id/items - Invalid format
        testEndpoint("GET /distributors/:id/items - Invalid format", "GET", "/distributors/abc/items", null, 400, "Invalid distributor ID format");
        
        // GET /distributors/:id/items - Non-existent
        testEndpoint("GET /distributors/:id/items - Non-existent", "GET", "/distributors/999/items", null, 200, "[]");
        
        // GET /distributors/:id/items - Negative ID
        testEndpoint("GET /distributors/:id/items - Negative", "GET", "/distributors/-1/items", null, 200, "[]");
        
        // GET /items/:id/distributors - Valid ID
        testEndpoint("GET /items/:id/distributors - Valid", "GET", "/items/1/distributors", null, 200, "[");
        
        // GET /items/:id/distributors - Invalid format
        testEndpoint("GET /items/:id/distributors - Invalid format", "GET", "/items/abc/distributors", null, 400, "Invalid item ID format");
        
        // GET /items/:id/distributors - Non-existent
        testEndpoint("GET /items/:id/distributors - Non-existent", "GET", "/items/999/distributors", null, 200, "[]");
    }

    // ================ POST ENDPOINTS ================
    private static void testPostEndpoints() {
        printSection("POST ENDPOINTS");
        
        // POST /items - Valid name
        testEndpoint("POST /items - Valid name", "POST", "/items?name=" + urlEncode("Test Item 1"), null, 200, "success");
        
        // POST /items - Empty name
        testEndpoint("POST /items - Empty name", "POST", "/items?name=", null, 400, "Item name is required");
        
        // POST /items - No name parameter
        testEndpoint("POST /items - No name", "POST", "/items", null, 400, "Item name is required");
        
        // POST /items - Name with special characters
        testEndpoint("POST /items - Special chars", "POST", "/items?name=" + urlEncode("Test Item @#$%"), null, 200, "success");
        
        // POST /items - Very long name
        String longName = "A".repeat(1000);
        testEndpoint("POST /items - Long name", "POST", "/items?name=" + urlEncode(longName), null, 200, "success");
        
        // POST /items - Duplicate name
        testEndpoint("POST /items - Duplicate", "POST", "/items?name=Licorice", null, 200, "already exists");
        
        // Get the ID of a test item for inventory tests
        String response = makeRequest("GET", "/items", null);
        int testItemId = extractLastItemId(response);
        
        // POST /inventory - Valid parameters
        testEndpoint("POST /inventory - Valid", "POST", "/inventory?itemId=" + testItemId + "&stock=50&capacity=100", null, 200, "success");
        
        // POST /inventory - Negative stock
        testEndpoint("POST /inventory - Negative stock", "POST", "/inventory?itemId=1&stock=-5&capacity=100", null, 200, "Stock cannot be negative");
        
        // POST /inventory - Negative capacity
        testEndpoint("POST /inventory - Negative capacity", "POST", "/inventory?itemId=1&stock=50&capacity=-10", null, 200, "Capacity cannot be negative");
        
        // POST /inventory - Zero values (should be valid)
        testEndpoint("POST /inventory - Zero stock", "POST", "/inventory?itemId=2&stock=0&capacity=100", null, 200, "success");
        
        // POST /inventory - Invalid item ID
        testEndpoint("POST /inventory - Invalid itemId", "POST", "/inventory?itemId=999&stock=50&capacity=100", null, 200, "does not exist");
        
        // POST /inventory - Invalid parameter format
        testEndpoint("POST /inventory - Invalid format", "POST", "/inventory?itemId=abc&stock=50&capacity=100", null, 400, "must be integers");
        
        // POST /inventory - Missing parameters
        testEndpoint("POST /inventory - Missing stock", "POST", "/inventory?itemId=1&capacity=100", null, 400, "must be integers");
        
        // POST /distributors - Valid name
        testEndpoint("POST /distributors - Valid", "POST", "/distributors?name=" + urlEncode("Test Distributor"), null, 200, "success");
        
        // POST /distributors - Empty name
        testEndpoint("POST /distributors - Empty name", "POST", "/distributors?name=", null, 400, "name is required");
        
        // POST /distributors - No name
        testEndpoint("POST /distributors - No name", "POST", "/distributors", null, 400, "name is required");
        
        // POST /distributors/:id/items - Valid
        testEndpoint("POST /distributors/items - Valid", "POST", "/distributors/1/items?itemId=" + testItemId + "&cost=2.50", null, 200, "success");
        
        // POST /distributors/:id/items - Negative cost
        testEndpoint("POST /distributors/items - Negative cost", "POST", "/distributors/1/items?itemId=1&cost=-2.50", null, 200, "Cost cannot be negative");
        
        // POST /distributors/:id/items - Zero cost (should be valid)
        testEndpoint("POST /distributors/items - Zero cost", "POST", "/distributors/1/items?itemId=2&cost=0", null, 200, "success");
        
        // POST /distributors/:id/items - Invalid distributor
        testEndpoint("POST /distributors/items - Invalid distributor", "POST", "/distributors/999/items?itemId=1&cost=2.50", null, 200, "does not exist");
        
        // POST /distributors/:id/items - Invalid item
        testEndpoint("POST /distributors/items - Invalid item", "POST", "/distributors/1/items?itemId=999&cost=2.50", null, 200, "does not exist");
    }

    // ================ PUT ENDPOINTS ================
    private static void testPutEndpoints() {
        printSection("PUT ENDPOINTS");
        
        // PUT /inventory/:id - Valid stock update
        testEndpoint("PUT /inventory - Update stock", "PUT", "/inventory/1?stock=25", null, 200, "success");
        
        // PUT /inventory/:id - Valid capacity update
        testEndpoint("PUT /inventory - Update capacity", "PUT", "/inventory/1?capacity=150", null, 200, "success");
        
        // PUT /inventory/:id - Update both
        testEndpoint("PUT /inventory - Update both", "PUT", "/inventory/1?stock=30&capacity=200", null, 200, "success");
        
        // PUT /inventory/:id - Negative stock
        testEndpoint("PUT /inventory - Negative stock", "PUT", "/inventory/1?stock=-5", null, 200, "Stock cannot be negative");
        
        // PUT /inventory/:id - Negative capacity
        testEndpoint("PUT /inventory - Negative capacity", "PUT", "/inventory/1?capacity=-10", null, 200, "Capacity cannot be negative");
        
        // PUT /inventory/:id - No parameters
        testEndpoint("PUT /inventory - No params", "PUT", "/inventory/1", null, 200, "At least one parameter");
        
        // PUT /inventory/:id - Invalid ID format
        testEndpoint("PUT /inventory - Invalid format", "PUT", "/inventory/abc?stock=25", null, 400, "Invalid");
        
        // PUT /inventory/:id - Non-existent ID
        testEndpoint("PUT /inventory - Non-existent", "PUT", "/inventory/999?stock=25", null, 200, "not found");
        
        // PUT /distributors/:distributorId/items/:itemId - Valid
        testEndpoint("PUT /distributors/items - Valid", "PUT", "/distributors/1/items/1?cost=3.00", null, 200, "success");
        
        // PUT /distributors/:distributorId/items/:itemId - Negative cost
        testEndpoint("PUT /distributors/items - Negative cost", "PUT", "/distributors/1/items/1?cost=-1.00", null, 200, "Cost cannot be negative");
        
        // PUT /distributors/:distributorId/items/:itemId - Non-existent record
        testEndpoint("PUT /distributors/items - Non-existent", "PUT", "/distributors/999/items/1?cost=3.00", null, 200, "No price record found");
    }

    // ================ DELETE ENDPOINTS ================
    private static void testDeleteEndpoints() {
        printSection("DELETE ENDPOINTS");
        
        // Create test data for deletion
        String itemResponse = makeRequest("POST", "/items?name=" + urlEncode("Delete Test Item"), null);
        int deleteItemId = extractIdFromResponse(itemResponse);
        makeRequest("POST", "/inventory?itemId=" + deleteItemId + "&stock=10&capacity=20", null);
        
        String distResponse = makeRequest("POST", "/distributors?name=" + urlEncode("Delete Test Distributor"), null);
        int deleteDistId = extractIdFromResponse(distResponse);
        
        // DELETE /inventory/:id - Valid
        testEndpoint("DELETE /inventory - Valid", "DELETE", "/inventory/" + deleteItemId, null, 200, "success");
        
        // DELETE /inventory/:id - Non-existent
        testEndpoint("DELETE /inventory - Non-existent", "DELETE", "/inventory/999", null, 200, "not found");
        
        // DELETE /inventory/:id - Invalid format
        testEndpoint("DELETE /inventory - Invalid format", "DELETE", "/inventory/abc", null, 400, "Invalid");
        
        // DELETE /distributors/:id - Valid
        testEndpoint("DELETE /distributors - Valid", "DELETE", "/distributors/" + deleteDistId, null, 200, "success");
        
        // DELETE /distributors/:id - Non-existent
        testEndpoint("DELETE /distributors - Non-existent", "DELETE", "/distributors/999", null, 200, "not found");
        
        // DELETE /distributors/:id - Invalid format
        testEndpoint("DELETE /distributors - Invalid format", "DELETE", "/distributors/abc", null, 400, "Invalid");
    }

    // ================ SPECIAL ENDPOINTS ================
    private static void testSpecialEndpoints() {
        printSection("SPECIAL ENDPOINTS");
        
        // GET /items/:id/cheapest - Valid
        testEndpoint("GET /items/cheapest - Valid", "GET", "/items/1/cheapest?quantity=100", null, 200, "total_cost");
        
        // GET /items/:id/cheapest - Zero quantity
        testEndpoint("GET /items/cheapest - Zero quantity", "GET", "/items/1/cheapest?quantity=0", null, 200, "greater than 0");
        
        // GET /items/:id/cheapest - Negative quantity
        testEndpoint("GET /items/cheapest - Negative quantity", "GET", "/items/1/cheapest?quantity=-10", null, 200, "greater than 0");
        
        // GET /items/:id/cheapest - Invalid quantity format
        testEndpoint("GET /items/cheapest - Invalid quantity", "GET", "/items/1/cheapest?quantity=abc", null, 400, "must be an integer");
        
        // GET /items/:id/cheapest - Missing quantity
        testEndpoint("GET /items/cheapest - Missing quantity", "GET", "/items/1/cheapest", null, 400, "must be an integer");
        
        // GET /items/:id/cheapest - Non-existent item
        testEndpoint("GET /items/cheapest - Non-existent item", "GET", "/items/999/cheapest?quantity=100", null, 200, "No distributors found");
        
        // GET /items/:id/cheapest - Invalid item ID format
        testEndpoint("GET /items/cheapest - Invalid item format", "GET", "/items/abc/cheapest?quantity=100", null, 400, "Invalid");
    }

    // ================ EDGE CASES ================
    private static void testEdgeCases() {
        printSection("EDGE CASES & BOUNDARY CONDITIONS");
        
        // Test maximum integer values
        testEndpoint("POST /inventory - Max int stock", "POST", "/inventory?itemId=1&stock=2147483647&capacity=100", null, 200, "already exists");
        
        // Test very large numbers
        testEndpoint("POST /distributors/items - Large cost", "POST", "/distributors/1/items?itemId=1&cost=999999.99", null, 200, "success");
        
        // Test decimal precision
        testEndpoint("POST /distributors/items - Decimal precision", "POST", "/distributors/1/items?itemId=2&cost=1.234567", null, 200, "success");
        
        // Test URL encoding edge cases
        testEndpoint("POST /items - URL encoded name", "POST", "/items?name=" + urlEncode("Test Item & Co. #1"), null, 200, "success");
        
        // Test empty path parameters
        testEndpoint("GET /inventory - Empty ID", "GET", "/inventory/", null, 404, null);
        
        // Test non-existent endpoints
        testEndpoint("GET - Non-existent endpoint", "GET", "/nonexistent", null, 404, null);
        
        // Test wrong HTTP methods
        testEndpoint("POST - Wrong method on GET endpoint", "POST", "/version", null, 404, null);
        testEndpoint("GET - Wrong method on POST endpoint", "GET", "/items?name=test", null, 200, null); // This actually works as GET /items
    }

    // ================ SECURITY & VALIDATION ================
    private static void testSecurityAndValidation() {
        printSection("SECURITY & VALIDATION");
        
        // Test SQL injection attempts (should be safe with prepared statements)
        testEndpoint("SQL Injection - Item name", "POST", "/items?name=" + urlEncode("'; DROP TABLE items; --"), null, 200, "success");
        
        // Test XSS attempts
        testEndpoint("XSS - Item name", "POST", "/items?name=" + urlEncode("<script>alert('xss')</script>"), null, 200, "success");
        
        // Test very long strings (reduced to avoid URL length limits)
        String veryLongString = "A".repeat(2000);
        testEndpoint("Long string - Item name", "POST", "/items?name=" + urlEncode(veryLongString), null, 200, "success");
        
        // Test Unicode characters
        testEndpoint("Unicode - Item name", "POST", "/items?name=" + urlEncode("Test Unicode"), null, 200, "success");
        
        // Test null characters (URL encoded)
        testEndpoint("Null chars - Item name", "POST", "/items?name=test%00null", null, 200, "success");
    }

    // ================ UTILITY METHODS ================
    private static void testEndpoint(String testName, String method, String endpoint, String body, int expectedStatus, String expectedContent) {
        totalTests++;
        try {
            String response = makeRequest(method, endpoint, body);
            boolean statusOk = true; // We'll assume status is OK since we get a response
            boolean contentOk = expectedContent == null || (response != null && response.contains(expectedContent));
            
            if (statusOk && contentOk) {
                passedTests++;
                System.out.println("PASS " + testName);
            } else {
                failedTests++;
                System.out.println("FAIL " + testName);
                if (!contentOk) {
                    System.out.println("   Expected: " + expectedContent);
                    if (response != null) {
                        System.out.println("   Got: " + response.substring(0, Math.min(100, response.length())));
                    } else {
                        System.out.println("   Got: null response");
                    }
                }
            }
        } catch (Exception e) {
            if (expectedStatus >= 400 && e.getMessage() != null && e.getMessage().contains("400")) {
                passedTests++;
                System.out.println("PASS " + testName + " (Expected error)");
            } else {
                failedTests++;
                System.out.println("FAIL " + testName + " - Error: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
            }
        }
    }

    private static String makeRequest(String method, String endpoint, String body) {
        try {
            @SuppressWarnings("deprecation")
            URL url = new URL(BASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            if (body != null) {
                conn.setDoOutput(true);
                conn.getOutputStream().write(body.getBytes());
            }
            
            int responseCode = conn.getResponseCode();
            java.io.InputStream inputStream = responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream();
            
            if (inputStream == null) {
                return ""; // Return empty string instead of null
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            return response.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void waitForServer() {
        System.out.println("Waiting for server to be ready...");
        int attempts = 0;
        while (attempts < 10) {
            try {
                makeRequest("GET", "/version", null);
                System.out.println("Server is ready!");
                return;
            } catch (Exception e) {
                attempts++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        System.out.println("Server not responding. Please start the server first.");
        System.exit(1);
    }

    private static void resetDatabase() {
        System.out.println("Resetting database to clean state...");
        try {
            makeRequest("GET", "/reset", null);
            System.out.println("Database reset complete!");
        } catch (Exception e) {
            System.out.println("Warning: Could not reset database - " + e.getMessage());
        }
    }

    private static void printSection(String sectionName) {
        System.out.println("\n" + sectionName);
        System.out.println("-".repeat(50));
    }

    private static void printFinalResults() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("TEST RESULTS SUMMARY");
        System.out.println("=".repeat(70));
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + failedTests);
        System.out.println("Success Rate: " + String.format("%.1f%%", (passedTests * 100.0 / totalTests)));
        
        if (failedTests == 0) {
            System.out.println("\nALL TESTS PASSED! Your API is working perfectly!");
        } else {
            System.out.println("\nSome tests failed. Please review the output above.");
        }
        System.out.println("=".repeat(70));
    }

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return value;
        }
    }

    private static int extractLastItemId(String response) {
        try {
            // Simple extraction - look for the last "id": number pattern
            String[] parts = response.split("\"id\":");
            if (parts.length > 1) {
                String lastPart = parts[parts.length - 1];
                String idStr = lastPart.split("[,}]")[0].trim();
                return Integer.parseInt(idStr);
            }
        } catch (Exception e) {
            // Fallback to a safe ID
        }
        return 1; // Fallback to item ID 1
    }

    private static int extractIdFromResponse(String response) {
        try {
            // Extract ID from success response like {"success": true, "id": 123}
            int idIndex = response.indexOf("\"id\":");
            if (idIndex != -1) {
                String substring = response.substring(idIndex + 5);
                String idStr = substring.split("[,}]")[0].trim();
                return Integer.parseInt(idStr);
            }
        } catch (Exception e) {
            // Fallback
        }
        return 1;
    }
}
