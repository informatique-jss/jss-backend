import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable, Subject } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { Notification } from '../../miscellaneous/model/Notification';

@Injectable({
  providedIn: 'root'
})
export class NotificationService extends AppRestService<Notification> {

  notifications: Observable<number> | undefined;
  notificationsResult: number = 0;

  displayFuture: boolean = false;
  displayRead: boolean = false;

  constructor(http: HttpClient,
    public notificationDialog: MatDialog) {
    super(http, "miscellaneous");
    /*this.notifications = timer(1, NOTIFICATION_REFRESH_INTERVAL).pipe(
      switchMap(() => this.getNotificationsNumber(this.displayFuture, this.displayRead)),
      retry(),
      tap((value) => {
        this.notificationsResult = value;
        if (!this.notificationsResult)
          this.notificationsResult = 0;
      }),
      share(),
      takeUntil(this.stopPolling)
    );
    this.notifications.subscribe();*/
  }

  ngOnDestroy() {
    this.stopPolling.next(false);
  }

  private stopPolling = new Subject();

  getNotificationsNumber(displayFuture: boolean, displayRead: boolean) {
    return this.get(new HttpParams().set("displayFuture", displayFuture).set("displayRead", displayRead), "notifications/nbr") as any as Observable<number>;
  }

  refreshNotificationsNumber() {
    this.getNotificationsNumber(this.displayFuture, this.displayRead).subscribe(response => {
      this.notificationsResult = response;
      if (!this.notificationsResult)
        this.notificationsResult = 0;
    })
  }

  getNotifications(displayFuture: boolean, notificationTypes: string[], displayRead: boolean) {
    let params = new HttpParams().set("displayFuture", displayFuture).set("displayRead", displayRead);
    if (notificationTypes)
      params = params.set("notificationTypes", notificationTypes.join(","));
    return this.getList(params, "notifications");
  }

  getNotificationsResult(): number {
    if (!this.notificationsResult)
      return 0;
    return this.notificationsResult;
  }

  getIsDisplayFuture() {
    return this.displayFuture;
  }

  getAllNotificationTypes() {
    return this.getListCached(new HttpParams(), "notifications/types") as any as Observable<string[]>;
  }

  addOrUpdateNotification(notification: Notification) {
    return this.addOrUpdate(new HttpParams(), "notification", notification);
  }

  deleteNotification(notification: Notification) {
    return this.delete(new HttpParams().set("notificationId", notification.id), "notification");
  }

  addPersonnalNotification(notification: Notification) {
    return this.addOrUpdate(new HttpParams(), "notification/personnal", notification, "Notification ajoutée", "Erreur lors de l'enregistrement de la notification");
  }

  getNotificationsForCustomerOrder(customerOrderId: number) {
    return this.getList(new HttpParams().set("customerOrderId", customerOrderId), "notifications/customer-order");
  }

  getNotificationsForQuotation(quotationId: number) {
    return this.getList(new HttpParams().set("quotationId", quotationId), "notifications/quotation");
  }

  getNotificationsForProvision(provisionId: number) {
    return this.getList(new HttpParams().set("provisionId", provisionId), "notifications/provision");
  }

  getNotificationsForInvoice(invoiceId: number) {
    return this.getList(new HttpParams().set("invoiceId", invoiceId), "notifications/invoice");
  }

  getNotificationsForService(serviceId: number) {
    return this.getList(new HttpParams().set("serviceId", serviceId), "notifications/service");
  }

  getNotificationsForAffaire(affaireId: number) {
    return this.getList(new HttpParams().set("affaireId", affaireId), "notifications/affaire");
  }

  getNotificationsForTiers(tiersId: number) {
    return this.getList(new HttpParams().set("tiersId", tiersId), "notifications/tiers");
  }

  getNotificationsForResponsable(responsableId: number) {
    return this.getList(new HttpParams().set("responsableId", responsableId), "notifications/responsable");
  }

}
