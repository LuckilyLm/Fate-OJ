package com.fate.oj.model.dto.question;

import lombok.Data;

/**
 * @Author: Fate
 * @Date: 2024/7/1 0:30
 **/

@Data
public class JudgeConfig
{
    /**
     * 时间限制，单位为毫秒
     */
    private Long timeLimit;

    /**
     * 内存限制，单位为KB
     */
    private Long memoryLimit;

    /**
     * 栈空间限制，单位为KB
     */
    private Long stackLimit;
}
