import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { MenuItem } from '../../../general/model/MenuItem';
import { AppService } from '../../../main/services/app.service';
import { QuotationService } from '../../../my-account/services/quotation.service';

@Component({
  selector: 'app-quotation',
  templateUrl: './quotation.component.html',
  styleUrls: ['./quotation.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class QuotationComponent implements OnInit {
  myJssQuotationItems!: MenuItem[];

  selectedTab: MenuItem | null = null;

  maxAccessibleStepIndex: number | null = null;

  constructor(
    private appService: AppService,
    private changeDetectorRef: ChangeDetectorRef,
    private router: Router,
    private quotationService: QuotationService,
  ) { }

  ngOnInit() {
    this.maxAccessibleStepIndex = parseInt(this.quotationService.getCurrentDraftQuotationStep() != null ? this.quotationService.getCurrentDraftQuotationStep()! : "0");

    this.myJssQuotationItems = this.appService.getAllQuotationMenuItems();
    if (this.quotationService.getCurrentDraftQuotationStep() && this.router.url.indexOf(this.quotationService.getCurrentDraftQuotationStep()!) < 0) {
      this.appService.openRoute(undefined, this.quotationService.getCurrentDraftQuotationStep()!, undefined);
    } else {
      if (this.myJssQuotationItems.length > 0 && this.router.url) {
        this.matchRoute(this.router.url);
      } else {
        this.selectedTab = this.myJssQuotationItems[0];
      }

      this.router.events.subscribe(url => {
        if (url instanceof NavigationEnd) {
          this.matchRoute(url.url);
        }
      });
    }
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
