import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ConditionVersementTVAService } from 'src/app/modules/miscellaneous/services/guichet-unique/condition.versement.tva.service';
import { ConditionVersementTVA } from '../../../../../quotation/model/guichet-unique/referentials/ConditionVersementTVA';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-condition-versement-tva',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectConditionVersementTVAComponent extends GenericSelectComponent<ConditionVersementTVA> implements OnInit {

  types: ConditionVersementTVA[] = [] as Array<ConditionVersementTVA>;

  constructor(private formBuild: UntypedFormBuilder, private ConditionVersementTVAService: ConditionVersementTVAService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.ConditionVersementTVAService.getConditionVersementTVA().subscribe(response => {
      this.types = response;
    })
  }
}
