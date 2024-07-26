import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { RegimeImpositionTVAService } from 'src/app/modules/miscellaneous/services/guichet-unique/regime.imposition.tva.service';
import { RegimeImpositionTVA } from '../../../../../quotation/model/guichet-unique/referentials/RegimeImpositionTVA';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-regime-imposition-tva',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectRegimeImpositionTVAComponent extends GenericSelectComponent<RegimeImpositionTVA> implements OnInit {

  types: RegimeImpositionTVA[] = [] as Array<RegimeImpositionTVA>;

  constructor(private formBuild: UntypedFormBuilder, private RegimeImpositionTVAService: RegimeImpositionTVAService,) {
    super(formBuild,)
  }

  initTypes(): void {
    this.RegimeImpositionTVAService.getRegimeImpositionTVA().subscribe(response => {
      this.types = response;
    })
  }
}
