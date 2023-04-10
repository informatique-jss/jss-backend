import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { AmountDialogComponent } from 'src/app/modules/invoicing/components/amount-dialog/amount-dialog.component';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { PaymentType } from 'src/app/modules/miscellaneous/model/PaymentType';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { formatDateForSortTable, formatEurosForSortTable } from '../../../../libs/FormatHelper';
import { AppService } from '../../../../services/app.service';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { CompetentAuthority } from '../../../miscellaneous/model/CompetentAuthority';
import { Debour } from '../../model/Debour';
import { IQuotation } from '../../model/IQuotation';
import { Provision } from '../../model/Provision';


@Component({
  selector: 'add-debour',
  templateUrl: './add-debour.component.html',
  styleUrls: ['./add-debour.component.css']
})
export class AddDebourComponent implements OnInit {

  @Input() provision: Provision | undefined;
  @Output() provisionChange: EventEmitter<Provision> = new EventEmitter<Provision>();
  @Input() editMode: boolean = false;
  @Input() customerOrder: IQuotation | undefined;
  newDebour: Debour | undefined;
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];
  paymentTypeVirement: PaymentType = this.constantService.getPaymentTypeVirement();
  paymentTypePrelevement: PaymentType = this.constantService.getPaymentTypePrelevement();
  paymentTypeCb: PaymentType = this.constantService.getPaymentTypeCB();
  paymentTypeEspeces: PaymentType = this.constantService.getPaymentTypeEspeces();
  paymentTypeCheques: PaymentType = this.constantService.getPaymentTypeCheques();
  paymentTypeAccount: PaymentType = this.constantService.getPaymentTypeAccount();
  refreshTable: Subject<void> = new Subject<void>();

  constructor(private formBuilder: FormBuilder,
    public confirmationDialog: MatDialog,
    public selectDeboursDialog: MatDialog,
    public invoicedAmountDialog: MatDialog,
    private constantService: ConstantService,
    private habilitationService: HabilitationsService,
    private appService: AppService,
  ) { }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn);
    this.displayedColumns.push({ id: "billingType", fieldName: "billingType.label", label: "Débour" } as SortTableColumn);
    this.displayedColumns.push({ id: "competentAuthority", fieldName: "competentAuthority.label", label: "Autorité compétente" } as SortTableColumn);
    this.displayedColumns.push({ id: "debourAmount", fieldName: "debourAmount", label: "Montant TTC", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "invoicedAmount", fieldName: "invoicedAmount", label: "Montant facturé TTC", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "paymentType", fieldName: "paymentType.label", label: "Type de paiement" } as SortTableColumn);
    this.displayedColumns.push({ id: "paymentDateTime", fieldName: "paymentDateTime", label: "Date de paiement", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "checkNumber", fieldName: "checkNumber", label: "N° de chèque" } as SortTableColumn);
    this.displayedColumns.push({ id: "payment", fieldName: "payment.id", label: "Paiement associé" } as SortTableColumn);
    this.displayedColumns.push({ id: "invoice", fieldName: "invoiceItem.invoice.id", label: "Facture associée" } as SortTableColumn);
    this.displayedColumns.push({ id: "comments", fieldName: "comments", label: "Commentaires", isShrinkColumn: true } as SortTableColumn);

    this.addInvoicedAmountColumn();
    this.addDeleteDeboursColumn();
    this.refreshTable.next();
  }

  addInvoicedAmountColumn() {
    if (this.editMode)
      this.tableAction.push({
        actionIcon: 'edit', actionName: 'Modifier le montant facturé', actionClick: (action: SortTableAction, element: Debour) => {
          if (element.billingType) {
            let amountDialogRef = this.invoicedAmountDialog.open(AmountDialogComponent, {
              width: '100%'
            });
            if (element.invoicedAmount)
              amountDialogRef.componentInstance.amount = parseFloat(element.invoicedAmount + "");
            amountDialogRef.componentInstance.label = "Indiquer le montant à facturer :";
            amountDialogRef.afterClosed().subscribe(response => {
              if (response != null) {
                element.invoicedAmount = parseFloat(response);
              } else {
                return;
              }
            });
          }
        }, display: true,
      } as SortTableAction);
  }

  addDeleteDeboursColumn() {
    if (this.editMode)
      this.tableAction.push({
        actionIcon: 'delete', actionName: 'Supprimer le débours', actionClick: (action: SortTableAction, element: Debour) => {
          if (element && this.provision) {
            if (element.id && (element.paymentType.id == this.paymentTypeEspeces.id || element.paymentType.id == this.paymentTypeCheques.id
              || element.invoiceItem || element.payment || element.isAssociated))
              this.appService.displaySnackBar("Impossible de supprimer ce débours, merci de contacter l'administrateur pour cela", true, 15);
            else if (element.id) {
              for (let i = 0; i < this.provision.debours.length; i++)
                if (this.provision.debours[i].id == element.id) {
                  this.provision.debours.splice(i, 1);
                  this.provisionChange.next(this.provision);
                  break;
                }
            }
          }
        }, display: true,
      } as SortTableAction);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.provision) {
      this.setData();
    }
    if (changes.editMode) {
      this.addInvoicedAmountColumn();
      this.addDeleteDeboursColumn();
    }
  }

  setData() {
    if (!this.newDebour) {
      this.newDebour = {} as Debour;
      this.refreshTable.next();
    }
  }

  fillInvoicedAmount() {
    if (this.newDebour && this.newDebour.billingType && this.newDebour.debourAmount)
      this.newDebour.invoicedAmount = this.newDebour.debourAmount;
  }

  canAddNewInvoice() {
    return this.habilitationService.canAddNewInvoice();
  }

  createInvoice(event: any, competentAuthority: CompetentAuthority) {
    if (this.customerOrder)
      this.appService.openRoute(event, "/invoicing/add/debour/" + competentAuthority.id + "/" + this.customerOrder.id, undefined);
  }

  addDebour() {
    if (this.getFormStatus()) {
      if (this.newDebour) {
        if (this.newDebour.paymentDateTime)
          this.newDebour.paymentDateTime = new Date(this.newDebour.paymentDateTime.setHours(12));

        const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
          data: {
            title: "Ajouter le débour ?",
            content: "Êtes-vous sûr de vouloir ajouter ce débour ? Cette action est irréversible ! ",
            closeActionText: "Annuler",
            validationActionText: "Confirmer"
          }
        });

        dialogRef.afterClosed().subscribe(dialogResult => {
          if (dialogResult) {
            if (this.provision) {
              if (!this.provision.debours)
                this.provision.debours = [];
              this.provision.debours.push(this.newDebour!);
              this.provisionChange.next(this.provision);
              this.newDebour = undefined;
              this.setData();
            }
          }
        });
      }
    }
  }

  debourForm = this.formBuilder.group({
  })

  getFormStatus(): boolean {
    return this.debourForm.valid;
  }

  getCompetentAuthorities(): CompetentAuthority[] {
    let outCompetentAuthorities: CompetentAuthority[] = [];
    if (this.provision && this.provision.debours && this.provision.debours.length > 0)
      for (let debour of this.provision.debours) {
        let found = false;
        for (let outCompetentAuthority of outCompetentAuthorities)
          if (outCompetentAuthority.id == debour.competentAuthority.id)
            found = true
        if (!found)
          outCompetentAuthorities.push(debour.competentAuthority);
      }
    return outCompetentAuthorities;
  }

  getTotalForCompetentAuthority(competentAuthority: CompetentAuthority): number {
    let total: number = 0;

    if (this.provision && this.provision.debours && this.provision.debours.length > 0)
      for (let debour of this.provision.debours) {
        if (debour.competentAuthority.id == competentAuthority.id)
          total += parseFloat(debour.debourAmount + "");
      }

    return Math.round(total * 100) / 100;
  }

  filledForInfogreffeKbis() {
    if (this.newDebour) {
      this.newDebour.billingType = this.constantService.getBillingTypeInfogreffeDebour();
      this.newDebour.competentAuthority = this.constantService.getCompetentAuthorityInfogreffe();
      this.newDebour.debourAmount = 3.37;
      this.fillInvoicedAmount();
      this.newDebour.comments = 'Kbis';
      this.newDebour.paymentType = this.constantService.getPaymentTypePrelevement();
    }
  }
}
