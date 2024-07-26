import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { OptionParticuliereRegimeBenefiService } from 'src/app/modules/miscellaneous/services/guichet-unique/option.particuliere.regime.benefi.service';
import { OptionParticuliereRegimeBenefi } from 'src/app/modules/quotation/model/guichet-unique/referentials/OptionParticuliereRegimeBenefi';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-option-particuliere-regime-benefi',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupOptionParticuliereRegimeBenefiComponent extends GenericRadioGroupComponent<OptionParticuliereRegimeBenefi> implements OnInit {
  types: OptionParticuliereRegimeBenefi[] = [] as Array<OptionParticuliereRegimeBenefi>;

  constructor(
    private formBuild: UntypedFormBuilder, private OptionParticuliereRegimeBenefiService: OptionParticuliereRegimeBenefiService,) {
    super(formBuild);
  }

  initTypes(): void {
    this.OptionParticuliereRegimeBenefiService.getOptionParticuliereRegimeBenefi().subscribe(response => { this.types = response })
  }
}
