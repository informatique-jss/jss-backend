import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BodaccPublicationType } from 'src/app/modules/quotation/model/BodaccPublicationType';
import { BodaccPublicationTypeService } from 'src/app/modules/quotation/services/bodacc-publication-type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';


@Component({
  selector: 'select-bodacc-publication-type',
  templateUrl: './select-bodacc-publication-type.component.html',
  styleUrls: ['./select-bodacc-publication-type.component.css']
})
export class SelectBodaccPublicationTypeComponent extends GenericSelectComponent<BodaccPublicationType> implements OnInit {

  types: BodaccPublicationType[] = [] as Array<BodaccPublicationType>;

  constructor(private formBuild: UntypedFormBuilder, private bodaccPublicationTypeService: BodaccPublicationTypeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.bodaccPublicationTypeService.getBodaccPublicationTypes().subscribe(response => {
      this.types = response;
      this.types.sort((a, b) => a.code.localeCompare(b.code));
    })
  }
}
