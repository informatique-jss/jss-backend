import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { PaperSetType } from 'src/app/modules/miscellaneous/model/PaperSetType';
import { PaperSetTypeService } from 'src/app/modules/miscellaneous/services/paper.set.type.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-paper-set-type',
  templateUrl: './referential-paper-set.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialPaperSetTypeComponent extends GenericReferentialComponent<PaperSetType> implements OnInit {
  constructor(private paperSetTypeService: PaperSetTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<PaperSetType> {
    return this.paperSetTypeService.addOrUpdatePaperSetType(this.selectedEntity!);
  }
  getGetObservable(): Observable<PaperSetType[]> {
    return this.paperSetTypeService.getPaperSetTypes();
  }
}
