import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { CodifNormeService } from 'src/app/modules/miscellaneous/services/guichet-unique/codif.norme.service';
import { CodifNorme } from '../../../../../quotation/model/guichet-unique/referentials/CodifNorme';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'autocomplete-codif-norme',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteCodifNormeComponent extends GenericLocalAutocompleteComponent<CodifNorme> implements OnInit {

  types: CodifNorme[] = [] as Array<CodifNorme>;

  constructor(private formBuild: UntypedFormBuilder, private CodifNormeService: CodifNormeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  filterEntities(types: CodifNorme[], value: string): CodifNorme[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(item =>
      item && item.label && item.code
      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));
  }

  initTypes(): void {
    this.CodifNormeService.getCodifNorme().subscribe(response => this.types = response);
  }

}
