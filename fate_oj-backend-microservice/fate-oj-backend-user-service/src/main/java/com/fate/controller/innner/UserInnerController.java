package com.fate.controller.innner;

import com.fate.model.entity.User;
import com.fate.feignclient.UserFeinClient;
import com.fate.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @Author: Fate
 * @Date: 2024/7/7 0:37
 **/
@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeinClient
{

    @Resource
    private UserService userService;

    @Override
    @GetMapping("/get/id")
    public User getById(@RequestParam("userId") long userId) {
        return userService.getById(userId);
    }

    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("ids") Collection<Long> ids) {
        return userService.listByIds(ids);
    }
}
