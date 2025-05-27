import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { Civility } from '../../../../profile/model/Civility';
import { CivilityService } from '../../../../quotation/services/civility.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-civility',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SelectCivilityComponent extends GenericSelectComponent<Civility> implements OnInit {

  @Input() types: Civility[] = [] as Array<Civility>;

  constructor(private formBuild: UntypedFormBuilder,
    private civilityService: CivilityService) {
    super(formBuild)
  }

  initTypes(): void {
    this.civilityService.getCivilities().subscribe(response => {
      this.types = response;
    })
  }
}
