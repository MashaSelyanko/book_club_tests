package api_clients;

import api_clients.users.UsersRegisterPostApiClient;

public class ApiClient {

    public final UsersRegisterPostApiClient auth = new UsersRegisterPostApiClient();
    public final UsersRegisterPostApiClient users = new UsersRegisterPostApiClient();
}
