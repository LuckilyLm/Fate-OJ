package com.fate.codesandbox.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.fate.codesandbox.model.ExecuteMessage;
import com.fate.codesandbox.service.JavaCodeSandboxTemplate;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * java docker代码沙箱实现类
 * @Author: Fate
 * @Date: 2024/7/3 0:29
 **/

@Component
@Slf4j
public class JavaDockerCodeSandboxImpl extends JavaCodeSandboxTemplate {

    private final static Long OVER_TIME = 50000L;
    private final DockerClient dockerClient;
    private final String imageName = "openjdk:8-alpine";
    private String containerId;

    public JavaDockerCodeSandboxImpl() {
        this.dockerClient = DockerClientBuilder.getInstance().build();
        pullImageIfNotExists();
    }

    /**
     * 拉取镜像，如果不存在则拉取
     */
    private void pullImageIfNotExists() {
        try {
            dockerClient.inspectImageCmd(imageName).exec();
        } catch (NotFoundException e) {
            log.info("镜像{}不存在,开始拉取", imageName);
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(imageName);
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    log.info("拉取镜像进度:{}", item.getStatus());
                    super.onNext(item);
                }
            };
            try {
                pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
            } catch (InterruptedException ex) {
                log.error("拉取镜像失败:{}", ex.getMessage());
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * 创建并启动容器
     * @param userCodeParentPath 用户代码父路径
     */
    private void createAndStartContainer(String userCodeParentPath) {
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(imageName);
        HostConfig hostConfig = new HostConfig()
                .withMemory(100 * 1000 * 1000L)
                .withMemorySwap(0L)
                .withCpuCount(1L)
                .withBinds(new Bind(userCodeParentPath, new Volume("/app")))
                .withReadonlyRootfs(true);

        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(true)
                .exec();
        containerId = createContainerResponse.getId();
        dockerClient.startContainerCmd(containerId).exec();
    }

    @Override
    public List<ExecuteMessage> runCode(File userCodeFile, List<String> inputList) {
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();

        // 如果容器ID为空
        if (StrUtil.isBlank(containerId)){
            // 获取所有的容器,包括停止的
            ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
            List<Container> containerList = listContainersCmd.withShowAll(true).exec();
            if(containerList.isEmpty()){
                // 容器不存在则创建并启动
                createAndStartContainer(userCodeParentPath);
            }else {
                // 容器存在则启动,默认取第一个容器
                containerId = containerList.get(0).getId();
            }

        }

        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        long timeCost;
        final long[] maxMemoryUsage = {0L};

        for (String inputArg : inputList) {
            StopWatch stopWatch = new StopWatch();
            String[] inputArray = inputArg.split(" ");
            String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main"},inputArray);
            ExecuteMessage executeMessage = new ExecuteMessage();
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withTty(true)
                    .exec();
            log.info("容器执行命令:{}", execCreateCmdResponse.toString());
            // 获取执行命令的ID
            String execId = execCreateCmdResponse.getId();
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            ResultCallback<Statistics> statsCallback = new ResultCallback<Statistics>() {
                @Override
                public void onStart(Closeable closeable) {

                }

                @Override
                public void onNext(Statistics statistics) {
                    // 记录最大内存占用
                    long memoryUsage = Optional.ofNullable(statistics.getMemoryStats().getUsage()).orElse(0L);
                    maxMemoryUsage[0] = Math.max(maxMemoryUsage[0], memoryUsage);
                }

                @Override
                public void onError(Throwable throwable) {
                }

                @Override
                public void onComplete() {

                }

                @Override
                public void close() {

                }
            };
            statsCmd.exec(statsCallback);

            try {
                stopWatch.start();
                dockerClient.execStartCmd(execId).withDetach(false).exec(new ResultCallback.Adapter<Frame>() {

                    @Override
                    public void onStart(Closeable stream) {
                        super.onStart(stream);
                    }

                    @Override
                    public void onNext(Frame item) {
                        StreamType streamType = item.getStreamType();
                        if (StreamType.STDERR.equals(streamType)) {
                            log.error("执行错误:{}", new String(item.getPayload(), StandardCharsets.UTF_8));
                        } else {
                            log.info("执行结果:{}", new String(item.getPayload(), StandardCharsets.UTF_8));
                        }
                        executeMessage.setMessage(new String(item.getPayload(), StandardCharsets.UTF_8));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        log.error("执行命令失败:{}", throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        log.info("命令执行完成");
                        try {
                            close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void close() throws IOException {
                        super.close();
                    }
                }).awaitCompletion(OVER_TIME, TimeUnit.SECONDS);

                stopWatch.stop();
                // 获取耗时
                timeCost = stopWatch.getLastTaskTimeMillis();
                // 获取执行命令的退出状态码
                InspectExecResponse inspectExecResponse = dockerClient.inspectExecCmd(execId).exec();
                int exitCode = Integer.parseInt(inspectExecResponse.getExitCodeLong().toString());
                executeMessage.setExitCode(exitCode);
                log.info("执行命令的退出状态码:{}",exitCode);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // 填充执行时间和内存占用
            executeMessage.setTimeCost(timeCost);
            executeMessage.setMemoryCost(maxMemoryUsage[0]);
            executeMessageList.add(executeMessage);
        }

        // 停止容器
        // dockerClient.stopContainerCmd(containerId).exec();
        // dockerClient.removeContainerCmd(containerId).withForce(true).exec();
        log.info("执行完毕,停止并删除容器ID:{}", containerId);
        // 重置容器ID
        containerId = null;
        return executeMessageList;
    }
}


