package com.fate.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目提交语言枚举
 * @Author: Fate
 * @Date: 2024/7/1 17:04
 */
public enum QuestionSubmitLanguageEnum {

    JAVA("java", "java"),
    C("c", "c"),
    CPP("c++", "c++"),
    PYTHON3("python3", "python3"),
    GOLANG("golang", "golang"),
    JAVASCRIPT("javascript", "javascript");

    private final String language;

    private final String value;

    QuestionSubmitLanguageEnum(String language, String value) {
        this.language = language;
        this.value = value;
    }

    /**
     * 获取值列表
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     */
    public static QuestionSubmitLanguageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (QuestionSubmitLanguageEnum anEnum : QuestionSubmitLanguageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getLanguage() {
        return language;
    }
}
