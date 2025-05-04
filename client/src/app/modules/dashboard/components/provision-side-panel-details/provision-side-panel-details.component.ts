import { Component, Input, OnInit } from '@angular/core';
import { FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY, GUICHET_UNIQUE_BASE_URL, SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT_AUTHORITY } from 'src/app/libs/Constants';
import { AssoAffaireOrder } from 'src/app/modules/quotation/model/AssoAffaireOrder';
import { FormaliteGuichetUnique } from 'src/app/modules/quotation/model/guichet-unique/FormaliteGuichetUnique';
import { Provision } from 'src/app/modules/quotation/model/Provision';
import { AppService } from 'src/app/services/app.service';

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

  constructor(
    private appService: AppService
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
    this.appService.openRoute(event, 'provision/' + asso.id + "/" + provision.id, null);
  }

}
