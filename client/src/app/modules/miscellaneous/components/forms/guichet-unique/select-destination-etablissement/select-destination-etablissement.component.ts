import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { DestinationEtablissementService } from 'src/app/modules/miscellaneous/services/guichet-unique/destination.etablissement.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { DestinationEtablissement } from '../../../../../quotation/model/guichet-unique/referentials/DestinationEtablissement';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-destination-etablissement',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectDestinationEtablissementComponent extends GenericSelectComponent<DestinationEtablissement> implements OnInit {

  types: DestinationEtablissement[] = [] as Array<DestinationEtablissement>;

  constructor(private formBuild: UntypedFormBuilder, private DestinationEtablissementService: DestinationEtablissementService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.DestinationEtablissementService.getDestinationEtablissement().subscribe(response => {
      this.types = response;
    })
  }
}
