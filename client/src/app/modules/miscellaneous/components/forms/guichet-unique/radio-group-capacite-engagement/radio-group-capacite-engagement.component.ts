import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { CapaciteEngagementService } from 'src/app/modules/miscellaneous/services/guichet-unique/capacite.engagement.service';
import { CapaciteEngagement } from 'src/app/modules/quotation/model/guichet-unique/referentials/CapaciteEngagement';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-capacite-engagement',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupCapaciteEngagementComponent extends GenericRadioGroupComponent<CapaciteEngagement> implements OnInit {
  types: CapaciteEngagement[] = [] as Array<CapaciteEngagement>;

  constructor(
    private formBuild: UntypedFormBuilder, private CapaciteEngagementService: CapaciteEngagementService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.CapaciteEngagementService.getCapaciteEngagement().subscribe(response => { this.types = response })
  }
}
