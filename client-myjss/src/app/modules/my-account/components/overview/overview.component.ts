import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { AvatarComponent } from '../../../miscellaneous/components/avatar/avatar.component';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { DashboardUserStatistics } from '../../../quotation/model/DashboardUserStatistics';
import { DashboardUserStatisticsService } from '../../../quotation/services/dashboard.user.statistics.service';

@Component({
  selector: 'overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, AvatarComponent]
})
export class OverviewComponent implements OnInit {

  currentUser: Responsable | undefined;
  statistics: DashboardUserStatistics | undefined;

  constructor(private route: ActivatedRoute,
    private appService: AppService,
    private loginService: LoginService,
    private dashboardUserStatisticsService: DashboardUserStatisticsService
  ) { }

  capitalizeName = capitalizeName;

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      // I'm coming to login in, ok
      if (params["aToken"] && params["userId"]) {
        this.loginService.logUser(parseInt(params["userId"]), params["aToken"]).subscribe(response => {
          this.appService.openRoute(null, "account/overview", undefined);
        });
      }
    });

    this.dashboardUserStatisticsService.getDashboardUserStatistics().subscribe(response => {
      this.statistics = response;
    })

    this.loginService.getCurrentUser().subscribe(response => this.currentUser = response);
  }

}
