// получение токена

package tests;

import TestData.TestData;
import models.login.EmptyCredentialsLoginResponseModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.WrongCredentialsLoginResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static TestData.TestData.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.*;

public class LoginTests extends TestBase {

    TestData testData = new TestData();

    @DisplayName("Позитивный тест - 200 статус-код - получение токена")
    @Test
    public void successfulLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(TestData.VALID_USERNAME, TestData.VALID_PASSWORD);

        SuccessfulLoginResponseModel loginResponse = given(loginRequestSpec)
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

        //добавляем библиотеку assertj для проверок
        //проверяем, что пришел токен, который содержит ожидаемое значение
        assertThat(actualAccess).startsWith(expectedTokenPath);
        assertThat(actualRefresh).startsWith(expectedTokenPath);
        // проверяем, что Access != Refresh
        assertThat(actualAccess).isNotEqualTo(actualRefresh);

    }

    @DisplayName("Негативный тест - 401 статус-код - некорректный password")
    @Test
    public void wrongPasswordLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(testData.VALID_USERNAME, TestData.WRONG_PASSWORD);

        WrongCredentialsLoginResponseModel loginResponse = given(loginRequestSpec)
                .config(timeoutConfig)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongPasswordLoginResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);

        //фактическое значение забираем
        String actualDetailErrorAccess = loginResponse.detail();

        //добавляем библиотеку assertj для проверок
        // проверяем текст сообщения об ошибке
        assertThat(actualDetailErrorAccess).isEqualTo(EXPECTED_ERROR_WRONG_PASSWORD);
    }

    @DisplayName("Негативный тест - 401 статус-код - некорректный username")
    @Test
    public void wrongUsernameLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(TestData.WRONG_USERNAME, TestData.VALID_PASSWORD);

        WrongCredentialsLoginResponseModel loginResponse = given(loginRequestSpec)
                .config(timeoutConfig)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongUsernameLoginResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);

        //фактическое значение забираем
        String actualDetailError = loginResponse.detail();

        //добавляем библиотеку assertj для проверок
        // проверяем текст сообщения об ошибке
        assertThat(actualDetailError).isEqualTo(EXPECTED_ERROR_WRONG_USERNAME);
    }

    @DisplayName("Негативный тест - 400 статус-код - пустой username")
    @Test
    public void emptyUsernameLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(TestData.EMPTY_VALUE, TestData.VALID_PASSWORD);

        EmptyCredentialsLoginResponseModel loginResponse = given(loginRequestSpec)
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

    @DisplayName("Негативный тест - 400 статус-код - пустой password")
    @Test
    public void emptyPasswordLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(TestData.VALID_USERNAME, TestData.EMPTY_VALUE);

        EmptyCredentialsLoginResponseModel loginResponse = given(loginRequestSpec)
                .config(timeoutConfig)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyPasswordLoginResponseSpec)
                .extract()
                .as(EmptyCredentialsLoginResponseModel.class);

        //получаем список ошибок
        List<String> actualPasswordError = loginResponse.password();

        //проверяем фактический список ошибок на наличие той, что в ожидаемом результате
        assertThat(actualPasswordError).contains(EXPECTED_ERROR_EMPTY_FIELD);
    }

    @DisplayName("Негативный тест - 400 статус-код - username = null")
    @Test
    public void nullUsernameLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(TestData.NULL_VALUE, TestData.VALID_PASSWORD);

        EmptyCredentialsLoginResponseModel loginResponse = given(loginRequestSpec)
                .config(timeoutConfig)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(nullUsernameLoginResponseSpec)
                .extract()
                .as(EmptyCredentialsLoginResponseModel.class);

        //получаем список ошибок
        List<String> actualPasswordError = loginResponse.username();

        //проверяем фактический список ошибок на наличие той, что в ожидаемом результате
        assertThat(actualPasswordError).contains(EXPECTED_ERROR_NULL_FIELD);
    }

    @DisplayName("Негативный тест - 400 статус-код  - пустой JSON")
    @Test
    public void emptyJsonLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(TestData.NULL_VALUE, TestData.NULL_VALUE);

        EmptyCredentialsLoginResponseModel loginResponse = given(loginRequestSpec)
                .config(timeoutConfig)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyJSONLoginResponseSpec)
                .extract()
                .as(EmptyCredentialsLoginResponseModel.class);

        //получаем список ошибок
        List<String> actualResponseError = loginResponse.username();
        List<String> actualPasswordError = loginResponse.password();

        //проверяем фактический список ошибок на наличие ожидаемого текста через AssertJ
        assertThat(actualPasswordError).contains(EXPECTED_ERROR_NULL_FIELD);
        assertThat(actualResponseError).contains(EXPECTED_ERROR_NULL_FIELD);
    }

    @Test
    @DisplayName("Негативный тест - 400 Bad Request при отправке закрывающей скобки")
    public void invalidParenthesisJsonLoginTest() {
        String brokenBody = ")";

        // отправляем строку напрямую и десериализуем в готовую модель ошибок
        WrongCredentialsLoginResponseModel loginResponse = given(loginRequestSpec)
                .config(timeoutConfig)
                .body(brokenBody)
                .when()
                .post("/auth/token/")
                .then()
                .statusCode(400)
                .log().all()
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);

        //проверяем точное совпадение текста ошибки через AssertJ
        assertThat(loginResponse.detail())
                .as("Текст ошибки парсинга JSON некорректен") // на случай, если проверка не пройдена
                .isEqualTo(EXPECTED_ERROR_JSON_PARSE);
    }
}