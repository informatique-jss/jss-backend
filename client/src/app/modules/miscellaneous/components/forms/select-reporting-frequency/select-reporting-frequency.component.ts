import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ReportingUpdateFrequencyService } from 'src/app/modules/reporting/services/reporting.update.frequency.service';
import { AppService } from 'src/app/services/app.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-reporting-frequency',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectReportingFrequencyComponent extends GenericSelectComponent<string> implements OnInit {

  types: string[] = [] as Array<string>;

  constructor(private formBuild: UntypedFormBuilder, private reportingUpdateFrequencyService: ReportingUpdateFrequencyService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.reportingUpdateFrequencyService.getReportingUpdateFrequencies().subscribe(response => {
      this.types = response.sort((a, b) => a.label.localeCompare(b.label)).map(f => f.code);
    })
  }

}
