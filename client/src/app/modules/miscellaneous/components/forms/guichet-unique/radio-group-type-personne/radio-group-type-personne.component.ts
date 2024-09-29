import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypePersonneService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.personne.service';
import { TypePersonne } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypePersonne';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-type-personne',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupTypePersonneComponent extends GenericRadioGroupComponent<TypePersonne> implements OnInit {
  types: TypePersonne[] = [] as Array<TypePersonne>;

  constructor(
    private formBuild: UntypedFormBuilder, private TypePersonneService: TypePersonneService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.TypePersonneService.getTypePersonne().subscribe(response => { this.types = response })
  }
}
