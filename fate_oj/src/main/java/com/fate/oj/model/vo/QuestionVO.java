package com.fate.oj.model.vo;

import cn.hutool.json.JSONUtil;
import com.fate.oj.model.dto.question.JudgeConfig;
import com.fate.oj.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目视图
 */
@Data
public class QuestionVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 标签列表(json数组)
     */
    private List<String> tags;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 题目提交次数
     */
    private Integer submitNum;

    /**
     * 题目通过次数
     */
    private Integer acceptNum;

    /**
     * 判题配置(json对象)
     */
    private JudgeConfig judgeConfig;

    /**
     * 点赞次数
     */
    private Integer thumbNum;

    /**
     * 收藏次数
     */
    private Integer favorNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户信息
     */
    private UserVO userVO;

    /**
     * 封装类转对象
     * @param questionVO 封装类
     * @return 实体类
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        // 从questionVO复制属性到question
        BeanUtils.copyProperties(questionVO, question);
        // 标签列表转json字符串
        List<String> tagsList = questionVO.getTags();
        question.setTags(JSONUtil.toJsonStr(tagsList));
        // 判题配置转json字符串
        JudgeConfig voJudgeConfig = questionVO.getJudgeConfig();
        if(voJudgeConfig != null){
            question.setJudgeConfig(JSONUtil.toJsonStr(voJudgeConfig));
        }
        return question;
    }

    /**
     * 对象转封装类
     * @param question 实体类
     * @return 封装类
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        // 从question复制属性到questionVO
        BeanUtils.copyProperties(question, questionVO);
        // json字符串转标签列表
        List<String> tagsList = JSONUtil.toList(question.getTags(), String.class);
        questionVO.setTags(tagsList);
        // json字符串转判题配置
        String judgeConfig = question.getJudgeConfig();
        questionVO.setJudgeConfig(JSONUtil.toBean(judgeConfig, JudgeConfig.class));
        return questionVO;
    }
}
