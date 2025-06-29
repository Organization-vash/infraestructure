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
import java.util.Map;

public class ServiceLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static {
        DatabaseInitializer.initServiceTable();
    }

    private final ServiceServiceLambda service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ServiceLambdaHandler() {
        this.service = new ServiceServiceLambda();
    }

    public ServiceLambdaHandler(ServiceServiceLambda mockService) {
        this.service = mockService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        response.withHeaders(Map.of(
                "Access-Control-Allow-Origin", "*",
                "Access-Control-Allow-Headers", "Content-Type",
                "Access-Control-Allow-Methods", "OPTIONS,GET,POST,PUT,DELETE"
        ));

        try {
            String httpMethod = event.getHttpMethod();
            System.out.println("HTTP Method: " + httpMethod);
            System.out.println("Path Parameters: " + event.getPathParameters());
            System.out.println("Request Body: " + event.getBody());

            switch (httpMethod) {
                case "GET":
                    System.out.println("Procesando GET...");
                    List<ServiceDTO> services = service.getAll();
                    System.out.println("Servicios obtenidos: " + services.size());
                    return response.withStatusCode(200)
                            .withBody(objectMapper.writeValueAsString(services));

                case "POST":
                    System.out.println("Procesando POST...");
                    ServiceDTO newService = objectMapper.readValue(event.getBody(), ServiceDTO.class);
                    ServiceDTO created = service.create(newService);
                    System.out.println("Servicio creado con ID: " + created.getId());
                    return response.withStatusCode(201)
                            .withBody(objectMapper.writeValueAsString(created));

                case "PUT":
                    System.out.println("Procesando PUT...");
                    String pathPut = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;
                    if (pathPut == null) {
                        System.err.println("PUT fallido: ID no proporcionado.");
                        return response.withStatusCode(400).withBody("ID es requerido para actualizar");
                    }

                    ServiceDTO updateService = objectMapper.readValue(event.getBody(), ServiceDTO.class);
                    ServiceDTO updated = service.update(Integer.parseInt(pathPut), updateService);
                    System.out.println("Servicio actualizado con ID: " + updated.getId());
                    return response.withStatusCode(200)
                            .withBody(objectMapper.writeValueAsString(updated));

                case "DELETE":
                    System.out.println("Procesando DELETE...");
                    String pathDelete = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;
                    if (pathDelete == null) {
                        System.err.println("DELETE fallido: ID no proporcionado.");
                        return response.withStatusCode(400).withBody("ID es requerido para eliminar");
                    }

                    service.delete(Integer.parseInt(pathDelete));
                    System.out.println("Servicio eliminado con ID: " + pathDelete);
                    return response.withStatusCode(204).withBody("");

                case "OPTIONS":
                    System.out.println("Preflight OPTIONS recibido");
                    return response.withStatusCode(200).withBody("Preflight OK");

                default:
                    System.err.println("Método no soportado: " + httpMethod);
                    return response.withStatusCode(405)
                            .withBody("Método no soportado: " + httpMethod);
            }

        } catch (Exception e) {
            System.err.println("Error interno en Lambda: " + e.getMessage());
            e.printStackTrace();
            return response.withStatusCode(500)
                    .withBody("Error interno: " + e.getMessage());
        }
    }
}
