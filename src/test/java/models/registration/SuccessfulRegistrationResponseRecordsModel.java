package models.registration;
// это нативный подход в java

//нужно добавить Jackson


public record SuccessfulRegistrationResponseRecordsModel(Integer id, String username, String firstName,
                                                         String lastName, String email, String remoteAddr) {}
