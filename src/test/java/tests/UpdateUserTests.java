package tests;

import test_data.TestData;
import models.DetailErrorResponseModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseRecordsModel;
import models.updateUser.PartialUpdateUserWithPatchBodyModel;
import models.updateUser.UpdateUserBodyModel;
import models.updateUser.UpdateUserResponseInvalidEmailModel;
import models.updateUser.UpdateUserResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static test_data.TestData.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.update_user.UpdateUserSpec.*;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.registration.RegistrationSpec.userRequestSpec;

public class UpdateUserTests extends TestBase {

    @DisplayName("Позитивный тест - обновление пользователя методом PUT: 200 статус-код")
    @Test
    public void successfulUpdateUserWithPutTest() {

        String expectedUsername = getRandomUsername();
        String expectedPassword = getRandomPassword();

        //работает в связке с конструктором (класс RegistrationBodyPojoModel)
        RegistrationBodyModel registrationData = new RegistrationBodyModel(expectedUsername,
                expectedPassword);

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
                    LoginBodyModel loginData = new LoginBodyModel(expectedUsername, expectedPassword);

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

        // подготовка ожидаемых данных
        String expectedFirstName = getRandomFirstName();
        String expectedLastName = getRandomLastName();
        String expectedEmail = getRandomEmail();

        UpdateUserBodyModel updateUser = new UpdateUserBodyModel(
                expectedUsername,
                expectedFirstName,
                expectedLastName,
                expectedEmail
        );

        UpdateUserResponseModel updateUserResponse =
                step("Успешное обновление пользователя (метод PUT)", () -> {
                    String accessToken = loginResponse.access();

                    return given(updateUserRequestSpec)
                            .header("Authorization", "Bearer " + accessToken)
                            .body(updateUser)
                            .when()
                            .put("/users/me/")
                            .then()
                            .spec(updateUserResponseSpec)
                            .extract()
                            .as(UpdateUserResponseModel.class);
                });

        step("Проверка корректного обновления username)", () -> {
            //Assert
            String actualUsername = updateUserResponse.username();

            assertThat(actualUsername)
                    .as("Проверка обновленного username")
                    .isEqualTo(expectedUsername);
        });

        step("Проверка, что полученный id>0)", () -> {
            assertThat(updateUserResponse
                    .id())
                    .as("ID пользователя должен быть больше нуля")
                    .isGreaterThan(0);
        });

        step("Проверка корректного обновления firstName)", () -> {
            assertThat(updateUserResponse
                    .firstName())
                    .as("Проверка обновленного имени")
                    .isEqualTo(expectedFirstName);
        });

        step("Проверка корректного обновления lastName)", () -> {
            assertThat(updateUserResponse
                    .lastName())
                    .as("Проверка обновленной фамилии")
                    .isEqualTo(expectedLastName);
        });

        step("Проверка корректного обновления email)", () -> {
            assertThat(updateUserResponse
                    .email())
                    .as("Проверка обновленного email")
                    .isEqualTo(expectedEmail);
        });

        step("Проверка формата полученного remoteAddr)", () -> {
            assertThat(updateUserResponse
                    .remoteAddr())
                    .as("Проверка формата полученного ip-адреса")
                    .matches(IP_ADDRESS_REGEXP);
        });
    }

