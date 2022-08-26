import { Directive, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Observable, Subscription } from 'rxjs';
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
  displayedColumns: string[] = ['id', 'code', 'label'];
  @ViewChild(MatSort) sort!: MatSort;

  entityDataSource: MatTableDataSource<T> = new MatTableDataSource<T>();

  constructor(
    private formBuilder: FormBuilder,
    private appService: AppService
  ) { }

  entityForm = this.formBuilder.group({
    boardGrade: [''],
    publicationCertificateDocumentGrade: [''],
    billingGrade: [''],
    paperGrade: [''],
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
    if (this.entityForm && this.entityForm.valid) {
      this.editMode = false;
      this.editModeChange.emit(this.editMode);
      this.getAddOrUpdateObservable().subscribe(response => {
        this.setDataTable();
      });
    } else {
      this.appService.displaySnackBar("Erreur, certains champs ne sont pas correctement renseignés !", true, 60);
    }
  }

  getFormStatus() {
    if (!this.entityForm.valid) {
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
      this.entityDataSource = new MatTableDataSource(this.entities);
      setTimeout(() => {
        this.entityDataSource.sort = this.sort;
        this.entityDataSource.sortingDataAccessor = (item: T, property) => {
          switch (property) {
            case 'id': return item.id!;
            case 'code': return this.getElementCode(item);
            case 'label': return this.getElementLabel(item);
            default: return this.getElementLabel(item);
          }
        };

        this.entityDataSource.filterPredicate = (data: any, filter) => {
          const dataStr = JSON.stringify(data).toLowerCase();
          return dataStr.indexOf(filter) != -1;
        }
      });
    })
  }

  mapEntities() {
    return;
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    filterValue = filterValue.toLowerCase();
    this.entityDataSource.filter = filterValue;
  }
}
