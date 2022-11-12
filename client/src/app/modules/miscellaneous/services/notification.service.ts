import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable, retry, share, Subject, switchMap, takeUntil, tap, timer } from 'rxjs';
import { NOTIFICATION_REFRESH_INTERVAL } from 'src/app/libs/Constants';
import { AppRestService } from 'src/app/services/appRest.service';
import { Notification } from '../../miscellaneous/model/Notification';
import { NotificationDialogComponent } from '../components/notification-dialog/notification-dialog.component';

@Injectable({
  providedIn: 'root'
})
export class NotificationService extends AppRestService<Notification>{

  notifications: Observable<Notification[]> | undefined;
  notificationsResult: Notification[] = [] as Array<Notification>;
  notificationDialogRef: MatDialogRef<NotificationDialogComponent> | undefined;

  constructor(http: HttpClient,
    public notificationDialog: MatDialog) {
    super(http, "miscellaneous");
    this.notifications = timer(1, NOTIFICATION_REFRESH_INTERVAL).pipe(
      switchMap(() => this.getNotifications()),
      retry(),
      tap((value) => {
        this.notificationsResult = value;
      }),
      share(),
      takeUntil(this.stopPolling)
    );
  }

  ngOnDestroy() {
    this.stopPolling.next(false);
  }

  private stopPolling = new Subject();

  getNotifications() {
    return this.getList(new HttpParams(), "notifications");
  }

  getNotificationsObservable(): Observable<Notification[]> {
    return this.notifications!;
  }

  getNotificationsResult(): Notification[] {
    return this.notificationsResult;
  }

  refreshNotifications() {
    this.getNotifications().subscribe(response => {
      this.notificationsResult = response;
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
}
