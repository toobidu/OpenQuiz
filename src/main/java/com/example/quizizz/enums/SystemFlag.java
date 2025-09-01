package com.example.quizizz.enums;

public enum SystemFlag {
    /**
     * Đối tượng thông thường.
     */
    NORMAL("0"),
    /**
     * Đối tượng hệ thống.
     */
    SYSTEM("1");

    /**
     * Giá trị trạng thái hệ thống.
     */
    private final String value;

    /**
     * Constructor để gán giá trị cho trạng thái hệ thống.
     * @param value Giá trị trạng thái.
     */
    SystemFlag(String value) {
        this.value = value;
    }

    /**
     * Lấy giá trị trạng thái hệ thống.
     * @return Giá trị trạng thái.
     */
    public String getValue() {
        return value;
    }

}
