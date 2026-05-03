package com.apidocgen.ai.interface;

import java.util.List;

public interface AIProvider {
    String generateEndpointDescription(String controllerName, String method, String path, 
                                       String httpMethod, List<String> paramTypes,
                                       String returnType, String detailLevel);
    
    String generateSchemaDescription(String className, List<String> fieldNames,
                                     List<String> fieldTypes, String detailLevel);
    
    String generateExampleValue(String fieldName, String fieldType);
    
    boolean isAvailable();
}