package models.login;


//этот подход, когда много разных параметров передается
public record LoginBodyModel(String username, String password) {}