import { Component, HostListener } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { SAVE_KEY_CODE, SEARCH_KEY_CODE } from './libs/Constants';
import { ConstantService } from './modules/miscellaneous/services/constant.service';
import { LoginDialogComponent } from './routing/login-dialog/login-dialog.component';
import { LoginService } from './routing/login-dialog/login.service';
import { AppService } from './services/app.service';
import { SearchService } from './services/search.service';
import { UserPreferenceService } from './services/user.preference.service';

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
    public confirmationDialog: MatDialog,
    private userPreferenceService: UserPreferenceService,
    private constantService: ConstantService,
    protected searchService: SearchService) { }
  groups: string[] = [] as Array<string>;

  ngOnInit() {
    this.constantService.initConstant();
    this.sidenavOpenStateSubscription = this.appService.sidenavOpenStateObservable.subscribe(item => this.sidenavOpenState = item);
    this.loggedStateSubscription = this.loginService.loggedStateObservable.subscribe(item => {
      this.loggedIn = item
      if (!this.loggedIn && !this.loginDialogRef) {
        this.loginDialogRef = this.loginDialog.open(LoginDialogComponent, {
          disableClose: true
        });
      }

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
    if (event != undefined && event != null && event.code != null && event != undefined && event.code == SAVE_KEY_CODE)
      this.appService.triggerSaveEvent();
  }

  openRoute(event: any, link: string) {
    this.appService.openRoute(event, link, null);
  }
}
