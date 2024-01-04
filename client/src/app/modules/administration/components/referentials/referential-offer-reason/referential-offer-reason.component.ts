import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { OfferReasonService } from 'src/app/modules/miscellaneous/services/offer.reason.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';
import { OfferReason } from 'src/app/modules/miscellaneous/model/OfferReason';

@Component({
  selector: 'referential-offer-reason',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialOfferReasonComponent extends GenericReferentialComponent<OfferReason> implements OnInit {
  constructor(private offerReasonService: OfferReasonService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<OfferReason> {
    return this.offerReasonService.addOrUpdateOfferReason(this.selectedEntity!);
  }
  getGetObservable(): Observable<OfferReason[]> {
    return this.offerReasonService.getOfferReasons();
  }
}
