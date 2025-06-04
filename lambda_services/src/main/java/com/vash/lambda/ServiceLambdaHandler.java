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
import java.util.HashMap;
import java.util.Map;

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
            log("INFO", "Método recibido: " + httpMethod, context, Map.of("path", event.getPath()));

            switch (httpMethod) {
                case "GET":
                    List<ServiceDTO> services = service.getAll();
                    log("INFO", "Servicios obtenidos", context, Map.of("count", services.size()));
                    return response.withStatusCode(200)
                            .withBody(objectMapper.writeValueAsString(services));

                case "POST":
                    ServiceDTO newService = objectMapper.readValue(event.getBody(), ServiceDTO.class);
                    ServiceDTO created = service.create(newService);
                    log("INFO", "Servicio creado", context, Map.of("created", created));
                    return response.withStatusCode(201)
                            .withBody(objectMapper.writeValueAsString(created));

                case "PUT":
                    String pathPut = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;
                    if (pathPut == null) {
                        log("WARN", "ID faltante para PUT", context, null);
                        return response.withStatusCode(400).withBody("ID es requerido para actualizar");
                    }

                    ServiceDTO updateService = objectMapper.readValue(event.getBody(), ServiceDTO.class);
                    ServiceDTO updated = service.update(Integer.parseInt(pathPut), updateService);
                    log("INFO", "Servicio actualizado", context, Map.of("id", pathPut));
                    return response.withStatusCode(200)
                            .withBody(objectMapper.writeValueAsString(updated));

                case "DELETE":
                    String pathDelete = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;
                    if (pathDelete == null) {
                        log("WARN", "ID faltante para DELETE", context, null);
                        return response.withStatusCode(400).withBody("ID es requerido para eliminar");
                    }

                    service.delete(Integer.parseInt(pathDelete));
                    log("INFO", "Servicio eliminado", context, Map.of("id", pathDelete));
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
