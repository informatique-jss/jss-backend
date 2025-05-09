import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Notification } from '../../app/modules/miscellaneous/model/Notification';
import { AddNotificationDialogComponent } from '../modules/miscellaneous/components/add-notification-dialog/add-notification-dialog.component';
import { NotificationService } from '../modules/miscellaneous/services/notification.service';
import { Affaire } from '../modules/quotation/model/Affaire';
import { CustomerOrder } from '../modules/quotation/model/CustomerOrder';
import { Invoice } from '../modules/quotation/model/Invoice';
import { Provision } from '../modules/quotation/model/Provision';
import { Quotation } from '../modules/quotation/model/Quotation';
import { Service } from '../modules/quotation/model/Service';
import { Responsable } from '../modules/tiers/model/Responsable';
import { Tiers } from '../modules/tiers/model/Tiers';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  private title: BehaviorSubject<string> = new BehaviorSubject<string>("title");
  titleObservable = this.title.asObservable();

  private save: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  saveObservable = this.save.asObservable();

  private sidenavOpenState: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  sidenavOpenStateObservable = this.sidenavOpenState.asObservable();

  constructor(
    private snackBar: MatSnackBar,
    private router: Router,
    private confirmationDialog: MatDialog,
    private notificationService: NotificationService
  ) { }

  changeHeaderTitle(title: string) {
    this.title.next(title);
  }

  changeSidenavOpenState(sidenavOpenState: boolean) {
    this.sidenavOpenState.next(sidenavOpenState);
  }

  triggerSaveEvent() {
    this.save.next(true);
  }

  displaySnackBar(message: string, isError: boolean, duration: number) {
    let sb = this.snackBar.open(message, 'Fermer', {
      duration: duration * 1000, panelClass: [isError ? "red-snackbar" : "blue-snackbar"]
    });
    sb.onAction().subscribe(() => {
      sb.dismiss();
    });
  }

  /**
   * Open given route taking account of user action to do it (simple click, ctrl + click, middle click)
   * @param event : click event given by angular
   * @param route  : route to open
   * @param sameWindowEndFonction : function to execute at the end of open with simple click
   * @returns
   */
  openRoute(event: any, route: string, sameWindowEndFonction: any) {
    if (event && (event.ctrlKey || event.button && event.button == "1")) {
      window.open(location.origin + "/" + route, "_blank");
    } else {
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() =>
        this.router.navigate(['/' + route])
      );
      if (sameWindowEndFonction)
        sameWindowEndFonction();
    }
    return;
  }

  /**
   * Open given route to MyJss website on a new tab
   * @param route  : route to open
   * @returns
   */
  openMyJssRoute(route: string) {
    window.open(environment.frontendMyJSSUrl + route, "_blank");
    return;
  }

  addPersonnalNotification(
    callback: (() => void),
    preLoadedNotifications: Notification[] | undefined,
    order: CustomerOrder | undefined,
    provision: Provision | undefined,
    service: Service | undefined,
    invoice: Invoice | undefined,
    quotation: Quotation | undefined,
    tiers: Tiers | undefined,
    responsable: Responsable | undefined,
    affaire: Affaire | undefined,
  ) {
    const dialogRef = this.confirmationDialog.open(AddNotificationDialogComponent, {
      width: "80%",
    });

    dialogRef.componentInstance.preLoadedNotifications = preLoadedNotifications;
    dialogRef.componentInstance.notification.customerOrder = order;
    dialogRef.componentInstance.notification.provision = provision;
    dialogRef.componentInstance.notification.service = service;
    dialogRef.componentInstance.notification.invoice = invoice;
    dialogRef.componentInstance.notification.quotation = quotation;
    dialogRef.componentInstance.notification.tiers = tiers;
    dialogRef.componentInstance.notification.responsable = responsable;
    dialogRef.componentInstance.notification.affaire = affaire;
    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult) {
        this.notificationService.addPersonnalNotification(dialogResult).subscribe(response => {
          callback();
          this.notificationService.refreshNotificationsNumber();
        });
      } else {
        callback();
      }
    });
  }

}
