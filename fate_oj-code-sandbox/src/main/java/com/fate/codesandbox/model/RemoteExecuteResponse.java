package com.fate.codesandbox.model;

import lombok.Data;

/**
 * @Author: Fate
 * @Date: 2024/7/16 17:02
 **/

@Data
public class RemoteExecuteResponse {

    /**
     * 代码执行结果
     */
    private String output;

    /**
     * 代码执行错误信息
     */
    private String error;

    /**
     * HTTP状态码
     */
    private int statusCode;

    /**
     * 执行内存消耗
     */
    private String memory;

    /**
     * 执行时间
     */
    private String cpuTime;

    /**
     * 编译状态
     */
    private String compilationStatus;

    /**
     * 项目key
     */
    private String projectKey;

    /**
     * 是否执行成功
     */
    private boolean isExecutionSuccess;

    /**
     * 是否编译成功
     */
    private boolean isCompiled;
}