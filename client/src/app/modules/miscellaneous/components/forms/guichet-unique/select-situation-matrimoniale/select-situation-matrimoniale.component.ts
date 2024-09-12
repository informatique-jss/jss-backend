import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SituationMatrimonialeService } from 'src/app/modules/miscellaneous/services/guichet-unique/situation.matrimoniale.service';
import { SituationMatrimoniale } from '../../../../../quotation/model/guichet-unique/referentials/SituationMatrimoniale';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-situation-matrimoniale',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectSituationMatrimonialeComponent extends GenericSelectComponent<SituationMatrimoniale> implements OnInit {

  types: SituationMatrimoniale[] = [] as Array<SituationMatrimoniale>;

  constructor(private formBuild: UntypedFormBuilder, private SituationMatrimonialeService: SituationMatrimonialeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.SituationMatrimonialeService.getSituationMatrimoniale().subscribe(response => {
      this.types = response;
    })
  }
}
