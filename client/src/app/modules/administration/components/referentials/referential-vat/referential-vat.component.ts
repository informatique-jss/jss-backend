import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Vat } from 'src/app/modules/miscellaneous/model/Vat';
import { VatService } from 'src/app/modules/miscellaneous/services/vat.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-vat',
  templateUrl: 'referential-vat.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialVatComponent extends GenericReferentialComponent<Vat> implements OnInit {
  constructor(private vatService: VatService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<Vat> {
    return this.vatService.addOrUpdateVat(this.selectedEntity!);
  }
  getGetObservable(): Observable<Vat[]> {
    return this.vatService.getVats();
  }
}
