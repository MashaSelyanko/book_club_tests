package tests;

import TestData.TestData;
import models.DetailErrorResponseModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseRecordsModel;
import models.updateUser.PartialUpdateUserWitchPatchBodyModel;
import models.updateUser.UpdateUserBodyModel;
import models.updateUser.UpdateUserResponseInvalidEmailModel;
import models.updateUser.UpdateUserResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static TestData.TestData.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.UpdateUser.UpdateUserSpec.*;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.registration.RegistrationSpec.userRequestSpec;

public class UpdateUserTests extends TestBase {

    TestData testData = new TestData();


    @DisplayName("Позитивный тест - обновление пользователя методом PUT: 200 статус-код")
    @Test
    public void successfulUpdateUserWithPutTest() {

        String expectedUsername = getRandomUsername();
        String expectedPassword = getRandomPassword();

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

        String accessToken = loginResponse.access();

        // подготовка ожидаемых данных
        String expectedFirstName =getRandomFirstName();
        String expectedLastName = getRandomLastName();
        String expectedEmail = getRandomEmail();

        UpdateUserBodyModel updateUser = new UpdateUserBodyModel(
                expectedUsername,
                expectedFirstName,
                expectedLastName,
                expectedEmail
                );

        UpdateUserResponseModel updateUserResponse = given(updateUserRequestSpec)
                //.config(timeoutConfig)
                .header("Authorization", "Bearer " + accessToken)
                .body(updateUser)
                .when()
                .put("/users/me/")
                .then()
                .spec(updateUserResponseSpec)
                .extract()
                .as(UpdateUserResponseModel.class);

        //Assert
        String actualUsername = updateUserResponse.username();
        //проверки AssertJ
        // указываем сначала фактическое, потом ожидаемое значение username
        assertThat(actualUsername)
                .as("Проверка обновленного username")
                .isEqualTo(expectedUsername);

        assertThat(updateUserResponse.id())
                .as("ID пользователя должен быть больше нуля")
                .isGreaterThan(0);

        assertThat(updateUserResponse.firstName())
                .as("Проверка обновленного имени")
                .isEqualTo(expectedFirstName);

        assertThat(updateUserResponse.lastName())
                .as("Проверка обновленной фамилии")
                .isEqualTo(expectedLastName);

        assertThat(updateUserResponse.email())
                .as("Проверка обновленного email")
                .isEqualTo(expectedEmail);

        //проверка, что поле ip-адреса не пустое
        assertThat(updateUserResponse.remoteAddr()).isNotBlank();

        //проверка на формат полученного ip
        assertThat(updateUserResponse.remoteAddr()).matches(IP_ADDRESS_REGEXP);
    }

    @DisplayName("Позитивнй тест - полное обновление пользователя методом PATCH: 200 статус-код")
    @Test
    public void successfulUpdateUserWithPatchTest() {

        String expectedUsername = getRandomUsername();
        String expectedPassword = getRandomPassword();

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

        String accessToken = loginResponse.access();

        // подготовка ожидаемых данных
        String expectedFirstName =getRandomFirstName();
        String expectedLastName = getRandomLastName();
        String expectedEmail = getRandomEmail();

        UpdateUserBodyModel updateUser = new UpdateUserBodyModel(
                expectedUsername,
                expectedFirstName,
                expectedLastName,
                expectedEmail
        );

        UpdateUserResponseModel updateUserResponse = given(updateUserRequestSpec)
                //.config(timeoutConfig)
                .header("Authorization", "Bearer " + accessToken)
                .body(updateUser)
                .when()
                .patch("/users/me/")
                .then()
                .spec(updateUserResponseSpec)
                .extract()
                .as(UpdateUserResponseModel.class);

        //Assert
        String actualUsername = updateUserResponse.username();
        //проверки AssertJ
        // указываем сначала фактическое, потом ожидаемое значение username
        assertThat(actualUsername)
                .as("Проверка обновленного username")
                .isEqualTo(expectedUsername);

        assertThat(updateUserResponse.id())
                .as("ID пользователя должен быть больше нуля")
                .isGreaterThan(0);

        assertThat(updateUserResponse.firstName())
                .as("Проверка обновленного имени")
                .isEqualTo(expectedFirstName);

        assertThat(updateUserResponse.lastName())
                .as("Проверка обновленной фамилии")
                .isEqualTo(expectedLastName);

        assertThat(updateUserResponse.email())
                .as("Проверка обновленного email")
                .isEqualTo(expectedEmail);

        //проверка, что поле ip-адреса не пустое
        assertThat(updateUserResponse.remoteAddr()).isNotBlank();

        //проверка на формат полученного ip
        assertThat(updateUserResponse.remoteAddr()).matches(IP_ADDRESS_REGEXP);
    }

