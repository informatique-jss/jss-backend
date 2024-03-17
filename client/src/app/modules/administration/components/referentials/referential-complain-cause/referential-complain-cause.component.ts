import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';
import { SalesComplainCause } from 'src/app/modules/miscellaneous/model/SalesComplainCause';
import { SalesComplainCauseService } from 'src/app/modules/tiers/services/sales.complain.cause.service';

@Component({
  selector: 'referential-complain-cause',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialComplainCauseComponent extends GenericReferentialComponent<SalesComplainCause> implements OnInit {
  constructor(private salesComplainCause: SalesComplainCauseService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<SalesComplainCause> {
    return this.salesComplainCause.addOrUpdateComplainCause(this.selectedEntity!);
  }
  getGetObservable(): Observable<SalesComplainCause[]> {
    return this.salesComplainCause.getComplainCauses();
  }
}
