import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { SpecialOffer } from 'src/app/modules/miscellaneous/model/SpecialOffer';
import { SpecialOfferService } from 'src/app/modules/miscellaneous/services/special.offer.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-special-offer',
  templateUrl: 'referential-special-offer.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialSpecialOfferComponent extends GenericReferentialComponent<SpecialOffer> implements OnInit {
  constructor(private specialOfferService: SpecialOfferService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<SpecialOffer> {
    return this.specialOfferService.addOrUpdateSpecialOffer(this.selectedEntity!);
  }
  getGetObservable(): Observable<SpecialOffer[]> {
    return this.specialOfferService.getSpecialOffers();
  }
}
