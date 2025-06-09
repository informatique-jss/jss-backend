import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { EntityType } from '../../../miscellaneous/model/EntityType';
import { IndexEntity } from '../../model/IndexEntity';
import { IndexEntityService } from '../../services/index.entity.service';

export const QUOTATION_ENTITY_TYPE: EntityType = { entityType: 'Quotation', tabName: 'Devis', entryPoint: 'quotation' };
export const CUSTOMER_ORDER_ENTITY_TYPE: EntityType = { entityType: 'CustomerOrder', tabName: 'Commande', entryPoint: 'order' };
export const AFFAIRE_ENTITY_TYPE: EntityType = { entityType: 'Affaire', tabName: 'Affaires', entryPoint: 'affaire' };
export const INVOICE_ENTITY_TYPE: EntityType = { entityType: 'Invoice', tabName: 'Factures', entryPoint: 'invoicing/view' };


@Component({
  selector: 'search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SearchComponent implements OnInit {
  @ViewChild('inputSearch') searchInput!: ElementRef<HTMLInputElement>;

  searchText: string = "";
  entities: IndexEntity[] | undefined;
  searchObservableRef: Subscription | undefined;

  debounce: any;
  searchInProgress: boolean = false;

  QUOTATION_ENTITY_TYPE = QUOTATION_ENTITY_TYPE;
  CUSTOMER_ORDER_ENTITY_TYPE = CUSTOMER_ORDER_ENTITY_TYPE;
  AFFAIRE_ENTITY_TYPE = AFFAIRE_ENTITY_TYPE;
  INVOICE_ENTITY_TYPE = INVOICE_ENTITY_TYPE;

  constructor(
    private indexEntityService: IndexEntityService,
    private appService: AppService,
    public activeModal: NgbActiveModal
  ) { }

  ngOnInit() {
  }

  ngOnDestroy() {
    if (this.searchObservableRef)
      this.searchObservableRef.unsubscribe();
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.searchInput?.nativeElement.focus();
    }, 200);
  }

  searchEntities() {
    clearTimeout(this.debounce);
    this.entities = [];
    this.debounce = setTimeout(() => {
      this.globalSearch();
    }, 500);
  }

  clearSearch() {
    this.searchText = '';
    this.entities = [];
  }

  globalSearch() {
    if (this.searchObservableRef)
      this.searchObservableRef.unsubscribe();

    this.searchInProgress = true;
    if (this.searchText && this.searchText.length > 2) {
      this.searchObservableRef = this.indexEntityService.globalSearchForEntity(this.searchText).subscribe(response => {
        this.entities = [];
        for (let foundEntity of response) {
          if (foundEntity && foundEntity.text) {
            foundEntity.text = JSON.parse((foundEntity.text as string));
            this.entities.push(foundEntity);
          }
        }
        this.searchInProgress = false;
      })
    } else {
      this.searchInProgress = false;
    }
  }

  openCustomerOrder(event: any, orderId: number) {
    this.appService.openRoute(event, "account/orders/details/" + orderId, undefined);
    this.searchText = '';
    this.activeModal.close();
  }

  openQuotation(event: any, quotation: IndexEntity) {
    this.appService.openRoute(event, "account/quotations/details/" + quotation.entityId, undefined);
    this.searchText = '';
    this.activeModal.close();
  }

  openAffaire(event: any, affaire: IndexEntity) {
    this.appService.openRoute(event, "account/affaires/" + affaire.entityId, undefined);
    this.searchText = '';
    this.activeModal.close();
  }

}
