package com.fate.codesandbox;

import com.fate.codesandbox.model.ExecuteRequest;
import com.fate.codesandbox.model.ExecuteResponse;
import com.fate.codesandbox.service.impl.JavaNativeCodeSandboxImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

/**
 * @Author: Fate
 * @Date: 2024/7/3 0:01
 **/

@SpringBootApplication
public class FateojCodesandbox {
    public static void main(String[] args) {
        SpringApplication.run(FateojCodesandbox.class, args);
    }
}
