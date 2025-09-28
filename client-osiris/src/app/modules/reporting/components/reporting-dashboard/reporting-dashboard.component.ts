import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { PageTitleComponent } from '../../../main/components/page-title/page-title.component';
import { ReportingDashboard } from '../../model/ReportingDashboard';
import { ReportingDashboardService } from '../../services/reporting.dashboard.service';
import { ReportingChartComponent } from '../reporting-chart/reporting-chart.component';

@Component({
  selector: 'app-reporting-dashboard',
  templateUrl: './reporting-dashboard.component.html',
  styleUrls: ['./reporting-dashboard.component.css'],
  standalone: true,
  imports: [...SHARED_IMPORTS, PageTitleComponent, ReportingChartComponent]
})
export class ReportingDashboardComponent implements OnInit {

  idDashboard: number | undefined;
  dashboard: ReportingDashboard | undefined;

  constructor(
    private activatedRoute: ActivatedRoute,
    private dashboardService: ReportingDashboardService
  ) { }

  ngOnInit() {
    this.idDashboard = this.activatedRoute.snapshot.params['id'];
    if (this.idDashboard)
      this.dashboardService.getReportingDashboardById(this.idDashboard).subscribe(response => {
        this.dashboard = response;
        if (this.dashboard.assoReportingDashboardWidgets)
          this.dashboard.assoReportingDashboardWidgets.sort((a, b) => a.widgetOrder - b.widgetOrder);
      })
  }

}
