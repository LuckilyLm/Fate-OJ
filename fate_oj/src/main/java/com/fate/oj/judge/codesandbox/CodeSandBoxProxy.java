package com.fate.oj.judge.codesandbox;

import com.fate.oj.judge.codesandbox.model.ExecuteRequest;
import com.fate.oj.judge.codesandbox.model.ExecuteResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码沙箱代理类
 * @Author: Fate
 * @Date: 2024/7/2 16:49
 **/
@Slf4j
public class CodeSandBoxProxy implements CodeSandBox
{
    private final CodeSandBox codeSandBox;

    public CodeSandBoxProxy(CodeSandBox codeSandBox) {
        this.codeSandBox = codeSandBox;
    }

    /**
     * 执行代码
     * @param executeRequest 请求信息
     * @return 响应信息
     */
    @Override
    public ExecuteResponse executeCode(ExecuteRequest executeRequest) {
        log.info("代码沙箱请求信息:{}", executeRequest.toString());
        ExecuteResponse executeResponse = codeSandBox.executeCode(executeRequest);
        log.info("代码沙箱响应信息:{}", executeResponse.toString());
        return executeResponse;
    }
}
