import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { MailService } from '../../services/mail.service';
import { GenericInputComponent } from "../generic-input/generic-input.component";
import { GenericTextareaComponent } from "../generic-textarea/generic-textarea.component";
import { NewsletterComponent } from "../newsletter/newsletter.component";

@Component({
  selector: 'contribute',
  templateUrl: './contribute.component.html',
  styleUrls: ['./contribute.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, NewsletterComponent, GenericInputComponent, GenericTextareaComponent]
})
export class ContributeComponent implements OnInit {

  contributeForm!: FormGroup;
  firstName: string = "";
  lastName: string = "";
  phone: string = "";
  mail: string = "";
  message: string = "";
  isConditionAccepted: boolean = false;

  constructor(private formBuilder: FormBuilder,
    private mailService: MailService,
    private appService: AppService,
  ) { }

  ngOnInit() {
    this.contributeForm = this.formBuilder.group({});
  }

  subscribeFormContact(event: any): any {
    if (!this.firstName || !this.lastName || !this.mail || !this.message) {
      return;
    }
    if (!this.isConditionAccepted) {
      this.appService.displayToast("Merci d'accepter les conditions", true, "Une erreur s’est produite...", 3000);
      return;
    }

    this.mailService.subscribeContactForm(this.mail, this.firstName, this.lastName, this.phone, this.message).subscribe(response => {
      if (response) {
        this.appService.displayToast("Vous allez recevoir un mail de confirmation.", false, "Demande reçue", 3000);
        this.contributeForm.reset();
        this.firstName = "";
        this.lastName = "";
        this.mail = "";
        this.phone = ""
        this.message = "";
        this.isConditionAccepted = false;
      }
    });
  }
}