    @DisplayName("Позитивный тест - полное обновление пользователя методом PATCH: 200 статус-код")
    @Test
    public void successfulUpdateUserWithPatchTest() {

        String expectedUsername = getRandomUsername();
        String expectedPassword = getRandomPassword();

        RegistrationBodyModel registrationData = new RegistrationBodyModel
                (expectedUsername, expectedPassword);

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
                    LoginBodyModel loginData = new LoginBodyModel(expectedUsername, expectedPassword);

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

        String accessToken = loginResponse.access();

        String expectedFirstName = getRandomFirstName();
        String expectedLastName = getRandomLastName();
        String expectedEmail = getRandomEmail();

        UpdateUserBodyModel updateUser = new UpdateUserBodyModel(
                expectedUsername,
                expectedFirstName,
                expectedLastName,
                expectedEmail
        );

        UpdateUserResponseModel updateUserResponse =
                step("Успешное полное обновление пользователя (метод PATCH)", () -> {
                    return given(updateUserRequestSpec)
                            .header("Authorization", "Bearer " + accessToken)
                            .body(updateUser)
                            .when()
                            .patch("/users/me/")
                            .then()
                            .spec(updateUserResponseSpec)
                            .extract()
                            .as(UpdateUserResponseModel.class);
                });

        step("Проверка корректного обновления username)", () -> {
            String actualUsername = updateUserResponse.username();

            assertThat(actualUsername)
                    .as("Проверка обновленного username")
                    .isEqualTo(expectedUsername);
        });

        step("Проверка, что полученный id>0)", () -> {
            assertThat(updateUserResponse.id())
                    .as("ID пользователя должен быть больше нуля")
                    .isGreaterThan(0);
        });

        step("Проверка корректного обновления firstName)", () -> {
            assertThat(updateUserResponse.firstName())
                    .as("Проверка обновленного имени")
                    .isEqualTo(expectedFirstName);
        });

        step("Проверка корректного обновления lastName)", () -> {
            assertThat(updateUserResponse.lastName())
                    .as("Проверка обновленной фамилии")
                    .isEqualTo(expectedLastName);
        });

        step("Проверка корректного обновления email)", () -> {
            assertThat(updateUserResponse
                    .email())
                    .as("Проверка обновленного email")
                    .isEqualTo(expectedEmail);
        });

        step("Проверка формата полученного remoteAddr)", () -> {
            assertThat(updateUserResponse
                    .remoteAddr())
                    .as("Проверка формата полученного ip-адреса")
                    .matches(IP_ADDRESS_REGEXP);
        });
    }

