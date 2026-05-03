package com.apidocgen.parser;

import java.util.List;

public class ParsedSchema {
    private String name;
    private String packageName;
    private List<ParsedField> fields;
    private String description;

    public ParsedSchema() {
        this.fields = new java.util.ArrayList<>();
    }

    public ParsedSchema(String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
        this.fields = new java.util.ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public List<ParsedField> getFields() { return fields; }
    public void setFields(List<ParsedField> fields) { this.fields = fields; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public void addField(ParsedField field) { this.fields.add(field); }
}