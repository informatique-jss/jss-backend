import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { InformationBanner } from 'src/app/modules/miscellaneous/model/InformationBanner';
import { InformationBannerService } from 'src/app/modules/miscellaneous/services/information.banner.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
    selector: 'referential-information-banner',
    templateUrl: './referential-information-banner.component.html',
    styleUrls: ['./../generic-referential/generic-referential.component.css'],
    standalone: false
})
export class ReferentialInformationBannerComponent extends GenericReferentialComponent<InformationBanner> implements OnInit {
  constructor(private informationBannerService: InformationBannerService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<InformationBanner> {
    return this.informationBannerService.addOrUpdateInformationBanner(this.selectedEntity!);
  }
  getGetObservable(): Observable<InformationBanner[]> {
    return this.informationBannerService.getInformationBanners();
  }
}
