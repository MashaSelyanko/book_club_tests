package models.registration;
// это нативный подход в java

//нужно добавить Jackson


import java.util.List;

public record RegistrationErrorResponseModel(
        List<String> username,
        List<String> password
) {}
