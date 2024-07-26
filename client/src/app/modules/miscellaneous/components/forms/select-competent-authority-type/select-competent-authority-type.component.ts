import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { CompetentAuthorityType } from '../../../model/CompetentAuthorityType';
import { CompetentAuthorityTypeService } from '../../../services/competent.authority.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-competent-authority-type',
  templateUrl: './select-competent-authority-type.component.html',
  styleUrls: ['./select-competent-authority-type.component.css']
})
export class SelectCompetentAuthorityTypeComponent extends GenericSelectComponent<CompetentAuthorityType> implements OnInit {

  types: CompetentAuthorityType[] = [] as Array<CompetentAuthorityType>;

  constructor(private formBuild: UntypedFormBuilder, private competentAuthorityTypeService: CompetentAuthorityTypeService,) {
    super(formBuild)
  }

  initTypes(): void {
    this.competentAuthorityTypeService.getCompetentAuthorityTypes().subscribe(response => {
      this.types = response;
    })
  }
}
