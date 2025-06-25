package com.vash.lambda;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vash.lambda.model.CodeDTO;
import com.vash.lambda.service.CodeServiceLambda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

public class CodeLambdaHandlerTest {

    @Mock
    private CodeServiceLambda mockService;

    @Mock
    private Context mockContext;

    private CodeLambdaHandler handler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        handler = new CodeLambdaHandler(mockService);
    }

    @Test
    public void testHandleRequest_GetAll_Returns200() throws Exception {
        System.out.println("▶ Ejecutando: GET sin ID debe retornar lista de tickets con código 200");

        CodeDTO dto = new CodeDTO();
        dto.setId(1);
        dto.setCode("ABC123");
        dto.setCustomerName("Juan");
        dto.setServiceName("Atención");
        dto.setCreated("2025-06-01T10:00:00");

        when(mockService.getAll()).thenReturn(List.of(dto));

        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
            .withHttpMethod("GET")
            .withPath("/tickets")
            .withPathParameters(null);

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().contains("Juan"));
    }

    @Test
    public void testHandleRequest_GetById_Returns200() throws Exception {
        System.out.println("▶ Ejecutando: GET con ID debe retornar el ticket con código 200");

        CodeDTO dto = new CodeDTO();
        dto.setId(2);
        dto.setCode("XYZ789");
        dto.setCustomerName("Ana");
        dto.setServiceName("Consultoría");
        dto.setCreated("2025-06-01T12:00:00");

        when(mockService.getById(2)).thenReturn(dto);

        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
            .withHttpMethod("GET")
            .withPath("/tickets/2")
            .withPathParameters(Map.of("id", "2"));

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().contains("Ana"));
    }

    @Test
    public void testHandleRequest_Post_Returns201() throws Exception {
        System.out.println("▶ Ejecutando: POST debe crear un ticket y retornar código 201");

        CodeDTO input = new CodeDTO();
        input.setCustomerName("Luis");
        input.setServiceName("Soporte");

        CodeDTO created = new CodeDTO();
        created.setId(3);
        created.setCode("QWE456");
        created.setCustomerName("Luis");
        created.setServiceName("Soporte");
        created.setCreated("2025-06-01T13:00:00");

        when(mockService.create(any(CodeDTO.class))).thenReturn(created);

        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
            .withHttpMethod("POST")
            .withPath("/tickets")
            .withBody(objectMapper.writeValueAsString(input));

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

        assertEquals(201, response.getStatusCode());
        assertTrue(response.getBody().contains("Luis"));
    }

    @Test
    public void testHandleRequest_PutWithoutId_Returns400() throws Exception {
        System.out.println("▶ Ejecutando: PUT sin ID debe retornar error 400");

        CodeDTO dto = new CodeDTO();
        dto.setCode("PUT999");
        dto.setServiceName("Actualización");

        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
            .withHttpMethod("PUT")
            .withPath("/tickets")
            .withBody(objectMapper.writeValueAsString(dto))
            .withPathParameters(null);

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("ID es requerido"));
    }

    @Test
    public void testHandleRequest_Delete_Returns204() {
        System.out.println("▶ Ejecutando: DELETE con ID debe retornar código 204");

        doNothing().when(mockService).delete(5);

        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
            .withHttpMethod("DELETE")
            .withPath("/tickets/5")
            .withPathParameters(Map.of("id", "5"));

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

        assertEquals(204, response.getStatusCode());
        assertEquals("", response.getBody());
    }
}
