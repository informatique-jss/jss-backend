import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { DestinationService } from 'src/app/modules/miscellaneous/services/guichet-unique/destination.service';
import { Destination } from '../../../../../quotation/model/guichet-unique/referentials/Destination';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-destination',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectDestinationComponent extends GenericSelectComponent<Destination> implements OnInit {

  types: Destination[] = [] as Array<Destination>;

  constructor(private formBuild: UntypedFormBuilder, private DestinationService: DestinationService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.DestinationService.getDestination().subscribe(response => {
      this.types = response;
    })
  }
}
