import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Provision } from 'src/app/modules/quotation/model/Provision';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-provision',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectProvisionComponent extends GenericSelectComponent<Provision> implements OnInit {

  @Input() types: Provision[] = [] as Array<Provision>;

  constructor(private formBuild: UntypedFormBuilder,
    private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
  }

  displayLabel(provision: Provision) {
    return provision.provisionFamilyType.label + " - " + provision.provisionType.label
  }
}
