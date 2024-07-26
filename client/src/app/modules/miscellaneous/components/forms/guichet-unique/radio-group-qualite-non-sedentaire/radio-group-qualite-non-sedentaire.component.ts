import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { QualiteNonSedentaireService } from 'src/app/modules/miscellaneous/services/guichet-unique/qualite.non.sedentaire.service';
import { QualiteNonSedentaire } from 'src/app/modules/quotation/model/guichet-unique/referentials/QualiteNonSedentaire';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-qualite-non-sedentaire',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupQualiteNonSedentaireComponent extends GenericRadioGroupComponent<QualiteNonSedentaire> implements OnInit {
  types: QualiteNonSedentaire[] = [] as Array<QualiteNonSedentaire>;

  constructor(
    private formBuild: UntypedFormBuilder, private QualiteNonSedentaireService: QualiteNonSedentaireService,) {
    super(formBuild,);
  }

  initTypes(): void {
    this.QualiteNonSedentaireService.getQualiteNonSedentaire().subscribe(response => { this.types = response })
  }
}
