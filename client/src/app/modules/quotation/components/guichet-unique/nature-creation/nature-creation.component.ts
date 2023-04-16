import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Provision } from '../../../model/Provision';
import { Formalite } from '../../../model/guichet-unique/Formalite';

@Component({
  selector: 'nature-creation',
  templateUrl: './nature-creation.component.html',
  styleUrls: ['./nature-creation.component.css']
})
export class NatureCreationComponent implements OnInit {

  @Input() formalite: Formalite = {} as Formalite;
  @Input() provision: Provision | undefined;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Output() provisionChange: EventEmitter<Provision> = new EventEmitter<Provision>();

  typePersonneExploitation = this.constantService.getTypePersonneExploitation();
  typePersonnePersonnePhysique = this.constantService.getTypePersonnePersonnePhysique();
  typeFormaliteMofication = this.constantService.getTypeFormaliteModification();
  typeFormaliteCreation = this.constantService.getTypeFormaliteCreation();

  constructor(
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
  ) { }

  formaliteForm = this.formBuilder.group({});

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    /*if (this.formalite && this.formalite.content && this.formalite.content.natureCreation) {
      if (!this.formalite.content.natureCreation.societeEtrangere)
        this.formalite.content.natureCreation.societeEtrangere = false;
      if (!this.formalite.content.natureCreation.microEntreprise)
        this.formalite.content.natureCreation.microEntreprise = false;
      if (this.formalite.content.natureCreation.etablieEnFrance == null || this.formalite.content.natureCreation.etablieEnFrance == undefined)
        this.formalite.content.natureCreation.etablieEnFrance = true;
      if (this.formalite.content.natureCreation.salarieEnFrance == null || this.formalite.content.natureCreation.salarieEnFrance == undefined) {
        if (this.formalite.content.natureCreation.societeEtrangere)
          this.formalite.content.natureCreation.salarieEnFrance = false;
        else
          this.formalite.content.natureCreation.salarieEnFrance = true;
      }
      if (!this.formalite.content.natureCreation.relieeEntrepriseAgricole)
        this.formalite.content.natureCreation.relieeEntrepriseAgricole = false;
      if (!this.formalite.content.natureCreation.entrepriseAgricole)
        this.formalite.content.natureCreation.entrepriseAgricole = false;
      if (!this.formalite.content.natureCreation.seulsBeneficiairesModifies)
        this.formalite.content.natureCreation.seulsBeneficiairesModifies = false;
      if (!this.formalite.content.natureCreation.eirl)
        this.formalite.content.natureCreation.eirl = false;
    }*/
  }

  getFormStatus() {
    this.formaliteForm.markAllAsTouched();
    let status = this.formaliteForm.valid;
    return status;
  }

  provisionChangeFunction() {
    this.provisionChange.emit(this.provision);
  }

}
