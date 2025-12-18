import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { CompanySize } from 'src/app/modules/tiers/model/CompanySize';
import { CompanySizeService } from 'src/app/modules/tiers/services/company.size.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-company-size',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialCompanySizeComponent extends GenericReferentialComponent<CompanySize> implements OnInit {
  constructor(private companySizeService: CompanySizeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<CompanySize> {
    return this.companySizeService.addOrUpdateCompanySize(this.selectedEntity!);
  }
  getGetObservable(): Observable<CompanySize[]> {
    return this.companySizeService.getCompanySizes();
  }
}
