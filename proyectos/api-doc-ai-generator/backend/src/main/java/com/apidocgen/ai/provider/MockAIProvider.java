package com.apidocgen.ai.provider;

import com.apidocgen.ai.interface.AIProvider;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class MockAIProvider implements AIProvider {
    
    private final Map<String, String> controllerDescriptions = new HashMap<>();
    private final Map<String, String> endpointDescriptions = new HashMap<>();
    
    public MockAIProvider() {
        initializeMockData();
    }
    
    private void initializeMockData() {
        controllerDescriptions.put("InvoiceController", "Handles invoice management operations including creation, retrieval, PDF generation, and status tracking.");
        controllerDescriptions.put("PaymentController", "Processes payment transactions, manages payment methods, and handles refund operations.");
        controllerDescriptions.put("CustomerController", "Manages customer profiles, addresses, and account settings.");
        controllerDescriptions.put("ProductController", "Handles product catalog operations including CRUD and inventory management.");
        controllerDescriptions.put("OrderController", "Processes and tracks customer orders through the fulfillment lifecycle.");
        controllerDescriptions.put("UserController", "Manages user authentication and account management.");
        
        endpointDescriptions.put("POST /api/v1/invoices", "Creates a new invoice with line items and applies applicable taxes and discounts.");
        endpointDescriptions.put("GET /api/v1/invoices/{id}", "Retrieves invoice details including status, line items, and payment information.");
        endpointDescriptions.put("POST /api/v1/invoices/{id}/pdf", "Triggers async PDF generation for the specified invoice using the configured template.");
        endpointDescriptions.put("GET /api/v1/invoices", "Lists all invoices with optional filtering by status, date range, and customer.");
        endpointDescriptions.put("PUT /api/v1/invoices/{id}", "Updates an existing invoice, recalculates totals, and notifies relevant parties.");
        
        endpointDescriptions.put("POST /api/v1/payments", "Processes a new payment transaction using the specified payment method.");
        endpointDescriptions.put("GET /api/v1/payments/{id}", "Retrieves payment details including transaction status and gateway response.");
        endpointDescriptions.put("POST /api/v1/payments/{id}/refund", "Initiates a full or partial refund for a completed payment.");
        endpointDescriptions.put("GET /api/v1/payments/customer/{customerId}", "Lists all payments for a specific customer with pagination support.");
        
        endpointDescriptions.put("POST /api/v1/customers", "Creates a new customer profile with contact information and preferences.");
        endpointDescriptions.put("GET /api/v1/customers/{id}", "Retrieves customer details including profile, addresses, and order history.");
        endpointDescriptions.put("PUT /api/v1/customers/{id}", "Updates customer information and preferences.");
        endpointDescriptions.put("GET /api/v1/customers", "Lists all customers with search and filtering capabilities.");
    }
    
    @Override
    public String generateEndpointDescription(String controllerName, String method, String path,
                                              String httpMethod, List<String> paramTypes,
                                              String returnType, String detailLevel) {
        String key = httpMethod + " " + path;
        
        if (endpointDescriptions.containsKey(key)) {
            return endpointDescriptions.get(key);
        }
        
        StringBuilder desc = new StringBuilder();
        desc.append(capitalize(httpMethod)).append(" ").append(path).append(" - ");
        
        if (controllerName.contains("Invoice")) {
            desc.append("Handles invoice-related operations including creation, retrieval, and PDF generation.");
        } else if (controllerName.contains("Payment")) {
            desc.append("Processes payment transactions and manages payment methods.");
        } else if (controllerName.contains("Customer")) {
            desc.append("Manages customer information and related operations.");
        } else {
            desc.append("REST endpoint for the ").append(controllerName.replace("Controller", "")).append(" resource.");
        }
        
        if ("detailed".equals(detailLevel) && !paramTypes.isEmpty()) {
            desc.append(" Accepts parameters: ").append(String.join(", ", paramTypes)).append(".");
        }
        
        return desc.toString();
    }
    
    @Override
    public String generateSchemaDescription(String className, List<String> fieldNames,
                                            List<String> fieldTypes, String detailLevel) {
        StringBuilder desc = new StringBuilder();
        desc.append("Schema for ").append(className).append(" entity");
        
        if ("detailed".equals(detailLevel)) {
            desc.append(". Contains fields: ");
            for (int i = 0; i < Math.min(fieldNames.size(), 5); i++) {
                if (i > 0) desc.append(", ");
                desc.append(fieldNames.get(i)).append(" (").append(fieldTypes.get(i)).append(")");
            }
        }
        
        return desc.toString();
    }
    
    @Override
    public String generateExampleValue(String fieldName, String fieldType) {
        if (fieldType.contains("String")) {
            if (fieldName.toLowerCase().contains("email")) return "user@example.com";
            if (fieldName.toLowerCase().contains("name")) return "John Doe";
            if (fieldName.toLowerCase().contains("phone")) return "+1-555-123-4567";
            if (fieldName.toLowerCase().contains("id")) return "550e8400-e29b-41d4-a716-446655440000";
            if (fieldName.toLowerCase().contains("desc") || fieldName.toLowerCase().contains("description"))
                return "Sample description text";
            return "sample-value";
        }
        if (fieldType.contains("Int") || fieldType.contains("Long")) {
            if (fieldName.toLowerCase().contains("age")) return "28";
            if (fieldName.toLowerCase().contains("price") || fieldName.toLowerCase().contains("amount"))
                return "99.99";
            return "12345";
        }
        if (fieldType.contains("Double") || fieldType.contains("Float") || fieldType.contains("BigDecimal"))
            return "99.99";
        if (fieldType.contains("Boolean")) return "true";
        if (fieldType.contains("Date") || fieldType.contains("Time")) return "2024-01-15T10:30:00Z";
        if (fieldType.contains("List")) return "[]";
        if (fieldType.contains("Map")) return "{}";
        return "null";
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}