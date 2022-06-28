import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Regie } from 'src/app/modules/quotation/model/Regie';
import { RegieService } from 'src/app/modules/quotation/services/regie.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-regie',
  templateUrl: './select-regie.component.html',
  styleUrls: ['./select-regie.component.css']
})
export class SelectRegieComponent extends GenericSelectComponent<Regie> implements OnInit {

  types: Regie[] = [] as Array<Regie>;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: UntypedFormBuilder, private regieService: RegieService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.regieService.getRegies().subscribe(response => {
      this.types = response;
    })
  }
}
