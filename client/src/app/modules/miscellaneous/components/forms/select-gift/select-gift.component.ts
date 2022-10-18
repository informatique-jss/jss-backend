import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
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

  constructor(private formBuild: UntypedFormBuilder, private giftService: GiftService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.giftService.getGifts().subscribe(response => {
      this.types = response;
    })
  }
}
