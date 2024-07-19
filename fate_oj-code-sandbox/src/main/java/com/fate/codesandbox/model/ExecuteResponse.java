package com.fate.codesandbox.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Fate
 * @Date: 2024/7/2 13:09
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteResponse
{
    /**
     * 输出
     */
    private List<String> outputList;

    /**
     * 调用接口信息
     */
    private String message;

    /**
     * 执行状态
     */
    private Integer status;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;
}
