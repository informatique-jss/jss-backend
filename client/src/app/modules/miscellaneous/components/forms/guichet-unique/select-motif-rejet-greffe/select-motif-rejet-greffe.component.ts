import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MotifRejetGreffeService } from 'src/app/modules/miscellaneous/services/guichet-unique/motif.rejet.greffe.service';
import { MotifRejetGreffe } from '../../../../../quotation/model/guichet-unique/referentials/MotifRejetGreffe';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-motif-rejet-greffe',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectMotifRejetGreffeComponent extends GenericSelectComponent<MotifRejetGreffe> implements OnInit {

  types: MotifRejetGreffe[] = [] as Array<MotifRejetGreffe>;

  constructor(private formBuild: UntypedFormBuilder, private MotifRejetGreffeService: MotifRejetGreffeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.MotifRejetGreffeService.getMotifRejetGreffe().subscribe(response => {
      this.types = response;
    })
  }
}
