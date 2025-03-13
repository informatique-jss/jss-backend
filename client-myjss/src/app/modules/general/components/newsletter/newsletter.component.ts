import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';
import { validateEmail } from '../../../../libs/CustomFormsValidatorsHelper';
import { CommunicationPreferencesService } from '../../../my-account/services/communication.preference.service';

@Component({
  selector: 'newsletter',
  templateUrl: './newsletter.component.html',
  styleUrls: ['./newsletter.component.css']
})
export class NewsletterComponent implements OnInit {

  mail: string = '';

  constructor(
    private communicationPreferencesService: CommunicationPreferencesService,
    private appService: AppService,
  ) { }

  ngOnInit() {
  }


  registerEmail(mailToRegister: string) {
    // Email verifications
    if (!mailToRegister) {
      this.appService.displayToast("Merci de renseigner une adresse e-mail.", true, "Une erreur s’est produite...", 3000);
      return;
    }

    if (!validateEmail(mailToRegister)) {
      this.appService.displayToast("Impossible de finaliser votre inscription. Vérifiez votre adresse e-mail et réessayez.", true, "Une erreur s’est produite...", 3000);
      return;
    }

    this.communicationPreferencesService.subscribeToCorporateNewsletter(mailToRegister).subscribe();
  }
}
