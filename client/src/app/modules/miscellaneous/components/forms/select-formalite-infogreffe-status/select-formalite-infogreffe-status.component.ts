import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Dictionnary } from 'src/app/libs/Dictionnary';
import { AppService } from 'src/app/services/app.service';
import { ENVOI_EN_COURS, ENVOYE_AU_GRF, RECU_PAR_LE_GRF, REJETE_DEF_GRF, REJETE_GRF, REPRISE, SAUVEGARDE, VALIDE } from '../../../../../libs/Constants';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-formalite-infogreffe-status',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})

export class SelectFormaliteInfogreffeStatusComponent extends GenericSelectComponent<string> implements OnInit {
  types: string[] = [] as Array<string>;

  constructor(private formBuild: UntypedFormBuilder, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.types = [];
    this.types.push(REJETE_DEF_GRF);
    this.types.push(REJETE_GRF);
    this.types.push(RECU_PAR_LE_GRF);
    this.types.push(REPRISE);
    this.types.push(SAUVEGARDE);
    this.types.push(ENVOYE_AU_GRF);
    this.types.push(ENVOI_EN_COURS);
    this.types.push(VALIDE);
    this.types = this.types.sort((a, b) => a.localeCompare(b));
  }

  compareWithId = this.compareWithLabel;

  compareWithLabel(o1: any, o2: any): boolean {
    if (o1 == null && o2 != null || o1 != null && o2 == null)
      return false;
    if (o1 && o2)
      return o1 == o2;
    return false
  }

  displayLabel(object: string): string {
    if (!object)
      return "";

    let dictionnary = Dictionnary as any;
    if (dictionnary[object])
      return dictionnary[object];

    return "";
  }
}
