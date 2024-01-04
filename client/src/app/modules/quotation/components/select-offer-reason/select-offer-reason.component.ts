import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { OfferReasonService } from 'src/app/modules/miscellaneous/services/offer.reason.service';
import { GenericSelectComponent } from '../../../miscellaneous/components/forms/generic-select/generic-select.component';
import { OfferReason } from 'src/app/modules/miscellaneous/model/OfferReason';

@Component({
  selector: 'select-offer-reason',
  templateUrl: '../../../miscellaneous/components/forms/generic-select/generic-select.component.html',
  styleUrls: ['../../../miscellaneous/components/forms/generic-select/generic-select.component.css']
})
  export class SelectOfferReasonComponent extends GenericSelectComponent<OfferReason> implements OnInit {

  types: OfferReason[] = [] as Array<OfferReason>;

  constructor(private formBuild: UntypedFormBuilder, private offerReasonService: OfferReasonService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  ngOnInit(){
    this.initTypes();
  }

  initTypes(): void {
    this.offerReasonService.getOfferReasons().subscribe(response => {
      this.types = response;
    });
  }
}
