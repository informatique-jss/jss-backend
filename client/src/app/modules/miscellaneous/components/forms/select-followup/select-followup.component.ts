import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TiersFollowupType } from '../../../model/TiersFollowupType';
import { TiersFollowupTypeService } from '../../../services/tiers.followup.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';


@Component({
  selector: 'select-followup',
  templateUrl: './select-followup.component.html',
  styleUrls: ['./select-followup.component.css']
})
export class SelectFollowupComponent extends GenericSelectComponent<TiersFollowupType> implements OnInit {

  types: TiersFollowupType[] = [] as Array<TiersFollowupType>;

  constructor(private formBuild: UntypedFormBuilder, private tiersFollowupTypeService: TiersFollowupTypeService,) {
    super(formBuild)
  }

  initTypes(): void {
    this.tiersFollowupTypeService.getTiersFollowupTypes().subscribe(response => {
      this.types = response;
    })
  }
}
