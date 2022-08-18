import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { QuotationLabelType } from 'src/app/modules/quotation/model/QuotationLabelType';
import { QuotationLabelTypeService } from 'src/app/modules/quotation/services/quotation.label.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-quotation-label-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialQuotationLabelTypeComponent extends GenericReferentialComponent<QuotationLabelType> implements OnInit {
  constructor(private quotationLabelTypeService: QuotationLabelTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<QuotationLabelType> {
    return this.quotationLabelTypeService.addOrUpdateQuotationLabelType(this.selectedEntity!);
  }
  getGetObservable(): Observable<QuotationLabelType[]> {
    return this.quotationLabelTypeService.getQuotationLabelTypes();
  }
}
