import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { PrecisionActiviteService } from 'src/app/modules/miscellaneous/services/guichet-unique/precision.activite.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { PrecisionActivite } from '../../../../../quotation/model/guichet-unique/referentials/PrecisionActivite';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-precision-activite',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectPrecisionActiviteComponent extends GenericSelectComponent<PrecisionActivite> implements OnInit {

  types: PrecisionActivite[] = [] as Array<PrecisionActivite>;

  constructor(private formBuild: UntypedFormBuilder, private PrecisionActiviteService: PrecisionActiviteService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.PrecisionActiviteService.getPrecisionActivite().subscribe(response => {
      this.types = response;
    })
  }
}
