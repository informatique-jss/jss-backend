import { AfterContentChecked, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { ActivatedRoute, UrlSegment } from '@angular/router';
import { Observable, Subject, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';

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

  saveObservableSubscription: Subscription = new Subscription;

  ACT_TYPE_REFERENTIAL = "Type d'actes";
  BUILDING_DOMICILIATION_REFERENTIAL = "Adresse de domiciliation";
  DOMICIALIATION_CONTRACT_TYPE_REFERENTIAL = "Type de contrat";
  FUND_TYPE_REFERENTIAL = "Type de fonds";
  JOURNAL_TYPE_REFERENTIAL = "Type de journal";
  MAIL_REDIRECTION_TYPE_REFERENTIAL = "Type de redirection courrier";
  NOTICE_TYPE_FAMILY_REFERENTIAL = "Famille de rubrique de l'annonce";
  NOTICE_TYPE = "Rubrique de l'annonce";
  PROVISION_FAMILY_TYPE_REFERENTIAL = "Famille de prestation";
  PROVISION_TYPE_REFERENTIAL = "Type de prestation";
  RECORD_TYPE_REFERENTIAL = "Type de dossier";
  TRANSFERT_FUNDS_TYPE_REFERENTIAL = "Type de cession de fonds";
  BILLING_CLOSURE_RECIPIENT_TYPE_REFERENTIAL = "Type de destinataire de relevé comptable";
  BILLING_CLOSURE_TYPE_REFERENTIAL = "Type de relevés de compte";
  BILLING_LABEL_TYPE_REFERENTIAL = "Type de libellé sur facture";
  PAYMENT_DEALINE_TYPE_REFERENTIAL = "Délais de paiement";
  REFUND_TYPE_REFERENTIAL = "Type de remboursement";
  SUBSCRIPTION_PERIOD_TYPE_REFERENTIAL = "Période d'abonnement";
  TIERS_CATEGORY_REFERENTIAL = "Catégorie de tiers";
  TIERS_FOLLOWUP_TYPE_REFERENTIAL = "Type de suivi";
  TIERS_TYPE_REFERENTIAL = "Type de tiers";
  ATTACHMENT_TYPE_REFERENTIAL = "Type de pièces jointes";
  TYPE_DOCUMENT_REFERENTIAL = "Type de pièces jointes - INPI";
  CIVILITY_REFERENTIAL = "Civilité";
  COMPETENT_AUTHORITY_TYPE_REFERENTIAL = "Type d'autorité compétente";
  COUNTRY_REFERENTIAL = "Pays";
  DELIVERY_SERVICE_REFERENTIAL = "Service de livraison";
  DOCUMENT_TYPE_REFERENTIAL = "Type de document";
  LANGUAGE_REFERENTIAL = "Langues";
  LEGAL_FORM_REFERENTIAL = "Forme juridique";
  PAYMENT_TYPE_REFERENTIAL = "Type de réglement";
  REGIONS_REFERENTIAL = "Régions";
  VAT_REFERENTIAL = "Taux de TVA";
  WEEK_DAY_REFERENTIAL = "Jours de la semaine";
  CHARACTER_PRICE_REFERENTIAL = "Prix du caractère";
  ANNOUNCEMENT_NOTICE_TEMPLATE_REFERENTIAL = "Modèle d'annonce";
  BILLING_ITEM_REFERENTIAL = "Poste de facturation";
  BILLING_TYPE_REFERENTIAL = "Famille de poste de facturation";
  CITY_REFERENTIAL = "Ville";
  DEPARTMENT_REFERENTIAL = "Département";
  GIFT_REFERENTIAL = "Cadeau";
  SPECIAL_OFFER_REFERENTIAL = "Tarifs spéciaux";
  ACCOUNTING_ACCOUNT_CLASS_REFERENTIAL = "Classe de compte comptable";
  ACCOUNTING_ACCOUNT_REFERENTIAL = "Compte comptable";
  CONFRERE_REFERENTIAL = "Confrère";
  COMPETITOR_REFERENTIAL = "Concurrent";
  VAT_COLLECTION_TYPE_REFERENTIAL = "Type d'encaissement de la TVA";
  ACCOUNTING_JOURNAL_REFERENTIAL = "Journal comptable";
  INVOICE_STAUTS_REFERENTIAL = "Statut de facture";
  PAYMENT_WAY_REFERENTIAL = "Sens de paiement";
  BILLING_REGIE = "Régie";
  PRINCIPAL_ACCOUNTING_ACCOUNT_REFERENTIAL = "Compte comptable principal";
  CUSTOMER_ORDER_ORIGIN_REFERENTIAL = "Origine des commandes";
  DEPARTMENT_VAT_SETTING_REFERENTIAL = "TVA par département";
  RFF_FREQUENCY_REFERENTIAL = "Périodicité des RFF";
  SERVICE_TYPE_REFERENTIAL = "Service";
  COMPLAIN_PROBLEM_REFERENTIAL = "Reclamation - Probleme de vente";
  COMPLAIN_CAUSE_REFERENTIAL = "Reclamation - Cause du probleme de vente";
  COMPLAIN_ORIGIN_REFERENTIAL = "Reclamation - Origine du probleme de vente";
  QUOTATION_ABANDON_REASON_REFERENTIAL = "Raison d'abandon des commandes/devis";
  SERVICE_FAMILY_REFERENTIAL = "Famille de service";
  SERVICE_FAMILY_GROUP_REFERENTIAL = "Groupe de famille de service";
  CUSTOMER_ORDER_FREQUENCY_REFERENTIAL = "Fréquence des commandes récurrentes";

  constructor(private appService: AppService,
    private formBuilder: FormBuilder,
    private activatedRoute: ActivatedRoute,
    private habilitationService: HabilitationsService,
    private changeDetectorRef: ChangeDetectorRef,
    private userPreferenceService: UserPreferenceService
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Administration");
    this.restoreTab();
    this.referentials.push(this.ACT_TYPE_REFERENTIAL);
    this.referentials.push(this.BUILDING_DOMICILIATION_REFERENTIAL);
    this.referentials.push(this.DOMICIALIATION_CONTRACT_TYPE_REFERENTIAL);
    this.referentials.push(this.FUND_TYPE_REFERENTIAL);
    this.referentials.push(this.JOURNAL_TYPE_REFERENTIAL);
    this.referentials.push(this.MAIL_REDIRECTION_TYPE_REFERENTIAL);
    this.referentials.push(this.NOTICE_TYPE_FAMILY_REFERENTIAL);
    this.referentials.push(this.CITY_REFERENTIAL);
    this.referentials.push(this.PROVISION_FAMILY_TYPE_REFERENTIAL);
    this.referentials.push(this.PROVISION_TYPE_REFERENTIAL);
    this.referentials.push(this.RECORD_TYPE_REFERENTIAL);
    this.referentials.push(this.TRANSFERT_FUNDS_TYPE_REFERENTIAL);
    this.referentials.push(this.BILLING_CLOSURE_RECIPIENT_TYPE_REFERENTIAL);
    this.referentials.push(this.BILLING_CLOSURE_TYPE_REFERENTIAL);
    this.referentials.push(this.BILLING_LABEL_TYPE_REFERENTIAL);
    this.referentials.push(this.PAYMENT_DEALINE_TYPE_REFERENTIAL);
    this.referentials.push(this.REFUND_TYPE_REFERENTIAL);
    this.referentials.push(this.SUBSCRIPTION_PERIOD_TYPE_REFERENTIAL);
    this.referentials.push(this.TIERS_CATEGORY_REFERENTIAL);
    this.referentials.push(this.RFF_FREQUENCY_REFERENTIAL);
    this.referentials.push(this.TIERS_FOLLOWUP_TYPE_REFERENTIAL);
    this.referentials.push(this.TIERS_TYPE_REFERENTIAL);
    this.referentials.push(this.ATTACHMENT_TYPE_REFERENTIAL);
    this.referentials.push(this.TYPE_DOCUMENT_REFERENTIAL);
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
    this.referentials.push(this.CUSTOMER_ORDER_FREQUENCY_REFERENTIAL);
    this.referentials.push(this.GIFT_REFERENTIAL);
    this.referentials.push(this.SPECIAL_OFFER_REFERENTIAL);
    this.referentials.push(this.ACCOUNTING_ACCOUNT_CLASS_REFERENTIAL);
    this.referentials.push(this.ACCOUNTING_ACCOUNT_REFERENTIAL);
    this.referentials.push(this.COMPETITOR_REFERENTIAL);
    this.referentials.push(this.VAT_COLLECTION_TYPE_REFERENTIAL);
    this.referentials.push(this.ACCOUNTING_JOURNAL_REFERENTIAL);
    this.referentials.push(this.INVOICE_STAUTS_REFERENTIAL);
    this.referentials.push(this.PAYMENT_WAY_REFERENTIAL);
    this.referentials.push(this.BILLING_REGIE);
    this.referentials.push(this.PRINCIPAL_ACCOUNTING_ACCOUNT_REFERENTIAL);
    this.referentials.push(this.CUSTOMER_ORDER_ORIGIN_REFERENTIAL);
    this.referentials.push(this.DEPARTMENT_VAT_SETTING_REFERENTIAL);
    this.referentials.push(this.COMPLAIN_PROBLEM_REFERENTIAL);
    this.referentials.push(this.COMPLAIN_CAUSE_REFERENTIAL);
    this.referentials.push(this.COMPLAIN_ORIGIN_REFERENTIAL);
    this.referentials.push(this.SERVICE_TYPE_REFERENTIAL);
    this.referentials.push(this.QUOTATION_ABANDON_REASON_REFERENTIAL);
    this.referentials.push(this.SERVICE_FAMILY_REFERENTIAL);
    this.referentials.push(this.SERVICE_FAMILY_GROUP_REFERENTIAL);
    this.referentials.sort((a, b) => a.localeCompare(b));

    this.filteredReferentials = this.referentialForm.get("entity")?.valueChanges.pipe(
      startWith(''),
      map(value => value ? this.referentials.filter(referential => referential.toUpperCase().indexOf(value?.toUpperCase().trim()) >= 0) : [] as Array<string>,
      ));

    let url: UrlSegment[] = this.activatedRoute.snapshot.url;
    if (url != undefined && url != null && url[0] != undefined && url[1] != undefined && url[1].path == "confrere") {
      this.selectedReferential = this.CONFRERE_REFERENTIAL;
    }

    this.saveObservableSubscription = this.appService.saveObservable.subscribe(response => {
      if (response)
        if (this.editMode)
          this.saveEntity()
        else if (this.selectedEntity)
          this.editEntity()
    });
  }

  ngOnDestroy() {
    this.saveObservableSubscription.unsubscribe();
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

  canViewLogdModule() {
    return this.habilitationService.canViewLogModule();
  }

  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('administration', event.index);
  }

  restoreTab() {
    this.index = this.userPreferenceService.getUserTabsSelectionIndex('administration');
  }
}

