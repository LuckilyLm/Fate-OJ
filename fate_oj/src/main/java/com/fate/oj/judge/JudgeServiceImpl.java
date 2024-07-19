package com.fate.oj.judge;

import cn.hutool.json.JSONUtil;
import com.fate.oj.common.ErrorCode;
import com.fate.oj.exception.BusinessException;
import com.fate.oj.exception.ThrowUtils;
import com.fate.oj.judge.codesandbox.CodeSandBox;
import com.fate.oj.judge.codesandbox.CodeSandBoxFactory;
import com.fate.oj.judge.codesandbox.CodeSandBoxProxy;
import com.fate.oj.judge.codesandbox.model.ExecuteRequest;
import com.fate.oj.judge.codesandbox.model.ExecuteResponse;
import com.fate.oj.judge.strategy.JudgeContext;
import com.fate.oj.model.dto.question.JudgeCase;
import com.fate.oj.model.dto.questionsubmit.JudgeInfo;
import com.fate.oj.model.entity.Question;
import com.fate.oj.model.entity.QuestionSubmit;
import com.fate.oj.model.enums.QuestionSubmitStatusEnum;
import com.fate.oj.service.QuestionService;
import com.fate.oj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 判题服务实现类
 * @Author: Fate
 * @Date: 2024/7/2 17:14
 **/
public class JudgeServiceImpl implements JudgeService {

    @Value("${codesandbox.type}")
    private String codeSandBoxType;

    @Resource
    private JudgeManager judgeManager;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private QuestionService questionService;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1. 根据提交题目id获取提交题目信息
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        ThrowUtils.throwIf(questionSubmit == null, new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交题目不存在"));

        // 根据题目id获取题目信息
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        ThrowUtils.throwIf(question == null, new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在"));

        // 2. 判断题目是否已经提交过, 如果已经提交过，不允许重复提交
        ThrowUtils.throwIf(!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue()),
                new BusinessException(ErrorCode.OPERATION_ERROR,"已经提交过，请勿重复提交"));

        // 3. 更新题目提交状态为正在判题中
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean updateResult = questionSubmitService.updateById(questionSubmitUpdate);
        ThrowUtils.throwIf(!updateResult,new BusinessException(ErrorCode.OPERATION_ERROR,"更新提交题目状态失败"));


        // 4. 调用沙箱接口执行代码, 获取执行结果
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(codeSandBoxType);
        codeSandBox = new CodeSandBoxProxy(codeSandBox);
        // 获取提交的代码和编程语言
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取题目的测试用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());

        ExecuteRequest executeRequest = ExecuteRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();

        // 调用沙箱接口执行代码
        ExecuteResponse executeResponse = codeSandBox.executeCode(executeRequest);

        // 5. 根据执行结果进行判题
        // 封装判题所需的上下文信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(executeResponse.getOutputList());
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);

        // 调用判题管理器进行判题
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        // 6. 更新题目提交状态为已完成, 并保存判题信息
        QuestionSubmit questionSubmitFinish = new QuestionSubmit();
        questionSubmitFinish.setId(questionSubmitId);
        questionSubmitFinish.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        questionSubmitFinish.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        boolean result = questionSubmitService.updateById(questionSubmitFinish);
        ThrowUtils.throwIf(!result,new BusinessException(ErrorCode.OPERATION_ERROR,"更新提交题目状态失败"));

        // 7. 返回更新后的题目提交信息
        return questionSubmitService.getById(questionSubmitId);
    }
}
