package com.fate.judge;


import com.fate.model.entity.QuestionSubmit;

/**
 * 判题服务接口
 * @Author: Fate
 * @Date: 2024/7/2 17:10
 **/
public interface JudgeService
{
    /**
     * 判题
     * @param questionSubmitId 提交题目ID
     * @return 判题结果
     */
    QuestionSubmit doJudge(long questionSubmitId);
}
