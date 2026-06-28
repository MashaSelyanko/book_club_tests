package tests;

import TestData.TestData;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.logout.LogoutBodyModel;
import models.logout.RepeatedLogoutResponseModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static TestData.TestData.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.logout.LogoutSpec.*;
import static specs.registration.RegistrationSpec.userRequestSpec;

public class LogoutTests extends TestBase {

    TestData testData = new TestData();

    @DisplayName("Позитивный тест на logout: 200 статус-код")
    @Test
    public void successfulLogout() {

        String expectedUsername = testData.getRandomUsername();
        String expectedPassword = testData.getRandomPassword();

        step("Успешная регистрация пользователя", () -> {
            RegistrationBodyModel RegistrationData = new RegistrationBodyModel(expectedUsername,
                    expectedPassword);
            given(userRequestSpec)
                    .config(timeoutConfig)
                    .body(RegistrationData)
                    .when()
                    .post("/users/register/")
                    .then();
        });

        step("Получение токена и успешный logout", () -> {
        LoginBodyModel loginData = new LoginBodyModel(expectedUsername, expectedPassword);
        SuccessfulLoginResponseModel loginResponse = given(userRequestSpec)
                .config(timeoutConfig)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseModel.class);

        String refreshToken = loginResponse.refresh();

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
        given(logoutRequestSpec)
                .config(timeoutConfig)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec);
        });
    }

    @DisplayName("Негативный тест на logout - невалидный токен: 401 статус-код")
    @Test
    public void invalidTokenLogout() {
        LogoutBodyModel logoutData = new LogoutBodyModel(testData.INVALID_TOKEN);
        RepeatedLogoutResponseModel logoutResponse = given(logoutRequestSpec)
                .config(timeoutConfig)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(invalidLogoutResponseSpec)
                .extract()
                .as(RepeatedLogoutResponseModel.class);

        assertThat(logoutResponse.detail()).isEqualTo(EXPECTED_ERROR_INVALID_TOKEN);
    }

    @DisplayName("Негативный тест - повторный logout: 401 статус-код")
    @Test
    public void doubleLogout() {
        String expectedUsername = testData.getRandomUsername();
        String expectedPassword = testData.getRandomPassword();

        RegistrationBodyModel RegistrationData = new RegistrationBodyModel(
                expectedUsername,
                expectedPassword);
        given(userRequestSpec)
                .config(timeoutConfig)
                .body(RegistrationData)
                .when()
                .post("/users/register/")
                .then();

        LoginBodyModel loginData = new LoginBodyModel(expectedUsername, expectedPassword);
        SuccessfulLoginResponseModel loginResponse = given(userRequestSpec)
                .config(timeoutConfig)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseModel.class);

        String refreshToken = loginResponse.refresh();

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
        given(logoutRequestSpec)
                .config(timeoutConfig)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec);

        RepeatedLogoutResponseModel logoutResponse = given(logoutRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(invalidLogoutResponseSpec)
                .extract()
                .as(RepeatedLogoutResponseModel.class);


        String actualDetailReusedRefreshToken = logoutResponse.detail();
        String actualCodeReusedRefreshToken = logoutResponse.code();

        assertThat(actualDetailReusedRefreshToken).isEqualTo(EXPECTED_ERROR_TOKEN_IS_BLACKLISTED);
        assertThat(actualCodeReusedRefreshToken).isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
    }
}
