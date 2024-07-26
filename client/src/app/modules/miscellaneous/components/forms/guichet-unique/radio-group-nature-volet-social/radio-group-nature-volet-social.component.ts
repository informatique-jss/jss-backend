import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { NatureVoletSocialService } from 'src/app/modules/miscellaneous/services/guichet-unique/nature.volet.social.service';
import { NatureVoletSocial } from 'src/app/modules/quotation/model/guichet-unique/referentials/NatureVoletSocial';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-nature-volet-social',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupNatureVoletSocialComponent extends GenericRadioGroupComponent<NatureVoletSocial> implements OnInit {
  types: NatureVoletSocial[] = [] as Array<NatureVoletSocial>;

  constructor(
    private formBuild: UntypedFormBuilder, private NatureVoletSocialService: NatureVoletSocialService,) {
    super(formBuild,);
  }

  initTypes(): void {
    this.NatureVoletSocialService.getNatureVoletSocial().subscribe(response => { this.types = response })
  }
}
