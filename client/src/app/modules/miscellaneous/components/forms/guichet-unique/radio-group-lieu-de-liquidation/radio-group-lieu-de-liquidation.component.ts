import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { LieuDeLiquidationService } from 'src/app/modules/miscellaneous/services/guichet-unique/lieu.de.liquidation.service';
import { LieuDeLiquidation } from 'src/app/modules/quotation/model/guichet-unique/referentials/LieuDeLiquidation';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-lieu-de-liquidation',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupLieuDeLiquidationComponent extends GenericRadioGroupComponent<LieuDeLiquidation> implements OnInit {
  types: LieuDeLiquidation[] = [] as Array<LieuDeLiquidation>;

  constructor(
    private formBuild: UntypedFormBuilder, private LieuDeLiquidationService: LieuDeLiquidationService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.LieuDeLiquidationService.getLieuDeLiquidation().subscribe(response => { this.types = response })
  }
}
