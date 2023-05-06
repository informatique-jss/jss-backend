import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_TO_ASSIGN, CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_VERIFY, CUSTOMER_ORDER_BEING_PROCESSED, CUSTOMER_ORDER_BEING_PROCESSED_FROM_DEPOSIT, CUSTOMER_ORDER_CREATE, CUSTOMER_ORDER_TO_BE_BILLED, INVOICE_REMINDER_PAYMENT, PROVISION_ADD_ATTACHMENT, PROVISION_GUICHET_UNIQUE_STATUS_MODIFIED, QUOTATION_ASSO_AFFAIRE_ORDER_VERIFY, QUOTATION_CREATE, QUOTATION_REFUSED_BY_CUSOMER, QUOTATION_SENT, QUOTATION_VALIDATED_BY_CUSOMER, TIERS_DEPOSIT_MANDATORY } from 'src/app/libs/Constants';
import { displayInTeams } from 'src/app/libs/MailHelper';
import { EntityType } from 'src/app/routing/search/EntityType';
import { INVOICE_ENTITY_TYPE, PROVISION_ENTITY_TYPE, QUOTATION_ENTITY_TYPE, TIERS_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { PERSONNAL } from '../../../../libs/Constants';
import { CUSTOMER_ORDER_ENTITY_TYPE } from '../../../../routing/search/search.component';
import { AppService } from '../../../../services/app.service';
import { Notification } from '../../model/Notification';
import { NotificationService } from '../../services/notification.service';
import { AddNotificationDialogComponent } from '../add-notification-dialog/add-notification-dialog.component';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-notification-dialog',
  templateUrl: './notification-dialog.component.html',
  styleUrls: ['./notification-dialog.component.css']
})
export class NotificationDialogComponent implements OnInit {

  tabNames: { [key: string]: string; } = {};

  QUOTATION_ENTITY_TYPE = QUOTATION_ENTITY_TYPE;
  CUSTOMER_ORDER_ENTITY_TYPE = CUSTOMER_ORDER_ENTITY_TYPE;
  INVOICE_ENTITY_TYPE = INVOICE_ENTITY_TYPE;
  PROVISION_ENTITY_TYPE = PROVISION_ENTITY_TYPE;
  TIERS_ENTITY_TYPE = TIERS_ENTITY_TYPE;

  PERSONNAL = PERSONNAL;
  QUOTATION_CREATE = QUOTATION_CREATE;
  QUOTATION_SENT = QUOTATION_SENT;
  QUOTATION_ASSO_AFFAIRE_ORDER_VERIFY = QUOTATION_ASSO_AFFAIRE_ORDER_VERIFY;
  QUOTATION_REFUSED_BY_CUSOMER = QUOTATION_REFUSED_BY_CUSOMER;
  QUOTATION_VALIDATED_BY_CUSOMER = QUOTATION_VALIDATED_BY_CUSOMER;

  CUSTOMER_ORDER_CREATE = CUSTOMER_ORDER_CREATE;
  CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_VERIFY = CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_VERIFY;
  CUSTOMER_ORDER_BEING_PROCESSED = CUSTOMER_ORDER_BEING_PROCESSED;
  CUSTOMER_ORDER_BEING_PROCESSED_FROM_DEPOSIT = CUSTOMER_ORDER_BEING_PROCESSED_FROM_DEPOSIT;
  CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_TO_ASSIGN = CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_TO_ASSIGN;
  CUSTOMER_ORDER_TO_BE_BILLED = CUSTOMER_ORDER_TO_BE_BILLED;

  INVOICE_REMINDER_PAYMENT = INVOICE_REMINDER_PAYMENT;

  TIERS_DEPOSIT_MANDATORY = TIERS_DEPOSIT_MANDATORY;

  PROVISION_ADD_ATTACHMENT = PROVISION_ADD_ATTACHMENT;
  PROVISION_GUICHET_UNIQUE_STATUS_MODIFIED = PROVISION_GUICHET_UNIQUE_STATUS_MODIFIED;

  constructor(
    private formBuilder: FormBuilder,
    private notificationService: NotificationService,
    public confirmationDialog: MatDialog,
    private appService: AppService,
    public newNotificationDialog: MatDialog,
  ) { }

  notificationForm = this.formBuilder.group({});

  displayFuture: boolean = false;

  displayInTeams = displayInTeams;

  ngOnInit() {
    this.displayFuture = this.notificationService.getIsDisplayFuture();
    this.notificationService.generateWindowsNotification();
  }

  getTabLabel(entityType: EntityType): string {
    let tabName = "";
    tabName += entityType.tabName;
    if (this.notificationService.getNotificationsResult()) {
      let entityNumber = 0;
      let entityUnreadNumber = 0;
      this.notificationService.getNotificationsResult().forEach(notification => {
        if (notification.entityType == entityType.entityType) {
          entityNumber++;
          if (!notification.isRead)
            entityUnreadNumber++;
        }
      })
      tabName += " (" + entityUnreadNumber + " / " + entityNumber + ")"
    }
    return tabName;
  }

