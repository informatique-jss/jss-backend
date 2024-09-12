import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { DomiciliationContractType } from 'src/app/modules/quotation/model/DomiciliationContractType';
import { DomiciliationContractTypeService } from 'src/app/modules/quotation/services/domiciliation.contract.type.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-domiciliation-contract-type',
  templateUrl: './referential-domiciliation-contract-type.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialDomiciliationContractTypeComponent extends GenericReferentialComponent<DomiciliationContractType> implements OnInit {
  constructor(private domiciliationContractTypeService: DomiciliationContractTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<DomiciliationContractType> {
    return this.domiciliationContractTypeService.addOrUpdateDomiciliationContractType(this.selectedEntity!);
  }
  getGetObservable(): Observable<DomiciliationContractType[]> {
    return this.domiciliationContractTypeService.getContractTypes();
  }
}
