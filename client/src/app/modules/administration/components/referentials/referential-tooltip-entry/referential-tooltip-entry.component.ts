import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { TooltipEntry } from 'src/app/modules/miscellaneous/model/TooltipEntry';
import { TooltipEntryService } from 'src/app/modules/miscellaneous/services/tooltip.entry.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-tooltip-entry',
  templateUrl: './referential-tooltip-entry.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialTooltipEntryComponent extends GenericReferentialComponent<TooltipEntry> implements OnInit {
  constructor(private tooltipEntryService: TooltipEntryService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<TooltipEntry> {
    return this.tooltipEntryService.addOrUpdateTooltipEntry(this.selectedEntity!);
  }

  getGetObservable(): Observable<TooltipEntry[]> {
    return this.tooltipEntryService.getTooltipEntries();
  }
}
