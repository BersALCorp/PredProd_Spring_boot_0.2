package web.models;

import web.models.enums.RoleType;

public class UserDto {
    private String login;
    private String password;
    RoleType role;

    public UserDto(){}
    public UserDto(String login, String password, RoleType role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }
}
