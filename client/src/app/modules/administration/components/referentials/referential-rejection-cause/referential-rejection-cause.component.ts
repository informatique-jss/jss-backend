import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { RejectionCause } from 'src/app/modules/quotation/model/RejectionCause';
import { RejectionCauseService } from 'src/app/modules/quotation/services/rejection.cause.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
    selector: 'referential-rejection-cause',
    templateUrl: './../generic-referential/generic-referential.component.html',
    styleUrls: ['./../generic-referential/generic-referential.component.css'],
    standalone: false
})
export class ReferentialRejectionCauseComponent extends GenericReferentialComponent<RejectionCause> implements OnInit {
  constructor(private rejectionCauseService: RejectionCauseService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<RejectionCause> {
    return this.rejectionCauseService.addOrUpdateRejectionCause(this.selectedEntity!);
  }
  getGetObservable(): Observable<RejectionCause[]> {
    return this.rejectionCauseService.getRejectionCauses();
  }
}
