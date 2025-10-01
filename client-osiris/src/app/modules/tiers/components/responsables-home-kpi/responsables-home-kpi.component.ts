import { Component, OnChanges, OnInit } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { EChartsOption } from 'echarts';
import { provideEchartsCore } from 'ngx-echarts';
import { Subscription } from 'rxjs';
import { OPPORTUNITY_CLOSING_AVERAGE_TIME, ORDER_COMPLETION_AVERAGE_TIME, PAYING_INVOICE_AVERAGE_TIME } from '../../../../libs/Constants';
import { EchartComponent } from '../../../../libs/echart/echart.component';
import { getColor } from '../../../../libs/inspinia/utils/color-utils';
import { AnalyticStatsType } from '../../../main/model/AnalyticStatsType';
import { ConstantService } from '../../../main/services/constant.service';
import { Responsable } from '../../../profile/model/Responsable';
import { AnalyticStatsTypeService } from '../../services/analytic-stats-type.service';
import { ResponsableService } from '../../services/responsable.service';
import { AnalyticStatisticWidgetComponent } from '../analytic-statistic-widget/analytic-statistic-widget.component';
import { echarts } from './../../../reporting/components/reporting-chart/echarts-config';

@Component({
  selector: 'responsables-home-kpi',
  providers: [provideEchartsCore({ echarts })],
  imports: [
    AnalyticStatisticWidgetComponent,
    NgIcon,
    EchartComponent
  ],
  standalone: true,
  templateUrl: './responsables-home-kpi.component.html',
})
export class ResponsablesHomeKpiComponent implements OnInit, OnChanges {

  graphsHeight: number = 220;

  kpisLeft: AnalyticStatsType[] = [
    {
      id: 1,
      icon: 'tablerEye',
      analyticStatsValue: { value: 14.59, suffix: 'M' },
      title: 'Total Views',
      percentage: 5.2,
      percentageIcon: 'tablerTrendingUp',
      isPositive: true,
      aggregateType: ''
    },
    {
      id: 2,
      icon: 'tablerClock',
      analyticStatsValue: { value: 815.58, suffix: 'k' },
      title: 'Sessions',
      percentage: 3.9,
      percentageIcon: 'tablerActivity',
      isPositive: true,
      aggregateType: ''
    },
    {
      id: 3,
      icon: 'tablerRepeatOnce',
      analyticStatsValue: { value: 41.3, suffix: '%' },
      title: 'Bounce Rate',
      percentage: 1.1,
      percentageIcon: 'tablerArrowDownLeft',
      isPositive: false,
      aggregateType: ''
    }
  ]

  kpisRight: AnalyticStatsType[] = [
    {
      id: 2,
      icon: 'tablerClock',
      analyticStatsValue: { value: 815.58, suffix: 'k' },
      title: 'Sessions',
      percentage: 3.9,
      percentageIcon: 'tablerActivity',
      isPositive: true,
      aggregateType: ''
    },
    {
      id: 3,
      icon: 'tablerRepeatOnce',
      analyticStatsValue: { value: 41.3, suffix: '%' },
      title: 'Bounce Rate',
      percentage: 1.1,
      percentageIcon: 'tablerArrowDownLeft',
      isPositive: false,
      aggregateType: ''
    },
    {
      id: 4,
      icon: 'tablerUser',
      analyticStatsValue: { value: 56.39, suffix: 'k' },
      title: 'Active Users',
      percentage: 2.3,
      percentageIcon: 'tablerUsers',
      isPositive: true,
      aggregateType: ''
    }
  ]

  kpisDown: AnalyticStatsType[] = [
    {
      id: 1,
      icon: 'tablerEye',
      analyticStatsValue: { value: 14.59, suffix: 'M' },
      title: 'Total Views',
      percentage: 5.2,
      percentageIcon: 'tablerTrendingUp',
      isPositive: true,
      aggregateType: ''
    },
    {
      id: 2,
      icon: 'tablerClock',
      analyticStatsValue: { value: 815.58, suffix: 'k' },
      title: 'Sessions',
      percentage: 3.9,
      percentageIcon: 'tablerActivity',
      isPositive: true,
      aggregateType: ''
    },
    {
      id: 3,
      icon: 'tablerRepeatOnce',
      analyticStatsValue: { value: 41.3, suffix: '%' },
      title: 'Bounce Rate',
      percentage: 1.1,
      percentageIcon: 'tablerArrowDownLeft',
      isPositive: false,
      aggregateType: ''
    },
    {
      id: 4,
      icon: 'tablerUser',
      analyticStatsValue: { value: 56.39, suffix: 'k' },
      title: 'Active Users',
      percentage: 2.3,
      percentageIcon: 'tablerUsers',
      isPositive: true,
      aggregateType: ''
    },
    {
      id: 2,
      icon: 'tablerClock',
      analyticStatsValue: { value: 815.58, suffix: 'k' },
      title: 'Sessions',
      percentage: 3.9,
      percentageIcon: 'tablerActivity',
      isPositive: true,
      aggregateType: ''
    },
    {
      id: 3,
      icon: 'tablerRepeatOnce',
      analyticStatsValue: { value: 41.3, suffix: '%' },
      title: 'Bounce Rate',
      percentage: 1.1,
      percentageIcon: 'tablerArrowDownLeft',
      isPositive: false,
      aggregateType: ''
    },
  ]

