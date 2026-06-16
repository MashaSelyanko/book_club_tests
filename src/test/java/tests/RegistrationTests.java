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

    @DisplayName("record_Позитивный тест на 201 статус-код - создание клиента")
    @Test
    public void successfulRegistrationTest() {

        //работает в связке с конструктором (класс RegistrationBodyPojoModel
        RegistrationBodyModel RegistrationData = new RegistrationBodyModel(testData.username, testData.password);

        SuccessfulRegistrationResponseRecordsModel registrationResponse = given(RegistrationRequestSpec)
                .body(RegistrationData)
                .when()
                .post("users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseRecordsModel.class);

        //Assert
        String actualUsername = registrationResponse.username();
        //проверки AssertJ
        // 1) указываем сначала фактическое, потом ожидаемое значение username
        assertThat(actualUsername).isEqualTo(testData.username);
        assertThat(registrationResponse.id()).isGreaterThan(0); // что >0
        assertThat(registrationResponse.firstName()).isEqualTo("");
        assertThat(registrationResponse.lastName()).isEqualTo("");
        assertThat(registrationResponse.email()).isEqualTo("");

        //проверка на формат полученного ip
        assertThat(registrationResponse.remoteAddr()).matches(IP_ADDRESS_REGEXP);
    }

    @DisplayName("Негативный тест на 400 статус-код - дублирование при создании клиента")
    @Test
    public void existingUserWrongRegistrationTest() {
        RegistrationBodyModel RegistrationData = new RegistrationBodyModel(testData.username, testData.password);

        SuccessfulRegistrationResponseRecordsModel firstRegistrationResponse = given(RegistrationRequestSpec)
                .body(RegistrationData)
                .when()
                .post("users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseRecordsModel.class);

        assertThat(firstRegistrationResponse.username()).isEqualTo(testData.username);

        RegistrationErrorResponseModel secondRegistrationResponse = given(RegistrationRequestSpec)
                .body(RegistrationData)
                .when()
                .post("users/register/")
                .then()
                .spec(wrongRegistrationResponseSpec)
                .extract()
                .as(RegistrationErrorResponseModel.class);

        String actualError = secondRegistrationResponse.username().getFirst();
        assertThat(actualError).isEqualTo(EXPECTED_ERROR_DUPLICATE_USERNAME_ERROR);
    }

    @DisplayName("Негативный тест на 400 статус-код - username более 150 символов")
    @Test
    public void exceedingMaxLengthUsernameRegistrationTest() {
        RegistrationBodyModel RegistrationData = new RegistrationBodyModel(testData.exceedingMaxLengthUsername,
                testData.password);
        RegistrationErrorResponseModel secondRegistrationResponse = given(RegistrationRequestSpec)
                .body(RegistrationData)
                .when()
                .post("users/register/")
                .then()
                .spec(exceedingMaxLengthUsernameRegistrationResponseSpec)
                .extract()
                .as(RegistrationErrorResponseModel.class);

        String actualError = secondRegistrationResponse.username().getFirst();
        assertThat(actualError).isEqualTo(EXPECTED_ERROR_EXCEEDED_USERNAME_MAX_LENGTH);
    }

    @DisplayName("Негативный тест на 400 статус-код - password более 128 символов")
    @Test
    public void exceedingMaxLengthPasswordRegistrationTest() {
        RegistrationBodyModel RegistrationData =
                new RegistrationBodyModel(testData.username, testData.exceedingMaxLengthPassword);
        RegistrationErrorResponseModel secondRegistrationResponse = given(RegistrationRequestSpec)
                .body(RegistrationData)
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

