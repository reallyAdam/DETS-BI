package com.zrd.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zrd.springbootinit.annotation.AuthCheck;
import com.zrd.springbootinit.common.BaseResponse;
import com.zrd.springbootinit.common.DeleteRequest;
import com.zrd.springbootinit.common.ErrorCode;
import com.zrd.springbootinit.common.ResultUtils;
import com.zrd.springbootinit.constant.FileConstant;
import com.zrd.springbootinit.constant.UserConstant;
import com.zrd.springbootinit.exception.BusinessException;
import com.zrd.springbootinit.exception.ThrowUtils;
import com.zrd.springbootinit.manager.AIManager;
import com.zrd.springbootinit.model.dto.chart.*;
import com.zrd.springbootinit.model.dto.file.UploadFileRequest;
import com.zrd.springbootinit.model.entity.Chart;
import com.zrd.springbootinit.model.entity.User;
import com.zrd.springbootinit.model.enums.FileUploadBizEnum;
import com.zrd.springbootinit.model.vo.BiResponseVO;
import com.zrd.springbootinit.service.ChartService;
import com.zrd.springbootinit.service.UserService;
import com.zrd.springbootinit.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * 帖子接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

    @Resource
    private AIManager aiManager;

    // region 增删改查

    /**
     * 创建
     *
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newChartId = chart.getId();
        return ResultUtils.success(newChartId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        long id = chartUpdateRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chart);
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param chartQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                chartService.getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Chart>> listChartVOByPage(@RequestBody ChartQueryRequest chartQueryRequest,
            HttpServletRequest request) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                chartService.getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<Chart>> listMyChartVOByPage(@RequestBody ChartQueryRequest chartQueryRequest,
            HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                chartService.getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param chartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartEditRequest, chart);
        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 智能图表分析
     *
     * @param multipartFile
     * @param genChartByAIRequest
     * @param request
     * @return
     */
    @PostMapping("/gen")
    public BaseResponse<BiResponseVO> genChartByAI(@RequestPart("file") MultipartFile multipartFile,
                                             GenChartByAIRequest genChartByAIRequest, HttpServletRequest request) {
        String goal = genChartByAIRequest.getGoal();
        String chartType = genChartByAIRequest.getChartType();
        String name = genChartByAIRequest.getName();
        ThrowUtils.throwIf(StringUtils.isBlank(goal),ErrorCode.PARAMS_ERROR,"分析目标不能为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(goal) && goal.length() > 100,ErrorCode.PARAMS_ERROR,"分析目标过长");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100,ErrorCode.PARAMS_ERROR,"图表名字过长");
        //必须是登录的用户才能使用
        User loginUser = userService.getLoginUser(request);

        if (StringUtils.isNotBlank(chartType)) {
            // 就将分析目标拼接上“请使用”+图表类型
            goal += "，请使用" + chartType +"图表类型";
        }

        //构建AI的提示词
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求:").append("\n");
        userInput.append(goal).append("\n");
        userInput.append("原始数据:").append("\n");
        //将传输的excel文件转换为csv
        String excelToCsv = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(excelToCsv).append("\n");

        //调用AI
        String doChat = aiManager.doChat(userInput.toString());
        //对返回数据进行处理
        String[] split = doChat.split("【【【【【");
        if(split.length < 3)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"AI生成错误");
        }
        String genChart = split[1].trim();
        String genResult = split[2].trim();
        BiResponseVO biResponseVO = new BiResponseVO();
        biResponseVO.setGenChart(genChart);
        biResponseVO.setGenResult(genResult);

        //插入数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(excelToCsv);
        chart.setChartType(chartType);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setUserId(loginUser.getId());
        boolean save = chartService.save(chart);
        ThrowUtils.throwIf(!save,ErrorCode.SYSTEM_ERROR,"数据库插入图表失败");
        biResponseVO.setChartId(chart.getId());
        return ResultUtils.success(biResponseVO);
    }
}
