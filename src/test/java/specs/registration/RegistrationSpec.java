package specs.registration;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;

public class RegistrationSpec {

    // спецификация для запроса
    public static RequestSpecification RegistrationRequestSpec = with()
            .log().all()
            .contentType(ContentType.JSON)
            .basePath("/api/v1"); //для проверки версионности отдельно указываем

    // спецификация для ответа для теста: 200 статус-код при получении токена
    public static ResponseSpecification successfulRegistrationResponseSpec = new ResponseSpecBuilder()
            .log(ALL)                                 //вместо .log().all()
            .expectStatusCode(201)  // вместо .statusCode(201)
            .expectBody(matchesJsonSchemaInClasspath(   //спец.метод, сравнивает со схемой
                    "schemas/registration/successful_registration_response_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("username", notNullValue())
            .expectBody("remoteAddr", notNullValue())
            .build();


    //    спецификация для ответа для теста: 400 статус-код (дублирование при создании клиента)
    public static ResponseSpecification wrongRegistrationResponseSpec = new ResponseSpecBuilder()
            .log(ALL)                                 //вместо .log().all()
            .expectStatusCode(400)  // вместо .statusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/registration/existing_user_registration_response_schema.json"))
            .expectBody("username", notNullValue())
            .build();

// спецификация для ответа для теста: 401 статус-код (username более 150 символов)

    public static ResponseSpecification exceedingMaxLengthUsernameRegistrationResponseSpec = new ResponseSpecBuilder()
            .log(ALL)                                 //вместо .log().all()
            .expectStatusCode(400)  // вместо .statusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/registration/username_length_error_registration_response_schema.json"))
            .expectBody("username", notNullValue())
            .build();

    //спецификация для ответа для теста: 400 статус-код (password более 128 символов)

    public static ResponseSpecification exceedingMaxLengthPasswordRegistrationResponseSpec = new ResponseSpecBuilder()
            .log(ALL)                                 //вместо .log().all()
            .expectStatusCode(400)  // вместо .statusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/registration/password_length_error_registration_response_schema.json"))
            .expectBody("password", notNullValue())
            .build();

}






