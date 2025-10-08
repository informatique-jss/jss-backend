import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { NgIcon } from '@ng-icons/core';
import { CountUpModule } from 'ngx-countup';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { PageTitleComponent } from '../../../main/components/page-title/page-title.component';
import { AssoReportingDashboardWidget } from '../../model/AssoReportingDashboardWidget';
import { ReportingDashboard } from '../../model/ReportingDashboard';
import { ReportingDashboardService } from '../../services/reporting.dashboard.service';
import { ReportingChartComponent } from '../reporting-chart/reporting-chart.component';

@Component({
  selector: 'app-reporting-dashboard',
  templateUrl: './reporting-dashboard.component.html',
  styleUrls: ['./reporting-dashboard.component.css'],
  standalone: true,
  imports: [...SHARED_IMPORTS, PageTitleComponent, ReportingChartComponent, NgIcon, CountUpModule]
})
export class ReportingDashboardComponent implements OnInit {

  idDashboard: number | undefined;
  dashboard: ReportingDashboard | undefined;
  stackedWidgets: AssoReportingDashboardWidget[] | undefined;
  selectedStackedWidget: AssoReportingDashboardWidget | undefined;

  constructor(
    private activatedRoute: ActivatedRoute,
    private dashboardService: ReportingDashboardService,
    private changeDetectorRef: ChangeDetectorRef,
    private router: Router
  ) { }

  ngOnInit() {

    this.router.events.subscribe(url => {
      if (url instanceof NavigationEnd) {
        this.refreshDashborard();
      }
    });
    this.refreshDashborard();
  }


  refreshDashborard() {
    this.stackedWidgets = undefined;
    this.selectedStackedWidget = undefined;
    this.dashboard = undefined;
    this.idDashboard = undefined;
    this.idDashboard = this.activatedRoute.snapshot.params['id'];
    if (this.idDashboard)
      this.dashboardService.getReportingDashboardById(this.idDashboard).subscribe(response => {
        this.dashboard = response;
        if (this.dashboard.assoReportingDashboardWidgets) {
          this.dashboard.assoReportingDashboardWidgets.sort((a, b) => a.widgetOrder - b.widgetOrder);

          for (let asso of this.dashboard.assoReportingDashboardWidgets)
            if (asso.isStacked) {
              if (!this.stackedWidgets)
                this.stackedWidgets = []
              this.stackedWidgets.push(asso);
            }

          if (this.stackedWidgets && this.stackedWidgets.length > 0)
            this.selectWidgetToDisplay(this.stackedWidgets[0]);
        }
      })
  }

  selectWidgetToDisplay(asso: AssoReportingDashboardWidget) {
    this.selectedStackedWidget = undefined;
    setTimeout(() => { this.selectedStackedWidget = asso }, 0);
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }
}
