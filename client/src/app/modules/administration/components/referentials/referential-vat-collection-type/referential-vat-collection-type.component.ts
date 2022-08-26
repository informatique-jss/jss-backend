import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { VatCollectionType } from 'src/app/modules/miscellaneous/model/VatCollectionType';
import { VatCollectionTypeService } from 'src/app/modules/miscellaneous/services/vat.collection.type.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-vat-collection-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialVatCollectionTypeComponent extends GenericReferentialComponent<VatCollectionType> implements OnInit {
  constructor(private vatCollectionTypeService: VatCollectionTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<VatCollectionType> {
    return this.vatCollectionTypeService.addOrUpdateVatCollectionType(this.selectedEntity!);
  }
  getGetObservable(): Observable<VatCollectionType[]> {
    return this.vatCollectionTypeService.getVatCollectionTypes();
  }
}
