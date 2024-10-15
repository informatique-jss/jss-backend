import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AppService } from '../../../../libs/app.service';
import { validateEmail, validateFrenchPhone, validateInternationalPhone } from '../../../../libs/CustomFormsValidatorsHelper';
import { capitalizeName, getListMails, getListPhones } from '../../../../libs/FormatHelper';
import { Mail } from '../../../profile/model/Mail';
import { Phone } from '../../../profile/model/Phone';
import { Affaire } from '../../model/Affaire';
import { AffaireService } from '../../services/affaire.service';

@Component({
  selector: 'app-edit-affaire',
  templateUrl: './edit-affaire.component.html',
  styleUrls: ['./edit-affaire.component.css']
})
export class EditAffaireComponent implements OnInit {

  affaire: Affaire | undefined;
  editAffaireForm = this.formBuilder.group({});
  newMail: string = "";
  newPhone: string = "";

  constructor(private activatedRoute: ActivatedRoute,
    private affaireService: AffaireService,
    private formBuilder: FormBuilder,
    private appService: AppService,
  ) { }

  capitalizeName = capitalizeName;
  getListMails = getListMails;
  getListPhones = getListPhones;

  ngOnInit() {
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
        this.appService.openRoute(null, "account/orders/details/" + this.activatedRoute.snapshot.params['idOrder'], undefined);
      })
  }

  cancelAffaire() {
    this.appService.openRoute(null, "account/orders/details/" + this.activatedRoute.snapshot.params['idOrder'], undefined);
  }

}
