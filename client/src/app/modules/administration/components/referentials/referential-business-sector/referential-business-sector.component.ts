import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { BusinessSector } from 'src/app/modules/tiers/model/BusinessSector';
import { BusinessSectorService } from 'src/app/modules/tiers/services/business.sector.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-business-sector',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialBusinessSectorComponent extends GenericReferentialComponent<BusinessSector> implements OnInit {
  constructor(private businessSectorService: BusinessSectorService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<BusinessSector> {
    return this.businessSectorService.addOrUpdateBusinessSector(this.selectedEntity!);
  }
  getGetObservable(): Observable<BusinessSector[]> {
    return this.businessSectorService.getBusinessSectors();
  }
}
