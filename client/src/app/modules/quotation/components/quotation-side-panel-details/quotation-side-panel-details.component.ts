import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { formatDateFrance } from 'src/app/libs/FormatHelper';
import { callNumber, displayInTeams, prepareMail } from 'src/app/libs/MailHelper';
import { getAffaireFromAssoAffaireOrder, getCustomerOrderNameForTiers, getServiceFromService } from 'src/app/modules/invoicing/components/invoice-tools';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { NotificationService } from 'src/app/modules/miscellaneous/services/notification.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { SelectMultiServiceTypeDialogComponent } from 'src/app/modules/quotation/components/select-multi-service-type-dialog/select-multi-service-type-dialog.component';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { AssoAffaireOrder } from 'src/app/modules/quotation/model/AssoAffaireOrder';
import { Quotation } from 'src/app/modules/quotation/model/Quotation';
import { QuotationStatus } from 'src/app/modules/quotation/model/QuotationStatus';
import { Service } from 'src/app/modules/quotation/model/Service';
import { AffaireService } from 'src/app/modules/quotation/services/affaire.service';
import { AssoAffaireOrderService } from 'src/app/modules/quotation/services/asso.affaire.order.service';
import { ServiceService } from 'src/app/modules/quotation/services/service.service';
import { QUOTATION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { Notification } from '../../../miscellaneous/model/Notification';


@Component({
  selector: 'quotation-side-panel-details',
  templateUrl: './quotation-side-panel-details.component.html',
  styleUrls: ['./quotation-side-panel-details.component.css']
})
export class QuotationSidePanelDetailsComponent implements OnInit {

  @Input() selectedEntity: Quotation | undefined;
  @Input() possibleEntityStatus: QuotationStatus[] = [];
  currentTabDisplayed: string = '';
  QUOTATION_ENTITY_TYPE = QUOTATION_ENTITY_TYPE;
  affaireNotification: Notification[][] = [];

  totalServicesPrice: number = 0;

  @Output() triggerRefreshEntity = new EventEmitter<void>();

  constructor(
    private appService: AppService,
    public mailLabelDialog: MatDialog,
    public confirmationDialog: MatDialog,
    public selectServiceTypeDialog: MatDialog,
    public quotationWorkflowDialog: MatDialog,
    private habilitationsService: HabilitationsService,
    private serviceService: ServiceService,
    private affaireService: AffaireService,
    private notificationService: NotificationService,
    private formBuilder: FormBuilder,
    private habilitationService: HabilitationsService,
    private assoAffaireOrderService: AssoAffaireOrderService
  ) { }

  orderForm = this.formBuilder.group({});

  ngOnInit() {
    if (this.selectedEntity)
      this.assoAffaireOrderService.getAssoAffaireOrdersForQuotation(this.selectedEntity).subscribe(response => {
        this.selectedEntity!.assoAffaireOrders = response;
        for (let i = 0; i < this.selectedEntity!.assoAffaireOrders.length; i++) {
          this.affaireService.getAffaire(this.selectedEntity!.assoAffaireOrders[i].affaire.id).subscribe(response => {
            this.selectedEntity!.assoAffaireOrders[i].affaire = response;
            this.totalServicesPrice = this.totalServicesPrice + this.selectedEntity!.assoAffaireOrders[i].services.map(service => service.serviceTotalPrice).reduce((sum, current) => sum + current, 0);

          })
        }
      })
  }

  triggerRefreshEntityFn() {
    this.triggerRefreshEntity.next();
  }

  formatDateFrance = formatDateFrance;
  getCustomerOrderNameForTiers = getCustomerOrderNameForTiers;
  getAffaireFromAssoAffaireOrder = getAffaireFromAssoAffaireOrder;
  getServiceFromService = getServiceFromService;

  openTiers(event: any, order: Quotation) {
    if (order.responsable && order.responsable.tiers)
      this.appService.openRoute({ ctrlKey: true }, 'tiers/' + order.responsable.tiers.id, undefined);
  }

  openResponsable(event: any, order: Quotation) {
    if (order.responsable)
      this.appService.openRoute({ ctrlKey: true }, 'tiers/responsable/' + order.responsable.id, undefined);
  }

  sendResponsableMail(event: any, order: Quotation) {
    if (order.responsable && order.responsable.mail)
      prepareMail(order.responsable.mail.mail, null, null);
  }

  callResponsable(event: any, order: Quotation) {
    if (order.responsable && order.responsable.phones)
      callNumber(order.responsable.phones[0].phoneNumber);
  }

  addNewNotificationOnAffaire(affaire: Affaire) {
    this.appService.addPersonnalNotification(() => this.affaireNotification = [], this.affaireNotification ? this.affaireNotification[affaire.id] : undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, affaire);
  }

  getNotificationForAffaire(affaire: Affaire) {
    if (this.affaireNotification[affaire.id] == undefined) {
      this.affaireNotification[affaire.id] = [];
      this.notificationService.getNotificationsForAffaire(affaire.id).subscribe(response => this.affaireNotification[affaire.id] = response);
    }
    return this.affaireNotification[affaire.id];
  }

  canDisplayNotifications() {
    return this.habilitationService.canDisplayNotifications();
  }

  displayAffaire(event: any, asso: AssoAffaireOrder) {
    this.appService.openRoute({ ctrlKey: true }, '/affaire/' + asso.affaire.id, null);
  }


  displayInTeams = function (employee: Employee) {
    displayInTeams(employee);
  }

  getSpecialOffersLabel(order: Quotation) {
    if (order.specialOffers && order.specialOffers.length > 0)
      return order.specialOffers.map(item => item.customLabel).join(", ");
    return "";
  }

  deleteService(service: Service) {
    if (!this.habilitationsService.canByPassProvisionLockOnBilledOrder())
      return;
    const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Supprimer le service",
        content: "Êtes-vous sûr de vouloir continuer ?",
        closeActionText: "Annuler",
        validationActionText: "Confirmer"
      }
    });

    dialogRef.afterClosed().subscribe(response => {
      if (response) {
        this.serviceService.deleteService(service).subscribe(response => {
          this.triggerRefreshEntity.next();
        });
      }
    });
  }

  modifyService(service: Service) {
    const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Modifier le type de service",
        content: "Attention, la modification du type de service ajoutera les nouvelles prestations sans supprimer les prestations existantes. Pensez à vérifier les doublons après modification. Êtes-vous sûr de vouloir continuer ?",
        closeActionText: "Annuler",
        validationActionText: "Modifier"
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult && service) {
        const dialogRef2 = this.selectServiceTypeDialog.open(SelectMultiServiceTypeDialogComponent, {
          width: "50%",
        });
        dialogRef2.componentInstance.isJustSelectServiceType = true;
        dialogRef2.afterClosed().subscribe(dialogResult => {
          if (dialogResult && service && this.selectedEntity) {
            this.serviceService.modifyServiceType(service, dialogResult).subscribe(response => {
              this.triggerRefreshEntity.next();
            })
          }
        });
      }
    });
  }

}
