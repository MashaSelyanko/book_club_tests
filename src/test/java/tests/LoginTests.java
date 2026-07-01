// получение токена

package tests;

import api_clients.auth.AuthTokenPostApiClient;
import api_clients.users.UsersRegisterPostApiClient;
import models.DetailErrorResponseModel;
import models.login.EmptyCredentialsLoginResponseModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import test_data.TestData;
import java.util.List;
import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static test_data.TestData.*;

public class LoginTests extends TestBase {

    @DisplayName("Позитивный тест - получение токена: 200 статус-код ")
    @Test
    public void successfulLoginTest() {
        String expectedUsername = TestData.getRandomUsername();
        String expectedPassword = TestData.getRandomPassword();

        RegistrationBodyModel registrationRequest
                = new RegistrationBodyModel(expectedUsername, expectedPassword);
        UsersRegisterPostApiClient.mainRequest(registrationRequest);

        LoginBodyModel loginRequest = new LoginBodyModel(expectedUsername, expectedPassword);
        SuccessfulLoginResponseModel loginResponse
                = AuthTokenPostApiClient.mainRequest(loginRequest);

        step("Верификация структуры и валидности полученных JWT-токенов", () -> {
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
        });
    }

    @DisplayName("Негативный тест- некорректный password: 401 статус-код ")
    @Test
    public void wrongPasswordLoginTest() {
        String expectedUsername = TestData.getRandomUsername();

        LoginBodyModel wrongPasswordRequest = new LoginBodyModel(expectedUsername, TestData.WRONG_PASSWORD);
        DetailErrorResponseModel errorPasswordResponse
                = AuthTokenPostApiClient.wrongPasswordRequest(wrongPasswordRequest);

        step("Верификация сообщения об ошибке валидации бэкенда (401)", () -> {
            //фактическое значение забираем
            String actualDetailErrorAccess = errorPasswordResponse.detail();

            assertThat(actualDetailErrorAccess)
                    .as("Проверка текста ошибки при вводе неверного пароля")
                    .isEqualTo(EXPECTED_ERROR_WRONG_PASSWORD);
        });
    }

    @DisplayName("Негативный тест - некорректный username: 401 статус-код ")
    @Test
    public void wrongUsernameLoginTest() {

        String expectedPassword = TestData.getRandomPassword();

        LoginBodyModel wrongUsernameRequest = new LoginBodyModel(TestData.WRONG_USERNAME,expectedPassword);
        DetailErrorResponseModel errorUsernameResponse
                = AuthTokenPostApiClient.wrongUsernameRequest(wrongUsernameRequest);

        step("Верификация сообщения об ошибке валидации бэкенда (401)", () -> {
            //фактическое значение забираем
            String actualDetailErrorAccess = errorUsernameResponse.detail();

            assertThat(actualDetailErrorAccess)
                    .as("Проверка текста ошибки при вводе неверного username")
                    .isEqualTo(EXPECTED_ERROR_WRONG_USERNAME);
        });
    }
    @DisplayName("Негативный тест - пустой username: 400 статус-код")
    @Test
    public void emptyUsernameLoginTest() {
        String expectedPassword = TestData.getRandomPassword();

        //Arrange
        LoginBodyModel emptyUsernameRequest
                = new LoginBodyModel(TestData.EMPTY_VALUE, expectedPassword);
        EmptyCredentialsLoginResponseModel errorEmptyUsernameResponse
                = AuthTokenPostApiClient.emptyUsernameRequest(emptyUsernameRequest);

        step("Верификация сообщения об ошибке валидации бэкенда (401)", () -> {
            //получаем список ошибок
            List<String> actualPasswordError = errorEmptyUsernameResponse.username();

            //проверяем фактический список ошибок на наличие той, что в ожидаемом результате
            assertThat(actualPasswordError)
                    .as("Проверка текста ошибки при вводе пустого username")
                    .contains(EXPECTED_ERROR_EMPTY_FIELD);
        });
    }

    @DisplayName("Негативный тест - пустой password: 400 статус-код")
    @Test
    public void emptyPasswordLoginTest() {
        String expectedUsername = TestData.getRandomUsername();

        LoginBodyModel emptyPasswordRequest = new LoginBodyModel(expectedUsername, EMPTY_VALUE);
        EmptyCredentialsLoginResponseModel errorEmptyPasswordResponse
                = AuthTokenPostApiClient.emptyPassword(emptyPasswordRequest);

        step("Верификация сообщения об ошибке валидации бэкенда (401)", () -> {
            List<String> actualPasswordError = errorEmptyPasswordResponse.password();

            assertThat(actualPasswordError)
                    .as("Проверка текста ошибки при вводе пустого password")
                    .contains(EXPECTED_ERROR_EMPTY_FIELD);
        });
    }

    @DisplayName("Негативный тест - username = null: 400 статус-код")
    @Test
    public void nullUsernameLoginTest() {
        String expectedPassword = TestData.getRandomPassword();

        //Arrange
        LoginBodyModel nullUsernameRequest = new LoginBodyModel(NULL_VALUE, expectedPassword);
        //Acc
        EmptyCredentialsLoginResponseModel errorNullUsernameResponse
                = AuthTokenPostApiClient.nullUsername(nullUsernameRequest);

        step("Верификация сообщения об ошибке валидации бэкенда (400)", () -> {
            List<String> actualPasswordError = errorNullUsernameResponse.username();

            assertThat(actualPasswordError)
                    .as("Проверка текста ошибки при username = null")
                    .contains(EXPECTED_ERROR_NULL_FIELD);
        });
    }

    @DisplayName("Негативный тест - пустой JSON: 400 статус-код")
    @Test
    public void emptyJsonLoginTest() {

String emptyJsonBody = "{}";
EmptyCredentialsLoginResponseModel errorNullJsonResponse
                = AuthTokenPostApiClient.nullJSON(emptyJsonBody);


        step("Верификация сообщения об ошибке валидации бэкенда (400)", () -> {
            List<String> actualResponseError = errorNullJsonResponse.username();
            List<String> actualPasswordError = errorNullJsonResponse.password();

            assertThat(actualPasswordError)
                    .as("Проверка наличия ошибки, что поле username пустое")
                    .contains(EXPECTED_ERROR_NULL_JSON);

            assertThat(actualResponseError)
                    .as("Проверка наличия ошибки, что поле password пустое")
                    .contains(EXPECTED_ERROR_NULL_JSON);
        });
    }

    @Test
    @DisplayName("Негативный тест - некорректный синтаксис: 400 статус-код")
    public void invalidParenthesisJsonLoginTest() {

        String invalidSyntaxRequest = "{username}";
        DetailErrorResponseModel invalidSyntaxResponse
                = AuthTokenPostApiClient.invalidSyntax(invalidSyntaxRequest);

        step("Верификация сообщения об ошибке валидации бэкенда (400)", () -> {
            //проверяем точное совпадение текста ошибки через AssertJ
            assertThat(invalidSyntaxResponse.detail())
                    .as("Текст ошибки парсинга JSON некорректен") // на случай, если проверка не пройдена
                    .isEqualTo(EXPECTED_ERROR_JSON_PARSE);
        });
    }
}