import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { PaymentType } from '../../../model/PaymentType';
import { PaymentTypeService } from '../../../services/payment-type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-payment-types',
  templateUrl: './select-payment-types.component.html',
  styleUrls: ['./select-payment-types.component.css']
})
export class SelectPaymentTypesComponent extends GenericSelectComponent<PaymentType> implements OnInit {

  types: PaymentType[] = [] as Array<PaymentType>;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: FormBuilder, private paymentTypeService: PaymentTypeService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.paymentTypeService.getPaymentTypes().subscribe(response => {
      this.types = response;
    })
  }
}
