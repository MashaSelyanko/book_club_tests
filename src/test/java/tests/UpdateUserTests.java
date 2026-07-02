package tests;

import api_clients.auth.AuthTokenPostApiClient;
import api_clients.users.UsersMePatchApiClient;
import api_clients.users.UsersMePutApiClient;
import api_clients.users.UsersRegisterPostApiClient;
import models.DetailErrorResponseModel;
import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.update_user.PartialUpdateUserWithPatchBodyModel;
import models.update_user.UpdateUserBodyModel;
import models.update_user.UpdateUserResponseInvalidEmailModel;
import models.update_user.UpdateUserResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import test_data.TestData;
import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static test_data.TestData.*;

public class UpdateUserTests extends TestBase {

    @DisplayName("Позитивный тест - обновление пользователя методом PUT: 200 статус-код")
    @Test
    public void successfulUpdateUserWithPutTest() {
        String expectedUsername = TestData.getRandomUsername();
        String expectedPassword = TestData.getRandomPassword();
        String expectedFirstName = getRandomFirstName();
        String expectedLastName = getRandomLastName();
        String expectedEmail = getRandomEmail();

        //регистрация пользователя
        RegistrationBodyModel registrationRequest
                = new RegistrationBodyModel(expectedUsername, expectedPassword);
        UsersRegisterPostApiClient.mainRequest(registrationRequest);

        //получение токена
        LoginBodyModel loginRequest = new LoginBodyModel(expectedUsername, expectedPassword);
        String accessToken = AuthTokenPostApiClient.receiveAccessToken(loginRequest);

        //обновление(PUT)
        UpdateUserBodyModel updateRequest = new UpdateUserBodyModel
                (expectedUsername,expectedFirstName, expectedLastName, expectedEmail);
        UpdateUserResponseModel changeUserResponse
                = UsersMePutApiClient.mainRequest(updateRequest, accessToken); //должен передаваться и токен

        step("Проверка корректного обновления username)", () -> {
            //Assert
            String actualUsername = changeUserResponse.username();

            assertThat(actualUsername)
                    .as("Проверка обновленного username")
                    .isEqualTo(expectedUsername);
        });

        step("Проверка, что полученный id>0)", () -> {
            assertThat(changeUserResponse.id())
                    .as("ID пользователя должен быть больше нуля")
                    .isGreaterThan(0);
        });

        step("Проверка корректного обновления firstName)", () -> {
            assertThat(changeUserResponse.firstName())
                    .as("Проверка обновленного имени")
                    .isEqualTo(expectedFirstName);
        });

        step("Проверка корректного обновления lastName)", () -> {
            assertThat(changeUserResponse.lastName())
                    .as("Проверка обновленной фамилии")
                    .isEqualTo(expectedLastName);
        });

        step("Проверка корректного обновления email)", () -> {
            assertThat(changeUserResponse.email())
                    .as("Проверка обновленного email")
                    .isEqualTo(expectedEmail);
        });

        step("Проверка формата полученного remoteAddr)", () -> {
            assertThat(changeUserResponse.remoteAddr())
                    .as("Проверка формата полученного ip-адреса")
                    .matches(IP_ADDRESS_REGEXP);
        });
    }

    @DisplayName("Позитивный тест - полное обновление пользователя методом PATCH: 200 статус-код")
    @Test
    public void successfulUpdateUserWithPatchTest() {
        String expectedUsername = TestData.getRandomUsername();
        String expectedPassword = TestData.getRandomPassword();
        String expectedFirstName = getRandomFirstName();
        String expectedLastName = getRandomLastName();
        String expectedEmail = getRandomEmail();

        //регистрация пользователя
        RegistrationBodyModel registrationRequest
                = new RegistrationBodyModel(expectedUsername, expectedPassword);
        UsersRegisterPostApiClient.mainRequest(registrationRequest);

        //получение токена
        LoginBodyModel loginRequest = new LoginBodyModel(expectedUsername, expectedPassword);
        String accessToken = AuthTokenPostApiClient.receiveAccessToken(loginRequest);

        //обновление(PATCH)
        UpdateUserBodyModel updateRequest = new UpdateUserBodyModel
                (expectedUsername,expectedFirstName, expectedLastName, expectedEmail);
        UpdateUserResponseModel changePatchUserResponse
                = UsersMePatchApiClient.mainRequestPatch(updateRequest, accessToken); //должен передаваться и токен

        step("Проверка корректного обновления username)", () -> {
            String actualUsername = changePatchUserResponse.username();

            assertThat(actualUsername)
                    .as("Проверка обновленного username")
                    .isEqualTo(expectedUsername);
        });

        step("Проверка, что полученный id>0)", () -> {
            assertThat(changePatchUserResponse.id())
                    .as("ID пользователя должен быть больше нуля")
                    .isGreaterThan(0);
        });

        step("Проверка корректного обновления firstName)", () -> {
            assertThat(changePatchUserResponse.firstName())
                    .as("Проверка обновленного имени")
                    .isEqualTo(expectedFirstName);
        });

        step("Проверка корректного обновления lastName)", () -> {
            assertThat(changePatchUserResponse.lastName())
                    .as("Проверка обновленной фамилии")
                    .isEqualTo(expectedLastName);
        });

        step("Проверка корректного обновления email)", () -> {
            assertThat(changePatchUserResponse.email())
                    .as("Проверка обновленного email")
                    .isEqualTo(expectedEmail);
        });

        step("Проверка формата полученного remoteAddr)", () -> {
            assertThat(changePatchUserResponse.remoteAddr())
                    .as("Проверка формата полученного ip-адреса")
                    .matches(IP_ADDRESS_REGEXP);
        });
    }



