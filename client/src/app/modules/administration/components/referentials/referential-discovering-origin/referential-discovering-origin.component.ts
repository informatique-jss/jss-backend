import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { DiscoveringOrigin } from 'src/app/modules/tiers/model/DiscoveringOrigin';
import { DiscoveringOriginService } from 'src/app/modules/tiers/services/discovering.origin.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-discovering-origin',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialDiscoveringOriginComponent extends GenericReferentialComponent<DiscoveringOrigin> implements OnInit {
  constructor(private discoveringOriginService: DiscoveringOriginService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<DiscoveringOrigin> {
    return this.discoveringOriginService.addOrUpdateDiscoveringOrigin(this.selectedEntity!);
  }
  getGetObservable(): Observable<DiscoveringOrigin[]> {
    return this.discoveringOriginService.getDiscoveringOrigins();
  }
}
