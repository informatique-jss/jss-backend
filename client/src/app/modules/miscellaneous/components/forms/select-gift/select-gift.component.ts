import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Gift } from '../../../model/Gift';
import { GiftService } from '../../../services/gift.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';


@Component({
  selector: 'select-gift',
  templateUrl: './select-gift.component.html',
  styleUrls: ['./select-gift.component.css']
})
export class SelectGiftComponent extends GenericSelectComponent<Gift> implements OnInit {

  types: Gift[] = [] as Array<Gift>;

  constructor(private formBuild: UntypedFormBuilder, private giftService: GiftService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.giftService.getGifts().subscribe(response => {
      this.types = response;
    })
  }
}
