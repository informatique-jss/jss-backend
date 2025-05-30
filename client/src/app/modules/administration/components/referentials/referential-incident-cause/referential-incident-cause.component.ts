import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { IncidentCause } from 'src/app/modules/reporting/model/IncidentCause';
import { IncidentCauseService } from 'src/app/modules/reporting/services/incident.cause.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-incident-cause',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialIncidentCauseComponent extends GenericReferentialComponent<IncidentCause> implements OnInit {
  constructor(private incidentCauseService: IncidentCauseService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<IncidentCause> {
    return this.incidentCauseService.addOrUpdateIncidentCause(this.selectedEntity!);
  }
  getGetObservable(): Observable<IncidentCause[]> {
    return this.incidentCauseService.getIncidentCauses();
  }
}
