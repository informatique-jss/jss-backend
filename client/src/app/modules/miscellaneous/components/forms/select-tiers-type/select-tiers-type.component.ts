import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TiersType } from 'src/app/modules/tiers/model/TiersType';
import { TiersTypeService } from 'src/app/modules/tiers/services/tiers.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-tiers-type',
  templateUrl: './select-tiers-type.component.html',
  styleUrls: ['./select-tiers-type.component.css']
})
export class SelectTiersTypeComponent extends GenericSelectComponent<TiersType> implements OnInit {

  types: TiersType[] = [] as Array<TiersType>;

  constructor(private formBuild: UntypedFormBuilder, private tiersTypeService: TiersTypeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.tiersTypeService.getTiersTypes().subscribe(response => {
      this.types = response;
    })
  }
}
