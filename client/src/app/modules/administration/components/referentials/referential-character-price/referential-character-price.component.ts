import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { formatDate } from 'src/app/libs/FormatHelper';
import { CharacterPrice } from 'src/app/modules/quotation/model/CharacterPrice';
import { CharacterPriceService } from 'src/app/modules/quotation/services/character.price.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-character-price',
  templateUrl: 'referential-character-price.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialCharacterPriceComponent extends GenericReferentialComponent<CharacterPrice> implements OnInit {

  constructor(private characterPriceService: CharacterPriceService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<CharacterPrice> {
    return this.characterPriceService.addOrUpdateCharacterPrice(this.selectedEntity!);
  }

  getGetObservable(): Observable<CharacterPrice[]> {
    return this.characterPriceService.getCharacterPrices();
  }

  getElementCode(element: CharacterPrice) {
    return element.departments.map(departement => departement.id).join(", ");
  }

  getElementLabel(element: CharacterPrice) {
    return formatDate(element.startDate);
  }

  mapEntities() {
    if (this.entities) {

      for (let entity of this.entities) {
        entity.startDate = new Date(entity.startDate);
      }
    }
  }
}
