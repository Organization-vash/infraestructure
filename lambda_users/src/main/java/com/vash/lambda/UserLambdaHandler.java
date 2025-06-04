package com.vash.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vash.lambda.model.UserDTO;
import com.vash.lambda.service.UserServiceLambda;
import com.vash.db.DatabaseInitializer;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class UserLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static {
        try {
            System.out.println("Conectado a la base de datos (users)");
            DatabaseInitializer.initUserTable();
        } catch (Exception e) {
            System.err.println("Error al conectarse a la base de datos (users): " + e.getMessage());
        }
    }

    private final UserServiceLambda userService = new UserServiceLambda();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            String httpMethod = event.getHttpMethod();
            Map<String, String> pathParams = event.getPathParameters();
            log("INFO", "Método recibido: " + httpMethod, context, Map.of("path", event.getPath()));

            switch (httpMethod) {
                case "GET":
                    List<UserDTO> users = userService.getAll();
                    log("INFO", "Usuarios obtenidos", context, Map.of("count", users.size()));
                    return response.withStatusCode(200)
                            .withBody(objectMapper.writeValueAsString(users));

                case "POST":
                    UserDTO newUser = objectMapper.readValue(event.getBody(), UserDTO.class);
                    UserDTO created = userService.createUser(newUser);
                    log("INFO", "Usuario creado", context, Map.of("id", created.getId()));
                    return response.withStatusCode(201)
                            .withBody(objectMapper.writeValueAsString(created));

                case "PUT":
                    String pathPut = pathParams != null ? pathParams.get("id") : null;
                    if (pathPut == null) {
                        log("WARN", "ID faltante para PUT", context, null);
                        return response.withStatusCode(400).withBody("ID es requerido para actualizar");
                    }

                    UserDTO updateUser = objectMapper.readValue(event.getBody(), UserDTO.class);
                    UserDTO updated = userService.updateUser(Integer.parseInt(pathPut), updateUser);
                    log("INFO", "Usuario actualizado", context, Map.of("id", pathPut));
                    return response.withStatusCode(200).withBody(objectMapper.writeValueAsString(updated));

                case "DELETE":
                    String pathDelete = pathParams != null ? pathParams.get("id") : null;
                    if (pathDelete == null) {
                        log("WARN", "ID faltante para DELETE", context, null);
                        return response.withStatusCode(400).withBody("ID es requerido para eliminar");
                    }

                    userService.delete(Integer.parseInt(pathDelete));
                    log("INFO", "Usuario eliminado", context, Map.of("id", pathDelete));
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
