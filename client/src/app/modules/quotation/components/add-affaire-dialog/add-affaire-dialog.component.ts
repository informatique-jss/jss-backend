import { Component, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { UNREGISTERED_COMPANY_LEGAL_FORM_CODE } from 'src/app/libs/Constants';
import { validateRna, validateSiren, validateSiret } from 'src/app/libs/CustomFormsValidatorsHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { Civility } from 'src/app/modules/miscellaneous/model/Civility';
import { LegalForm } from 'src/app/modules/miscellaneous/model/LegalForm';
import { Mail } from 'src/app/modules/miscellaneous/model/Mail';
import { Phone } from 'src/app/modules/miscellaneous/model/Phone';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { CivilityService } from 'src/app/modules/miscellaneous/services/civility.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { AppService } from 'src/app/services/app.service';
import { Affaire } from '../../model/Affaire';
import { Rna } from '../../model/Rna';
import { Siren } from '../../model/Siren';
import { Siret } from '../../model/Siret';
import { AffaireService } from '../../services/affaire.service';
import { RnaService } from '../../services/rna.service';
import { SirenService } from '../../services/siren.service';
import { SiretService } from '../../services/siret.service';

@Component({
  selector: 'app-add-affaire-dialog',
  templateUrl: './add-affaire-dialog.component.html',
  styleUrls: ['./add-affaire-dialog.component.css']
})
export class AddAffaireDialogComponent implements OnInit {

  @ViewChild('tabs', { static: false }) tabs: any;
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  affaire: Affaire = {} as Affaire;

  UNREGISTERED_COMPANY_LEGAL_FORM_CODE = UNREGISTERED_COMPANY_LEGAL_FORM_CODE;

  civilities: Civility[] = [] as Civility[];
  legalForms: LegalForm[] = [] as LegalForm[];
  filteredLegalForms: Observable<LegalForm[]> | undefined;

  filteredPostalCodes: String[] | undefined;

  filteredCities: City[] | undefined;

  filteredAffaires: Affaire[] | undefined;

  selectedAffaire: Affaire | null = null;


  constructor(private formBuilder: FormBuilder,
    private cityService: CityService,
    private sirenService: SirenService,
    private siretService: SiretService,
    private civilityService: CivilityService,
    private rnaService: RnaService,
    private constantService: ConstantService,
    private affaireService: AffaireService,
    private appService: AppService,
    private affaireDialogRef: MatDialogRef<AddAffaireDialogComponent>
  ) { }

  ngOnInit() {
    this.civilityService.getCivilities().subscribe(response => {
      this.civilities = response;
      if (!this.affaire.civility)
        this.affaire.civility = this.civilities[0];
    })
    if (this.affaire && !this.affaire.isIndividual)
      this.affaire.isIndividual = false;
    if (this.tabs)
      this.tabs.realignInkBar();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.affaire != undefined) {
      if (this.affaire && !this.affaire.isIndividual)
        this.affaire.isIndividual = false;
      if (this.affaire.isIndividual && (this.affaire.civility == null || this.affaire.civility == undefined))
        this.affaire.civility = this.civilities[0];
      this.affaireForm.markAllAsTouched();
    }
  }

  fillAffaire(entity: IndexEntity) {
    let obj = JSON.parse((entity.text as string));
    this.affaireService.getAffaire(obj.id).subscribe(affaire => {
      this.selectedAffaire = affaire;
    });
  }

  affaireForm = this.formBuilder.group({
  });

  getFormStatus(): boolean {
    return this.affaireForm.valid;
  }

  saveAffaire() {
    if (this.selectedAffaire) {
      this.affaireDialogRef.close(this.selectedAffaire);
    } else if (this.getFormStatus()) {
      this.affaireService.addOrUpdateAffaire(this.affaire).subscribe(response => {
        this.affaire = response;
        this.affaireDialogRef.close(this.affaire);
      })
    } else {
      this.appService.displaySnackBar("L'onglet de saisie de l'affaire n'est pas correctement rempli. Veuillez le compléter avant de sauvegarder", true, 20);
    }
  }

  closeDialog() {
    this.affaireDialogRef.close(null);
  }


  checkSiren(fieldName: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (!this.affaire.isIndividual && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0 || !validateSiren(fieldValue)))
        return {
          notFilled: true
        };
      return null;
    };
  }

  checkSiret(fieldName: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (!this.affaire.isIndividual && (fieldValue != undefined && fieldValue != null && fieldValue.length > 0 && !validateSiret(fieldValue)))
        return {
          notFilled: true
        };
      return null;
    };
  }

  checkRna(fieldName: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (!this.affaire.isIndividual && fieldValue != undefined && fieldValue != null && fieldValue.length > 0 && !validateRna(fieldValue))
        return {
          notFilled: true
        };
      return null;
    };
  }

  fillPostalCode(city: City) {
    if (this.affaire.country == null || this.affaire.country == undefined)
      this.affaire.country = city.country;

    if (this.affaire.country.id == this.constantService.getCountryFrance().id && city.postalCode != null)
      this.affaire.postalCode = city.postalCode;
  }

  fillCity(postalCode: string) {
    this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
      if (response != null && response != undefined && response.length == 1) {
        let city = response[0];
        if (this.affaire.country == null || this.affaire.country == undefined)
          this.affaire.country = city.country;

        this.affaire.city = city;
      }
    })
  }

  fillSiren(siren: Siren) {
    if (siren != undefined && siren != null) {
      this.affaire.siren = siren!.uniteLegale.siren;
      if (siren!.uniteLegale.identifiantAssociationUniteLegale && siren!.uniteLegale.identifiantAssociationUniteLegale.substring(0, 1) == "W") {
        this.rnaService.getRna(siren!.uniteLegale.identifiantAssociationUniteLegale).subscribe(response => {
          if (response != null && response.length == 1) {
            this.fillRna(response[0]);
          }
        })
      } else if (siren!.uniteLegale.siren != undefined && siren!.uniteLegale.siren != null) {
        if (siren.uniteLegale.periodesUniteLegale != null && siren.uniteLegale.periodesUniteLegale != undefined && siren.uniteLegale.periodesUniteLegale.length > 0) {
          siren.uniteLegale.periodesUniteLegale.forEach(periode => {
            if (periode.dateFin == null) {
              this.affaire.denomination = periode.denominationUniteLegale;
              this.affaireForm.markAllAsTouched();
              if (periode.nicSiegeUniteLegale != null && periode.nicSiegeUniteLegale != undefined)
                this.siretService.getSiret(siren?.uniteLegale.siren + periode.nicSiegeUniteLegale).subscribe(response => {
                  if (response != null && response.length == 1) {
                    this.fillSiret(response[0]);
                  }
                })
            }
          });
        }
      }
    }
    this.affaireForm.markAllAsTouched();
  }

  fillSiret(siret: Siret) {
    if (siret != undefined && siret != null) {
      this.affaire.siret = siret!.etablissement.siret;
      if ((this.affaire.siren == null || this.affaire.siren == undefined) && siret.etablissement.siren != null && siret.etablissement.siren != undefined) {
        this.sirenService.getSiren(siret.etablissement.siren).subscribe(response => {
          if (response != null && response.length == 1) {
            this.fillSiren(response[0]);
          }
        })
      }
      if (siret.etablissement.adresseEtablissement != null && siret.etablissement.adresseEtablissement.codePostalEtablissement != null) {
        this.cityService.getCitiesFilteredByPostalCode(siret.etablissement.adresseEtablissement.codePostalEtablissement).subscribe(response => {
          if (response != null && response.length == 1) {
            this.affaire.postalCode = siret!.etablissement.adresseEtablissement.codePostalEtablissement;
            this.fillPostalCode(response[0]);
            this.affaireForm.markAllAsTouched();
          } else if (siret!.etablissement.adresseEtablissement.libelleCommuneEtablissement) {
            this.cityService.getCitiesFilteredByCountryAndName(siret!.etablissement.adresseEtablissement.libelleCommuneEtablissement, undefined).subscribe(response2 => {
              if (response2 != null && response2.length == 1) {
                this.affaire.city = response2[0];
                this.affaire.country = response2[0].country;
                this.affaireForm.markAllAsTouched();
              }
            })
          }
        })
        this.affaire.address = siret.etablissement.adresseEtablissement.numeroVoieEtablissement + " " + siret.etablissement.adresseEtablissement.typeVoieEtablissement + " " + siret.etablissement.adresseEtablissement.libelleVoieEtablissement;
      }
    }
    this.affaireForm.markAllAsTouched();
  }

  fillRna(rna: Rna) {
    if (rna != undefined && rna != null) {
      this.affaire.rna = rna.association.id_association;
      this.affaire.denomination = rna.association.titre_court;
      this.affaire.address = rna.association.adresse_gestion_libelle_voie + "\n" + rna.association.adresse_gestion_distribution;
      if (rna.association.email != null) {
        if (!this.affaire.mails)
          this.affaire.mails = [] as Array<Mail>;
        let newMail = {} as Mail;
        newMail.mail = rna.association.email;
        this.affaire.mails.push(newMail);
      }
      if (rna.association.telephone != null) {
        if (!this.affaire.phones)
          this.affaire.phones = [] as Array<Phone>;
        let newPhone = {} as Phone;
        newPhone.phoneNumber = rna.association.telephone;
        this.affaire.phones.push(newPhone);
      }
      this.cityService.getCitiesFilteredByPostalCode(rna.association.adresse_code_postal).subscribe(response => {
        if (response != null && response.length == 1) {
          this.affaire.postalCode = rna!.association.adresse_code_postal;
          this.fillPostalCode(response[0]);
        } else {
          this.cityService.getCitiesFilteredByCountryAndName(rna!.association.adresse_libelle_commune, undefined).subscribe(response2 => {
            if (response2 != null && response2.length == 1) {
              this.affaire.city = response2[0];
              this.affaire.country = response2[0].country;
              this.affaireForm.markAllAsTouched();
            }
          })
        }
      })
    }
    this.affaireForm.markAllAsTouched();
  }
}
