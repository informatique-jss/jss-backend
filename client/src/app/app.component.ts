import { CdkDragEnd, Point } from '@angular/cdk/drag-drop';
import { Component, HostListener } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { environment } from 'src/environments/environment';
import { NOTIFICATION_KEY_CODE, SAVE_KEY_CODE, SEARCH_KEY_CODE } from './libs/Constants';
import { ConfirmDialogComponent } from './modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { ConstantService } from './modules/miscellaneous/services/constant.service';
import { NotificationService } from './modules/miscellaneous/services/notification.service';
import { EmployeeService } from './modules/profile/services/employee.service';
import { LoginDialogComponent } from './routing/login-dialog/login-dialog.component';
import { LoginService } from './routing/login-dialog/login.service';
import { AppService } from './services/app.service';
import { Note } from './services/model/Note';
import { SearchService } from './services/search.service';
import { UserPreferenceService } from './services/user.preference.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  title: string = 'OSIRIS';

  userNotes: Note[] = [] as Array<Note>;
  userNotesSubscription: Subscription = new Subscription;
  currentUserNotePosition: Point = { x: 0, y: 0 };
  displayEditNote: boolean = false;

  sidenavOpenState: boolean = true;
  sidenavOpenStateSubscription: Subscription = new Subscription;
  loggedStateSubscription: Subscription = new Subscription;

  loggedIn = true;
  loginDialogRef: MatDialogRef<LoginDialogComponent> | undefined;

  constructor(protected appService: AppService,
    public loginDialog: MatDialog,
    private loginService: LoginService,
    public confirmationDialog: MatDialog,
    private notificationService: NotificationService,
    private userPreferenceService: UserPreferenceService,
    private constantService: ConstantService,
    private employeeService: EmployeeService,
    protected searchService: SearchService) { }
  groups: string[] = [] as Array<string>;

  ngOnInit() {
    this.restoreNoteTablePosition();
    this.constantService.initConstant();
    this.sidenavOpenStateSubscription = this.appService.sidenavOpenStateObservable.subscribe(item => this.sidenavOpenState = item);
    this.loggedStateSubscription = this.loginService.loggedStateObservable.subscribe(item => {
      this.loggedIn = item
      if (!this.loggedIn && !this.loginDialogRef) {
        this.loginDialogRef = this.loginDialog.open(LoginDialogComponent, {
          disableClose: true
        });
      }

      if (environment.production == false && this.loginService.hasGroup(['toto']) == false)
        this.loginService.setUserRoleAndRefresh();
    });
  }

  ngOnDestroy() {
    this.sidenavOpenStateSubscription.unsubscribe();
    this.loggedStateSubscription.unsubscribe();
    this.userNotesSubscription.unsubscribe();
  }

  @HostListener('window:keyup', ['$event'])
  keyEvent(event: KeyboardEvent) {
    if (event != undefined && event != null && event.code != null && event != undefined && event.code == SEARCH_KEY_CODE)
      this.searchService.openSearch();
    if (event != undefined && event != null && event.code != null && event != undefined && event.code == NOTIFICATION_KEY_CODE)
      this.notificationService.openNotificationDialog();
    if (event != undefined && event != null && event.code != null && event != undefined && event.code == SAVE_KEY_CODE)
      this.appService.triggerSaveEvent();
  }

  dropNoteTable(event: CdkDragEnd) {
    this.userPreferenceService.setUserNoteTablePosition(event.source.getFreeDragPosition());
  }

  restoreNoteTablePosition() {
    this.currentUserNotePosition = this.userPreferenceService.getUserNoteTablePosition();
  }

  restoreDefaultNoteTablePosition() {
    this.userPreferenceService.setUserNoteTablePosition({ x: 0, y: 0 });
    this.restoreNoteTablePosition();
  }

  openRoute(event: any, link: string) {
    this.appService.openRoute(event, link, null);
  }
}
