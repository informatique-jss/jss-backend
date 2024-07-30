import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { DestinationLocationGeranceMandService } from 'src/app/modules/miscellaneous/services/guichet-unique/destination.location.gerance.mand.service';
import { DestinationLocationGeranceMand } from 'src/app/modules/quotation/model/guichet-unique/referentials/DestinationLocationGeranceMand';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-destination-location-gerance-mand',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupDestinationLocationGeranceMandComponent extends GenericRadioGroupComponent<DestinationLocationGeranceMand> implements OnInit {
  types: DestinationLocationGeranceMand[] = [] as Array<DestinationLocationGeranceMand>;

  constructor(
    private formBuild: UntypedFormBuilder, private DestinationLocationGeranceMandService: DestinationLocationGeranceMandService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.DestinationLocationGeranceMandService.getDestinationLocationGeranceMand().subscribe(response => { this.types = response })
  }
}
