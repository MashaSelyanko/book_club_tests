package specs.login;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;
import static specs.BaseSpec.baseRequestSpec;

public class LoginSpec {

    public static RequestSpecification loginRequestSpec = baseRequestSpec;

    //спецификация для ответа для теста: 200 статус-код при получении токена
    public static ResponseSpecification successfulLoginResponseSpec = new ResponseSpecBuilder()
            .log(ALL)                                 //вместо .log().all()
            .expectStatusCode(200)  // вместо .statusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/login/successful_login_response_schema.json"))
            .expectBody("access", notNullValue())
            .expectBody("refresh", notNullValue())
            .build();

    //спецификация для ответа для теста: 401 статус-код (некорректный password)
    public static ResponseSpecification wrongPasswordLoginResponseSpec = new ResponseSpecBuilder()
            .log(ALL)                                 //вместо .log().all()
            .expectStatusCode(401)  // вместо .statusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/login/wrong_credentials_login_response_schema.json"))
            .expectBody("detail", notNullValue())
            .build();

//спецификация для ответа для теста: 401 статус-код (некорректный username)

    public static ResponseSpecification wrongUsernameLoginResponseSpec = new ResponseSpecBuilder()
            .log(ALL)                                 //вместо .log().all()
            .expectStatusCode(401)  // вместо .statusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/login/wrong_credentials_login_response_schema.json"))
            .expectBody("detail", notNullValue())
            .build();

//спецификация для ответа для теста: 400 статус-код (пустой username)

    public static ResponseSpecification emptyUsernameLoginResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/login/empty_login_response_schema.json"))
            .expectBody("username", notNullValue())
            .build();

//спецификация для ответа для теста: 400 статус-код (пустой password)

    public static ResponseSpecification emptyPasswordLoginResponseSpec = new ResponseSpecBuilder()
            .log(ALL)                                 //вместо .log().all()
            .expectStatusCode(400)  // вместо .statusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/login/empty_password_response_schema.json"))
            .expectBody("password", notNullValue())
            .build();

    //спецификация для ответа для теста: 400 статус-код (username = null)

    public static ResponseSpecification nullUsernameLoginResponseSpec = new ResponseSpecBuilder()
            .log(ALL)                                 //вместо .log().all()
            .expectStatusCode(400)  // вместо .statusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/login/empty_login_response_schema.json"))
            .expectBody("username", notNullValue())
            .build();

    //спецификация для ответа для теста: 400 статус-код (username = null)

    public static ResponseSpecification emptyJSONLoginResponseSpec = new ResponseSpecBuilder()
            .log(ALL)                                 //вместо .log().all()
            .expectStatusCode(400)  // вместо .statusCode(200)
            .build();
}






