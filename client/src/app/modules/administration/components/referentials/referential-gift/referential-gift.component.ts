import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { Gift } from 'src/app/modules/miscellaneous/model/Gift';
import { GiftService } from 'src/app/modules/miscellaneous/services/gift.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-gift',
  templateUrl: 'referential-gift.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialGiftComponent extends GenericReferentialComponent<Gift> implements OnInit {
  constructor(private giftService: GiftService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<Gift> {
    return this.giftService.addOrUpdateGift(this.selectedEntity!);
  }
  getGetObservable(): Observable<Gift[]> {
    return this.giftService.getGifts();
  }
}
