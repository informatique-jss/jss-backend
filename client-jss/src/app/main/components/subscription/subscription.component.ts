import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { ANNUAL_SUBSCRIPTION, MONTHLY_SUBSCRIPTION } from '../../model/Subscription';
import { GenericToggleComponent } from '../generic-toggle/generic-toggle.component';

@Component({
  selector: 'subscription',
  templateUrl: './subscription.component.html',
  styleUrls: ['./subscription.component.css'],
  imports: [SHARED_IMPORTS, GenericToggleComponent],
  standalone: true
})
export class SubscriptionComponent implements OnInit {

  isMonthlySubscription: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private appService: AppService,
  ) { }

  subscriptionForm!: FormGroup;

  ngOnInit() {
    this.subscriptionForm = this.formBuilder.group({});
  }

  subscribe(event: any, isMonthly: boolean, isPriceReduction: boolean, idArticle: number | null) {
    let subscriptionType: string = "";
    if (isMonthly) {
      subscriptionType = MONTHLY_SUBSCRIPTION;
    } else {
      subscriptionType = ANNUAL_SUBSCRIPTION;
    }

    this.appService.openMyJssRoute(event, "/quotation/" + subscriptionType + "/" + isPriceReduction + "/" + idArticle, true);
  }
}
