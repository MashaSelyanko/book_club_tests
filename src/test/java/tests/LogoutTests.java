package tests;

import TestData.TestData;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.logout.LogoutBodyModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseRecordsModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.logout.LogoutSpec.logoutRequestSpec;
import static specs.logout.LogoutSpec.successfulLogoutResponseSpec;
import static specs.registration.RegistrationSpec.RegistrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;

public class LogoutTests extends TestBase {

    TestData testData = new TestData();

    @DisplayName("record_Позитивный тест на 200 статус-код - logout")
    @Test
    public void successfulLogout() {

        RegistrationBodyModel RegistrationData = new RegistrationBodyModel(testData.username, testData.password);
        SuccessfulRegistrationResponseRecordsModel registrationResponse = given(RegistrationRequestSpec)
                .body(RegistrationData)
                .when()
                .post("users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseRecordsModel.class);

        LoginBodyModel loginData = new LoginBodyModel(TestData.VALID_USERNAME, TestData.VALID_PASSWORD);
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
        SuccessfulLoginResponseModel logoutResponse = given(logoutRequestSpec)
                .body(logoutData)
                .when()
                .post("auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseModel.class);
    }

    //SuccessfulLogoutBodyModel
}
