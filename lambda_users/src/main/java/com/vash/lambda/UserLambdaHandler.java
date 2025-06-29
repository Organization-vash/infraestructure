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

public class UserLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static {
        DatabaseInitializer.initUserTable();
    }

    private final UserServiceLambda userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserLambdaHandler() {
        this(new UserServiceLambda());
    }

    public UserLambdaHandler(UserServiceLambda userService) {
        this.userService = userService;
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
            System.out.println("Método HTTP recibido: " + httpMethod);

            switch (httpMethod) {
                case "GET":
                    List<UserDTO> users = userService.getAll();
                    System.out.println("Usuarios obtenidos: " + users.size());
                    return response.withStatusCode(200)
                            .withBody(objectMapper.writeValueAsString(users));

                case "POST":
                    UserDTO newUser = objectMapper.readValue(event.getBody(), UserDTO.class);
                    UserDTO created = userService.createUser(newUser);
                    System.out.println("Usuario creado: ID " + created.getId());
                    return response.withStatusCode(201)
                            .withBody(objectMapper.writeValueAsString(created));

                case "PUT":
                    String pathPut = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;
                    if (pathPut == null) return response.withStatusCode(400).withBody("ID es requerido para actualizar");

                    UserDTO updateUser = objectMapper.readValue(event.getBody(), UserDTO.class);
                    UserDTO updated = userService.updateUser(Integer.parseInt(pathPut), updateUser);
                    System.out.println("Usuario actualizado: ID " + updated.getId());
                    return response.withStatusCode(200).withBody(objectMapper.writeValueAsString(updated));

                case "DELETE":
                    String pathDelete = event.getPathParameters() != null ? event.getPathParameters().get("id") : null;
                    if (pathDelete == null) return response.withStatusCode(400).withBody("ID es requerido para eliminar");

                    userService.delete(Integer.parseInt(pathDelete));
                    System.out.println("Usuario eliminado: ID " + pathDelete);
                    return response.withStatusCode(204).withBody("");

                case "OPTIONS":
                    return response.withStatusCode(200).withBody("Preflight OK");

                default:
                    System.err.println("Método no soportado: " + httpMethod);
                    return response.withStatusCode(405).withBody("Método no soportado: " + httpMethod);
            }

        } catch (Exception e) {
            System.err.println("Error procesando la solicitud: " + e.getMessage());
            e.printStackTrace();
            return response.withStatusCode(500)
                    .withBody("Error interno: " + e.getMessage());
        }
    }
}
