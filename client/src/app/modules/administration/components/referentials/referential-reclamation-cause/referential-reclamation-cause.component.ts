import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';
import { SalesReclamationCause } from 'src/app/modules/miscellaneous/model/SalesReclamationCause';
import { SalesReclamationCauseService } from 'src/app/modules/tiers/services/sales.reclamation.cause.service';

@Component({
  selector: 'referential-reclamation-cause',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialReclamationCauseComponent extends GenericReferentialComponent<SalesReclamationCause> implements OnInit {
  constructor(private salesReclamationCause: SalesReclamationCauseService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<SalesReclamationCause> {
    return this.salesReclamationCause.addOrUpdateReclamationCause(this.selectedEntity!);
  }
  getGetObservable(): Observable<SalesReclamationCause[]> {
    return this.salesReclamationCause.getReclamationCauses();
  }
}
