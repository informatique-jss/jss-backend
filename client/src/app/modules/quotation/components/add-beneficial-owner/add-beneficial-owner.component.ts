import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AppService } from 'src/app/services/app.service';
import { Affaire } from '../../model/Affaire';
import { BeneficialOwner } from '../../model/beneficial-owner/BeneficialOwner';
import { OtherControls } from '../../model/beneficial-owner/OtherControls';
import { ShareHolding } from '../../model/beneficial-owner/ShareHolding';
import { VotingRights } from '../../model/beneficial-owner/VotingRights';
import { Formalite } from '../../model/Formalite';
import { BeneficialOwnerService } from '../../services/beneficial.owner.service';

@Component({
  selector: 'add-beneficial-owner',
  templateUrl: './add-beneficial-owner.component.html',
  styleUrls: ['./add-beneficial-owner.component.css']
})
export class AddBeneficialOwnerComponent implements OnInit {
  @Input() editMode: boolean = false;
  @Input() affaire: Affaire | undefined;
  @Input() formalite: Formalite | undefined;
  newBeneficialOwner: BeneficialOwner = { shareHolding: {}, votingRights: {}, otherControls: {} } as BeneficialOwner;
  hasControls: boolean = false;

  constructor(private beneficialOwnerService: BeneficialOwnerService,
    private formBuilder: FormBuilder,
    private changeDetectorRef: ChangeDetectorRef,
    private appService: AppService
  ) { }

  ngOnInit() {
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }
  addBeneficialOwnerForm = this.formBuilder.group({});


  loadBeneficialOwnerForEdit(owner: BeneficialOwner) {
    this.newBeneficialOwner = owner;
    if (!this.newBeneficialOwner.otherControls)
      this.newBeneficialOwner.otherControls = {} as OtherControls;
    if (!this.newBeneficialOwner.shareHolding)
      this.newBeneficialOwner.shareHolding = {} as ShareHolding;
    if (!this.newBeneficialOwner.votingRights)
      this.newBeneficialOwner.votingRights = {} as VotingRights;
  }

  onSelectShareHolding(isDirect: boolean) {
    if (this.newBeneficialOwner) {
      this.newBeneficialOwner.shareHolding.isDirectShare = isDirect;
      this.newBeneficialOwner.shareHolding.isIndirectShare = !isDirect;
    }
  }

  onSelectVotingRights(isDirect: boolean) {
    if (this.newBeneficialOwner) {
      this.newBeneficialOwner.votingRights.isDirectVoting = isDirect;
      this.newBeneficialOwner.votingRights.isIndirectVoting = !isDirect;
    }
  }

  addBeneficialOwner() {
    if (this.newBeneficialOwner && this.affaire) {
      if (!this.newBeneficialOwner.shareHolding.shareHoldsMoreThan25Percent && !this.newBeneficialOwner.votingRights.votingHoldsMoreThan25Percent) {
        this.appService.displaySnackBar("Un bénéficiaire effectif doit détenir au moins 25% du capital ou des droits de vote", true, 15);
        return;
      }
      if (this.newBeneficialOwner.votingRights.votingTotalPercentage > 100 || this.newBeneficialOwner.shareHolding.shareTotalPercentage > 100
        || (this.newBeneficialOwner.shareHolding.shareTotalPercentage + this.newBeneficialOwner.votingRights.votingTotalPercentage > 100)) {
        this.appService.displaySnackBar("La détention de parts ne peut pas dépasser 100%", true, 15);
        return;
      }

      this.newBeneficialOwner.affaire = this.affaire;

      this.beneficialOwnerService.addOrUpdateBeneficialOwner(this.newBeneficialOwner).subscribe(response => {
        if (response) {
          if (this.formalite)
            this.formalite.beneficialOwners.push(response);
          if (!this.newBeneficialOwner.id)
            this.appService.displaySnackBar("Le bénéficiaire effectif a bien été créé", false, 15);
          else
            this.appService.displaySnackBar("Le bénéficiaire effectif a bien été mis à jour", false, 15);

          this.addBeneficialOwnerForm.reset();
        }
      });
    }
  }
}
