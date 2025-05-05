import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AppService } from '../../../../libs/app.service';
import { validateEmail } from '../../../../libs/CustomFormsValidatorsHelper';
import { Mail } from '../../../profile/model/Mail';
import { WebinarParticipant } from '../../model/WebinarParticipant';
import { WebinarParticipantService } from '../../services/webinar.participant.service';

@Component({
  selector: 'webinars',
  templateUrl: './webinars.component.html',
  styleUrls: ['./webinars.component.css'],
  standalone: false
})
export class WebinarsComponent implements OnInit {
  webinarParticipant: WebinarParticipant = {} as WebinarParticipant;
  isConditionAccepted: boolean = false;

  constructor(private webinarParticipantService: WebinarParticipantService,
    private appService: AppService,
    private formBuilder: FormBuilder,) { }

  ngOnInit() {
    this.webinarParticipant.mail = {} as Mail;
  }

  subscribeWebinar(event: any): any {
    if (!this.webinarParticipant.phoneNumber || !this.webinarParticipant.firstname || !this.webinarParticipant.lastname) {
      this.appService.displayToast("Merci de renseigner tous les champs.", true, "Une erreur s’est produite...", 3000);
      return;
    }
    if (!this.isConditionAccepted) {
      this.appService.displayToast("Merci d'accepter les conditions.", true, "Une erreur s’est produite...", 3000);
      return;
    }
    if (!validateEmail(this.webinarParticipant.mail.mail)) {
      this.appService.displayToast("Impossible de finaliser votre inscription. Vérifiez votre adresse e-mail et réessayez.", true, "Une erreur s’est produite...", 3000);
      return;
    }
    this.webinarParticipantService.subscribeWebinar(this.webinarParticipant).subscribe(response => {
      if (response)
        this.appService.displayToast("Vous allez recevoir un mail de confirmation.", false, "Inscription validée", 3000);
    });
  }

  webinarsForm = this.formBuilder.group({});
}
