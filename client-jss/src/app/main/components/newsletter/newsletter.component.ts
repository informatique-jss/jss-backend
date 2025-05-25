import { Component, OnInit } from '@angular/core';
import { validateEmail } from '../../../libs/CustomFormsValidatorsHelper';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { CommunicationPreferencesService } from '../../services/communication.preference.service';

@Component({
  selector: 'newsletter',
  templateUrl: './newsletter.component.html',
  styleUrls: ['./newsletter.component.css'],
  imports: [SHARED_IMPORTS],
  standalone: true
})
export class NewsletterComponent implements OnInit {

  mail: string = '';
  isSent: boolean = false;

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

    this.communicationPreferencesService.subscribeToNewspaperNewsletter(mailToRegister).subscribe(res => {
      this.mail = '';
      this.isSent = true;
    });
  }
}
