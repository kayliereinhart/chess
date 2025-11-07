package model;

public record UserData(String username, String password, String email) {

    public UserData replacePassword(String password) {
        return new UserData(username, password, email);
    }
}
