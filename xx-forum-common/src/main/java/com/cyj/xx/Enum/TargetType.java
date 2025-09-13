package com.cyj.xx.Enum;



// TargetType.java

public enum TargetType {
    POST("post", "帖子"),
    USER("user", "用户");

    private final String code;
    private final String description;

    TargetType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TargetType fromCode(String code) {
        for (TargetType type : TargetType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}