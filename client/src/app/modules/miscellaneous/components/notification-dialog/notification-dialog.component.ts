import { Component } from '@angular/core';

@Component({
  selector: 'app-notification-dialog',
  templateUrl: './notification-dialog.component.html',
  styleUrls: ['./notification-dialog.component.css']
})
export class NotificationDialogComponent {
  /*
    PROVISION_ADD_ATTACHMENT = "PROVISION_ADD_ATTACHMENT";
    SERVICE_ADD_ATTACHMENT = "SERVICE_ADD_ATTACHMENT";
    ORDER_ADD_ATTACHMENT = "ORDER_ADD_ATTACHMENT";
    PROVISION_GUICHET_UNIQUE_STATUS_MODIFIED = "PROVISION_GUICHET_UNIQUE_STATUS_MODIFIED";
    PROVISION_GUICHET_UNIQUE_STATUS_SIGNED = "PROVISION_GUICHET_UNIQUE_STATUS_SIGNED";

    constructor(
      private formBuilder: FormBuilder,
      private notificationService: NotificationService,
      public confirmationDialog: MatDialog,
      private appService: AppService,
      public newNotificationDialog: MatDialog,
      private userPreferenceService: UserPreferenceService
    ) { }

    notificationForm = this.formBuilder.group({});

    displayFuture: boolean = false;

    displayInTeams = displayInTeams;

    ngOnInit() {
      this.displayFuture = this.notificationService.getIsDisplayFuture();
      this.notificationService.generateWindowsNotification();
      this.restoreTab();
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

    //Tabs management
    index: number = 0;
    onTabChange(event: MatTabChangeEvent) {
      this.userPreferenceService.setUserTabsSelectionIndex('notification-dialog', event.index);
    }

    restoreTab() {
      this.index = this.userPreferenceService.getUserTabsSelectionIndex('notification-dialog');
    }*/
}
