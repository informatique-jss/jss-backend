import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { IncidentResponsibility } from 'src/app/modules/reporting/model/IncidentResponsibility';
import { IncidentResponsibilityService } from 'src/app/modules/reporting/services/incident.responsibility.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-incident-responsibility',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialIncidentResponsibilityComponent extends GenericReferentialComponent<IncidentResponsibility> implements OnInit {
  constructor(private incidentResponsibilityService: IncidentResponsibilityService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<IncidentResponsibility> {
    return this.incidentResponsibilityService.addOrUpdateIncidentResponsibility(this.selectedEntity!);
  }
  getGetObservable(): Observable<IncidentResponsibility[]> {
    return this.incidentResponsibilityService.getIncidentResponsibilities();
  }
}
