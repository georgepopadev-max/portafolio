package com.apidocgen.service;

import com.apidocgen.entity.GeneratedDoc;
import com.apidocgen.repository.GeneratedDocRepository;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.util.Optional;
import java.util.UUID;

@Service
public class ExportService {
    
    private final GeneratedDocRepository generatedDocRepository;
    private final Yaml yamlParser;
    
    public ExportService(GeneratedDocRepository generatedDocRepository) {
        this.generatedDocRepository = generatedDocRepository;
        this.yamlParser = new Yaml();
    }
    
    public String exportAsYaml(UUID docId) {
        GeneratedDoc doc = generatedDocRepository.findById(docId)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + docId));
        return doc.getSpecYaml();
    }
    
    public String exportAsJson(UUID docId) {
        GeneratedDoc doc = generatedDocRepository.findById(docId)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + docId));
        
        try {
            Object yamlContent = yamlParser.load(doc.getSpecYaml());
            return convertToJsonString(yamlContent);
        } catch (Exception e) {
            return doc.getSpecJson();
        }
    }
    
    public String updateSpec(UUID docId, String newYaml) {
        GeneratedDoc doc = generatedDocRepository.findById(docId)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + docId));
        
        validateYaml(newYaml);
        
        doc.setSpecYaml(newYaml);
        doc.setSpecJson(convertToJsonString(yamlParser.load(newYaml)));
        generatedDocRepository.save(doc);
        
        return "Specification updated successfully";
    }
    
    public String validateYaml(String yaml) {
        try {
            yamlParser.load(yaml);
            return "Valid YAML";
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid YAML: " + e.getMessage());
        }
    }
    
    private String convertToJsonString(Object obj) {
        StringBuilder json = new StringBuilder();
        convertValue(json, obj, 0);
        return json.toString();
    }
    
    private void convertValue(StringBuilder json, Object value, int indent) {
        if (value instanceof String) {
            json.append("\"").append(escapeJson((String) value)).append("\"");
        } else if (value instanceof Number) {
            json.append(value);
        } else if (value instanceof Boolean) {
            json.append(value);
        } else if (value instanceof Iterable) {
            json.append("[");
            boolean first = true;
            for (Object item : (Iterable<?>) value) {
                if (!first) json.append(", ");
                convertValue(json, item, indent + 1);
                first = false;
            }
            json.append("]");
        } else if (value instanceof java.util.Map) {
            json.append("{");
            boolean first = true;
            for (java.util.Map.Entry<?, ?> entry : ((java.util.Map<?, ?>) value).entrySet()) {
                if (!first) json.append(", ");
                json.append("\"").append(escapeJson(entry.getKey().toString())).append("\": ");
                convertValue(json, entry.getValue(), indent + 1);
                first = false;
            }
            json.append("}");
        } else {
            json.append("null");
        }
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}