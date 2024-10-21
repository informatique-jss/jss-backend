import { Component, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatTabGroup } from '@angular/material/tabs';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { formatDateFrance } from 'src/app/libs/FormatHelper';
import { ServiceService } from 'src/app/modules/quotation/services/service.service';
import { ConstantService } from '../../modules/miscellaneous/services/constant.service';
import { AppService } from '../../services/app.service';
import { EntityType } from './EntityType';
import { IndexEntity } from './IndexEntity';
import { IndexEntityService } from './index.entity.service';

export const OFX_ENTITY_TYPE: EntityType = { entityType: 'Ofx', tabName: 'Ofx', entryPoint: 'ofx' };
export const TIERS_ENTITY_TYPE: EntityType = { entityType: 'Tiers', tabName: 'Tiers', entryPoint: 'tiers' };
export const RESPONSABLE_ENTITY_TYPE: EntityType = { entityType: 'Responsable', tabName: 'Responsable', entryPoint: 'tiers/responsable' };
export const CONFRERE_ENTITY_TYPE: EntityType = { entityType: 'Confrere', tabName: 'Confrère', entryPoint: 'confrere' };
export const PROVIDER_ENTITY_TYPE: EntityType = { entityType: 'Provider', tabName: 'Fournisseur', entryPoint: 'administration/provider' };
export const COMPETENT_AUTHORITY_ENTITY_TYPE: EntityType = { entityType: 'CompetentAuthority', tabName: 'Autorité compétente', entryPoint: 'administration/competent/authority' };
export const QUOTATION_ENTITY_TYPE: EntityType = { entityType: 'Quotation', tabName: 'Devis', entryPoint: 'quotation' };
export const CUSTOMER_ORDER_ENTITY_TYPE: EntityType = { entityType: 'CustomerOrder', tabName: 'Commande', entryPoint: 'order' };
export const DOMICILIATION_ENTITY_TYPE: EntityType = { entityType: 'Domiciliation', tabName: 'Domiciliation', entryPoint: 'quotation/domiciliation' };
export const ANNOUNCEMENT_ENTITY_TYPE: EntityType = { entityType: 'Announcement', tabName: 'Announcement', entryPoint: 'quotation/announcement' };
export const FORMALITE_ENTITY_TYPE: EntityType = { entityType: 'Formalite', tabName: 'Formalité', entryPoint: 'formalite' };
export const PROVISION_ENTITY_TYPE: EntityType = { entityType: 'Provision', tabName: 'Prestation', entryPoint: 'provision/null' };
export const SERVICE_ENTITY_TYPE: EntityType = { entityType: 'Service', tabName: 'Service', entryPoint: 'service' };
export const ASSO_AFFAIRE_ENTITY_TYPE: EntityType = { entityType: 'AssoAffaireOrder', tabName: 'Prestations', entryPoint: 'provision' };
export const AFFAIRE_ENTITY_TYPE: EntityType = { entityType: 'Affaire', tabName: 'Affaires', entryPoint: 'affaire' };
export const INVOICE_ENTITY_TYPE: EntityType = { entityType: 'Invoice', tabName: 'Factures', entryPoint: 'invoicing/view' };
export const JOURNAL_ENTITY_TYPE: EntityType = { entityType: 'Journal', tabName: 'Journaux', entryPoint: 'journal' };
export const SIMPLE_PROVISION_ENTITY_TYPE: EntityType = { entityType: 'SimpleProvision', tabName: 'Formalité simple', entryPoint: 'simpleProvision' };
export const AZURE_INVOICE_ENTITY_TYPE: EntityType = { entityType: 'AzureInvoice', tabName: 'Facture automatique', entryPoint: 'invoicing/azure/edit' };
export const PAYMENT_ENTITY_TYPE: EntityType = { entityType: 'Payment', tabName: 'Paiements', entryPoint: 'invoicing/payment' };
export const REFUND_ENTITY_TYPE: EntityType = { entityType: 'Refund', tabName: 'Remboursements', entryPoint: 'invoicing/refund' };
export const BANK_TRANSFERT_ENTITY_TYPE: EntityType = { entityType: 'BankTransfert', tabName: 'Virements', entryPoint: 'invoicing/bankTransfert' };
export const DIRECT_DEBIT_TRANSFERT_ENTITY_TYPE: EntityType = { entityType: 'DirectDebitTransfert', tabName: 'Prélèvements', entryPoint: 'invoicing/directDebit' };
export const ASSO_SERVICE_DOCUMENT_ENTITY_TYPE: EntityType = { entityType: 'AssoServiceDocument', tabName: 'Documents du service', entryPoint: 'quotation/service' };
export const TYPE_DOCUMENT_ATTACHMENT_TYPE: EntityType = { entityType: 'TypeDocument', tabName: 'Type de document', entryPoint: 'administration/type-document' };

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})

