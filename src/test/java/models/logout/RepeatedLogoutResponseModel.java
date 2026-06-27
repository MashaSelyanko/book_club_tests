package models.logout;

import java.util.List;

//этот подход, когда много разных параметров передается
public record RepeatedLogoutResponseModel(String detail, String code) {
}