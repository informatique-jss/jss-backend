import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { PaymentWay } from 'src/app/modules/invoicing/model/PaymentWay';
import { PaymentWayService } from 'src/app/modules/invoicing/services/payment.way.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-payment-way-one',
  templateUrl: './select-payment-way-one.component.html',
  styleUrls: ['./select-payment-way-one.component.css']
})
export class SelectPaymentWayOneComponent extends GenericSelectComponent<PaymentWay> implements OnInit {

  types: PaymentWay[] = [] as Array<PaymentWay>;

  /**
 * List of code to autoselect at loading
 */
  @Input() defaulTypeSelected: PaymentWay[] | undefined;

  constructor(private formBuild: UntypedFormBuilder, private paymentWayService: PaymentWayService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.paymentWayService.getPaymentWays().subscribe(response => {
      this.types = response;
      if (this.defaulTypeSelected) {
        this.model = undefined;
        for (let type of this.types) {
          for (let defaultType of this.defaulTypeSelected) {
            if (type.id == defaultType.id) {
              this.model = type;
            }
          }
        }
        this.modelChange.emit(this.model);
        this.selectionChange.emit(this.model);
      }
    });
  }
}

