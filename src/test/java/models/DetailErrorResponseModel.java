package models;

// модель десереализует JSON-ответ от сервера, происходит парсинг

//этот подход, когда много разных параметров передается
public record DetailErrorResponseModel(String detail) {}