package com.fate.model.dto.questionsubmit;

import lombok.Data;

/**
 * @Author: Fate
 * @Date: 2024/7/1 0:33
 **/

@Data
public class JudgeInfo
{
    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 消耗内存
     */
    private Long memory;

    /**
     * 程序执行时间
     */
    private Long time;
}
