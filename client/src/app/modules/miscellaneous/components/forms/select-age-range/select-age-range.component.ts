import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { AgeRangeService } from 'src/app/modules/tiers/services/age.range.service';
import { AppService } from 'src/app/services/app.service';
import { AgeRange } from '../../../../tiers/model/AgeRange';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-age-range',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})

export class SelectAgeRangeComponent extends GenericSelectComponent<AgeRange> implements OnInit {
  types: AgeRange[] = [] as Array<AgeRange>;

  constructor(private formBuild: UntypedFormBuilder, private appService3: AppService, private ageRangeService: AgeRangeService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.ageRangeService.getAgeRanges().subscribe(response => {
      this.types = response;
      this.types = this.types.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

  compareWithId = compareWithId;

}
