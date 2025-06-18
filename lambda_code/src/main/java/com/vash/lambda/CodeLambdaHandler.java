package com.vash.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vash.db.DatabaseInitializer;
import com.vash.lambda.model.CodeDTO;
import com.vash.lambda.service.CodeServiceLambda;

import java.util.List;
import java.util.Map;

public class CodeLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static {
        DatabaseInitializer.initTicketCodeTable();
    }

    private final CodeServiceLambda service = new CodeServiceLambda();
    private final ObjectMapper objectMapper = new ObjectMapper();

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

            switch (httpMethod) {
                case "GET":
                    String pathId = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;
            
                    if (pathId != null) {
                        CodeDTO one = service.getById(Integer.parseInt(pathId));
                        if (one != null) {
                            return response.withStatusCode(200)
                                .withBody(objectMapper.writeValueAsString(one));
                        } else {
                            return response.withStatusCode(404).withBody("Ticket no encontrado");
                        }
                    } else {
                        List<CodeDTO> allTickets = service.getAll();
                        return response.withStatusCode(200)
                                .withBody(objectMapper.writeValueAsString(allTickets));
                    }
            
                case "POST":
                    CodeDTO newTicket = objectMapper.readValue(event.getBody(), CodeDTO.class);
                    CodeDTO created = service.create(newTicket);
                    return response.withStatusCode(201)
                            .withBody(objectMapper.writeValueAsString(created));
            
                case "PUT":
                    String pathPut = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;
                    if (pathPut == null)
                        return response.withStatusCode(400).withBody("ID es requerido para actualizar");
            
                    CodeDTO updateTicket = objectMapper.readValue(event.getBody(), CodeDTO.class);
                    CodeDTO updated = service.update(Integer.parseInt(pathPut), updateTicket);
                    return response.withStatusCode(200)
                            .withBody(objectMapper.writeValueAsString(updated));
            
                case "DELETE":
                    String pathDelete = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;
                    if (pathDelete == null)
                        return response.withStatusCode(400).withBody("ID es requerido para eliminar");
            
                    service.delete(Integer.parseInt(pathDelete));
                    return response.withStatusCode(204).withBody("");

                case "OPTIONS":
                    return response.withStatusCode(200).withBody("Preflight OK");
            
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
