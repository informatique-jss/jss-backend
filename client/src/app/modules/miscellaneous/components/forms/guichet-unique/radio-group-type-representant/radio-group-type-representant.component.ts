import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypeRepresentantService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.representant.service';
import { TypeRepresentant } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeRepresentant';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-type-representant',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupTypeRepresentantComponent extends GenericRadioGroupComponent<TypeRepresentant> implements OnInit {
  types: TypeRepresentant[] = [] as Array<TypeRepresentant>;

  constructor(
    private formBuild: UntypedFormBuilder, private TypeRepresentantService: TypeRepresentantService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.TypeRepresentantService.getTypeRepresentant().subscribe(response => { this.types = response })
  }
}
