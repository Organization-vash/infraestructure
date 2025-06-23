package com.vash.lambda;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AgencyLambdaHandlerTest {

  @Mock
  private Context mockContext;

  private AgencyLambdaHandler handler;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    handler = new AgencyLambdaHandler();
  }
  
  @Test
  public void testHandleRequest_GetAll_Returns200() throws Exception {
    APIGatewayProxyRequestEvent ev = new APIGatewayProxyRequestEvent()
        .withHttpMethod("GET")
        .withPath("/users")
        .withPathParameters(null);
    
    APIGatewayProxyResponseEvent resp = handler.handleRequest(ev, mockContext);
    
    assertEquals(200, resp.getStatusCode());
    assertNotNull(resp.getBody(), "El body no debe ser null");
  }
}
