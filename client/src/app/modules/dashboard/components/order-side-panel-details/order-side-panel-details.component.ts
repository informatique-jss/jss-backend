import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { formatDateFrance } from 'src/app/libs/FormatHelper';
import { callNumber, displayInTeams, prepareMail } from 'src/app/libs/MailHelper';
import { getAffaireFromAssoAffaireOrder, getCustomerOrderNameForTiers, getServiceFromService } from 'src/app/modules/invoicing/components/invoice-tools';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { SelectMultiServiceTypeDialogComponent } from 'src/app/modules/quotation/components/select-multi-service-type-dialog/select-multi-service-type-dialog.component';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { CustomerOrderStatus } from 'src/app/modules/quotation/model/CustomerOrderStatus';
import { Service } from 'src/app/modules/quotation/model/Service';
import { ServiceService } from 'src/app/modules/quotation/services/service.service';
import { CUSTOMER_ORDER_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';

@Component({
  selector: 'order-side-panel-details',
  templateUrl: './order-side-panel-details.component.html',
  styleUrls: ['./order-side-panel-details.component.css']
})
export class OrderSidePanelDetailsComponent implements OnInit {

  @Input() selectedEntity: CustomerOrder | undefined;
  @Input() possibleEntityStatus: CustomerOrderStatus[] = [];
  currentTabDisplayed: string = '';
  CUSTOMER_ORDER_ENTITY_TYPE = CUSTOMER_ORDER_ENTITY_TYPE;

  @Output() triggerRefreshEntity = new EventEmitter<void>();

  constructor(
    private appService: AppService,
    public mailLabelDialog: MatDialog,
    public confirmationDialog: MatDialog,
    public selectServiceTypeDialog: MatDialog,
    public quotationWorkflowDialog: MatDialog,
    private habilitationsService: HabilitationsService,
    private serviceService: ServiceService,
  ) { }

  ngOnInit() {
  }

  formatDateFrance = formatDateFrance;
  getCustomerOrderNameForTiers = getCustomerOrderNameForTiers;
  getAffaireFromAssoAffaireOrder = getAffaireFromAssoAffaireOrder;
  getServiceFromService = getServiceFromService;

  openTiers(event: any, order: CustomerOrder) {
    if (order.responsable && order.responsable.tiers)
      this.appService.openRoute(event, 'tiers/' + order.responsable.tiers.id, undefined);
  }

  openResponsable(event: any, order: CustomerOrder) {
    if (order.responsable)
      this.appService.openRoute(event, 'tiers/responsable/' + order.responsable.id, undefined);
  }

  sendResponsableMail(event: any, order: CustomerOrder) {
    if (order.responsable && order.responsable.mail)
      prepareMail(order.responsable.mail.mail, null, null);
  }

  callResponsable(event: any, order: CustomerOrder) {
    if (order.responsable && order.responsable.phones)
      callNumber(order.responsable.phones[0].phoneNumber);
  }


  displayInTeams = function (employee: Employee) {
    displayInTeams(employee);
  }

  getSpecialOffersLabel(order: CustomerOrder) {
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
