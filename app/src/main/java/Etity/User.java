package Etity;

import java.io.Serializable;

public class User implements Serializable {
    private String user_name;
    private String user_pwd;
    private String birth;
    private String what_up;

    public String getWhat_up() {
        return what_up;
    }

    public void setWhat_up(String what_up) {
        this.what_up = what_up;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public enum Gender{
        BOY,
        GIRL,
        SECRET
    }
    private Gender sex;

    public Gender getSex() {
        return sex;
    }

    public void setSex(Gender sex) {
        this.sex = sex;
    }

    public User() {
    }

    public String getUser_name() {
        return user_name;
    }
    public String getUser_pwd() {
        return user_pwd;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_pwd(String user_pwd) {
        this.user_pwd = user_pwd;
    }
}
