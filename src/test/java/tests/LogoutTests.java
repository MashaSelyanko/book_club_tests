package tests;

import test_data.TestData;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.logout.LogoutBodyModel;
import models.logout.RepeatedLogoutResponseModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static test_data.TestData.*;
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

        SuccessfulLoginResponseModel loginResponse =
                step("Получение токена", () -> {
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

        step("Успешный logout", () -> {
            String refreshToken = loginResponse.refresh();

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
        RepeatedLogoutResponseModel logoutResponse =
                step("Неуспешный logout (невалидный токен)", () -> {
                    LogoutBodyModel logoutData = new LogoutBodyModel(testData.INVALID_TOKEN);

                    return given(logoutRequestSpec)
                            .config(timeoutConfig)
                            .body(logoutData)
                            .when()
                            .post("/auth/logout/")
                            .then()
                            .spec(invalidLogoutResponseSpec)
                            .extract()
                            .as(RepeatedLogoutResponseModel.class);
                });

        step("Верификация сообщения об ошибке валидации бэкенда (401)", () -> {
            assertThat(logoutResponse.detail())
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

        String refreshToken = loginResponse.refresh();
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

        RepeatedLogoutResponseModel logoutResponse =
                step("Повторный logout", () -> {
                    return given(logoutRequestSpec)
                            .body(logoutData)
                            .when()
                            .post("/auth/logout/")
                            .then()
                            .spec(invalidLogoutResponseSpec)
                            .extract()
                            .as(RepeatedLogoutResponseModel.class);
                });

        step("Верификация сообщения об ошибке валидации бэкенда при повторном logout (401)", () -> {
            String actualDetailReusedRefreshToken = logoutResponse.detail();
            String actualCodeReusedRefreshToken = logoutResponse.code();

            assertThat(actualDetailReusedRefreshToken)
                    .as("Проверка наличия ошибки logout при невалидном токене")
                    .isEqualTo(EXPECTED_ERROR_TOKEN_IS_BLACKLISTED);

            assertThat(actualCodeReusedRefreshToken)
                    .as("Проверка наличия ошибки logout при невалидном токене")
                    .isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
        });
    }
}
