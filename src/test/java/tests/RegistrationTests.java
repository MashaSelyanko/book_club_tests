package tests;

import TestData.TestData;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationErrorResponseModel;
import models.registration.SuccessfulRegistrationResponseRecordsModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static TestData.TestData.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.registration.RegistrationSpec.*;

public class RegistrationTests extends TestBase {
    TestData testData = new TestData();

    @DisplayName("Позитивный тест - успешная регистрация пользователя: 201 статус-код")
    @Test
    public void successfulRegistrationTest() {

        String expectedUsername = testData.getRandomUsername();
        String expectedPassword = testData.getRandomPassword();

        //работает в связке с конструктором (класс RegistrationBodyPojoModel
        RegistrationBodyModel registrationData = new RegistrationBodyModel(expectedUsername,
                expectedPassword);

        SuccessfulRegistrationResponseRecordsModel registrationResponse = given(userRequestSpec)
                .config(timeoutConfig)
                .body(registrationData)
                .when()
                .post("users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseRecordsModel.class);

        //Assert
        String actualUsername = registrationResponse.username();
        //проверки AssertJ
        // указываем сначала фактическое, потом ожидаемое значение username
        assertThat(actualUsername).isEqualTo(expectedUsername);
        assertThat(registrationResponse.id()).isGreaterThan(0); // что >0
        assertThat(registrationResponse.firstName()).isEqualTo("");
        assertThat(registrationResponse.lastName()).isEqualTo("");
        assertThat(registrationResponse.email()).isEqualTo("");

        //проверка на формат полученного ip
        assertThat(registrationResponse.remoteAddr()).matches(IP_ADDRESS_REGEXP);
    }

    @DisplayName("Негативный тест - дублирование при создании клиента: 400 статус-код")
    @Test
    public void existingUserWrongRegistrationTest() {

        String expectedUsername = testData.getRandomUsername();
        String expectedPassword = testData.getRandomPassword();

        RegistrationBodyModel registrationData = new RegistrationBodyModel(expectedUsername,
                expectedPassword);

        SuccessfulRegistrationResponseRecordsModel firstRegistrationResponse = given(userRequestSpec)
                .config(timeoutConfig)
                .body(registrationData)
                .when()
                .post("users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseRecordsModel.class);

        RegistrationErrorResponseModel secondRegistrationResponse = given(userRequestSpec)
                .config(timeoutConfig)
                .body(registrationData)
                .when()
                .post("users/register/")
                .then()
                .spec(wrongRegistrationResponseSpec)
                .extract()
                .as(RegistrationErrorResponseModel.class);

        String actualError = secondRegistrationResponse.username().getFirst();
        assertThat(actualError).isEqualTo(EXPECTED_ERROR_DUPLICATE_USERNAME_ERROR);
    }

    @DisplayName("Негативный тест -регистрация пользователя с username более 150 символов: 400 статус-код")
    @Test
    public void exceedingMaxLengthUsernameRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(testData.exceedingMaxLengthUsername,
                testData.getRandomPassword());
        RegistrationErrorResponseModel secondRegistrationResponse = given(userRequestSpec)
                .config(timeoutConfig)
                .body(registrationData)
                .when()
                .post("users/register/")
                .then()
                .spec(exceedingMaxLengthUsernameRegistrationResponseSpec)
                .extract()
                .as(RegistrationErrorResponseModel.class);

        String actualError = secondRegistrationResponse.username().getFirst();
        assertThat(actualError).isEqualTo(EXPECTED_ERROR_EXCEEDED_USERNAME_MAX_LENGTH);
    }

    @DisplayName("Негативный тест -регистрация пользователя с password более 128 символов: 400 статус-код")
    @Test
    public void exceedingMaxLengthPasswordRegistrationTest() {
        RegistrationBodyModel registrationData =
                new RegistrationBodyModel(testData.getRandomUsername(), testData.exceedingMaxLengthPassword);
        RegistrationErrorResponseModel secondRegistrationResponse = given(userRequestSpec)
                .config(timeoutConfig)
                .body(registrationData)
                .when()
                .post("users/register/")
                .then()
                .spec(exceedingMaxLengthPasswordRegistrationResponseSpec)
                .extract()
                .as(RegistrationErrorResponseModel.class);

        String actualError = secondRegistrationResponse.password().getFirst();
        assertThat(actualError).isEqualTo(EXPECTED_ERROR_EXCEEDED_PASSWORD_MAX_LENGTH);
    }
}

