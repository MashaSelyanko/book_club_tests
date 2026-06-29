// получение токена

package tests;

import test_data.TestData;
import models.DetailErrorResponseModel;
import models.login.EmptyCredentialsLoginResponseModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseRecordsModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static test_data.TestData.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.*;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.registration.RegistrationSpec.userRequestSpec;

public class LoginTests extends TestBase {

    @DisplayName("Позитивный тест - получение токена: 200 статус-код ")
    @Test
    public void successfulLoginTest() {

        String expectedUsername = getRandomUsername();
        String expectedPassword = getRandomPassword();

       RegistrationBodyModel registrationData = new RegistrationBodyModel
                (expectedUsername, expectedPassword);
        LoginBodyModel loginData = new LoginBodyModel(expectedUsername, expectedPassword);

        step("Предусловие: успешная регистрация пользователя", () -> {
            given(userRequestSpec)
                    .config(timeoutConfig)
                    .body(registrationData)
                    .when()
                    .post("users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract()
                    .as(SuccessfulRegistrationResponseRecordsModel.class);
        });

        SuccessfulLoginResponseModel loginResponse =
        step("Отправка запроса на авторизацию (получени токена)", () -> {
            return given(userRequestSpec)
                    .config(timeoutConfig)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract()
                    .as(SuccessfulLoginResponseModel.class);
        });

        //ожидаемое значение
        String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.";

        step("Верификация структуры и валидности полученных JWT-токенов", () -> {
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
        });
    }

    @DisplayName("Негативный тест- некорректный password: 401 статус-код ")
    @Test
    public void wrongPasswordLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel
                (getRandomUsername(), TestData.WRONG_PASSWORD);

        DetailErrorResponseModel loginResponse =
        step("Отправка запроса на авторизацию с некорректным паролем", () -> {
            return given(userRequestSpec)
                    .config(timeoutConfig)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongPasswordLoginResponseSpec)
                    .extract()
                    .as(DetailErrorResponseModel.class);
        });

