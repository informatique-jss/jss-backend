import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { Provider } from 'src/app/modules/miscellaneous/model/Provider';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { ProviderService } from 'src/app/modules/miscellaneous/services/provider.service';
import { PROVIDER_ENTITY_TYPE } from 'src/app/routing/search/search.component';
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
    private activatedRoute: ActivatedRoute,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  selectedProviderId: number | undefined;
  PROVIDER_ENTITY_TYPE = PROVIDER_ENTITY_TYPE;

  ngOnInit(): void {
    super.ngOnInit();
    let idProvider = this.activatedRoute.snapshot.params.id;
    if (idProvider)
      this.selectedProviderId = idProvider;
  }

  mapEntities(): void {
    if (this.selectedProviderId && this.entities)
      for (let confrere of this.entities)
        if (confrere.id == this.selectedProviderId)
          this.selectEntity(confrere);
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
