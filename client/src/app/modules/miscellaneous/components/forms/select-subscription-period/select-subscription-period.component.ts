
import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SubscriptionPeriodType } from 'src/app/modules/tiers/model/SubscriptionPeriodType';
import { SubscriptionPeriodTypeService } from 'src/app/modules/tiers/services/subscription.period.type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-subscription-period',
  templateUrl: './select-subscription-period.component.html',
  styleUrls: ['./select-subscription-period.component.css']
})
export class SelectSubscriptionPeriodComponent extends GenericSelectComponent<SubscriptionPeriodType> implements OnInit {

  types: SubscriptionPeriodType[] = [] as Array<SubscriptionPeriodType>;

  constructor(private formBuild: UntypedFormBuilder, private subscriptionPeriodTypeService: SubscriptionPeriodTypeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.subscriptionPeriodTypeService.getSubscriptionPeriodTypes().subscribe(response => {
      this.types = response;
    })
  }
}
