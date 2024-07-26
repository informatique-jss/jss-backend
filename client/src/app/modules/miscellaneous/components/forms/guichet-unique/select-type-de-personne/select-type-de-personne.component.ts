import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypeDePersonneService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.de.personne.service';
import { TypeDePersonne } from '../../../../../quotation/model/guichet-unique/referentials/TypeDePersonne';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-type-de-personne',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectTypeDePersonneComponent extends GenericSelectComponent<TypeDePersonne> implements OnInit {

  types: TypeDePersonne[] = [] as Array<TypeDePersonne>;

  constructor(private formBuild: UntypedFormBuilder, private TypeDePersonneService: TypeDePersonneService,) {
    super(formBuild,)
  }

  initTypes(): void {
    this.TypeDePersonneService.getTypeDePersonne().subscribe(response => {
      this.types = response;
    })
  }
}
