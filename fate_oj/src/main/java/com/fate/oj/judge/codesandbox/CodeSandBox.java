package com.fate.oj.judge.codesandbox;

import com.fate.oj.judge.codesandbox.model.ExecuteRequest;
import com.fate.oj.judge.codesandbox.model.ExecuteResponse;

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
