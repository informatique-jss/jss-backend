import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { PAYMENT_TYPE_PRELEVEMENT, PAYMENT_TYPE_VIREMENT } from 'src/app/libs/Constants';
import { Provider } from 'src/app/modules/miscellaneous/model/Provider';
import { ProviderService } from 'src/app/modules/miscellaneous/services/provider.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-provider',
  templateUrl: './referential-provider.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialProviderComponent extends GenericReferentialComponent<Provider> implements OnInit {
  constructor(private providerService: ProviderService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  PAYMENT_TYPE_PRELEVEMENT = PAYMENT_TYPE_PRELEVEMENT;
  PAYMENT_TYPE_VIREMENT = PAYMENT_TYPE_VIREMENT;

  getAddOrUpdateObservable(): Observable<Provider> {
    return this.providerService.addOrUpdateProvider(this.selectedEntity!);
  }
  getGetObservable(): Observable<Provider[]> {
    return this.providerService.getProviders();
  }
}
