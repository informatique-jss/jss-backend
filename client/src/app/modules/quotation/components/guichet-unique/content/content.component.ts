import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { validateVat } from 'src/app/libs/CustomFormsValidatorsHelper';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Provision } from '../../../model/Provision';
import { Formalite } from '../../../model/guichet-unique/Formalite';
import { PersonnePhysiqueComponent } from '../personne-physique/personne-physique.component';

@Component({
  selector: 'content',
  templateUrl: './content.component.html',
  styleUrls: ['./content.component.css']
})
export class ContentComponent implements OnInit {

  @Input() formalite: Formalite = {} as Formalite;
  @Input() provision: Provision | undefined;;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Output() provisionChange: EventEmitter<Provision> = new EventEmitter<Provision>();
  @ViewChild(PersonnePhysiqueComponent) personnePhysiqueComponent: PersonnePhysiqueComponent | undefined;

  typeFormaliteCessation = this.constantService.getTypeFormaliteCessation();
  typeFormaliteCorrection = this.constantService.getTypeFormaliteCorrection();
  typeFormaliteModification = this.constantService.getTypeFormaliteModification();
  typeFormaliteCreation = this.constantService.getTypeFormaliteCreation();
  typePersonnePersonneMorale = this.constantService.getTypePersonnePersonneMorale();
  typePersonnePersonnePhysique = this.constantService.getTypePersonnePersonnePhysique();
  typePersonneExploitation = this.constantService.getTypePersonneExploitation();

  constructor(
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
  ) { }

  formaliteForm = this.formBuilder.group({});

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    /* if (this.formalite && this.formalite.content) {
       if (!this.formalite.content.personnePhysique)
         this.formalite.content.personnePhysique = {} as PersonnePhysique;
       if (!this.formalite.content.indicateurPoursuiteCessation)
         this.formalite.content.indicateurPoursuiteCessation = false;
       if (!this.formalite.content.indicateurActive)
         this.formalite.content.indicateurActive = false;
     }*/
  }

  getFormStatus() {
    this.formaliteForm.markAllAsTouched();
    let status = this.formaliteForm.valid;
    if (this.personnePhysiqueComponent)
      status = status && this.personnePhysiqueComponent.getFormStatus();
    return status;
  }

  provisionChangeFunction() {
    this.provisionChange.emit(this.provision);
  }

  checkVAT(fieldName: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;
      const fieldValue = root.get(fieldName)?.value;
      if (fieldValue && !validateVat(fieldValue))
        return {
          notFilled: true
        };
      return null;
    };
  }

}
