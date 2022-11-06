import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { OptionEirlService } from 'src/app/modules/miscellaneous/services/guichet-unique/option.eirl.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { OptionEirl } from '../../../../../quotation/model/guichet-unique/referentials/OptionEirl';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-option-eirl',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectOptionEirlComponent extends GenericSelectComponent<OptionEirl> implements OnInit {

  types: OptionEirl[] = [] as Array<OptionEirl>;

  constructor(private formBuild: UntypedFormBuilder, private OptionEirlService: OptionEirlService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.OptionEirlService.getOptionEirl().subscribe(response => {
      this.types = response;
    })
  }
}
