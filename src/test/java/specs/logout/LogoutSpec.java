package specs.logout;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;

public class LogoutSpec {

    // спецификация для запроса
    public static RequestSpecification logoutRequestSpec = with()
            .log().all()
            .contentType(ContentType.JSON)
            .basePath("/api/v1"); //для проверки версионности отдельно указываем

    //спецификация для ответа для теста: 200 статус-код (успешный logout)
    public static ResponseSpecification successfulLogoutResponseSpec = new ResponseSpecBuilder()
            .log(ALL)                                 //вместо .log().all()
            .expectStatusCode(200)  // вместо .statusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/logout/successful_logout_response_schema.json"))
            .expectBody("refresh", notNullValue())
            .build();
}

//        //спецификация для ответа для теста: 401 статус-код (некорректный password)
//    public static ResponseSpecification wrongPasswordLoginResponseSpec = new ResponseSpecBuilder()
//            .log(ALL)                                 //вместо .log().all()
//            .expectStatusCode(401)  // вместо .statusCode(200)
//            .expectBody(matchesJsonSchemaInClasspath(
//                    "schemas/logout/successful_logout_response_schema.json"))
//            .expectBody("detail", notNullValue())
//            .build();
//
////спецификация для ответа для теста: 401 статус-код (некорректный username)
//
//public static ResponseSpecification wrongUsernameLoginResponseSpec = new ResponseSpecBuilder()
//        .log(ALL)                                 //вместо .log().all()
//        .expectStatusCode(401)  // вместо .statusCode(200)
//        .expectBody(matchesJsonSchemaInClasspath(
//                "schemas/login/wrong_credentials_login_response_schema.json"))
//        .expectBody("detail", notNullValue())
//        .build();
//
////спецификация для ответа для теста: 400 статус-код (пустой username)
//
//    public static ResponseSpecification emptyUsernameLoginResponseSpec = new ResponseSpecBuilder()
//            .log(ALL)                                 //вместо .log().all()
//            .expectStatusCode(400)  // вместо .statusCode(200)
//            .expectBody(matchesJsonSchemaInClasspath(
//                    "schemas/login/empty_login_response_schema.json"))
//            .expectBody("username", notNullValue())
//            .build();
//
////спецификация для ответа для теста: 400 статус-код (пустой password)
//
//    public static ResponseSpecification emptyPasswordLoginResponseSpec = new ResponseSpecBuilder()
//            .log(ALL)                                 //вместо .log().all()
//            .expectStatusCode(400)  // вместо .statusCode(200)
//            .expectBody(matchesJsonSchemaInClasspath(
//                    "schemas/login/empty_password_response_schema.json"))
//            .expectBody("password", notNullValue())
//            .build();
//
//    //спецификация для ответа для теста: 400 статус-код (username = null)
//
//    public static ResponseSpecification nullUsernameLoginResponseSpec = new ResponseSpecBuilder()
//            .log(ALL)                                 //вместо .log().all()
//            .expectStatusCode(400)  // вместо .statusCode(200)
//            .expectBody(matchesJsonSchemaInClasspath(
//                    "schemas/login/empty_login_response_schema.json"))
//            .expectBody("username", notNullValue())
//            .build();
//
//    //спецификация для ответа для теста: 400 статус-код (username = null)
//
//    public static ResponseSpecification emptyJSONLoginResponseSpec = new ResponseSpecBuilder()
//            .log(ALL)                                 //вместо .log().all()
//            .expectStatusCode(400)  // вместо .statusCode(200)
//            .build();
//}






