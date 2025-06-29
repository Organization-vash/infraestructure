package com.vash.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vash.lambda.model.ServiceDTO;
import com.vash.lambda.service.ServiceServiceLambda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServiceLambdaHandlerTest {

    @Mock
    private ServiceServiceLambda mockService;

    @Mock
    private Context mockContext;

    private ServiceLambdaHandler handler;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        handler = new ServiceLambdaHandler(mockService);
    }

    @Test
    public void testGetAll_returns200() throws Exception {
        // Arrange
        ServiceDTO s = new ServiceDTO();
        s.setId(1);
        s.setName("Consulta");
        s.setType("PRESENCIAL");
        s.setDescription("Atención presencial");

        when(mockService.getAll()).thenReturn(List.of(s));

        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
                .withHttpMethod("GET");

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().contains("Consulta"));
    }

    @Test
    public void testPost_createsService_returns201() throws Exception {
        // Arrange
        ServiceDTO dto = new ServiceDTO();
        dto.setName("Trámite");
        dto.setType("VIRTUAL");
        dto.setDescription("Solicitud virtual");

        when(mockService.create(any())).thenReturn(dto);

        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
                .withHttpMethod("POST")
                .withBody(objectMapper.writeValueAsString(dto));

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

        // Assert
        assertEquals(201, response.getStatusCode());
        assertTrue(response.getBody().contains("Trámite"));
    }

    @Test
    public void testPut_withId_updatesService_returns200() throws Exception {
        // Arrange
        ServiceDTO dto = new ServiceDTO();
        dto.setName("Modificado");
        dto.setType("PRESENCIAL");
        dto.setDescription("Modificado");

        when(mockService.update(eq(1), any())).thenReturn(dto);

        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
                .withHttpMethod("PUT")
                .withPathParameters(Map.of("id", "1"))
                .withBody(objectMapper.writeValueAsString(dto));

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().contains("Modificado"));
    }

    @Test
    public void testPut_withoutId_returns400() {
        // Arrange
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
                .withHttpMethod("PUT")
                .withBody("{}");

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

        // Assert
        assertEquals(400, response.getStatusCode());
        assertEquals("ID es requerido para actualizar", response.getBody());
    }

    @Test
    public void testDelete_withId_returns204() {
        // Arrange
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
                .withHttpMethod("DELETE")
                .withPathParameters(Map.of("id", "2"));

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

        // Assert
        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void testDelete_withoutId_returns400() {
        // Arrange
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
                .withHttpMethod("DELETE");

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

        // Assert
        assertEquals(400, response.getStatusCode());
        assertEquals("ID es requerido para eliminar", response.getBody());
    }

    @Test
    public void testUnsupportedMethod_returns405() {
        // Arrange
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
                .withHttpMethod("PATCH");

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

        // Assert
        assertEquals(405, response.getStatusCode());
        assertTrue(response.getBody().contains("Método no soportado"));
    }
}
