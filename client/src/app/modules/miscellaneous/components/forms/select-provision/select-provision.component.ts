import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Provision } from 'src/app/modules/quotation/model/Provision';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-provision',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectProvisionComponent extends GenericSelectComponent<Provision> implements OnInit {

  @Input() types: Provision[] = [] as Array<Provision>;

  constructor(private formBuild: UntypedFormBuilder, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
  }

  displayLabel(provision: Provision) {
    return provision.provisionFamilyType.label + " - " + provision.provisionType.label
  }

  getPreviewActionLinkFunction(entity: Provision): string[] | undefined {
    return ['provision/', + entity.service.assoAffaireOrder.affaire.id + "/" + entity.id + ""];
  }
}
