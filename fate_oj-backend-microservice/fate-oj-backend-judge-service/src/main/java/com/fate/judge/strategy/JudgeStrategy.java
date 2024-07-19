package com.fate.judge.strategy;


import com.fate.model.dto.questionsubmit.JudgeInfo;

/**
 * 判题策略
 * @Author: Fate
 * @Date: 2024/7/2 18:04
 **/
public interface JudgeStrategy
{
    /**
     * 判题
     * @param judgeContext 判题所需的上下文信息
     * @return 判题信息
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
