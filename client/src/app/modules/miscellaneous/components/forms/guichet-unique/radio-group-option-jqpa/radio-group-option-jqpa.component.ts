import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { OptionJQPAService } from 'src/app/modules/miscellaneous/services/guichet-unique/option.jqpa.service';
import { OptionJQPA } from 'src/app/modules/quotation/model/guichet-unique/referentials/OptionJQPA';

import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-option-jqpa',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupOptionJQPAComponent extends GenericRadioGroupComponent<OptionJQPA> implements OnInit {
  types: OptionJQPA[] = [] as Array<OptionJQPA>;

  constructor(
    private formBuild: UntypedFormBuilder, private OptionJQPAService: OptionJQPAService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.OptionJQPAService.getOptionJQPA().subscribe(response => { this.types = response })
  }
}
