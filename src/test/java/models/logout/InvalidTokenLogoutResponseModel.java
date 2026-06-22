package models.logout;


import java.util.List;

//этот подход, когда много разных параметров передается
public record InvalidTokenLogoutResponseModel(List<String> refresh) {

}