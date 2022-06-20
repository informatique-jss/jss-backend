import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { DomiciliationContractType } from 'src/app/modules/quotation/model/DomiciliationContractType';
import { DomiciliationContractTypeService } from 'src/app/modules/quotation/services/domiciliation.contract.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-contract-type',
  templateUrl: './select-contract-type.component.html',
  styleUrls: ['./select-contract-type.component.css']
})
export class SelectContractTypeComponent extends GenericSelectComponent<DomiciliationContractType> implements OnInit {

  types: DomiciliationContractType[] = [] as Array<DomiciliationContractType>;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: FormBuilder, private domiciliationContractTypeService: DomiciliationContractTypeService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.domiciliationContractTypeService.getContractTypes().subscribe(response => {
      this.types = response;
    })
  }
}