    @DisplayName("Позитивный тест - частичное обновление пользователя методом PATCH: 200 статус-код")
    @Test
    public void successfulPartialUpdateUserWithPatchTest() {
        String expectedUsername = TestData.getRandomUsername();
        String expectedPassword = TestData.getRandomPassword();
        String expectedEmail = getRandomEmail();


        //регистрация пользователя
        RegistrationBodyModel registrationRequest
                = new RegistrationBodyModel(expectedUsername, expectedPassword);
        UsersRegisterPostApiClient.mainRequest(registrationRequest);

        //получение токена
        LoginBodyModel loginRequest = new LoginBodyModel(expectedUsername, expectedPassword);
        String accessToken = AuthTokenPostApiClient.receiveAccessToken(loginRequest);

        //обновление(PATCH)
        PartialUpdateUserWithPatchBodyModel partialUpdateRequest = new PartialUpdateUserWithPatchBodyModel
                (expectedUsername, expectedEmail);
        UpdateUserResponseModel partialPatchUserResponse
                = UsersMePatchApiClient.partialPatchRequest
                (partialUpdateRequest, accessToken);

        step("Проверка корректного обновления username)", () -> {
            String actualUsername = partialPatchUserResponse.username();

            assertThat(actualUsername)
                    .as("Проверка обновленного username")
                    .isEqualTo(expectedUsername);
        });

        step("Проверка корректного обновления email)", () -> {
            assertThat(partialPatchUserResponse.email())
                    .as("Проверка обновленного email")
                    .isEqualTo(expectedEmail);
        });

        step("Проверка формата полученного remoteAddr)", () -> {
            assertThat(partialPatchUserResponse.remoteAddr())
                    .as("Проверка формата полученного ip-адреса")
                    .matches(IP_ADDRESS_REGEXP);
        });
    }

    @DisplayName("Негативный тест - частичное обновление пользователя методом PATCH: 400 статус-код")
    @Test
    public void invalidPartialUpdateUserWithPatchTest() {

        String expectedUsername = getRandomUsername();
        String expectedPassword = getRandomPassword();

        //регистрация пользователя
        RegistrationBodyModel registrationRequest
                = new RegistrationBodyModel(expectedUsername, expectedPassword);
        UsersRegisterPostApiClient.mainRequest(registrationRequest);

        //получение токена
        LoginBodyModel loginRequest = new LoginBodyModel(expectedUsername, expectedPassword);
        String accessToken = AuthTokenPostApiClient.receiveAccessToken(loginRequest);

        //обновление(PATCH)
        PartialUpdateUserWithPatchBodyModel invalidPartialUpdateRequest = new PartialUpdateUserWithPatchBodyModel
                (expectedUsername, TestData.WRONG_EMAIL);
        UpdateUserResponseInvalidEmailModel invalidPartialPatchUserResponse
                = UsersMePatchApiClient.invalidPartialRequest
                (invalidPartialUpdateRequest, accessToken);

        step("Верификация сообщения об ошибке валидации бэкенда (400)", () -> {
            //извлекаем текст ошибки
            String actualEmailError = invalidPartialPatchUserResponse.email().get(0);

            assertThat(actualEmailError)
                    .as("Проверка текста ошибки при вводе невалидного email")
                    .isEqualTo(EXPECTED_ERROR_WRONG_EMAIL);
        });
    }

    @DisplayName("Негативный тест - обновление без авторизации методом PATCH: 401 статус-код")
    @Test
    public void unauthorizedUpdateUserWithPatchTest() {

        String expectedUsername = TestData.getRandomUsername();
        String expectedPassword = TestData.getRandomPassword();
        String expectedEmail = getRandomEmail();

        //обновление(PATCH)
        PartialUpdateUserWithPatchBodyModel partialUpdateRequest = new PartialUpdateUserWithPatchBodyModel
                (expectedUsername, expectedEmail);
        DetailErrorResponseModel unauthorizedPatchUserResponse
                = UsersMePatchApiClient.unauthorizedUpdateUserRequest(partialUpdateRequest);

        step("Верификация сообщения об ошибке валидации бэкенда (400)", () -> {
            String actualDetail = unauthorizedPatchUserResponse.detail();
            assertThat(actualDetail)
                    .as("Проверка текста ошибки при обновлении без авторизации (метод PATCH)")
                    .isEqualTo(EXPECTED_UNAUTHORIZED_ERROR);
        });
    }

   }



