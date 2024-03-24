import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ServiceFamily } from 'src/app/modules/quotation/model/ServiceFamily';
import { ServiceFamilyService } from 'src/app/modules/quotation/services/service.family.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-service-family',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectServiceFamilyComponent extends GenericSelectComponent<ServiceFamily> implements OnInit {

  @Input() types: ServiceFamily[] = [] as Array<ServiceFamily>;

  constructor(private formBuild: UntypedFormBuilder,
    private serviceFamilyService: ServiceFamilyService,
    private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.serviceFamilyService.getServiceFamilies().subscribe(response => {
      this.types = response;
    })
  }
}
