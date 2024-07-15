package com.zrd.springbootinit.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *
 * @author zrd
  
 */
@Data
public class GenChartByAIRequest implements Serializable {
    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表类型
     */
    private String chartType;

    /**
     * 图表名称
     */
    private String name;

    private static final long serialVersionUID = 1L;
}