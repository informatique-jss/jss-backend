import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { CompetentAuthorityType } from 'src/app/modules/miscellaneous/model/CompetentAuthorityType';
import { CompetentAuthorityTypeService } from 'src/app/modules/miscellaneous/services/competent.authority.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-competent-authority-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialCompetentAuthorityTypeComponent extends GenericReferentialComponent<CompetentAuthorityType> implements OnInit {
  constructor(private competentAuthorityTypeService: CompetentAuthorityTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<CompetentAuthorityType> {
    return this.competentAuthorityTypeService.addOrUpdateCompetentAuthorityType(this.selectedEntity!);
  }
  getGetObservable(): Observable<CompetentAuthorityType[]> {
    return this.competentAuthorityTypeService.getCompetentAuthorityTypes();
  }
}
