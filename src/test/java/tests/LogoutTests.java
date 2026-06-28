package tests;

import TestData.TestData;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.logout.LogoutBodyModel;
import models.logout.RepeatedLogoutResponseModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicReference;
import static TestData.TestData.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.logout.LogoutSpec.*;
import static specs.registration.RegistrationSpec.userRequestSpec;

public class LogoutTests extends TestBase {

    TestData testData = new TestData();

    @DisplayName("Позитивный тест на logout: 200 статус-код")
    @Test
    public void successfulLogout() {

        String expectedUsername = getRandomUsername();
        String expectedPassword = getRandomPassword();
        AtomicReference<SuccessfulLoginResponseModel> loginResponse = new AtomicReference<>();

        RegistrationBodyModel RegistrationData = new RegistrationBodyModel
                (expectedUsername, expectedPassword);
        LoginBodyModel loginData = new LoginBodyModel
                (expectedUsername, expectedPassword);

        step("Успешная регистрация пользователя", () -> {
            given(userRequestSpec)
                    .config(timeoutConfig)
                    .body(RegistrationData)
                    .when()
                    .post("/users/register/")
                    .then();
        });

        step("Получение токена", () -> {
            loginResponse.set(given(userRequestSpec)
                    .config(timeoutConfig)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract()
                    .as(SuccessfulLoginResponseModel.class));
        });

        String refreshToken = loginResponse.get().refresh();

        step("Успешный logout", () -> {
            LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
            given(logoutRequestSpec)
                    .config(timeoutConfig)
                    .body(logoutData)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(successfulLogoutResponseSpec);
        });
    }

    @DisplayName("Негативный тест на logout - невалидный токен: 401 статус-код")
    @Test
    public void invalidTokenLogout() {
        LogoutBodyModel logoutData = new LogoutBodyModel(testData.INVALID_TOKEN);
        AtomicReference<RepeatedLogoutResponseModel> logoutResponse = new AtomicReference<>();

        step("Неуспешный logout (невалидный токен)", () -> {
            logoutResponse.set(given(logoutRequestSpec)
                    .config(timeoutConfig)
                    .body(logoutData)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(invalidLogoutResponseSpec)
                    .extract()
                    .as(RepeatedLogoutResponseModel.class));
        });

        step("Верификация сообщения об ошибке валидации бэкенда (401)", () -> {
            assertThat(logoutResponse.get().detail())
                    .as("Проверка наличия ошибки logout при невалидном токене")
                    .isEqualTo(EXPECTED_ERROR_INVALID_TOKEN);
        });
    }

    @DisplayName("Негативный тест - повторный logout: 401 статус-код")
    @Test
    public void doubleLogout() {
        String expectedUsername = testData.getRandomUsername();
        String expectedPassword = testData.getRandomPassword();
        LoginBodyModel loginData = new LoginBodyModel(expectedUsername, expectedPassword);

        AtomicReference<SuccessfulLoginResponseModel> loginResponse = new AtomicReference<>();
        AtomicReference<RepeatedLogoutResponseModel> logoutResponse = new AtomicReference<>();

        step("Предусловие: успешная регистрация пользователя", () -> {
            RegistrationBodyModel RegistrationData = new RegistrationBodyModel(
                    expectedUsername,
                    expectedPassword);
            given(userRequestSpec)
                    .config(timeoutConfig)
                    .body(RegistrationData)
                    .when()
                    .post("/users/register/")
                    .then();
        });
        step("Отправка запроса на авторизацию (получени токена)", () -> {
            loginResponse.set(given(userRequestSpec)
                    .config(timeoutConfig)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract()
                    .as(SuccessfulLoginResponseModel.class));
        });

        String refreshToken = loginResponse.get().refresh();
        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);

        step("Успешный logout", () -> {
            given(logoutRequestSpec)
                    .config(timeoutConfig)
                    .body(logoutData)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(successfulLogoutResponseSpec);
        });

        step("Повторный logout", () -> {
            logoutResponse.set(given(logoutRequestSpec)
                    .body(logoutData)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(invalidLogoutResponseSpec)
                    .extract()
                    .as(RepeatedLogoutResponseModel.class));
        });

        String actualDetailReusedRefreshToken = logoutResponse.get().detail();
        String actualCodeReusedRefreshToken = logoutResponse.get().code();

        step("Верификация сообщения об ошибке валидации бэкенда при повторном logout (401)", () -> {
            assertThat(actualDetailReusedRefreshToken)
                    .as("Проверка наличия ошибки logout при невалидном токене")
                    .isEqualTo(EXPECTED_ERROR_TOKEN_IS_BLACKLISTED);

            assertThat(actualCodeReusedRefreshToken)
                    .as("Проверка наличия ошибки logout при невалидном токене")
                    .isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
        });
    }
}
