import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { FormeSocialeService } from 'src/app/modules/miscellaneous/services/guichet-unique/forme.sociale.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { FormeSociale } from '../../../../../quotation/model/guichet-unique/referentials/FormeSociale';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-forme-sociale',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectFormeSocialeComponent extends GenericSelectComponent<FormeSociale> implements OnInit {

  types: FormeSociale[] = [] as Array<FormeSociale>;

  constructor(private formBuild: UntypedFormBuilder, private FormeSocialeService: FormeSocialeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.FormeSocialeService.getFormeSociale().subscribe(response => {
      this.types = response;
    })
  }
}