  getEntitiesForTab(entityType: EntityType): Notification[] {
    let tabEntities = [] as Array<Notification>;
    if (this.notificationService.getNotificationsResult() != null) {
      this.notificationService.getNotificationsResult().forEach(notification => {
        if (notification.entityType == entityType.entityType)
          tabEntities.push(notification);
      })
    }
    return tabEntities.sort(function (a: Notification, b: Notification) {
      return new Date(b.createdDateTime).getTime() - new Date(a.createdDateTime).getTime();
    });
  }

  getEntitiesPersonnal(): Notification[] {
    let tabEntities = [] as Array<Notification>;
    if (this.notificationService.getNotificationsResult() != null) {
      this.notificationService.getNotificationsResult().forEach(notification => {
        if (notification.notificationType == PERSONNAL)
          tabEntities.push(notification);
      })
    }
    return tabEntities.sort(function (a: Notification, b: Notification) {
      return new Date(b.createdDateTime).getTime() - new Date(a.createdDateTime).getTime();
    });
  }

  getPersonnalNotificationNumber(isRead: boolean) {
    let notificationNumber = 0;
    for (let notification of this.getEntitiesPersonnal())
      if (notification.isRead == isRead)
        notificationNumber++;
    return notificationNumber;
  }

  openEntity(event: any, notification: Notification, entityType: EntityType) {
    this.appService.openRoute(event, "/" + entityType.entryPoint + '/' + notification.entityId, () => { this.notificationService.closeNotificationDialog(); });
  }

  setAsRead(event: any, notification: Notification) {
    if (event && event.ctrlKey)
      return this.setAllAsRead(notification.entityType);
    notification.isRead = true;
    this.notificationService.addOrUpdateNotification(notification).subscribe(reponse => {
      this.notificationService.refreshNotifications(this.displayFuture);
    })
  }

  setAsUnread(event: any, notification: Notification) {
    if (event && event.ctrlKey)
      return this.setAllAsUnread(notification.entityType);
    notification.isRead = false;
    this.notificationService.addOrUpdateNotification(notification).subscribe(reponse => {
      this.notificationService.refreshNotifications(this.displayFuture);
    })
  }

  delete(event: any, notification: Notification) {
    let isAll = (event && event.ctrlKey);
    const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Supprimer " + (isAll ? "toutes les" : "la") + " notification" + (isAll ? "s" : ""),
        content: "Êtes-vous sûr de vouloir supprimer " + (isAll ? "toutes les" : "cette") + " notification" + (isAll ? "s" : "") + " ?",
        closeActionText: "Annuler",
        validationActionText: "Confirmer"
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult) {
        if (isAll)
          return this.deleteAll(notification.entityType);
        this.notificationService.deleteNotification(notification).subscribe(reponse => {
          this.notificationService.refreshNotifications(this.displayFuture);
        })
      }
    });
  }

  setAllAsRead(entityType: string) {
    this.notificationService.getNotificationsResult().forEach(notification => {
      if (notification.entityType == entityType && !notification.isRead) {
        notification.isRead = true;
        this.notificationService.addOrUpdateNotification(notification).subscribe(reponse => {
          this.notificationService.refreshNotifications(this.displayFuture);
        })
      }
    })
  }

  setAllAsUnread(entityType: string) {
    this.notificationService.getNotificationsResult().forEach(notification => {
      if (notification.entityType == entityType && notification.isRead) {
        notification.isRead = false;
        this.notificationService.addOrUpdateNotification(notification).subscribe(reponse => {
          this.notificationService.refreshNotifications(this.displayFuture);
        })
      }
    })
  }

  deleteAll(entityType: string) {
    this.notificationService.getNotificationsResult().forEach(notification => {
      if (notification.entityType == entityType) {
        this.notificationService.deleteNotification(notification).subscribe(reponse => {
          this.notificationService.refreshNotifications(this.displayFuture);
        })
      }
    })
  }

  addPersonnalNotification() {
    const dialogRef = this.confirmationDialog.open(AddNotificationDialogComponent, {
      maxWidth: "600px",
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult) {
        this.notificationService.addPersonnalNotification(dialogResult).subscribe(response => {
          this.notificationService.refreshNotifications(this.displayFuture);
        })
      }
    });
  }

  editNotification(event: any, notification: Notification) {
    const dialogRef = this.confirmationDialog.open(AddNotificationDialogComponent, {
      maxWidth: "600px",
    });

    dialogRef.componentInstance.notification = notification;

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult) {
        this.notificationService.addPersonnalNotification(dialogResult).subscribe(response => {
          this.notificationService.refreshNotifications(this.displayFuture);
        })
      }
    });
  }

  toggleDisplayFuture() {
    this.displayFuture = !this.displayFuture;
    this.notificationService.refreshNotifications(this.displayFuture);
  }
}
