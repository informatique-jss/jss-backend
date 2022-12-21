import { CdkDragEnd, Point } from '@angular/cdk/drag-drop';
import { Component, HostListener } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { environment } from 'src/environments/environment';
import { NOTIFICATION_KEY_CODE, SEARCH_KEY_CODE } from './libs/Constants';
import { ConfirmDialogComponent } from './modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { ConstantService } from './modules/miscellaneous/services/constant.service';
import { NotificationService } from './modules/miscellaneous/services/notification.service';
import { LoginDialogComponent } from './routing/login-dialog/login-dialog.component';
import { LoginService } from './routing/login-dialog/login.service';
import { AppService } from './services/app.service';
import { Note } from './services/model/Note';
import { SearchService } from './services/search.service';
import { UserNoteService } from './services/user.notes.service';
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
    private userNoteService: UserNoteService,
    public confirmationDialog: MatDialog,
    private notificationService: NotificationService,
    private userPreferenceService: UserPreferenceService,
    private constantService: ConstantService,
    protected searchService: SearchService) { }
  groups: string[] = [] as Array<string>;

  ngOnInit() {
    this.userNoteService.restoreUserNotes();
    this.restoreNoteTablePosition();
    this.constantService.initConstant();
    this.sidenavOpenStateSubscription = this.appService.sidenavOpenStateObservable.subscribe(item => this.sidenavOpenState = item);
    this.userNotesSubscription = this.userNoteService.userNotesEventObservable.subscribe(item => this.userNotes = item)
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

  deleteAllNotes() {
    const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Supprimer toutes les notes",
        content: "Êtes-vous sûr de vouloir toutes vos notes ?",
        closeActionText: "Annuler",
        validationActionText: "Confirmer"
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult)
        this.userNoteService.deleteAllNotes();
    });
  }

  deleteNote(note: Note) {
    this.userNoteService.deleteNote(note);
  }

  upNote(note: Note) {
    this.userNoteService.upNote(note);
  }

  downNote(note: Note) {
    this.userNoteService.downNote(note);
  }

  displayEditNotes() {
    this.displayEditNote = !this.displayEditNote;
  }

  openRoute(event: any, link: string) {
    this.appService.openRoute(event, link, null);
  }

}
