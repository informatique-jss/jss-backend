import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { validateEmail, validateFrenchPhone, validateInternationalPhone } from '../../../../libs/CustomFormsValidatorsHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { Mail } from '../../../general/model/Mail';
import { AppService } from '../../../main/services/app.service';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { WebinarParticipant } from '../../model/WebinarParticipant';
import { WebinarParticipantService } from '../../services/webinar.participant.service';

@Component({
  selector: 'webinars',
  templateUrl: './webinars.component.html',
  styleUrls: ['./webinars.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericInputComponent]
})
export class WebinarsComponent implements OnInit {
  webinarParticipant: WebinarParticipant = { mail: {} as Mail } as WebinarParticipant;
  isConditionAccepted: boolean = false;
  replayMail: string = "";

  webinarsForm!: FormGroup;

  @ViewChild('formRef') formRef: ElementRef<HTMLInputElement> | undefined;

  constructor(private webinarParticipantService: WebinarParticipantService,
    private appService: AppService,
    private formBuilder: FormBuilder,) { }

  ngOnInit() {
    this.webinarsForm = this.formBuilder.group({});
  }
  validateEmail = validateEmail;
  validateFrenchPhone = validateFrenchPhone;
  validateInternationalPhone = validateInternationalPhone;


  subscribeWebinar(event: any): any {
    if (!this.webinarParticipant.firstname || !this.webinarParticipant.lastname || !this.webinarParticipant.mail.mail) {
      this.appService.displayToast("Merci de remplir les champs obligatoires", true, "Une erreur s’est produite...", 3000);
      return;
    }
    if (!this.isConditionAccepted) {
      this.appService.displayToast("Merci d'accepter les conditions", true, "Une erreur s’est produite...", 3000);
      return;
    }
    this.webinarParticipantService.subscribeWebinar(this.webinarParticipant).subscribe(response => {
      if (response) {
        this.appService.displayToast("Vous allez recevoir un mail de confirmation", false, "Inscription validée", 3000);
        this.webinarsForm.reset();
        Object.values(this.webinarsForm.controls).forEach(control => {
          control.markAsPristine();
          control.markAsUntouched();
          control.updateValueAndValidity();
        });
        this.isConditionAccepted = false;
        this.webinarParticipant = { mail: {} as Mail } as WebinarParticipant;
        if (this.formRef)
          this.formRef.nativeElement.classList.remove("was-validated");
      }
    });
  }

  sendReplay(): any {
    if (this.replayMail && validateEmail(this.replayMail))
      this.webinarParticipantService.subscribeWebinarReplay(this.replayMail).subscribe(response => {
        if (response) {
          this.appService.displayToast("Vous allez recevoir un mail de confirmation", false, "Demande validée", 3000);
          this.webinarsForm.reset();
          this.isConditionAccepted = false;
          this.webinarParticipant = { mail: {} as Mail } as WebinarParticipant;
          this.replayMail = "";
          if (this.formRef)
            this.formRef.nativeElement.classList.remove("was-validated");
        }
      });
  }
}

