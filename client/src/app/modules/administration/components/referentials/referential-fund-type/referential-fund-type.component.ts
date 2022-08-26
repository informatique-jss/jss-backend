import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { FundType } from 'src/app/modules/quotation/model/FundType';
import { FundTypeService } from 'src/app/modules/quotation/services/fund-type.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-fund-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialFundTypeComponent extends GenericReferentialComponent<FundType> implements OnInit {
  constructor(private fundTypeService: FundTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<FundType> {
    return this.fundTypeService.addOrUpdateFundType(this.selectedEntity!);
  }
  getGetObservable(): Observable<FundType[]> {
    return this.fundTypeService.getFundTypes();
  }
}
