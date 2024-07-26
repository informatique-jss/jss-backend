import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TiersCategory } from 'src/app/modules/tiers/model/TiersCategory';
import { TiersCategoryService } from 'src/app/modules/tiers/services/tiers.category.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-tiers-category',
  templateUrl: './select-tiers-category.component.html',
  styleUrls: ['./select-tiers-category.component.css']
})
export class SelectTiersCategoryComponent extends GenericSelectComponent<TiersCategory> implements OnInit {

  types: TiersCategory[] = [] as Array<TiersCategory>;

  constructor(private formBuild: UntypedFormBuilder, private tiersCategoryService: TiersCategoryService,) {
    super(formBuild)
  }

  initTypes(): void {
    this.tiersCategoryService.getTiersCategories().subscribe(response => {
      this.types = response;
    })
  }
}
