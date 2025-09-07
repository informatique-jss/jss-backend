import { Component, OnInit } from '@angular/core';
import { validateEmail } from '../../../../libs/CustomFormsValidatorsHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { GtmService } from '../../../main/services/gtm.service';
import { FormSubmitPayload, PageInfo } from '../../../main/services/GtmPayload';
import { CommunicationPreferencesService } from '../../services/communication.preference.service';

@Component({
  selector: 'newsletter',
  templateUrl: './newsletter.component.html',
  styleUrls: ['./newsletter.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class NewsletterComponent implements OnInit {

  mail: string = '';

  constructor(
    private communicationPreferencesService: CommunicationPreferencesService,
    private appService: AppService,
    private gtmService: GtmService,
  ) { }

  ngOnInit() {
  }


  trackFormNewsletter() {
    this.gtmService.trackFormSubmit(
      {
        form: { type: "S'inscrire" },
        page: {
          type: 'general',
          name: 'newsletter'
        } as PageInfo
      } as FormSubmitPayload
    );
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

    this.communicationPreferencesService.subscribeToCorporateNewsletter(mailToRegister).subscribe(response => {
      this.trackFormNewsletter();
      this.appService.displayToast("Votre inscription a bien été prise en compte", false, "Inscription", 5000);
    });
  }
}
