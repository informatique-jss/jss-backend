import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { COUNTRY_CODE_FRANCE } from 'src/app/libs/Constants';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { AffaireService } from 'src/app/modules/quotation/services/affaire.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-affaire',
  templateUrl: './referential-affaire.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialAffaireComponent extends GenericReferentialComponent<Affaire> implements OnInit {
  constructor(private affaireService: AffaireService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,
    private cityService: CityService) {
    super(formBuilder2, appService2);
  }

  COUNTRY_CODE_FRANCE = COUNTRY_CODE_FRANCE;

  getAddOrUpdateObservable(): Observable<Affaire> {
    return this.affaireService.addOrUpdateAffaire(this.selectedEntity!);
  }
  getGetObservable(): Observable<Affaire[]> {
    return this.affaireService.getAffaires();
  }

  getElementCode(element: Affaire): string {
    return element.id + "";
  }

  getElementLabel(element: Affaire): string {
    return element.denomination ? element.denomination : (element.firstname + " " + element.lastname);
  }
}
