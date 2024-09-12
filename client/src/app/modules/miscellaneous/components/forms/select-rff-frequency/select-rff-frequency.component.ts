import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { RffFrequency } from 'src/app/modules/tiers/model/RffFrequency';
import { RffFrequencyService } from '../../../../tiers/services/rff.frequency.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-rff-frequency',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})
export class SelectRffFrequencyComponent extends GenericSelectComponent<RffFrequency> implements OnInit {

  types: RffFrequency[] = [] as Array<RffFrequency>;

  constructor(private formBuild: UntypedFormBuilder,
    private rffFrequencyService: RffFrequencyService,
    private appService3: AppService
  ) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.types = [];
    this.rffFrequencyService.getRffFrequencies().subscribe(response => {
      this.types = response;
      this.types = this.types.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

}
