import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AppService } from 'src/app/services/app.service';
import { SpecialOffer } from '../../../model/SpecialOffer';
import { SpecialOfferService } from '../../../services/special.offer.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-special-offer',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectSpecialOfferComponent extends GenericSelectComponent<SpecialOffer> implements OnInit {

  @Input() types: SpecialOffer[] = [] as Array<SpecialOffer>;

  constructor(private formBuild: UntypedFormBuilder,
    private specialOfferService: SpecialOfferService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.specialOfferService.getSpecialOffers().subscribe(response => {
      this.types = response;
    })
  }
}
