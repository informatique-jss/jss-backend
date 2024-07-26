import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TotalitePartieService } from 'src/app/modules/miscellaneous/services/guichet-unique/totalite.partie.service';
import { TotalitePartie } from 'src/app/modules/quotation/model/guichet-unique/referentials/TotalitePartie';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-totalite-partie',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupTotalitePartieComponent extends GenericRadioGroupComponent<TotalitePartie> implements OnInit {
  types: TotalitePartie[] = [] as Array<TotalitePartie>;

  constructor(
    private formBuild: UntypedFormBuilder, private TotalitePartieService: TotalitePartieService,) {
    super(formBuild,);
  }

  initTypes(): void {
    this.TotalitePartieService.getTotalitePartie().subscribe(response => { this.types = response })
  }
}
