package com.fate.oj.judge.codesandbox;

import com.fate.oj.judge.codesandbox.impl.ExampleCodeSandBox;
import com.fate.oj.judge.codesandbox.impl.RemoteCodeSandBox;
import com.fate.oj.judge.codesandbox.impl.ThirdPartyCodeSandBox;

/**
 * 代码沙箱工厂类
 * @Author: Fate
 * @Date: 2024/7/2 13:33
 **/
public class CodeSandBoxFactory
{
    /**
     * 创建代码沙盒实例
     * @param type 代码沙箱类型
     * @return 代码沙箱实例
     */
    public static CodeSandBox newInstance(String type){
        return switch (type) {
            case "remote" -> new RemoteCodeSandBox();
            case "thirdParty" -> new ThirdPartyCodeSandBox();
            default -> new ExampleCodeSandBox();
        };
    }
}
