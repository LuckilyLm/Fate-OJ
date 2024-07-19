package com.fate.oj.judge.strategy.impl;

import cn.hutool.json.JSONUtil;
import com.fate.oj.judge.strategy.JudgeContext;
import com.fate.oj.judge.strategy.JudgeStrategy;
import com.fate.oj.model.dto.question.JudgeCase;
import com.fate.oj.model.dto.question.JudgeConfig;
import com.fate.oj.model.dto.questionsubmit.JudgeInfo;
import com.fate.oj.model.entity.Question;
import com.fate.oj.model.enums.JudgeInfoMessageEnum;

import java.util.List;
import java.util.Optional;

/**
 * 默认的判题策略
 * @Author: Fate
 * @Date: 2024/7/2 18:09
 **/
public class JavaJudgeStrategy implements JudgeStrategy
{
    /**
     * 执行判题
     * @param judgeContext 判题所需的上下文信息
     * @return 判题结果
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {

        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Question question = judgeContext.getQuestion();

        // 获取执行代码的返回结果信息
        Long memory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
        Long time = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);

        // 设置判题结果信息, 默认为 ACCEPTED
        JudgeInfo judgeInfoResult = new JudgeInfo();
        judgeInfoResult.setMessage(JudgeInfoMessageEnum.ACCEPTED.getValue());
        judgeInfoResult.setMemory(memory);
        judgeInfoResult.setTime(time);

        // 判断输出是否与测试输出用例一致
        if(outputList.size() != inputList.size()){;
            judgeInfoResult.setMessage(JudgeInfoMessageEnum.WRONG_ANSWER.getValue());
            return judgeInfoResult;
        }

        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if(!judgeCase.getOutput().equals(outputList.get(i))){
                judgeInfoResult.setMessage(JudgeInfoMessageEnum.WRONG_ANSWER.getValue());
                return judgeInfoResult;
            }
        }

        // 获取题目的判题配置, 判断题目限制信息
        String judgeConfig = question.getJudgeConfig();
        JudgeConfig questionJudgeConfig = JSONUtil.toBean(judgeConfig, JudgeConfig.class);
        // 是否超出内存限制
        if(memory > questionJudgeConfig.getMemoryLimit()){
            judgeInfoResult.setMessage(JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED.getValue());
            return judgeInfoResult;
        }
        // 是否超出时间限制
        long JAVA_TIME_LIMIT = 10000L;
        if((time - JAVA_TIME_LIMIT) > questionJudgeConfig.getTimeLimit()){
            judgeInfoResult.setMessage(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getValue());
            return judgeInfoResult;
        }

        // 返回判题结果
        return judgeInfoResult;
    }
}
