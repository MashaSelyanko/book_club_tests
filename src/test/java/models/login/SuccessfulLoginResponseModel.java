package models.login;


//этот подход, когда много разных параметров передается
public record SuccessfulLoginResponseModel(String access, String refresh) {}