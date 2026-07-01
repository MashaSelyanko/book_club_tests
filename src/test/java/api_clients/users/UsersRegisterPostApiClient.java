package api_clients.users;

import io.qameta.allure.Step;
import models.login.EmptyCredentialsLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationErrorResponseModel;
import models.registration.SuccessfulRegistrationResponseRecordsModel;
import specs.registration.RegistrationSpec;
import tests.TestBase;
import static io.restassured.RestAssured.given;
import static specs.login.LoginSpec.emptyPasswordLoginResponseSpec;
import static specs.login.LoginSpec.userRequestSpec;
import static specs.registration.RegistrationSpec.*;

public class UsersRegisterPostApiClient extends TestBase {

    @Step("Успешная регистрация пользователя")
    public static SuccessfulRegistrationResponseRecordsModel mainRequest(RegistrationBodyModel body) {
        return given(userRequestSpec)
                .config(timeoutConfig)
                .body(body)
                .when()
                .post("users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseRecordsModel.class);
    }

    @Step("Отправка дубликата запроса на регистацию")
    public static RegistrationErrorResponseModel dublicateRequest(RegistrationBodyModel body) {
        return given(RegistrationSpec.userRequestSpec)
                .config(timeoutConfig)
                .body(body)
                .when()
                .post("users/register/")
                .then()
                .spec(wrongRegistrationResponseSpec)
                .extract()
                .as(RegistrationErrorResponseModel.class);
    }

    @Step("Регистрация пользователя с username более 150 символов (400)")
    public static RegistrationErrorResponseModel wrongUsername(RegistrationBodyModel body) {
            return given(RegistrationSpec.userRequestSpec)
                    .config(timeoutConfig)
                    .body(body)
                    .when()
                    .post("users/register/")
                    .then()
                    .spec(exceedingMaxLengthUsernameRegistrationResponseSpec)
                    .extract()
                    .as(RegistrationErrorResponseModel.class);
    }

        @Step("Регистрация пользователя с password более 128 символов (400)")
                public static RegistrationErrorResponseModel wrongPassword(RegistrationBodyModel body) {
        return given(RegistrationSpec.userRequestSpec)
                .config(timeoutConfig)
                .body(body)
                .when()
                .post("users/register/")
                .then()
                .spec(exceedingMaxLengthPasswordRegistrationResponseSpec)
                .extract()
                .as(RegistrationErrorResponseModel.class);
    }



}

