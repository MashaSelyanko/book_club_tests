package tests;

import TestData.TestData;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.logout.InvalidTokenLogoutResponseModel;
import models.logout.LogoutBodyModel;
import models.logout.RepeatedLogoutResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseRecordsModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static TestData.TestData.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.logout.LogoutSpec.*;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;

public class LogoutTests extends TestBase {

    TestData testData = new TestData();

    @DisplayName("Logout: 200")
    @Test
    public void successfulLogout() {

        RegistrationBodyModel RegistrationData = new RegistrationBodyModel(testData.username, testData.password);
        given(registrationRequestSpec)
                .config(timeoutConfig)
                .body(RegistrationData)
                .when()
                .post("users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseRecordsModel.class);

        LoginBodyModel loginData = new LoginBodyModel(testData.VALID_USERNAME, testData.VALID_PASSWORD);
        SuccessfulLoginResponseModel loginResponse = given(loginRequestSpec)
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
                .post("auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseModel.class);
    }

    @DisplayName("Logout: невалидный токен. 401")
    @Test
    public void invalidTokenLogout() {
        LogoutBodyModel logoutData = new LogoutBodyModel(testData.INVALID_TOKEN);
        SuccessfulLoginResponseModel logoutResponse = given(logoutRequestSpec)
                .config(timeoutConfig)
                .body(logoutData)
                .when()
                .post("auth/logout/")
                .then()
                .spec(invalidLogoutResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseModel.class);

        //String actualErrorWithInvalidToken = logoutResponse,refresh().get(0);
        //assertThat(actualErrorWithInvalidToken).isEqualTo(EXPECTED_ERROR_INVALID_TOKEN);
          }

    @DisplayName("Logout: повторный logout. 401")
    @Test
    public void doubleLogout() {

        RegistrationBodyModel RegistrationData = new RegistrationBodyModel(testData.username, testData.password);
        SuccessfulRegistrationResponseRecordsModel registrationResponse = given(registrationRequestSpec)
                .body(RegistrationData)
                .when()
                .post("users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseRecordsModel.class);

        LoginBodyModel loginData = new LoginBodyModel(testData.VALID_USERNAME, testData.VALID_PASSWORD);
        SuccessfulLoginResponseModel loginResponse = given(loginRequestSpec)
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
                .body(logoutData)
                .when()
                .post("auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseModel.class);

        RepeatedLogoutResponseModel logoutResponse = given(logoutRequestSpec)
                .body(logoutData)
                .when()
                .post("auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec)
                .extract()
                .as(RepeatedLogoutResponseModel.class);

        String actualDetailReusedRefreshToken = logoutResponse.detail();
        String actualCodeReusedRefreshToken = logoutResponse.code();

        assertThat(actualDetailReusedRefreshToken).isEqualTo(EXPECTED_ERROR_TOKEN_IS_BLACKLISTED);
        assertThat(actualCodeReusedRefreshToken).isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
    }
}
