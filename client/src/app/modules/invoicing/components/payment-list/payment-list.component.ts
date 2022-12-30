import { AfterContentChecked, ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { formatDateTimeForSortTable, formatEurosForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { UploadAttachementDialogComponent } from 'src/app/modules/miscellaneous/components/upload-attachement-dialog/upload-attachement-dialog.component';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { OFX_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { IAttachment } from '../../../miscellaneous/model/IAttachment';
import { Payment } from '../../model/Payment';
import { PaymentSearch } from '../../model/PaymentSearch';
import { PaymentSearchResult } from '../../model/PaymentSearchResult';
import { PaymentSearchResultService } from '../../services/payment.search.result.service';
import { PaymentService } from '../../services/payment.service';

@Component({
  selector: 'payment-list',
  templateUrl: './payment-list.component.html',
  styleUrls: ['./payment-list.component.css']
})
export class PaymentListComponent implements OnInit, AfterContentChecked {
  @Input() paymentSearch: PaymentSearch = {} as PaymentSearch;
  @Input() isForDashboard: boolean = false;
  payments: PaymentSearchResult[] | undefined;
  availableColumns: SortTableColumn[] = [];
  displayedColumns: SortTableColumn[] = [];
  columnToDisplayOnDashboard: string[] = ["payemntDate", "payemntAmount", "label"];
  tableAction: SortTableAction[] = [];
  uploadAttachementDialogRef: MatDialogRef<UploadAttachementDialogComponent> | undefined;

  @Output() actionBypass: EventEmitter<PaymentSearchResult> = new EventEmitter<PaymentSearchResult>();
  @Input() overrideIconAction: string = "";
  @Input() overrideTooltipAction: string = "";
  @Input() defaultStatusFilter: string[] | undefined;
  paymentWayInbound = this.constantService.getPaymentWayInbound();

  constructor(
    private paymentSearchResultService: PaymentSearchResultService,
    private paymentService: PaymentService,
    private constantService: ConstantService,
    protected uploadAttachementDialog: MatDialog,
    private changeDetectorRef: ChangeDetectorRef,
    private appService: AppService,
    private formBuilder: FormBuilder,
    private habilitationService: HabilitationsService,
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  canImportOfxFile() {
    return this.habilitationService.canImportOfxFile();
  }

  ngOnInit() {
    this.availableColumns = [];
    this.availableColumns.push({ id: "id", fieldName: "id", label: "N° du paiement" } as SortTableColumn);
    this.availableColumns.push({ id: "paymentWay", fieldName: "paymentWayLabel", label: "Sens" } as SortTableColumn);
    this.availableColumns.push({ id: "payemntDate", fieldName: "paymentDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "payemntAmount", fieldName: "paymentAmount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "label", fieldName: "paymentLabel", label: "Libellé", isShrinkColumn: true } as SortTableColumn);
    this.availableColumns.push({ id: "isInternallyAssociated", fieldName: "isInternallyAssociated", label: "Associé dans Osiris", valueFonction: (element: any) => { return (element.invoiceId || element.customerOrderId) ? "Oui" : "Non" } } as SortTableColumn);
    this.availableColumns.push({ id: "isExternallyAssociated", fieldName: "isExternallyAssociated", label: "Associé hors Osiris", valueFonction: (element: any) => { return element.isExternallyAssociated ? "Oui" : "Non" } } as SortTableColumn);
    this.availableColumns.push({ id: "invoice", fieldName: "invoiceId", label: "Facture associée", actionLinkFunction: this.getActionLink, actionIcon: "visibility", actionTooltip: "Voir la facture associée" } as SortTableColumn);

    if (this.overrideIconAction == "") {
      this.tableAction.push({
        actionIcon: "merge_type", actionName: "Indiquer comme associé hors Osiris", actionClick: (action: SortTableAction, element: any) => {
          if ((!element.invoice && !element.customerOrder))
            this.paymentService.setExternallyAssociated(element as Payment).subscribe(response => {
              this.searchPayments();
            });
          else
            this.appService.displaySnackBar("Paiement déjà associé dans Osiris", true, 20);
        }, display: true,
      } as SortTableAction);
      this.tableAction.push({
        actionIcon: "remove_done", actionName: "Indiquer comme non associé hors Osiris", actionClick: (action: SortTableAction, element: any) => {
          if ((!element.invoice && !element.customerOrder && element.isExternallyAssociated))
            this.paymentService.unsetExternallyAssociated(element as Payment).subscribe(response => {
              this.searchPayments();
            });
          else
            this.appService.displaySnackBar("Paiement non indiqué associé hors Osiris", true, 20);
        }, display: true,
      } as SortTableAction);
    } else {
      this.tableAction.push({
        actionIcon: this.overrideIconAction, actionName: this.overrideTooltipAction, actionClick: (action: SortTableAction, element: any) => {
          this.actionBypass.emit(element);
        }, display: true,
      } as SortTableAction);
    };

    this.setColumns();

    this.paymentSearch.isHideAssociatedPayments = true;

    if (this.isForDashboard && !this.payments && this.paymentSearch) {
      this.searchPayments();
    }
  }

  paymentForm = this.formBuilder.group({
  });

  setColumns() {
    this.displayedColumns = [];
    if (this.availableColumns && this.columnToDisplayOnDashboard && this.isForDashboard) {
      for (let availableColumn of this.availableColumns)
        for (let columnToDisplay of this.columnToDisplayOnDashboard)
          if (availableColumn.id == columnToDisplay)
            this.displayedColumns.push(availableColumn);
    }
    else
      this.displayedColumns.push(...this.availableColumns);
  }

  getActionLink(action: SortTableColumn, element: any) {
    if (element && action.id == "invoice" && element.invoices && element.invoices[0] && element.invoices.length == 1)
      return ['/invoicing', element.invoices[0].id];
    return undefined;
  }

  searchPayments() {
    if (this.paymentForm.valid) {
      if (this.paymentSearch.startDate)
        this.paymentSearch.startDate = new Date(toIsoString(this.paymentSearch.startDate));
      if (this.paymentSearch.endDate)
        this.paymentSearch.endDate = new Date(toIsoString(this.paymentSearch.endDate));
      this.paymentSearchResultService.getPayments(this.paymentSearch).subscribe(response => {
        this.payments = response;
      })
    }
  }

  importOfxFile() {
    this.uploadAttachementDialogRef = this.uploadAttachementDialog.open(UploadAttachementDialogComponent, {
    });
    this.uploadAttachementDialogRef.componentInstance.entity = { id: 1 } as IAttachment;
    this.uploadAttachementDialogRef.componentInstance.entityType = OFX_ENTITY_TYPE.entityType;
    this.uploadAttachementDialogRef.componentInstance.forcedAttachmentType = this.constantService.getAttachmentTypeCni();
    this.uploadAttachementDialogRef.componentInstance.replaceExistingAttachementType = true;
    this.uploadAttachementDialogRef.afterClosed().subscribe(response => {
      this.searchPayments();
    });
  }

  // TODO : à retirer avant la MEP !!
  currentPayment = {} as Payment;
  paymentForm2 = this.formBuilder.group({
  });
  addPayment() {
    this.currentPayment.isExternallyAssociated = false;
    this.paymentService.addOrUpdatePayment(this.currentPayment).subscribe(response => {
      this.searchPayments();
      this.currentPayment = {} as Payment;
    })
  }
}
