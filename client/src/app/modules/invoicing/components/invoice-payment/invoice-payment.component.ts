import { AfterContentChecked, ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { formatDateTimeForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { Payment } from '../../model/Payment';
import { PaymentSearchResult } from '../../model/PaymentSearchResult';
import { PaymentDetailsDialogService } from '../../services/payment.details.dialog.service';
import { PaymentService } from '../../services/payment.service';
import { AssociatePaymentDialogComponent } from '../associate-payment-dialog/associate-payment-dialog.component';
import { getRemainingToPay } from '../invoice-tools';

@Component({
  selector: 'invoice-payment',
  templateUrl: './invoice-payment.component.html',
  styleUrls: ['./invoice-payment.component.css']
})
export class InvoicePaymentComponent implements OnInit, AfterContentChecked {

  advisedPayment: Payment[] = [] as Array<Payment>;
  @Input() invoice: Invoice = {} as Invoice;
  displayedColumns: SortTableColumn<Payment>[] = [];
  tableAction: SortTableAction<Payment>[] = [];
  newPayment: Payment = {} as Payment;
  displayAddCashPayment = this.habilitationsService.canAddNewCashPayment();

  @Output() stateChanged = new EventEmitter<void>();

  invoiceStatusSend = this.constantService.getInvoiceStatusSend();
  invoiceStatusReceived = this.constantService.getInvoiceStatusReceived();
  invoiceStatusCreditNoteReceived = this.constantService.getInvoiceStatusCreditNoteReceived();

  constructor(private paymentService: PaymentService,
    private appService: AppService,
    private constantService: ConstantService,
    public associatePaymentDialog: MatDialog,
    private formBuilder: FormBuilder,
    private habilitationsService: HabilitationsService,
    private changeDetectorRef: ChangeDetectorRef,
    private paymentDetailsDialogService: PaymentDetailsDialogService,
  ) { }

  invoicePaymentForm = this.formBuilder.group({});

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "N° du paiement", actionFunction: (element: Payment) => this.paymentDetailsDialogService.displayPaymentDetailsDialog(element), actionIcon: "visibility", actionTooltip: "Voir le détail du paiement" } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "payemntDate", fieldName: "paymentDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "payemntAmount", fieldName: "paymentAmount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé" } as SortTableColumn<Payment>);

    this.tableAction.push({
      actionIcon: "merge_type", actionName: "Associer le paiement", actionClick: (column: SortTableAction<Payment>, element: Payment, event: any): void => {
        if (element.invoice) {
          this.appService.displaySnackBar("Veuillez choisir un paiement non associé à une facture", true, 15);
          return;
        }
        this.openAssociationDialog(element as any);
      }, display: true,
    } as SortTableAction<Payment>);
    if (this.invoice && !this.invoice.isCreditNote && this.invoice.responsable) {
      this.paymentService.getAdvisedPayment(this.invoice).subscribe(response => {
        this.advisedPayment = response;
      })
    }
  }

  onModifyAmount() {
    this.newPayment.paymentAmount = Math.min(getRemainingToPay(this.invoice), this.newPayment.paymentAmount ? this.newPayment.paymentAmount : 100000000000000);
  }

  openAssociationDialog(elementIn: PaymentSearchResult) {
    this.paymentService.getPaymentById(elementIn.id).subscribe(element => {
      let dialogPaymentDialogRef = this.associatePaymentDialog.open(AssociatePaymentDialogComponent, {
        width: '100%'
      });
      dialogPaymentDialogRef.componentInstance.invoice = this.invoice;
      dialogPaymentDialogRef.componentInstance.payment = element;
      dialogPaymentDialogRef.afterClosed().subscribe(response => {
        this.stateChanged.emit();
      });
    })
  }

  addCashPayment() {
    if (this.newPayment && this.invoicePaymentForm.valid) {
      this.onModifyAmount();
      this.newPayment.paymentType = this.constantService.getPaymentTypeEspeces();
      this.newPayment.isCancelled = false;
      this.newPayment.isExternallyAssociated = false;
      this.paymentService.addCashPaymentForInvoice(this.newPayment, this.invoice).subscribe(reposne => {
        this.stateChanged.emit();
      })
    }
  }
}
