import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
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

  constructor(private formBuild: UntypedFormBuilder, private deliveryServiceService: DeliveryServiceService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.deliveryServiceService.getDeliveryServices().subscribe(response => {
      this.types = response;
    })
  }
}
