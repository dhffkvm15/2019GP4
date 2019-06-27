package com.example.gp4;

/* 사용자가 가지고 있는 카트리지 정보 (향 종류, 잔량)을 저장하기 위한 클래스 - 파이어베이스에 저장 */
public class CatridgeInfo {
    String name; // 향 종류
    int rest; // 잔량 - 단위 %

    CatridgeInfo() {}
    CatridgeInfo(String name, int rest){ this.name = name; this.rest = rest; }

    public void setName(String name){ this.name = name; }
    public String getName() { return name; }

    public void setRest(int rest) { this.rest = rest; }
    public int getRest() { return rest;  }
}
