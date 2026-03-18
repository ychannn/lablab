package org.ychan.lablab.enums;

/**
 * 学者职级枚举（职级从高到低：教授 > 副教授 > 讲师 > 助教）
 * 存储为数字 code，数值越小职级越高，便于 ORDER BY title ASC 即按职级高到低排序
 */
public enum ScholarRankEnum {

    /** 教授 */
    PROFESSOR(1, "教授"),
    /** 副教授 */
    ASSOCIATE_PROFESSOR(2, "副教授"),
    /** 讲师 */
    LECTURER(3, "讲师"),
    /** 助教 */
    TEACHING_ASSISTANT(4, "助教");

    private final int code;
    private final String label;

    ScholarRankEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 根据数字 code 获取职级枚举
     */
    public static ScholarRankEnum getByCode(int code) {
        for (ScholarRankEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根据 code 获取职级名称，未知 code 返回空字符串
     */
    public static String getLabelByCode(int code) {
        ScholarRankEnum e = getByCode(code);
        return e != null ? e.getLabel() : "";
    }
}
