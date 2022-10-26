import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AffaireStatus } from 'src/app/modules/quotation/model/AffaireStatus';
import { AffaireStatusService } from 'src/app/modules/quotation/services/affaire.status.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-affaire-status',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialAffaireStatusComponent extends GenericReferentialComponent<AffaireStatus> implements OnInit {
  constructor(private affaireStatusService: AffaireStatusService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<AffaireStatus> {
    return this.affaireStatusService.addOrUpdateAffaireStatus(this.selectedEntity!);
  }
  getGetObservable(): Observable<AffaireStatus[]> {
    return this.affaireStatusService.getAffaireStatus();
  }
}
