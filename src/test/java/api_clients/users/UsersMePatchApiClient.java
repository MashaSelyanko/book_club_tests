package api_clients.users;

import io.qameta.allure.Step;
import models.DetailErrorResponseModel;
import models.update_user.PartialUpdateUserWithPatchBodyModel;
import models.update_user.UpdateUserBodyModel;
import models.update_user.UpdateUserResponseInvalidEmailModel;
import models.update_user.UpdateUserResponseModel;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static specs.update_user.UpdateUserSpec.*;
import static tests.TestBase.timeoutConfig;

public class UsersMePatchApiClient {

    @Step("Успешное полное обновление пользователя (метод PATCH)")
public static UpdateUserResponseModel mainRequestPatch(UpdateUserBodyModel body,String accessToken) {
            return given(updateUserRequestSpec)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(body)
                    .when()
                    .patch("/users/me/")
                    .then()
                    .spec(updateUserResponseSpec)
                    .extract()
                    .as(UpdateUserResponseModel.class);
    }

    @Step("Успешное частичное обновление пользователя (метод PATCH)")
    public static UpdateUserResponseModel partialPatchRequest
            (PartialUpdateUserWithPatchBodyModel body, String accessToken) {
        return given(updateUserRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .patch("/users/me/")
                .then()
                .spec(partialUpdateUserWithPatchResponseSpec)
                .extract()
                .as(UpdateUserResponseModel.class);
    }

    @Step("Неуспешное обновление пользователя (метод PATCH) - невалидный email")
    public static UpdateUserResponseInvalidEmailModel invalidPartialRequest
            (PartialUpdateUserWithPatchBodyModel body, String accessToken) {
        return given(updateUserRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .patch("/users/me/")
                .then()
                .spec(invalidPartialUpdateUserWithPatchResponseSpec)
                .extract()
                .as(UpdateUserResponseInvalidEmailModel.class);
    }

    @Step("Неуспешное обновление пользователя (метод PATCH) - без авторизации")
    public static DetailErrorResponseModel unauthorizedUpdateUserRequest
            (PartialUpdateUserWithPatchBodyModel body) {
        return given(updateUserRequestSpec)
                .config(timeoutConfig)
                .body(body)
                .when()
                .patch("/users/me/")
                .then()
                .spec(unauthorizedUpdateUserWithPatchResponseSpec)
                .extract()
                .as(DetailErrorResponseModel.class);
    }

}
