


import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { FormaliteInfogreffe } from 'src/app/modules/quotation/model/infogreffe/FormaliteInfogreffe';
import { AppService } from 'src/app/services/app.service';
import { FormaliteInfogreffeService } from '../../../services/formalite.infogreffe.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-infogreffe-formalite',
  templateUrl: './autocomplete-formalite-infogreffe.component.html',
  styleUrls: ['./autocomplete-formalite-infogreffe.component.css']
})
export class AutocompleteInfogreffeFormaliteComponent extends GenericAutocompleteComponent<FormaliteInfogreffe, FormaliteInfogreffe> implements OnInit {
  /**
 * Label to display.
 */
  @Input() label: string = "Label";

  constructor(private formBuild: UntypedFormBuilder,
    private formaliteInfogreffeService: FormaliteInfogreffeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  searchEntities(value: string): Observable<FormaliteInfogreffe[]> {
    return this.formaliteInfogreffeService.getFormaliteInfogreffeServiceByReference(value);
  }

  mapResponse(response: FormaliteInfogreffe[]): FormaliteInfogreffe[] {
    return response;
  }

  displayLabel(object: FormaliteInfogreffe): string {
    return (object && object.entreprise && object.greffeDestinataire ?
      object.referenceTechnique + " - " + object.greffeDestinataire.typeTribunalReel + " - " + object.greffeDestinataire.nom + " - " + object.entreprise.denomination + " - " + object.entreprise.siren : (object && object.greffeDestinataire ?
        object.referenceTechnique + " - " + object.greffeDestinataire.typeTribunalReel + " - " + object.greffeDestinataire.nom : (object && object.entreprise ?
          object.referenceTechnique + " - " + object.entreprise.denomination + " - " + object.entreprise.siren : (object ?
            object.referenceTechnique : ""))));
  }
}
