import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { DeliveryService } from '../../../model/DeliveryService';
import { DeliveryServiceService } from '../../../services/delivery.service.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-delivery-service',
  templateUrl: './select-delivery-service.component.html',
  styleUrls: ['./select-delivery-service.component.css']
})
export class SelectDeliveryServiceComponent extends GenericSelectComponent<DeliveryService> implements OnInit {

  types: DeliveryService[] = [] as Array<DeliveryService>;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: FormBuilder, private deliveryServiceService: DeliveryServiceService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.deliveryServiceService.getDeliveryServices().subscribe(response => {
      this.types = response;
    })
  }
}
