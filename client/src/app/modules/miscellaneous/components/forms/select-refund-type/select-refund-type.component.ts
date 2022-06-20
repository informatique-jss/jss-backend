import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { RefundType } from 'src/app/modules/tiers/model/RefundType';
import { RefundTypeService } from 'src/app/modules/tiers/services/refund.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-refund-type',
  templateUrl: './select-refund-type.component.html',
  styleUrls: ['./select-refund-type.component.css']
})
export class SelectRefundTypeComponent extends GenericSelectComponent<RefundType> implements OnInit {

  types: RefundType[] = [] as Array<RefundType>;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: UntypedFormBuilder, private refundTypeService: RefundTypeService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.refundTypeService.getRefundTypes().subscribe(response => {
      this.types = response;
    })
  }
}
