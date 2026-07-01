package api_clients.auth;

import io.qameta.allure.Step;
import models.logout.LogoutBodyModel;
import models.logout.RepeatedLogoutResponseModel;
import static io.restassured.RestAssured.given;
import static specs.logout.LogoutSpec.*;
import static tests.TestBase.timeoutConfig;

public class AuthLogoutPostApiClient {

    @Step("Успешный logout")
    public static void mainLogoutRequest(LogoutBodyModel body) {
        given(logoutRequestSpec)
                .config(timeoutConfig)
                .body(body)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec);
    }

    @Step("Неуспешный logout (невалидный токен/повторный logout)")
    public static RepeatedLogoutResponseModel wrongToken(LogoutBodyModel body) {
        return given(logoutRequestSpec)
                .config(timeoutConfig)
                .body(body)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(invalidLogoutResponseSpec)
                .extract()
                .as(RepeatedLogoutResponseModel.class);
    }
}
