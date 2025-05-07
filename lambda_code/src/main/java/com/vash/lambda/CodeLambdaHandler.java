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

public class CodeLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static {
        DatabaseInitializer.initTicketCodeTable();
    }

    private final CodeServiceLambda service = new CodeServiceLambda();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            String httpMethod = event.getHttpMethod();

            switch (httpMethod) {
                case "GET":
                    List<CodeDTO> allTickets = service.getAll();
                    return response.withStatusCode(200)
                            .withBody(objectMapper.writeValueAsString(allTickets));

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
