import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { IncidentTypeService } from 'src/app/modules/reporting/services/incident.type.service';
import { AppService } from 'src/app/services/app.service';
import { IncidentType } from '../../../../reporting/model/IncidentType';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-incident-type',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectIncidentTypeComponent extends GenericSelectComponent<IncidentType> implements OnInit {

  @Input() types: IncidentType[] = [] as Array<IncidentType>;

  constructor(private formBuild: UntypedFormBuilder,
    private incidentTypeService: IncidentTypeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.incidentTypeService.getIncidentTypes().subscribe(response => {
      this.types = response;
    })
  }
}
