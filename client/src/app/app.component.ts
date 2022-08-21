import { Component, HostListener } from '@angular/core';
import { FormGroupDirective, NgForm, UntypedFormControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { environment } from 'src/environments/environment';
import { AppService } from './app.service';
import { SEARCH_KEY_CODE } from './libs/Constants';
import { LoginDialogComponent } from './routing/login-dialog/login-dialog.component';
import { LoginService } from './routing/login-dialog/login.service';
import { SearchService } from './search.service';

export class CustomErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: UntypedFormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  title: string = 'OSIRIS';

  sidenavOpenState: boolean = true;
  sidenavOpenStateSubscription: Subscription = new Subscription;
  loggedStateSubscription: Subscription = new Subscription;

  loggedIn = true;
  loginDialogRef: MatDialogRef<LoginDialogComponent> | undefined;

  constructor(protected appService: AppService,
    public loginDialog: MatDialog,
    private loginService: LoginService,
    protected searchService: SearchService) { }
  groups: string[] = [] as Array<string>;

  ngOnInit() {
    this.sidenavOpenStateSubscription = this.appService.sidenavOpenStateObservable.subscribe(item => this.sidenavOpenState = item);
    this.loggedStateSubscription = this.loginService.loggedStateObservable.subscribe(item => {
      this.loggedIn = item
      if (!this.loggedIn && !this.loginDialogRef) {
        this.loginDialogRef = this.loginDialog.open(LoginDialogComponent, {
          disableClose: true
        });
      }

      // For dev mode
      if (environment.production == false && this.loginService.hasGroup(['toto']) == false)
        this.loginService.setUserRoleAndRefresh();
    });
  }

  ngOnDestroy() {
    this.sidenavOpenStateSubscription.unsubscribe();
    this.loggedStateSubscription.unsubscribe();
  }

  @HostListener('window:keyup', ['$event'])
  keyEvent(event: KeyboardEvent) {
    if (event != undefined && event != null && event.code != null && event != undefined && event.code == SEARCH_KEY_CODE)
      this.searchService.openSearch();
  }

}
