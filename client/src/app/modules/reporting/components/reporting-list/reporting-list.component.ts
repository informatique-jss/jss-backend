import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Subject, Subscription } from 'rxjs';
import { EmployeeDialogComponent } from 'src/app/modules/miscellaneous/components/employee-dialog/employee-dialog.component';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from '../../../../services/app.service';
import { EmployeeService } from '../../../profile/services/employee.service';
import { UserReporting } from '../../model/UserReporting';
import { ReportingService } from '../../services/reporting.service';
import { UserReportingService } from '../../services/user.reporting.service';
import { ReportingComponent } from '../reporting/reporting.component';

@Component({
  selector: 'app-reporting-list',
  templateUrl: './reporting-list.component.html',
  styleUrls: ['./reporting-list.component.css']
})
export class ReportingListComponent implements OnInit {

  displayedColumns: SortTableColumn<UserReporting>[] = [];
  tableAction: SortTableAction<UserReporting>[] = [];
  userReportings: UserReporting[] | undefined;
  filteredUserReportings: UserReporting[] | undefined;
  currentUserReporting: UserReporting | undefined;
  searchText: string = '';
  reportingObservableRef: Subscription | undefined;
  refreshTable: Subject<void> = new Subject<void>();

  dataToDisplay: any | undefined;

  @ViewChild(ReportingComponent) reportingComponent: ReportingComponent | undefined;

  constructor(
    private appService: AppService,
    private employeeService: EmployeeService,
    private userReportingService: UserReportingService,
    private employeeDialog: MatDialog,
    private reportingService: ReportingService,
    private formBuilder: FormBuilder,
  ) { }

  reportingListForm = this.formBuilder.group({});

  ngOnInit() {
    this.appService.changeHeaderTitle("Reporting");

    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn<UserReporting>);
    this.displayedColumns.push({ id: "dataset", fieldName: "dataset", label: "Jeu de données" } as SortTableColumn<UserReporting>);
    this.displayedColumns.push({ id: "name", fieldName: "name", label: "Nom" } as SortTableColumn<UserReporting>);

    this.tableAction.push({
      actionIcon: "edit", actionName: "Modifier le rapport", actionClick: (action: SortTableAction<UserReporting>, element: any, event: any) => {
        this.appService.openRoute(event, "reporting/add/" + element.id + "/false", undefined);
      }, display: true,
    } as SortTableAction<UserReporting>);

    this.employeeService.getCurrentEmployee().subscribe(employee => {
      this.userReportingService.getUserReportings(employee).subscribe(reportings => {
        this.userReportings = reportings
        this.applyFilter();
      });
    })
  }

  applyFilter() {
    let value = this.searchText.toLowerCase();
    if (value.length > 2) {
      this.filteredUserReportings = [];
      if (this.userReportings)
        for (let reporting of this.userReportings)
          if (reporting.name && reporting.name.toLowerCase().indexOf(value) >= 0)
            this.filteredUserReportings.push(reporting);
    } else {
      this.filteredUserReportings = this.userReportings;
    }
    this.refreshTable.next();
  }

  selectReporting(userReporting: UserReporting, columns: string[]) {
    this.currentUserReporting = userReporting;
    if (this.reportingObservableRef)
      this.reportingObservableRef.unsubscribe();
    this.reportingObservableRef = this.reportingService.getDataset(userReporting.dataset, columns).subscribe(data => {
      this.dataToDisplay = data;
      this.appService.changeHeaderTitle("Reporting - " + userReporting.name);
      this.reportingComponent?.refreshPivotWithData(this.dataToDisplay);
    })
  }

  addReporting() {
    this.appService.openRoute(null, "reporting/add/null/false", undefined);
  }

  saveReportingLayout() {
    if (this.currentUserReporting && this.reportingComponent) {
      this.currentUserReporting.settings = this.reportingComponent.getCurrentSettings();
      this.userReportingService.addOrUpdateUserReporting(this.currentUserReporting).subscribe(response => {
      })
    }
  }

  editReport() {
    if (this.currentUserReporting)
      this.appService.openRoute(null, "reporting/add/" + this.currentUserReporting.id + "/false", undefined);
  }

  cloneReport() {
    if (this.currentUserReporting)
      this.appService.openRoute(null, "reporting/add/" + this.currentUserReporting.id + "/true", undefined);
  }

  deleteReport() {
    if (this.currentUserReporting) {
      this.userReportingService.deleteUserReporting(this.currentUserReporting).subscribe(response => {
        this.appService.openRoute(null, "reporting/list", undefined);
      })
    }
  }

  copyToUserReport() {
    if (this.currentUserReporting) {
      let shareEmployeeDialogRef = this.employeeDialog.open(EmployeeDialogComponent, {
        width: '50%'
      });
      shareEmployeeDialogRef.componentInstance.content = "Indiquez ici le collaborateur chez qui copier ce rapport :";
      shareEmployeeDialogRef.componentInstance.closeActionText = "Annuler";
      shareEmployeeDialogRef.componentInstance.validationActionText = "Copier";
      shareEmployeeDialogRef.afterClosed().subscribe(response => {
        if (response)
          this.userReportingService.copyUserReportingToUser(this.currentUserReporting!, response).subscribe();
      })
    }
  }
}
