import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { AgeRange } from 'src/app/modules/tiers/model/AgeRange';
import { AgeRangeService } from 'src/app/modules/tiers/services/age.range.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-age-range',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialAgeRangeComponent extends GenericReferentialComponent<AgeRange> implements OnInit {
  constructor(private ageRangeService: AgeRangeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<AgeRange> {
    return this.ageRangeService.addOrUpdateAgeRange(this.selectedEntity!);
  }
  getGetObservable(): Observable<AgeRange[]> {
    return this.ageRangeService.getAgeRanges();
  }
}
