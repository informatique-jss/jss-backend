import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { PaymentWay } from 'src/app/modules/invoicing/model/PaymentWay';
import { PaymentWayService } from 'src/app/modules/invoicing/services/payment.way.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-payment-way',
  templateUrl: './select-payment-way.component.html',
  styleUrls: ['./select-payment-way.component.css']
})
export class SelectPaymentWayComponent extends GenericMultipleSelectComponent<PaymentWay> implements OnInit {

  types: PaymentWay[] = [] as Array<PaymentWay>;

  /**
 * List of code to autoselect at loading
 */
  @Input() defaultCodesSelected: string[] | undefined;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private paymentWayService: PaymentWayService,
    private formBuild: UntypedFormBuilder) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.paymentWayService.getPaymentWays().subscribe(response => {
      this.types = response;
      if (this.defaultCodesSelected) {
        this.model = [];
        for (let type of this.types) {
          for (let defaultCode of this.defaultCodesSelected) {
            if (type.code == defaultCode) {
              this.model.push(type);
            }
          }
        }
        this.modelChange.emit(this.model);
        this.selectionChange.emit(this.model);
      }
    });
  }
}

