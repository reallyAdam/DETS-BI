import React, {useEffect, useState} from 'react';
import {listMyChartVoByPageUsingPost} from "@/services/DETS/chartController";
import {Avatar, Card, List, message} from "antd";
import EChartsReact from "echarts-for-react";
import {useModel} from "@@/exports";
import {data} from "@umijs/utils/compiled/cheerio/lib/api/attributes";
import {char} from "stylis";


/**
 * 查看我的图表
 * @constructor
 */
const MyChartPage: React.FC = () => {

  const initQueryParams = {
    pageSize:12
  }

  const [queryParam,setQueryParams] = useState<API.ChartQueryRequest>({...initQueryParams})
  const [chartData, setChartData] = useState<API.Chart[]>();
  const [total, setTotal] = useState<number>(0);
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState ?? {};


  const loadData= async () => {
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
  }

  useEffect(() => {
    // 这个页面首次渲染的时候，以及这个数组中的搜索条件发生变化的时候，会执行loadData方法,自动触发重新搜索
    loadData();
  },[setQueryParams]);


  return (
    <List
      itemLayout="horizontal"
      pagination={{
        pageSize: queryParam.pageSize
      }}
      grid={{gutter:16 ,
        xs: 1,
        sm: 1,
        md: 1,
        lg: 2,
        xl: 2,
        xxl: 2,
    }}
      dataSource={chartData}
      renderItem={(item) => (
        <List.Item
          key={item.id}
        >
          <Card>
            <List.Item.Meta
              avatar={<Avatar src={currentUser?.userAvatar ?? undefined}/>}
              title={item.name}
              description={item.chartType ? ("图表类型:  " + item.chartType) : undefined}
            />
            <div style={{marginBottom: 16}}/>
            {"分析目标" + item.goal}
            <div style={{marginBottom: 16}}/>
            <EChartsReact option={JSON.parse(item.genChart ?? '{}')}/>
          </Card>
        </List.Item>
      )}
    />
  );
};
export default MyChartPage;
