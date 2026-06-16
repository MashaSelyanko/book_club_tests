package models.registration.model_examples.records;
// это нативный подход в java

//нужно добавить Jackson


import java.util.List;

public record RegistrationErrorResponseRecordsModel(
        List<String> username,
        List<String> password
) {}
