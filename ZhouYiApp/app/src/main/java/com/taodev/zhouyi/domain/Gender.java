package com.taodev.zhouyi.domain;

public enum Gender {
    MALE("乾造"),
    FEMALE("坤造");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
