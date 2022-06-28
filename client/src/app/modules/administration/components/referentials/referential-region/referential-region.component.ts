import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Region } from 'src/app/modules/miscellaneous/model/Region';
import { RegionService } from 'src/app/modules/miscellaneous/services/region.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-region',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialRegionComponent extends GenericReferentialComponent<Region> implements OnInit {
  constructor(private regionService: RegionService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<Region> {
    return this.regionService.addOrUpdateRegion(this.selectedEntity!);
  }
  getGetObservable(): Observable<Region[]> {
    return this.regionService.getRegions();
  }
}
