import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypeLocataireGerantMandataireService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.locataire.gerant.mandataire.service';
import { TypeLocataireGerantMandataire } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeLocataireGerantMandataire';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-type-locataire-gerant-mandataire',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupTypeLocataireGerantMandataireComponent extends GenericRadioGroupComponent<TypeLocataireGerantMandataire> implements OnInit {
  types: TypeLocataireGerantMandataire[] = [] as Array<TypeLocataireGerantMandataire>;

  constructor(
    private formBuild: UntypedFormBuilder, private TypeLocataireGerantMandataireService: TypeLocataireGerantMandataireService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.TypeLocataireGerantMandataireService.getTypeLocataireGerantMandataire().subscribe(response => { this.types = response })
  }
}
