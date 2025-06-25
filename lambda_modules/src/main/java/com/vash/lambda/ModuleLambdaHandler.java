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

public class ModuleLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static {
        DatabaseInitializer.initModulesTable();
    }

    private final ModuleServiceLambda service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ModuleLambdaHandler() {
        this.service = new ModuleServiceLambda();
    }

    // Para pruebas
    public ModuleLambdaHandler(ModuleServiceLambda service) {
        this.service = service;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.withHeaders(Map.of(
            "Access-Control-Allow-Origin", "*",
            "Access-Control-Allow-Headers", "Content-Type",
            "Access-Control-Allow-Methods", "OPTIONS,GET,POST,DELETE"
        ));

        try {
            String method = event.getHttpMethod();
            String path = event.getPath();
            String requestId = context != null ? context.getAwsRequestId() : null;
            String function = context != null ? context.getFunctionName() : null;

            System.out.println(String.format("{\"path\":\"%s\",\"level\":\"INFO\",\"requestId\":\"%s\",\"function\":\"%s\",\"message\":\"Método recibido: %s\"}",
                    path, requestId, function, method));

            switch (method) {
                case "GET":
                    String pathId = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;
                    if (pathId != null) {
                        ModuleDTO one = service.getById(Integer.parseInt(pathId));
                        if (one != null) {
                            System.out.println(String.format("{\"level\":\"INFO\",\"requestId\":\"%s\",\"function\":\"%s\",\"id\":\"%s\",\"message\":\"Módulo encontrado\"}", requestId, function, pathId));
                            return response.withStatusCode(200)
                                    .withBody(objectMapper.writeValueAsString(one));
                        } else {
                            return response.withStatusCode(404).withBody("Módulo no encontrado");
                        }
                    } else {
                        List<ModuleDTO> all = service.getAll();
                        System.out.println(String.format("{\"level\":\"INFO\",\"requestId\":\"%s\",\"function\":\"%s\",\"count\":%d,\"message\":\"Lista de módulos obtenida\"}", requestId, function, all.size()));
                        return response.withStatusCode(200)
                                .withBody(objectMapper.writeValueAsString(all));
                    }

                case "POST":
                    ModuleDTO dto = objectMapper.readValue(event.getBody(), ModuleDTO.class);
                    ModuleDTO created = service.create(dto);
                    System.out.println(String.format("{\"level\":\"INFO\",\"requestId\":\"%s\",\"function\":\"%s\",\"id\":%d,\"message\":\"Módulo creado\"}", requestId, function, created.getId()));
                    return response.withStatusCode(201)
                            .withBody(objectMapper.writeValueAsString(created));

                case "DELETE":
                    String pathDelete = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;
                    if (pathDelete == null) {
                        System.out.println(String.format("{\"level\":\"WARN\",\"requestId\":\"%s\",\"function\":\"%s\",\"message\":\"ID faltante para eliminar\"}", requestId, function));
                        return response.withStatusCode(400).withBody("ID es requerido para eliminar");
                    }
                    service.delete(Integer.parseInt(pathDelete));
                    System.out.println(String.format("{\"level\":\"INFO\",\"requestId\":\"%s\",\"function\":\"%s\",\"id\":\"%s\",\"message\":\"Módulo eliminado\"}", requestId, function, pathDelete));
                    return response.withStatusCode(204).withBody("");

                case "OPTIONS":
                    return response.withStatusCode(200).withBody("Preflight OK");

                default:
                    return response.withStatusCode(405).withBody("Método no soportado: " + method);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return response.withStatusCode(500).withBody("Error interno: " + e.getMessage());
        }
    }
}
