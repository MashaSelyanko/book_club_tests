//если нужны методы только get, можно вместо @Data (содержит и getter и setter) добавить @Getter

package models.registration.model_examples.lombok;

import lombok.Data;

@Data
public class RegistrationResponseLombokModel {
    Integer id;
    String username;
    String firstName;
    String lastName;
    String email;
    String remoteAddr;

}
