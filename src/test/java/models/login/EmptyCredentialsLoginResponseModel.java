package models.login;

// модель десереализует JSON-ответ от сервера, происходит парсинг

import java.util.List;

//этот подход, когда много разных параметров передается
public record EmptyCredentialsLoginResponseModel
        (List<String> password, List<String> username) {}