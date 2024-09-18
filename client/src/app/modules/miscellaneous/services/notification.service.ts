import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable, Subject, retry, share, switchMap, takeUntil, tap, timer } from 'rxjs';
import { NOTIFICATION_REFRESH_INTERVAL } from 'src/app/libs/Constants';
import { AppRestService } from 'src/app/services/appRest.service';
import { Notification } from '../../miscellaneous/model/Notification';
import { NotificationDialogComponent } from '../components/notification-dialog/notification-dialog.component';

@Injectable({
  providedIn: 'root'
})
export class NotificationService extends AppRestService<Notification> {

  notifications: Observable<Notification[]> | undefined;
  notificationsResult: Notification[] = [] as Array<Notification>;
  notificationDialogRef: MatDialogRef<NotificationDialogComponent> | undefined;
  notificationAlreadyNotified: Notification[] = [] as Array<Notification>;

  displayFuture: boolean = false;

  constructor(http: HttpClient,
    public notificationDialog: MatDialog) {
    super(http, "miscellaneous");
    this.notifications = timer(1, NOTIFICATION_REFRESH_INTERVAL).pipe(
      switchMap(() => this.getNotifications(this.displayFuture)),
      retry(),
      tap((value) => {
        this.notificationsResult = value;
        if (!this.notificationsResult)
          this.notificationsResult = [] as Array<Notification>;
        this.generateWindowsNotification();
      }),
      share(),
      takeUntil(this.stopPolling)
    );
  }

  ngOnDestroy() {
    this.stopPolling.next(false);
  }

  private stopPolling = new Subject();

  getNotifications(displayFuture: boolean) {
    return this.getList(new HttpParams().set("displayFuture", displayFuture), "notifications");
  }

  getNotificationsObservable(): Observable<Notification[]> {
    return this.notifications!;
  }

  getNotificationsResult(): Notification[] {
    if (!this.notificationsResult)
      this.notificationsResult = [] as Array<Notification>;
    return this.notificationsResult;
  }

  getIsDisplayFuture() {
    return this.displayFuture;
  }

  refreshNotifications(displayFuture: boolean) {
    this.displayFuture = displayFuture;
    this.getNotifications(displayFuture).subscribe(response => {
      this.notificationsResult = response;
      this.generateWindowsNotification();
    })
  }

  addOrUpdateNotification(notification: Notification) {
    return this.addOrUpdate(new HttpParams(), "notification", notification);
  }

  openNotificationDialog() {
    if (this.notificationDialogRef == undefined || this.notificationDialogRef?.getState() != 0) {
      this.notificationDialogRef = this.notificationDialog.open(NotificationDialogComponent, {
        width: '100%',
        height: '90%'
      });
    }
  }

  closeNotificationDialog() {
    if (this.notificationDialogRef)
      this.notificationDialogRef.close();
  }

  deleteNotification(notification: Notification) {
    return this.delete(new HttpParams().set("notificationId", notification.id), "notification");
  }

  addPersonnalNotification(notification: Notification) {
    return this.addOrUpdate(new HttpParams(), "notification/personnal", notification);
  }

  generateWindowsNotification() {
    if (!('Notification' in window)) {
      return;
    }

    if (this.notificationsResult) {
      for (let notification of this.notificationsResult) {
        if (notification.showPopup && !notification.isRead && (new Date(notification.createdDateTime)).getTime() <= (new Date()).getTime()) {
          let found = false;
          for (let alreadyNotificated of this.notificationAlreadyNotified)
            if (alreadyNotificated.id == notification.id)
              found = true;
          if (!found) {
            Notification.requestPermission(function (permission) {
              var popup = new Notification(notification.summary, { body: notification.detail1, icon: 'assets/images/osiris.png', dir: 'auto' });
              setTimeout(function () {
                popup.close();
              }, 10000);
            });
            this.notificationAlreadyNotified.push(notification);
          }
        }
      }

    }


  }
}
