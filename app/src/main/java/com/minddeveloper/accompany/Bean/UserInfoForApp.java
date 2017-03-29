package com.minddeveloper.accompany.Bean;

/**
 * Created by YX on 2017/3/21.
 */
public class UserInfoForApp {
    private String id;
    private String username;
    private String nick_name;
    private String heart_coin;
    private String is_teacher;
    private String token;
    private Boolean isLogin;

    public UserInfoForApp() {
    }

    public UserInfoForApp(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public UserInfoForApp(String id, String username,String nick_name, String heart_coin, String is_teacher, String token) {
        this.id = id;
        this.username = username;
        this.nick_name = nick_name;
        this.heart_coin = heart_coin;
        this.is_teacher = is_teacher;
        this.token = token;
    }

    public UserInfoForApp(String id, String username, String token) {
        this.id = id;
        this.username = username;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHeart_coin() {
        return heart_coin;
    }

    public void setHeart_coin(String heart_coin) {
        this.heart_coin = heart_coin;
    }

    public String getIs_teacher() {
        return is_teacher;
    }

    public void setIs_teacher(String is_teacher) {
        this.is_teacher = is_teacher;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getLogin() {
        return isLogin;
    }

    public void setLogin(Boolean login) {
        isLogin = login;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }
}
