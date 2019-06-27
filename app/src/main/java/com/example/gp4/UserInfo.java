package com.example.gp4;

/* 사용자 기기 고유값, 이름, 나이, 성별 정보를 저장하기 위한 클래스 - 내부 저장소에 저장 */

public class UserInfo {
    String name;
    int age;
    boolean sex; // 남자 false 여자 true

    UserInfo( String name, int age, boolean sex){
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public boolean getSex() {
        return sex;
    }
}
