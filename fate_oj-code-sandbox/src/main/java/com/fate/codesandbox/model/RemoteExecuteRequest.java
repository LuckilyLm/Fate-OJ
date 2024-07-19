package com.fate.codesandbox.model;

import lombok.Data;

/**
 * 调用api接口参数
 * @Author: Fate
 * @Date: 2024/7/16 16:52
 **/

// https://www.jdoodle.com/ 调用API接口
@Data
public class RemoteExecuteRequest {

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 客户端密钥
     */
    private String clientSecret;

    /**
     * 执行代码
     */
    private String script;

    /**
     * 输入参数
     */
    private String stdin;

    /**
     * 语言
     */
    private String language;

    /**
     * 语言版本
     */
    private String versionIndex;

    /**
     * 是否仅编译
     */
    private String compileOnly;
}
