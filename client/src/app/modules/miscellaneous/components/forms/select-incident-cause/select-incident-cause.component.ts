import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { IncidentCauseService } from 'src/app/modules/reporting/services/incident.cause.service';
import { AppService } from 'src/app/services/app.service';
import { IncidentCause } from '../../../../reporting/model/IncidentCause';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-incident-cause',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectIncidentCauseComponent extends GenericSelectComponent<IncidentCause> implements OnInit {

  @Input() types: IncidentCause[] = [] as Array<IncidentCause>;

  constructor(private formBuild: UntypedFormBuilder,
    private incidentCauseService: IncidentCauseService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.incidentCauseService.getIncidentCauses().subscribe(response => {
      this.types = response;
    })
  }
}
