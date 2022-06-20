import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Gift } from '../../../model/Gift';
import { GiftService } from '../../../services/gift.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';


@Component({
  selector: 'select-gift',
  templateUrl: './select-gift.component.html',
  styleUrls: ['./select-gift.component.css']
})
export class SelectGiftComponent extends GenericSelectComponent<Gift> implements OnInit {

  types: Gift[] = [] as Array<Gift>;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: FormBuilder, private giftService: GiftService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.giftService.getGifts().subscribe(response => {
      this.types = response;
    })
  }
}
