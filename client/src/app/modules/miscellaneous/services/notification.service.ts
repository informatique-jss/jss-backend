import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable, Subject, retry, share, switchMap, takeUntil, tap, timer } from 'rxjs';
import { NOTIFICATION_REFRESH_INTERVAL } from 'src/app/libs/Constants';
import { AppRestService } from 'src/app/services/appRest.service';
import { Notification } from '../../miscellaneous/model/Notification';

@Injectable({
  providedIn: 'root'
})
export class NotificationService extends AppRestService<Notification> {

  notifications: Observable<number> | undefined;
  notificationsResult: number = 0;

  displayFuture: boolean = false;

  constructor(http: HttpClient,
    public notificationDialog: MatDialog) {
    super(http, "miscellaneous");
    this.notifications = timer(1, NOTIFICATION_REFRESH_INTERVAL).pipe(
      switchMap(() => this.getNotificationsNumber(this.displayFuture)),
      retry(),
      tap((value) => {
        this.notificationsResult = value;
        if (!this.notificationsResult)
          this.notificationsResult = 0;
      }),
      share(),
      takeUntil(this.stopPolling)
    );
  }

  ngOnDestroy() {
    this.stopPolling.next(false);
  }

  private stopPolling = new Subject();

  getNotificationsNumber(displayFuture: boolean) {
    return this.get(new HttpParams().set("displayFuture", displayFuture), "notifications/number") as any as Observable<number>;
  }

  getNotificationsResult(): number {
    if (!this.notificationsResult)
      return 0;
    return this.notificationsResult;
  }

  getIsDisplayFuture() {
    return this.displayFuture;
  }

  addOrUpdateNotification(notification: Notification) {
    return this.addOrUpdate(new HttpParams(), "notification", notification);
  }

  deleteNotification(notification: Notification) {
    return this.delete(new HttpParams().set("notificationId", notification.id), "notification");
  }

  addPersonnalNotification(notification: Notification) {
    return this.addOrUpdate(new HttpParams(), "notification/personnal", notification);
  }

}
