package api_clients.auth;

import io.qameta.allure.Step;
import models.DetailErrorResponseModel;
import models.login.EmptyCredentialsLoginResponseModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import specs.registration.RegistrationSpec;
import static io.restassured.RestAssured.given;
import static specs.login.LoginSpec.*;
import static specs.registration.RegistrationSpec.userRequestSpec;
import static tests.TestBase.timeoutConfig;

public class AuthTokenPostApiClient {

    @Step("Отправка запроса на авторизацию (получение токена)")
    public static SuccessfulLoginResponseModel mainRequest(LoginBodyModel body) {
        return given(userRequestSpec)
                .config(timeoutConfig)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseModel.class);
    }

    @Step("Отправка запроса на авторизацию с некорректным паролем")
    public static DetailErrorResponseModel wrongPasswordRequest(LoginBodyModel body) {
        return given(userRequestSpec)
                .config(timeoutConfig)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongPasswordLoginResponseSpec)
                .extract()
                .as(DetailErrorResponseModel.class);
    }

    @Step("Отправка запроса на авторизацию с некорректным username")
    public static DetailErrorResponseModel wrongUsernameRequest(LoginBodyModel body) {
        return given(userRequestSpec)
                .config(timeoutConfig)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongUsernameLoginResponseSpec)
                .extract()
                .as(DetailErrorResponseModel.class);
    }

    @Step("Отправка запроса на авторизацию с пустым username")
    public static EmptyCredentialsLoginResponseModel emptyUsernameRequest(LoginBodyModel body) {
        return given(userRequestSpec)
                .config(timeoutConfig)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyUsernameLoginResponseSpec)
                .extract()
                .as(EmptyCredentialsLoginResponseModel.class);
    }

    @Step("Отправка запроса на авторизацию с пустым username")
    public static EmptyCredentialsLoginResponseModel emptyUsername(LoginBodyModel body) {
        return given(RegistrationSpec.userRequestSpec)
                .config(timeoutConfig)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyPasswordLoginResponseSpec)
                .extract()
                .as(EmptyCredentialsLoginResponseModel.class);
    }

    @Step("Отправка запроса на авторизацию с пустым password")
    public static EmptyCredentialsLoginResponseModel emptyPassword(LoginBodyModel body) {
        return given(RegistrationSpec.userRequestSpec)
                .config(timeoutConfig)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyPasswordLoginResponseSpec)
                .extract()
                .as(EmptyCredentialsLoginResponseModel.class);
    }

    @Step("Отправка запроса на авторизацию при username = null")
    public static EmptyCredentialsLoginResponseModel nullUsername(LoginBodyModel body) {
        return given(userRequestSpec)
                .config(timeoutConfig)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(nullUsernameLoginResponseSpec)
                .extract()
                .as(EmptyCredentialsLoginResponseModel.class);
    }

    @Step("Отправка запроса на авторизацию при пустом JSON")
    public static EmptyCredentialsLoginResponseModel nullJSON(String body) {
        return given(userRequestSpec)
                .config(timeoutConfig)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyJSONLoginResponseSpec)
                .extract()
                .as(EmptyCredentialsLoginResponseModel.class);
    }

    @Step("Отправка запроса на авторизацию при некорректном синтаксисе в запросе")
    public static DetailErrorResponseModel invalidSyntax(String body) {
        return given(userRequestSpec)
                .config(timeoutConfig)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .statusCode(400)
                .log().all()
                .extract()
                .as(DetailErrorResponseModel.class);
    }

    @Step("Отправка запроса на авторизацию и получение refresh-токена")
    public static String receiveRefreshToken(LoginBodyModel body) {
        return given(userRequestSpec)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .path("refresh");
    }
}
