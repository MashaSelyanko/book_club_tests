package test_data;


import net.datafaker.Faker;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TestData {

    public static Faker faker = new Faker();

    //валидные тестовые данные
    public static @NonNull String getRandomUsername() {
        return "Usr_" + System.currentTimeMillis();
    }

    public static @NonNull String getRandomPassword() {
        return "Pwd_" + System.currentTimeMillis();
    }

    public static final String exceedingMaxLengthUsername = "2".repeat(151);
    public static final String exceedingMaxLengthPassword = "3".repeat(129);

    public static String getRandomFirstName() {
        return faker.name().firstName();
    }

    public static String getRandomLastName() {
        return faker.name().lastName();
    }

    public static String getRandomEmail() {
        return faker.internet().emailAddress();
    }

    // невалидные данные для негативных тестов
    public static final String
            WRONG_PASSWORD = "gaguru12",
            WRONG_USERNAME = "gaguru000",
            WRONG_EMAIL = "www@.ru",

            EMPTY_VALUE = "",
            NULL_VALUE = null,
            INVALID_TOKEN = faker.lorem().characters(16, 32,
                    true, true);

    public static final String
            IP_ADDRESS_REGEXP = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.){3}(25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)$",
            EXPECTED_ERROR_DUPLICATE_USERNAME_ERROR = "A user with that username already exists.",
            EXPECTED_ERROR_EXCEEDED_USERNAME_MAX_LENGTH = "Ensure this field has no more than 150 characters.",
            EXPECTED_ERROR_EXCEEDED_PASSWORD_MAX_LENGTH = "Ensure this field has no more than 128 characters.",
            EXPECTED_ERROR_WRONG_PASSWORD = "Invalid username or password.",
            EXPECTED_ERROR_WRONG_USERNAME = "Invalid username or password.",
            EXPECTED_ERROR_WRONG_EMAIL = "Enter a valid email address.",
            EXPECTED_ERROR_EMPTY_FIELD = "This field may not be blank.",
            EXPECTED_ERROR_NULL_FIELD = "This field may not be null.",
            EXPECTED_ERROR_NULL_JSON = "This field is required.",
            EXPECTED_ERROR_JSON_PARSE = "JSON parse error - unexpected character: line 1 column 2 (char 1)",
            EXPECTED_ERROR_INVALID_TOKEN = "Token is invalid",
            EXPECTED_ERROR_TOKEN_IS_BLACKLISTED = "Token is blacklisted",
            EXPECTED_TOKEN_NOT_VALID_CODE = "token_not_valid",
            EXPECTED_UNAUTHORIZED_ERROR = "Authentication credentials were not provided.";
}
