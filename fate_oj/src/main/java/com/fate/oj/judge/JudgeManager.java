package com.fate.oj.judge;

import com.fate.oj.judge.strategy.JudgeContext;
import com.fate.oj.judge.strategy.JudgeStrategy;
import com.fate.oj.judge.strategy.impl.DefaultJudgeStrategy;
import com.fate.oj.judge.strategy.impl.JavaJudgeStrategy;
import com.fate.oj.model.dto.questionsubmit.JudgeInfo;
import com.fate.oj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Component;

/**
 * 判题管理器
 * @Author: Fate
 * @Date: 2024/7/2 22:58
 **/

@Component
public class JudgeManager
{
    /**
     * 执行判题
     * @param judgeContext 判题上下文
     * @return 判题结果
     */
     protected JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if("java".equals(language)){
            judgeStrategy = new JavaJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
