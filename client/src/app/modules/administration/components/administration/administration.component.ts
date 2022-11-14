import { AfterContentChecked, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, UrlSegment } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'app-administration',
  templateUrl: './administration.component.html',
  styleUrls: ['./administration.component.css']
})
export class AdministrationComponent implements OnInit, AfterContentChecked {

  selectedReferential: string = "";
  filteredReferentials: Observable<string[]> | undefined;
  referentials: string[] = [] as Array<string>;
  selectedEntity: any | undefined;

  editMode: boolean = false;

  saveEvent: Subject<void> = new Subject<void>();
  addEvent: Subject<void> = new Subject<void>();
  cloneEvent: Subject<void> = new Subject<void>();

  ACT_TYPE_REFERENTIAL = "Devis - BODACC - type d'actes";
  BODACC_PUBLICATION_TYPE_REFERENTIAL = "Devis - BODACC - type de publication";
  BUILDING_DOMICILIATION_REFERENTIAL = "Devis - Domiciliation - adresse de domiciliation";
  DOMICIALIATION_CONTRACT_TYPE_REFERENTIAL = "Devis - Domiciliation - type de contrat";
  FUND_TYPE_REFERENTIAL = "Devis - BODACC - type de fonds";
  JOURNAL_TYPE_REFERENTIAL = "Devis - Annonce - type de journal";
  MAIL_REDIRECTION_TYPE_REFERENTIAL = "Devis - Domiciliation - type de redirection courrier";
  NOTICE_TYPE_FAMILY_REFERENTIAL = "Devis - Annonce - famille de rubrique de l'annonce";
  NOTICE_TYPE = "Devis - Annonce - rubrique de l'annonce";
  PROVISION_FAMILY_TYPE_REFERENTIAL = "Devis - famille de prestation";
  PROVISION_TYPE_REFERENTIAL = "Devis - type de prestation";
  QUOTATION_LABEL_TYPE_REFERENTIAL = "Devis - type de libellé sur devis";
  QUOTATION_STATUS_REFERENTIAL = "Devis - statuts du devis";
  RECORD_TYPE_REFERENTIAL = "Devis - type de dossier";
  TRANSFERT_FUNDS_TYPE_REFERENTIAL = "Devis - BODACC - type de cession de fonds";
  BILLING_CLOSURE_RECIPIENT_TYPE_REFERENTIAL = "Divers - type de destinataire de document";
  BILLING_CLOSURE_TYPE_REFERENTIAL = "Divers - type de relevés de compte";
  BILLING_LABEL_TYPE_REFERENTIAL = "Divers - type de libellé sur facture";
  PAYMENT_DEALINE_TYPE_REFERENTIAL = "Divers - délais de paiement";
  REFUND_TYPE_REFERENTIAL = "Divers - type de remboursement";
  SUBSCRIPTION_PERIOD_TYPE_REFERENTIAL = "Divers - période d'abonnement";
  TIERS_CATEGORY_REFERENTIAL = "Tiers - catégorie de tiers";
  TIERS_FOLLOWUP_TYPE_REFERENTIAL = "Tiers - type de suivi";
  TIERS_TYPE_REFERENTIAL = "Tiers - type de tiers";
  ATTACHMENT_TYPE_REFERENTIAL = "Divers - type de pièces jointes";
  CIVILITY_REFERENTIAL = "Divers - civilité";
  COMPETENT_AUTHORITY_TYPE_REFERENTIAL = "Divers - type d'autorité compétente";
  COUNTRY_REFERENTIAL = "Divers - pays";
  DELIVERY_SERVICE_REFERENTIAL = "Divers - service de livraison";
  DOCUMENT_TYPE_REFERENTIAL = "Divers - type de document";
  LANGUAGE_REFERENTIAL = "Divers - langues";
  LEGAL_FORM_REFERENTIAL = "Divers - forme juridique";
  PAYMENT_TYPE_REFERENTIAL = "Divers - type de réglement";
  REGIONS_REFERENTIAL = "Divers - régions";
  VAT_REFERENTIAL = "Divers - taux de TVA";
  WEEK_DAY_REFERENTIAL = "Divers - jours de la semaine";
  CHARACTER_PRICE_REFERENTIAL = "Devis - Annonce - prix du caractère";
  ANNOUNCEMENT_NOTICE_TEMPLATE_REFERENTIAL = "Devis - Annonce - modèle d'annonce";
  BILLING_ITEM_REFERENTIAL = "Divers - poste de facturation";
  BILLING_TYPE_REFERENTIAL = "Divers - famille de poste de facturation";
  CITY_REFERENTIAL = "Divers - ville";
  DEPARTMENT_REFERENTIAL = "Divers - département";
  GIFT_REFERENTIAL = "Tiers - cadeau";
  SPECIAL_OFFER_REFERENTIAL = "Tiers - tarifs spéciaux";
  COMPETENT_AUTHORITY_REFERENTIAL = "Divers - autorité compétente";
  ACCOUNTING_ACCOUNT_CLASS_REFERENTIAL = "Comptabilité - classe de compte comptable";
  ACCOUNTING_ACCOUNT_REFERENTIAL = "Comptabilité - compte comptable";
  CONFRERE_REFERENTIAL = "Devis - Annonces - confrère";
  COMPETITOR_REFERENTIAL = "Tiers - concurrent";
  VAT_COLLECTION_TYPE_REFERENTIAL = "Divers - type d'encaissement de la TVA";
  PROVIDER_REFERENTIAL = "Divers - Fournisseur";
  ACCOUNTING_JOURNAL_REFERENTIAL = "Comptabilité - journal comptable";
  INVOICE_STAUTS_REFERENTIAL = "Facturation - statut de facture";
  PAYMENT_WAY_REFERENTIAL = "Facturation - sens de paiement";
  BILLING_REGIE = "Divers - régie";
  AFFAIRE_REFERENTIAL = "Devis - affaire";

