package cl.duoc.auth_service.dto;

public record LoginResponse(String token, String tipo, String rol) {}