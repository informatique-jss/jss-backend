import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CUSTOMER_ORDER_STATUS_BEING_PROCESSED, CUSTOMER_ORDER_STATUS_BILLED, CUSTOMER_ORDER_STATUS_OPEN, CUSTOMER_ORDER_STATUS_REQUIRE_ATTENTION, CUSTOMER_ORDER_WITH_UNREAD_COMMENTS, QUOTATION_STATUS_OPEN, QUOTATION_STATUS_SENT_TO_CUSTOMER, QUOTATION_WITH_UNREAD_COMMENTS } from '../../../../libs/Constants';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { GoogleAnalyticsService } from '../../../main/services/googleAnalytics.service';
import { AvatarComponent } from '../../../miscellaneous/components/avatar/avatar.component';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { ResponsableService } from '../../../profile/services/responsable.service';
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
  isLoadingStats: boolean = false;

  QUOTATION_STATUS_SENT_TO_CUSTOMER = QUOTATION_STATUS_SENT_TO_CUSTOMER;
  QUOTATION_STATUS_OPEN = QUOTATION_STATUS_OPEN;
  QUOTATION_STATUS_WITH_UNREAD_COMMENTS = QUOTATION_WITH_UNREAD_COMMENTS;
  CUSTOMER_ORDER_STATUS_OPEN = CUSTOMER_ORDER_STATUS_OPEN;
  CUSTOMER_ORDER_STATUS_BILLED = CUSTOMER_ORDER_STATUS_BILLED;
  CUSTOMER_ORDER_STATUS_BEING_PROCESSED = CUSTOMER_ORDER_STATUS_BEING_PROCESSED;
  CUSTOMER_ORDER_STATUS_REQUIRE_ATTENTION = CUSTOMER_ORDER_STATUS_REQUIRE_ATTENTION;
  CUSTOMER_ORDER_STATUS_WITH_UNREAD_COMMENTS = CUSTOMER_ORDER_WITH_UNREAD_COMMENTS;

  appointmentUrl: SafeResourceUrl | undefined;

  @ViewChild('acceptTermsModal') acceptTermsModalView!: TemplateRef<any>;
  acceptTermsModalInstance: any | undefined;
  acceptTerms: boolean = false;

  constructor(private route: ActivatedRoute,
    private appService: AppService,
    private loginService: LoginService,
    private dashboardUserStatisticsService: DashboardUserStatisticsService,
    public modalService: NgbModal,
    private responsableService: ResponsableService,
    private googleAnalyticsService: GoogleAnalyticsService,
    private sanitizer: DomSanitizer
  ) { }

  capitalizeName = capitalizeName;

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      // I'm coming to login in, ok
      if (params["aToken"] && params["userId"]) {
        this.loginService.logUser(parseInt(params["userId"]), params["aToken"]).subscribe(response => {
          this.googleAnalyticsService.trackLoginLogout("login", "sign-in", "my-account").subscribe();
          this.appService.openRoute(null, "account/overview", undefined);
        });
      }
    });

    this.isLoadingStats = true;
    this.dashboardUserStatisticsService.getDashboardUserStatistics().subscribe(response => {
      this.isLoadingStats = false;
      this.statistics = response;
    })

    this.loginService.getCurrentUser().subscribe(response => {
      if (response) {
        this.currentUser = response;
        if (this.currentUser && this.currentUser.salesEmployee && this.currentUser.salesEmployee.bookingPageUrl)
          this.appointmentUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.currentUser.salesEmployee.bookingPageUrl);
        if (this.currentUser && !this.currentUser.consentTermsDate) {
          if (this.acceptTermsModalInstance) {
            return;
          }

          setTimeout(() => {
            this.acceptTermsModalInstance = this.modalService.open(this.acceptTermsModalView, { centered: true, size: 'md' });

            this.acceptTermsModalInstance.result.finally(() => {
              this.acceptTermsModalInstance = undefined;
            });
          });
        }
      }
    });
  }

  acceptTermsForCurrentUser() {
    if (this.currentUser && this.acceptTerms)
      this.responsableService.updateAcceptTermsForCurrentUser().subscribe(res => {
        this.loginService.getCurrentUser().subscribe(res => this.currentUser = res);
      });
  }

  cancelAcceptation() {
    this.acceptTerms = false;
  }
} 