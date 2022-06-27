import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { ProvisionFamilyType } from 'src/app/modules/quotation/model/ProvisionFamilyType';
import { QuotationStatus } from 'src/app/modules/quotation/model/QuotationStatus';
import { QuotationStatusService } from 'src/app/modules/quotation/services/quotation-status.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-quotation-status',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialQuotationStatusComponent extends GenericReferentialComponent<QuotationStatus> implements OnInit {
  constructor(private quotationStatusService: QuotationStatusService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<ProvisionFamilyType> {
    return this.quotationStatusService.addOrUpdateQuotationStatus(this.selectedEntity!);
  }
  getGetObservable(): Observable<ProvisionFamilyType[]> {
    return this.quotationStatusService.getQuotationStatus();
  }
}
