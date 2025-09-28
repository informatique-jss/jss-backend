import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AssoReportingDashboardWidget } from 'src/app/modules/reporting/model/AssoReportingDashboardWidget';
import { ReportingDashboard } from 'src/app/modules/reporting/model/ReportingDashboard';
import { ReportingDashboardService } from 'src/app/modules/reporting/services/reporting.dashboard.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-reporting-dashboard',
  templateUrl: './referential-reporting-dashboard.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialReportingDashboardComponent extends GenericReferentialComponent<ReportingDashboard> implements OnInit {
  constructor(private reportingDashboardService: ReportingDashboardService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  deleteIndex = 0;

  getAddOrUpdateObservable(): Observable<ReportingDashboard> {
    return this.reportingDashboardService.addOrUpdateReportingDashboard(this.selectedEntity!);
  }
  getGetObservable(): Observable<ReportingDashboard[]> {
    return this.reportingDashboardService.getReportingDashboards();
  }

  deleteWidgetAsso(assoReportingDashboardWidgets: AssoReportingDashboardWidget) {
    this.deleteIndex++;
    if (this.selectedEntity && this.selectedEntity.assoReportingDashboardWidgets)
      for (let i = 0; i < this.selectedEntity.assoReportingDashboardWidgets.length; i++)
        if (this.selectedEntity.assoReportingDashboardWidgets[i].reportingWidget.id == assoReportingDashboardWidgets.reportingWidget.id)
          this.selectedEntity.assoReportingDashboardWidgets.splice(i, 1);
  }

  addWidgetAsso() {
    if (this.selectedEntity)
      if (!this.selectedEntity.assoReportingDashboardWidgets)
        this.selectedEntity.assoReportingDashboardWidgets = [] as Array<AssoReportingDashboardWidget>;
    this.selectedEntity?.assoReportingDashboardWidgets.push({} as AssoReportingDashboardWidget);
  }
}
