import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { LieuDeLiquidationService } from 'src/app/modules/miscellaneous/services/guichet-unique/lieu.de.liquidation.service';
import { LieuDeLiquidation } from 'src/app/modules/quotation/model/guichet-unique/referentials/LieuDeLiquidation';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-lieu-de-liquidation',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupLieuDeLiquidationComponent extends GenericRadioGroupComponent<LieuDeLiquidation> implements OnInit {
  types: LieuDeLiquidation[] = [] as Array<LieuDeLiquidation>;

  constructor(
    private formBuild: UntypedFormBuilder, private LieuDeLiquidationService: LieuDeLiquidationService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.LieuDeLiquidationService.getLieuDeLiquidation().subscribe(response => { this.types = response })
  }
}