  getAnalyticChartOptions() {
    return {
      chart: {
        height: this.graphsHeight,
        type: 'area',
        toolbar: { show: true }
      },
      dataLabels: {
        enabled: false
      },
      stroke: {
        width: 2,
        curve: 'smooth'
      },
      colors: [getColor('primary'), getColor('secondary')],
      series: [
        {
          name: 'CA HT année N',
          data: generateRandomData(12, 250, 450)
        },
        {
          name: 'CA HT année N-1',
          data: generateRandomData(12, 250, 450)
        }
      ],
      legend: {
        offsetY: 5,
      },
      xaxis: {
        categories: ["8 AM", "9 AM", "10 AM", "11 AM", "12 PM", "1 PM",
          "2 PM", "3 PM", "4 PM", "5 PM", "6 PM", "7 PM", "8 PM",
          "9 PM", "10 PM", "11 PM", "12 AM"],
        axisBorder: { show: false },
        axisTicks: { show: false },
        tickAmount: 6,
        labels: {
          style: {
            fontSize: "12px"
          }
        }
      },
      tooltip: {
        shared: true,
        y: {
          formatter: function (val: number, opts: any) {
            const label = opts.seriesIndex === 0 ? "k€" : "k€";
            return `${val} ${label}`;
          }
        }
      },
      fill: {
        type: "gradient",
        gradient: {
          shadeIntensity: 1,
          opacityFrom: 0.4,
          opacityTo: 0.2,
          stops: [15, 120, 100]
        }
      },
      grid: {
        borderColor: getColor('border-color'),
        padding: {
          bottom: 5
        }
      },
      redrawOnParentResize: true, // ← important
      redrawOnWindowResize: true, // ← aussi
    } as EChartsOption
  }

  selectedResponsablesSubscription: Subscription = new Subscription;

  selectedResponsables: Responsable[] = [];

  kpiCodeToLoad: string[] = [ORDER_COMPLETION_AVERAGE_TIME, OPPORTUNITY_CLOSING_AVERAGE_TIME, PAYING_INVOICE_AVERAGE_TIME];
  kpiLoaded: AnalyticStatsType[] = [];
  // TODO : first try to init chart after timeout
  // ngAfterViewInit() {
  //   setTimeout(() => {
  //     window.dispatchEvent(new Event('resize'));
  //   }, 250);
  // }

  constructor(
    private responsableService: ResponsableService,
    private analyticStatsTypeService: AnalyticStatsTypeService,
    private constantService: ConstantService
  ) {

  }

  ngOnInit() {
    this.selectedResponsablesSubscription = this.responsableService.getSelectedResponsables().subscribe(respos => {
      this.selectedResponsables = respos;
      if (this.selectedResponsables && this.selectedResponsables.length > 0 && this.kpiCodeToLoad)
        for (let kpiCode of this.kpiCodeToLoad)
          this.analyticStatsTypeService.getAnalyticStatsTypeForTiers(kpiCode, this.selectedResponsables).subscribe(response => {
            if (response && response.length > 0) {
              for (let kpiToReplace of response) {
                const index = this.kpiLoaded.findIndex(kpi => kpi.id === kpiToReplace.id);
                if (index !== -1)
                  this.kpiLoaded.splice(index, 1);
              }
              this.kpiLoaded.push(...response);
            }
          });
    });

  }

  ngOnChanges() {
    // this.responsableService.getSelectedResponsables().subscribe(respos => {
    //   this.selectedResponsables = respos;

    // });
  }
}

function convertMinutesToTime(durationInMinutes: number): string {
  const days = Math.floor(durationInMinutes / (60 * 24));
  const hours = Math.floor((durationInMinutes % (60 * 24)) / 60);
  const minutes = durationInMinutes % 60;

  let result = '';
  if (days > 0) {
    result += `${days}j `;
  }
  if (hours > 0) {
    result += `${hours}h `;
  }
  if (minutes > 0 || result === '') {
    result += `${minutes}m`;
  }

  return result.trim();
}
function generateRandomData(count: number, min: number, max: number): number[] {
  return Array.from({ length: count }, () =>
    Math.floor(Math.random() * (max - min + 1)) + min
  );
}
