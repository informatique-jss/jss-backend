import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MineurLienParenteService } from 'src/app/modules/miscellaneous/services/guichet-unique/mineur.lien.parente.service';
import { MineurLienParente } from '../../../../../quotation/model/guichet-unique/referentials/MineurLienParente';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-mineur-lien-parente',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectMineurLienParenteComponent extends GenericSelectComponent<MineurLienParente> implements OnInit {

  types: MineurLienParente[] = [] as Array<MineurLienParente>;

  constructor(private formBuild: UntypedFormBuilder, private MineurLienParenteService: MineurLienParenteService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.MineurLienParenteService.getMineurLienParente().subscribe(response => {
      this.types = response;
    })
  }
}