export class SearchComponent implements OnInit {

  constructor(private searchDialogRef: MatDialogRef<SearchComponent>,
    protected indexEntityService: IndexEntityService,
    private appService: AppService,
    private serviceService: ServiceService,
    private constantService: ConstantService,
    protected formBuilder: UntypedFormBuilder,
    private router: Router) { }

  TIERS_ENTITY_TYPE = TIERS_ENTITY_TYPE;
  RESPONSABLE_ENTITY_TYPE = RESPONSABLE_ENTITY_TYPE;
  QUOTATION_ENTITY_TYPE = QUOTATION_ENTITY_TYPE;
  CUSTOMER_ORDER_ENTITY_TYPE = CUSTOMER_ORDER_ENTITY_TYPE;
  INVOICE_ENTITY_TYPE = INVOICE_ENTITY_TYPE;
  ASSO_AFFAIRE_ENTITY_TYPE = ASSO_AFFAIRE_ENTITY_TYPE;
  CONFRERE_ENTITY_TYPE = CONFRERE_ENTITY_TYPE;
  PROVIDER_ENTITY_TYPE = PROVIDER_ENTITY_TYPE;
  AFFAIRE_ENTITY_TYPE = AFFAIRE_ENTITY_TYPE;
  PAYMENT_ENTITY_TYPE = PAYMENT_ENTITY_TYPE;
  REFUND_ENTITY_TYPE = REFUND_ENTITY_TYPE;
  BANK_TRANSFERT_ENTITY_TYPE = BANK_TRANSFERT_ENTITY_TYPE;
  DIRECT_DEBIT_TRANSFERT_ENTITY_TYPE = DIRECT_DEBIT_TRANSFERT_ENTITY_TYPE;
  entityTypes: EntityType[] = [TIERS_ENTITY_TYPE, RESPONSABLE_ENTITY_TYPE, QUOTATION_ENTITY_TYPE, CUSTOMER_ORDER_ENTITY_TYPE,
    INVOICE_ENTITY_TYPE, ASSO_AFFAIRE_ENTITY_TYPE, AFFAIRE_ENTITY_TYPE,
    PAYMENT_ENTITY_TYPE, REFUND_ENTITY_TYPE, BANK_TRANSFERT_ENTITY_TYPE, DIRECT_DEBIT_TRANSFERT_ENTITY_TYPE];
  userSelectedModule: EntityType | null = null;
  @ViewChild(MatTabGroup) tabGroup: MatTabGroup | undefined;

  searchObservableRef: Subscription | undefined;
  search: string = "";
  foundEntities: IndexEntity[] | null = null;
  tabNames: { [key: string]: string; } = {};
  tabNumber: { [key: string]: number; } = {};

  isLoading: boolean | undefined = undefined;

  ngOnInit() {
    this.generateTabLabel();
  }

  closeDialog() {
    this.searchDialogRef.close(null);
  }

  searchForm = this.formBuilder.group({
    search: ['']
  });

  ngOnDestroy() {
    if (this.searchObservableRef)
      this.searchObservableRef.unsubscribe();
  }

  debounce: any;

  searchEntities() {
    clearTimeout(this.debounce);
    this.debounce = setTimeout(() => {
      this.effectiveSearchEntities();
    }, 500);
  }

  effectiveSearchEntities() {
    if (this.searchObservableRef)
      this.searchObservableRef.unsubscribe();

    if (this.search != null && this.search != undefined) {
      if (this.search.length >= 2) {
        this.isLoading = true;
        this.searchObservableRef = this.indexEntityService.searchEntities(this.search).subscribe(response => {
          this.isLoading = false;
          if (response) {
            this.foundEntities = [] as Array<IndexEntity>;
            for (let foundEntity of response) {
              if (foundEntity && foundEntity.text) {
                foundEntity.text = JSON.parse((foundEntity.text as string));
                this.foundEntities?.push(foundEntity);
              }
            }
            this.generateTabLabel();
            this.selectTab();
          }
        })
      } else {
        this.foundEntities = null;
      }
      this.generateTabLabel();
      this.selectTab();
    }
  }

  getTabLabel(entityType: EntityType): string {
    if (this.tabNames != null && this.tabNames != undefined)
      return this.tabNames[entityType.entityType] != undefined ? this.tabNames[entityType.entityType] : "";
    return "";
  }

  displayTab(entityType: EntityType): boolean {
    if (this.tabNumber != null && this.tabNumber != undefined)
      return this.tabNumber[entityType.entityType] ? true : false;
    return false;
  }

