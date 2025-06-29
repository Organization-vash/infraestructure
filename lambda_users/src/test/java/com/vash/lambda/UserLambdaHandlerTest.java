package com.vash.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vash.lambda.model.UserDTO;
import com.vash.lambda.service.UserServiceLambda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserLambdaHandlerTest {

    private UserServiceLambda mockService;
    private UserLambdaHandler handler;
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mockService = mock(UserServiceLambda.class);
        handler = new UserLambdaHandler(mockService);
        mapper = new ObjectMapper();
    }

    @Test
    void shouldReturnAllUsers_whenGetMethod() throws Exception {
        // Arrange
        UserDTO user = new UserDTO();
        user.setId(1); user.setName("Juan");

        when(mockService.getAll()).thenReturn(List.of(user));
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent().withHttpMethod("GET");

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(event, mock(Context.class));

        // Assert
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().contains("Juan"));
    }

    @Test
    void shouldCreateUser_whenPostMethod() throws Exception {
        // Arrange
        UserDTO input = new UserDTO(); input.setName("Ana");
        UserDTO created = new UserDTO(); created.setId(100); created.setName("Ana");

        when(mockService.createUser(any())).thenReturn(created);
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
                .withHttpMethod("POST")
                .withBody(mapper.writeValueAsString(input));

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(event, mock(Context.class));

        // Assert
        assertEquals(201, response.getStatusCode());
        assertTrue(response.getBody().contains("Ana"));
    }

    @Test
    void shouldUpdateUser_whenPutMethod() throws Exception {
        // Arrange
        UserDTO input = new UserDTO(); input.setName("Carlos");
        UserDTO updated = new UserDTO(); updated.setId(1); updated.setName("Carlos");

        when(mockService.updateUser(eq(1), any())).thenReturn(updated);

        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
                .withHttpMethod("PUT")
                .withPathParameters(Map.of("id", "1"))
                .withBody(mapper.writeValueAsString(input));

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(event, mock(Context.class));

        // Assert
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().contains("Carlos"));
    }

    @Test
    void shouldDeleteUser_whenDeleteMethod() {
        // Arrange
        doNothing().when(mockService).delete(2);
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
                .withHttpMethod("DELETE")
                .withPathParameters(Map.of("id", "2"));

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(event, mock(Context.class));

        // Assert
        assertEquals(204, response.getStatusCode());
        assertTrue(response.getBody() == null || response.getBody().isBlank());
    }

    @Test
    void shouldReturn405_whenUnsupportedMethod() {
        // Arrange
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent().withHttpMethod("PATCH");

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(event, mock(Context.class));

        // Assert
        assertEquals(405, response.getStatusCode());
        assertTrue(response.getBody().contains("Método no soportado"));
    }
}
