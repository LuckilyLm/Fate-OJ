package com.fate.codesandbox.service.impl;

import com.fate.codesandbox.model.ExecuteRequest;
import com.fate.codesandbox.model.ExecuteResponse;
import com.fate.codesandbox.service.JavaCodeSandboxTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * java原生代码沙箱实现类
 * @Author: Fate
 * @Date: 2024/7/3 0:29
 **/

@Component
@Slf4j
public class JavaNativeCodeSandboxImpl extends JavaCodeSandboxTemplate {

    @Override
    public ExecuteResponse executeCode(ExecuteRequest executeRequest) {
        return super.executeCode(executeRequest);
    }
}
