package com.fate.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fate.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.fate.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.fate.model.entity.QuestionSubmit;
import com.fate.model.entity.User;
import com.fate.model.vo.QuestionSubmitVO;


public interface QuestionSubmitService extends IService<QuestionSubmit> {

    /**
     * 提交题目
     * @param questionSubmitAddRequest 提交题目请求
     * @param loginUser 登录用户
     * @return 提交结果
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目封装
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);

}
