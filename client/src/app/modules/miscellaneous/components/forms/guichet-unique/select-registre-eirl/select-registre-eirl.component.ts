import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { RegistreEirlService } from 'src/app/modules/miscellaneous/services/guichet-unique/registre.eirl.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { RegistreEirl } from '../../../../../quotation/model/guichet-unique/referentials/RegistreEirl';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-registre-eirl',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectRegistreEirlComponent extends GenericSelectComponent<RegistreEirl> implements OnInit {

  types: RegistreEirl[] = [] as Array<RegistreEirl>;

  constructor(private formBuild: UntypedFormBuilder, private RegistreEirlService: RegistreEirlService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.RegistreEirlService.getRegistreEirl().subscribe(response => {
      this.types = response;
    })
  }
}
