import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { OrganismeAssuranceMaladieActueService } from 'src/app/modules/miscellaneous/services/guichet-unique/organisme.assurance.maladie.actue.service';
import { OrganismeAssuranceMaladieActue } from '../../../../../quotation/model/guichet-unique/referentials/OrganismeAssuranceMaladieActue';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-organisme-assurance-maladie-actue',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectOrganismeAssuranceMaladieActueComponent extends GenericSelectComponent<OrganismeAssuranceMaladieActue> implements OnInit {

  types: OrganismeAssuranceMaladieActue[] = [] as Array<OrganismeAssuranceMaladieActue>;

  constructor(private formBuild: UntypedFormBuilder, private OrganismeAssuranceMaladieActueService: OrganismeAssuranceMaladieActueService,) {
    super(formBuild,)
  }

  initTypes(): void {
    this.OrganismeAssuranceMaladieActueService.getOrganismeAssuranceMaladieActue().subscribe(response => {
      this.types = response;
    })
  }
}