    @DisplayName("Позитивный тест - частичное обновление пользователя методом PATCH: 200 статус-код")
    @Test
    public void successfulPartialUpdateUserWithPatchTest() {

        String expectedUsername = getRandomUsername();
        String expectedPassword = getRandomPassword();

        RegistrationBodyModel registrationData = new RegistrationBodyModel(expectedUsername,
                expectedPassword);

        LoginBodyModel loginData = new LoginBodyModel
                (expectedUsername, expectedPassword);

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
                    return given(updateUserRequestSpec)
                            .config(timeoutConfig)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(successfulLoginResponseSpec)
                            .extract()
                            .as(SuccessfulLoginResponseModel.class);
                });

        String expectedEmail = getRandomEmail();

        PartialUpdateUserWithPatchBodyModel partialUpdateUserWitchPatch = new PartialUpdateUserWithPatchBodyModel(
                expectedUsername,
                expectedEmail
        );

        UpdateUserResponseModel partialUpdateUserResponse =
                step("Успешное частичное обновление пользователя (метод PATCH)", () -> {
                    String accessToken = loginResponse.access();

                    return given(updateUserRequestSpec)
                            //.config(timeoutConfig)
                            .header("Authorization", "Bearer " + accessToken)
                            .body(partialUpdateUserWitchPatch)
                            .when()
                            .patch("/users/me/")
                            .then()
                            .spec(partialUpdateUserWithPatchResponseSpec)
                            .extract()
                            .as(UpdateUserResponseModel.class);
                });

        step("Проверка корректного обновления username)", () -> {
            String actualUsername = partialUpdateUserWitchPatch.username();

            assertThat(actualUsername)
                    .as("Проверка обновленного username")
                    .isEqualTo(expectedUsername);
        });

        step("Проверка корректного обновления email)", () -> {
            assertThat(partialUpdateUserResponse.email())
                    .as("Проверка обновленного email")
                    .isEqualTo(expectedEmail);
        });

        step("Проверка формата полученного remoteAddr)", () -> {
            assertThat(partialUpdateUserResponse.remoteAddr())
                    .as("Проверка формата полученного ip-адреса")
                    .matches(IP_ADDRESS_REGEXP);
        });
    }

    @DisplayName("Негативный тест - частичное обновление пользователя методом PATCH: 400 статус-код")
    @Test
    public void invalidPartialUpdateUserWithPatchTest() {

        String expectedUsername = getRandomUsername();
        String expectedPassword = getRandomPassword();

        RegistrationBodyModel registrationData = new RegistrationBodyModel(expectedUsername,
                expectedPassword);

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
                    LoginBodyModel loginData = new LoginBodyModel
                            (expectedUsername, expectedPassword);

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


        UpdateUserResponseInvalidEmailModel partialUpdateUserResponse =
                step("Неуспешное обновление пользователя (метод PATCH) - невалидный email", () -> {

                    String accessToken = loginResponse.access();
                    String expectedEmail = TestData.WRONG_EMAIL;

                    PartialUpdateUserWithPatchBodyModel partialUpdateUserWitchPatch = new PartialUpdateUserWithPatchBodyModel(
                            expectedUsername,
                            expectedEmail
                    );

                    return given(updateUserRequestSpec)
                            .header("Authorization", "Bearer " + accessToken)
                            .body(partialUpdateUserWitchPatch)
                            .when()
                            .patch("/users/me/")
                            .then()
                            .spec(invalidPartialUpdateUserWithPatchResponseSpec)
                            .extract()
                            .as(UpdateUserResponseInvalidEmailModel.class);
                });

        step("Верификация сообщения об ошибке валидации бэкенда (400)", () -> {
            //извлекаем текст ошибки
            String actualEmailError = partialUpdateUserResponse.email().get(0);

            assertThat(actualEmailError)
                    .as("Проверка текста ошибки при вводе невалидного email")
                    .isEqualTo(EXPECTED_ERROR_WRONG_EMAIL);
        });
    }

    @DisplayName("Негативный тест - обновление без авторизации методом PATCH: 401 статус-код")
    @Test
    public void unauthorizedUpdateUserWithPatchTest() {

        String expectedUsername = getRandomUsername();
        String expectedEmail = getRandomEmail();

        PartialUpdateUserWithPatchBodyModel partialUpdateUserWitchPatch = new PartialUpdateUserWithPatchBodyModel(
                expectedUsername,
                expectedEmail
        );

        DetailErrorResponseModel partialUpdateUserResponse =
                step("Неуспешное обновление пользователя (метод PATCH) - без авторизации", () -> {
                    return given(updateUserRequestSpec)
                            .config(timeoutConfig)
                            .body(partialUpdateUserWitchPatch)
                            .when()
                            .patch("/users/me/")
                            .then()
                            .spec(unauthorizedUpdateUserWithPatchResponseSpec)
                            .extract()
                            .as(DetailErrorResponseModel.class);
                });

        step("Верификация сообщения об ошибке валидации бэкенда (400)", () -> {
            String actualDetail = partialUpdateUserResponse.detail();
            assertThat(actualDetail)
                    .as("Проверка текста ошибки при обновлении без авторизации (метод PATCH)")
                    .isEqualTo(EXPECTED_UNAUTHORIZED_ERROR);
        });
    }

    @DisplayName("Негативный тест - обновление без авторизации методом PUT: 401 статус-код")
    @Test
    public void unauthorizedUpdateUserWithPutTest() {

        String expectedUsername = getRandomUsername();
        String expectedFirstName = getRandomFirstName();
        String expectedLastName = getRandomLastName();
        String expectedEmail = getRandomEmail();

        UpdateUserBodyModel updateUser = new UpdateUserBodyModel(
                expectedUsername,
                expectedFirstName,
                expectedLastName,
                expectedEmail
        );

        DetailErrorResponseModel partialUpdateUserResponse =
                step("Неуспешное обновление пользователя (метод PUT) - без авторизации", () -> {
                    return given(updateUserRequestSpec)
                            .body(updateUser)
                            .when()
                            .put("/users/me/")
                            .then()
                            .spec(unauthorizedUpdateUserWithPutResponseSpec)
                            .extract()
                            .as(DetailErrorResponseModel.class);
                });

        step("Верификация сообщения об ошибке валидации бэкенда (400)", () -> {
            String actualDetail = partialUpdateUserResponse.detail();
            assertThat(actualDetail)
                    .as("Проверка текста ошибки при обновлении без авторизации (метод PUT)")
                    .isEqualTo(EXPECTED_UNAUTHORIZED_ERROR);
        });
    }
}



