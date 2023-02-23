import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { PaymentType } from '../../../model/PaymentType';
import { ConstantService } from '../../../services/constant.service';
import { PaymentTypeService } from '../../../services/payment.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-payment-types',
  templateUrl: './select-payment-types.component.html',
  styleUrls: ['./select-payment-types.component.css']
})
export class SelectPaymentTypesComponent extends GenericSelectComponent<PaymentType> implements OnInit {

  types: PaymentType[] = [] as Array<PaymentType>;

  /**
 * Limit the list of payment type code displayed
 */
  @Input() filteredPaymentType: PaymentType[] | undefined;

  paymentTypePrelevement: PaymentType = this.constantService.getPaymentTypePrelevement();

  constructor(private formBuild: UntypedFormBuilder, private paymentTypeService: PaymentTypeService, private userNoteService2: UserNoteService,
    private constantService: ConstantService) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.paymentTypeService.getPaymentTypes().subscribe(response => {
      this.types = [];
      if (!this.filteredPaymentType) {
        this.types = response;
      } else {
        if (this.filteredPaymentType) {
          for (let paymentType of response) {
            for (let filter of this.filteredPaymentType)
              if (filter && filter.code == paymentType.code && this.types.indexOf(paymentType) < 0)
                this.types.push(paymentType);
          }
        }
      }
      if ((this.model == null || this.model == undefined) && this.types.length > 0) {
        for (const paymentType of this.types) {
          if (this.paymentTypePrelevement && paymentType.id == this.paymentTypePrelevement.id)
            this.model = paymentType;
          this.modelChange.emit(this.model);
        }
      }
    })
  }
}
