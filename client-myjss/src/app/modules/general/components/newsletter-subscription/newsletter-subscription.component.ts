import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AppService } from '../../../../libs/app.service';
import { validateEmail } from '../../../../libs/CustomFormsValidatorsHelper';
import { CommunicationPreferencesService } from '../../../my-account/services/communication.preference.service';

@Component({
  selector: 'newsletter-subscription',
  templateUrl: './newsletter-subscription.component.html',
  styleUrls: ['./newsletter-subscription.component.css'],
  standalone: false
})
export class NewsletterSubscriptionComponent implements OnInit {

  mail: string = '';

  constructor(
    private communicationPreferencesService: CommunicationPreferencesService,
    private appService: AppService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit() {
  }
  newsletterForm = this.formBuilder.group({});
  registerEmail(mailToRegister: string) {
    if (!mailToRegister) {
      return;
    }

    if (!validateEmail(mailToRegister)) {
      this.appService.displayToast("Impossible de finaliser votre inscription. Vérifiez votre adresse e-mail et réessayez.", true, "Une erreur s’est produite...", 3000);
      return;
    }

    this.communicationPreferencesService.subscribeToCorporateNewsletter(mailToRegister).subscribe(response => {
      if (response) {
        this.newsletterForm.reset();
        this.mail = "";
      }
    });
  }
}
