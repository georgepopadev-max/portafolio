package com.apidocgen.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.annotations.Annotation;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JavaCodeParser {
    
    private final JavaParser parser;
    
    private static final Set<String> HTTP_ANNOTATIONS = Set.of(
        "GetMapping", "PostMapping", "PutMapping", "DeleteMapping", 
        "PatchMapping", "RequestMapping", "RestController", "Controller"
    );
    
    private static final Pattern PATH_PATTERN = Pattern.compile("\"([^\"]+)\"");
    
    public JavaCodeParser() {
        this.parser = new JavaParser();
    }
    
    public ParseResult parseSourceCode(String sourceCode) {
        ParseResult result = new ParseResult();
        
        try {
            CompilationUnit cu = parser.parse(sourceCode).getResult()
                .orElseThrow(() -> new IllegalArgumentException("Failed to parse Java code"));
            
            String packageName = cu.getPackageDeclaration()
                .map(p -> p.getName().asString())
                .orElse("");
            
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cls -> {
                boolean isRestController = cls.getAnnotations().stream()
                    .anyMatch(a -> a.getNameAsString().equals("RestController") 
                                || a.getNameAsString().equals("Controller"));
                
                boolean hasRequestMapping = cls.getAnnotations().stream()
                    .anyMatch(a -> a.getNameAsString().equals("RequestMapping"));
                
                if (isRestController || hasRequestMapping || cls.getNameAsString().contains("Controller")) {
                    ParsedController controller = parseController(cls, packageName);
                    if (!controller.getEndpoints().isEmpty()) {
                        result.addController(controller);
                    }
                }
                
                if (isDtoOrEntity(cls)) {
                    ParsedSchema schema = parseSchema(cls, packageName);
                    result.addSchema(schema);
                }
            });
            
        } catch (Exception e) {
            throw new RuntimeException("Error parsing source code: " + e.getMessage(), e);
        }
        
        return result;
    }
    
    private ParsedController parseController(ClassOrInterfaceDeclaration cls, String packageName) {
        String name = cls.getNameAsString();
        ParsedController controller = new ParsedController(name, packageName, extractBasePath(cls));
        
        cls.getMethods().forEach(method -> {
            ParsedEndpoint endpoint = parseEndpoint(method, name, controller.getBasePath());
            if (endpoint != null) {
                controller.addEndpoint(endpoint);
            }
        });
        
        return controller;
    }
    
    private ParsedEndpoint parseEndpoint(MethodDeclaration method, String controllerName, String basePath) {
        Optional<Annotation> httpAnnotation = method.getAnnotations().stream()
            .filter(a -> isHttpMethod(a.getNameAsString()))
            .findFirst();
        
        if (httpAnnotation.isEmpty()) {
            return null;
        }
        
        Annotation annotation = httpAnnotation.get();
        String httpMethod = mapToHttpMethod(annotation.getNameAsString());
        String path = extractPath(annotation);
        String fullPath = basePath.isEmpty() ? path : basePath + path;
        
        ParsedEndpoint endpoint = new ParsedEndpoint(
            controllerName,
            method.getNameAsString(),
            httpMethod,
            fullPath,
            method.getType().asString()
        );
        
        method.getParameters().forEach(param -> {
            ParsedParameter parsedParam = new ParsedParameter(
                param.getName().asString(),
                param.getType().asString(),
                isRequired(param),
                extractParamDescription(param)
            );
            endpoint.addParameter(parsedParam);
        });
        
        annotation.getArguments().forEach(arg -> 
            endpoint.addAnnotation(arg.toString()));
        
        return endpoint;
    }
    
    private ParsedSchema parseSchema(ClassOrInterfaceDeclaration cls, String packageName) {
        ParsedSchema schema = new ParsedSchema(cls.getNameAsString(), packageName);
        
        cls.getFields().forEach(field -> {
            ParsedField parsedField = new ParsedField(
                extractFieldName(field),
                field.getVariables().get(0).getType().asString(),
                isFieldRequired(field)
            );
            schema.addField(parsedField);
        });
        
        return schema;
    }
    
    private String extractBasePath(ClassOrInterfaceDeclaration cls) {
        return cls.getAnnotations().stream()
            .filter(a -> a.getNameAsString().equals("RequestMapping"))
            .findFirst()
            .map(this::extractPath)
            .orElse("/api");
    }
    
    private String extractPath(Annotation annotation) {
        for (var arg : annotation.getArguments()) {
            Matcher matcher = PATH_PATTERN.matcher(arg.toString());
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return "";
    }
    
    private String mapToHttpMethod(String annotationName) {
        return switch (annotationName) {
            case "GetMapping" -> "GET";
            case "PostMapping" -> "POST";
            case "PutMapping" -> "PUT";
            case "DeleteMapping" -> "DELETE";
            case "PatchMapping" -> "PATCH";
            default -> "GET";
        };
    }
    
    private boolean isHttpMethod(String name) {
        return Set.of("GetMapping", "PostMapping", "PutMapping", "DeleteMapping", "PatchMapping").contains(name);
    }
    
    private boolean isDtoOrEntity(ClassOrInterfaceDeclaration cls) {
        return cls.getAnnotations().stream()
            .anyMatch(a -> Set.of("Data", "Getter", "Setter", "Entity", "Table", "Document", "Schema").contains(a.getNameAsString()))
            || cls.getNameAsString().endsWith("Dto") 
            || cls.getNameAsString().endsWith("Request")
            || cls.getNameAsString().endsWith("Response")
            || cls.getNameAsString().endsWith("Entity");
    }
    
    private String extractFieldName(FieldDeclaration field) {
        return field.getVariables().get(0).getNameAsString();
    }
    
    private boolean isRequired(FieldDeclaration field) {
        return field.getAnnotations().stream()
            .anyMatch(a -> Set.of("NotNull", "NotBlank", "NotEmpty", "Required").contains(a.getNameAsString()));
    }
    
    private boolean isRequired(Parameter param) {
        return param.getAnnotations().stream()
            .anyMatch(a -> Set.of("NotNull", "NotBlank", "Valid", "RequestParam").contains(a.getNameAsString()));
    }
    
    private String extractParamDescription(Parameter param) {
        String name = param.getName().asString();
        return "Request parameter: " + name;
    }
}