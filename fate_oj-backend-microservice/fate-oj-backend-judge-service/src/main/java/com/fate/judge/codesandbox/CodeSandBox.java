package com.fate.judge.codesandbox;


import com.fate.model.codesandbox.ExecuteRequest;
import com.fate.model.codesandbox.ExecuteResponse;

/**
 * 代码沙箱接口
 * @Author: Fate
 * @Date: 2024/7/2 13:01
 **/
public interface CodeSandBox
{
    /**
     * 执行代码
     * @param executeRequest 请求参数
     * @return 执行结果
     */
    ExecuteResponse executeCode(ExecuteRequest executeRequest);
}
