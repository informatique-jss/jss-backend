import { Component, OnInit, ViewChild } from '@angular/core';
import { provideEchartsCore } from 'ngx-echarts';
import { Subscription } from 'rxjs';
import { TIERS_BUSINESS_DISPLAY } from '../../../../libs/Constants';
import { KpiGenericComponent } from '../../../main/components/kpi-generic/kpi-generic.component';
import { Responsable } from '../../../profile/model/Responsable';
import { ResponsableService } from '../../services/responsable.service';
import { echarts } from './../../../reporting/components/reporting-chart/echarts-config';

@Component({
  selector: 'responsables-business-kpi',
  providers: [provideEchartsCore({ echarts })],
  imports: [
    KpiGenericComponent
  ],
  standalone: true,
  templateUrl: './responsables-business-kpi.component.html',
  styleUrls: ['./responsables-business-kpi.component.css']
})
export class ResponsablesBusinessKpiComponent implements OnInit {
  TIERS_BUSINESS_DISPLAY = TIERS_BUSINESS_DISPLAY;
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
