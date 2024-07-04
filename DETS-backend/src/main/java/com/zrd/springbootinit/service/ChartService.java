package com.zrd.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zrd.springbootinit.model.dto.chart.ChartQueryRequest;
import com.zrd.springbootinit.model.entity.Chart;


/**
* @author 张瑞东
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2024-07-04 14:09:46
*/
public interface ChartService extends IService<Chart> {

    QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest);
}
