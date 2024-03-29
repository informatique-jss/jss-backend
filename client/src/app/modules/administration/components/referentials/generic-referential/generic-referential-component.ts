import { Directive, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable, Subscription } from 'rxjs';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { IReferential } from '../../../model/IReferential';

@Directive()
export abstract class GenericReferentialComponent<T extends IReferential> implements OnInit {

  selectedEntity: T | undefined;
  @Input() editMode: boolean = false;
  @Output() editModeChange: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Input() saveEvent: Observable<void> | undefined;
  saveEventSubscription: Subscription | undefined;
  @Input() addEvent: Observable<void> | undefined;
  addEventSubscription: Subscription | undefined;
  @Input() cloneEvent: Observable<void> | undefined;
  cloneEventSubscription: Subscription | undefined;
  @Output() selectedEntityChange: EventEmitter<T> = new EventEmitter<T>();
  entities: T[] = [] as Array<T>;
  displayedColumns: SortTableColumn<T>[] = [];
  searchText: string | undefined;
  idRowSelected: number | undefined;

  constructor(
    private formBuilder: FormBuilder,
    private appService: AppService
  ) { }

  entityForm = this.formBuilder.group({
    notice: [''],
  });

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

  selectEntity(element: T) {
    this.selectedEntity = element;
    this.idRowSelected = element.id;
    this.selectedEntityChange.emit(this.selectedEntity);
  }

  getElementCode(element: T) {
    return element.code;
  }

  getElementLabel(element: T) {
    return element.label;
  }

  abstract getAddOrUpdateObservable(): Observable<T>;
  abstract getGetObservable(): Observable<T[]>;

  saveEntity() {
    if (this.getFormStatus()) {
      this.editMode = false;
      this.editModeChange.emit(this.editMode);
      this.getAddOrUpdateObservable().subscribe(response => {
        this.setDataTable();
      });
    } else {
      this.appService.displaySnackBar("Erreur, certains champs ne sont pas correctement renseignés !", true, 15);
    }
  }

  getFormStatus() {
    if (!this.entityForm || !this.entityForm.valid) {
      return false;
    }
    return true;
  }

  addEntity() {
    this.selectedEntity = {} as T;
  }

  cloneEntity() {
    this.selectedEntity = structuredClone(this.selectedEntity);
    this.selectedEntity!.id = undefined;
    this.entities.push(this.selectedEntity!);
    this.setDataTable();
  }

  setDataTable() {
    this.getGetObservable().subscribe(response => {
      this.entities = response;
      this.mapEntities();
      this.definedMatTableColumn();
    })
  }

  definedMatTableColumn() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "Identifiant technique" } as SortTableColumn<T>);
    this.displayedColumns.push({ id: "code", fieldName: "code", label: "Codification fonctionnelle", valueFonction: (element: T, column: SortTableColumn<T>) => { if (element && column) return this.getElementCode(element); return "" } } as SortTableColumn<T>);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé", valueFonction: (element: T, column: SortTableColumn<T>) => { if (element && column) return this.getElementLabel(element); return "" } } as SortTableColumn<T>);
  }

  mapEntities() {
    return;
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();
  }
}
