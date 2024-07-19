package com.fate.oj.judge.strategy;

import com.fate.oj.model.dto.question.JudgeCase;
import com.fate.oj.model.dto.questionsubmit.JudgeInfo;
import com.fate.oj.model.entity.Question;
import com.fate.oj.model.entity.QuestionSubmit;
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
    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;
}
