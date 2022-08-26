import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { AppService } from 'src/app/services/app.service';
import { LoginService } from './login.service';
import { User } from './User';

@Component({
  selector: 'app-login-dialog',
  templateUrl: './login-dialog.component.html',
  styleUrls: ['./login-dialog.component.css']
})
export class LoginDialogComponent implements OnInit {

  constructor(private formBuilder: FormBuilder,
    private appService: AppService,
    private loginService: LoginService) { }

  logoOsiris: string = '/assets/images/jss_icon.png';

  user: User = {} as User;

  ngOnInit() {
  }

  loginForm = this.formBuilder.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  submitLogin() {
    this.loginService.logUser(this.user).subscribe(response => {
      if (response) {
        this.appService.displaySnackBar("👍 Authentification réussie", false, 10);
        this.loginService.setUserRoleAndRefresh();
      } else {
        this.appService.displaySnackBar("😢 Erreur lors de l'authentification", true, 10);
      }
    })
  }

}
