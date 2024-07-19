package com.fate.codesandbox.service;

import cn.hutool.core.io.FileUtil;
import com.fate.codesandbox.model.ExecuteMessage;
import com.fate.codesandbox.model.ExecuteRequest;
import com.fate.codesandbox.model.ExecuteResponse;
import com.fate.codesandbox.model.JudgeInfo;
import com.fate.codesandbox.utils.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 模板模式：Java代码沙箱抽象类
 * 默认实现了原生的Java代码的编译、执行、结果获取、清理等功能
 * @Author: Fate
 * @Date: 2024/7/4 23:32
 **/

@Slf4j
public abstract class JavaCodeSandboxTemplate implements CodeSandBox{
    private final static String TMP_CODE_PATH = "tmpCode";

    private final static String DEFAULT_CLASS_NAME = "Main.java";

    private final static Long LIMIT_TIME = 5000L;

    @Override
    public ExecuteResponse executeCode(ExecuteRequest executeRequest) {

        List<String> inputList = executeRequest.getInputList();
        String code = executeRequest.getCode();

        // 1. 保存用户提交的代码到文件中
        File userCodeFile = saveCodeToFile(code);

        // 2. 编译代码
        ExecuteMessage compileResult = compileCode(userCodeFile);

        List<ExecuteMessage> executeMessageList;
        // 3. 如果编译成功，则执行代码,否则直接返回编译结果
        if(compileResult.getExitCode() == 0){
            executeMessageList = runCode(userCodeFile, inputList);
        }else {
            // 编译失败，返回编译结果
            ExecuteResponse executeResponse = new ExecuteResponse();
            executeResponse.setMessage(compileResult.getMessage());
            executeResponse.setStatus(compileResult.getExitCode());
            JudgeInfo judgeInfo = new JudgeInfo();
            judgeInfo.setMessage(compileResult.getMessage());
            judgeInfo.setMemory(0L);
            judgeInfo.setTime(0L);
            executeResponse.setJudgeInfo(judgeInfo);
            // 清空暂存文件
            clearTmpFile(userCodeFile);
            return executeResponse;
        }

        // 4. 获取执行结果
        ExecuteResponse executeResponse = getExecuteResponse(executeMessageList);


        // 5. 清理临时文件
        if(clearTmpFile(userCodeFile)){
            log.info("清理临时文件成功");
        }else{
            log.error("清理临时文件失败");
        }

        // 返回执行结果
        return executeResponse;
    }


    /**
     * 保存用户提交的代码到文件中
     * @param code 用户提交的代码
     * @return 文件对象
     */
    public File saveCodeToFile(String code){
        // 获取当前项目路径
        String property = System.getProperty("user.dir");

        // 临时代码存放路径
        String tmpCodePath = property + File.separator + TMP_CODE_PATH;

        // 如果临时代码存放路径不存在，则创建
        if(!FileUtil.exist(tmpCodePath)){
            FileUtil.mkdir(tmpCodePath);
        }

        // 用户提交代码存放路径
        String userCodeParentPath = tmpCodePath + File.separator + UUID.randomUUID();

        // 将用户提交的代码写入到默认的类名文件中
        return FileUtil.writeString(code, userCodeParentPath + File.separator + DEFAULT_CLASS_NAME, StandardCharsets.UTF_8);
    }


    /**
     * 编译代码
     * @param userCodeFile 用户代码文件
     * @return 编译结果信息
     */
    public ExecuteMessage compileCode(File userCodeFile){

        // 编译代码
        try {
            // 编译代码命令
            String compileCmd = String.format("javac -encoding UTF-8 %s", userCodeFile.getAbsolutePath());
            // 执行编译命令
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage codeCompileMessage = ProcessUtil.runProcess(compileProcess, "编译");
            if(codeCompileMessage.getExitCode() != 0){
                codeCompileMessage.setMessage("编译错误");
            }
            return codeCompileMessage;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行代码
     * @param userCodeFile 编译后的用户代码文件
     * @param inputList 输入参数列表
     * @return 执行结果信息列表
     */
    public List<ExecuteMessage> runCode(File userCodeFile, List<String> inputList){
        List<ExecuteMessage> executeMessageList = new ArrayList<>();

        // 获取用户编译后的代码的父路径
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();

        for (String inputArg : inputList) {
            // 执行代码命令
            // 简单在jvm层面限制堆内存大小 -Xmx256m
            String executeCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s %s %s",userCodeParentPath, "Main",inputArg);
            try {
                Process executeProcess = Runtime.getRuntime().exec(executeCmd);

                // 简单超时控制
                new Thread(() -> {
                    try {
                        Thread.sleep(LIMIT_TIME);
                        executeProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                ExecuteMessage executeMessage = ProcessUtil.runProcess(executeProcess, "执行");
                // 收集执行结果
                executeMessageList.add(executeMessage);
            } catch (IOException e) {
                throw new RuntimeException("执行代码失败", e);
            }
        }
        return executeMessageList;
    }


    /**
     * 获取执行结果
     * @param executeMessageList 执行代码信息列表
     * @return 执行结果
     */
    public ExecuteResponse getExecuteResponse(List<ExecuteMessage> executeMessageList){
        if(CollectionUtils.isEmpty(executeMessageList)){
            throw new IllegalArgumentException("执行结果列表为空");
        }

        // 设置代码执行结果
        ExecuteResponse executeResponse = new ExecuteResponse();
        // 执行代码用例输出列表
        List<String> outputList = new ArrayList<>();
        long maxTimeCost = 0;
        long maxMemoryCost = 0;
        for(ExecuteMessage executeMessage : executeMessageList){
            if(executeMessage == null){
                continue;
            }
            // 如果代码运行用例中有错误
            if(executeMessage.getExitCode() != 0){
                executeResponse.setMessage(executeMessage.getMessage());
                // 设置代码执行状态为失败
                executeResponse.setStatus(executeMessage.getExitCode());
                break;
            }
            if(executeMessage.getTimeCost() != null){
                maxTimeCost = Math.max(maxTimeCost, executeMessage.getTimeCost());
            }

            if(executeMessage.getMemoryCost() != null){
                maxMemoryCost = Math.max(maxMemoryCost, executeMessage.getMemoryCost());
            }
            outputList.add(executeMessage.getMessage());
        }

        // 如果代码运行用例中没有错误
        if(executeMessageList.size() == outputList.size()){
            // 设置代码执行状态为成功
            executeResponse.setStatus(0);
        }
        executeResponse.setOutputList(outputList);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage("代码运行成功");

        // 设置代码执行用时和内存占用
        // 将字节转换为KB
        judgeInfo.setMemory(maxMemoryCost / 1024);
        judgeInfo.setTime(maxTimeCost);
        executeResponse.setJudgeInfo(judgeInfo);

        return executeResponse;
    }

    /**
     * 清理临时文件
     * @param tmpCodeFile 临时代码文件
     * @return 是否清理成功
     */
    public boolean clearTmpFile(File tmpCodeFile){
        if(tmpCodeFile.getParentFile().exists()){
            String userCodeParentPath = tmpCodeFile.getParentFile().getAbsolutePath();
            return FileUtil.del(userCodeParentPath);
        }
        return true;
    }

}
