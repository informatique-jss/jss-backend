import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { DestinationLocationGeranceMandService } from 'src/app/modules/miscellaneous/services/guichet-unique/destination.location.gerance.mand.service';
import { DestinationLocationGeranceMand } from 'src/app/modules/quotation/model/guichet-unique/referentials/DestinationLocationGeranceMand';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-destination-location-gerance-mand',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupDestinationLocationGeranceMandComponent extends GenericRadioGroupComponent<DestinationLocationGeranceMand> implements OnInit {
  types: DestinationLocationGeranceMand[] = [] as Array<DestinationLocationGeranceMand>;

  constructor(
    private formBuild: UntypedFormBuilder, private DestinationLocationGeranceMandService: DestinationLocationGeranceMandService,) {
    super(formBuild);
  }

  initTypes(): void {
    this.DestinationLocationGeranceMandService.getDestinationLocationGeranceMand().subscribe(response => { this.types = response })
  }
}
