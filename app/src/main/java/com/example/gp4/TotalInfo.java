package com.example.gp4;

import java.io.Serializable;

public class TotalInfo implements Serializable {
    private String name;
    private CatridgeInfo catridgeInfo1;
    private CatridgeInfo catridgeInfo2;
    private CatridgeInfo catridgeInfo3;
    private CatridgeInfo catridgeInfo4;
    private CatridgeInfo catridgeInfo5;
    private CatridgeInfo catridgeInfo6;

    public TotalInfo(){}

    public TotalInfo(String name, CatridgeInfo catridgeInfo1, CatridgeInfo catridgeInfo2, CatridgeInfo catridgeInfo3,
                     CatridgeInfo catridgeInfo4, CatridgeInfo catridgeInfo5, CatridgeInfo catridgeInfo6){
        this.name = name;
        this.catridgeInfo1 = catridgeInfo1;
        this.catridgeInfo2 = catridgeInfo2;
        this.catridgeInfo3 = catridgeInfo3;
        this.catridgeInfo4 = catridgeInfo4;
        this.catridgeInfo5 = catridgeInfo5;
        this.catridgeInfo6 = catridgeInfo6;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCatridgeInfo1(CatridgeInfo catridgeInfo1) {
        this.catridgeInfo1 = catridgeInfo1;
    }

    public void setCatridgeInfo2(CatridgeInfo catridgeInfo2) {
        this.catridgeInfo2 = catridgeInfo2;
    }

    public void setCatridgeInfo3(CatridgeInfo catridgeInfo3) {
        this.catridgeInfo3 = catridgeInfo3;
    }

    public void setCatridgeInfo4(CatridgeInfo catridgeInfo4) {
        this.catridgeInfo4 = catridgeInfo4;
    }

    public void setCatridgeInfo5(CatridgeInfo catridgeInfo5) {
        this.catridgeInfo5 = catridgeInfo5;
    }

    public void setCatridgeInfo6(CatridgeInfo catridgeInfo6) {
        this.catridgeInfo6 = catridgeInfo6;
    }

    public String getName() {
        return name;
    }

    public CatridgeInfo getCatridgeInfo1() {
        return catridgeInfo1;
    }

    public CatridgeInfo getCatridgeInfo2() {
        return catridgeInfo2;
    }

    public CatridgeInfo getCatridgeInfo3() {
        return catridgeInfo3;
    }

    public CatridgeInfo getCatridgeInfo4() {
        return catridgeInfo4;
    }

    public CatridgeInfo getCatridgeInfo5() {
        return catridgeInfo5;
    }

    public CatridgeInfo getCatridgeInfo6() {
        return catridgeInfo6;
    }
}
