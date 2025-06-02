package com.vash.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vash.db.DatabaseInitializer;
import com.vash.lambda.model.AgencyDTO;
import com.vash.lambda.service.AgencyServiceLambda;

import java.util.List;
import java.util.Map;

public class AgencyLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static {
        DatabaseInitializer.initAgencyTable();
    }

    private final AgencyServiceLambda service = new AgencyServiceLambda();
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
            Map<String, String> pathParams = event.getPathParameters();

            switch (method) {
                case "GET":
                    if (pathParams != null && pathParams.get("id") != null) {
                        int id = Integer.parseInt(pathParams.get("id"));
                        AgencyDTO agency = service.findById(id);
                        if (agency != null) {
                            return response.withStatusCode(200)
                                    .withBody(objectMapper.writeValueAsString(agency));
                        } else {
                            return response.withStatusCode(404).withBody("Agencia no encontrada");
                        }
                    } else {
                        List<AgencyDTO> list = service.getAll();
                        return response.withStatusCode(200)
                                .withBody(objectMapper.writeValueAsString(list));
                    }

                case "POST":
                    AgencyDTO newAgency = objectMapper.readValue(event.getBody(), AgencyDTO.class);
                    AgencyDTO created = service.createAgency(newAgency);
                    return response.withStatusCode(201)
                            .withBody(objectMapper.writeValueAsString(created));

                case "PUT":
                    if (pathParams == null || pathParams.get("id") == null)
                        return response.withStatusCode(400).withBody("ID es requerido para actualizar");

                    AgencyDTO agencyToUpdate = objectMapper.readValue(event.getBody(), AgencyDTO.class);
                    AgencyDTO updated = service.updateAgency(Integer.parseInt(pathParams.get("id")), agencyToUpdate);
                    return response.withStatusCode(200)
                            .withBody(objectMapper.writeValueAsString(updated));

                case "DELETE":
                    if (pathParams == null || pathParams.get("id") == null)
                        return response.withStatusCode(400).withBody("ID es requerido para eliminar");

                    service.delete(Integer.parseInt(pathParams.get("id")));
                    return response.withStatusCode(204).withBody("");

                case "OPTIONS":
                    return response.withStatusCode(200).withBody("Preflight OK");

                default:
                    return response.withStatusCode(405)
                            .withBody("Método no soportado: " + method);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return response.withStatusCode(500)
                    .withBody("Error interno: " + e.getMessage());
        }
    }
}
