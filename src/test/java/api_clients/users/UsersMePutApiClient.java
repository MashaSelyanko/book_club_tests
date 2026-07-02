package api_clients.users;

import io.qameta.allure.Step;
import models.update_user.UpdateUserBodyModel;
import models.update_user.UpdateUserResponseModel;

import static io.restassured.RestAssured.given;
import static specs.update_user.UpdateUserSpec.updateUserRequestSpec;
import static specs.update_user.UpdateUserSpec.updateUserResponseSpec;

public class UsersMePutApiClient {

    @Step("Успешное обновление пользователя (метод PUT)")
    //вторым параметром добавляем токен
    public static UpdateUserResponseModel mainRequest(UpdateUserBodyModel body, String accessToken) {
        return given(updateUserRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .put("/users/me/")
                .then()
                .spec(updateUserResponseSpec)
                .extract()
                .as(UpdateUserResponseModel.class);
    }

}
