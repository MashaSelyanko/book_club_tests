package TestData;

import com.github.javafaker.Faker;

public class TestData {

    public static Faker faker = new Faker();

    public String
            username = faker.name().firstName(),
            password = faker.name().firstName(),
            exceedingMaxLengthUsername = "2".repeat(151),
            exceedingMaxLengthPassword = "3".repeat(128);

    //валидные тестовые данные
    public static final String
            VALID_USERNAME = "gaguru222",
            VALID_PASSWORD = "gaguru1234";

    // невалидные данные для негативных тестов
    public static final String
            WRONG_PASSWORD = "gaguru12",
            WRONG_USERNAME = "gaguru000",
            EMPTY_VALUE = "",
            NULL_VALUE = null;

    public static final String
            IP_ADDRESS_REGEXP = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.){3}(25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)$",
            EXPECTED_ERROR_DUPLICATE_USERNAME_ERROR = "A user with that username already exists.",
            EXPECTED_ERROR_EXCEEDED_USERNAME_MAX_LENGTH = "Ensure this field has no more than 150 characters.",
            EXPECTED_ERROR_EXCEEDED_PASSWORD_MAX_LENGTH = "Ensure this field has no more than 128 characters.",
            EXPECTED_ERROR_WRONG_PASSWORD = "Invalid username or password.",
            EXPECTED_ERROR_WRONG_USERNAME = "Invalid username or password.",
            EXPECTED_ERROR_EMPTY_FIELD = "This field may not be blank.",
            EXPECTED_ERROR_NULL_FIELD = "This field may not be null.",
            EXPECTED_ERROR_JSON_PARSE = "JSON parse error - unexpected character: line 1 column 1 (char 0)";






}
