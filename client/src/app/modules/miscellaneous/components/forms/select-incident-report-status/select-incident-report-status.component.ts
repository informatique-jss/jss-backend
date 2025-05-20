import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { IncidentReportStatusService } from 'src/app/modules/reporting/services/incident.report.status.service';
import { AppService } from 'src/app/services/app.service';
import { IncidentReportStatus } from '../../../../reporting/model/IncidentReportStatus';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-incident-report-status',
  templateUrl: './../generic-select/generic-multiple-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectIncidentReportStatusComponent extends GenericMultipleSelectComponent<IncidentReportStatus> implements OnInit {

  @Input() types: IncidentReportStatus[] = [] as Array<IncidentReportStatus>;

  constructor(private formBuild: UntypedFormBuilder,
    private incidentReportStatusService: IncidentReportStatusService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.incidentReportStatusService.getIncidentReportStatusList().subscribe(response => {
      this.types = response;
    })
  }
}
