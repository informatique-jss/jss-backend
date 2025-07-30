import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { validateEmail } from '../../../../libs/CustomFormsValidatorsHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { LoginService } from '../../../profile/services/login.service';

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericInputComponent]
})
export class SignInComponent implements OnInit {

  inputMail: string = '';
  mailValidator = Validators.email;
  signinForm!: FormGroup;

  constructor(private formBuilder: FormBuilder,
    private appService: AppService,
    private loginService: LoginService,
    private activatedRoute: ActivatedRoute
  ) { }


  ngOnInit() {
    this.signinForm = this.formBuilder.group({});
  }

  sendConnectionLink() {
    if (this.signinForm.valid && (validateEmail(this.inputMail) || this.inputMail.indexOf("#") > 0)) {
      this.loginService.sendConnectionLink(this.inputMail).subscribe(response => {
        let from = this.activatedRoute.snapshot.params['from'];
        if (from && from == 'jss')
          this.appService.openJssRoute(undefined, "", false);
        else
          this.appService.openRoute(undefined, '/', undefined);
      })
    } else {
      this.appService.displayToast("L'adresse saisie n'est pas une adresse mail valide", true, "Erreur", 5000);
    }
  }

}
