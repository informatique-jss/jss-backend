import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { BuildingDomiciliation } from 'src/app/modules/quotation/model/BuildingDomiciliation';
import { BuildingDomiciliationService } from 'src/app/modules/quotation/services/building.domiciliation.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-building-domiciliation',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialBuildingDomiciliationComponent extends GenericReferentialComponent<BuildingDomiciliation> implements OnInit {
  constructor(private buildingDomiciliationService: BuildingDomiciliationService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<BuildingDomiciliation> {
    return this.buildingDomiciliationService.addOrUpdateBuildingDomiciliation(this.selectedEntity!);
  }
  getGetObservable(): Observable<BuildingDomiciliation[]> {
    return this.buildingDomiciliationService.getBuildingDomiciliations();
  }
}
