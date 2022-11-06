import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { NatureGeranceService } from 'src/app/modules/miscellaneous/services/guichet-unique/nature.gerance.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { NatureGerance } from '../../../../../quotation/model/guichet-unique/referentials/NatureGerance';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-nature-gerance',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectNatureGeranceComponent extends GenericSelectComponent<NatureGerance> implements OnInit {

  types: NatureGerance[] = [] as Array<NatureGerance>;

  constructor(private formBuild: UntypedFormBuilder, private NatureGeranceService: NatureGeranceService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.NatureGeranceService.getNatureGerance().subscribe(response => {
      this.types = response;
    })
  }
}
