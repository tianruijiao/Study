package com.example.springaifirstattempt.controller;

import com.example.springaifirstattempt.vo.resp.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    private DeepSeekChatModel deepSeekChatModel;

    @GetMapping("/chat")
    private Result<String> chat(@RequestParam("question") String question) {
        String call = deepSeekChatModel.call(question);
        return Result.success(call);
    }

    /**
     * 聊天流
     * @param question  问题
     * @param temp  前端传入
     * @return Flux<String>，流式数据不使用包装类返回
     */
    @GetMapping(value = "/chatStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    private Flux<String> chatStream(@RequestParam("question") String question
                                    , @RequestParam(value = "temp", required = false) Double temp
                                    ,HttpServletResponse  response) {
        //设置编码
        response.setCharacterEncoding("UTF-8");
        // 创建Prompt
        Prompt prompt;
        if (null != temp) {
            // 如果传入temp参数，则使用自定义的DeepSeekChatOptions
            DeepSeekChatOptions build = DeepSeekChatOptions.builder().temperature(temp).build();
            prompt = new Prompt(new UserMessage(question), build);
        } else {
            prompt = new Prompt(new UserMessage(question));
        }
        Flux<ChatResponse> stream = deepSeekChatModel.stream(prompt);
        return stream.mapNotNull(chatResponse -> chatResponse.getResult().getOutput().getText());
    }
}
