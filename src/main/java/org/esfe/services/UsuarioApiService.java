package org.esfe.services;

import org.esfe.dtos.usuario.UsuarioDetalleDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class UsuarioApiService {

    private final WebClient webClient;

    // Ya no se inyecta el token con @Value. Se recibe por parámetro.
    public UsuarioApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Obtiene los detalles de un usuario a partir de su ID usando la API externa.
     * El token de autorización es suministrado por el contexto de seguridad en el momento de la llamada.
     *
     * @param userId El ID del usuario.
     * @param apiToken El token JWT del usuario logueado para autorización.
     * @return El DTO de detalle del usuario o un objeto por defecto si falla la llamada.
     */
    public UsuarioDetalleDto obtenerUsuarioPorId(Integer userId, String apiToken) {
        if (userId == null) {
            return new UsuarioDetalleDto(); // Retorna por defecto si no hay ID
        }

        // Manejo básico de token nulo para evitar NullPointerException en el header
        if (apiToken == null || apiToken.isEmpty()) {
            System.err.println("Advertencia: No se proporcionó token de API para la llamada al usuario ID: " + userId);
            return new UsuarioDetalleDto();
        }

        try {
            // 1. Define la llamada con WebClient
            Mono<UsuarioDetalleDto> monoUsuario = webClient.get()
                    .uri("/{id}", userId) // {id} será reemplazado por userId
                    .header("Authorization", "Bearer " + apiToken) // **El token se usa aquí**
                    .retrieve()
                    // Manejo de errores 4xx/5xx: si falla la API, retornamos un error.
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> {
                        System.err.println("Error al llamar API de usuarios. Estado: " + clientResponse.statusCode());
                        return Mono.error(new WebClientResponseException(
                                clientResponse.statusCode().value(),
                                "Fallo en API de usuarios",
                                null, null, null));
                    })
                    .bodyToMono(UsuarioDetalleDto.class); // 2. Espera la respuesta como nuestro DTO

            // 3. Bloquea de forma síncrona para obtener el resultado
            return monoUsuario.block();

        } catch (Exception e) {
            // **Manejo de Errores** (ej. usuario no encontrado, API caída, timeout)
            System.err.println("Excepción al obtener usuario ID " + userId + ": " + e.getMessage());
            // Retorna un objeto con datos por defecto para no romper el flujo principal
            return new UsuarioDetalleDto();
        }
    }
}