        step("Верификация сообщения об ошибке валидации бэкенда (401)", () -> {
            //фактическое значение забираем
            String actualDetailErrorAccess = loginResponse.detail();

            assertThat(actualDetailErrorAccess)
                    .as("Проверка текста ошибки при вводе неверного пароля")
                    .isEqualTo(EXPECTED_ERROR_WRONG_PASSWORD);
        });
    }

    @DisplayName("Негативный тест - некорректный username: 401 статус-код ")
    @Test
    public void wrongUsernameLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(TestData.WRONG_USERNAME, TestData.getRandomPassword());

        DetailErrorResponseModel loginResponse =
        step("Отправка запроса на авторизацию с некорректным username", () -> {
            return given(userRequestSpec)
                    .config(timeoutConfig)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongUsernameLoginResponseSpec)
                    .extract()
                    .as(DetailErrorResponseModel.class);
        });

        step("Верификация сообщения об ошибке валидации бэкенда (401)", () -> {
            //фактическое значение забираем
            String actualDetailErrorAccess = loginResponse.detail();

            assertThat(actualDetailErrorAccess)
                    .as("Проверка текста ошибки при вводе неверного username")
                    .isEqualTo(EXPECTED_ERROR_WRONG_USERNAME);
        });
    }

    @DisplayName("Негативный тест - пустой username: 400 статус-код")
    @Test
    public void emptyUsernameLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(TestData.EMPTY_VALUE, TestData.getRandomPassword());

        EmptyCredentialsLoginResponseModel loginResponse =
        step("Отправка запроса на авторизацию с пустым username", () -> {
            return given(userRequestSpec)
                    .config(timeoutConfig)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(emptyUsernameLoginResponseSpec)
                    .extract()
                    .as(EmptyCredentialsLoginResponseModel.class);
        });

        //получаем список ошибок
        List<String> actualPasswordError = loginResponse.username();

        step("Верификация сообщения об ошибке валидации бэкенда (401)", () -> {
            //проверяем фактический список ошибок на наличие той, что в ожидаемом результате
            assertThat(actualPasswordError)
                    .as("Проверка текста ошибки при вводе пустого username")
                    .contains(EXPECTED_ERROR_EMPTY_FIELD);
        });
    }

    @DisplayName("Негативный тест - пустой password: 400 статус-код")
    @Test
    public void emptyPasswordLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(TestData.getRandomUsername(), TestData.EMPTY_VALUE);
        EmptyCredentialsLoginResponseModel loginResponse =
        step("Отправка запроса на авторизацию с пустым password", () -> {
            return given(userRequestSpec)
                    .config(timeoutConfig)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(emptyPasswordLoginResponseSpec)
                    .extract()
                    .as(EmptyCredentialsLoginResponseModel.class);
        });

        List<String> actualPasswordError = loginResponse.password();

        step("Верификация сообщения об ошибке валидации бэкенда (401)", () -> {
            assertThat(actualPasswordError)
                    .as("Проверка текста ошибки при вводе пустого password")
                    .contains(EXPECTED_ERROR_EMPTY_FIELD);
        });
    }

    @DisplayName("Негативный тест - username = null: 400 статус-код")
    @Test
    public void nullUsernameLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(TestData.NULL_VALUE, TestData.getRandomPassword());

        EmptyCredentialsLoginResponseModel loginResponse =
        step("Отправка запроса на авторизацию при username = null", () -> {
            return given(userRequestSpec)
                    .config(timeoutConfig)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(nullUsernameLoginResponseSpec)
                    .extract()
                    .as(EmptyCredentialsLoginResponseModel.class);
        });

        List<String> actualPasswordError = loginResponse.username();

        step("Верификация сообщения об ошибке валидации бэкенда (400)", () -> {
            assertThat(actualPasswordError)
                    .as("Проверка текста ошибки при username = null")
                    .contains(EXPECTED_ERROR_NULL_FIELD);
        });
    }

    @DisplayName("Негативный тест - пустой JSON: 400 статус-код")
    @Test
    public void emptyJsonLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(TestData.NULL_VALUE, TestData.NULL_VALUE);

        EmptyCredentialsLoginResponseModel loginResponse =
        step("Отправка запроса на авторизацию при пустом JSON", () -> {
            return given(userRequestSpec)
                    .config(timeoutConfig)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(emptyJSONLoginResponseSpec)
                    .extract()
                    .as(EmptyCredentialsLoginResponseModel.class);
        });

        step("Верификация сообщения об ошибке валидации бэкенда (400)", () -> {
            List<String> actualResponseError = loginResponse.username();
            List<String> actualPasswordError = loginResponse.password();

            assertThat(actualPasswordError)
                    .as("Проверка наличия ошибки, что поле username пустое")
                    .contains(EXPECTED_ERROR_NULL_FIELD);

            assertThat(actualResponseError)
                    .as("Проверка наличия ошибки, что поле password пустое")
                    .contains(EXPECTED_ERROR_NULL_FIELD);
        });
    }

    @Test
    @DisplayName("Негативный тест - некорректный синтаксис: 400 статус-код")
    public void invalidParenthesisJsonLoginTest() {
        String brokenBody = ")";

        DetailErrorResponseModel loginResponse =
        step("Отправка запроса на авторизацию при некорректном синтаксисе в запросе", () -> {
            return given(userRequestSpec)
                    .config(timeoutConfig)
                    .body(brokenBody)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .statusCode(400)
                    .log().all()
                    .extract()
                    .as(DetailErrorResponseModel.class);
        });

        step("Верификация сообщения об ошибке валидации бэкенда (400)", () -> {
            //проверяем точное совпадение текста ошибки через AssertJ
            assertThat(loginResponse.detail())
                    .as("Текст ошибки парсинга JSON некорректен") // на случай, если проверка не пройдена
                    .isEqualTo(EXPECTED_ERROR_JSON_PARSE);
        });
    }
}