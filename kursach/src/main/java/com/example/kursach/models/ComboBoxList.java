package com.example.kursach.models;

import java.io.Serializable;

public class ComboBoxList implements Serializable {
    String tableName;
    String fieldName;
    String value1;
    String value2;

    public ComboBoxList() {
    }

    public ComboBoxList(String tableName, String fieldName, String value1, String value2) {
        this.tableName = tableName;
        this.fieldName = fieldName;
        this.value1 = value1;
        this.value2 = value2;
    }

    public ComboBoxList(String tableName, String fieldName, String value1) {
        this.tableName = tableName;
        this.fieldName = fieldName;
        this.value1 = value1;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }
}
