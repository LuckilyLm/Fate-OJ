package com.fate.codesandbox.model;

import lombok.Data;

/**
 * 进程执行结果信息
 * @Author: Fate
 * @Date: 2024/7/3 15:00
 **/

@Data
public class ExecuteMessage
{
    /**
     * 执行状态
     */
    private Integer exitCode;

    /**
     * 执行信息
     */
    private String message;

    /**
     * 执行耗时
     */
    private Long timeCost;

    /**
     * 执行内存消耗
     */
    private Long memoryCost;
}
