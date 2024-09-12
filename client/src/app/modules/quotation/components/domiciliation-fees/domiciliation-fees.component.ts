import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { CUSTOMER_ORDER_STATUS_BILLED } from 'src/app/libs/Constants';
import { formatDateTimeForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { instanceOfCustomerOrder } from 'src/app/libs/TypeHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { CUSTOMER_ORDER_STATUS_ABANDONED } from '../../../../libs/Constants';
import { DomiciliationFee } from '../../model/DomiciliationFee';
import { IQuotation } from '../../model/IQuotation';
import { Provision } from '../../model/Provision';
import { DomiciliationFeeService } from '../../services/domiciliation.fee.service';

@Component({
  selector: 'domiciliation-fees',
  templateUrl: './domiciliation-fees.component.html',
  styleUrls: ['./domiciliation-fees.component.css']
})
export class DomiciliationFeesComponent implements OnInit {

  @Input() provision: Provision | undefined;
  @Input() editMode: boolean = false;
  @Input() quotation: IQuotation | undefined;
  @Output() provisionChange: EventEmitter<Provision> = new EventEmitter<Provision>();

  domiciliationFeesDisplayedColumns: SortTableColumn<DomiciliationFee>[] = [];
  domiciliationFeesTableActions: SortTableAction<DomiciliationFee>[] = [];

  newFee: DomiciliationFee = {} as DomiciliationFee;

  constructor(
    private appService: AppService,
    private formBuilder: FormBuilder,
    private domiciliationFeeService: DomiciliationFeeService,
  ) { }

  domiciliationFeesForm = this.formBuilder.group({});

  ngOnInit() {
    this.domiciliationFeesDisplayedColumns = [];
    this.domiciliationFeesDisplayedColumns.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn<DomiciliationFee>);
    this.domiciliationFeesDisplayedColumns.push({ id: "feeDate", fieldName: "feeDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn<DomiciliationFee>);
    this.domiciliationFeesDisplayedColumns.push({ id: "amount", fieldName: "amount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn<DomiciliationFee>);
    this.domiciliationFeesDisplayedColumns.push({ id: "billingType", fieldName: "billingType.label", label: "Type" } as SortTableColumn<DomiciliationFee>);


    this.domiciliationFeesTableActions.push({
      actionIcon: "delete", actionName: "Supprimer le frais", actionClick: (column: SortTableAction<DomiciliationFee>, element: DomiciliationFee, event: any) => {
        if (element && this.quotation && instanceOfCustomerOrder(this.quotation) && this.quotation.customerOrderStatus.code != CUSTOMER_ORDER_STATUS_BILLED && this.quotation.customerOrderStatus.code != CUSTOMER_ORDER_STATUS_ABANDONED) {
          this.domiciliationFeeService.deleteDomiciliationFee(element).subscribe(fee => {
            this.appService.openRoute(null, '/order/' + this.quotation!.id, null);
          })
        }
      }, display: true,
    } as SortTableAction<DomiciliationFee>);
  }

  addNewFee() {
    if (this.newFee && this.domiciliationFeesForm.valid && this.provision && this.provision.domiciliation) {
      this.newFee.feeDate = new Date(this.newFee.feeDate.setHours(12));
      this.newFee.domiciliation = this.provision.domiciliation;
      this.domiciliationFeeService.addDomiciliationFee(this.newFee).subscribe(fee => {
        this.appService.openRoute(null, '/order/' + this.quotation!.id, null);
      })
    }
  }

}


