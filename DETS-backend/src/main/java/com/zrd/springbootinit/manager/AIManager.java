package com.zrd.springbootinit.manager;

import com.zrd.springbootinit.common.ErrorCode;
import com.zrd.springbootinit.exception.BusinessException;
import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.exception.SparkException;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import io.github.briqt.spark4j.model.response.SparkTextUsage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AIManager {
    private SparkClient sparkClient = new SparkClient();
    /**
     * AI 对话
     *
     * @param message
     * @return
     */
    public String doChat(String message) {
            sparkClient.appid="69e3188e";
            sparkClient.apiKey="41730c7243b5dbbe2ea8fed52a40a2ff";
            sparkClient.apiSecret="MjAxMTIyMzM0M2UyNTc1MWQ3ZTYxMWMw";
            // 消息列表，可以在此列表添加历史对话记录
            List<SparkMessage> messages=new ArrayList<>();
        messages.add(SparkMessage.systemContent("你是一个数据分析师和前端开发专家,精通各种图表"+"接下来我会按照以下固定格式给你提供内容：\n" +
                "图表类型(默认为柱形图): \n" +
                "分析需求：\n" +
                "{数据分析的需求或者目标}\n" +
                "原始数据：\n" +
                "{csv格式的原始数据，用,作为分隔符}\n" +
                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
                "【【【【【\n" +
                "{前端 Echarts V5 的 option 配置对象json代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释\n" +
                "【【【【【\n" +
                "{明确的数据分析结论、越详细越好，不要生成多余的注释}\n" +
                "示例格式:\n" +
                "【【【【【\n" +
                "{  \n" +
                "    \"title\": {  \n" +
                "        \"text\": \"网站用户增长情况\",  \n" +
                "        \"subtext\": \"\" \n" +
                "    },  \n" +
                "    \"tooltip\": {  \n" +
                "        \"trigger\": \"axis\",  \n" +
                "        \"axisPointer\": {  \n" +
                "            \"type\": \"shadow\"  \n" +
                "        }  // 注意这里也添加了逗号  \n" +
                "    },  \n" +
                "    \"legend\": {  \n" +
                "        \"data\": [\"用户数\"]  // 注意这里使用了双引号  \n" +
                "    },  \n" +
                "    \"xAxis\": {  \n" +
                "        \"data\": [\"1号\", \"2号\", \"3号\"]  \n" +
                "    },  \n" +
                "    \"yAxis\": {},  \n" +
                "    \"series\": [{  \n" +
                "        \"name\": \"用户数\",  \n" +
                "        \"type\": \"bar\",  \n" +
                "        \"data\": [10, 20, 30]  \n" +
                "    }]  \n" +
                "}\n" +
                "【【【【【\n" +
                "根据数据分析可得，该网站用户数量逐日增长，时间越长，用户数量增长越多。"));
            messages.add(SparkMessage.userContent(message));
            // 构造请求
            SparkRequest sparkRequest=SparkRequest.builder()
                    // 消息列表
                    .messages(messages)
                    // 模型回答的tokens的最大长度,非必传，默认为2048。
                    // V1.5取值为[1,4096]
                    // V2.0取值为[1,8192]
                    // V3.0取值为[1,8192]
                    .maxTokens(2048)
                    // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                    .temperature(0.2)
                    .topK(2)
                    // 指定请求版本，默认使用最新3.0版本
                    .apiVersion(SparkApiVersion.V3_5)
                    .build();

            try {
                // 同步调用
                SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
                SparkTextUsage textUsage = chatResponse.getTextUsage();

                log.info("\n提问tokens：" + textUsage.getPromptTokens()
                        + "，回答tokens：" + textUsage.getCompletionTokens()
                        + "，总消耗tokens：" + textUsage.getTotalTokens());
                return chatResponse.getContent();
            } catch (SparkException e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"AI请求错误");
            }
    }

}
