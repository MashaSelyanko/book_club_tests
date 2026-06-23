package models.logout;


//этот подход, когда много разных параметров передается
public record SuccessfulLogoutResponseModel(String access, String refresh) {}