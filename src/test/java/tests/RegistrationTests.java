package tests;

import models.registration.RegistrationBodyModel;
import models.registration.RegistrationErrorResponseModel;
import models.registration.SuccessfulRegistrationResponseRecordsModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static test_data.TestData.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.registration.RegistrationSpec.*;

public class RegistrationTests extends TestBase {

    @DisplayName("Позитивный тест - успешная регистрация пользователя: 201 статус-код")
    @Test
    public void successfulRegistrationTest() {

        String expectedUsername = getRandomUsername();
        String expectedPassword = getRandomPassword();

        // объявляем переменную заранее, чтобы данные переходили из step в step
        RegistrationBodyModel registrationData = new RegistrationBodyModel
                (expectedUsername, expectedPassword);

        SuccessfulRegistrationResponseRecordsModel registrationResponse =
                step("Успешная регистрация пользователя", () -> {
                    //работает в связке с конструктором (класс RegistrationBodyPojoModel
                    return given(userRequestSpec)
                            .config(timeoutConfig)
                            .body(registrationData)
                            .when()
                            .post("users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract()
                            .as(SuccessfulRegistrationResponseRecordsModel.class);
                });

        //Assert
        step("Проверка соответствия username)", () -> {
            assertThat(registrationResponse.username())
                    .as("Проверка на соответствие username")
                    .isEqualTo(expectedUsername);
        });

        step("Проверка, что поле firstName вернулось пустым", () -> {
            assertThat(registrationResponse.firstName())
                    .as("Проверка, что поле firstName вернулось пустое")
                    .isEmpty();
        });

        step("Проверка, что поле lastName вернулось пустым", () -> {
            assertThat(registrationResponse.lastName())
                    .as("Проверка, что поле lastName вернулось пустое")
                    .isEmpty();
        });

        step("Проверка, что ID пользователя — положительное число", () -> {
            assertThat(registrationResponse.id())
                    .as("Проверка, что id положительное число")
                    .isGreaterThan(0);
        });

        step("Проверка формата полученного ip-адреса", () -> {
            assertThat(registrationResponse.remoteAddr())
                    .as("Проверка формата полученного ip-адреса")
                    .matches(IP_ADDRESS_REGEXP);
        });
    }

    @DisplayName("Негативный тест - дублирование при создании клиента: 400 статус-код")
    @Test
    public void existingUserWrongRegistrationTest() {

        String expectedUsername = getRandomUsername();
        String expectedPassword = getRandomPassword();

        // объявляем переменную заранее, чтобы данные переходили из step в step
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

        RegistrationErrorResponseModel errorResponse =
                step("Отправка дубликата запроса на регистацию", () -> {
                    return given(userRequestSpec)
                            .config(timeoutConfig)
                            .body(registrationData)
                            .when()
                            .post("users/register/")
                            .then()
                            .spec(wrongRegistrationResponseSpec)
                            .extract()
                            .as(RegistrationErrorResponseModel.class);
                });

        step("Верификация сообщения об ошибке валидации бэкенда (400)", () -> {

            //берем текст ошибки из DTO-модели ошибок
            String actualError = errorResponse.username().getFirst();

            assertThat(actualError)
                    .as("Проверка текста ошибки дублирования username")
                    .isEqualTo(EXPECTED_ERROR_DUPLICATE_USERNAME_ERROR);
        });
    }

    @DisplayName("Негативный тест - регистрация пользователя с username более 150 символов: 400 статус-код")
    @Test
    public void exceedingMaxLengthUsernameRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel
                (exceedingMaxLengthUsername, getRandomPassword());

        RegistrationErrorResponseModel secondRegistrationResponse =
                step("Регистрация пользователя с username более 150 символов (400)", () -> {
                    return given(userRequestSpec)
                            .config(timeoutConfig)
                            .body(registrationData)
                            .when()
                            .post("users/register/")
                            .then()
                            .spec(exceedingMaxLengthUsernameRegistrationResponseSpec)
                            .extract()
                            .as(RegistrationErrorResponseModel.class);
                });

        step("Верификация сообщения об ошибке валидации бэкенда", () -> {
            String actualError = secondRegistrationResponse.username().getFirst();

            assertThat(actualError)
                    .as("Проверка текста ошибки при превышении количества вводимых значений в поле username")
                    .isEqualTo(EXPECTED_ERROR_EXCEEDED_USERNAME_MAX_LENGTH);
        });
    }

    @DisplayName("Негативный тест -регистрация пользователя с password более 128 символов: 400 статус-код")
    @Test
    public void exceedingMaxLengthPasswordRegistrationTest() {

        RegistrationErrorResponseModel secondRegistrationResponse =
                step("Регистрация пользователя с password более 128 символов (400)", () -> {
                    RegistrationBodyModel registrationData =
                            new RegistrationBodyModel(getRandomUsername(), exceedingMaxLengthPassword);

                    return given(userRequestSpec)
                            .config(timeoutConfig)
                            .body(registrationData)
                            .when()
                            .post("users/register/")
                            .then()
                            .spec(exceedingMaxLengthPasswordRegistrationResponseSpec)
                            .extract()
                            .as(RegistrationErrorResponseModel.class);
                });

        step("Верификация сообщения об ошибке валидации бэкенда", () -> {
            String actualError = secondRegistrationResponse.password().getFirst();
            assertThat(actualError)
                    .as("Проверка текста ошибки при превышении количества вводимых значений в поле password")
                    .isEqualTo(EXPECTED_ERROR_EXCEEDED_PASSWORD_MAX_LENGTH);
        });
    }
}

