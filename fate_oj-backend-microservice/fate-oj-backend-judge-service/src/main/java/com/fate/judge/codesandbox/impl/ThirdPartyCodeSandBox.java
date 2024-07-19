package com.fate.judge.codesandbox.impl;


import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.fate.common.ErrorCode;
import com.fate.exception.BusinessException;
import com.fate.judge.codesandbox.CodeSandBox;
import com.fate.model.codesandbox.ExecuteRequest;
import com.fate.model.codesandbox.ExecuteResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 第三方代码沙箱实现类（调用第三方代码沙箱）
 * @Author: Fate
 * @Date: 2024/7/2 13:21
 **/

@Slf4j
public class ThirdPartyCodeSandBox implements CodeSandBox {
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "fate";

    @Override
    public ExecuteResponse executeCode(ExecuteRequest executeRequest) {
        log.info("---调用第三方代码沙箱执行代码---");
        // String url = "http://localhost:8090/executeCode/remote";
        String url = "http://192.168.211.130:8090/executeCode/remote";
        String json = JSONUtil.toJsonStr(executeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();

        if(StringUtils.isBlank(responseStr)){
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR,"远程调用接口失败!" + responseStr);
        }
        return JSONUtil.toBean(responseStr, ExecuteResponse.class);
    }
}
