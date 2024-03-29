import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { DestinationService } from 'src/app/modules/miscellaneous/services/guichet-unique/destination.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { Destination } from '../../../../../quotation/model/guichet-unique/referentials/Destination';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-destination',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectDestinationComponent extends GenericSelectComponent<Destination> implements OnInit {

  types: Destination[] = [] as Array<Destination>;

  constructor(private formBuild: UntypedFormBuilder, private DestinationService: DestinationService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.DestinationService.getDestination().subscribe(response => {
      this.types = response;
    })
  }
}
