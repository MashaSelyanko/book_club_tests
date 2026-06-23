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
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.logout.LogoutSpec.*;
import static specs.registration.RegistrationSpec.registrationRequestSpec;

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
                .post("/users/register/")
                .then()
                .statusCode(201);

        LoginBodyModel loginData = new LoginBodyModel(testData.username, testData.password);
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
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec);
}

    @DisplayName("Logout: невалидный токен. 401")
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

    @DisplayName("Logout: повторный logout. 401")
    @Test
    public void doubleLogout() {

//        RegistrationBodyModel RegistrationData = new RegistrationBodyModel(testData.username, testData.password);
//        given(registrationRequestSpec)
//                .config(timeoutConfig)
//                .body(RegistrationData)
//                .when()
//                .post("/users/register/")
//                .then()
//                .statusCode(201);

        LoginBodyModel loginData = new LoginBodyModel(testData.username, testData.password);
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
