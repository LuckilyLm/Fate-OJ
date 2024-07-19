package com.fate.model.dto.question;

import lombok.Data;

/**
 * @Author: Fate
 * @Date: 2024/7/1 0:28
 **/

@Data
public class JudgeCase
{
    /**
     * 输入用例
     */
    private String input;

    /**
     * 输出用例
     */
    private String output;
}
