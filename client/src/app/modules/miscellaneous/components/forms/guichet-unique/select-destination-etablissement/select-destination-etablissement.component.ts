import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { DestinationEtablissementService } from 'src/app/modules/miscellaneous/services/guichet-unique/destination.etablissement.service';
import { DestinationEtablissement } from '../../../../../quotation/model/guichet-unique/referentials/DestinationEtablissement';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-destination-etablissement',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectDestinationEtablissementComponent extends GenericSelectComponent<DestinationEtablissement> implements OnInit {

  types: DestinationEtablissement[] = [] as Array<DestinationEtablissement>;

  constructor(private formBuild: UntypedFormBuilder, private DestinationEtablissementService: DestinationEtablissementService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.DestinationEtablissementService.getDestinationEtablissement().subscribe(response => {
      this.types = response;
    })
  }
}
