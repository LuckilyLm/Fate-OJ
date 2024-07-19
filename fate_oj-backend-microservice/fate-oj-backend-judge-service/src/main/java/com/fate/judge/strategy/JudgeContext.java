package com.fate.judge.strategy;


import com.fate.model.dto.question.JudgeCase;
import com.fate.model.dto.questionsubmit.JudgeInfo;
import com.fate.model.entity.Question;
import com.fate.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略模式中传递的参数）
 * @Author: Fate
 * @Date: 2024/7/2 18:05
 **/

@Data
public class JudgeContext
{
    /**
     * 题目的判题信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 输入参数列表
     */
    private List<String> inputList;

    /**
     * 输出结果列表
     */
    private List<String> outputList;

    /**
     * 输入输出用例列表
     */
    private List<JudgeCase> judgeCaseList;

    /**
     * 题目实体类
     */
    private Question question;

    /**
     * 题目提交实体类
     */
    private QuestionSubmit questionSubmit;
}