    @DisplayName("Позитивный тест - частичное обновление пользователя методом PATCH: 200 статус-код")
    @Test
    public void successfulPartialUpdateUserWithPatchTest() {

        String expectedUsername = getRandomUsername();
        String expectedPassword = getRandomPassword();

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

        SuccessfulLoginResponseModel loginResponse = given(updateUserRequestSpec)
                .config(timeoutConfig)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseModel.class);

        String accessToken = loginResponse.access();

        // подготовка ожидаемых данных
               String expectedEmail = getRandomEmail();

        PartialUpdateUserWitchPatchBodyModel partialUpdateUserWitchPatch = new PartialUpdateUserWitchPatchBodyModel(
                expectedUsername,
                expectedEmail
        );

        UpdateUserResponseModel partialUpdateUserResponse = given(updateUserRequestSpec)
                //.config(timeoutConfig)
                .header("Authorization", "Bearer " + accessToken)
                .body(partialUpdateUserWitchPatch)
                .when()
                .patch("/users/me/")
                .then()
                .spec(partialUpdateUserWithPatchResponseSpec)
                .extract()
                .as(UpdateUserResponseModel.class);

        //Assert
        String actualUsername = partialUpdateUserWitchPatch.username();

        assertThat(actualUsername)
                .as("Проверка обновленного username")
                .isEqualTo(expectedUsername);

        assertThat(partialUpdateUserResponse.email())
                .as("Проверка обновленного email")
                .isEqualTo(expectedEmail);

        assertThat(partialUpdateUserResponse.remoteAddr())
                .as("Проверка формата полученного ip-адреса")
                .matches(IP_ADDRESS_REGEXP);
    }

    @DisplayName("Негативный тест - частичное обновление пользователя методом PATCH: 400 статус-код")
    @Test
    public void invalidPartialUpdateUserWithPatchTest() {

        String expectedUsername = getRandomUsername();
        String expectedPassword = getRandomPassword();

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

        SuccessfulLoginResponseModel loginResponse = given(updateUserRequestSpec)
                .config(timeoutConfig)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseModel.class);

        String accessToken = loginResponse.access();

        // подготовка ожидаемых данных
        String expectedEmail = TestData.WRONG_EMAIL;

        PartialUpdateUserWitchPatchBodyModel partialUpdateUserWitchPatch = new PartialUpdateUserWitchPatchBodyModel(
                expectedUsername,
                expectedEmail
        );

        UpdateUserResponseInvalidEmailModel partialUpdateUserResponse = given(updateUserRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(partialUpdateUserWitchPatch)
                .when()
                .patch("/users/me/")
                .then()
                .spec(invalidPartialUpdateUserWithPatchResponseSpec)
                .extract()
                .as(UpdateUserResponseInvalidEmailModel.class);

        assertThat(partialUpdateUserResponse.email().get(0)).isEqualTo(EXPECTED_ERROR_WRONG_EMAIL);
    }

    @DisplayName("Негативный тест - обновление без авторизации методом PATCH: 401 статус-код")
    @Test
    public void unauthorizedUpdateUserWithPatchTest() {

        // подготовка ожидаемых данных
        String expectedUsername = getRandomUsername();
        String expectedEmail = getRandomEmail();

        PartialUpdateUserWitchPatchBodyModel partialUpdateUserWitchPatch = new PartialUpdateUserWitchPatchBodyModel(
                expectedUsername,
                expectedEmail
        );

        DetailErrorResponseModel partialUpdateUserResponse = given(updateUserRequestSpec)
                .config(timeoutConfig)
                .body(partialUpdateUserWitchPatch)
                .when()
                .patch("/users/me/")
                .then()
                .spec(unauthorizedUpdateUserWithPatchResponseSpec)
                .extract()
                .as(DetailErrorResponseModel.class);

        String actualDetail = partialUpdateUserResponse.detail();
        assertThat(actualDetail).isEqualTo(EXPECTED_UNAUTHORIZED_ERROR);
    }

    @DisplayName("Негативный тест - обновление без авторизации методом PUT: 401 статус-код")
    @Test
    public void unauthorizedUpdateUserWithPutTest() {

        // подготовка ожидаемых данных
        String expectedUsername = getRandomUsername();
        String expectedFirstName =getRandomFirstName();
        String expectedLastName = getRandomLastName();
        String expectedEmail = getRandomEmail();

        UpdateUserBodyModel updateUser = new UpdateUserBodyModel(
                expectedUsername,
                expectedFirstName,
                expectedLastName,
                expectedEmail
        );

        DetailErrorResponseModel partialUpdateUserResponse = given(updateUserRequestSpec)
                .config(timeoutConfig)
                .body(updateUser)
                .when()
                .put("/users/me/")
                .then()
                .spec(unauthorizedUpdateUserWithPutResponseSpec)
                .extract()
                .as(DetailErrorResponseModel.class);

        String actualDetail = partialUpdateUserResponse.detail();
        assertThat(actualDetail).isEqualTo(EXPECTED_UNAUTHORIZED_ERROR);
    }
}

