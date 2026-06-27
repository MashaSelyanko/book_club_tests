package specs.logout;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;
import static specs.BaseSpec.baseRequestSpec;

public class LogoutSpec {

    // спецификация для запроса
    public static RequestSpecification logoutRequestSpec = baseRequestSpec;


    //спецификация для 200 статус-кода (успешный logout)
    public static ResponseSpecification successfulLogoutResponseSpec = new ResponseSpecBuilder()
            .log(ALL)                                 //вместо .log().all()
            .expectStatusCode(200)  // вместо .statusCode(200)
            .build();

    //спецификация для 401 статус-кода (некорректный logout)
    public static ResponseSpecification invalidLogoutResponseSpec = new ResponseSpecBuilder()
            .log(ALL)                                 //вместо .log().all()
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/logout/invalid_logout_response_schema.json"))
            .expectBody("detail", notNullValue())
            .expectBody("code", notNullValue())
            .build();
}
