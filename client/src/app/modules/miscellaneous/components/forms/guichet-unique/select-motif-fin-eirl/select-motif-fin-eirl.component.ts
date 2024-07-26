import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MotifFinEirlService } from 'src/app/modules/miscellaneous/services/guichet-unique/motif.fin.eirl.service';
import { MotifFinEirl } from '../../../../../quotation/model/guichet-unique/referentials/MotifFinEirl';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-motif-fin-eirl',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectMotifFinEirlComponent extends GenericSelectComponent<MotifFinEirl> implements OnInit {

  types: MotifFinEirl[] = [] as Array<MotifFinEirl>;

  constructor(private formBuild: UntypedFormBuilder, private MotifFinEirlService: MotifFinEirlService,) {
    super(formBuild,)
  }

  initTypes(): void {
    this.MotifFinEirlService.getMotifFinEirl().subscribe(response => {
      this.types = response;
    })
  }
}
