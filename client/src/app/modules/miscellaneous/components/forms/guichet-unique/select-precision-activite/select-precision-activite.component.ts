import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { PrecisionActiviteService } from 'src/app/modules/miscellaneous/services/guichet-unique/precision.activite.service';
import { PrecisionActivite } from '../../../../../quotation/model/guichet-unique/referentials/PrecisionActivite';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-precision-activite',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectPrecisionActiviteComponent extends GenericSelectComponent<PrecisionActivite> implements OnInit {

  types: PrecisionActivite[] = [] as Array<PrecisionActivite>;

  constructor(private formBuild: UntypedFormBuilder, private PrecisionActiviteService: PrecisionActiviteService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.PrecisionActiviteService.getPrecisionActivite().subscribe(response => {
      this.types = response;
    })
  }
}
