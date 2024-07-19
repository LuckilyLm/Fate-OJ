package com.fate.oj.judge.codesandbox.impl;

import com.fate.oj.judge.codesandbox.CodeSandBox;
import com.fate.oj.judge.codesandbox.model.ExecuteRequest;
import com.fate.oj.judge.codesandbox.model.ExecuteResponse;
import com.fate.oj.model.dto.questionsubmit.JudgeInfo;
import com.fate.oj.model.enums.JudgeInfoMessageEnum;
import com.fate.oj.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * 示例代码沙盒实现类（跑通测试用例）
 * @Author: Fate
 * @Date: 2024/7/2 13:14
 **/
public class ExampleCodeSandBox implements CodeSandBox
{

    @Override
    public ExecuteResponse executeCode(ExecuteRequest executeRequest) {
        List<String> inputList = executeRequest.getInputList();

        ExecuteResponse executeResponse = new ExecuteResponse();
        executeResponse.setOutputList(inputList);
        executeResponse.setMessage(QuestionSubmitStatusEnum.SUCCESS.getMessage());
        executeResponse.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());

        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);

        executeResponse.setJudgeInfo(judgeInfo);

        return executeResponse;
    }
}
