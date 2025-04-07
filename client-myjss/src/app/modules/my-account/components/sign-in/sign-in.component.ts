import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { validateEmail } from '../../../../libs/CustomFormsValidatorsHelper';
import { AppService } from '../../../../libs/app.service';
import { LoginService } from '../../../profile/services/login.service';

@Component({
    selector: 'app-sign-in',
    templateUrl: './sign-in.component.html',
    styleUrls: ['./sign-in.component.css'],
    standalone: false
})
export class SignInComponent implements OnInit {

  inputMail: string = '';
  mailValidator = Validators.email;

  constructor(private formBuilder: FormBuilder,
    private appService: AppService,
    private loginService: LoginService,
  ) { }

  signinForm = this.formBuilder.group({});

  ngOnInit() {
  }

  sendConnectionLink() {
    if (this.signinForm.valid && validateEmail(this.inputMail)) {
      this.loginService.sendConnectionLink(this.inputMail).subscribe(response => {
        this.appService.openRoute(undefined, '/', undefined);
      })
    } else {
      this.appService.displayToast("L'adresse saisie n'est pas une adresse mail valide", true, "Erreur", 5000);
    }
  }

}
