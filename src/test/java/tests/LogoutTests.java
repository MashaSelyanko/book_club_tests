package tests;

import api_clients.auth.AuthLogoutPostApiClient;
import api_clients.auth.AuthTokenPostApiClient;
import api_clients.users.UsersRegisterPostApiClient;
import models.login.LoginBodyModel;
import models.logout.LogoutBodyModel;
import models.logout.RepeatedLogoutResponseModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import test_data.TestData;
import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static test_data.TestData.*;

public class LogoutTests extends TestBase {

    @DisplayName("Позитивный тест на logout: 200 статус-код")
    @Test
    public void successfulLogout() {

        String expectedUsername = TestData.getRandomUsername();
        String expectedPassword = TestData.getRandomPassword();

        //регистрация пользователя
        RegistrationBodyModel registrationRequest
                = new RegistrationBodyModel(expectedUsername, expectedPassword);
        UsersRegisterPostApiClient.mainRequest(registrationRequest);

        //получение токена
        LoginBodyModel loginRequest = new LoginBodyModel(expectedUsername, expectedPassword);
        String refreshToken = AuthTokenPostApiClient.receiveRefreshToken(loginRequest);

        //logout
        LogoutBodyModel logoutRequest = new LogoutBodyModel(refreshToken);
        AuthLogoutPostApiClient.mainLogoutRequest(logoutRequest);

        step("Проверка соответствия username)", () -> {
            assertThat(registrationRequest.username())
                    .as("Проверка на соответствие username")
                    .isEqualTo(expectedUsername);
        });

    }

    @DisplayName("Негативный тест на logout - невалидный токен: 401 статус-код")
    @Test
    public void invalidTokenLogout() {
        LogoutBodyModel logoutRequest = new LogoutBodyModel(TestData.INVALID_TOKEN);
        RepeatedLogoutResponseModel logoutResponse
                = AuthLogoutPostApiClient.wrongToken(logoutRequest);

        step("Верификация сообщения об ошибке валидации бэкенда (401)", () -> {
            assertThat(logoutResponse.detail())
                    .as("Проверка наличия ошибки logout при невалидном токене")
                    .isEqualTo(EXPECTED_ERROR_INVALID_TOKEN);
        });
    }

    @DisplayName("Негативный тест - повторный logout: 401 статус-код")
    @Test
    public void doubleLogout() {

        String expectedUsername = TestData.getRandomUsername();
        String expectedPassword = TestData.getRandomPassword();

        //регистрация пользователя
        RegistrationBodyModel registrationRequest
                = new RegistrationBodyModel(expectedUsername, expectedPassword);
        UsersRegisterPostApiClient.mainRequest(registrationRequest);

        //получение токена
        LoginBodyModel loginRequest = new LoginBodyModel(expectedUsername, expectedPassword);
        String refreshToken = AuthTokenPostApiClient.receiveRefreshToken(loginRequest);

        //logout
        LogoutBodyModel logoutRequest = new LogoutBodyModel(refreshToken);
        AuthLogoutPostApiClient.mainLogoutRequest(logoutRequest);
        //повторный logout
        RepeatedLogoutResponseModel repeatedLogoutResponseModel
                = AuthLogoutPostApiClient.wrongToken(logoutRequest);

        step("Верификация сообщения об ошибке валидации бэкенда при повторном logout (401)", () -> {
            String actualDetailReusedRefreshToken = repeatedLogoutResponseModel.detail();
            String actualCodeReusedRefreshToken = repeatedLogoutResponseModel.code();

            assertThat(actualDetailReusedRefreshToken)
                    .as("Проверка наличия ошибки logout при невалидном токене")
                    .isEqualTo(EXPECTED_ERROR_TOKEN_IS_BLACKLISTED);

            assertThat(actualCodeReusedRefreshToken)
                    .as("Проверка наличия ошибки logout при невалидном токене")
                    .isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
        });
    }
}