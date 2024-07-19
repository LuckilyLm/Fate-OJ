package com.fate.oj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fate.oj.model.dto.question.QuestionQueryRequest;
import com.fate.oj.model.entity.Question;
import com.fate.oj.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

public interface QuestionService extends IService<Question> {

    /**
     * 校验参数
     */
    void validQuestion(Question post, boolean add);

    /**
     * 获取分页查询条件包装
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest postQueryRequest);

    /**
     * 获取题目视图对象
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目视图对象
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

}
