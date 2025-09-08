import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Meta, Title } from '@angular/platform-browser';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { FormSubmitPayload, PageInfo } from '../../../main/services/GtmPayload';
import { AppService } from '../../../main/services/app.service';
import { GtmService } from '../../../main/services/gtm.service';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { MailService } from '../../services/mail.service';

@Component({
  selector: 'demo',
  templateUrl: './demo.component.html',
  styleUrls: ['./demo.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericInputComponent]
})
export class DemoComponent implements OnInit {
  isConditionAccepted: boolean = false;

  firstName: string = "";
  lastName: string = "";
  phoneNumber: string = "";
  mail: string = "";
  demoForm!: FormGroup;

  constructor(private formBuilder: FormBuilder,
    private mailService: MailService,
    private appService: AppService,
    private titleService: Title, private meta: Meta,
    private gtmService: GtmService
  ) { }

  ngOnInit() {
    this.titleService.setTitle("Demandez-votre démo - MyJSS");
    this.meta.updateTag({ name: 'description', content: "Découvrez comment notre plateforme simplifie vos formalités. Demandez votre démo personnalisée et voyez comment MyJSS peut vous faire gagner du temps et en sérénité." });
    this.demoForm = this.formBuilder.group({});
  }

  trackFormDemo() {
    this.gtmService.trackFormSubmit(
      {
        form: { type: 'Demander une démo' },
        page: {
          type: 'general',
          name: 'demo'
        } as PageInfo
      } as FormSubmitPayload
    );
  }

  getDemoByMail(event: any) {
    if (!this.firstName || !this.lastName || !this.mail) {
      this.appService.displayToast("Merci de remplir les champs obligatoires", true, "Une erreur s’est produite...", 3000);
    }
    if (!this.isConditionAccepted) {
      this.appService.displayToast("Merci d'accepter les conditions", true, "Une erreur s’est produite...", 3000);
      return;
    }

    this.mailService.subscribeDemo(this.mail, this.firstName, this.lastName, this.phoneNumber).subscribe(response => {
      if (response) {
        this.trackFormDemo();
        this.appService.displayToast("Vous allez recevoir un mail de confirmation.", false, "Demande validée", 3000);
        this.demoForm.reset();
        this.firstName = "";
        this.lastName = "";
        this.phoneNumber = "";
        this.mail = "";
        this.isConditionAccepted = false;
      }
    });
  }
}
