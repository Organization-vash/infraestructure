package com.vash.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vash.lambda.model.ModuleDTO;
import com.vash.lambda.service.ModuleServiceLambda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ModuleLambdaHandlerTest {

    private ModuleLambdaHandler handler;
    private ModuleServiceLambda mockService;
    private Context mockContext;

    @BeforeEach
    public void setUp() {
        mockService = mock(ModuleServiceLambda.class);
        handler = new ModuleLambdaHandler(mockService); // Constructor inyectado para test
        mockContext = mock(Context.class);
    }

    @Test
    public void testGetAllModules_returns200() throws Exception {
        // Arrange
        ModuleDTO mockModule = new ModuleDTO();
        mockModule.setId(1);
        mockModule.setStatus("INACTIVE");

        when(mockService.getAll()).thenReturn(List.of(mockModule));

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("GET")
                .withPath("/modules");

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, mockContext);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().contains("INACTIVE"));
    }

    @Test
    public void testGetById_returnsModuleIfExists() throws Exception {
        // Arrange
        ModuleDTO mockModule = new ModuleDTO();
        mockModule.setId(1);
        mockModule.setStatus("INACTIVE");

        when(mockService.getById(1)).thenReturn(mockModule);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("GET")
                .withPath("/modules/1")
                .withPathParameters(Map.of("id", "1"));

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, mockContext);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().contains("INACTIVE"));
    }

    @Test
    public void testPostCreatesModule_returns201() throws Exception {
        // Arrange
        ModuleDTO input = new ModuleDTO();
        input.setId(2);

        ModuleDTO created = new ModuleDTO();
        created.setId(2);
        created.setStatus("INACTIVE");

        when(mockService.create(any())).thenReturn(created);

        ObjectMapper mapper = new ObjectMapper();

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("POST")
                .withPath("/modules")
                .withBody(mapper.writeValueAsString(input));

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, mockContext);

        // Assert
        assertEquals(201, response.getStatusCode());
        assertTrue(response.getBody().contains("INACTIVE"));
    }

    @Test
    public void testDeleteModule_withId_returns204() {
        // Arrange
        doNothing().when(mockService).delete(4);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("DELETE")
                .withPath("/modules/4")
                .withPathParameters(Map.of("id", "4"));

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, mockContext);

        // Assert
        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void testDeleteModule_withoutId_returns400() {
        // Arrange
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("DELETE")
                .withPath("/modules");

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, mockContext);

        // Assert
        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("ID es requerido"));
    }

    @Test
    public void testOptionsRequest_returns200() {
        // Arrange
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("OPTIONS")
                .withPath("/modules");

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, mockContext);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().contains("Preflight OK"));
    }

    @Test
    public void testUnsupportedMethod_returns405() {
        // Arrange
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("PUT")
                .withPath("/modules");

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, mockContext);

        // Assert
        assertEquals(405, response.getStatusCode());
        assertTrue(response.getBody().contains("no soportado"));
    }
}
