package com.fate.oj.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fate.oj.common.ErrorCode;
import com.fate.oj.constant.CommonConstant;
import com.fate.oj.exception.BusinessException;
import com.fate.oj.judge.JudgeService;
import com.fate.oj.mapper.QuestionSubmitMapper;
import com.fate.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.fate.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.fate.oj.model.entity.Question;
import com.fate.oj.model.entity.QuestionSubmit;
import com.fate.oj.model.entity.User;
import com.fate.oj.model.enums.QuestionSubmitLanguageEnum;
import com.fate.oj.model.enums.QuestionSubmitStatusEnum;
import com.fate.oj.model.vo.QuestionSubmitVO;
import com.fate.oj.model.vo.QuestionVO;
import com.fate.oj.service.QuestionService;
import com.fate.oj.service.QuestionSubmitService;
import com.fate.oj.service.UserService;
import com.fate.oj.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService{

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    /**
     * 提交题目
     * @param questionSubmitAddRequest 题目提交请求
     * @param loginUser 登录用户
     * @return 提交题目信息的ID
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 校验语言类型是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum enumByValue = QuestionSubmitLanguageEnum.getEnumByValue(language);

        if (enumByValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"语言类型不存在");
        }
        Long questionId = questionSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 获取用户ID
        Long userId = loginUser.getId();

        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(questionSubmitAddRequest.getLanguage());
        // 设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");

        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"提交失败");
        }

        Long questionSubmitId = questionSubmit.getId();

        // 异步判题
        CompletableFuture.runAsync( () -> judgeService.doJudge(questionSubmitId) );

        return questionSubmitId;
    }

    /**
     * 获取查询包装
     * @param questionSubmitQueryRequest 查询请求
     * @return 查询包装
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> questionSubmitQueryWrapper = new QueryWrapper<>();

        if(questionSubmitQueryRequest == null){
            return questionSubmitQueryWrapper;
        }
        // 获取查询参数
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 根据语言类型模糊查询
        questionSubmitQueryWrapper.like(StringUtils.isNotBlank(language), "language", language);

        // 根据题目ID、题目提交状态、用户ID查询
        questionSubmitQueryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        questionSubmitQueryWrapper.eq(ObjectUtils.isNotEmpty(status) && QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        questionSubmitQueryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);

        // 排除已删除的提交题目信息
        questionSubmitQueryWrapper.eq("isDelete",false);

        // 排序,根据sortField和sortOrder进行排序,sortOrder默认降序
        questionSubmitQueryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return questionSubmitQueryWrapper;
    }

    /**
     * 获取单个提交题目信息视图对象
     * @param questionSubmit 提交题目信息
     * @param loginUser 当前登录用户
     * @return 提交题目信息视图对象
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        // 获取原始的题目提交信息
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        long userId = loginUser.getId();
        // 如果当前登录用户不是题目提交者或管理员，则隐藏提交代码
        if (userId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)){
            questionSubmitVO.setCode(null);
        }
        // 根据用户ID获取用户信息
        User questionSubmitUser = userService.getById(questionSubmit.getUserId());
        if(questionSubmitUser != null){
            // 如果用户信息存在，则获取对应的用户信息视图对象并填充到提交题目信息视图对象中
            questionSubmitVO.setUserVO(userService.getUserVO(questionSubmitUser));
        }
        // 根据题目ID获取题目信息
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if(question != null){
            // 如果题目信息存在，则获取对应的题目信息视图对象并填充到提交题目信息视图对象中
            questionSubmitVO.setQuestionVO(QuestionVO.objToVo(question));
        }
        return questionSubmitVO;
    }

    /**
     * 分页查询提交题目信息
     * @param questionSubmitPage 分页对象
     * @param loginUser 当前登录用户
     * @return 提交题目信息视图分页对象
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage,User loginUser) {
        // 获取原始提交题目的信息
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();

        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollUtil.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }

        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        // 填充分页提交题目视图对象并返回
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }
}




