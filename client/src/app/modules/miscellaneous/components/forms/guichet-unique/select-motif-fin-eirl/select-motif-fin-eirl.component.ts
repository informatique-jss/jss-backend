import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MotifFinEirlService } from 'src/app/modules/miscellaneous/services/guichet-unique/motif.fin.eirl.service';
import { MotifFinEirl } from '../../../../../quotation/model/guichet-unique/referentials/MotifFinEirl';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-motif-fin-eirl',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectMotifFinEirlComponent extends GenericSelectComponent<MotifFinEirl> implements OnInit {

  types: MotifFinEirl[] = [] as Array<MotifFinEirl>;

  constructor(private formBuild: UntypedFormBuilder, private MotifFinEirlService: MotifFinEirlService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.MotifFinEirlService.getMotifFinEirl().subscribe(response => {
      this.types = response;
    })
  }
}
