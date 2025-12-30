package com.taodev.zhouyi.domain;

public class Pillar {
    //天干
    private String stem;
    //地支
    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    private String branch;

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public Pillar(String stem, String branch) {
        this.stem = stem;
        this.branch = branch;
    }
}
