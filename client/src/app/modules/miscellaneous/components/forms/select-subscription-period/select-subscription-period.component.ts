
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { SubscriptionPeriodType } from 'src/app/modules/tiers/model/SubscriptionPeriodType';
import { SubscriptionPeriodTypeService } from 'src/app/modules/tiers/services/subscription.period.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-subscription-period',
  templateUrl: './select-subscription-period.component.html',
  styleUrls: ['./select-subscription-period.component.css']
})
export class SelectSubscriptionPeriodComponent extends GenericSelectComponent<SubscriptionPeriodType> implements OnInit {

  types: SubscriptionPeriodType[] = [] as Array<SubscriptionPeriodType>;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: FormBuilder, private subscriptionPeriodTypeService: SubscriptionPeriodTypeService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.subscriptionPeriodTypeService.getSubscriptionPeriodTypes().subscribe(response => {
      this.types = response;
    })
  }
}
