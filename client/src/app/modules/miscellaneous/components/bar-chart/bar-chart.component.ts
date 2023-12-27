import { formatDate } from '@angular/common';
import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ECharts, EChartsOption } from 'echarts';

@Component({
  selector: 'bar-chart',
  templateUrl: './bar-chart.component.html',
  styleUrls: ['./bar-chart.component.css']
})
export class BarChartComponent implements OnChanges {

  constructor() { }

  @Input() firstSerieName: string = "First serie";
  @Input() firstSerieData: Array<Object> = [] as Array<Object>;
  @Input() firstSerieColors: Array<string> = [] as Array<string>;

  @Input() secondSerieName: string = "Second serie";
  @Input() secondSerieData: Array<Object> = [] as Array<Object>;

  @Input() title: string = "default";

  chartOption: EChartsOption = {};
  chartInstance: ECharts = {} as ECharts;

  onChartInit($event: any) {
    this.chartInstance = $event;
    this.initChart();
  }

  ngOnChanges(changes: SimpleChanges) {
    this.initChart();
  }

  initChart() {
    if (this.chartInstance.setOption == undefined)
      return;
    let series: any;
    let tmpfirstSerieData: Array<Object> = [] as Array<Object>;
    for (let i = 0; i < this.firstSerieData.length; i++) {
      const data = this.firstSerieData[i];
      let obj: Object = {} as Object;
      obj = {
        value: data,
        itemStyle: {
          color: this.firstSerieColors[i]
        }
      };
      tmpfirstSerieData.push(obj);
    }
    if (this.secondSerieData.length > 0) {
      series =
        [
          {
            name: this.firstSerieName,
            type: 'bar',
            data: tmpfirstSerieData
          },
          {
            name: this.secondSerieName,
            type: 'line',
            data: this.secondSerieData,
            lineStyle: {
              type: 'dotted', width: 4
            }
          }
        ];
    } else {
      series =
        [
          {
            name: this.firstSerieName,
            type: 'bar',
            data: tmpfirstSerieData
          }
        ];
    }

    this.chartOption = {
      xAxis: {
        type: 'time',
        axisLabel: {
        }
      },
      yAxis: {
        type: 'value',
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'line'
        }, formatter: function (params: any) {
          let currentDate = formatDate(params[0].axisValue, 'EEEE dd/MM/yyyy à HH:mm', 'fr-FR');
          return currentDate[0].toUpperCase() + currentDate.substr(1).toLowerCase() + "<br>" +
            params[0].marker + params[0].seriesName + " : " + params[0].value[1] + "<br>" +
            params[1].marker + params[1].seriesName + " : " + params[1].value[1]
        }
      },
      toolbox: {
        feature: {
          saveAsImage: {
            show: true,
            name: this.title
          }
        }
      },
      dataZoom: [
        {
          type: 'slider',
          throttle: 500,
          labelFormatter: function (value, valueStr) {
            let currentDate = formatDate(value, 'dd/MM/yyyy HH:mm', 'fr-FR');
            return currentDate;
          }
        }
      ],
      series: series
    };
  }

}
