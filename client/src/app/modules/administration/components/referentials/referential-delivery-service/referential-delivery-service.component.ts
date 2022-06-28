import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { DeliveryService } from 'src/app/modules/miscellaneous/model/DeliveryService';
import { DeliveryServiceService } from 'src/app/modules/miscellaneous/services/delivery.service.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-delivery-service',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialDeliveryServiceComponent extends GenericReferentialComponent<DeliveryService> implements OnInit {
  constructor(private deliveryServiceService: DeliveryServiceService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<DeliveryService> {
    return this.deliveryServiceService.addOrUpdateDeliveryService(this.selectedEntity!);
  }
  getGetObservable(): Observable<DeliveryService[]> {
    return this.deliveryServiceService.getDeliveryServices();
  }
}
