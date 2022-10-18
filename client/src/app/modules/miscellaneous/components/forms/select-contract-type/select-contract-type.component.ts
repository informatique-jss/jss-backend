import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { DomiciliationContractType } from 'src/app/modules/quotation/model/DomiciliationContractType';
import { DomiciliationContractTypeService } from 'src/app/modules/quotation/services/domiciliation.contract.type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-contract-type',
  templateUrl: './select-contract-type.component.html',
  styleUrls: ['./select-contract-type.component.css']
})
export class SelectContractTypeComponent extends GenericSelectComponent<DomiciliationContractType> implements OnInit {

  types: DomiciliationContractType[] = [] as Array<DomiciliationContractType>;

  constructor(private formBuild: UntypedFormBuilder, private domiciliationContractTypeService: DomiciliationContractTypeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.domiciliationContractTypeService.getContractTypes().subscribe(response => {
      this.types = response;
    })
  }
}
