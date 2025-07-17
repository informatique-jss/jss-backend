import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { NgbAccordionModule, NgbDropdownModule, NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { UserPreferenceService } from '../../../main/services/user.preference.service';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { Affaire } from '../../model/Affaire';
import { Attachment } from '../../model/Attachment';
import { CustomerOrder } from '../../model/CustomerOrder';
import { AffaireService } from '../../services/affaire.service';
import { AttachmentService } from '../../services/attachment.service';
import { CustomerOrderService } from '../../services/customer.order.service';
import { UploadAttachmentService } from '../../services/upload.attachment.service';
import { getClassForCustomerOrderStatus, getCustomerOrderStatusLabel } from '../orders/orders.component';

@Component({
  selector: 'app-affaires',
  templateUrl: './affaires.component.html',
  styleUrls: ['./affaires.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericInputComponent, NgbDropdownModule, NgbAccordionModule, NgbNavModule]
})
export class AffairesComponent implements OnInit {

  currentSort: string = "nameAsc";
  currentPage: number = 0;

  affaires: Affaire[] = [];

  searchText: string = "";
  timeoutFilter: number | undefined;

  hideSeeMore: boolean = false;
  isFirstLoading: boolean = true;

  capitalizeName = capitalizeName;

  ordersAffaire: CustomerOrder[][] = [];
  attachmentsAffaire: Attachment[][] = [];
  affaireForm!: FormGroup;

  inputIdAffaire: number | undefined;

  constructor(
    private customerOrderService: CustomerOrderService,
    private affaireService: AffaireService,
    private appService: AppService,
    private userPreferenceService: UserPreferenceService,
    private attachmentService: AttachmentService,
    private formBuilder: FormBuilder,
    private uploadAttachmentService: UploadAttachmentService,
    private activatedRoute: ActivatedRoute,
  ) { }

  ngOnInit() {
    this.affaireForm = this.formBuilder.group({});
    this.inputIdAffaire = this.activatedRoute.snapshot.params['idAffaire'];
    if (this.inputIdAffaire && this.inputIdAffaire > 0) {
      this.searchText = this.inputIdAffaire + "";
      return;
    }
    this.retrieveBookmark();
  }

  refreshAffaires() {
    this.setBookmark();

    if (this.currentPage == 0)
      this.isFirstLoading = true;

    this.appService.showLoadingSpinner();
    this.affaireService.searchAffairesForCurrentUser(this.searchText, this.currentPage, this.currentSort).subscribe(response => {
      this.appService.hideLoadingSpinner();
      if (response) {
        this.affaires.push(...response);
        if (response.length < 10)
          this.hideSeeMore = true;
      }
      this.isFirstLoading = false;
    })
  }

  changeSort(sorter: string) {
    this.currentPage = 0;
    this.affaires = [];
    this.currentSort = sorter;
    this.hideSeeMore = false;
    this.refreshAffaires();
  }

  changeFilterPaused() {
    this.currentPage = 0;
    this.affaires = [];
    this.hideSeeMore = false;
    this.refreshAffaires()
  }

  changeFilter() {
    window.clearTimeout(this.timeoutFilter);
    this.timeoutFilter = window.setTimeout(() => this.changeFilterPaused(), 500);
  }

  loadMore() {
    this.currentPage++;
    this.refreshAffaires();
  }

  loadAffaireDetails(affaire: Affaire) {
    if (!this.ordersAffaire[affaire.id]) {
      this.customerOrderService.getCustomerOrdersForAffaireAndCurrentUser(affaire.id).subscribe(response => {
        this.ordersAffaire[affaire.id] = response;
      })
      this.attachmentService.getAttachmentsForAffaire(affaire.id).subscribe(response => {
        this.attachmentsAffaire[affaire.id] = response;
      })
    }
  }

  openOrderDetails(event: any, order: CustomerOrder) {
    this.appService.openRoute(event, "account/orders/details/" + order.id, undefined);
  }

  setBookmark() {
    this.userPreferenceService.setUserSearchBookmark(this.currentSort, "affaire-currentSort");
  }

  retrieveBookmark() {
    this.currentSort = this.userPreferenceService.getUserSearchBookmark("affaire-currentSort");
    if (!this.currentSort)
      this.currentSort = "nameAsc";
  }

  downloadAttachment(attachment: Attachment) {
    this.uploadAttachmentService.downloadAttachment(attachment);
  }

  getCustomerOrderStatusLabel = getCustomerOrderStatusLabel;
  getClassForCustomerOrderStatus = getClassForCustomerOrderStatus;
}

