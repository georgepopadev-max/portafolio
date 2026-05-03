package com.apidocgen.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JavaCodeParserTest {
    
    private JavaCodeParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new JavaCodeParser();
    }
    
    @Test
    void parseRestController_withValidCode() {
        String code = """
            package com.example.api;
            
            import org.springframework.web.bind.annotation.*;
            
            @RestController
            @RequestMapping("/api/v1/users")
            public class UserController {
                
                @GetMapping("/{id}")
                public User getUser(@PathVariable Long id) {
                    return new User();
                }
                
                @PostMapping
                public User createUser(@RequestBody CreateUserRequest request) {
                    return new User();
                }
            }
            """;
        
        ParseResult result = parser.parseSourceCode(code);
        
        assertEquals(1, result.getTotalControllers());
        assertEquals(2, result.getTotalEndpoints());
        
        ParsedController controller = result.getControllers().get(0);
        assertEquals("UserController", controller.getName());
        assertEquals("/api/v1/users", controller.getBasePath());
        
        ParsedEndpoint firstEndpoint = controller.getEndpoints().get(0);
        assertEquals("GET", firstEndpoint.getHttpMethod());
        assertEquals("/api/v1/users/{id}", firstEndpoint.getPath());
        assertEquals("getUser", firstEndpoint.getMethodName());
    }
    
    @Test
    void parseDtoClasses() {
        String code = """
            package com.example.dto;
            
            public class CreateUserRequest {
                private String name;
                private String email;
                private Integer age;
            }
            
            public class UserResponse {
                private Long id;
                private String name;
                private String email;
            }
            """;
        
        ParseResult result = parser.parseSourceCode(code);
        
        assertTrue(result.getTotalSchemas() >= 1);
    }
    
    @Test
    void parseController_withMultipleHttpMethods() {
        String code = """
            @RestController
            @RequestMapping("/api/items")
            public class ItemController {
                
                @GetMapping
                public List<Item> listItems() {
                    return List.of();
                }
                
                @GetMapping("/{id}")
                public Item getItem(@PathVariable Long id) {
                    return new Item();
                }
                
                @PostMapping
                public Item createItem(@RequestBody ItemRequest request) {
                    return new Item();
                }
                
                @PutMapping("/{id}")
                public Item updateItem(@PathVariable Long id, @RequestBody ItemRequest request) {
                    return new Item();
                }
                
                @DeleteMapping("/{id}")
                public void deleteItem(@PathVariable Long id) {
                }
            }
            """;
        
        ParseResult result = parser.parseSourceCode(code);
        
        ParsedController controller = result.getControllers().get(0);
        assertEquals(5, controller.getEndpoints().size());
        
        assertTrue(controller.getEndpoints().stream()
            .anyMatch(e -> e.getHttpMethod().equals("GET")));
        assertTrue(controller.getEndpoints().stream()
            .anyMatch(e -> e.getHttpMethod().equals("POST")));
        assertTrue(controller.getEndpoints().stream()
            .anyMatch(e -> e.getHttpMethod().equals("PUT")));
        assertTrue(controller.getEndpoints().stream()
            .anyMatch(e -> e.getHttpMethod().equals("DELETE")));
    }
    
    @Test
    void parseEndpoint_parameters() {
        String code = """
            @RestController
            public class TestController {
                
                @GetMapping("/search")
                public List<String> search(
                    @RequestParam String query,
                    @RequestParam(defaultValue = "10") Integer limit,
                    @PathVariable Long id
                ) {
                    return List.of();
                }
            }
            """;
        
        ParseResult result = parser.parseSourceCode(code);
        
        ParsedEndpoint endpoint = result.getControllers().get(0).getEndpoints().get(0);
        assertEquals(3, endpoint.getParameters().size());
        
        ParsedParameter firstParam = endpoint.getParameters().get(0);
        assertEquals("query", firstParam.getName());
        assertEquals("String", firstParam.getType());
    }
    
    @Test
    void parse_withInvalidCode_handlesGracefully() {
        String code = "this is not valid java code {{{}}}";
        
        assertThrows(RuntimeException.class, () -> parser.parseSourceCode(code));
    }
    
    @Test
    void parseEmptySource_returnsEmptyResult() {
        ParseResult result = parser.parseSourceCode("");
        
        assertEquals(0, result.getTotalControllers());
        assertEquals(0, result.getTotalSchemas());
    }
}