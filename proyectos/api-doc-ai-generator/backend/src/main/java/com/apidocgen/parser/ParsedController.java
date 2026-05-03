package com.apidocgen.parser;

import java.util.List;

public class ParsedController {
    private String name;
    private String packageName;
    private String basePath;
    private List<ParsedEndpoint> endpoints;
    private String description;

    public ParsedController() {
        this.endpoints = new java.util.ArrayList<>();
    }

    public ParsedController(String name, String packageName, String basePath) {
        this.name = name;
        this.packageName = packageName;
        this.basePath = basePath;
        this.endpoints = new java.util.ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public String getBasePath() { return basePath; }
    public void setBasePath(String basePath) { this.basePath = basePath; }
    public List<ParsedEndpoint> getEndpoints() { return endpoints; }
    public void setEndpoints(List<ParsedEndpoint> endpoints) { this.endpoints = endpoints; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public void addEndpoint(ParsedEndpoint endpoint) { this.endpoints.add(endpoint); }
}