import { Component } from '@angular/core';

import { ActivatedRoute } from '@angular/router';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { PhoneService } from 'src/app/modules/miscellaneous/services/tiers.phone.service';
import { EntityType } from 'src/app/routing/search/EntityType';
import { AFFAIRE_ENTITY_TYPE, ASSO_AFFAIRE_ENTITY_TYPE, COMPETENT_AUTHORITY_ENTITY_TYPE, CONFRERE_ENTITY_TYPE, CUSTOMER_ORDER_ENTITY_TYPE, INVOICE_ENTITY_TYPE, PROVIDER_ENTITY_TYPE, QUOTATION_ENTITY_TYPE, RESPONSABLE_ENTITY_TYPE, TIERS_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { PhoneSearch } from '../../model/PhoneSearch';

@Component({
  selector: 'seach-phone',
  templateUrl: './search-phone.component.html',
  styleUrls: ['./search-phone.component.css']
})
export class SearchPhoneComponent {
  number: string = '';
  phoneSearchs: PhoneSearch[] | undefined;
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];

  entityTypes: EntityType[] = [TIERS_ENTITY_TYPE, COMPETENT_AUTHORITY_ENTITY_TYPE, RESPONSABLE_ENTITY_TYPE, QUOTATION_ENTITY_TYPE, CUSTOMER_ORDER_ENTITY_TYPE, INVOICE_ENTITY_TYPE, ASSO_AFFAIRE_ENTITY_TYPE, AFFAIRE_ENTITY_TYPE, PROVIDER_ENTITY_TYPE, CONFRERE_ENTITY_TYPE];

  constructor(
    private phoneService: PhoneService,
    private appService: AppService,
    private route: ActivatedRoute) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Recherche par téléphone");
    const phoneNumber = this.route.snapshot.paramMap.get('phoneNumber');
    if (phoneNumber) {
      this.phoneService.getPhones(phoneNumber).subscribe(result => {
        if (result) {
          this.phoneSearchs = result;
          if (this.phoneSearchs.length === 1)
            this.openEntity(this.phoneSearchs[0], null);
        }
      });
    }

    this.displayedColumns = [];
    this.displayedColumns.push({ id: "entityId", fieldName: "entityId", label: "N°" } as SortTableColumn);
    this.displayedColumns.push({
      id: "entityType", fieldName: "entityType", label: "Type", valueFonction: (element: any) => {
        return this.getEntityType(element) ? this.getEntityType(element)?.tabName : ""
      }
    } as SortTableColumn);
    this.displayedColumns.push({ id: "entityLabel", fieldName: "entityLabel", label: "Libellé" } as SortTableColumn);

    this.tableAction.push({
      actionIcon: "visibility", actionName: "Ouvrir", actionClick: (action: SortTableAction, element: any, event: any) => {
        this.openEntity(element, event);
      }, display: true,
    } as SortTableAction);
  }

  openEntity(phoneSearch: PhoneSearch, event: any) {
    let entityType: EntityType | undefined = this.getEntityType(phoneSearch);
    if (entityType)
      this.appService.openRoute(event, '/' + entityType.entryPoint + '/' + phoneSearch.entityId, undefined);
  }

  getEntityType(phoneSearch: PhoneSearch): EntityType | undefined {
    let entityType: EntityType | undefined;
    this.entityTypes.forEach(entityTypeEach => {
      if (phoneSearch.entityType == entityTypeEach.entityType)
        entityType = entityTypeEach;
    })
    return entityType;
  }
}
