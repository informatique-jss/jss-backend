import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { ReportingWorkingTable } from 'src/app/modules/reporting/model/ReportingWorkingTable';
import { ReportingWorkingTableService } from 'src/app/modules/reporting/services/reporting.working.table.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-reporting-working-table',
  templateUrl: './referential-reporting-working-table.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialReportingWorkingTableComponent extends GenericReferentialComponent<ReportingWorkingTable> implements OnInit {
  constructor(private reportingWorkingTableService: ReportingWorkingTableService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<ReportingWorkingTable> {
    return this.reportingWorkingTableService.addOrUpdateReportingWorkingTable(this.selectedEntity!);
  }
  getGetObservable(): Observable<ReportingWorkingTable[]> {
    return this.reportingWorkingTableService.getReportingWorkingTables();
  }
}
