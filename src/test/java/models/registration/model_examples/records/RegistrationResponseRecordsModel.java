package models.registration.model_examples.records;
// это нативный подход в java

//нужно добавить Jackson


public record RegistrationResponseRecordsModel(Integer id, String username, String firstName,
                                               String lastName, String email, String remoteAddr) {}
