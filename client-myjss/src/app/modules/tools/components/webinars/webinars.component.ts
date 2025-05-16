import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AppService } from '../../../../libs/app.service';
import { validateEmail, validateFrenchPhone, validateInternationalPhone } from '../../../../libs/CustomFormsValidatorsHelper';
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
  webinarParticipant: WebinarParticipant = { mail: {} as Mail } as WebinarParticipant;
  isConditionAccepted: boolean = false;

  constructor(private webinarParticipantService: WebinarParticipantService,
    private appService: AppService,
    private formBuilder: FormBuilder,) { }

  ngOnInit() {
  }
  validateEmail = validateEmail;
  validateFrenchPhone = validateFrenchPhone;
  validateInternationalPhone = validateInternationalPhone;
  webinarsForm = this.formBuilder.group({});

  subscribeWebinar(event: any): any {
    if (!this.webinarParticipant.firstname || !this.webinarParticipant.lastname || !this.webinarParticipant.mail.mail) {
      this.appService.displayToast("Merci de remplir les champs obligatoires", true, "Une erreur s’est produite...", 3000);
    }
    if (!this.isConditionAccepted) {
      this.appService.displayToast("Merci d'accepter les conditions", true, "Une erreur s’est produite...", 3000);
      return;
    }
    this.webinarParticipantService.subscribeWebinar(this.webinarParticipant).subscribe(response => {
      if (response) {
        this.appService.displayToast("Vous allez recevoir un mail de confirmation", false, "Inscription validée", 3000);
        this.webinarsForm.reset();
        this.isConditionAccepted = false;
        this.webinarParticipant = { mail: {} as Mail } as WebinarParticipant;
      }
    });
  }
}
