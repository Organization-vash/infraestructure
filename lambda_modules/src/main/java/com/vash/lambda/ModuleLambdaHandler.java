package com.vash.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vash.db.DatabaseInitializer;
import com.vash.lambda.model.ModuleDTO;
import com.vash.lambda.service.ModuleServiceLambda;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ModuleLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    static {
        try {
            System.out.println("Conectado a la base de datos (modules)");
            DatabaseInitializer.initModulesTable();
        } catch (Exception e) {
            System.err.println("Error al conectarse a la base de datos (modules): " + e.getMessage());
        }
    }

    private final ModuleServiceLambda service = new ModuleServiceLambda();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            String method = event.getHttpMethod();
            Map<String, String> pathParams = event.getPathParameters();
            log("INFO", "Método recibido: " + method, context, Map.of("path", event.getPath()));

            switch (method) {
                case "GET":
                    if (pathParams != null && pathParams.get("id") != null) {
                        int id = Integer.parseInt(pathParams.get("id"));
                        ModuleDTO one = service.getById(id);
                        if (one != null) {
                            log("INFO", "Módulo encontrado", context, Map.of("id", id));
                            return response.withStatusCode(200)
                                    .withBody(objectMapper.writeValueAsString(one));
                        } else {
                            log("WARN", "Módulo no encontrado", context, Map.of("id", id));
                            return response.withStatusCode(404).withBody("Módulo no encontrado");
                        }
                    } else {
                        List<ModuleDTO> all = service.getAll();
                        log("INFO", "Lista de módulos obtenida", context, Map.of("count", all.size()));
                        return response.withStatusCode(200)
                                .withBody(objectMapper.writeValueAsString(all));
                    }

                case "POST":
                    ModuleDTO dto = objectMapper.readValue(event.getBody(), ModuleDTO.class);
                    ModuleDTO created = service.create(dto);
                    log("INFO", "Módulo creado", context, Map.of("id", created.getId()));
                    return response.withStatusCode(201)
                            .withBody(objectMapper.writeValueAsString(created));

                case "DELETE":
                    String pathDelete = pathParams != null ? pathParams.get("id") : null;
                    if (pathDelete == null) {
                        log("WARN", "ID faltante para DELETE", context, null);
                        return response.withStatusCode(400).withBody("ID es requerido para eliminar");
                    }
                    service.delete(Integer.parseInt(pathDelete));
                    log("INFO", "Módulo eliminado", context, Map.of("id", pathDelete));
                    return response.withStatusCode(204).withBody("");

                default:
                    log("ERROR", "Método no soportado", context, Map.of("method", method));
                    return response.withStatusCode(405).withBody("Método no soportado: " + method);
            }

        } catch (Exception e) {
            log("ERROR", "Excepción durante ejecución", context, Map.of("error", e.getMessage()));
            return response.withStatusCode(500).withBody("Error interno: " + e.getMessage());
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
