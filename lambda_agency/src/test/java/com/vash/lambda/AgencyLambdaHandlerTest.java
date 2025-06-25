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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

public class AgencyLambdaHandlerTest {

  @Mock
  private Context mockContext;

  @Mock
  private AgencyServiceLambda mockService;

  private AgencyLambdaHandler handler;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    handler = new AgencyLambdaHandler(mockService);
  }

  @Test
  public void testHandleRequest_GetAll_Returns200() throws Exception {
    System.out.println("▶ Ejecutando: GET sin ID debe retornar todas las agencias con código 200");

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

    APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

    assertEquals(200, response.getStatusCode());
    assertTrue(response.getBody().contains("Lima"));
  }

  @Test
  public void testHandleRequest_GetById_Returns200() throws Exception {
    System.out.println("▶ Ejecutando: GET con ID debe retornar la agencia encontrada con código 200");

    AgencyDTO mockAgency = new AgencyDTO();
    mockAgency.setId(2);
    mockAgency.setCity("Cusco");
    mockAgency.setSeat("Sede Cusco");
    mockAgency.setCreatedAt("2024-02-01T00:00:00");

    when(mockService.findById(2)).thenReturn(mockAgency);

    APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
        .withHttpMethod("GET")
        .withPath("/agencies/2")
        .withPathParameters(Map.of("id", "2"));

    APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

    assertEquals(200, response.getStatusCode());
    assertTrue(response.getBody().contains("Cusco"));
  }

  @Test
  public void testHandleRequest_Post_Returns201() throws Exception {
    System.out.println("▶ Ejecutando: POST debe crear una agencia y retornar código 201");

    AgencyDTO inputAgency = new AgencyDTO();
    inputAgency.setCity("Arequipa");
    inputAgency.setSeat("Sede Sur");

    AgencyDTO createdAgency = new AgencyDTO();
    createdAgency.setId(3);
    createdAgency.setCity("Arequipa");
    createdAgency.setSeat("Sede Sur");
    createdAgency.setCreatedAt("2024-03-01T00:00:00");

    when(mockService.createAgency(any(AgencyDTO.class))).thenReturn(createdAgency);

    APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
        .withHttpMethod("POST")
        .withPath("/agencies")
        .withBody(objectMapper.writeValueAsString(inputAgency));

    APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

    assertEquals(201, response.getStatusCode());
    assertTrue(response.getBody().contains("Arequipa"));
  }

  @Test
  public void testHandleRequest_Delete_Returns204() throws Exception {
    System.out.println("▶ Ejecutando: DELETE con ID debe retornar código 204");

    doNothing().when(mockService).delete(4);

    APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
        .withHttpMethod("DELETE")
        .withPath("/agencies/4")
        .withPathParameters(Map.of("id", "4"));

    APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

    assertEquals(204, response.getStatusCode());
    assertEquals("", response.getBody());
  }

  @Test
  public void testHandleRequest_PutWithoutId_Returns400() throws Exception {
    System.out.println("▶ Ejecutando: PUT sin ID debe retornar error 400");

    AgencyDTO updatedAgency = new AgencyDTO();
    updatedAgency.setCity("Tacna");
    updatedAgency.setSeat("Sede Sur");

    APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
        .withHttpMethod("PUT")
        .withPath("/agencies")
        .withBody(objectMapper.writeValueAsString(updatedAgency))
        .withPathParameters(null);

    APIGatewayProxyResponseEvent response = handler.handleRequest(event, mockContext);

    assertEquals(400, response.getStatusCode());
    assertTrue(response.getBody().contains("ID es requerido"));
  }
}
