import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { TiersCategory } from 'src/app/modules/tiers/model/TiersCategory';
import { TiersCategoryService } from 'src/app/modules/tiers/services/tiers.category.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-tiers-category',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialTiersCategoryComponent extends GenericReferentialComponent<TiersCategory> implements OnInit {
  constructor(private tiersCategoryService: TiersCategoryService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<TiersCategory> {
    return this.tiersCategoryService.addOrUpdateTiersCategory(this.selectedEntity!);
  }
  getGetObservable(): Observable<TiersCategory[]> {
    return this.tiersCategoryService.getTiersCategories();
  }
}
