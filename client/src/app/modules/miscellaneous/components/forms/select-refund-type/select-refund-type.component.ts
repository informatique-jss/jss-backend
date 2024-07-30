import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { RefundType } from 'src/app/modules/tiers/model/RefundType';
import { RefundTypeService } from 'src/app/modules/tiers/services/refund.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-refund-type',
  templateUrl: './select-refund-type.component.html',
  styleUrls: ['./select-refund-type.component.css']
})
export class SelectRefundTypeComponent extends GenericSelectComponent<RefundType> implements OnInit {

  types: RefundType[] = [] as Array<RefundType>;

  constructor(private formBuild: UntypedFormBuilder, private refundTypeService: RefundTypeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.refundTypeService.getRefundTypes().subscribe(response => {
      this.types = response;
    })
  }
}
