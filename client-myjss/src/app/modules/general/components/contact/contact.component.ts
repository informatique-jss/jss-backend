import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { PlatformService } from '../../../main/services/platform.service';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { GenericTextareaComponent } from '../../../miscellaneous/components/forms/generic-textarea/generic-textarea.component';
import { MailService } from '../../services/mail.service';

@Component({
  selector: 'contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericTextareaComponent, GenericInputComponent]
})
export class ContactComponent implements OnInit {

  firstName: string = "";
  lastName: string = "";
  mail: string = "";
  message: string = "";
  emailJss = "contact@jss.fr";
  isConditionAccepted: boolean = false;
  contactForm!: FormGroup;

  constructor(
    private appService: AppService,
    private mailService: MailService,
    private formBuilder: FormBuilder,
    private platformService: PlatformService) { }

  ngOnInit() {
    this.contactForm = this.formBuilder.group({});
  }

  ngAfterViewInit(): void {
    if (this.platformService.getNativeDocument())
      import('jarallax').then(module => {
        module.jarallax(this.platformService.getNativeDocument()!.querySelectorAll('.jarallax'), {
          speed: 0.5
        });
      });
  }

  subscribeFormContact(event: any): any {
    if (!this.firstName || !this.lastName || !this.mail || !this.message) {
      return;
    }
    if (!this.isConditionAccepted) {
      this.appService.displayToast("Merci d'accepter les conditions", true, "Une erreur s’est produite...", 3000);
      return;
    }

    this.mailService.subscribeContactForm(this.mail, this.firstName, this.lastName, this.message).subscribe(response => {
      if (response) {
        this.appService.displayToast("Vous allez recevoir un mail de confirmation.", false, "Demande reçue", 3000);
        this.contactForm.reset();
        this.firstName = "";
        this.lastName = "";
        this.mail = "";
        this.message = "";
        this.isConditionAccepted = false;
      }
    });
  }

}