  constructor(private appService: AppService,
    private formBuilder: FormBuilder,
    private activatedRoute: ActivatedRoute,
    private changeDetectorRef: ChangeDetectorRef
  ) { }

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
    this.referentials.push(this.CITY_REFERENTIAL);
    this.referentials.push(this.PROVISION_FAMILY_TYPE_REFERENTIAL);
    this.referentials.push(this.PROVISION_TYPE_REFERENTIAL);
    this.referentials.push(this.QUOTATION_LABEL_TYPE_REFERENTIAL);
    this.referentials.push(this.QUOTATION_STATUS_REFERENTIAL);
    this.referentials.push(this.RECORD_TYPE_REFERENTIAL);
    this.referentials.push(this.TRANSFERT_FUNDS_TYPE_REFERENTIAL);
    this.referentials.push(this.BILLING_CLOSURE_RECIPIENT_TYPE_REFERENTIAL);
    this.referentials.push(this.BILLING_CLOSURE_TYPE_REFERENTIAL);
    this.referentials.push(this.BILLING_LABEL_TYPE_REFERENTIAL);
    this.referentials.push(this.PAYMENT_DEALINE_TYPE_REFERENTIAL);
    this.referentials.push(this.REFUND_TYPE_REFERENTIAL);
    this.referentials.push(this.SUBSCRIPTION_PERIOD_TYPE_REFERENTIAL);
    this.referentials.push(this.TIERS_CATEGORY_REFERENTIAL);
    this.referentials.push(this.TIERS_FOLLOWUP_TYPE_REFERENTIAL);
    this.referentials.push(this.TIERS_TYPE_REFERENTIAL);
    this.referentials.push(this.ATTACHMENT_TYPE_REFERENTIAL);
    this.referentials.push(this.CIVILITY_REFERENTIAL);
    this.referentials.push(this.COMPETENT_AUTHORITY_TYPE_REFERENTIAL);
    this.referentials.push(this.COUNTRY_REFERENTIAL);
    this.referentials.push(this.DELIVERY_SERVICE_REFERENTIAL);
    this.referentials.push(this.DOCUMENT_TYPE_REFERENTIAL);
    this.referentials.push(this.LANGUAGE_REFERENTIAL);
    this.referentials.push(this.LEGAL_FORM_REFERENTIAL);
    this.referentials.push(this.PAYMENT_TYPE_REFERENTIAL);
    this.referentials.push(this.REGIONS_REFERENTIAL);
    this.referentials.push(this.VAT_REFERENTIAL);
    this.referentials.push(this.WEEK_DAY_REFERENTIAL);
    this.referentials.push(this.CHARACTER_PRICE_REFERENTIAL);
    this.referentials.push(this.NOTICE_TYPE);
    this.referentials.push(this.ANNOUNCEMENT_NOTICE_TEMPLATE_REFERENTIAL);
    this.referentials.push(this.BILLING_ITEM_REFERENTIAL);
    this.referentials.push(this.BILLING_TYPE_REFERENTIAL);
    this.referentials.push(this.DEPARTMENT_REFERENTIAL);
    this.referentials.push(this.GIFT_REFERENTIAL);
    this.referentials.push(this.SPECIAL_OFFER_REFERENTIAL);
    this.referentials.push(this.COMPETENT_AUTHORITY_REFERENTIAL);
    this.referentials.push(this.ACCOUNTING_ACCOUNT_CLASS_REFERENTIAL);
    this.referentials.push(this.ACCOUNTING_ACCOUNT_REFERENTIAL);
    this.referentials.push(this.CONFRERE_REFERENTIAL);
    this.referentials.push(this.COMPETITOR_REFERENTIAL);
    this.referentials.push(this.VAT_COLLECTION_TYPE_REFERENTIAL);
    this.referentials.push(this.PROVIDER_REFERENTIAL);
    this.referentials.push(this.ACCOUNTING_JOURNAL_REFERENTIAL);
    this.referentials.push(this.INVOICE_STAUTS_REFERENTIAL);
    this.referentials.push(this.PAYMENT_WAY_REFERENTIAL);
    this.referentials.push(this.BILLING_REGIE);
    this.referentials.push(this.AFFAIRE_REFERENTIAL);
    this.referentials.sort((a, b) => a.localeCompare(b));

    this.filteredReferentials = this.referentialForm.get("entity")?.valueChanges.pipe(
      startWith(''),
      map(value => value ? this.referentials.filter(referential => referential.toUpperCase().indexOf(value?.toUpperCase().trim()) >= 0) : [] as Array<string>,
      ));

    let url: UrlSegment[] = this.activatedRoute.snapshot.url;
    if (url != undefined && url != null && url[0] != undefined &&  url[1]!=undefined && url[1].path == "affaire") {
      this.selectedReferential = this.AFFAIRE_REFERENTIAL;
    }
  }

  referentialForm = this.formBuilder.group({
    entity: ['', []],
  });

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  saveEntity() {
    if (this.selectedEntity) {
      this.saveEvent.next();
    }
  }

  createEntity() {
    this.addEvent.next();
    this.selectedEntity = {};
    this.editMode = true;
  }

  editEntity() {
    this.editMode = true;
  }

  cloneEntity() {
    this.cloneEvent.next();
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

