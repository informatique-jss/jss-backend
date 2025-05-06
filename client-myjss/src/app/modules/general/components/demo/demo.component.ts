import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AppService } from '../../../../libs/app.service';
import { validateEmail, validateFrenchPhone, validateInternationalPhone } from '../../../../libs/CustomFormsValidatorsHelper';
import { GeneralService } from '../../services/general.service';

@Component({
  selector: 'demo',
  templateUrl: './demo.component.html',
  styleUrls: ['./demo.component.css'],
  standalone: false
})
export class DemoComponent implements OnInit {
  isConditionAccepted: boolean = false;
  checkedOnce: boolean = false;

  firstName: string = "";
  lastName: string = "";
  phoneNumber: string = "";
  mail: string = "";

  validateEmail = validateEmail;
  validateFrenchPhone = validateFrenchPhone;
  validateInternationalPhone = validateInternationalPhone;
  demoForm = this.formBuilder.group({});

  constructor(private formBuilder: FormBuilder,
    private generalService: GeneralService,
    private appService: AppService
  ) { }

  ngOnInit() {
  }

  getDemoByMail(event: any) {
    this.checkedOnce = true;
    if (!this.firstName || !this.lastName || !this.mail || !this.isConditionAccepted) {
      return;
    }

    this.generalService.receiveDemoByMail(this.mail).subscribe(response => {
      if (response) {
        this.appService.displayToast("Vous allez recevoir un mail de confirmation.", false, "Demande validée", 3000);
        this.demoForm.reset();
        this.firstName = "";
        this.lastName = "";
        this.phoneNumber = "";
        this.mail = "";
        this.isConditionAccepted = false;
        this.checkedOnce = false;
      }
    });
  }
}
