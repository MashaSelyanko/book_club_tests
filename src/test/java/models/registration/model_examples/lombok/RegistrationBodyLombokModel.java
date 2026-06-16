package models.registration.model_examples.lombok;


//нужно добавить к pojo @Data
// + в плагинах его добавить
// + если работа с конструктором, то добавить @AllArgsConstructor

// если создаем объекты без аргументов, то добавляем @NoArgsConstructor

import lombok.Data;

@Data
//@AllArgsConstructor
//@NoArgsConstructor
public class RegistrationBodyLombokModel {
    String username;
    String password;

    //этот подход, когда много разных параметров передается
    public RegistrationBodyLombokModel() {}

    //этот подход хорош, если параметров не более 4х
    public RegistrationBodyLombokModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "{\"username\": \"" + this.username + "\",\"password\": \"" + this.password + "\"}";
    }
}
