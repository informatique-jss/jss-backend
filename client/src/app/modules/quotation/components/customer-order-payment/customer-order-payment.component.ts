import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { CUSTOMER_ORDER_STATUS_BILLED } from "src/app/libs/Constants";
import { formatDateTimeForSortTable, formatEurosForSortTable } from "src/app/libs/FormatHelper";
import { AssociatePaymentDialogComponent } from "src/app/modules/invoicing/components/associate-payment-dialog/associate-payment-dialog.component";
import { Payment } from "src/app/modules/invoicing/model/Payment";
import { PaymentSearchResult } from "src/app/modules/invoicing/model/PaymentSearchResult";
import { PaymentDetailsDialogService } from "src/app/modules/invoicing/services/payment.details.dialog.service";
import { PaymentService } from "src/app/modules/invoicing/services/payment.service";
import { SortTableAction } from "src/app/modules/miscellaneous/model/SortTableAction";
import { SortTableColumn } from "src/app/modules/miscellaneous/model/SortTableColumn";
import { AppService } from "src/app/services/app.service";
import { HabilitationsService } from '../../../../services/habilitations.service';
import { ConstantService } from '../../../miscellaneous/services/constant.service';
import { CustomerOrder } from "../../model/CustomerOrder";

@Component({
  selector: 'customer-order-payment',
  templateUrl: './customer-order-payment.component.html',
  styleUrls: ['./customer-order-payment.component.css']
})
export class CustomerOrderPaymentComponent implements OnInit {

  advisedPayment: Payment[] = [] as Array<Payment>;
  @Input() customerOrder: CustomerOrder = {} as CustomerOrder;
  displayedColumns: SortTableColumn<Payment>[] = [];
  tableAction: SortTableAction<Payment>[] = [];
  newPayment: Payment = {} as Payment;
  displayAddCashPayment = this.habilitationsService.canAddNewCashPayment();

  @Output() stateChanged = new EventEmitter<void>();

  CUSTOMER_ORDER_STATUS_BILLED = CUSTOMER_ORDER_STATUS_BILLED;

  constructor(private paymentService: PaymentService,
    private appService: AppService,
    private constantService: ConstantService,
    public associatePaymentDialog: MatDialog,
    private formBuilder: FormBuilder,
    private habilitationsService: HabilitationsService,
    private paymentDetailsDialogService: PaymentDetailsDialogService,
  ) { }

  customerOrderPaymentForm = this.formBuilder.group({});

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "N° du paiement", actionFunction: (element: Payment) => this.paymentDetailsDialogService.displayPaymentDetailsDialog(element), actionIcon: "visibility", actionTooltip: "Voir le détail du paiement" } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "payemntDate", fieldName: "paymentDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "paymentAmount", fieldName: "paymentAmount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé" } as SortTableColumn<Payment>);

    this.tableAction.push({
      actionIcon: "merge_type", actionName: "Associer le paiement", actionClick: (column: SortTableAction<Payment>, element: Payment, event: any): void => {
        if (element.invoice) {
          this.appService.displaySnackBar("Veuillez choisir un paiement non associé à une facture", true, 15);
          return;
        }
        if (element.isCancelled) {
          this.appService.displaySnackBar("Veuillez choisir un paiement non annulé", true, 15);
          return;
        }
        this.openAssociationDialog(element as any);
      }, display: true,
    } as SortTableAction<Payment>);
    if (this.customerOrder) {
      this.paymentService.getAdvisedPaymentForCustomerOrder(this.customerOrder).subscribe(response => {
        this.advisedPayment = response;
      })
    }
  }

  openPaymentDialog(payment: Payment) {
    this.paymentDetailsDialogService.displayPaymentDetailsDialog(payment);
  }

  openAssociationDialog(elementIn: PaymentSearchResult) {
    this.paymentService.getPaymentById(elementIn.id).subscribe(element => {
      let dialogPaymentDialogRef = this.associatePaymentDialog.open(AssociatePaymentDialogComponent, {
        width: '100%'
      });
      dialogPaymentDialogRef.componentInstance.customerOrder = this.customerOrder;
      dialogPaymentDialogRef.componentInstance.payment = element;
      dialogPaymentDialogRef.afterClosed().subscribe(response => {
        this.stateChanged.emit();
      });
    })
  }


  addCashPayment() {
    if (this.newPayment && this.customerOrderPaymentForm.valid) {
      this.newPayment.paymentType = this.constantService.getPaymentTypeEspeces();
      this.newPayment.isCancelled = false;
      this.newPayment.isExternallyAssociated = false;
      this.paymentService.addCashPaymentForCustomerOrder(this.newPayment, this.customerOrder).subscribe(reposne => {
        this.stateChanged.emit();
      })
    }
  }
}
