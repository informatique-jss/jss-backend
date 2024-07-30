import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SuccursaleOuFilialeService } from 'src/app/modules/miscellaneous/services/guichet-unique/succursale.ou.filiale.service';
import { SuccursaleOuFiliale } from '../../../../../quotation/model/guichet-unique/referentials/SuccursaleOuFiliale';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-succursale-ou-filiale',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectSuccursaleOuFilialeComponent extends GenericSelectComponent<SuccursaleOuFiliale> implements OnInit {

  types: SuccursaleOuFiliale[] = [] as Array<SuccursaleOuFiliale>;

  constructor(private formBuild: UntypedFormBuilder, private SuccursaleOuFilialeService: SuccursaleOuFilialeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.SuccursaleOuFilialeService.getSuccursaleOuFiliale().subscribe(response => {
      this.types = response;
    })
  }
}
