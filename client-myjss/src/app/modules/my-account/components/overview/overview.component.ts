import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { NgbDropdownModule, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { UserPreferenceService } from '../../../../../../../client/src/app/services/user.preference.service';
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
import { QuotationService } from '../../services/quotation.service';

@Component({
  selector: 'overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, AvatarComponent, NgbDropdownModule]
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
  responsablesForCurrentUser: Responsable[] | undefined;
  responsableCheck: boolean[] = [];
  selectAllResponsable: boolean = false;

  constructor(private route: ActivatedRoute,
    private appService: AppService,
    private loginService: LoginService,
    private dashboardUserStatisticsService: DashboardUserStatisticsService,
    public modalService: NgbModal,
    private responsableService: ResponsableService,
    private googleAnalyticsService: GoogleAnalyticsService,
    private sanitizer: DomSanitizer,
    private quotationService: QuotationService,
    private userPreferenceService: UserPreferenceService,
  ) { }

  capitalizeName = capitalizeName;

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      let currentQuotationRoute = this.quotationService.getCurrentDraftQuotationStep();
      // I'm coming to login in, ok
      if (params["aToken"] && params["userId"]) {
        this.loginService.logUser(parseInt(params["userId"]), params["aToken"], params["isFromQuotation"] == "true").subscribe(response => {
          this.googleAnalyticsService.trackLoginLogout("login", "sign-in", "my-account").subscribe();
          if (params["isFromQuotation"] == "true") {
            this.appService.openRoute(null, currentQuotationRoute ? currentQuotationRoute : "quotation", undefined);
          } else {
            this.appService.openRoute(null, "account/overview", undefined);
          }
        });
      }
    });

    this.isLoadingStats = true;
    this.loginService.getCurrentUser().subscribe(response => {
      if (response) {
        this.currentUser = response;
        if (this.currentUser.canViewAllTiersInWeb)
          this.responsableService.getResponsablesForCurrentUser().subscribe(response => {
            if (response) {
              this.responsablesForCurrentUser = response;
              this.retrieveBookmark();
              this.refreshStats();
            }
          });

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

  selectAllResponsables() {
    if (this.responsablesForCurrentUser)
      for (let respo of this.responsablesForCurrentUser)
        this.responsableCheck[respo.id] = this.selectAllResponsable;

    this.refreshStats();
  }

  getCurrentSelectedResponsable() {
    let filterResponsable = undefined;
    if (this.responsablesForCurrentUser) {
      filterResponsable = [];
      for (let respoForCurrentUser of this.responsablesForCurrentUser)
        if (this.responsableCheck[respoForCurrentUser.id])
          filterResponsable.push(respoForCurrentUser);
    }
    return filterResponsable;
  }

  refreshStats() {
    this.setBookmark();
    this.dashboardUserStatisticsService.getDashboardUserStatistics(this.getCurrentSelectedResponsable()).subscribe(response => {
      this.isLoadingStats = false;
      this.statistics = response;
    });
  }

  setBookmark() {
    if (this.responsablesForCurrentUser && this.getCurrentSelectedResponsable())
      this.userPreferenceService.setUserSearchBookmark(this.getCurrentSelectedResponsable()!.map(r => r.id).join(","), "responsables");
  }

  retrieveBookmark() {
    if (this.userPreferenceService.getUserSearchBookmark("responsables")) {
      let respoIds = this.userPreferenceService.getUserSearchBookmark("responsables").split(",");
      for (let i in this.responsableCheck)
        this.responsableCheck[i] = false;
      for (let respoId of respoIds) {
        this.responsableCheck[parseInt(respoId)] = true;
      }
      this.selectAllResponsable = false;
    }
  }

}
