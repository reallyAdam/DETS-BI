import React, {useEffect, useState} from 'react';
import {listMyChartVoByPageUsingPost} from "@/services/DETS/chartController";
import {Avatar, Card, List, message} from "antd";
import EChartsReact from "echarts-for-react";
import {useModel} from "@@/exports";
import Search from "antd/es/input/Search";


/**
 * 查看我的图表
 * @constructor
 */
const MyChartPage: React.FC = () => {

  const initQueryParams = {
    current: 1,
    pageSize:2,
  };

  const [queryParam,setQueryParams] = useState<API.ChartQueryRequest>({...initQueryParams})
  const [chartData, setChartData] = useState<API.Chart[]>();
  const [total, setTotal] = useState<number>(0);
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState ?? {};
  // 加载状态，用来控制页面是否加载，默认正在加载
  const [loading, setLoading] = useState<boolean>(true);


  const loadData= async () => {
    setLoading(true);
    try{
      const res = await listMyChartVoByPageUsingPost(queryParam);
      if(res.data)
      {
          setChartData(res.data.records ?? []);
          setTotal(res.data.total ?? 0);
          if(res.data.records)
          {
            res.data.records.forEach(data=>{
              const chart = JSON.parse(data.genChart ?? '{}')
              chart.title = undefined
              data.genChart = JSON.stringify(chart)
            })
          }
      }
      else
      {
        message.error("获取我的图表失败");
      }
    }
    catch (e:any)
    {
      message.error("获取我的图表失败" + e.message);
    }
    setLoading(false)
  }

  useEffect(() => {
    // 这个页面首次渲染的时候，以及这个数组中的搜索条件发生变化的时候，会执行loadData方法,自动触发重新搜索
    loadData();
  },[queryParam]);

  return (
    <div className="my-chart-page">
      {/* 引入搜索框 */}
      <div>
        {/*
          当用户点击搜索按钮触发 一定要把新设置的搜索条件初始化，要把页面切回到第一页;
          如果用户在第二页,输入了一个新的搜索关键词,应该重新展示第一页,而不是还在搜第二页的内容
        */}
        <Search placeholder="请输入图表名称" enterButton loading={loading} onSearch={(value) => {
          // 设置搜索条件
          setQueryParams({
            // 原始搜索条件
            ...initQueryParams,
            // 搜索词
            name: value,
          })
        }}/>
      </div>
      <div className={"margin-16"}/>
      <List
        /*
          栅格间隔16像素;xs屏幕<576px,栅格数1;
          sm屏幕≥576px，栅格数1;md屏幕≥768px,栅格数1;
          lg屏幕≥992px,栅格数2;xl屏幕≥1200px,栅格数2;
          xxl屏幕≥1600px,栅格数2
        */
        grid={{
          gutter: 16,
          xs: 1,
          sm: 1,
          md: 1,
          lg: 2,
          xl: 2,
          xxl: 2,
        }}
        pagination={{
          /*
            page第几页，pageSize每页显示多少条;
            当用户点击这个分页组件,切换分页时,这个组件就会去触发onChange方法,会改变咱们现在这个页面的搜索条件
          */
          onChange: (page, pageSize) => {
            // 当切换分页，在当前搜索条件的基础上，把页数调整为当前的页数
            setQueryParams({
              ...queryParam,
              current: page,
              pageSize,
            })
          },
          // 显示当前页数
          current: queryParam.current,
          // 页面参数改成自己的
          pageSize: queryParam.pageSize,
          // 总数设置成自己的
          total: total,
        }}
        // 设置成我们的加载状态
        loading={loading}
        dataSource={chartData}
        renderItem={(item) => (
          <List.Item key={item.id}>
            {/* 用卡片包裹 */}
            <Card style={{ width: '100%' }}>
              <List.Item.Meta
                // 把当前登录用户信息的头像展示出来
                avatar={<Avatar src={currentUser && currentUser.userAvatar} />}
                title={item.name}
                description={item.chartType ? '图表类型：' + item.chartType : undefined}
              />
              {/* 在元素的下方增加16像素的外边距 */}
              <div style={{ marginBottom: 16 }} />
              <p>{'分析目标：' + item.goal}</p>
              {/* 在元素的下方增加16像素的外边距 */}
              <div style={{ marginBottom: 16 }} />
              <EChartsReact option={item.genChart && JSON.parse(item.genChart)} />
            </Card>
          </List.Item>
        )}
      />
    </div>
  );
};
export default MyChartPage;
