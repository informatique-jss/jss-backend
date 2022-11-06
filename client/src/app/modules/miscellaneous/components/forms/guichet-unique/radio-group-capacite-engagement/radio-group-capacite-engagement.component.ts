import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { CapaciteEngagementService } from 'src/app/modules/miscellaneous/services/guichet-unique/capacite.engagement.service';
import { CapaciteEngagement } from 'src/app/modules/quotation/model/guichet-unique/referentials/CapaciteEngagement';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-capacite-engagement',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupCapaciteEngagementComponent extends GenericRadioGroupComponent<CapaciteEngagement> implements OnInit {
  types: CapaciteEngagement[] = [] as Array<CapaciteEngagement>;

  constructor(
    private formBuild: UntypedFormBuilder, private CapaciteEngagementService: CapaciteEngagementService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.CapaciteEngagementService.getCapaciteEngagement().subscribe(response => { this.types = response })
  }
}
