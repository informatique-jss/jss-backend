import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ModeExerciceService } from 'src/app/modules/miscellaneous/services/guichet-unique/mode.exercice.service';
import { ModeExercice } from '../../../../../quotation/model/guichet-unique/referentials/ModeExercice';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-mode-exercice',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteModeExerciceComponent extends GenericLocalAutocompleteComponent<ModeExercice> implements OnInit {

  types: ModeExercice[] = [] as Array<ModeExercice>;

  constructor(private formBuild: UntypedFormBuilder, private ModeExerciceService: ModeExerciceService,) {
    super(formBuild,)
  }

  filterEntities(types: ModeExercice[], value: string): ModeExercice[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(item =>
      item && item.label && item.code
      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));
  }

  initTypes(): void {
    this.ModeExerciceService.getModeExercice().subscribe(response => this.types = response);
  }

}
