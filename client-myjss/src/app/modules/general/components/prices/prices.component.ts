import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Meta, Title } from '@angular/platform-browser';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { GtmService } from '../../../main/services/gtm.service';
import { FormSubmitPayload, PageInfo } from '../../../main/services/GtmPayload';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { MailService } from '../../services/mail.service';

@Component({
  selector: 'prices',
  templateUrl: './prices.component.html',
  styleUrls: ['./prices.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericInputComponent]
})
export class PricesComponent implements OnInit {

  isConditionAccepted: boolean = false;

  firstName: string = "";
  lastName: string = "";
  phoneNumber: string = "";
  mail: string = "";
  pricesForm!: FormGroup;

  constructor(private formBuilder: FormBuilder,
    private mailService: MailService,
    private appService: AppService,
    private gtmService: GtmService,
    private titleService: Title,
    private meta: Meta,
  ) { }

  ngOnInit() {
    this.meta.updateTag({ name: 'description', content: "Obtenez une tarification claire et adaptée à vos besoins en formalités légales. Remplissez notre formulaire pour recevoir votre tarif personnalisé par MyJSS." });
    this.titleService.setTitle("Demande de tarif - MyJSS");
    this.pricesForm = this.formBuilder.group({});
  }

  trackFormPrice() {
    this.gtmService.trackFormSubmit(
      {
        form: { type: 'Demandez nos tarifs' },
        page: {
          type: 'general',
          name: 'prices'
        } as PageInfo
      } as FormSubmitPayload
    );
  }

  getPricesByMail(event: any) {
    if (!this.firstName || !this.lastName || !this.mail || !this.phoneNumber) {
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
