import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from '../../../../services/app.service';
import { OsirisLog } from '../../model/OsirisLog';
import { OsirisLogService } from '../../services/osiris.log.service';

@Component({
  selector: 'log',
  templateUrl: './log.component.html',
  styleUrls: ['./log.component.css']
})
export class LogComponent implements OnInit {

  @Input() isForDashboard = false;
  logs: OsirisLog[] | undefined;
  availableColumns: SortTableColumn[] = [];
  columnToDisplayOnDashboard: string[] = ["className", "currentUser", "createdDateTime"];
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];
  hideRead: boolean = true;

  constructor(
    private appService: AppService,
    private formBuilder: FormBuilder,
    private osirisLogService: OsirisLogService
  ) { }

  logForm = this.formBuilder.group({});

  ngOnInit() {
    this.availableColumns = [];
    this.availableColumns.push({ id: "logType", fieldName: "logType", label: "Type de log" } as SortTableColumn);
    this.availableColumns.push({ id: "currentUser", fieldName: "currentUser", label: "Utilisateur", displayAsEmployee: true } as SortTableColumn);
    this.availableColumns.push({ id: "createdDateTime", fieldName: "createdDateTime", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "className", fieldName: "className", label: "Classe" } as SortTableColumn);
    this.availableColumns.push({ id: "methodName", fieldName: "methodName", label: "Méthode" } as SortTableColumn);
    this.availableColumns.push({ id: "isRead", fieldName: "isRead", label: "Vu ?", valueFonction: (element: any) => { return element.isRead ? "Oui" : "Non" } } as SortTableColumn);

    this.setColumns();

    this.tableAction.push({ actionIcon: "settings", actionName: "Voir le log", actionLinkFunction: this.getActionLink, display: true, } as SortTableAction);

    if (!this.isForDashboard) {
      this.tableAction.push({
        actionIcon: "visibility", actionName: "Indiquer comme lu", actionClick: (action: SortTableAction, element: any) => {
          if (element) {
            let log = (element as OsirisLog);
            log.isRead = true;
            this.osirisLogService.addOrUpdateOsirisLog(log).subscribe(response => {
              this.searchLogs();
            });
          }
        }, display: true,
      } as SortTableAction);

      this.tableAction.push({
        actionIcon: "visibility_off", actionName: "Indiquer comme non lu", actionClick: (action: SortTableAction, element: any) => {
          if (element) {
            let log = (element as OsirisLog);
            log.isRead = false;
            this.osirisLogService.addOrUpdateOsirisLog(log).subscribe(response => {
              this.searchLogs();
            });
          }
        }, display: true,
      } as SortTableAction);
    }

    if (this.isForDashboard) {
      this.hideRead = true;
      this.searchLogs();
    }

  }

  setColumns() {
    this.displayedColumns = [];
    if (this.availableColumns && this.columnToDisplayOnDashboard && this.isForDashboard) {
      for (let availableColumn of this.availableColumns)
        for (let columnToDisplay of this.columnToDisplayOnDashboard)
          if (availableColumn.id == columnToDisplay)
            this.displayedColumns.push(availableColumn);
    }
    else
      this.displayedColumns.push(...this.availableColumns);
  }

  getActionLink(action: SortTableAction, element: any) {
    if (element)
      return ['/administration/log', element.id];
    return undefined;
  }

  searchLogs() {
    this.osirisLogService.getOsirisLogs(this.hideRead).subscribe(response => this.logs = response);
  }
}
