package tests;

import api_clients.users.UsersRegisterPostApiClient;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationErrorResponseModel;
import models.registration.SuccessfulRegistrationResponseRecordsModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import test_data.TestData;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static test_data.TestData.*;

public class RegistrationTests extends TestBase {

    @DisplayName("Позитивный тест - успешная регистрация пользователя: 201 статус-код")
    @Test
    public void successfulRegistrationTest() {

        String expectedUsername = TestData.getRandomUsername();
        String expectedPassword = TestData.getRandomPassword();

        RegistrationBodyModel registrationRequest
                = new RegistrationBodyModel(expectedUsername, expectedPassword);

        SuccessfulRegistrationResponseRecordsModel registrationResponse
                = UsersRegisterPostApiClient.mainRequest(registrationRequest);

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

        String expectedUsername = TestData.getRandomUsername();
        String expectedPassword = TestData.getRandomPassword();

        RegistrationBodyModel registrationRequest
                = new RegistrationBodyModel(expectedUsername, expectedPassword);

        //метод без объявления переменной, т.к. для предусловия нужно только вызвать метод
        UsersRegisterPostApiClient.mainRequest(registrationRequest);

        RegistrationErrorResponseModel errorDublicateResponse
                = UsersRegisterPostApiClient.dublicateRequest(registrationRequest);

        step("Верификация сообщения об ошибке валидации бэкенда (400)", () -> {
            //берем текст ошибки из DTO-модели ошибок
            String actualError = errorDublicateResponse.username().getFirst();

            assertThat(actualError)
                    .as("Проверка текста ошибки дублирования username")
                    .isEqualTo(EXPECTED_ERROR_DUPLICATE_USERNAME_ERROR);
        });
    }

    @DisplayName("Негативный тест - регистрация пользователя с username более 150 символов: 400 статус-код")
    @Test
    public void exceedingMaxLengthUsernameRegistrationTest() {
       String expectedPassword = TestData.getRandomPassword();

        RegistrationBodyModel registrationRequest
                = new RegistrationBodyModel(TestData.exceedingMaxLengthUsername, expectedPassword);

        RegistrationErrorResponseModel errorUsernameResponse
                = UsersRegisterPostApiClient.wrongUsername(registrationRequest);


        step("Верификация сообщения об ошибке валидации бэкенда", () -> {
            String actualError = errorUsernameResponse.username().getFirst();

            assertThat(actualError)
                    .as("Проверка текста ошибки при превышении количества вводимых значений в поле username")
                    .isEqualTo(EXPECTED_ERROR_EXCEEDED_USERNAME_MAX_LENGTH);
        });
    }

    @DisplayName("Негативный тест - регистрация пользователя с password более 128 символов: 400 статус-код")
    @Test
    public void exceedingMaxLengthPasswordRegistrationTest() {
        String expectedUsername = TestData.getRandomUsername();

        RegistrationBodyModel registrationRequest
                = new RegistrationBodyModel(expectedUsername, TestData.exceedingMaxLengthPassword);

        RegistrationErrorResponseModel errorPasswordResponse
                = UsersRegisterPostApiClient.wrongPassword(registrationRequest);

        step("Верификация сообщения об ошибке валидации бэкенда", () -> {
            String actualError = errorPasswordResponse.password().getFirst();
            assertThat(actualError)
                    .as("Проверка текста ошибки при превышении количества вводимых значений в поле password")
                    .isEqualTo(EXPECTED_ERROR_EXCEEDED_PASSWORD_MAX_LENGTH);
        });
    }
}

