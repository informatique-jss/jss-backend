import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Meta, Title } from '@angular/platform-browser';
import { validateEmail, validateFrenchPhone, validateInternationalPhone } from '../../../../libs/CustomFormsValidatorsHelper';
import { LiteralDatePipe } from '../../../../libs/LiteralDatePipe';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { Mail } from '../../../general/model/Mail';
import { AppService } from '../../../main/services/app.service';
import { GtmService } from '../../../main/services/gtm.service';
import { FormSubmitPayload, PageInfo } from '../../../main/services/GtmPayload';
import { PlatformService } from '../../../main/services/platform.service';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { GenericTextareaComponent } from '../../../miscellaneous/components/forms/generic-textarea/generic-textarea.component';
import { Webinar } from '../../model/Webinar';
import { WebinarParticipant } from '../../model/WebinarParticipant';
import { WebinarParticipantService } from '../../services/webinar.participant.service';
import { WebinarService } from '../../services/webinar.service';

@Component({
  selector: 'webinars',
  templateUrl: './webinars.component.html',
  styleUrls: ['./webinars.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericInputComponent, LiteralDatePipe, GenericTextareaComponent]
})
export class WebinarsComponent implements OnInit {
  webinarParticipant: WebinarParticipant = { mail: {} as Mail } as WebinarParticipant;
  isConditionAccepted: boolean = false;
  replayMail: string = "";
  displayForm: boolean = true;

  webinarToDisplay: Webinar | undefined;
  lastWebinar: Webinar | undefined;

  webinarsForm!: FormGroup;

  @ViewChild('formRef') formRef: ElementRef<HTMLInputElement> | undefined;

  constructor(private webinarParticipantService: WebinarParticipantService,
    private webinarService: WebinarService,
    private appService: AppService,
    private gtmService: GtmService,
    private titleService: Title, private meta: Meta,
    private platformService: PlatformService,
    private formBuilder: FormBuilder,) { }

  ngOnInit() {
    this.titleService.setTitle("Webinaires - MyJSS");
    this.meta.updateTag({ name: 'description', content: "Montez en compétence sur les sujets juridiques d'entreprise. Participez à nos webinaires animés par les experts MyJSS et profitez de conseils pratiques en direct." });
    this.webinarsForm = this.formBuilder.group({});
    this.webinarService.getLastWebinar().subscribe(last => {
      this.lastWebinar = last;
    })

    this.webinarService.getNextWebinar().subscribe(next => {
      this.webinarToDisplay = next;
    })
  }
  validateEmail = validateEmail;
  validateFrenchPhone = validateFrenchPhone;
  validateInternationalPhone = validateInternationalPhone;


  subscribeWebinar(event: any): any {
    if (!this.webinarParticipant.firstname || !this.webinarParticipant.lastname || !this.webinarParticipant.mail.mail || !this.webinarParticipant.phoneNumber) {
      this.appService.displayToast("Merci de remplir les champs obligatoires", true, "Une erreur s’est produite...", 3000);
      return;
    }
    if (!this.isConditionAccepted) {
      this.appService.displayToast("Merci d'accepter les conditions", true, "Une erreur s’est produite...", 3000);
      return;
    }
    this.webinarParticipantService.subscribeWebinar(this.webinarParticipant).subscribe(response => {
      this.trackFormWebinarRequest();
      if (response) {
        this.appService.displayToast("Vous allez recevoir un mail de confirmation", false, "Inscription validée", 3000);
        this.webinarsForm.reset();
        this.isConditionAccepted = false;
        this.webinarParticipant = { mail: {} as Mail } as WebinarParticipant;
        this.displayForm = false;
        setTimeout(() => this.displayForm = true, 0);
      }
    });
  }

  ngAfterViewInit(): void {
    if (this.platformService.getNativeDocument())
      import('jarallax').then(module => {
        module.jarallax(this.platformService.getNativeDocument()!.querySelectorAll('.jarallax'), {
          speed: 0.5
        });
      });
  }

  trackFormWebinarRequest() {
    this.gtmService.trackFormSubmit(
      {
        form: { type: "S'inscrire" },
        page: {
          type: 'tools',
          name: 'webinars'
        } as PageInfo
      } as FormSubmitPayload
    );
  }

  trackFormWebinarReplayRequest() {
    this.gtmService.trackFormSubmit(
      {
        form: { type: "Voir le replay" },
        page: {
          type: 'tools',
          name: 'webinars'
        } as PageInfo
      } as FormSubmitPayload
    );
  }

  sendReplay(): any {
    if (this.replayMail && validateEmail(this.replayMail))
      this.webinarParticipantService.subscribeWebinarReplay(this.replayMail).subscribe(response => {
        this.trackFormWebinarReplayRequest();
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

