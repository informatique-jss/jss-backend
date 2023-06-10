import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { AppService } from '../../../../services/app.service';
import { EmployeeService } from '../../../profile/services/employee.service';
import { UserReporting } from '../../model/UserReporting';
import { ReportingService } from '../../services/reporting.service';
import { UserReportingService } from '../../services/user.reporting.service';
import { ReportingComponent } from '../reporting/reporting.component';

@Component({
  selector: 'app-reporting-add',
  templateUrl: './reporting-add.component.html',
  styleUrls: ['./reporting-add.component.css']
})
export class ReportingAddComponent implements OnInit {

  idReporting: number | undefined;
  reporting: UserReporting | undefined;
  dataToDisplay: any | undefined;
  loadingDataset: boolean = false;

  @ViewChild(ReportingComponent) reportingComponent: ReportingComponent | undefined;

  saveObservableSubscription: Subscription = new Subscription;

  constructor(
    private activatedRoute: ActivatedRoute,
    private userReportingService: UserReportingService,
    private appService: AppService,
    private formBuilder: FormBuilder,
    private reportingService: ReportingService,
    private employeeService: EmployeeService,
  ) { }

  userReportingForm = this.formBuilder.group({});

  ngOnInit() {
    this.idReporting = this.activatedRoute.snapshot.params.id != 'null' ? this.activatedRoute.snapshot.params.id : undefined;
    let isCloning = this.activatedRoute.snapshot.params.cloning == 'true' ? true : false;
    this.appService.changeHeaderTitle("Rapport");

    if (this.idReporting) {
      this.userReportingService.getUserReporting(this.idReporting).subscribe(reporting => {
        if (reporting) {
          this.appService.changeHeaderTitle("Rapport " + reporting.name);
          this.reporting = reporting;
          if (isCloning) {
            this.reporting.id = null;
            this.reporting.name = null;
          }
        }
      })
    } else {
      this.appService.changeHeaderTitle("Nouveau rapport");
      this.reporting = {} as UserReporting;
      this.employeeService.getCurrentEmployee().subscribe(user => this.reporting!.employee = user);
    }

    this.saveObservableSubscription = this.appService.saveObservable.subscribe(response => {
      if (response)
        this.saveReporting()
    });
  }

  ngOnDestroy() {
    this.saveObservableSubscription.unsubscribe();
  }

  saveReporting() {
    if (this.userReportingForm.valid && this.reporting && this.reportingComponent) {
      this.reporting.settings = this.reportingComponent.getCurrentSettings();
      this.userReportingService.addOrUpdateUserReporting(this.reporting).subscribe(response => {
        this.appService.openRoute(null, "reporting/list", undefined);
      })
    }
  }

  selectDataSet(dataset: string) {
    if (!this.loadingDataset) {
      this.loadingDataset = true;
      this.reportingService.getDataset(dataset, undefined).subscribe(data => {
        this.dataToDisplay = data;
        this.loadingDataset = false;
      });
    }
  }
}
