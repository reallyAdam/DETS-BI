package com.zrd.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.zrd.springbootinit.common.ErrorCode;
import com.zrd.springbootinit.constant.BIMessageConstant;
import com.zrd.springbootinit.exception.BusinessException;
import com.zrd.springbootinit.manager.AIManager;
import com.zrd.springbootinit.model.entity.Chart;
import com.zrd.springbootinit.service.ChartService;
import com.zrd.springbootinit.utils.ExcelUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class BiMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AIManager aiManager;

    @SneakyThrows
    @RabbitListener(queues = {BIMessageConstant.BI_QUEUE_NAME},ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG)long deliveryTag)
    {
        if(StringUtils.isBlank(message))
        {
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息为空");
        }
        log.info("receiveMessage message = {}",message);
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if(chart == null)
        {
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"图表为空");
        }
        //chart/userInput
        //先将执行状态改为执行中,以减少被重复执行的风险
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus("running");
        boolean b = chartService.updateById(updateChart);
        if(!b)
        {
            channel.basicNack(deliveryTag,false,false);
            handleUpdateChartError(chart.getId(),"更新图表运行中状态失败");
            return;
        }

        //调用AI
        String doChat = aiManager.doChat(buildUserInput(chart));
        //对返回数据进行处理
        String[] split = doChat.split("【【【【【");
        if(split.length < 3)
        {
            channel.basicNack(deliveryTag,false,false);
            handleUpdateChartError(chart.getId(),"AI生成错误");
            return;
        }
        String genChart = split[1].trim();
        String genResult = split[2].trim();

        Chart newUpdateChart = new Chart();
        newUpdateChart.setId(chart.getId());
        //TODO 建议将状态改为枚举值
        newUpdateChart.setStatus("succeed");
        newUpdateChart.setGenResult(genResult);
        newUpdateChart.setGenChart(genChart);
        boolean b1 = chartService.updateById(newUpdateChart);

        if(!b1)
        {
            channel.basicNack(deliveryTag,false,false);
            handleUpdateChartError(chart.getId(),"更新图表成功状态失败");
        }
        channel.basicAck(deliveryTag,false);
    }

    //创建用户输入
    private String buildUserInput(Chart chart)
    {
        String chartType = chart.getChartType();
        String goal = chart.getGoal();
        String chartData = chart.getChartData();
        if (StringUtils.isNotBlank(chartType)) {
            // 就将分析目标拼接上“请使用”+图表类型
            goal += "，请使用" + chartType +"图表类型";
        }

        //构建AI的提示词
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求:").append("\n");
        userInput.append(goal).append("\n");
        userInput.append("原始数据:").append("\n");
        userInput.append(chartData).append("\n");
        return String.valueOf(userInput);
    }


    private void handleUpdateChartError(Long chartId,String message)
    {
        Chart errorChart = new Chart();
        errorChart.setId(chartId);
        errorChart.setStatus("failed");
        errorChart.setExecMassage(message);
        boolean b1 = chartService.updateById(errorChart);
        if(!b1)
        {
            log.error("更新图表状态失败失败:" + chartId);
        }
    }

}
