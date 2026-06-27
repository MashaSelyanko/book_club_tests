package tests.examples;

import io.restassured.http.ContentType;
import models.registration.model_examples.lombok.RegistrationBodyLombokModel;
import models.registration.model_examples.lombok.RegistrationResponseLombokModel;
import models.registration.model_examples.pojo.RegistrationBodyPojoModel;
import models.registration.model_examples.pojo.RegistrationResponsePojoModel;
import models.registration.model_examples.records.RegistrationBodyRecordsModel;
import models.registration.model_examples.records.RegistrationErrorResponseRecordsModel;
import models.registration.model_examples.records.RegistrationResponseRecordsModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("Учебные примеры отключены, чтобы не спамить в отчет")
public class RegistrationTests_with_model_examples {
    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().firstName();
    }

    @DisplayName("Позитивный тест на 201 статус-код")
    @Test
    public void successfulRegistrationTest() {

        //убрать в model
        String data = "{\"username\":\"" + username + "\"," +
                "\"password\": \"" + password + "\"}";

//body {
//        "username":"mariya",
//        "password":"string"
//    }
//        https://book-club.qa.guru/api/v1/users/register/}

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .body("username", is(username)) //вынести из теста
                .body("id", notNullValue());  //вынести из теста
    }

    @DisplayName("Pojo_Позитивный тест на 201 статус-код")
    @Test
    public void successfulRegistrationTest_with_pojo() {

        RegistrationBodyPojoModel data = new RegistrationBodyPojoModel();
        data.setUsername(username); //заполняем поле username тестовыми данными
        data.setPassword(password);

        //работает в связке с конструктором (класс RegistrationBodyPojoModel, строки 14-18)
        //RegistrationBodyPojoModel data = new RegistrationBodyPojoModel(username, password);

        RegistrationResponsePojoModel registrationResponse = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .as(RegistrationResponsePojoModel.class);

        assertEquals(username, registrationResponse.getUsername());
//
//                .body("username", is(username)) //вынести из теста
//                .body("id", notNullValue());  //вынести из теста
    }

    @DisplayName("Lombok_Позитивный тест на 201 статус-код")
    @Test
    public void successfulRegistrationTest_with_lombok() {

        RegistrationBodyLombokModel data = new RegistrationBodyLombokModel();
        data.setUsername(username); //заполняем поле username тестовыми данными
        data.setPassword(password);

        //работает в связке с конструктором (класс RegistrationBodyPojoModel, строки 14-18)
        //RegistrationBodyLombokModel data = new RegistrationBodyLombokModel(username, password);

        RegistrationResponseLombokModel registrationResponse = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .as(RegistrationResponseLombokModel.class);

        assertEquals(username, registrationResponse.getUsername());
//
//                .body("username", is(username)) //вынести из теста
//                .body("id", notNullValue());  //вынести из теста
    }

    @DisplayName("record_Позитивный тест на 201 статус-код")
    @Test
    public void successfulRegistrationTest_with_records() {

        //работает в связке с конструктором (класс RegistrationBodyPojoModel, строки 14-18)
        RegistrationBodyRecordsModel data = new RegistrationBodyRecordsModel(username, password);

        RegistrationResponseRecordsModel registrationResponse = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .as(RegistrationResponseRecordsModel.class);

        assertEquals(username, registrationResponse.username());
//
//                .body("username", is(username)) //вынести из теста
//                .body("id", notNullValue());  //вынести из теста
    }

    @DisplayName("record_Негативная проверка 400 статус-код: передача username = \"\" ")
    @Test
    public void negativeRegistrationEmptyUsernameTest_with_records() {

        //работает в связке с конструктором (класс RegistrationBodyPojoModel, строки 14-18)
        RegistrationBodyRecordsModel data = new RegistrationBodyRecordsModel("", password);

        RegistrationErrorResponseRecordsModel response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(400)
                .extract()
                .as(RegistrationErrorResponseRecordsModel.class);

        String expectedError = "This field may not be blank.";
        assertEquals(expectedError, response.username().getFirst());
    }

    @DisplayName("record_Негативная проверка 400 статус-код: передача username = null ")
    @Test
    public void negativeRegistrationNullUsernameTest_with_records() {

        //работает в связке с конструктором (класс RegistrationBodyPojoModel, строки 14-18)
        RegistrationBodyRecordsModel data = new RegistrationBodyRecordsModel(null, password);

        RegistrationErrorResponseRecordsModel response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(400)
                .extract()
                .as(RegistrationErrorResponseRecordsModel.class);

        String expectedError = "This field may not be null.";
        assertEquals(expectedError, response.username().getFirst());
    }

    @DisplayName("record_Негативная проверка 400 статус-код: передача password = \"\" ")
    @Test
    public void negativeRegistrationEmptyPasswordTest_with_records() {

        //работает в связке с конструктором (класс RegistrationBodyPojoModel, строки 14-18)
        RegistrationBodyRecordsModel data = new RegistrationBodyRecordsModel(username, "");

        RegistrationErrorResponseRecordsModel response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(400)
                .extract()
                .as(RegistrationErrorResponseRecordsModel.class);

        String expectedError = "This field may not be blank.";
        assertEquals(expectedError, response.password().getFirst());
    }

    @DisplayName("record_Негативная проверка 400 статус-код: передача password = null ")
    @Test
    public void negativeRegistrationNullPasswordTest_with_records() {

        //работает в связке с конструктором (класс RegistrationBodyPojoModel, строки 14-18)
        RegistrationBodyRecordsModel data = new RegistrationBodyRecordsModel(username, null);

        RegistrationErrorResponseRecordsModel response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(400)
                .extract()
                .as(RegistrationErrorResponseRecordsModel.class);

        String expectedError = "This field may not be null.";
        assertEquals(expectedError, response.password().getFirst());
    }

    @DisplayName("record_Негативная проверка 400 статус-код: передача недопустимого значения в username")
    @Test
    public void negativeRegistrationInvalidUsernameTest_with_records() {

        //работает в связке с конструктором (класс RegistrationBodyPojoModel, строки 14-18)
        RegistrationBodyRecordsModel data = new RegistrationBodyRecordsModel("///", password);

        RegistrationErrorResponseRecordsModel response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(400)
                .extract()
                .as(RegistrationErrorResponseRecordsModel.class);

        String expectedError =
                "Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.";
        assertEquals(expectedError, response.username().getFirst());
    }

    @DisplayName("Негативный тест - дублирование запроса на создание клиента (400 статус-код)")
    @Test
    public void existingUser400Test() {

        RegistrationBodyRecordsModel data = new RegistrationBodyRecordsModel(username, password);

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .body("username", is(username))
                .body("id", notNullValue());

        RegistrationErrorResponseRecordsModel response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(400)
                .extract()
                .as(RegistrationErrorResponseRecordsModel.class);

        String expectedError = "A user with that username already exists.";
        assertEquals(expectedError, response.username().getFirst());
    }

    @DisplayName("Негативный тест на 500 статус-код")
    @Test
    public void negativeRegistration500Test() {

        io.restassured.response.Response response = given()
                .log().all()
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register")
                .then()
                .log().all()
                .statusCode(502)
                .extract()
                .response();

        String htmlBody = response.getBody().asString();
        assertTrue(htmlBody.contains("502 Bad Gateway"));

    }

    //@DisplayName("Негативный тест на 400 статус-код") - отдает 502 Bad Gateway
    @Test
    public void negativeRegistration400Test() {

        String data = "{\"username\":\"" + username + "\"," +
                "\"password\": \"" + password + "\",}";

//body {
//        "username":"mariya",
//        "password":"string"
//    }
//        https://book-club.qa.guru/api/v1/users/register/}

        given()
                .log().all()
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register")
                .then()
                .log().all()
                .statusCode(400)
                .body("username", is(username))
                .body("id", notNullValue());

    }

    @DisplayName("bad_practice - Позитивный тест на 201 статус-код")
    @Test
    public void succeessfulRegistrationTest_bad_practice() {

        //убрать в model
        String data = "{\"username\":\"" + username + "\"," +
                "\"password\": \"" + password + "\"}";

    given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .body("username", is(username)) //вынести из теста
                .body("id", notNullValue());  //вынести из теста
    }
}

