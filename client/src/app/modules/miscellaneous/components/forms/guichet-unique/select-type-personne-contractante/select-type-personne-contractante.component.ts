import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypePersonneContractanteService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.personne.contractante.service';
import { TypePersonneContractante } from '../../../../../quotation/model/guichet-unique/referentials/TypePersonneContractante';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-type-personne-contractante',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectTypePersonneContractanteComponent extends GenericSelectComponent<TypePersonneContractante> implements OnInit {

  types: TypePersonneContractante[] = [] as Array<TypePersonneContractante>;

  constructor(private formBuild: UntypedFormBuilder, private TypePersonneContractanteService: TypePersonneContractanteService,) {
    super(formBuild,)
  }

  initTypes(): void {
    this.TypePersonneContractanteService.getTypePersonneContractante().subscribe(response => {
      this.types = response;
    })
  }
}
