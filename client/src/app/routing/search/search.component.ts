import { Component, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatTabGroup } from '@angular/material/tabs';
import { Router } from '@angular/router';
import { EntityType } from './EntityType';
import { IndexEntityService } from './index.entity.service';
import { IndexEntity } from './IndexEntity';

export const TIERS_ENTITY_TYPE: EntityType = { entityType: 'Tiers', tabName: 'Tiers', entryPoint: 'tiers' };
export const RESPONSABLE_ENTITY_TYPE: EntityType = { entityType: 'Responsable', tabName: 'Responsable', entryPoint: 'tiers/responsable' };
export const QUOTATION_ENTITY_TYPE: EntityType = { entityType: 'Quotation', tabName: 'Devis', entryPoint: 'quotation' };
export const CUSTOMER_ORDER_ENTITY_TYPE: EntityType = { entityType: 'CustomerOrder', tabName: 'Commande', entryPoint: 'order' };
export const DOMICILIATION_ENTITY_TYPE: EntityType = { entityType: 'Domiciliation', tabName: 'Domiciliation', entryPoint: 'quotation/domiciliation' };
export const ANNOUNCEMENT_ENTITY_TYPE: EntityType = { entityType: 'Announcement', tabName: 'Announcement', entryPoint: 'quotation/announcement' };
export const BODACC_ENTITY_TYPE: EntityType = { entityType: 'Bodacc', tabName: 'BODACC', entryPoint: 'quotation/bodacc  ' };
export const ASSO_AFFAIRE_ENTITY_TYPE: EntityType = { entityType: 'AssoAffaireOrder', tabName: 'Affaires / Prestations', entryPoint: 'affaire' };
export const AFFAIRE_ENTITY_TYPE: EntityType = { entityType: 'Affaire', tabName: 'Affaires', entryPoint: 'affaire' };
export const INVOICE_ENTITY_TYPE: EntityType = { entityType: 'Invoice', tabName: 'Factures', entryPoint: 'invoicing' };

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})

export class SearchComponent implements OnInit {

  constructor(private searchDialogRef: MatDialogRef<SearchComponent>,
    protected indexEntityService: IndexEntityService,
    protected formBuilder: UntypedFormBuilder,
    private router: Router) { }

  TIERS_ENTITY_TYPE = TIERS_ENTITY_TYPE;
  RESPONSABLE_ENTITY_TYPE = RESPONSABLE_ENTITY_TYPE;
  QUOTATION_ENTITY_TYPE = QUOTATION_ENTITY_TYPE;
  CUSTOMER_ORDER_ENTITY_TYPE = CUSTOMER_ORDER_ENTITY_TYPE;
  INVOICE_ENTITY_TYPE = INVOICE_ENTITY_TYPE;
  ASSO_AFFAIRE_ENTITY_TYPE = ASSO_AFFAIRE_ENTITY_TYPE;
  entityTypes: EntityType[] = [TIERS_ENTITY_TYPE, RESPONSABLE_ENTITY_TYPE, QUOTATION_ENTITY_TYPE, CUSTOMER_ORDER_ENTITY_TYPE, INVOICE_ENTITY_TYPE, ASSO_AFFAIRE_ENTITY_TYPE];
  userSelectedModule: EntityType | null = null;
  @ViewChild(MatTabGroup) tabGroup: MatTabGroup | undefined;

  search: string = "";
  foundEntities: IndexEntity[] | null = null;
  tabNames: { [key: string]: string; } = {};

  ngOnInit() {
    this.generateTabLabel();
  }

  closeDialog() {
    this.searchDialogRef.close(null);
  }

  searchForm = this.formBuilder.group({
    search: ['']
  });

  debounce: any;

  searchEntities() {
    clearTimeout(this.debounce);
    this.debounce = setTimeout(() => {
      this.effectiveSearchEntities();
    }, 500);
  }

  effectiveSearchEntities() {
    if (this.search != null && this.search != undefined) {
      if (this.search.length >= 2) {
        this.indexEntityService.searchEntities(this.search).subscribe(response => {
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

  generateTabLabel() {
    this.entityTypes.forEach(entityType => {
      let tabName = "";
      tabName += entityType.tabName;
      if (this.foundEntities != null && this.foundEntities != undefined) {
        let entityNumber = 0;
        this.foundEntities.forEach(foundEntity => {
          if (foundEntity.entityType == entityType.entityType)
            entityNumber++;
        })
        tabName += " (" + entityNumber + ")"
      }
      this.tabNames[entityType.entityType] = tabName;
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
    return tabEntities;
  }

  openEntity(indexEntity: IndexEntity) {
    this.entityTypes.forEach(entityType => {
      if (indexEntity.entityType == entityType.entityType) {
        this.router.navigateByUrl('/', { skipLocationChange: true }).then(() =>
          this.router.navigate(['/' + entityType.entryPoint + '/', "" + indexEntity.entityId])
        );
      }
      this.searchDialogRef.close();
      return;
    })
  }

  selectTab() {
    // select tab : input tab if provide else first tab of first fetch entity (because it's the most relevant)
    if (this.userSelectedModule != null) {
      for (let i = 0; i < this.entityTypes.length; i++) {
        const entityType = this.entityTypes[i];
        if (entityType == this.userSelectedModule && this.tabGroup) {
          this.tabGroup.selectedIndex = i;
        }
      }
    } else if (this.foundEntities != null && this.foundEntities.length > 0) {
      for (let i = 0; i < this.entityTypes.length; i++) {
        const entityType = this.entityTypes[i];
        if (entityType.entityType == this.foundEntities[0].entityType && this.tabGroup)
          this.tabGroup.selectedIndex = i;
      }
    }
  }

  getProvisionLabel(entity: any) {
    return entity.text.provisions.map((provision: { provisionFamilyType: { label: string; }; provisionType: { label: string; }; }) => provision.provisionFamilyType.label + " - " + provision.provisionType.label).join(" / ");
  }
}
