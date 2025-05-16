import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { combineLatest } from 'rxjs';
import { Dictionnary } from 'src/app/libs/Dictionnary';
import { formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { Notification } from '../../model/Notification';
import { SortTableAction } from '../../model/SortTableAction';
import { SortTableColumn } from '../../model/SortTableColumn';
import { NotificationService } from '../../services/notification.service';
import { AddNotificationDialogComponent } from '../add-notification-dialog/add-notification-dialog.component';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'notification-list',
  templateUrl: './notification-list.component.html',
  styleUrls: ['./notification-list.component.css']
})
export class NotificationListComponent implements OnInit {
  notificationForm = this.formBuilder.group({});
  displayedColumns: SortTableColumn<Notification>[] = [];
  notificationTypes: string[] = [];
  @Input() notifications: Notification[] | undefined;
  @Input() isForIntegration: boolean = false;
  displayFuture: boolean = true;
  displayRead: boolean = true;
  tableAction: SortTableAction<Notification>[] = [];
  bookmark: any | undefined;


  constructor(private notificationService: NotificationService,
    private formBuilder: FormBuilder,
    private appService: AppService,
    public confirmationDialog: MatDialog,
    private userPreferenceService: UserPreferenceService
  ) { }

  ngOnInit() {
    if (!this.isForIntegration)
      this.appService.changeHeaderTitle("Notifications");

    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn<Notification>);
    this.displayedColumns.push({ id: "isRead", fieldName: "isRead", label: "Lu ?", valueFonction: (element: Notification, column: SortTableColumn<Notification>) => { return element.isRead ? "Oui" : "Non" }, colorWarnFunction: (element: Notification) => { return !element.isRead } } as SortTableColumn<Notification>);
    this.displayedColumns.push({ id: "notificationType", fieldName: "notificationType", label: "Type", valueFonction: (element: Notification, column: SortTableColumn<Notification>) => { return this.translateDataLabel(element.notificationType) } } as SortTableColumn<Notification>);
    this.displayedColumns.push({ id: "customerOrder", fieldName: "customerOrder.id", label: "N° de commande", actionLinkFunction: this.getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la commande associée" } as SortTableColumn<Notification>);
    this.displayedColumns.push({ id: "quotation", fieldName: "quotation.id", label: "N° de devis", actionLinkFunction: this.getColumnLink, actionIcon: "visibility", actionTooltip: "Voir le devis associé" } as SortTableColumn<Notification>);
    this.displayedColumns.push({ id: "affairesList", fieldName: "customerOrder.affairesList", label: "Affaire(s)" } as SortTableColumn<Notification>);
    this.displayedColumns.push({ id: "service", fieldName: "service.serviceLabelToDisplay", label: "Service" } as SortTableColumn<Notification>);
    this.displayedColumns.push({ id: "provision", fieldName: "provision.provisionType.label", label: "Prestation", isShrinkColumn: true } as SortTableColumn<Notification>);
    this.displayedColumns.push({
      id: "tiers", fieldName: "tiers", label: "Tiers", actionIcon: "visibility", actionTooltip: "Voir le tiers associé", actionLinkFunction: this.getColumnLink, valueFonction: (element: Notification, column: SortTableColumn<Notification>) => {
        if (element && element.tiers) {
          return element.tiers.denomination ? element.tiers.denomination : (element.tiers.firstname + ' ' + element.tiers.lastname)
        }
        return "";
      }
    } as SortTableColumn<Notification>);
    this.displayedColumns.push({
      id: "responsable", fieldName: "responsable", label: "Responsable", actionIcon: "visibility", actionTooltip: "Voir le responsable associé", actionLinkFunction: this.getColumnLink, valueFonction: (element: Notification, column: SortTableColumn<Notification>) => {
        if (element && element.responsable) {
          return element.responsable.firstname + ' ' + element.responsable.lastname
        }
        return "";
      }
    } as SortTableColumn<Notification>);
    this.displayedColumns.push({ id: "invoice", fieldName: "invoice.id", label: "Facture", actionIcon: "visibility", actionTooltip: "Voir la facture associée", actionLinkFunction: this.getColumnLink } as SortTableColumn<Notification>);
    this.displayedColumns.push({
      id: "affaire", fieldName: "affaire", label: "Affaire", actionIcon: "visibility", actionTooltip: "Voir l'affaire associée", actionLinkFunction: this.getColumnLink, valueFonction: (element: Notification, column: SortTableColumn<Notification>) => {
        if (element && element.affaire) {
          return element.affaire.denomination ? element.affaire.denomination : (element.affaire.firstname + ' ' + element.affaire.lastname)
        }
        return "";
      }
    } as SortTableColumn<Notification>);
    this.displayedColumns.push({ id: "detail1", fieldName: "detail1", label: "Commentaire", isShrinkColumn: true } as SortTableColumn<Notification>);
    this.displayedColumns.push({ id: "createdBy", fieldName: "createdBy", label: "Par", displayAsEmployee: true } as SortTableColumn<Notification>);
    this.displayedColumns.push({ id: "createdDateTime", fieldName: "createdDateTime", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn<Notification>);
    this.displayedColumns.push({ id: "updatedBy", fieldName: "updatedBy", label: "Mis à jour par", displayAsEmployee: true } as SortTableColumn<Notification>);
    this.displayedColumns.push({ id: "updatedDateTime", fieldName: "updatedDateTime", label: "Notification prévue le", valueFonction: formatDateTimeForSortTable } as SortTableColumn<Notification>);

    this.tableAction.push({
      actionIcon: "edit", actionName: "Editer la notification", actionClick: (column: SortTableAction<Notification>, element: Notification, event: any) => {
        this.editNotification(null, element);
      }, display: true,
    } as SortTableAction<Notification>);

    this.tableAction.push({
      actionIcon: "mark_email_read", actionName: "Marquer comme lu", actionClick: (column: SortTableAction<Notification>, element: Notification, event: any) => {
        this.setAsRead(element);
      }, display: true,
    } as SortTableAction<Notification>);

    this.tableAction.push({
      actionIcon: "mark_email_read", actionName: "Marquer tout comme lu", actionClick: (column: SortTableAction<Notification>, element: Notification, event: any) => {
        this.setAllAsRead();
      }, display: true,
    } as SortTableAction<Notification>);

    this.tableAction.push({
      actionIcon: "mark_email_unread", actionName: "Marquer comme non lu", actionClick: (column: SortTableAction<Notification>, element: Notification, event: any) => {
        this.setAsUnread(element);
      }, display: true,
    } as SortTableAction<Notification>);

    this.tableAction.push({
      actionIcon: "mark_email_unread", actionName: "Marquer tout comme non lu", actionClick: (column: SortTableAction<Notification>, element: Notification, event: any) => {
        this.setAllAsUnread();
      }, display: true,
    } as SortTableAction<Notification>);

    this.tableAction.push({
      actionIcon: "delete", actionName: "Supprimer", actionClick: (column: SortTableAction<Notification>, element: Notification, event: any) => {
        this.delete(element, false, false);
      }, display: true,
    } as SortTableAction<Notification>);

    this.tableAction.push({
      actionIcon: "delete", actionName: "Supprimer toutes les lues", actionClick: (column: SortTableAction<Notification>, element: Notification, event: any) => {
        this.delete(element, true, true);
      }, display: true,
    } as SortTableAction<Notification>);

    this.tableAction.push({
      actionIcon: "delete", actionName: "Supprimer toutes les non lues", actionClick: (column: SortTableAction<Notification>, element: Notification, event: any) => {
        this.delete(element, true, false);
      }, display: true,
    } as SortTableAction<Notification>);

    this.bookmark = this.userPreferenceService.getUserSearchBookmark("notifications") as any;
    if (this.bookmark) {
      if (this.bookmark.notificationTypes)
        this.notificationTypes = this.bookmark.notificationTypes;
      if (this.bookmark.displayFuture != undefined)
        this.displayFuture = this.bookmark.displayFuture;
      if (this.bookmark.displayRead != undefined)
        this.displayRead = this.bookmark.displayRead;
    }

    if (!this.isForIntegration)
      this.refresh();
  }

  refresh() {
    this.notificationService.getNotifications(this.displayFuture, this.notificationTypes, this.displayRead).subscribe(response => this.notifications = response);
    this.userPreferenceService.setUserSearchBookmark({ displayFuture: this.displayFuture, notificationTypes: this.notificationTypes, displayRead: this.displayRead }, 'notifications');
    this.notificationService.refreshNotificationsNumber();
  }

  getColumnLink(column: SortTableColumn<Notification>, element: Notification) {
    if (element && column.id == "customerOrder" && element.customerOrder && element.customerOrder.id) {
      return ['order', element.customerOrder.id];
    }
    if (element && column.id == "quotation" && element.quotation && element.quotation.id) {
      return ['quotation', element.quotation.id];
    }
    if (element && column.id == "tiers" && element.tiers && element.tiers.id) {
      return ['tiers', element.tiers.id];
    }
    if (element && column.id == "responsable" && element.responsable && element.responsable.id) {
      return ['tiers/responsable', element.responsable.id];
    }
    if (element && column.id == "invoice" && element.invoice && element.invoice.id) {
      return ['invoicing/view', element.invoice.id];
    }
    if (element && column.id == "affaire" && element.affaire && element.affaire.id) {
      return ['affaire', element.affaire.id];
    }
    return null;
  }

  setAsRead(notification: Notification) {
    notification.isRead = true;
    this.notificationService.addOrUpdateNotification(notification).subscribe(reponse => {
      this.refresh();
    })
  }


  setAsUnread(notification: Notification) {
    notification.isRead = false;
    this.notificationService.addOrUpdateNotification(notification).subscribe(reponse => {
      this.refresh();
    })
  }

  setAllAsRead() {
    let promises = [];
    if (this.notifications)
      for (let notification of this.notifications) {
        if (!notification.isRead) {
          notification.isRead = true;
          promises.push(this.notificationService.addOrUpdateNotification(notification));
        }
      }
    combineLatest(promises).subscribe(response => this.refresh());
  }

  setAllAsUnread() {
    let promises = [];
    if (this.notifications)
      for (let notification of this.notifications) {
        if (notification.isRead) {
          notification.isRead = false;
          promises.push(this.notificationService.addOrUpdateNotification(notification));
        }
      }
    combineLatest(promises).subscribe(response => this.refresh());
  }

  delete(notification: Notification, isAll: boolean, isRead: boolean) {
    if (!isAll) {
      this.notificationService.deleteNotification(notification).subscribe(reponse => {
        this.refresh();
      })
    } else {
      const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
        maxWidth: "400px",
        data: {
          title: "Supprimer " + (isAll ? "toutes les" : "la") + " notification" + (isAll ? "s" : ""),
          content: "Êtes-vous sûr de vouloir supprimer " + (isAll ? "toutes les" : "cette") + " notification" + (isAll ? "s" : "") + (isRead ? ' lues' : ' non lues') + " ?",
          closeActionText: "Annuler",
          validationActionText: "Confirmer"
        }
      });

      dialogRef.afterClosed().subscribe(dialogResult => {
        if (dialogResult) {
          if (isAll)
            return this.deleteAll(isRead);
          this.notificationService.deleteNotification(notification).subscribe(reponse => {
            this.refresh();
          })
        }
      });
    }
  }


  deleteAll(isRead: boolean) {
    let promises = [];
    if (this.notifications)
      for (let notification of this.notifications) {
        if (isRead && notification.isRead || !isRead && !notification.isRead)
          promises.push(this.notificationService.deleteNotification(notification));
      }
    combineLatest(promises).subscribe(response => this.refresh());
  }


  translateDataLabel(item: string): string {
    let dictionnary = Dictionnary as any;
    if (dictionnary[item])
      return dictionnary[item];
    return item;
  }

  addPersonnalNotification() {
    const dialogRef = this.confirmationDialog.open(AddNotificationDialogComponent, {
      width: "80%",
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult) {
        this.notificationService.addPersonnalNotification(dialogResult).subscribe(response => {
          this.refresh();
        })
      }
    });
  }

  editNotification(event: any, notification: Notification) {
    const dialogRef = this.confirmationDialog.open(AddNotificationDialogComponent, {
      width: "80%",
    });

    dialogRef.componentInstance.notification = notification;

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult) {
        this.notificationService.addPersonnalNotification(dialogResult).subscribe(response => {
          this.refresh();
        })
      }
    });
  }
}
