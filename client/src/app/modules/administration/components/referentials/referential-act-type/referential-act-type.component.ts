import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { ActType } from 'src/app/modules/quotation/model/ActType';
import { ActTypeService } from 'src/app/modules/quotation/services/act-type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-act-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialActTypeComponent extends GenericReferentialComponent<ActType> implements OnInit {
  constructor(private actTypeService: ActTypeService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<ActType> {
    return this.actTypeService.addOrUpdateActType(this.selectedEntity!);
  }
  getGetObservable(): Observable<ActType[]> {
    return this.actTypeService.getActTypes();
  }
}
