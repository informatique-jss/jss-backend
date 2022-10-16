import { Component, Input, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { COUNTRY_CODE_FRANCE, UNREGISTERED_COMPANY_LEGAL_FORM_CODE } from 'src/app/libs/Constants';
import { validateRna, validateSiren, validateSiret } from 'src/app/libs/CustomFormsValidatorsHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { Mail } from 'src/app/modules/miscellaneous/model/Mail';
import { Phone } from 'src/app/modules/miscellaneous/model/Phone';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { Rna } from 'src/app/modules/quotation/model/Rna';
import { Siren } from 'src/app/modules/quotation/model/Siren';
import { Siret } from 'src/app/modules/quotation/model/Siret';
import { RnaService } from 'src/app/modules/quotation/services/rna.service';
import { SirenService } from 'src/app/modules/quotation/services/siren.service';
import { SiretService } from 'src/app/modules/quotation/services/siret.service';

@Component({
  selector: 'add-affaire',
  templateUrl: './add-affaire.component.html',
  styleUrls: ['./add-affaire.component.css']
})
export class AddAffaireComponent implements OnInit {

  @Input() affaire: Affaire = {} as Affaire;
  @Input() editMode: boolean = false;

  UNREGISTERED_COMPANY_LEGAL_FORM_CODE = UNREGISTERED_COMPANY_LEGAL_FORM_CODE;

  constructor(private formBuilder: FormBuilder,
    private cityService: CityService,
    private rnaService: RnaService,
    private sirenService: SirenService,
    private siretService: SiretService,
  ) { }

  ngOnInit() {
  }

  affaireForm = this.formBuilder.group({});


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

    if (this.affaire.country.code == COUNTRY_CODE_FRANCE && city.postalCode != null)
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
