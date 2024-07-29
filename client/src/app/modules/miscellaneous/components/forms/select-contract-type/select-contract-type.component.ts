import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { DomiciliationContractType } from 'src/app/modules/quotation/model/DomiciliationContractType';
import { DomiciliationContractTypeService } from 'src/app/modules/quotation/services/domiciliation.contract.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-contract-type',
  templateUrl: './select-contract-type.component.html',
  styleUrls: ['./select-contract-type.component.css']
})
export class SelectContractTypeComponent extends GenericSelectComponent<DomiciliationContractType> implements OnInit {

  types: DomiciliationContractType[] = [] as Array<DomiciliationContractType>;

  constructor(private formBuild: UntypedFormBuilder, private domiciliationContractTypeService: DomiciliationContractTypeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.domiciliationContractTypeService.getContractTypes().subscribe(response => {
      this.types = response;
    })
  }
}
