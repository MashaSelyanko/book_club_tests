package models.registration;


//этот подход, когда много разных параметров передается
public record RegistrationBodyModel(String username, String password) {}