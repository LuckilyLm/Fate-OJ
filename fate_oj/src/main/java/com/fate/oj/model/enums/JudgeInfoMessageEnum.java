package com.fate.oj.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 判题信息枚举
 * @Author: Fate
 * @Date: 2024/7/1 17:21
 */
public enum JudgeInfoMessageEnum {

    ACCEPTED("Accepted","成功"),
    COMPILE_ERROR("Compile Error","编译错误"),
    TIME_LIMIT_EXCEEDED("Time Limit Exceeded","超时"),
    MEMORY_LIMIT_EXCEEDED("Memory Limit Exceeded","内存溢出"),
    WRONG_ANSWER("Wrong Answer","错误答案"),
    PRESENTATION_ERROR("Presentation Error","格式错误"),
    RUNTIME_ERROR("Runtime Error","运行错误"),
    SYSTEM_ERROR("System Error","系统错误"),
    DANGEROUS_OPERATION("Dangerous Operation","危险操作"),
    OUTPUT_LIMIT_EXCEEDED("Output Limit Exceeded","输出超出限制");

    private final String text;

    private final String value;

    JudgeInfoMessageEnum(String text, String value) {
        this.text = text;
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
    public static JudgeInfoMessageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (JudgeInfoMessageEnum anEnum : JudgeInfoMessageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
