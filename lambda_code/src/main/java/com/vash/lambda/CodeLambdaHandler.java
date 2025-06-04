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
import java.util.HashMap;
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

        try {
            String httpMethod = event.getHttpMethod();
            log("INFO", "Método recibido: " + httpMethod, context, Map.of("path", event.getPath()));

            switch (httpMethod) {
                case "GET":
                    String pathId = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;

                    if (pathId != null) {
                        CodeDTO one = service.getById(Integer.parseInt(pathId));
                        if (one != null) {
                            log("INFO", "Ticket obtenido", context, Map.of("id", pathId));
                            return response.withStatusCode(200)
                                    .withBody(objectMapper.writeValueAsString(one));
                        } else {
                            log("WARN", "Ticket no encontrado", context, Map.of("id", pathId));
                            return response.withStatusCode(404).withBody("Ticket no encontrado");
                        }
                    } else {
                        List<CodeDTO> allTickets = service.getAll();
                        log("INFO", "Lista de tickets obtenida", context, Map.of("count", allTickets.size()));
                        return response.withStatusCode(200)
                                .withBody(objectMapper.writeValueAsString(allTickets));
                    }

                case "POST":
                    CodeDTO newTicket = objectMapper.readValue(event.getBody(), CodeDTO.class);
                    CodeDTO created = service.create(newTicket);
                    log("INFO", "Ticket creado", context, Map.of("ticket", created));
                    return response.withStatusCode(201)
                            .withBody(objectMapper.writeValueAsString(created));

                case "PUT":
                    String pathPut = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;
                    if (pathPut == null) {
                        log("WARN", "ID faltante", context, null);
                        return response.withStatusCode(400).withBody("ID es requerido para actualizar");
                    }

                    CodeDTO updateTicket = objectMapper.readValue(event.getBody(), CodeDTO.class);
                    CodeDTO updated = service.update(Integer.parseInt(pathPut), updateTicket);
                    log("INFO", "Ticket actualizado", context, Map.of("id", pathPut));
                    return response.withStatusCode(200)
                            .withBody(objectMapper.writeValueAsString(updated));

                case "DELETE":
                    String pathDelete = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;
                    if (pathDelete == null) {
                        log("WARN", "ID faltante", context, null);
                        return response.withStatusCode(400).withBody("ID es requerido para eliminar");
                    }

                    service.delete(Integer.parseInt(pathDelete));
                    log("INFO", "Ticket eliminado", context, Map.of("id", pathDelete));
                    return response.withStatusCode(204).withBody("");

                default:
                    log("ERROR", "Método no soportado", context, Map.of("method", httpMethod));
                    return response.withStatusCode(405)
                            .withBody("Método no soportado: " + httpMethod);
            }

        } catch (Exception e) {
            log("ERROR", "Excepción durante ejecución", context, Map.of("error", e.getMessage()));
            return response.withStatusCode(500)
                    .withBody("Error interno: " + e.getMessage());
        }
    }

    private void log(String level, String message, Context context, Map<String, Object> extra) {
        Map<String, Object> log = new HashMap<>();
        log.put("timestamp", System.currentTimeMillis());
        log.put("level", level);
        log.put("function", context.getFunctionName());
        log.put("requestId", context.getAwsRequestId());
        log.put("message", message);
        if (extra != null)
            log.putAll(extra);
        System.out.println(new ObjectMapper().valueToTree(log));
    }
}
