import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';
import { SalesComplainOrigin } from 'src/app/modules/miscellaneous/model/SalesComplainOrigin';
import { SalesComplainOriginService } from 'src/app/modules/tiers/services/sales.complain.origin.service';

@Component({
  selector: 'referential-complain-origin',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialComplainOriginComponent extends GenericReferentialComponent<SalesComplainOrigin> implements OnInit {
  constructor(private salesComplainOrigin: SalesComplainOriginService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<SalesComplainOrigin> {
    return this.salesComplainOrigin.addOrUpdateComplainOrigin(this.selectedEntity!);
  }

  getGetObservable(): Observable<SalesComplainOrigin[]> {
    return this.salesComplainOrigin.getComplainOrigins();
  }
}
