import { ChangeDetectorRef, Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { MenuItem } from '../../../general/model/MenuItem';
import { AppService } from '../../../main/services/app.service';
import { CustomerOrder } from '../../../my-account/model/CustomerOrder';
import { Service } from '../../../my-account/model/Service';
import { CustomerOrderService } from '../../../my-account/services/customer.order.service';
import { QuotationService } from '../../../my-account/services/quotation.service';
import { NoticeTemplateDescription } from '../../model/NoticeTemplateDescription';
import { NoticeTemplateService } from '../../services/notice.template.service';
import { NoticeTemplateComponent } from '../notice-template/notice-template.component';

@Component({
  selector: 'app-quotation',
  templateUrl: './quotation.component.html',
  styleUrls: ['./quotation.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, NoticeTemplateComponent]
})
export class QuotationComponent implements OnInit {
  myJssQuotationItems!: MenuItem[];

  @ViewChild('cleanModal') cleanModalView!: TemplateRef<any>;

  selectedTab: MenuItem | null = null;
  cleanModalInstance: any | undefined;

  noticeTemplateDescription: NoticeTemplateDescription | undefined;
  noticeTemplateDescriptionSubscription: Subscription = new Subscription;

  selectedServiceInRequiredInformation: Service | undefined;
  isShowNoticeTemplate: boolean = false;
  isNoticeTemplateReadyToBeShown: boolean = false;

  form!: FormGroup;

  maxAccessibleStepIndex: number | null = null;

  subscriptionType: any;
  isPriceReductionForSubscription: any;
  idArticle: any;
  idOrder: number | undefined;
  idQuotation: number | undefined;

  customerOrder: CustomerOrder | undefined;

  constructor(
    private appService: AppService,
    private changeDetectorRef: ChangeDetectorRef,
    private router: Router,
    private quotationService: QuotationService,
    public modalService2: NgbModal,
    private activatedRoute: ActivatedRoute,
    private customerOrderService: CustomerOrderService,
    private noticeTemplateService: NoticeTemplateService,
  ) { }

  ngOnInit() {
    this.subscriptionType = this.activatedRoute.snapshot.params['subscription-type'];
    this.isPriceReductionForSubscription = this.activatedRoute.snapshot.params['is-price-reduction'];
    this.idArticle = this.activatedRoute.snapshot.params['id-article'];
    this.idOrder = this.activatedRoute.snapshot.params['idOrder'];
    this.idQuotation = this.activatedRoute.snapshot.params['idQuotation'];

    this.myJssQuotationItems = this.appService.getAllQuotationMenuItems();

    if (this.idArticle == "null") {
      this.idArticle = null;
    }

    if (this.idQuotation || this.idOrder) {
      this.quotationService.cleanStorageData();
      this.idQuotation ? this.quotationService.setCurrentDraftQuotationId(this.idQuotation) : this.customerOrderService.setCurrentDraftOrderId(this.idOrder!);
      this.quotationService.setCurrentDraftQuotationStep(this.myJssQuotationItems[2]);
    } else if (this.subscriptionType) {
      this.appService.showLoadingSpinner();
      this.customerOrderService.getCustomerOrderForSubscription(this.subscriptionType, this.isPriceReductionForSubscription, this.idArticle).subscribe(computedCustomerOrder => {
        this.customerOrder = computedCustomerOrder;
        this.customerOrderService.setCurrentDraftOrder(this.customerOrder);

        this.appService.hideLoadingSpinner();
        this.appService.openRoute(event, "/quotation/checkout/", undefined);
      });
    }

    this.maxAccessibleStepIndex = parseInt(this.quotationService.getCurrentDraftQuotationStep() != null ? this.quotationService.getCurrentDraftQuotationStep()! : "0");

    if (this.quotationService.getCurrentDraftQuotationStep() && this.router.url.indexOf(this.quotationService.getCurrentDraftQuotationStep()!) < 0) {
      this.appService.openRoute(undefined, this.quotationService.getCurrentDraftQuotationStep()!, undefined);
    } else {
      if (this.myJssQuotationItems.length > 0 && this.router.url) {
        this.matchRoute(this.router.url);
      } else {
        this.selectedTab = this.myJssQuotationItems[0];
      }
    }

    this.noticeTemplateDescriptionSubscription = this.noticeTemplateService.noticeTemplateDescriptionObservable.subscribe(item => {
      if (item) {
        this.noticeTemplateDescription = item;
        if (this.noticeTemplateDescription.service) {
          this.isNoticeTemplateReadyToBeShown = true;
          this.selectedServiceInRequiredInformation = this.noticeTemplateDescription.service;
        }

        this.isShowNoticeTemplate = item.isShowNoticeTemplate;
      }
    });

    this.router.events.subscribe(url => {
      if (url instanceof NavigationEnd) {
        this.matchRoute(url.url);
      }
    });
  }

  cleanStorageData() {
    this.cleanModal(this.cleanModalView);
  }

  finalCancel() {
    if (this.quotationService.getCurrentDraftQuotationId()) {
      this.appService.showLoadingSpinner();
      this.quotationService.cancelQuotation(parseInt(this.quotationService.getCurrentDraftQuotationId()!)).subscribe(response => {
        this.appService.hideLoadingSpinner();
        this.finalCleanStorageData();
      })
    } else if (this.customerOrderService.getCurrentDraftOrderId()) {
      this.appService.showLoadingSpinner();
      this.customerOrderService.cancelCustomerOrder(parseInt(this.customerOrderService.getCurrentDraftOrderId()!)).subscribe(response => {
        this.appService.hideLoadingSpinner();
        this.finalCleanStorageData();
      })
    } else {
      this.finalCleanStorageData();
    }
  }

  finalCleanStorageData() {
    this.quotationService.cleanStorageData();
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.appService.openRoute(undefined, "quotation/identification", undefined);
    });
  }

  cleanModal(content: TemplateRef<any>) {
    if (this.cleanModalInstance) {
      return;
    }

    this.cleanModalInstance = this.modalService2.open(content, {
    });

    this.cleanModalInstance.result.finally(() => {
      this.cleanModalInstance = undefined;
    });
  }

  private matchRoute(url: string): boolean {
    for (let route of this.myJssQuotationItems) {
      if (url && url.indexOf(route.route) >= 0) {
        this.selectedTab = route;
        return true;
      }
    }
    return false;
  }

  isStepAccessible(item: MenuItem): boolean {
    const index = this.myJssQuotationItems.findIndex(i => i === item);
    if (this.quotationService.getCurrentDraftQuotationStep() != null) {
      let currentStep = this.quotationService.getCurrentDraftQuotationStep()!;
      return index <= this.myJssQuotationItems.map(e => e.route).indexOf(currentStep);
    }
    return false;
  }

  goToStep(item: MenuItem) {
    if (this.isStepAccessible(item)) {
      this.quotationService.setCurrentDraftQuotationStep(item);
      this.appService.openRoute(undefined, item.route, undefined);
    }
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }
}
