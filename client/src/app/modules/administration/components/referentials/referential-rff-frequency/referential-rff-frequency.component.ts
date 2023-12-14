import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { RffFrequency } from 'src/app/modules/tiers/model/RffFrequency';
import { RffFrequencyService } from 'src/app/modules/tiers/services/rff.frequency.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-rff-frequency',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialRffFrequencyComponent extends GenericReferentialComponent<RffFrequency> implements OnInit {
  constructor(private rffFrequencyService: RffFrequencyService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<RffFrequency> {
    return this.rffFrequencyService.addOrUpdateRffFrequency(this.selectedEntity!);
  }
  getGetObservable(): Observable<RffFrequency[]> {
    return this.rffFrequencyService.getRffFrequencies();
  }
}
