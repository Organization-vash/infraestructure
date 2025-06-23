package com.vash.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vash.db.DatabaseInitializer;
import com.vash.lambda.model.ModuleDTO;
import com.vash.lambda.service.ModuleServiceLambda;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static {
        DatabaseInitializer.initModulesTable();
    }

    private final ModuleServiceLambda service = new ModuleServiceLambda();
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
            String method = event.getHttpMethod();
            String path = event.getPath();
            log("INFO", "Método recibido: " + method, context, Map.of("path", path));

            switch (method) {
                case "GET":
                    String pathId = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;
                    if (pathId != null) {
                        ModuleDTO one = service.getById(Integer.parseInt(pathId));
                        if (one != null) {
                            log("INFO", "Módulo encontrado", context, Map.of("id", one.getId(), "name", one.getStatus()));
                            return response.withStatusCode(200)
                                    .withBody(objectMapper.writeValueAsString(one));
                        } else {
                            log("WARN", "Módulo no encontrado", context, Map.of("id", pathId));
                            return response.withStatusCode(404).withBody("Módulo no encontrado");
                        }
                    } else {
                        List<ModuleDTO> all = service.getAll();
                        log("INFO", "Listado de módulos obtenido", context, Map.of("cantidad", all.size()));
                        return response.withStatusCode(200)
                                .withBody(objectMapper.writeValueAsString(all));
                    }

                case "POST":
                    ModuleDTO dto = objectMapper.readValue(event.getBody(), ModuleDTO.class);
                    ModuleDTO created = service.create(dto);
                    log("INFO", "Módulo creado", context, Map.of("id", created.getId(), "name", created.getStatus()));
                    return response.withStatusCode(201)
                            .withBody(objectMapper.writeValueAsString(created));

                case "DELETE":
                    String pathDelete = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;
                    if (pathDelete == null) {
                        log("WARN", "ID faltante para eliminar", context, null);
                        return response.withStatusCode(400).withBody("ID es requerido para eliminar");
                    }
                    service.delete(Integer.parseInt(pathDelete));
                    log("INFO", "Módulo eliminado", context, Map.of("id", pathDelete));
                    return response.withStatusCode(204).withBody("");

                case "OPTIONS":
                    return response.withStatusCode(200).withBody("Preflight OK");

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
        try {
            Map<String, Object> log = new HashMap<>();
            log.put("timestamp", System.currentTimeMillis());
            log.put("level", level);
            log.put("function", context.getFunctionName());
            log.put("requestId", context.getAwsRequestId());
            log.put("message", message);
            if (extra != null) log.putAll(extra);
            System.out.println(new ObjectMapper().writeValueAsString(log));
        } catch (Exception ex) {
            System.out.println("Error al generar log estructurado: " + ex.getMessage());
        }
    }
}
