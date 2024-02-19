import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';
import { SalesReclamationOrigin } from 'src/app/modules/miscellaneous/model/SalesReclamationOrigin';
import { SalesReclamationOriginService } from 'src/app/modules/tiers/services/sales.reclamation.origin.service';

@Component({
  selector: 'referential-reclamation-origin',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialReclamationOriginComponent extends GenericReferentialComponent<SalesReclamationOrigin> implements OnInit {
  constructor(private salesReclamationOrigin: SalesReclamationOriginService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<SalesReclamationOrigin> {
    return this.salesReclamationOrigin.addOrUpdateReclamationOrigin(this.selectedEntity!);
  }

  getGetObservable(): Observable<SalesReclamationOrigin[]> {
    return this.salesReclamationOrigin.getReclamationOrigins();
  }
}
