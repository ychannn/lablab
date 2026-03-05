package org.ychan.lablab.enums;

/**
 * 角色枚举
 */
public enum RoleEnum {

    /**
     * 超级管理员
     */
    ADMIN("admin", "超级管理员"),

    /**
     * 操作员
     */
    OPERATOR("operator", "操作员");

    private final String code;
    private final String name;

    RoleEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 根据代码获取角色枚举
     */
    public static RoleEnum getByCode(String code) {
        for (RoleEnum role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        return null;
    }
}