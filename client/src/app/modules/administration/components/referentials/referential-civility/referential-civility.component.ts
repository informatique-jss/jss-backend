import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { Civility } from 'src/app/modules/miscellaneous/model/Civility';
import { CivilityService } from 'src/app/modules/miscellaneous/services/civility.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-civility',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialCivilityComponent extends GenericReferentialComponent<Civility> implements OnInit {
  constructor(private civilityService: CivilityService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }
  getAddOrUpdateObservable(): Observable<Civility> {
    return this.civilityService.addOrUpdateCivility(this.selectedEntity!);
  }
  getGetObservable(): Observable<Civility[]> {
    return this.civilityService.getCivilities();
  }
}
