import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { provideEchartsCore } from 'ngx-echarts';
import { ANNUALLY_PERIOD, LABEL_TYPE_DATETIME, MONTHLY_PERIOD, WEEKLY_PERIOD } from '../../../../libs/Constants';
import { EchartComponent } from '../../../../libs/echart/echart.component';
import { Responsable } from '../../../profile/model/Responsable';
import { KpiWidgetService } from '../../../tiers/services/kpiWidget.service';
import { ResponsableService } from '../../../tiers/services/responsable.service';
import { KpiWidgetDto } from '../../model/KpiWidgetDto';
import { echarts } from './../../../reporting/components/reporting-chart/echarts-config';

@Component({
  selector: 'kpi-generic', providers: [provideEchartsCore({ echarts })],
  imports: [
    NgIcon,
    EchartComponent,
    CommonModule
  ],
  standalone: true,
  templateUrl: './kpi-generic.component.html',
  styleUrls: ['./kpi-generic.component.css']
})
export class KpiGenericComponent implements OnInit {
  @Input() selectedResponsables: Responsable[] = [];
  @Input() selectedDisplayedPageCode: string = "";
  graphsHeight: number = 220;
  kpiCrms: KpiWidgetDto[] = [];

  LABEL_TYPE_DATETIME = LABEL_TYPE_DATETIME;
  selectedKpiCrm: KpiWidgetDto | undefined;
  ANNUALLY_PERIOD = ANNUALLY_PERIOD;
  MONTHLY_PERIOD = MONTHLY_PERIOD;
  WEEKLY_PERIOD = WEEKLY_PERIOD;
  selectedTimeScale: string = "";
  serieValues: { [key: number]: Array<any> } = {};
  // selectedResponsablesSubscription: Subscription = new Subscription;

  constructor(private kpiWidgetService: KpiWidgetService,
    private responsableService: ResponsableService
  ) { }

  ngOnInit() {

    this.selectedTimeScale = ANNUALLY_PERIOD;
    this.changeTimeScale(this.selectedTimeScale);
    this.selectWidgetToDisplay(this.kpiCrms[0]);
    this.selectedKpiCrm = this.kpiCrms[0];
  }

  changeTimeScale(timeScale: string): void {
    this.selectedTimeScale = timeScale;
    this.kpiWidgetService.getKpiWidgetsByPage(this.selectedDisplayedPageCode, this.selectedTimeScale, this.selectedResponsables).subscribe(response => {
      if (response) {
        this.kpiCrms = response;
      }
    });
  }

  selectWidgetToDisplay(kpiWidget: KpiWidgetDto) {
    this.selectedKpiCrm = undefined;
    setTimeout(() => {
      this.selectedKpiCrm = kpiWidget;
      //TODO compute LabelType Echart from Unit
      // kpiWidget.labelType = LABEL_TYPE_DATETIME;
      if (this.selectedKpiCrm && !this.serieValues[this.selectedKpiCrm.idKpi]) {
        //TODO limiter le stockage en tableau à 3-5 kpi pour les perf navigateur
        this.serieValues[this.selectedKpiCrm.idKpi] = [];
        this.kpiWidgetService.getKpiWidgetSerieValues(this.selectedKpiCrm, this.selectedResponsables).subscribe(response => {
          if (response && this.selectedKpiCrm) {
            this.serieValues[this.selectedKpiCrm.idKpi].push(response);
          }
        });
      }
    }, 0);
  }
}

