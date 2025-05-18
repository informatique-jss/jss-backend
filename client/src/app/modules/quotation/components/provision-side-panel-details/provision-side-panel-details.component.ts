import { Component, Input, OnInit } from '@angular/core';
import { FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY, GUICHET_UNIQUE_BASE_URL, SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT_AUTHORITY } from 'src/app/libs/Constants';
import { NotificationService } from 'src/app/modules/miscellaneous/services/notification.service';
import { AssoAffaireOrder } from 'src/app/modules/quotation/model/AssoAffaireOrder';
import { FormaliteGuichetUnique } from 'src/app/modules/quotation/model/guichet-unique/FormaliteGuichetUnique';
import { Provision } from 'src/app/modules/quotation/model/Provision';
import { PROVISION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { Notification } from '../../../miscellaneous/model/Notification';

@Component({
  selector: 'provision-side-panel-details',
  templateUrl: './provision-side-panel-details.component.html',
  styleUrls: ['./provision-side-panel-details.component.css']
})
export class ProvisionSidePanelDetailsComponent implements OnInit {

  @Input() assoAffaireOrder: AssoAffaireOrder | undefined;
  @Input() provision: Provision | undefined;
  @Input() displayStatus: boolean = true;

  SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT_AUTHORITY = SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT_AUTHORITY;
  FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY = FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY;
  PROVISION_ENTITY_TYPE = PROVISION_ENTITY_TYPE;

  currentTabDisplayed: string = '';
  provisionNotification: Notification[][] = [];

  constructor(
    private appService: AppService,
    private habilitationService: HabilitationsService,
    private notificationService: NotificationService
  ) { }

  ngOnInit() {
  }

  openFormaliteGu(formalite: FormaliteGuichetUnique) {
    if (formalite.isFormality)
      window.open(GUICHET_UNIQUE_BASE_URL + formalite.id, "_blank");
    if (formalite.isAnnualAccounts)
      window.open(GUICHET_UNIQUE_BASE_URL + "annual-accounts/" + formalite.id, "_blank");
    if (formalite.isActeDeposit)
      window.open(GUICHET_UNIQUE_BASE_URL + "acte-deposits/" + formalite.id, "_blank");
  }


  displayProvision(event: any, asso: AssoAffaireOrder, provision: Provision) {
    this.appService.openRoute({ ctrlKey: true }, 'provision/' + asso.id + "/" + provision.id, null);
  }

  addNewNotificationOnProvision(provision: Provision) {
    this.appService.addPersonnalNotification(() => this.provisionNotification = [], this.provisionNotification ? this.provisionNotification[provision.id] : undefined, undefined, provision, undefined, undefined, undefined, undefined, undefined, undefined);
  }

  getNotificationForProvision(provision: Provision) {
    if (this.provisionNotification[provision.id] == undefined) {
      this.provisionNotification[provision.id] = [];
      this.notificationService.getNotificationsForProvision(provision.id).subscribe(response => this.provisionNotification[provision.id] = response);
    }
    return this.provisionNotification[provision.id];
  }

  canDisplayNotifications() {
    return this.habilitationService.canDisplayNotifications();
  }

}
