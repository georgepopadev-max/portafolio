package com.apidocgen.ai;

import com.apidocgen.ai.interface.AIProvider;
import com.apidocgen.ai.provider.MockAIProvider;
import com.apidocgen.ai.provider.RealAIProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIService {
    
    private final AIProvider aiProvider;
    
    @Value("${ai.provider:mock}")
    private String providerType;
    
    public AIService(MockAIProvider mockProvider, RealAIProvider realProvider) {
        this.aiProvider = "openai".equals(providerType) ? realProvider : mockProvider;
    }
    
    public String generateEndpointDoc(String controllerName, String method, String path,
                                      String httpMethod, List<String> paramTypes,
                                      String returnType, String detailLevel) {
        return aiProvider.generateEndpointDescription(
            controllerName, method, path, httpMethod, paramTypes, returnType, detailLevel
        );
    }
    
    public String generateSchemaDoc(String className, List<String> fieldNames,
                                    List<String> fieldTypes, String detailLevel) {
        return aiProvider.generateSchemaDescription(className, fieldNames, fieldTypes, detailLevel);
    }
    
    public String generateExampleValue(String fieldName, String fieldType) {
        return aiProvider.generateExampleValue(fieldName, fieldType);
    }
    
    public boolean isAIEnabled() {
        return aiProvider.isAvailable();
    }
}