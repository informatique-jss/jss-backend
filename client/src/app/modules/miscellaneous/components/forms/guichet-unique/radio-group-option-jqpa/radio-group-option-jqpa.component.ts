import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { OptionJQPAService } from 'src/app/modules/miscellaneous/services/guichet-unique/option.jqpa.service';
import { OptionJQPA } from 'src/app/modules/quotation/model/guichet-unique/referentials/OptionJQPA';

import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-option-jqpa',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupOptionJQPAComponent extends GenericRadioGroupComponent<OptionJQPA> implements OnInit {
  types: OptionJQPA[] = [] as Array<OptionJQPA>;

  constructor(
    private formBuild: UntypedFormBuilder, private OptionJQPAService: OptionJQPAService,) {
    super(formBuild,);
  }

  initTypes(): void {
    this.OptionJQPAService.getOptionJQPA().subscribe(response => { this.types = response })
  }
}
