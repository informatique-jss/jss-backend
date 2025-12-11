import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { DiscoveringOrigin } from 'src/app/modules/tiers/model/DiscoveringOrigin';
import { DiscoveringOriginService } from 'src/app/modules/tiers/services/discovering.origin.service';
import { AppService } from 'src/app/services/app.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-discovery-origin',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})

export class SelectDiscoveringOriginComponent extends GenericSelectComponent<DiscoveringOrigin> implements OnInit {
  types: DiscoveringOrigin[] = [] as Array<DiscoveringOrigin>;

  constructor(private formBuild: UntypedFormBuilder, private appService3: AppService, private discoveryOriginService: DiscoveringOriginService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.discoveryOriginService.getDiscoveringOrigins().subscribe(response => {
      this.types = response;
      this.types = this.types.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

  compareWithId = compareWithId;

}
