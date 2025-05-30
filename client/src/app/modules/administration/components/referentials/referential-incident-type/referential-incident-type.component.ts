import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { IncidentType } from 'src/app/modules/reporting/model/IncidentType';
import { IncidentTypeService } from 'src/app/modules/reporting/services/incident.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-incident-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialIncidentTypeComponent extends GenericReferentialComponent<IncidentType> implements OnInit {
  constructor(private incidentTypeService: IncidentTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<IncidentType> {
    return this.incidentTypeService.addOrUpdateIncidentType(this.selectedEntity!);
  }
  getGetObservable(): Observable<IncidentType[]> {
    return this.incidentTypeService.getIncidentTypes();
  }
}
