import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ECharts, EChartsOption } from 'echarts';

@Component({
  selector: 'gauge-chart',
  templateUrl: './gauge-chart.component.html',
  styleUrls: ['./gauge-chart.component.css']
})
export class GaugeChartComponent implements OnChanges {

  constructor() { }

  @Input() serieName: string = "First serie";
  @Input() serieValue: number | undefined;
  @Input() firstZoneLimit: number = 0.5;
  @Input() secondZoneLimit: number = 0.85;
  @Input() thirdZoneLimit: number = 1;
  @Input() firstColor: string = "#27ae60";
  @Input() secondColor: string = "#f1c40f";
  @Input() thirdColor: string = "#e74c3c";
  @Input() unit: string = "";

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

    this.chartOption = {
      series: [
        {
          type: 'gauge',
          axisLine: {
            lineStyle: {
              width: 30,
              color: [
                [this.firstZoneLimit, this.firstColor],
                [this.secondZoneLimit, this.secondColor],
                [this.thirdZoneLimit, this.thirdColor]
              ]
            }
          },
          pointer: {
            itemStyle: {
              color: 'auto'
            }
          },
          axisTick: {
            distance: -30,
            length: 8,
            lineStyle: {
              color: '#fff',
              width: 2
            }
          },
          splitLine: {
            distance: -30,
            length: 30,
            lineStyle: {
              color: '#fff',
              width: 4
            }
          },
          axisLabel: {
            color: 'inherit',
            distance: 40,
            fontSize: 15
          },
          title: {
            color: 'auto',
            fontWeight: 800,
            fontSize: 20
          },
          detail: {
            valueAnimation: true,
            formatter: '{value} ' + this.unit,
            color: 'inherit',
            fontSize: 20
          },
          data: [
            {
              value: this.serieValue,
              name: this.serieName
            }
          ]
        }
      ]
    };
  }

}
