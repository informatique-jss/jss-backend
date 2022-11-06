import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MotifRejetGreffeService } from 'src/app/modules/miscellaneous/services/guichet-unique/motif.rejet.greffe.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { MotifRejetGreffe } from '../../../../../quotation/model/guichet-unique/referentials/MotifRejetGreffe';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-motif-rejet-greffe',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectMotifRejetGreffeComponent extends GenericSelectComponent<MotifRejetGreffe> implements OnInit {

  types: MotifRejetGreffe[] = [] as Array<MotifRejetGreffe>;

  constructor(private formBuild: UntypedFormBuilder, private MotifRejetGreffeService: MotifRejetGreffeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.MotifRejetGreffeService.getMotifRejetGreffe().subscribe(response => {
      this.types = response;
    })
  }
}
