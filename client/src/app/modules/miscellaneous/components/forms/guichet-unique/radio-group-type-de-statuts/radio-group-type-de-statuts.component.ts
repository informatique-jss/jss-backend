import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypeDeStatutsService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.de.statuts.service';
import { TypeDeStatuts } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeDeStatuts';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-type-de-statuts',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupTypeDeStatutsComponent extends GenericRadioGroupComponent<TypeDeStatuts> implements OnInit {
  types: TypeDeStatuts[] = [] as Array<TypeDeStatuts>;

  constructor(
    private formBuild: UntypedFormBuilder, private TypeDeStatutsService: TypeDeStatutsService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.TypeDeStatutsService.getTypeDeStatuts().subscribe(response => { this.types = response })
  }
}
