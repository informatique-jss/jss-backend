import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { RecordType } from 'src/app/modules/quotation/model/RecordType';
import { RecordTypeService } from 'src/app/modules/quotation/services/record.type.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-record-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialRecordTypeComponent extends GenericReferentialComponent<RecordType> implements OnInit {
  constructor(private recordTypeService: RecordTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<RecordType> {
    return this.recordTypeService.addOrUpdateRecordType(this.selectedEntity!);
  }
  getGetObservable(): Observable<RecordType[]> {
    return this.recordTypeService.getRecordTypes();
  }
}
