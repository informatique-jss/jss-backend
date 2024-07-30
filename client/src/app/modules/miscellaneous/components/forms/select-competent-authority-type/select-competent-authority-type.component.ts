import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { CompetentAuthorityType } from '../../../model/CompetentAuthorityType';
import { CompetentAuthorityTypeService } from '../../../services/competent.authority.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-competent-authority-type',
  templateUrl: './select-competent-authority-type.component.html',
  styleUrls: ['./select-competent-authority-type.component.css']
})
export class SelectCompetentAuthorityTypeComponent extends GenericSelectComponent<CompetentAuthorityType> implements OnInit {

  types: CompetentAuthorityType[] = [] as Array<CompetentAuthorityType>;
  isDisplayPreviewShortcut: boolean = true;
  constructor(private formBuild: UntypedFormBuilder, private competentAuthorityTypeService: CompetentAuthorityTypeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.competentAuthorityTypeService.getCompetentAuthorityTypes().subscribe(response => {
      this.types = response;
    })
  }

}
