import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { TiersFollowupType } from 'src/app/modules/tiers/model/TiersFollowupType';
import { TiersFollowupTypeService } from 'src/app/modules/tiers/services/tiers.followup.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';


@Component({
  selector: 'select-followup',
  templateUrl: './select-followup.component.html',
  styleUrls: ['./select-followup.component.css']
})
export class SelectFollowupComponent extends GenericSelectComponent<TiersFollowupType> implements OnInit {

  types: TiersFollowupType[] = [] as Array<TiersFollowupType>;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: FormBuilder, private tiersFollowupTypeService: TiersFollowupTypeService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.tiersFollowupTypeService.getTiersFollowupTypes().subscribe(response => {
      this.types = response;
    })
  }
}
