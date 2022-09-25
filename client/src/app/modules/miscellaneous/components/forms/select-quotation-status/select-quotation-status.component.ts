import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { QuotationStatus } from 'src/app/modules/quotation/model/QuotationStatus';
import { QuotationStatusService } from 'src/app/modules/quotation/services/quotation-status.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-quotation-status',
  templateUrl: './select-quotation-status.component.html',
  styleUrls: ['./select-quotation-status.component.css']
})

export class SelectOrderingStatusComponent extends GenericMultipleSelectComponent<QuotationStatus> implements OnInit {

  types: QuotationStatus[] = [] as Array<QuotationStatus>;

  /**
 * List of code to autoselect at loading
 */
  @Input() defaultCodesSelected: string[] | undefined;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private quotationStatusService: QuotationStatusService,
    private formBuild: UntypedFormBuilder) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.quotationStatusService.getQuotationStatus().subscribe(response => {
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
