package com.apidocgen.parser;

import java.util.List;
import java.util.ArrayList;

public class ParsedEndpoint {
    private String controllerName;
    private String methodName;
    private String httpMethod;
    private String path;
    private String returnType;
    private List<ParsedParameter> parameters;
    private List<String> annotations;
    private String summary;
    private String description;

    public ParsedEndpoint() {
        this.parameters = new ArrayList<>();
        this.annotations = new ArrayList<>();
    }

    public ParsedEndpoint(String controllerName, String methodName, String httpMethod,
                          String path, String returnType) {
        this.controllerName = controllerName;
        this.methodName = methodName;
        this.httpMethod = httpMethod;
        this.path = path;
        this.returnType = returnType;
        this.parameters = new ArrayList<>();
        this.annotations = new ArrayList<>();
    }

    public String getControllerName() { return controllerName; }
    public void setControllerName(String controllerName) { this.controllerName = controllerName; }
    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getReturnType() { return returnType; }
    public void setReturnType(String returnType) { this.returnType = returnType; }
    public List<ParsedParameter> getParameters() { return parameters; }
    public void setParameters(List<ParsedParameter> parameters) { this.parameters = parameters; }
    public List<String> getAnnotations() { return annotations; }
    public void setAnnotations(List<String> annotations) { this.annotations = annotations; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public void addParameter(ParsedParameter param) { this.parameters.add(param); }
    public void addAnnotation(String annotation) { this.annotations.add(annotation); }
}