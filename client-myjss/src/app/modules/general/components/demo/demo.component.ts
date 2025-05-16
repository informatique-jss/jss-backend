import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AppService } from '../../../../libs/app.service';
import { MailService } from '../../services/mail.service';

@Component({
  selector: 'demo',
  templateUrl: './demo.component.html',
  styleUrls: ['./demo.component.css'],
  standalone: false
})
export class DemoComponent implements OnInit {
  isConditionAccepted: boolean = false;

  firstName: string = "";
  lastName: string = "";
  phoneNumber: string = "";
  mail: string = "";
  demoForm = this.formBuilder.group({});

  constructor(private formBuilder: FormBuilder,
    private mailService: MailService,
    private appService: AppService
  ) { }

  ngOnInit() {
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
