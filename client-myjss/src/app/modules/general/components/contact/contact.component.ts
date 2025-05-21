import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { jarallax } from 'jarallax';
import { AppService } from '../../../../libs/app.service';
import { MailService } from '../../services/mail.service';

@Component({
  selector: 'contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css'],
  standalone: false
})
export class ContactComponent implements OnInit {

  firstName: string = "";
  lastName: string = "";
  mail: string = "";
  message: string = "";
  emailJss = "contact@jss.fr";
  isConditionAccepted: boolean = false;

  constructor(
    private appService: AppService,
    private mailService: MailService,
    private formBuilder: FormBuilder,) { }

  ngOnInit() {
  }

  contactForm = this.formBuilder.group({});

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }

  subscribeFormContact(event: any): any {
    if (!this.firstName || !this.lastName || !this.mail || !this.message) {
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
