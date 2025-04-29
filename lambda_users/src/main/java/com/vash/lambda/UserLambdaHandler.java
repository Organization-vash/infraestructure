package com.vash.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.vash.lambda.model.UserDTO;
import com.vash.lambda.service.UserServiceLambda;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserLambdaHandler implements RequestHandler<Map<String, Object>, Object> {

    private final UserServiceLambda userService = new UserServiceLambda();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object handleRequest(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Extraer httpMethod
            String httpMethod = (String) input.get("httpMethod");

            if (httpMethod == null) {
                response.put("statusCode", 400);
                response.put("body", "httpMethod es requerido");
                return response;
            }

            switch (httpMethod) {
                case "GET":
                    List<UserDTO> users = userService.getAll();
                    response.put("statusCode", 200);
                    response.put("body", objectMapper.writeValueAsString(users));
                    break;

                case "POST":
                    String bodyJson = (String) input.get("body");
                    if (bodyJson == null) {
                        response.put("statusCode", 400);
                        response.put("body", "El body es requerido");
                        break;
                    }

                    UserDTO user = objectMapper.readValue(bodyJson, UserDTO.class);
                    UserDTO createdUser = userService.createUser(user);
                    response.put("statusCode", 201);
                    response.put("body", objectMapper.writeValueAsString(createdUser));
                    break;

                default:
                    response.put("statusCode", 400);
                    response.put("body", "Método no soportado: " + httpMethod);
            }

        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("body", "Error interno: " + e.getMessage());
        }

        return response;
    }
}