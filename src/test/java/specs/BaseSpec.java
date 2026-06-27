package specs;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.with;
import static io.restassured.http.ContentType.JSON;


public class BaseSpec {

    public static RequestSpecification baseRequestSpec = with()
            .filter(new AllureRestAssured())
            .log().all()
            .contentType(JSON)
            .basePath("/api/v1");
}