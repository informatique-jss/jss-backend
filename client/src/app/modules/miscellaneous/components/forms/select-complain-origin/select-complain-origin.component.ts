import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { SalesComplainOrigin } from '../../../model/SalesComplainOrigin';
import { SalesComplainOriginService } from 'src/app/modules/tiers/services/sales.complain.origin.service';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-complain-origin',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectComplainOriginComponent extends GenericSelectComponent<SalesComplainOrigin> implements OnInit {

  types: SalesComplainOrigin[] = [] as Array<SalesComplainOrigin>;

  constructor(private formBuild: UntypedFormBuilder, private salesComplainOriginService: SalesComplainOriginService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.types = [];
    this.salesComplainOriginService.getComplainOrigins().subscribe(response => {
      this.types = response;
      this.types = this.types.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

  displayLabel(object: SalesComplainOrigin): string {
    if (object)
      return object.label + " (" + object.code + ")";
    return "";
  }
}
