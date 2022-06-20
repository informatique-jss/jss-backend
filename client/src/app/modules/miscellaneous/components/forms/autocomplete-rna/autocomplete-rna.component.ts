import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Rna } from 'src/app/modules/quotation/model/Rna';
import { RnaService } from 'src/app/modules/quotation/services/rna.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-rna',
  templateUrl: './autocomplete-rna.component.html',
  styleUrls: ['./autocomplete-rna.component.css']
})
export class AutocompleteRnaComponent extends GenericAutocompleteComponent<Rna, Rna> implements OnInit {

  constructor(private formBuild: FormBuilder,
    private rnaService: RnaService, private changeDetectorRef: ChangeDetectorRef) {
    super(formBuild, changeDetectorRef)
  }

  searchEntities(value: string): Observable<Rna[]> {
    this.expectedMinLengthInput = 10;
    return this.rnaService.getRna(value);
  }

  displayRna(rna: Rna): string {
    if (!rna)
      return "";
    if (!rna.association)
      return rna + "";
    return rna.association.id_association;
  }
}
