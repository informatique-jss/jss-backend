import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { validateEmail, validateFrenchPhone, validateInternationalPhone } from '../../../../libs/CustomFormsValidatorsHelper';
import { capitalizeName, getListMails, getListPhones } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { Mail } from '../../../general/model/Mail';
import { AppService } from '../../../main/services/app.service';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { Phone } from '../../../profile/model/Phone';
import { Affaire } from '../../model/Affaire';
import { AffaireService } from '../../services/affaire.service';

@Component({
  selector: 'app-edit-affaire',
  templateUrl: './edit-affaire.component.html',
  styleUrls: ['./edit-affaire.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericInputComponent]
})
export class EditAffaireComponent implements OnInit {

  affaire: Affaire | undefined;
  editAffaireForm!: FormGroup;
  newMail: string = "";
  newPhone: string = "";

  idOrder: number | undefined;
  idQuotation: number | undefined;

  constructor(private activatedRoute: ActivatedRoute,
    private affaireService: AffaireService,
    private formBuilder: FormBuilder,
    private appService: AppService,
  ) { }

  capitalizeName = capitalizeName;
  getListMails = getListMails;
  getListPhones = getListPhones;

  ngOnInit() {
    this.idOrder = this.activatedRoute.snapshot.params['idOrder'];
    this.idQuotation = this.activatedRoute.snapshot.params['idQuotation'];

    this.editAffaireForm = this.formBuilder.group({});
    this.affaireService.getAffaire(this.activatedRoute.snapshot.params['id']).subscribe(response => {
      this.affaire = response;
    })
  }

  deleteMail(mail: Mail) {
    if (this.affaire)
      this.affaire.mails.splice(this.affaire.mails.indexOf(mail), 1);
  }

  addMail() {
    if (this.affaire && this.newMail && validateEmail(this.newMail)) {
      let mail = {} as Mail;
      mail.mail = this.newMail;
      if (!this.affaire.mails)
        this.affaire.mails = [];
      this.affaire.mails.push(mail);
      this.newMail = "";
    }
  }

  deletePhone(phone: Phone) {
    if (this.affaire)
      this.affaire.mails.splice(this.affaire.phones.indexOf(phone), 1);
  }

  addPhone() {
    if (this.affaire && this.newPhone && (validateFrenchPhone(this.newPhone) || validateInternationalPhone(this.newPhone))) {
      let phone = {} as Phone;
      phone.phoneNumber = this.newPhone;
      if (!this.affaire.phones)
        this.affaire.phones = [];
      this.affaire.phones.push(phone);
      this.newPhone = "";
    }
  }

  saveAffaire() {
    if (this.affaire)
      this.affaireService.addOrUpdateAffaire(this.affaire).subscribe(response => {
        this.goBackAffaire();
      })
  }

  goBackAffaire() {
    if (this.idOrder) {
      this.appService.openRoute(null, "account/orders/details/" + this.idOrder, undefined);
    }
    if (this.idQuotation) {
      this.appService.openRoute(null, "account/quotations/details/" + this.idQuotation, undefined);
    }
  }

}
