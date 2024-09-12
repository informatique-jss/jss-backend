import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { RegimeImpositionBenefices2Service } from 'src/app/modules/miscellaneous/services/guichet-unique/regime.imposition.benefices2.service';
import { RegimeImpositionBenefices2 } from '../../../../../quotation/model/guichet-unique/referentials/RegimeImpositionBenefices2';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';
import { A } from '@angular/cdk/keycodes';

@Component({
  selector: 'select-regime-imposition-benefices2',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectRegimeImpositionBenefices2Component extends GenericSelectComponent<RegimeImpositionBenefices2> implements OnInit {

  types: RegimeImpositionBenefices2[] = [] as Array<RegimeImpositionBenefices2>;

  constructor(private formBuild: UntypedFormBuilder, private RegimeImpositionBenefices2Service: RegimeImpositionBenefices2Service, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.RegimeImpositionBenefices2Service.getRegimeImpositionBenefices2().subscribe(response => {
      this.types = response;
    })
  }
}
