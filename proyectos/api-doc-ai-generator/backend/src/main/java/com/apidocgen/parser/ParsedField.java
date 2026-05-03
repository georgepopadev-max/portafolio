package com.apidocgen.parser;

public class ParsedField {
    private String name;
    private String type;
    private boolean required;
    private String description;
    private List<String> annotations;

    public ParsedField() {
        this.annotations = new java.util.ArrayList<>();
    }

    public ParsedField(String name, String type) {
        this.name = name;
        this.type = type;
        this.required = true;
        this.annotations = new java.util.ArrayList<>();
    }

    public ParsedField(String name, String type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.annotations = new java.util.ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getAnnotations() { return annotations; }
    public void setAnnotations(List<String> annotations) { this.annotations = annotations; }

    public void addAnnotation(String annotation) { this.annotations.add(annotation); }
}