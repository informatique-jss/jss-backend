import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { QualiteDeNonSedentariteService } from 'src/app/modules/miscellaneous/services/guichet-unique/qualite.de.non.sedentarite.service';
import { QualiteDeNonSedentarite } from 'src/app/modules/quotation/model/guichet-unique/referentials/QualiteDeNonSedentarite';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';
import { A } from '@angular/cdk/keycodes';

@Component({
  selector: 'radio-group-qualite-de-non-sedentarite',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupQualiteDeNonSedentariteComponent extends GenericRadioGroupComponent<QualiteDeNonSedentarite> implements OnInit {
  types: QualiteDeNonSedentarite[] = [] as Array<QualiteDeNonSedentarite>;

  constructor(
    private formBuild: UntypedFormBuilder, private QualiteDeNonSedentariteService: QualiteDeNonSedentariteService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.QualiteDeNonSedentariteService.getQualiteDeNonSedentarite().subscribe(response => { this.types = response })
  }
}
