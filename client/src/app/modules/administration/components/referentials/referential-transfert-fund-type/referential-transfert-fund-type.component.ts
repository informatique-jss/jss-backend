import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { TransfertFundsType } from 'src/app/modules/quotation/model/TransfertFundsType';
import { TransfertFundsTypeService } from 'src/app/modules/quotation/services/transfert-funds-type.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-transfert-fund-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialTransfertFundsTypeComponent extends GenericReferentialComponent<TransfertFundsType> implements OnInit {
  constructor(private transfertFundsTypeService: TransfertFundsTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<TransfertFundsType> {
    return this.transfertFundsTypeService.addOrUpdateTransfertFundsType(this.selectedEntity!);
  }
  getGetObservable(): Observable<TransfertFundsType[]> {
    return this.transfertFundsTypeService.getTransfertFundsTypes();
  }
}
