package com.fate.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fate.oj.common.BaseResponse;
import com.fate.oj.common.ErrorCode;
import com.fate.oj.common.ResultUtils;
import com.fate.oj.exception.BusinessException;
import com.fate.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.fate.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.fate.oj.model.entity.QuestionSubmit;
import com.fate.oj.model.entity.User;
import com.fate.oj.model.vo.QuestionSubmitVO;
import com.fate.oj.service.QuestionSubmitService;
import com.fate.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: Fate
 * @Date: 2024/7/1 00:15
 */

@RestController
@RequestMapping("/question_submit")
@Slf4j
@Deprecated
public class QuestionSubmitController {

    @Resource
    private UserService userService;

    @Resource
    private QuestionSubmitService questionSubmitService;


    @RequestMapping("/submit")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
      HttpServletRequest request) {
        if(questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0 ){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        final User loginUser = userService.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest,loginUser);
        return ResultUtils.success(questionSubmitId) ;
    }

    /**
     * 分页获取题目提交信息
     * @param questionSubmitQueryRequest 查询条件
     * @param request 请求
     * @return 提交题目信息
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        final User loginUser = userService.getLoginUser(request);
        // 获取原始的题目提交信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        // 返回脱敏后的题目提交信息
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
    }
}
