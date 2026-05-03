package com.apidocgen.parser;

import java.util.List;

public class ParseResult {
    private List<ParsedController> controllers;
    private List<ParsedSchema> schemas;
    private int totalEndpoints;
    private int totalControllers;
    private int totalSchemas;

    public ParseResult() {
        this.controllers = new java.util.ArrayList<>();
        this.schemas = new java.util.ArrayList<>();
    }

    public ParseResult(List<ParsedController> controllers, List<ParsedSchema> schemas) {
        this.controllers = controllers;
        this.schemas = schemas;
        this.totalEndpoints = controllers.stream()
            .mapToInt(c -> c.getEndpoints().size())
            .sum();
        this.totalControllers = controllers.size();
        this.totalSchemas = schemas.size();
    }

    public List<ParsedController> getControllers() { return controllers; }
    public void setControllers(List<ParsedController> controllers) { this.controllers = controllers; }
    public List<ParsedSchema> getSchemas() { return schemas; }
    public void setSchemas(List<ParsedSchema> schemas) { this.schemas = schemas; }
    public int getTotalEndpoints() { return totalEndpoints; }
    public void setTotalEndpoints(int totalEndpoints) { this.totalEndpoints = totalEndpoints; }
    public int getTotalControllers() { return totalControllers; }
    public void setTotalControllers(int totalControllers) { this.totalControllers = totalControllers; }
    public int getTotalSchemas() { return totalSchemas; }
    public void setTotalSchemas(int totalSchemas) { this.totalSchemas = totalSchemas; }

    public void addController(ParsedController controller) { this.controllers.add(controller); }
    public void addSchema(ParsedSchema schema) { this.schemas.add(schema); }
}