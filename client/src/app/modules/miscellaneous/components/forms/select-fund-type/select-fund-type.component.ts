import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { FundType } from 'src/app/modules/quotation/model/FundType';
import { FundTypeService } from 'src/app/modules/quotation/services/fund-type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-fund-type',
  templateUrl: './select-fund-type.component.html',
  styleUrls: ['./select-fund-type.component.css']
})
export class SelectFundTypeComponent extends GenericSelectComponent<FundType> implements OnInit {

  types: FundType[] = [] as Array<FundType>;

  constructor(private formBuild: UntypedFormBuilder, private fundTypeService: FundTypeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.fundTypeService.getFundTypes().subscribe(response => {
      this.types = response;
    })
  }
}
