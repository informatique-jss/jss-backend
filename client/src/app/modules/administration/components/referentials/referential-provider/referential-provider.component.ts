import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { Provider } from 'src/app/modules/miscellaneous/model/Provider';
import { ProviderService } from 'src/app/modules/miscellaneous/services/provider.service';
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

  getAddOrUpdateObservable(): Observable<Provider> {
    return this.providerService.addOrUpdateProvider(this.selectedEntity!);
  }
  getGetObservable(): Observable<Provider[]> {
    return this.providerService.getProviders();
  }
}
