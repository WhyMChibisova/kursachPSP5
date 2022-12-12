package com.example.kursach.models;

import java.io.Serializable;

public enum Operations implements Serializable {
    AUTHORIZATION,
    REGISTRATION,
    RESTORE_USER,
    VIEW_USERS,
    ADD_NEW_USER,
    DELETE_USER,
    EDIT_LOGIN_USER,
    EDIT_USER,
    VIEW_PRODUCTS,
    ADD_NEW_PRODUCT,
    EDIT_PRODUCT,
    DELETE_PRODUCT,
    VIEW_CATEGORIES,
    VIEW_SALES,
    EDIT_SALE,
    DELETE_SALE,
    ADD_NEW_SALE,
    SORT,
    SEARCH,
    PIE_CHART_DATA,
    BAR_CHART_DATA,
    LINE_CHART_DATA,
    SAVE_FILE,
    EXIT,
    OK,
    ERROR_PASS,
    ERROR_USER,
    ERROR,
}
