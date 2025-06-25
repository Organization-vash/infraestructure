package com.vash.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vash.lambda.model.AgencyDTO;
import com.vash.lambda.service.AgencyServiceLambda;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class AgencyLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final AgencyServiceLambda service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Constructor de producción (se conecta a BD)
    public AgencyLambdaHandler() {
        this.service = new AgencyServiceLambda();
    }

    // Constructor para test (con servicio mockeado)
    public AgencyLambdaHandler(AgencyServiceLambda service) {
        this.service = service;
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
            String method = event.getHttpMethod();
            Map<String, String> pathParams = event.getPathParameters();
            log("INFO", "Método recibido: " + method, context, Map.of("path", event.getPath()));

            switch (method) {
                case "GET":
                    if (pathParams != null && pathParams.get("id") != null) {
                        int id = Integer.parseInt(pathParams.get("id"));
                        AgencyDTO agency = service.findById(id);
                        if (agency != null) {
                            log("INFO", "Agencia encontrada", context, Map.of("id", id));
                            return response.withStatusCode(200)
                                    .withBody(objectMapper.writeValueAsString(agency));
                        } else {
                            log("WARN", "Agencia no encontrada", context, Map.of("id", id));
                            return response.withStatusCode(404).withBody("Agencia no encontrada");
                        }
                    } else {
                        List<AgencyDTO> list = service.getAll();
                        log("INFO", "Lista de agencias obtenida", context, Map.of("count", list.size()));
                        return response.withStatusCode(200)
                                .withBody(objectMapper.writeValueAsString(list));
                    }

                case "POST":
                    AgencyDTO newAgency = objectMapper.readValue(event.getBody(), AgencyDTO.class);
                    AgencyDTO created = service.createAgency(newAgency);
                    log("INFO", "Agencia creada", context, Map.of("id", created.getId()));
                    return response.withStatusCode(201)
                            .withBody(objectMapper.writeValueAsString(created));

                case "PUT":
                    if (pathParams == null || pathParams.get("id") == null) {
                        log("WARN", "ID faltante", context, null);
                        return response.withStatusCode(400).withBody("ID es requerido para actualizar");
                    }

                    AgencyDTO agencyToUpdate = objectMapper.readValue(event.getBody(), AgencyDTO.class);
                    AgencyDTO updated = service.updateAgency(Integer.parseInt(pathParams.get("id")), agencyToUpdate);
                    log("INFO", "Agencia actualizada", context, Map.of("id", pathParams.get("id")));
                    return response.withStatusCode(200)
                            .withBody(objectMapper.writeValueAsString(updated));

                case "DELETE":
                    if (pathParams == null || pathParams.get("id") == null) {
                        log("WARN", "ID faltante", context, null);
                        return response.withStatusCode(400).withBody("ID es requerido para eliminar");
                    }

                    service.delete(Integer.parseInt(pathParams.get("id")));
                    log("INFO", "Agencia eliminada", context, Map.of("id", pathParams.get("id")));
                    return response.withStatusCode(204).withBody("");

                case "OPTIONS":
                    return response.withStatusCode(200).withBody("Preflight OK");

                default:
                    log("ERROR", "Método no soportado", context, Map.of("method", method));
                    return response.withStatusCode(405)
                            .withBody("Método no soportado: " + method);
            }

        } catch (Exception e) {
            log("ERROR", "Excepción durante ejecución", context, Map.of("error", e.getMessage()));
            return response.withStatusCode(500)
                    .withBody("Error interno: " + e.getMessage());
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
            if (extra != null)
                log.putAll(extra);
            System.out.println(objectMapper.writeValueAsString(log));
        } catch (Exception ignored) {
        }
    }
}
