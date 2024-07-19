package com.fate.feignclient;


import com.fate.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 判题服务接口
 * @Author: Fate
 * @Date: 2024/7/2 17:10
 **/

@FeignClient(name = "fateoj-backend-judge-service",path = "/api/judge/inner")
public interface JudgeFeignClient
{
    /**
     * 判题
     * @param questionSubmitId 提交题目ID
     * @return 判题结果
     */
    @PostMapping("/do")
    QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId);
}
