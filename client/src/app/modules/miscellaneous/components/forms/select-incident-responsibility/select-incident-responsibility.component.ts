import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { IncidentResponsibility } from 'src/app/modules/reporting/model/IncidentResponsibility';
import { AppService } from 'src/app/services/app.service';
import { IncidentResponsibilityService } from '../../../../reporting/services/incident.responsibility.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-incident-responsibility',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectIncidentResponsibilityComponent extends GenericSelectComponent<IncidentResponsibility> implements OnInit {

  @Input() types: IncidentResponsibility[] = [] as Array<IncidentResponsibility>;

  constructor(private formBuild: UntypedFormBuilder,
    private incidentResponsibilityService: IncidentResponsibilityService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.incidentResponsibilityService.getIncidentResponsibilities().subscribe(response => {
      this.types = response;
    })
  }
}
