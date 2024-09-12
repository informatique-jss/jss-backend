import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Vat } from '../../../model/Vat';
import { VatService } from '../../../services/vat.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-vat',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectVatComponent extends GenericSelectComponent<Vat> implements OnInit {

  types: Vat[] = [] as Array<Vat>;

  constructor(private formBuild: UntypedFormBuilder, private vatService: VatService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.vatService.getVats().subscribe(response => {
      this.types = response.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

  displayLabel(object: Vat): string {
    if (object)
      return object.label + " (" + object.code + ")";
    return "";
  }

}
