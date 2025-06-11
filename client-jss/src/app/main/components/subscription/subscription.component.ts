import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { ANNUAL_SUBSCRIPTION, ENTERPRISE_ANNUAL_SUBSCRIPTION, MONTHLY_SUBSCRIPTION } from '../../model/Subscription';
import { GenericToggleComponent } from '../generic-toggle/generic-toggle.component';
import { NewsletterComponent } from "../newsletter/newsletter.component";

@Component({
  selector: 'subscription',
  templateUrl: './subscription.component.html',
  styleUrls: ['./subscription.component.css'],
  imports: [SHARED_IMPORTS, GenericToggleComponent, NewsletterComponent],
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

  subscribe(event: any, isMonthly: boolean, isPriceReduction: boolean, isEnteprise: boolean, idArticle: number | null) {
    let subscriptionType: string = "";
    if (isMonthly) {
      subscriptionType = MONTHLY_SUBSCRIPTION;
    } else {
      if (isEnteprise)
        subscriptionType = ENTERPRISE_ANNUAL_SUBSCRIPTION;
      else
        subscriptionType = ANNUAL_SUBSCRIPTION;

    }

    this.appService.openMyJssRoute(event, "/quotation/" + subscriptionType + "/" + isPriceReduction + "/" + idArticle, true);
  }

  openToast($event: MouseEvent) {
    this.appService.displayToast("Ce type d'abonnement n'est malheureusement pas encore disponible, nous faisons notre maximum pour vous le proposer au plus vite. N'hésitez pas à contacter nos équipes si vous souhaitez souscrire à plusieurs abonnements à la fois.", true, "Type d'abonnement indisponible", 10000);
  }
}