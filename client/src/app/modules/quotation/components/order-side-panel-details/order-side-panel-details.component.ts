import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { formatDateFrance } from 'src/app/libs/FormatHelper';
import { callNumber, displayInTeams, prepareMail } from 'src/app/libs/MailHelper';
import { getAffaireFromAssoAffaireOrder, getCustomerOrderNameForTiers, getServiceFromService } from 'src/app/modules/invoicing/components/invoice-tools';
import { NotificationService } from 'src/app/modules/miscellaneous/services/notification.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { CustomerOrderStatus } from 'src/app/modules/quotation/model/CustomerOrderStatus';
import { AssoAffaireOrderService } from 'src/app/modules/quotation/services/asso.affaire.order.service';
import { CUSTOMER_ORDER_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { Notification } from '../../../miscellaneous/model/Notification';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { AffaireService } from '../../services/affaire.service';

@Component({
  selector: 'order-side-panel-details',
  templateUrl: './order-side-panel-details.component.html',
  styleUrls: ['./order-side-panel-details.component.css'],
  standalone: false
})
export class OrderSidePanelDetailsComponent implements OnInit {

  @Input() selectedEntity: CustomerOrder | undefined;
  @Input() possibleEntityStatus: CustomerOrderStatus[] = [];
  currentTabDisplayed: string = '';
  CUSTOMER_ORDER_ENTITY_TYPE = CUSTOMER_ORDER_ENTITY_TYPE;
  affaireNotification: Notification[][] = [];

  @Output() triggerRefreshEntity = new EventEmitter<void>();

  constructor(
    private appService: AppService,
    public mailLabelDialog: MatDialog,
    public quotationWorkflowDialog: MatDialog,
    private affaireService: AffaireService,
    private notificationService: NotificationService,
    private formBuilder: FormBuilder,
    private habilitationService: HabilitationsService,
    private assoAffaireOrderService: AssoAffaireOrderService
  ) { }

  orderForm = this.formBuilder.group({});

  ngOnInit() {
    if (this.selectedEntity)
      this.assoAffaireOrderService.getAssoAffaireOrdersForCustomerOrder(this.selectedEntity).subscribe(response => {
        this.selectedEntity!.assoAffaireOrders = response;
        for (let i = 0; i < this.selectedEntity!.assoAffaireOrders.length; i++) {
          this.affaireService.getAffaire(this.selectedEntity!.assoAffaireOrders[i].affaire.id).subscribe(response => {
            this.selectedEntity!.assoAffaireOrders[i].affaire = response;
          })
        }
      })
  }

  formatDateFrance = formatDateFrance;
  getCustomerOrderNameForTiers = getCustomerOrderNameForTiers;
  getAffaireFromAssoAffaireOrder = getAffaireFromAssoAffaireOrder;
  getServiceFromService = getServiceFromService;

  openTiers(event: any, order: CustomerOrder) {
    if (order.responsable && order.responsable.tiers)
      this.appService.openRoute({ ctrlKey: true }, 'tiers/' + order.responsable.tiers.id, undefined);
  }

  triggerRefreshEntityFn() {
    this.triggerRefreshEntity.next();
  }

  openResponsable(event: any, order: CustomerOrder) {
    if (order.responsable)
      this.appService.openRoute({ ctrlKey: true }, 'tiers/responsable/' + order.responsable.id, undefined);
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

  updateAssignedToForAffaire(employee: any, asso: AssoAffaireOrder) {
    this.assoAffaireOrderService.updateAssignedToForAsso(asso, employee).subscribe(response => {
    });
  }

  canDisplayNotifications() {
    return this.habilitationService.canDisplayNotifications();
  }

  displayAffaire(event: any, asso: AssoAffaireOrder) {
    this.appService.openRoute({ ctrlKey: true }, '/affaire/' + asso.affaire.id, null);
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
}
