import { Component, OnChanges, OnInit } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { Subscription } from 'rxjs';
import { getColor } from '../../../../libs/inspinia/utils/color-utils';
import { AnalyticStatisticWidgetComponent } from '../../../main/components/analytic-statistic-widget/analytic-statistic-widget.component';
import { ApexchartComponent } from '../../../main/components/apexchart/apexchart.component';
import { AnalyticStatsType } from '../../../main/model/AnalyticStatsType';
import { Responsable } from '../../../profile/model/Responsable';
import { AnalyticStatsTypeService } from '../../services/analytic-stats-type.service';
import { ResponsableService } from '../../services/responsable.service';

@Component({
  selector: 'responsables-home-kpi',
  imports: [
    AnalyticStatisticWidgetComponent,
    NgIcon,
    ApexchartComponent
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
      isPositive: true
    },
    {
      id: 2,
      icon: 'tablerClock',
      analyticStatsValue: { value: 815.58, suffix: 'k' },
      title: 'Sessions',
      percentage: 3.9,
      percentageIcon: 'tablerActivity',
      isPositive: true
    },
    {
      id: 3,
      icon: 'tablerRepeatOnce',
      analyticStatsValue: { value: 41.3, suffix: '%' },
      title: 'Bounce Rate',
      percentage: 1.1,
      percentageIcon: 'tablerArrowDownLeft',
      isPositive: false
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
      isPositive: true
    },
    {
      id: 3,
      icon: 'tablerRepeatOnce',
      analyticStatsValue: { value: 41.3, suffix: '%' },
      title: 'Bounce Rate',
      percentage: 1.1,
      percentageIcon: 'tablerArrowDownLeft',
      isPositive: false
    },
    {
      id: 4,
      icon: 'tablerUser',
      analyticStatsValue: { value: 56.39, suffix: 'k' },
      title: 'Active Users',
      percentage: 2.3,
      percentageIcon: 'tablerUsers',
      isPositive: true
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
      isPositive: true
    },
    {
      id: 2,
      icon: 'tablerClock',
      analyticStatsValue: { value: 815.58, suffix: 'k' },
      title: 'Sessions',
      percentage: 3.9,
      percentageIcon: 'tablerActivity',
      isPositive: true
    },
    {
      id: 3,
      icon: 'tablerRepeatOnce',
      analyticStatsValue: { value: 41.3, suffix: '%' },
      title: 'Bounce Rate',
      percentage: 1.1,
      percentageIcon: 'tablerArrowDownLeft',
      isPositive: false
    },
    {
      id: 4,
      icon: 'tablerUser',
      analyticStatsValue: { value: 56.39, suffix: 'k' },
      title: 'Active Users',
      percentage: 2.3,
      percentageIcon: 'tablerUsers',
      isPositive: true
    },
    {
      id: 2,
      icon: 'tablerClock',
      analyticStatsValue: { value: 815.58, suffix: 'k' },
      title: 'Sessions',
      percentage: 3.9,
      percentageIcon: 'tablerActivity',
      isPositive: true
    },
    {
      id: 3,
      icon: 'tablerRepeatOnce',
      analyticStatsValue: { value: 41.3, suffix: '%' },
      title: 'Bounce Rate',
      percentage: 1.1,
      percentageIcon: 'tablerArrowDownLeft',
      isPositive: false
    },
  ]

  analyticChartOptions: any = () => ({
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
  })

  selectedResponsablesSubscription: Subscription = new Subscription;

  selectedResponsables: Responsable[] = [];

  // TODO : first try to init chart after timeout
  // ngAfterViewInit() {
  //   setTimeout(() => {
  //     window.dispatchEvent(new Event('resize'));
  //   }, 250);
  // }

  constructor(
    private responsableService: ResponsableService,
    private analyticStatsTypeService: AnalyticStatsTypeService,
  ) {

  }

  ngOnInit() {
    this.selectedResponsablesSubscription = this.responsableService.getSelectedResponsables().subscribe(respos => {
      this.selectedResponsables = respos;
      if (this.selectedResponsables)
        this.analyticStatsTypeService.getAnalyticStatsTypesForTiers(this.selectedResponsables).subscribe(response => {
          if (response)
            this.kpisLeft = response;
        });
    });

  }

  ngOnChanges() {
    // this.responsableService.getSelectedResponsables().subscribe(respos => {
    //   this.selectedResponsables = respos;

    // });
  }
}

function generateRandomData(count: number, min: number, max: number): number[] {
  return Array.from({ length: count }, () =>
    Math.floor(Math.random() * (max - min + 1)) + min
  );
}
