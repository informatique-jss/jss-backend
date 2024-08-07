import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { NatureGeranceService } from 'src/app/modules/miscellaneous/services/guichet-unique/nature.gerance.service';
import { NatureGerance } from '../../../../../quotation/model/guichet-unique/referentials/NatureGerance';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-nature-gerance',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectNatureGeranceComponent extends GenericSelectComponent<NatureGerance> implements OnInit {

  types: NatureGerance[] = [] as Array<NatureGerance>;

  constructor(private formBuild: UntypedFormBuilder, private NatureGeranceService: NatureGeranceService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.NatureGeranceService.getNatureGerance().subscribe(response => {
      this.types = response;
    })
  }
}
