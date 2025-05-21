import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AppService } from '../../../../libs/app.service';
import { MailService } from '../../services/mail.service';

@Component({
  selector: 'prices',
  templateUrl: './prices.component.html',
  styleUrls: ['./prices.component.css'],
  standalone: false
})
export class PricesComponent implements OnInit {

  isConditionAccepted: boolean = false;

  firstName: string = "";
  lastName: string = "";
  phoneNumber: string = "";
  mail: string = "";
  pricesForm = this.formBuilder.group({});

  constructor(private formBuilder: FormBuilder,
    private mailService: MailService,
    private appService: AppService
  ) { }

  ngOnInit() {
  }

  getPricesByMail(event: any) {
    if (!this.firstName || !this.lastName || !this.mail) {
      return;
    }

    if (!this.isConditionAccepted) {
      this.appService.displayToast("Merci d'accepter les conditions", true, "Une erreur s’est produite...", 3000);
      return;
    }


    this.mailService.subscribePrices(this.mail, this.firstName, this.lastName, this.phoneNumber).subscribe(response => {
      if (response) {
        this.appService.displayToast("Vous allez recevoir un mail avec les tarifs.", false, "Demande validée", 3000);
        this.pricesForm.reset();
        this.firstName = "";
        this.lastName = "";
        this.phoneNumber = "";
        this.mail = "";
        this.isConditionAccepted = false;
      }
    });
  }
}
