import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { RejectionCause } from 'src/app/modules/quotation/model/RejectionCause';
import { RejectionCauseService } from 'src/app/modules/quotation/services/rejection.cause.service';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from '../../../../../services/habilitations.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-rejection-cause',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectRejectionCauseComponent extends GenericSelectComponent<RejectionCause> implements OnInit {

  types: RejectionCause[] = [] as Array<RejectionCause>;
  displayNonJssCauses: boolean = false;

  constructor(private formBuild: UntypedFormBuilder, private rejectionCauseService: RejectionCauseService, private appService3: AppService,
    private habilitationsService: HabilitationsService
  ) {
    super(formBuild, appService3)
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.displayNonJssCauses = this.habilitationsService.canDisplayNonJssRejectionCause();
  }

  initTypes(): void {
    this.rejectionCauseService.getRejectionCauses().subscribe(response => {
      this.types = response.filter(t => this.displayNonJssCauses || t.label.indexOf("JSS") >= 0);
      this.types = this.types.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

  displayLabel(object: RejectionCause): string {
    if (object)
      return object.label;
    return "";
  }

}
