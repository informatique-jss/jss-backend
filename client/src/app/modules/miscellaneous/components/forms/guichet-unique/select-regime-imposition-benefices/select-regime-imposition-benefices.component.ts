import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { RegimeImpositionBeneficesService } from 'src/app/modules/miscellaneous/services/guichet-unique/regime.imposition.benefices.service';
import { RegimeImpositionBenefices } from '../../../../../quotation/model/guichet-unique/referentials/RegimeImpositionBenefices';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-regime-imposition-benefices',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectRegimeImpositionBeneficesComponent extends GenericSelectComponent<RegimeImpositionBenefices> implements OnInit {

  types: RegimeImpositionBenefices[] = [] as Array<RegimeImpositionBenefices>;

  constructor(private formBuild: UntypedFormBuilder, private RegimeImpositionBeneficesService: RegimeImpositionBeneficesService,) {
    super(formBuild,)
  }

  initTypes(): void {
    this.RegimeImpositionBeneficesService.getRegimeImpositionBenefices().subscribe(response => {
      this.types = response;
    })
  }
}
