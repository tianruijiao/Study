package com.example.springaifirstattempt.controller;

import com.example.springaifirstattempt.vo.resp.Result;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    private DeepSeekChatModel deepSeekChatModel;

    @GetMapping("/chat")
    private Result chat(@RequestParam("question") String question) {
        String call = deepSeekChatModel.call(question);
        System.out.println(call);
        return Result.success(call);
    }
}
