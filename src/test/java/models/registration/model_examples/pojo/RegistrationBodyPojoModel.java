package models.registration.model_examples.pojo;


//нативный, простой java-объект

public class RegistrationBodyPojoModel {
    String username;
    String password;

    //этот подход, когда много разных параметров передается
    public RegistrationBodyPojoModel() {}

    //этот подход хорош, если параметров не более 4х
    public RegistrationBodyPojoModel(String username,String password) {
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
