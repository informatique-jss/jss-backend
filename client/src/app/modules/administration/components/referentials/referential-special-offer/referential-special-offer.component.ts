import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { Observable, Subscription } from 'rxjs';
import { AssoSpecialOfferBillingType } from 'src/app/modules/miscellaneous/model/AssoSpecialOfferBillingType';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { SpecialOffer } from 'src/app/modules/miscellaneous/model/SpecialOffer';
import { SpecialOfferService } from 'src/app/modules/miscellaneous/services/special.offer.service';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'referential-special-offer',
  templateUrl: 'referential-special-offer.component.html',
  styleUrls: ['referential-special-offer.component.css']
})
export class ReferentialSpecialOfferComponent implements OnInit {

  selectedEntity: SpecialOffer | undefined;
  @Input() editMode: boolean = false;
  @Output() editModeChange: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Input() saveEvent: Observable<void> | undefined;
  saveEventSubscription: Subscription | undefined;
  @Input() addEvent: Observable<void> | undefined;
  addEventSubscription: Subscription | undefined;
  @Input() cloneEvent: Observable<void> | undefined;
  cloneEventSubscription: Subscription | undefined;
  @Output() selectedEntityChange: EventEmitter<SpecialOffer> = new EventEmitter<SpecialOffer>();
  entities: SpecialOffer[] = [] as Array<SpecialOffer>;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;
  idRowSelected: number | undefined;

  assoSpecialOfferBillingItems: AssoSpecialOfferBillingType[] | undefined;

  displayedColumns: SortTableColumn[] = [];
  searchText: string | undefined;

  constructor(
    private formBuilder: FormBuilder,
    private specialOfferService: SpecialOfferService,
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

  selectEntity(element: SpecialOffer) {
    this.selectedEntity = element;
    this.idRowSelected = element.id;
    this.setBillingItems();
    this.selectedEntityChange.emit(this.selectedEntity);
  }

  getElementCode(element: SpecialOffer) {
    return element.code;
  }

  getElementLabel(element: SpecialOffer) {
    return element.label;
  }

  saveEntity() {
    if (this.selectedEntity && this.entityForm && this.entityForm.valid) {
      this.editMode = false;
      this.editModeChange.emit(this.editMode);
      this.selectedEntity.assoSpecialOfferBillingTypes = [] as Array<AssoSpecialOfferBillingType>;
      if (this.assoSpecialOfferBillingItems)
        this.selectedEntity.assoSpecialOfferBillingTypes.push(...this.assoSpecialOfferBillingItems);
      this.specialOfferService.addOrUpdateSpecialOffer(this.selectedEntity).subscribe(response => {
        this.setDataTable();
      });
    } else {
      this.appService.displaySnackBar("Erreur, certains champs ne sont pas correctement renseignés !", true, 15);
    }
  }

  addEntity() {
    this.selectedEntity = {} as SpecialOffer;
    this.setBillingItems();
  }

  setDataTable() {
    this.specialOfferService.getSpecialOffers().subscribe(response => {
      this.entities = response;
    })
    this.definedMatTableColumn();
  }

  setBillingItems() {
    this.assoSpecialOfferBillingItems = [] as Array<AssoSpecialOfferBillingType>;
    if (this.selectedEntity && this.selectedEntity.assoSpecialOfferBillingTypes)
      this.assoSpecialOfferBillingItems?.push(...this.selectedEntity.assoSpecialOfferBillingTypes);
  }

  addAsso() {
    if (this.selectedEntity) {
      if (this.assoSpecialOfferBillingItems == undefined)
        this.assoSpecialOfferBillingItems = [] as Array<AssoSpecialOfferBillingType>;
      this.assoSpecialOfferBillingItems.push({} as AssoSpecialOfferBillingType);
    }
  }

  definedMatTableColumn() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "Identifiant technique" } as SortTableColumn);
    this.displayedColumns.push({ id: "code", fieldName: "code", label: "Codification fonctionnelle", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]) => { if (element && column) return this.getElementCode(element); return "" } } as SortTableColumn);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]) => { if (element && column) return this.getElementLabel(element); return "" } } as SortTableColumn);
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();
  }
}
