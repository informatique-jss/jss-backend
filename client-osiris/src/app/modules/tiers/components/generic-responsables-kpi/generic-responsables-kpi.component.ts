import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgIcon } from '@ng-icons/core';
import { EChartsOption } from 'echarts';
import { provideEchartsCore } from 'ngx-echarts';
import { Subscription } from 'rxjs';
import { ANNUALLY_PERIOD, LABEL_TYPE_DATETIME, MONTHLY_PERIOD, TIERS_KPI_HOME_DISPLAY, WEEKLY_PERIOD } from '../../../../libs/Constants';
import { EchartComponent } from "../../../../libs/echart/echart.component";
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { KpiWidgetDto } from '../../../main/model/KpiWidgetDto';
import { Responsable } from '../../../profile/model/Responsable';
import { echarts } from '../../../reporting/components/reporting-chart/echarts-config';
import { KpiWidgetService } from '../../services/kpiWidget.service';
import { ResponsableService } from '../../services/responsable.service';

@Component({
  selector: 'generic-responsables-kpi',
  standalone: true,
  providers: [provideEchartsCore({ echarts })],
  imports: [...SHARED_IMPORTS, NgIcon, EchartComponent],
  templateUrl: './generic-responsables-kpi.component.html',
  styleUrls: ['./generic-responsables-kpi.component.css']
})
export class GenericResponsablesKpiComponent implements OnInit {
  pageCode: string = TIERS_KPI_HOME_DISPLAY;

  options: EChartsOption | undefined;
  selectedResponsablesSubscription: Subscription = new Subscription;
  selectedResponsables: Responsable[] = [];

  TIERS_KPI_HOME_DISPLAY = TIERS_KPI_HOME_DISPLAY;
  graphsHeight: number = 220;
  kpiCrms: KpiWidgetDto[] = [];

  LABEL_TYPE_DATETIME = LABEL_TYPE_DATETIME;
  selectedKpiCrm: KpiWidgetDto | undefined;
  ANNUALLY_PERIOD = ANNUALLY_PERIOD;
  MONTHLY_PERIOD = MONTHLY_PERIOD;
  WEEKLY_PERIOD = WEEKLY_PERIOD;
  selectedTimeScale: string = ANNUALLY_PERIOD;
  serieValues: any[] = [];

  constructor(
    private responsableService: ResponsableService,
    private kpiWidgetService: KpiWidgetService,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.pageCode = params['screen'];

      this.selectedResponsablesSubscription = this.responsableService.getSelectedResponsables().subscribe(respos => {
        this.selectedResponsables = respos;
        this.refreshKpi();
      });
    });
  }

  refreshKpi() {
    this.kpiWidgetService.getKpiWidgetsByPage(this.pageCode, this.selectedTimeScale, this.selectedResponsables).subscribe(response => {
      if (response) {
        this.kpiCrms = response;
        this.selectWidgetToDisplay(this.kpiCrms[0]);
      }
    });
  }

  changeTimeScale(timeScale: string): void {
    this.selectedTimeScale = timeScale;
    this.refreshKpi();
  }

  selectWidgetToDisplay(kpiWidget: KpiWidgetDto) {
    this.selectedKpiCrm = kpiWidget;
    if (this.selectedKpiCrm) {
      this.kpiWidgetService.getKpiWidgetSerieValues(this.selectedKpiCrm, this.selectedResponsables, this.selectedTimeScale).subscribe(response => {
        if (response) {
          this.serieValues = response;
        }
      });
    }
  }
}

