import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Observable, combineLatest } from 'rxjs';
import { Dictionnary } from 'src/app/libs/Dictionnary';
import { formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { isInt } from 'src/app/libs/TypeHelper';
import { IReferential } from 'src/app/modules/administration/model/IReferential';
import { Audit } from 'src/app/modules/miscellaneous/model/Audit';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { ResponsableService } from 'src/app/modules/tiers/services/responsable.service';
import { EntityType } from 'src/app/routing/search/EntityType';
import { SortTableAction } from '../../model/SortTableAction';
import { SortTableColumn } from '../../model/SortTableColumn';
import { AuditService } from '../../services/audit.service';

@Component({
  selector: 'history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {

  @Input() entity: any = {};
  @Input() entityType: EntityType = {} as EntityType;
  @ViewChild(MatSort) sort!: MatSort;
  @Input() displayOnlyFields: string[] | null = null;
  @Input() historyActions: SortTableAction<Audit>[] = [] as Array<SortTableAction<Audit>>;
  internalHistoryActions: SortTableAction<Audit>[] = [] as Array<SortTableAction<Audit>>;
  @Input() parseTypeList: IReferential[] | undefined;

  audits: Audit[] = [] as Array<Audit>;
  finalAudits: Audit[] = [] as Array<Audit>;

  ddisplayedColumns: string[] = ['', '', '', '', ''];

  auditDataSource: MatTableDataSource<Audit> = new MatTableDataSource<Audit>();

  dictionnary = new Map<string, string>(Object.entries(Dictionnary));

  displayedColumns: SortTableColumn<Audit>[] = [];
  searchText: string | undefined;

  allEmployees: Employee[] | undefined;
  responsableIdDone: number[] = [];
  responsableCache: Responsable[] = [];


  constructor(
    protected auditService: AuditService,
    private employeeService: EmployeeService,
    private responsableService: ResponsableService
  ) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.entity != undefined && this.entity.id != undefined) {
      this.employeeService.getEmployees().subscribe(response => {
        this.allEmployees = response;
        this.setData();
      })
    }
  }

  ngOnInit() {
    this.internalHistoryActions = this.historyActions;
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "fieldName", fieldName: "fieldName", label: "Champ" } as SortTableColumn<Audit>);
    this.displayedColumns.push({ id: "oldValue", fieldName: "oldValue", label: "Ancienne valeur", valueFonction: (element: Audit, column: SortTableColumn<Audit>) => { return this.parseValues(element.oldValue) } } as SortTableColumn<Audit>);
    this.displayedColumns.push({ id: "newValue", fieldName: "newValue", label: "Nouvelle valeur", valueFonction: (element: Audit, column: SortTableColumn<Audit>) => { return this.parseValues(element.newValue) } } as SortTableColumn<Audit>);
    this.displayedColumns.push({ id: "createdBy", fieldName: "username", label: "Auteur" } as SortTableColumn<Audit>);
    this.displayedColumns.push({ id: "creationDate", fieldName: "datetime", label: "Modifié le", valueFonction: formatDateTimeForSortTable } as SortTableColumn<Audit>);
  }

  historyActionTrigger(historyAction: SortTableAction<Audit>, element: Audit) {
    if (historyAction && historyAction.actionClick)
      historyAction.actionClick(historyAction, element, undefined);
  }

  setData() {
    this.auditService.getAuditForEntity(this.entity.id, this.entityType).subscribe(response => {

      let promises: Observable<Responsable>[] = [] as Observable<Responsable>[];
      // Gather responsables
      if (response) {
        for (let audit of response) {
          if (isInt(audit.username) && this.responsableIdDone.indexOf(parseInt(audit.username)) < 0) {
            promises.push(this.responsableService.getResponsable(parseInt(audit.username)));
            this.responsableIdDone.push(parseInt(audit.username));
          }
        }
        if (promises && promises.length > 0)
          combineLatest(promises).subscribe(responseRespo => {
            this.responsableCache = responseRespo;
            this.pushResponse(response);
          });
        else
          this.pushResponse(response);
      }
    })
  }

  pushResponse(response: Audit[]) {
    if (this.displayOnlyFields != null && this.displayOnlyFields != undefined && response) {
      for (let audit of response) {
        if (this.displayOnlyFields!.indexOf(audit.fieldName) >= 0)
          this.completeAuditUsernameAndPush(audit);
      }
    } else {
      if (response)
        for (let audit of response)
          this.completeAuditUsernameAndPush(audit);
    }
    this.refreshTable();
  }

  refreshTable() {
    this.finalAudits = this.audits;
    if (this.audits != undefined && this.audits != null && this.audits.length > 0) {
      this.audits.sort(function (a: Audit, b: Audit) {
        return new Date(b.datetime).getTime() - new Date(a.datetime).getTime();
      });
    }

    setTimeout(() => {
      this.auditDataSource = new MatTableDataSource(this.audits);
      this.auditDataSource.sort = this.sort;
      this.auditDataSource.sortingDataAccessor = (item: Audit, property) => {
        switch (property) {
          case 'fieldName': return item.fieldName;
          case 'oldValue': return this.parseValues(item.oldValue);
          case 'newValue': return this.parseValues(item.newValue);
          case 'createdBy': return item.username;
          case 'creationDate': return new Date(item.datetime).getTime() + "";
          default: return item.fieldName;
        }
      };

      this.auditDataSource.filterPredicate = (data: any, filter) => {
        const dataStr = JSON.stringify(data).toLowerCase();
        return dataStr.indexOf(filter) != -1;
      }
    });
  }

  completeAuditUsernameAndPush(audit: Audit) {
    let found: boolean = false;
    if (this.allEmployees) {
      for (let employee of this.allEmployees) {
        if (employee.username == audit.username) {
          found = true;
          audit.username = employee.firstname + ' ' + employee.lastname;
        }
      }
    }

    if (found == true) {
      this.audits.push(audit);
    } else if (isInt(audit.username)) {
      if (this.responsableCache)
        for (let responsable of this.responsableCache)
          if (responsable.id == parseInt(audit.username)) {
            audit.username = responsable.firstname + ' ' + responsable.lastname + ' (' + responsable.id + ')';
          }
      this.audits.push(audit);
    } else {
      this.audits.push(audit);
    }
  }

  parseValues(value: string): string {
    if (this.parseTypeList)
      for (let type of this.parseTypeList)
        if (type.code == value)
          return type.label;

    return value;
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();
  }

  getFieldLabel(fieldName: string) {
    if (this.dictionnary.get(fieldName))
      return this.dictionnary.get(fieldName);
    return fieldName;
  }
}
