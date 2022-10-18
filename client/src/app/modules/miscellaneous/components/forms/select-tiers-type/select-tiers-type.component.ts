import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TiersType } from 'src/app/modules/tiers/model/TiersType';
import { TiersTypeService } from 'src/app/modules/tiers/services/tiers.type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-tiers-type',
  templateUrl: './select-tiers-type.component.html',
  styleUrls: ['./select-tiers-type.component.css']
})
export class SelectTiersTypeComponent extends GenericSelectComponent<TiersType> implements OnInit {

  types: TiersType[] = [] as Array<TiersType>;

  constructor(private formBuild: UntypedFormBuilder, private tiersTypeService: TiersTypeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.tiersTypeService.getTiersTypes().subscribe(response => {
      this.types = response;
    })
  }
}
