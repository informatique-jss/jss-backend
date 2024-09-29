import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MotifCessationService } from 'src/app/modules/miscellaneous/services/guichet-unique/motif.cessation.service';
import { MotifCessation } from '../../../../../quotation/model/guichet-unique/referentials/MotifCessation';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-motif-cessation',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectMotifCessationComponent extends GenericSelectComponent<MotifCessation> implements OnInit {

  types: MotifCessation[] = [] as Array<MotifCessation>;

  constructor(private formBuild: UntypedFormBuilder, private MotifCessationService: MotifCessationService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.MotifCessationService.getMotifCessation().subscribe(response => {
      this.types = response;
    })
  }
}
