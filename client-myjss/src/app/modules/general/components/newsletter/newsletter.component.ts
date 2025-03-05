import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';
import { validateEmail } from '../../../../libs/CustomFormsValidatorsHelper';
import { NewsletterService } from '../../../my-account/services/newsletter.service';

@Component({
  selector: 'app-newsletter',
  templateUrl: './newsletter.component.html',
  styleUrls: ['./newsletter.component.css']
})
export class NewsletterComponent implements OnInit {

  email: string = '';

  constructor(
    private newsletterService: NewsletterService,
    private appService: AppService,
  ) { }

  ngOnInit() {
  }

  registerEmail(emailToRegister: string) {
    if (emailToRegister) {
      if (!validateEmail(emailToRegister)) {
        this.appService.displayToast("Impossible de finaliser votre inscription. Vérifiez votre adresse e-mail et réessayez.", true, "Une erreur s’est produite...", 3000);
        return;
      }
    } else {
      this.appService.displayToast("Merci de renseigner une adresse e-mail.", true, "Une erreur s’est produite...", 3000);
      return;
    }

    this.newsletterService.saveEmailForNewsletter(emailToRegister).subscribe(response => {
      if (response)
        this.appService.displayToast("Bienvenue ! Vous recevrez bientôt notre newsletter avec l\’essentiel de l\’actualité chaque semaine.", true, "Inscription confirmée !", 3000);
      this.email = '';
    })
  }
}
