// получение токена

package tests;

import TestData.TestData;
import models.login.EmptyCredentialsLoginResponseModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.DetailErrorResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseRecordsModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static TestData.TestData.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.*;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.registration.RegistrationSpec.userRequestSpec;

public class LoginTests extends TestBase {

    TestData testData = new TestData();

    @DisplayName("Позитивный тест - получение токена: 200 статус-код ")
    @Test
    public void successfulLoginTest() {

        String expectedUsername = testData.getRandomUsername();
        String expectedPassword = testData.getRandomPassword();

        //работает в связке с конструктором (класс RegistrationBodyPojoModel
        RegistrationBodyModel registrationData = new RegistrationBodyModel(expectedUsername,
                expectedPassword);

        given(userRequestSpec)
                .config(timeoutConfig)
                .body(registrationData)
                .when()
                .post("users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseRecordsModel.class);

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

        //ожидаемое значение
        String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.";

        //фактическое значение забираем
        String actualAccess = loginResponse.access();
        String actualRefresh = loginResponse.refresh();

        assertThat(actualAccess)
                .as("Проверка, что access-токен начинается с ожидаемых символов")
                .startsWith(expectedTokenPath);

        assertThat(actualRefresh)
                .as("Проверка, что refresh-токен начинается с ожидаемых символов")
                .startsWith(expectedTokenPath);

        assertThat(actualAccess)
                .as("Проверка, что access-токен отличается от refresh-токена")
                .isNotEqualTo(actualRefresh);
    }

    @DisplayName("Негативный тест- некорректный password: 401 статус-код ")
    @Test
    public void wrongPasswordLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(TestData.getRandomUsername(), TestData.WRONG_PASSWORD);

        DetailErrorResponseModel loginResponse = given(userRequestSpec)
                .config(timeoutConfig)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongPasswordLoginResponseSpec)
                .extract()
                .as(DetailErrorResponseModel.class);

        //фактическое значение забираем
        String actualDetailErrorAccess = loginResponse.detail();

        assertThat(actualDetailErrorAccess).isEqualTo(EXPECTED_ERROR_WRONG_PASSWORD);
    }

    @DisplayName("Негативный тест - некорректный username: 401 статус-код ")
    @Test
    public void wrongUsernameLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(TestData.WRONG_USERNAME, TestData.getRandomPassword());

        DetailErrorResponseModel loginResponse = given(userRequestSpec)
                .config(timeoutConfig)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongUsernameLoginResponseSpec)
                .extract()
                .as(DetailErrorResponseModel.class);

        String actualDetailError = loginResponse.detail();

        assertThat(actualDetailError).isEqualTo(EXPECTED_ERROR_WRONG_USERNAME);
    }

    @DisplayName("Негативный тест - пустой username: 400 статус-код")
    @Test
    public void emptyUsernameLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(TestData.EMPTY_VALUE, TestData.getRandomPassword());

        EmptyCredentialsLoginResponseModel loginResponse = given(userRequestSpec)
                .config(timeoutConfig)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyUsernameLoginResponseSpec)
                .extract()
                .as(EmptyCredentialsLoginResponseModel.class);

        //получаем список ошибок
        List<String> actualPasswordError = loginResponse.username();

        //проверяем фактический список ошибок на наличие той, что в ожидаемом результате
        assertThat(actualPasswordError).contains(EXPECTED_ERROR_EMPTY_FIELD);
    }

    @DisplayName("Негативный тест - пустой password: 400 статус-код")
    @Test
    public void emptyPasswordLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(TestData.getRandomUsername(), TestData.EMPTY_VALUE);

        EmptyCredentialsLoginResponseModel loginResponse = given(userRequestSpec)
                .config(timeoutConfig)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyPasswordLoginResponseSpec)
                .extract()
                .as(EmptyCredentialsLoginResponseModel.class);

        List<String> actualPasswordError = loginResponse.password();

        assertThat(actualPasswordError).contains(EXPECTED_ERROR_EMPTY_FIELD);
    }

    @DisplayName("Негативный тест - username = null: 400 статус-код")
    @Test
    public void nullUsernameLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(TestData.NULL_VALUE, TestData.getRandomPassword());

        EmptyCredentialsLoginResponseModel loginResponse = given(userRequestSpec)
                .config(timeoutConfig)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(nullUsernameLoginResponseSpec)
                .extract()
                .as(EmptyCredentialsLoginResponseModel.class);

        List<String> actualPasswordError = loginResponse.username();

        assertThat(actualPasswordError).contains(EXPECTED_ERROR_NULL_FIELD);
    }

    @DisplayName("Негативный тест - пустой JSON: 400 статус-код")
    @Test
    public void emptyJsonLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(TestData.NULL_VALUE, TestData.NULL_VALUE);

        EmptyCredentialsLoginResponseModel loginResponse = given(userRequestSpec)
                .config(timeoutConfig)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyJSONLoginResponseSpec)
                .extract()
                .as(EmptyCredentialsLoginResponseModel.class);

        List<String> actualResponseError = loginResponse.username();
        List<String> actualPasswordError = loginResponse.password();

        assertThat(actualPasswordError).contains(EXPECTED_ERROR_NULL_FIELD);
        assertThat(actualResponseError).contains(EXPECTED_ERROR_NULL_FIELD);
    }

    @Test
    @DisplayName("Негативный тест - некорректный синтаксис: 400 статус-код")
    public void invalidParenthesisJsonLoginTest() {
        String brokenBody = ")";

        // отправляем строку напрямую и десериализуем в готовую модель ошибок
        DetailErrorResponseModel loginResponse = given(userRequestSpec)
                .config(timeoutConfig)
                .body(brokenBody)
                .when()
                .post("/auth/token/")
                .then()
                .statusCode(400)
                .log().all()
                .extract()
                .as(DetailErrorResponseModel.class);

        //проверяем точное совпадение текста ошибки через AssertJ
        assertThat(loginResponse.detail())
                .as("Текст ошибки парсинга JSON некорректен") // на случай, если проверка не пройдена
                .isEqualTo(EXPECTED_ERROR_JSON_PARSE);
    }
}