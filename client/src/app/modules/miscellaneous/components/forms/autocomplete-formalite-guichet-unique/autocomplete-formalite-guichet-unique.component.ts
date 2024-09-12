


import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { GUICHET_UNIQUE_BASE_URL } from 'src/app/libs/Constants';
import { FormaliteGuichetUnique } from 'src/app/modules/quotation/model/guichet-unique/FormaliteGuichetUnique';
import { AppService } from 'src/app/services/app.service';
import { FormaliteInfogreffeService } from '../../../services/formalite.infogreffe.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';
import { FormaliteGuichetUniqueService } from '../../../services/formalite.guichet.unique.service';

@Component({
  selector: 'autocomplete-guichet-unique-formalite',
  templateUrl: './autocomplete-formalite-guichet-unique.component.html',
  styleUrls: ['./autocomplete-formalite-guichet-unique.component.css']
})
export class AutocompleteGuichetUniqueFormaliteComponent extends GenericAutocompleteComponent<FormaliteGuichetUnique, FormaliteGuichetUnique> implements OnInit {
  /**
 * Label to display.
 */
  @Input() label: string = "Label";

  constructor(private formBuild: UntypedFormBuilder,
    private formaliteGuichetUniqueService: FormaliteGuichetUniqueService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  searchEntities(value: string): Observable<FormaliteGuichetUnique[]> {
    return this.formaliteGuichetUniqueService.getFormaliteGuichetUniqueServiceByReference(value);
  }

  mapResponse(response: FormaliteGuichetUnique[]): FormaliteGuichetUnique[] {
    return response;
  }

  displayLabel(object: FormaliteGuichetUnique): string {
    if (object && object.referenceMandataire)
      return object.referenceMandataire;
    return "";
  }

  openGuichetUnique(event: any) {
    window.open(GUICHET_UNIQUE_BASE_URL + this.model.id, "_blank");
    return;
  }
}
