import { Component, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { AppService } from 'src/app/app.service';

@Component({
  selector: 'app-administration',
  templateUrl: './administration.component.html',
  styleUrls: ['./administration.component.css']
})
export class AdministrationComponent implements OnInit {

  selectedReferential: string = "";
  referentials: string[] = [] as Array<string>;
  selectedEntity: any | undefined;

  editMode: boolean = false;

  saveEvent: Subject<void> = new Subject<void>();
  addEvent: Subject<void> = new Subject<void>();

  ACT_TYPE_REFERENTIAL = "Devis - BODACC - Type d'actes";
  BODACC_PUBLICATION_TYPE_REFERENTIAL = "Devis - BODACC - Type de publication";
  BUILDING_DOMICILIATION_REFERENTIAL = "Devis - Domiciliation - Adresse de domiciliation";
  DOMICIALIATION_CONTRACT_TYPE_REFERENTIAL = "Devis - Domiciliation - Type de contrat";
  FUND_TYPE_REFERENTIAL = "Devis - BODACC - Type de fonds";
  JOURNAL_TYPE_REFERENTIAL = "Devis - SHAL - Type de journal";
  MAIL_REDIRECTION_TYPE_REFERENTIAL = "Devis - Domiciliation - Type de redirection courrier";
  NOTICE_TYPE_FAMILY_REFERENTIAL = "Devis - SHAL - Sous-rubrique de l'annonce";
  PROVISION_FAMILY_TYPE_REFERENTIAL = "Devis - Famille de prestation";
  QUOTATION_LABEL_TYPE_REFERENTIAL = "Devis - type de libellé sur devis";
  QUOTATION_STATUS_REFERENTIAL = "Devis - statuts du devis";
  RECORD_TYPE_REFERENTIAL = "Devis - type de dossier";
  TRANSFERT_FUNDS_TYPE_REFERENTIAL = "Devis - BODACC - type de transfert de fonds";
  BILLING_CLOSURE_RECIPIENT_TYPE_REFERENTIAL = "Divers - type de destinataire de document";
  BILLING_CLOSURE_TYPE_REFERENTIAL = "Divers - type de document";

  constructor(private appService: AppService,) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Administration");
    this.referentials.push(this.ACT_TYPE_REFERENTIAL);
    this.referentials.push(this.BODACC_PUBLICATION_TYPE_REFERENTIAL);
    this.referentials.push(this.BUILDING_DOMICILIATION_REFERENTIAL);
    this.referentials.push(this.DOMICIALIATION_CONTRACT_TYPE_REFERENTIAL);
    this.referentials.push(this.FUND_TYPE_REFERENTIAL);
    this.referentials.push(this.JOURNAL_TYPE_REFERENTIAL);
    this.referentials.push(this.MAIL_REDIRECTION_TYPE_REFERENTIAL);
    this.referentials.push(this.NOTICE_TYPE_FAMILY_REFERENTIAL);
    this.referentials.push(this.PROVISION_FAMILY_TYPE_REFERENTIAL);
    this.referentials.push(this.QUOTATION_LABEL_TYPE_REFERENTIAL);
    this.referentials.push(this.QUOTATION_STATUS_REFERENTIAL);
    this.referentials.push(this.RECORD_TYPE_REFERENTIAL);
    this.referentials.push(this.TRANSFERT_FUNDS_TYPE_REFERENTIAL);
    this.referentials.push(this.BILLING_CLOSURE_RECIPIENT_TYPE_REFERENTIAL);
    this.referentials.push(this.BILLING_CLOSURE_TYPE_REFERENTIAL);
    this.referentials.sort((a, b) => a.localeCompare(b));
  }

  saveEntity() {
    this.saveEvent.next();
    this.editMode = false;
  }

  createEntity() {
    this.addEvent.next();
    this.editMode = true;
  }

  editEntity() {
    this.editMode = true;
  }

  changeSelectedEntity(element: any) {
    this.selectedEntity = element;
  }

  changeReferential() {
    this.selectedEntity = undefined;
    this.editMode = false;
  }
}
