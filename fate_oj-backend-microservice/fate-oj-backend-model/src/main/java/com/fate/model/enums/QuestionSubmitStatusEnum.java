package com.fate.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目提交状态枚举
 * @Author: Fate
 * @Date: 2024/7/1 16:52
 **/
public enum QuestionSubmitStatusEnum
{
    WAITING("等待提交",0),
    RUNNING("判题中",1),
    SUCCESS("成功",2),
    FAILED("失败",3);

    private final String message;
    private final Integer value;

    QuestionSubmitStatusEnum(String message,Integer value) {
        this.value = value;
        this.message = message;
    }

    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public static QuestionSubmitStatusEnum getEnumByValue(Integer value) {
        if(ObjectUtils.isEmpty(value)){
            return null;
        }

        for (QuestionSubmitStatusEnum item : QuestionSubmitStatusEnum.values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }
}
