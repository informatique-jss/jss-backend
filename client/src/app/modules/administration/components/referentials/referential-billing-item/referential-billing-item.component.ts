import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { Observable, Subscription } from 'rxjs';
import { formatDate } from 'src/app/libs/FormatHelper';
import { AccountingAccount } from 'src/app/modules/accounting/model/AccountingAccount';
import { BillingItem } from 'src/app/modules/miscellaneous/model/BillingItem';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { BillingItemService } from 'src/app/modules/miscellaneous/services/billing.item.service';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'referential-billing-item',
  templateUrl: 'referential-billing-item.component.html',
  styleUrls: ['referential-billing-item.component.css']
})
export class ReferentialBillingItemComponent implements OnInit {

  selectedEntity: BillingItem | undefined;
  @Input() editMode: boolean = false;
  @Output() editModeChange: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Input() saveEvent: Observable<void> | undefined;
  saveEventSubscription: Subscription | undefined;
  @Input() addEvent: Observable<void> | undefined;
  addEventSubscription: Subscription | undefined;
  @Input() cloneEvent: Observable<void> | undefined;
  cloneEventSubscription: Subscription | undefined;
  @Output() selectedEntityChange: EventEmitter<BillingItem> = new EventEmitter<BillingItem>();
  entities: BillingItem[] = [] as Array<BillingItem>;
  displayedColumns: SortTableColumn[] = [];
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  accountingAccountCharge: AccountingAccount | undefined;
  accountingAccountProduct: AccountingAccount | undefined;

  searchText: string | undefined;

  constructor(
    private formBuilder: FormBuilder,
    private billingItemService: BillingItemService,
    private appService: AppService,
  ) { }

  entityForm = this.formBuilder.group({
  });

  cloneEntity() {
    this.selectedEntity = structuredClone(this.selectedEntity);
    this.selectedEntity!.id = undefined;
    this.entities.push(this.selectedEntity!);
    this.setDataTable();
  }

  ngOnInit() {
    this.setDataTable();
    if (this.saveEvent)
      this.saveEventSubscription = this.saveEvent.subscribe(() => this.saveEntity());
    if (this.addEvent)
      this.addEventSubscription = this.addEvent.subscribe(() => this.addEntity());
    if (this.cloneEvent)
      this.cloneEventSubscription = this.cloneEvent.subscribe(() => this.cloneEntity());
  }

  ngOnDestroy() {
    if (this.saveEventSubscription)
      this.saveEventSubscription.unsubscribe();
    if (this.addEventSubscription)
      this.addEventSubscription.unsubscribe();
    if (this.cloneEventSubscription)
      this.cloneEventSubscription.unsubscribe();
  }

  selectEntity(element: BillingItem) {
    this.selectedEntity = element;
    if (this.selectedEntity.startDate)
      this.selectedEntity.startDate = new Date(this.selectedEntity.startDate);
    this.setCharge();
    this.setProduct();
    this.selectedEntityChange.emit(this.selectedEntity);
  }

  getElementCode(element: BillingItem) {
    return element.billingType.code;
  }

  getElementLabel(element: BillingItem) {
    return element.billingType.label + " - " + formatDate(new Date(element.startDate));
  }

  saveEntity() {
    if (this.selectedEntity && this.entityForm && this.entityForm.valid) {
      this.editMode = false;
      this.editModeChange.emit(this.editMode);
      this.selectedEntity.accountingAccounts = [] as Array<AccountingAccount>;
      if (this.accountingAccountCharge)
        this.selectedEntity.accountingAccounts.push(this.accountingAccountCharge);
      if (this.accountingAccountProduct)
        this.selectedEntity.accountingAccounts.push(this.accountingAccountProduct);
      this.billingItemService.addOrUpdateBillingItem(this.selectedEntity).subscribe(response => {
        this.setDataTable();
      });
    } else {
      this.appService.displaySnackBar("Erreur, certains champs ne sont pas correctement renseignés !", true, 60);
    }
  }

  addEntity() {
    this.selectedEntity = {} as BillingItem;
    this.setCharge();
    this.setProduct();
  }

  setDataTable() {
    this.billingItemService.getBillingItems().subscribe(response => {
      this.entities = response;
      this.definedMatTableColumn();
    })
  }

  definedMatTableColumn() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "Identifiant technique" } as SortTableColumn);
    this.displayedColumns.push({ id: "code", fieldName: "code", label: "Codification fonctionnelle", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]) => { if (element && column) return this.getElementCode(element); return "" } } as SortTableColumn);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]) => { if (element && column) return this.getElementLabel(element); return "" } } as SortTableColumn);
  }

  setCharge() {
    this.accountingAccountCharge = undefined;
    if (this.selectedEntity && this.selectedEntity.accountingAccounts)
      for (let entity of this.selectedEntity.accountingAccounts)
        if (entity && entity.accountingAccountNumber.substring(0, 3) == "606")
          this.accountingAccountCharge = entity;
  }

  setProduct() {
    this.accountingAccountProduct = undefined;
    if (this.selectedEntity && this.selectedEntity.accountingAccounts)
      for (let entity of this.selectedEntity.accountingAccounts)
        if (entity && entity.accountingAccountNumber.substring(0, 3) == "706")
          this.accountingAccountProduct = entity;

    if (!this.accountingAccountProduct)
      this.addProduct();
  }

  addCharge() {
    if (this.selectedEntity) {
      if (this.accountingAccountCharge == undefined)
        this.accountingAccountCharge = { accountingAccountNumber: "606" } as AccountingAccount;
    }
  }

  addProduct() {
    if (this.selectedEntity) {
      if (this.accountingAccountProduct == undefined)
        this.accountingAccountProduct = { accountingAccountNumber: "706" } as AccountingAccount;
    }
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();
  }

  displayLabel(object: any): string {
    return object ? object.label : '';
  }
}
