import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { PerimetreService } from 'src/app/modules/miscellaneous/services/guichet-unique/perimetre.service';
import { Perimetre } from 'src/app/modules/quotation/model/guichet-unique/referentials/Perimetre';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-perimetre',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupPerimetreComponent extends GenericRadioGroupComponent<Perimetre> implements OnInit {
  types: Perimetre[] = [] as Array<Perimetre>;

  constructor(
    private formBuild: UntypedFormBuilder, private PerimetreService: PerimetreService,) {
    super(formBuild,);
  }

  initTypes(): void {
    this.PerimetreService.getPerimetre().subscribe(response => { this.types = response })
  }
}
