import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { DomiciliationContractType } from '../../../../quotation/model/DomiciliationContractType';
import { DomiciliationContractTypeService } from '../../../../quotation/services/domiciliation.contract.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-contract-type',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SelectContractTypeComponent extends GenericSelectComponent<DomiciliationContractType> implements OnInit {

  @Input() types: DomiciliationContractType[] = [] as Array<DomiciliationContractType>;

  constructor(private formBuild: UntypedFormBuilder,
    private domiciliationContractTypeService: DomiciliationContractTypeService) {
    super(formBuild)
  }

  initTypes(): void {
    this.domiciliationContractTypeService.getContractTypes().subscribe(response => {
      this.types = response;
    })
  }
}