  generateTabLabel() {
    this.entityTypes.forEach(entityType => {
      let tabName = "";
      let entityNumber = 0;
      tabName += entityType.tabName;
      if (this.foundEntities != null && this.foundEntities != undefined) {
        this.foundEntities.forEach(foundEntity => {
          if (foundEntity.entityType == entityType.entityType)
            entityNumber++;
        })
        tabName += " (" + entityNumber + ")"
      }
      this.tabNames[entityType.entityType] = tabName;
      this.tabNumber[entityType.entityType] = entityNumber;
    })
  }

  getEntitiesForTab(entityType: EntityType): IndexEntity[] {
    let tabEntities = [] as Array<IndexEntity>;
    if (this.foundEntities != null) {
      this.foundEntities.forEach(foundEntity => {
        if (foundEntity.entityType == entityType.entityType)
          tabEntities.push(foundEntity);
      })
    }
    if (tabEntities && tabEntities.length > 0)
      return tabEntities.sort(function (a: IndexEntity, b: IndexEntity) {
        if (!a.createdDate && b.createdDate)
          return 1;
        if (!b.createdDate && a.createdDate)
          return -1;
        if (!b.createdDate && !a.createdDate)
          return 0;
        return new Date(b.createdDate).getTime() - new Date(a.createdDate).getTime();
      });
    return tabEntities;
  }

  openEntity(event: any, indexEntity: IndexEntity) {
    this.entityTypes.forEach(entityType => {
      if (indexEntity.entityType == entityType.entityType) {
        this.appService.openRoute(event, '/' + entityType.entryPoint + '/' + indexEntity.entityId, this.searchDialogRef.close());
      }
      return;
    })
  }

  selectTab() {
    // select tab : input tab if provide else first tab of first fetch entity (because it's the most relevant)
    if (this.userSelectedModule != null) {
      let selectedIndex = 0;
      for (let i = 0; i < this.entityTypes.length; i++) {
        const entityType = this.entityTypes[i];
        if (entityType == this.userSelectedModule && this.tabGroup) {
          this.tabGroup.selectedIndex = selectedIndex;
        }
        if (this.displayTab(entityType))
          selectedIndex++;
      }
    } else if (this.foundEntities != null && this.foundEntities.length > 0) {
      let selectedIndex = 0;
      for (let i = 0; i < this.entityTypes.length; i++) {
        const entityType = this.entityTypes[i];
        if (entityType.entityType == this.foundEntities[0].entityType && this.tabGroup)
          this.tabGroup.selectedIndex = selectedIndex;
        if (this.displayTab(entityType))
          selectedIndex++;
      }
    }
  }

  getProvisionLabel(entity: any) {
    let out = [];
    if (entity.text.services && entity.text.customerOrder) {
      for (let service of entity.text.services) {
        if (service.serviceType)
          out.push(service.serviceType.label);
        for (let provision of service.provisions)
          out.push((provision.assignedTo ? provision.assignedTo.firstname + " " + provision.assignedTo.lastname : "")
            + (provision.simpleProvision && provision.simpleProvision.simpleProvisionStatus ? " - " + provision.simpleProvision.simpleProvisionStatus.label : "")
            + (provision.formalite && provision.formalite.formaliteStatus ? " - " + provision.formalite.formaliteStatus.label : "")
          );
      }
      return out.join(" / ") + " / Commande " + entity.text.customerOrder.id;
    }
    return "";
  }

  getOrderingCustomerForInvoice(entity: any) {
    if (entity.text.tiers)
      return entity.text.tiers.denomination ? entity.text.tiers.denomination : (entity.text.tiers.firstname + ' ' + entity.text.tiers.lastname);
    if (entity.text.responsable)
      return entity.text.responsable.firstname + ' ' + entity.text.responsable.lastname;
    if (entity.text.confrere)
      return entity.text.confrere.label;
    if (entity.text.competentAuthority)
      return entity.text.competentAuthority.label;
    if (entity.text.provider)
      return entity.text.provider.label;
    return "";
  }

  getTooltipDateText(entity: IndexEntity) {
    let text = "";
    let createdString = "";
    let createdDateString = "";
    let updatedString = "";
    let updatedDateString = "";

    if (entity.createdDate)
      createdDateString = `Créé le ${formatDateFrance(entity.createdDate)}`;

    if (entity.createdBy)
      createdString = `par ${entity.createdBy.firstname} ${entity.createdBy.lastname}`;

    if (entity.udpatedDate)
      updatedDateString = `Mis à jour le ${formatDateFrance(entity.udpatedDate)}`;

    if (entity.updatedBy)
      updatedString = `par ${entity.updatedBy.firstname} ${entity.updatedBy.lastname} `;

    return `${createdDateString}
      ${createdString}
      ${updatedDateString}
      ${updatedString}`;
  }
}
