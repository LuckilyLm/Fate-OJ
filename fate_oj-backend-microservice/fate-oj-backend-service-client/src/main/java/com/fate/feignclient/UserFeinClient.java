package com.fate.feignclient;


import com.fate.common.ErrorCode;
import com.fate.exception.BusinessException;
import com.fate.model.entity.User;
import com.fate.model.enums.UserRoleEnum;
import com.fate.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

import static com.fate.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 远程调用用户服务
 */
@SuppressWarnings("all")
@FeignClient(name = "fateoj-backend-user-service",path = "/api/user/inner")
public interface UserFeinClient {


    /**
     * 获取当前登录用户
     */
    default User getLoginUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 根据id获取用户信息
     */
    @GetMapping("/get/id")
    User getById(@RequestParam("userId") long userId);

    /**
     * 根据ids获取用户信息
     */
    @GetMapping("/get/ids")
    List<User> listByIds(@RequestParam("ids") Collection<Long> ids);

    /**
     * 是否为管理员
     */
    default boolean isAdmin(User user){
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }


    /**
     * 获取脱敏的用户信息
     */
     default UserVO getUserVO(User user){
         if (user == null) {
             return null;
         }
         UserVO userVO = new UserVO();
         BeanUtils.copyProperties(user, userVO);
         return userVO;
    }


}
