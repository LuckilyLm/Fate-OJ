package com.fate.codesandbox.controller;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.fate.codesandbox.model.*;
import com.fate.codesandbox.service.impl.JavaDockerCodeSandboxImpl;
import com.fate.codesandbox.service.impl.JavaNativeCodeSandboxImpl;
import com.fate.constant.CommonConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * @Author: Fate
 * @Date: 2024/7/8 13:16
 **/

@RestController
@RequestMapping("/")
public class MainController
{

    @Value("${jdoodle.clientId}")
    private String clientId;

    @Value("${jdoodle.clientSecret}")
    private String clientSecret;

    @Resource
    private JavaNativeCodeSandboxImpl javaNativeCodeSandboxImpl;

    @Resource
    private JavaDockerCodeSandboxImpl javaDockerCodeSandboxImpl;

    /**
     * 执行代码,使用本地原生
     * @param executeRequest 请求参数
     * @param request http请求
     * @param response http响应
     * @return 执行结果
     */
    @PostMapping("/executeCode/native")
    public ExecuteResponse executeCodeByNative(@RequestBody ExecuteRequest executeRequest, HttpServletRequest request
        , HttpServletResponse response){

        if(check(executeRequest,request,response)){
            response.setStatus(HttpStatus.HTTP_FORBIDDEN);
            return null;
        }
        if (executeRequest == null){
            throw new IllegalArgumentException("请求参数不能为空!");
        }
        return javaNativeCodeSandboxImpl.executeCode(executeRequest);
    }

    /**
     * 执行代码,使用docker
     * @param executeRequest 请求参数
     * @param request http请求
     * @param response http响应
     * @return 执行结果
     */
    @PostMapping("/executeCode/docker")
    public ExecuteResponse executeCodeByDocker(@RequestBody ExecuteRequest executeRequest, HttpServletRequest request
            , HttpServletResponse response){

        if(check(executeRequest,request,response)){
            response.setStatus(HttpStatus.HTTP_FORBIDDEN);
            return null;
        }
        return javaDockerCodeSandboxImpl.executeCode(executeRequest);
    }


    /**
     * 执行代码,使用远程api
     * @param executeRequest 请求参数
     * @param request http请求
     * @param response http响应
     * @return 执行结果
     * @throws IOException 异常
     */
    @PostMapping("/executeCode/remote")
    public ExecuteResponse executeCodeByRemote(@RequestBody ExecuteRequest executeRequest, HttpServletRequest request
            , HttpServletResponse response) throws IOException {
        if(check(executeRequest,request,response)){
            response.setStatus(HttpStatus.HTTP_FORBIDDEN);
            return null;
        }


        HttpResponse httpResponse = HttpUtil.createPost("https://api.jdoodle.com/v1/credit-spent")
                .body("{" +
                        " \"clientId\": \"" + "\"" + clientId + "\"," +
                        " \"clientSecret\": " + "\"" + clientSecret + "\"," +
                        "}")
                .execute();
        int status = httpResponse.getStatus();


        if(status == 429){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().println("当日请求次数已达上限,请明日再试");
            return null;
        }

        StringBuilder input = new StringBuilder(executeRequest.getInputList().get(0));
        for (int i = 1; i < executeRequest.getInputList().size(); i++){
            input.append(" ").append(executeRequest.getInputList().get(i));
        }

        RemoteExecuteRequest remoteExecuteRequest = new RemoteExecuteRequest();
        remoteExecuteRequest.setClientId(clientId);
        remoteExecuteRequest.setClientSecret(clientSecret);
        remoteExecuteRequest.setScript(executeRequest.getCode());
        remoteExecuteRequest.setStdin(input.toString());
        remoteExecuteRequest.setLanguage(executeRequest.getLanguage());
        remoteExecuteRequest.setVersionIndex("0");
        remoteExecuteRequest.setCompileOnly("false");


        String responseBody;
        HttpResponse execute = HttpUtil.createPost("https://api.jdoodle.com/v1/execute")
                .body(JSONUtil.toJsonStr(remoteExecuteRequest))
                .execute();
        responseBody = execute.body();

        RemoteExecuteResponse result = JSONUtil.toBean(responseBody, RemoteExecuteResponse.class);

        ExecuteResponse executeResponse = new ExecuteResponse();
        if(result.getError() == null){
            executeResponse.setMessage("执行成功");
        }else {
            executeResponse.setMessage(result.getError());
            executeResponse.setStatus(1);
            return executeResponse;
        }
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage("ok");
        judgeInfo.setMemory(Long.parseLong(result.getMemory()));
        double timeCost = Double.parseDouble(result.getCpuTime()) * 1000;
        judgeInfo.setTime((long)timeCost);

        String[] output = result.getOutput().split(" ");

        executeResponse.setOutputList(Arrays.asList(output));
        executeResponse.setStatus(0);
        executeResponse.setJudgeInfo(judgeInfo);

        System.err.println(JSONUtil.toJsonStr(executeResponse));
        return executeResponse;
    }


    // 校验数据和请求头
    private boolean check(ExecuteRequest executeRequest,HttpServletRequest request, HttpServletResponse response)
    {
        String auth = request.getHeader(CommonConstant.AUTH_REQUEST_HEADER);
        if (!CommonConstant.AUTH_REQUEST_SECRET.equals(auth)){
            response.setStatus(HttpStatus.HTTP_FORBIDDEN);
            return true;
        }
        if (executeRequest == null){
            throw new IllegalArgumentException("请求参数不能为空!");
        }
        return false;
    }
}
