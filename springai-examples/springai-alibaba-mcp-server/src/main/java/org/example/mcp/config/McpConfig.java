package org.example.mcp.config;

import org.example.mcp.tool.TimeTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    /**
     * 创建并配置工具回调提供者（ToolCallbackProvider）Bean
     *
     * 这个方法将自定义的MCP工具（如TimeTools）注册到Spring AI框架中，
     * 使得AI模型能够发现并调用这些工具。当AI模型需要执行特定功能时（如获取时间），
     * 框架会通过这个提供者找到对应的工具方法并执行。
     *
     * 工作流程：
     * 1. AI模型识别用户请求需要工具调用
     * 2. 框架通过ToolCallbackProvider查找可用工具
     * 3. 找到匹配的工具方法并执行
     * 4. 将工具执行结果返回给AI模型继续处理
     *
     * @param timeTools 时间工具实例，由Spring容器自动注入
     *                  这个参数包含了所有使用@Tool注解标记的工具方法
     *
     * @return ToolCallbackProvider 实例，用于提供工具调用能力
     *         这个提供者包装了所有注册的工具，使它们对AI模型可用
     *
     * @see ToolCallbackProvider
     * @see MethodToolCallbackProvider
     * @see MethodToolCallbackProvider.Builder
     *
     * @example 配置示例：
     * <pre>
     * // 当AI模型需要调用时间相关工具时：
     * // 1. 用户提问："现在上海是什么时间？"
     * // 2. AI模型识别需要调用getTimeByZoneId工具
     * // 3. 框架通过此ToolCallbackProvider找到TimeTools工具
     * // 4. 执行getTimeByZoneId("Asia/Shanghai")方法
     * // 5. 返回结果给AI模型生成最终回答
     * </pre>
     *
     * @note 注意事项：
     * - 可以注册多个工具对象，只需在toolObjects()方法中继续添加
     * - 所有工具类必须使用@Component或@Service等Spring注解
     * - 工具方法必须使用@Tool和@ToolParam注解进行标记
     * - 工具方法的参数和返回值类型应该简单明了，便于AI模型理解
     */
    @Bean
    public ToolCallbackProvider getToolCallbackProvider(TimeTools timeTools) {
        return MethodToolCallbackProvider.builder().toolObjects(timeTools).build();
    }

}
