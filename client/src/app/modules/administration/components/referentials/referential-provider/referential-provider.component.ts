import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Provider } from 'src/app/modules/miscellaneous/model/Provider';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
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
    private constantService: ConstantService,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  paymentTypePrelevement = this.constantService.getPaymentTypePrelevement();
  paymentTypeVirement = this.constantService.getPaymentTypeVirement();

  getAddOrUpdateObservable(): Observable<Provider> {
    return this.providerService.addOrUpdateProvider(this.selectedEntity!);
  }
  getGetObservable(): Observable<Provider[]> {
    return this.providerService.getProviders();
  }
}
