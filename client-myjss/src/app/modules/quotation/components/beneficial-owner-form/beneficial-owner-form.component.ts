import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { combineLatest, Observable } from 'rxjs';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AutocompleteCityComponent } from '../../../miscellaneous/components/forms/autocomplete-city/autocomplete-city.component';
import { GenericDatePickerComponent } from '../../../miscellaneous/components/forms/generic-date-picker/generic-datetime-picker.component';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { GenericToggleComponent } from '../../../miscellaneous/components/forms/generic-toggle/generic-toggle.component';
import { SelectCountryComponent } from '../../../miscellaneous/components/forms/select-country/select-country.component';
import { Provision } from '../../../my-account/model/Provision';
import { City } from '../../../profile/model/City';
import { BeneficialOwner } from '../../model/BeneficialOwner';
import { OtherControls } from '../../model/OtherControls';
import { ShareHolding } from '../../model/ShareHolding';
import { VotingRights } from '../../model/VotingRights';
import { BeneficialOwnerService } from '../../services/beneficial.owner.service';

@Component({
  selector: 'beneficial-owner-form',
  templateUrl: './beneficial-owner-form.component.html',
  styleUrls: ['./beneficial-owner-form.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS,
    GenericInputComponent,
    GenericDatePickerComponent,
    GenericToggleComponent,
    SelectCountryComponent,
    AutocompleteCityComponent,
  ]
})
export class BeneficialOwnerFormComponent implements OnInit {

  @Input() provision: Provision | undefined;

  modifiedBeneficialOwners: BeneficialOwner[] = [this.getEmptyBeneficialOwner()];

  constructor(
    private formBuilder: FormBuilder,
    private beneficialOwnerService: BeneficialOwnerService,
  ) { }

  beneficialOwnerForm!: FormGroup;

  ngOnInit() {
    this.beneficialOwnerForm = this.formBuilder.group({});
    this.refreshBeneficialOwnersList(this.provision);
  }

  updateBeneficialOwners(): Observable<boolean> {
    return new Observable<boolean>(observer => {
      let promises = [];
      for (let beneficialOwner of this.modifiedBeneficialOwners)
        if (this.provision)
          promises.push(this.beneficialOwnerService.addOrUpdateBeneficialOwner(beneficialOwner, this.provision));

      combineLatest(promises).subscribe(res => {
        observer.next(true);
        observer.complete;
      });
    })
  }

  addBeneficialOwner() {
    this.modifiedBeneficialOwners.push(this.getEmptyBeneficialOwner());
  }

  saveBeneficialOwner(beneficialOwner: BeneficialOwner) {
    if (this.provision) {
      this.beneficialOwnerService.addOrUpdateBeneficialOwner(beneficialOwner, this.provision).subscribe(res => {
        this.refreshBeneficialOwnersList(res);
      });
    }
  }

  deleteLastBeneficialOwner(beneficialOwner: BeneficialOwner) {
    if (this.provision) {
      this.beneficialOwnerService.deleteBeneficialOwner(beneficialOwner).subscribe(res => {
        this.refreshBeneficialOwnersList(res);
      });
    }
  }

  refreshBeneficialOwnersList(res: any) {
    if (res)
      this.beneficialOwnerService.getBeneficialOwnersByProvision(this.provision!).subscribe(beneficialOwners => {
        this.modifiedBeneficialOwners = beneficialOwners;
      });
  }

  getEmptyBeneficialOwner(): BeneficialOwner {
    return { city: {} as City, shareHolding: {} as ShareHolding, votingRights: {} as VotingRights, otherControls: {} as OtherControls } as BeneficialOwner;
  }
}
