import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { SubscriptionPeriodType } from 'src/app/modules/tiers/model/SubscriptionPeriodType';
import { SubscriptionPeriodTypeService } from 'src/app/modules/tiers/services/subscription.period.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-subscription-period-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialSubscriptionPeriodTypeComponent extends GenericReferentialComponent<SubscriptionPeriodType> implements OnInit {
  constructor(private subscriptionPeriodTypeService: SubscriptionPeriodTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<SubscriptionPeriodType> {
    return this.subscriptionPeriodTypeService.addOrUpdateSubscriptionPeriodType(this.selectedEntity!);
  }
  getGetObservable(): Observable<SubscriptionPeriodType[]> {
    return this.subscriptionPeriodTypeService.getSubscriptionPeriodTypes();
  }
}
