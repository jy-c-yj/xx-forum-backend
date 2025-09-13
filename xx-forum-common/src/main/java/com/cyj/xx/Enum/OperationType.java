package com.cyj.xx.Enum;

// OperationType.java
public enum OperationType {
    BAN("ban", "封禁"),
    UNBAN("unban", "解封"),
    PIN("pin", "置顶"),
    UNPIN("unpin", "取消置顶");

    private final String code;
    private final String description;

    OperationType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static OperationType fromCode(String code) {
        for (OperationType type : OperationType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
