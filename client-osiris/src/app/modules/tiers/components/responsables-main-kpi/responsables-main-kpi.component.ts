import { Component, OnInit, ViewChild } from '@angular/core';
import { provideEchartsCore } from 'ngx-echarts';
import { Subscription } from 'rxjs';
import { TIERS_MAIN_DISPLAY } from '../../../../libs/Constants';
import { KpiGenericComponent } from '../../../main/components/kpi-generic/kpi-generic.component';
import { Responsable } from '../../../profile/model/Responsable';
import { ResponsableService } from '../../services/responsable.service';
import { echarts } from './../../../reporting/components/reporting-chart/echarts-config';

@Component({
  selector: 'responsables-main-kpi',
  providers: [provideEchartsCore({ echarts })],
  imports: [
    KpiGenericComponent
  ],
  standalone: true,
  templateUrl: './responsables-main-kpi.component.html',
  styleUrls: ['./responsables-main-kpi.component.css']
})
export class ResponsablesMainKpiComponent implements OnInit {
  TIERS_MAIN_DISPLAY = TIERS_MAIN_DISPLAY;
  selectedResponsablesSubscription: Subscription = new Subscription;
  selectedResponsables: Responsable[] = [];
  @ViewChild('kpiComponent') kpiComponent!: KpiGenericComponent;

  constructor(private responsableService: ResponsableService,) { }

  ngOnInit() {
    this.selectedResponsablesSubscription = this.responsableService.getSelectedResponsables().subscribe(respos => {
      this.selectedResponsables = respos;

      if (this.kpiComponent) {
        this.kpiComponent.ngOnInit();
      }
    });
  }

}
