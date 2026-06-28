package tests;

import TestData.TestData;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationErrorResponseModel;
import models.registration.SuccessfulRegistrationResponseRecordsModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static TestData.TestData.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static specs.registration.RegistrationSpec.*;
import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationTests extends TestBase {
    //TestData testData = new TestData();

    @DisplayName("Позитивный тест - успешная регистрация пользователя: 201 статус-код")
    @Test
    public void successfulRegistrationTest() {

        String expectedUsername = getRandomUsername();
        String expectedPassword = getRandomPassword();

        // объявляем переменную заранее, чтобы данные переходили из step в step
        RegistrationBodyModel registrationData = new RegistrationBodyModel
                (expectedUsername, expectedPassword);

        // оборачиваем в AtomicReference локальные переменные, чтобы менять их внутри лямбда-выражений
        AtomicReference<SuccessfulRegistrationResponseRecordsModel> registrationResponse = new AtomicReference<>();

        step("Успешная регистрация пользователя", () -> {
            //работает в связке с конструктором (класс RegistrationBodyPojoModel

            registrationResponse.set(given(userRequestSpec)
                    .config(timeoutConfig)
                    .body(registrationData)
                    .when()
                    .post("users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract()
                    .as(SuccessfulRegistrationResponseRecordsModel.class));
        });

        //Assert
        step("Проверка результата регистрации", () -> {
            // берем объект ответа из обертки один раз
            var response = registrationResponse.get();

            //Подшаг 1
            step("Проверка соответствия username)", () -> {
                assertThat(response.username())
                        .as("Проверка на соответствие username")
                        .isEqualTo(expectedUsername);
            });

            // Подшаг 2
            step("Проверка, что поле firstName вернулось пустым", () -> {
                assertThat(response.firstName())
                        .as("Проверка, что поле firstName вернулось пустое")
                        .isEmpty();
            });

            // Подшаг 3
            step("Проверка, что поле lastName вернулось пустым", () -> {
                assertThat(response.lastName())
                        .as("Проверка, что поле lastName вернулось пустое")
                        .isEmpty();
            });

            // Подшаг 4
            step("Проверка, что ID пользователя — положительное число", () -> {
                assertThat(response.id())
                        .as("Проверка, что id положительное число")
                        .isGreaterThan(0);
            });

            // Подшаг 5
            step("Проверка формата полученного ip-адреса", () -> {
                assertThat(response.remoteAddr())
                        .as("Проверка формата полученного ip-адреса")
                        .matches(IP_ADDRESS_REGEXP);
            });
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

        // оборачиваем в AtomicReference локальные переменные, чтобы менять их внутри лямбда-выражений
        AtomicReference<RegistrationErrorResponseModel> errorResponse = new AtomicReference<>();

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

        step("Отправка дубликата запроса на регистацию", () -> {

            errorResponse.set(given(userRequestSpec)                //добавляем set()
                    .config(timeoutConfig)
                    .body(registrationData)
                    .when()
                    .post("users/register/")
                    .then()
                    .spec(wrongRegistrationResponseSpec)
                    .extract()
                    .as(RegistrationErrorResponseModel.class));
        });

        step("Верификация сообщения об ошибке валидации бэкенда", () -> {
            var error = errorResponse.get(); //var и get() - извлекаем значение из обертки

            //берем текст ошибки из DTO-модели ошибок
            String actualError = error.username().getFirst();

            assertThat(actualError)
                    .as("Проверка текста ошибки дублирования username")
                    .isEqualTo(EXPECTED_ERROR_DUPLICATE_USERNAME_ERROR);
        });
    }

    @DisplayName("Негативный тест - регистрация пользователя с username более 150 символов: 400 статус-код")
    @Test
    public void exceedingMaxLengthUsernameRegistrationTest() {
        // оборачиваем в AtomicReference локальные переменные, чтобы менять их внутри лямбда-выражений
        AtomicReference<RegistrationErrorResponseModel> secondRegistrationResponse = new AtomicReference<>();

        step("Регистрация пользователя с username более 150 символов (400)", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel
                    (exceedingMaxLengthUsername, getRandomPassword());
            secondRegistrationResponse.set(given(userRequestSpec)
                    .config(timeoutConfig)
                    .body(registrationData)
                    .when()
                    .post("users/register/")
                    .then()
                    .spec(exceedingMaxLengthUsernameRegistrationResponseSpec)
                    .extract()
                    .as(RegistrationErrorResponseModel.class));
        });

        step("Верификация сообщения об ошибке валидации бэкенда", () -> {
            String actualError = secondRegistrationResponse.get().username().getFirst();

            assertThat(actualError)
                    .as("Проверка текста ошибки при превышении количества вводимых значений в поле username")
                    .isEqualTo(EXPECTED_ERROR_EXCEEDED_USERNAME_MAX_LENGTH);
        });
    }

    @DisplayName("Негативный тест -регистрация пользователя с password более 128 символов: 400 статус-код")
    @Test
    public void exceedingMaxLengthPasswordRegistrationTest() {
        AtomicReference<RegistrationErrorResponseModel> secondRegistrationResponse = new AtomicReference<>();

        RegistrationBodyModel registrationData =
                new RegistrationBodyModel(getRandomUsername(), exceedingMaxLengthPassword);

        step("Регистрация пользователя с password более 128 символов (400)", () -> {
            secondRegistrationResponse.set(given(userRequestSpec)
                    .config(timeoutConfig)
                    .body(registrationData)
                    .when()
                    .post("users/register/")
                    .then()
                    .spec(exceedingMaxLengthPasswordRegistrationResponseSpec)
                    .extract()
                    .as(RegistrationErrorResponseModel.class));
        });

        step("Верификация сообщения об ошибке валидации бэкенда", () -> {
            String actualError = secondRegistrationResponse.get().password().getFirst();
            assertThat(actualError)
                    .as("Проверка текста ошибки при превышении количества вводимых значений в поле password")
                    .isEqualTo(EXPECTED_ERROR_EXCEEDED_PASSWORD_MAX_LENGTH);
        });
    }
}

