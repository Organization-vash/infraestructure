package com.vash.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vash.lambda.model.ServiceDTO;
import com.vash.lambda.service.ServiceServiceLambda;
import com.vash.db.DatabaseInitializer;

import java.util.List;

public class ServiceLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static {
        DatabaseInitializer.initServiceTable();
    }

    private final ServiceServiceLambda service = new ServiceServiceLambda();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            String httpMethod = event.getHttpMethod();

            switch (httpMethod) {
                case "GET":
                    List<ServiceDTO> services = service.getAll();
                    return response.withStatusCode(200)
                            .withBody(objectMapper.writeValueAsString(services));

                case "POST":
                    ServiceDTO newService = objectMapper.readValue(event.getBody(), ServiceDTO.class);
                    ServiceDTO created = service.create(newService);
                    return response.withStatusCode(201)
                            .withBody(objectMapper.writeValueAsString(created));

                case "PUT":
                    String pathPut = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;
                    if (pathPut == null) return response.withStatusCode(400).withBody("ID es requerido para actualizar");

                    ServiceDTO updateService = objectMapper.readValue(event.getBody(), ServiceDTO.class);
                    ServiceDTO updated = service.update(Integer.parseInt(pathPut), updateService);
                    return response.withStatusCode(200)
                            .withBody(objectMapper.writeValueAsString(updated));

                case "DELETE":
                    String pathDelete = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;
                    if (pathDelete == null) return response.withStatusCode(400).withBody("ID es requerido para eliminar");

                    service.delete(Integer.parseInt(pathDelete));
                    return response.withStatusCode(204).withBody("");

                default:
                    return response.withStatusCode(405)
                            .withBody("Método no soportado: " + httpMethod);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return response.withStatusCode(500)
                    .withBody("Error interno: " + e.getMessage());
        }
    }
}
