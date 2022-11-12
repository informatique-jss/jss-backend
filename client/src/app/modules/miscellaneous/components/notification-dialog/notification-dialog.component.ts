import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { EntityType } from 'src/app/routing/search/EntityType';
import { QUOTATION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { Notification } from '../../model/Notification';
import { NotificationService } from '../../services/notification.service';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-notification-dialog',
  templateUrl: './notification-dialog.component.html',
  styleUrls: ['./notification-dialog.component.css']
})
export class NotificationDialogComponent implements OnInit {

  tabNames: { [key: string]: string; } = {};

  QUOTATION_ENTITY_TYPE = QUOTATION_ENTITY_TYPE;

  constructor(
    private formBuilder: FormBuilder,
    private notificationService: NotificationService,
    public confirmationDialog: MatDialog,
    private router: Router,
  ) { }

  notificationForm = this.formBuilder.group({});

  ngOnInit() {
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
      return new Date(a.createdDateTime).getTime() - new Date(b.createdDateTime).getTime();
    });
  }

  openEntity(notification: Notification, entityType: EntityType) {
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() =>
      this.router.navigate(['/' + entityType.entryPoint + '/', "" + notification.entityId])
    );
    this.notificationService.closeNotificationDialog();
    return;
  }

  setAsRead(event: any, notification: Notification) {
    if (event && event.ctrlKey)
      return this.setAllAsRead(notification.entityType);
    notification.isRead = true;
    this.notificationService.addOrUpdateNotification(notification).subscribe(reponse => {
      this.notificationService.refreshNotifications();
    })
  }

  setAsUnread(event: any, notification: Notification) {
    if (event && event.ctrlKey)
      return this.setAllAsUnread(notification.entityType);
    notification.isRead = false;
    this.notificationService.addOrUpdateNotification(notification).subscribe(reponse => {
      this.notificationService.refreshNotifications();
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
          this.notificationService.refreshNotifications();
        })
      }
    });
  }

  setAllAsRead(entityType: string) {
    this.notificationService.getNotificationsResult().forEach(notification => {
      if (notification.entityType == entityType && !notification.isRead) {
        notification.isRead = true;
        this.notificationService.addOrUpdateNotification(notification).subscribe(reponse => {
          this.notificationService.refreshNotifications();
        })
      }
    })
  }

  setAllAsUnread(entityType: string) {
    this.notificationService.getNotificationsResult().forEach(notification => {
      if (notification.entityType == entityType && notification.isRead) {
        notification.isRead = false;
        this.notificationService.addOrUpdateNotification(notification).subscribe(reponse => {
          this.notificationService.refreshNotifications();
        })
      }
    })
  }

  deleteAll(entityType: string) {
    this.notificationService.getNotificationsResult().forEach(notification => {
      if (notification.entityType == entityType && !notification.isRead) {
        this.notificationService.deleteNotification(notification).subscribe(reponse => {
          this.notificationService.refreshNotifications();
        })
      }
    })
  }

}
