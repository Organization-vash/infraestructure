package com.vash.lambda;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vash.lambda.model.AgencyDTO;
import com.vash.lambda.service.AgencyServiceLambda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

public class AgencyLambdaHandlerTest {

  @Mock
  private Context mockContext;

  @Mock
  private AgencyServiceLambda mockService;

  private AgencyLambdaHandler handler;

  private final ObjectMapper mapper = new ObjectMapper();

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    handler = new AgencyLambdaHandler(mockService);
  }

  @Test
  @DisplayName("Metodo GET debe retornar 200 sin parametros y retorna agencias")
  public void testHandleRequest_GetAll_Returns200() throws Exception {
    // Arrange
    AgencyDTO mockAgency = new AgencyDTO();
    mockAgency.setId(1);
    mockAgency.setCity("Lima");
    mockAgency.setSeat("Sede Central");
    mockAgency.setCreatedAt("2024-01-01T00:00:00");

    when(mockService.getAll()).thenReturn(List.of(mockAgency));

    APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
        .withHttpMethod("GET")
        .withPath("/agencies")
        .withPathParameters(null);

    // Act
    APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

    // Assert
    assertEquals(200, response.getStatusCode());
    assertTrue(response.getBody().contains("Lima"));
  }

  @Test
  @DisplayName("Metodo GET debe retornar 200 con agencia existente y parametros")
  public void testHandleRequest_GetById_Returns200() throws Exception {
    // Arrange
    AgencyDTO mockAgency = new AgencyDTO();
    mockAgency.setId(10);
    mockAgency.setCity("Arequipa");
    mockAgency.setSeat("Av. Ejército");
    mockAgency.setCreatedAt("2024-02-01T12:00:00");

    when(mockService.findById(10)).thenReturn(mockAgency);

    APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
        .withHttpMethod("GET")
        .withPath("/agencies/10")
        .withPathParameters(Map.of("id", "10"));

    // Act
    APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

    // Assert
    assertEquals(200, response.getStatusCode());
    assertTrue(response.getBody().contains("Arequipa"));
  }

  @Test
  @DisplayName("Metodo POST debe retornar 201 con body válido y debe retornar agencia creada")
  public void testHandleRequest_Post_Returns201() throws Exception {
    // Arrange
    AgencyDTO input = new AgencyDTO();
    input.setCity("Cusco");
    input.setSeat("Plaza Central");

    AgencyDTO created = new AgencyDTO();
    created.setId(5);
    created.setCity("Cusco");
    created.setSeat("Plaza Central");
    created.setCreatedAt("2024-03-01T08:00:00");

    when(mockService.createAgency(any())).thenReturn(created);

    APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
        .withHttpMethod("POST")
        .withPath("/agencies")
        .withBody(mapper.writeValueAsString(input));

    // Act
    APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

    // Assert
    assertEquals(201, response.getStatusCode());
    assertTrue(response.getBody().contains("Cusco"));
  }

  @Test
  @DisplayName("Metodo PUT debe retornar 400 por parametro faltante")
  public void testHandleRequest_PutWithoutId_Returns400() {
    // Arrange
    APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
        .withHttpMethod("PUT")
        .withPath("/agencies")
        .withPathParameters(null);

    // Act
    APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

    // Assert
    assertEquals(400, response.getStatusCode());
    assertTrue(response.getBody().contains("ID es requerido"));
  }

  @Test
  @DisplayName("Metodo DELETE debe retornar 204 y responder No Content")
  public void testHandleRequest_Delete_Returns204() throws Exception {
    // Arrange
    doNothing().when(mockService).delete(7);

    APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
        .withHttpMethod("DELETE")
        .withPath("/agencies/7")
        .withPathParameters(Map.of("id", "7"));

    // Act
    APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

    // Assert
    assertEquals(204, response.getStatusCode());
    assertEquals("", response.getBody());
  }
}
