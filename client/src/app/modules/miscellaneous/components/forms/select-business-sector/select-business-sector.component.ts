import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { BusinessSector } from 'src/app/modules/tiers/model/BusinessSector';
import { DiscoveringOrigin } from 'src/app/modules/tiers/model/DiscoveringOrigin';
import { BusinessSectorService } from 'src/app/modules/tiers/services/business.sector.service';
import { AppService } from 'src/app/services/app.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-business-sector',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})

export class SelectBusinessSectorComponent extends GenericSelectComponent<BusinessSector> implements OnInit {
  types: DiscoveringOrigin[] = [] as Array<DiscoveringOrigin>;

  constructor(private formBuild: UntypedFormBuilder, private appService3: AppService, private businessSectorService: BusinessSectorService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.businessSectorService.getBusinessSectors().subscribe(response => {
      this.types = response;
      this.types = this.types.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

  compareWithId = compareWithId;

}
