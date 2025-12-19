import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { CompanySize } from 'src/app/modules/tiers/model/CompanySize';
import { CompanySizeService } from 'src/app/modules/tiers/services/company.size.service';
import { AppService } from 'src/app/services/app.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-company-size',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})

export class SelectCompanySizeComponent extends GenericSelectComponent<CompanySize> implements OnInit {
  types: CompanySize[] = [] as Array<CompanySize>;

  constructor(private formBuild: UntypedFormBuilder, private appService3: AppService, private companySizeService: CompanySizeService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.companySizeService.getCompanySizes().subscribe(response => {
      this.types = response;
      this.types = this.types.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

  compareWithId = compareWithId;

}
