package com.fate.codesandbox.utils;

import cn.hutool.core.util.StrUtil;
import com.fate.codesandbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 进程信息处理工具类
 * @Author: Fate
 * @Date: 2024/7/3 14:59
 **/
@Slf4j
public class ProcessUtil
{
    /**
     * 运行进程
     * @param process 进程
     * @param operation 操作类型
     * @return 结果
     */
    public static ExecuteMessage runProcess(Process process,String operation){

        ExecuteMessage executeMessage = new ExecuteMessage();

        BufferedReader bufferedReader;
        String line;

        try {
            // 计算耗时
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            int exitValue = 0;
            long memoryCost = 0;
            if(StrUtil.isNotBlank(operation) && operation.equals("执行")){
                // 获取内存使用情况
                MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
                MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
                long startMemory = heapMemoryUsage.getUsed();

                // 等待进程执行完成,并获取返回值
                exitValue = process.waitFor();

                // 等待一秒,防止获取到的内存使用情况与实际使用情况有偏差
                Thread.sleep(1000);

                // 获取结束时的内存使用情况
                // 多次采样内存使用情况
                long[] memorySamples = new long[5];
                for (int i = 0; i < memorySamples.length; i++) {
                    heapMemoryUsage = memoryBean.getHeapMemoryUsage();
                    memorySamples[i] = heapMemoryUsage.getUsed();
                    Thread.sleep(50);
                }
                // 取最大值作为结束内存使用情况
                long endMemory = Arrays.stream(memorySamples).max().orElse(0);
                memoryCost = endMemory - startMemory;
            }else {
                // 等待进程执行完成,并获取返回值
                exitValue = process.waitFor();
            }

            // 设置进程退出状态
            log.info("{}执行完成,退出状态:{}", operation, exitValue);
            executeMessage.setExitCode(exitValue);
            List<String> outputList = new ArrayList<>();
            if (exitValue != 0) {
                bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            }else {
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            }
            do {
                line = bufferedReader.readLine();
                outputList.add(line);
            } while (line != null);

            if(operation.equals("编译") && exitValue == 0){
                executeMessage.setMessage("编译成功");
            }
            else {
                executeMessage.setMessage(StringUtils.join(outputList));
            }
            bufferedReader.close();

            if(operation.equals("执行")){
                executeMessage.setMemoryCost(memoryCost);
            }
            stopWatch.stop();
            executeMessage.setTimeCost(stopWatch.getLastTaskTimeMillis() - 1250);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            log.info("{}结果:{}", operation, executeMessage.getMessage().trim());
        }
        return executeMessage;
    }

    /**
     * 测试
     * 运行交互式进程
     * @param process 进程
     * @param operation 操作类型
     * @param args 参数
     * @return 结果
     */
    public static ExecuteMessage runInteractProcess(Process process,String operation,String args){

        ExecuteMessage executeMessage = new ExecuteMessage();

        BufferedReader bufferedReader;
        String line;
        StringBuilder result = new StringBuilder();


        try {
            // 向进程输入参数
            OutputStream outputStream = process.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            String[] inputArgs = args.split(" ");
            String join = StrUtil.join("\n", inputArgs) + "\n";
            outputStreamWriter.write(join);
            // 相当于回车
            outputStreamWriter.flush();

            // 获取输入的参数
            InputStream inputStream = process.getInputStream();

            // 等待进程执行完成,并获取返回值
            int exitValue = process.waitFor();

            executeMessage.setExitCode(exitValue);

            if (exitValue != 0) {
                bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            }else {

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            }
            do {
                line = bufferedReader.readLine();
                result.append(line);
            } while (line != null);

            if(operation.equals("编译") && exitValue == 0){
                executeMessage.setMessage("编译成功");
            }else {
                executeMessage.setMessage(result.toString());
            }

            outputStreamWriter.close();
            outputStream.close();
            inputStream.close();
            bufferedReader.close();
            process.destroy();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            log.info("{}结果:{}", operation, executeMessage.getMessage());
        }
        return executeMessage;
    }
}
