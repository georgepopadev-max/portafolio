package com.apidocgen.parser;

public class ParsedParameter {
    private String name;
    private String type;
    private boolean required;
    private String description;
    private String annotation;

    public ParsedParameter() {}

    public ParsedParameter(String name, String type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
    }

    public ParsedParameter(String name, String type, boolean required, String description) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.description = description;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAnnotation() { return annotation; }
    public void setAnnotation(String annotation) { this.annotation = annotation; }
}