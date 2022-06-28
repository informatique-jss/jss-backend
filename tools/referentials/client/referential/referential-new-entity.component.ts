import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { NewEntity } from 'src/app/modules/targetPackage/model/NewEntity';
import { NewEntityService } from 'src/app/modules/targetPackage/services/serviceName.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-entryPointNameSingular',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialNewEntityComponent extends GenericReferentialComponent<NewEntity> implements OnInit {
  constructor(private newEntityService: NewEntityService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<NewEntity> {
    return this.newEntityService.addOrUpdateNewEntity(this.selectedEntity!);
  }
  getGetObservable(): Observable<NewEntity[]> {
    return this.newEntityService.getNewEntities();
  }
}
