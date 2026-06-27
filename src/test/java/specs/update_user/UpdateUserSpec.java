package specs.update_user;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;
import static specs.BaseSpec.baseRequestSpec;

public class UpdateUserSpec {

    public static RequestSpecification updateUserRequestSpec = baseRequestSpec;

    // спецификация для ответа для теста: 200 статус-код (update клиента)
    public static ResponseSpecification updateUserResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/updateUser/success_update_user_response_schema.json"))
            .expectBody("username", notNullValue())
            .expectBody("firstName", notNullValue())
            .expectBody("lastName", notNullValue())
            .expectBody("email", notNullValue())
            .expectBody("remoteAddr", notNullValue())
            .build();

    public static ResponseSpecification partialUpdateUserWithPatchResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/updateUser/success_partial_update_witch_patch_user.json"))
            .expectBody("username", notNullValue())
            .expectBody("email", notNullValue())
            .build();

    public static ResponseSpecification invalidPartialUpdateUserWithPatchResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/updateUser/invalid_partial_update_witch_patch_user.json"))
            .expectBody("email", notNullValue())
            .build();

    public static ResponseSpecification unauthorizedUpdateUserWithPatchResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/updateUser/unauthorized_update_witch_patch_user.json"))
            .expectBody("detail", notNullValue())
            .build();

    public static ResponseSpecification unauthorizedUpdateUserWithPutResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/updateUser/unauthorized_update_witch_patch_user.json"))
            .expectBody("detail", notNullValue())
            .build();
}






