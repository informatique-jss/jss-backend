import { Component, OnChanges, OnInit, ViewChild } from '@angular/core';
import { provideEchartsCore } from 'ngx-echarts';
import { Subscription } from 'rxjs';
import { TIERS_HOME_DISPLAY } from '../../../../libs/Constants';
import { KpiGenericComponent } from "../../../main/components/kpi-generic/kpi-generic.component";
import { Responsable } from '../../../profile/model/Responsable';
import { ResponsableService } from '../../services/responsable.service';
import { echarts } from './../../../reporting/components/reporting-chart/echarts-config';

@Component({
  selector: 'responsables-home-kpi',
  providers: [provideEchartsCore({ echarts })],
  imports: [
    KpiGenericComponent
  ],
  standalone: true,
  templateUrl: './responsables-home-kpi.component.html',
})
export class ResponsablesHomeKpiComponent implements OnInit, OnChanges {

  selectedResponsablesSubscription: Subscription = new Subscription;
  selectedResponsables: Responsable[] = [];
  @ViewChild('kpiComponent') kpiComponent!: KpiGenericComponent;
  TIERS_HOME_DISPLAY = TIERS_HOME_DISPLAY;
  // TODO : first try to init chart after timeout
  // ngAfterViewInit() {
  //   setTimeout(() => {
  //     window.dispatchEvent(new Event('resize'));
  //   }, 250);
  // }

  constructor(
    private responsableService: ResponsableService,
  ) { }

  ngOnInit() {
    this.selectedResponsablesSubscription = this.responsableService.getSelectedResponsables().subscribe(respos => {
      this.selectedResponsables = respos;

      if (this.kpiComponent) {
        this.kpiComponent.ngOnInit();
      }
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
