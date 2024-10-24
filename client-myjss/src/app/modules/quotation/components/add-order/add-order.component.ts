import { Component, OnInit } from '@angular/core';
import { Affaire } from '../../../my-account/model/Affaire';
import { AffaireService } from '../../../my-account/services/affaire.service';
import { ServiceTypeChosen } from '../../model/ServiceTypeChosen';
import { ServiceTypeService } from '../../services/service.type.service';
import { TYPE_CHOSEN_ORDER, TYPE_CHOSEN_QUOTATION } from '../choose-type/choose-type.component';

@Component({
  selector: 'app-add-order',
  templateUrl: './add-order.component.html',
  styleUrls: ['./add-order.component.css']
})
export class AddOrderComponent implements OnInit {
  typeChosen: string | undefined;
  servicesChosen: ServiceTypeChosen[] = [];
  displayAddNewService: boolean = true;

  constructor(
    //debug
    private serviceTypeService: ServiceTypeService,
    private affaireService: AffaireService,
  ) { }

  ngOnInit() {
    //debug
    this.typeChosen = TYPE_CHOSEN_ORDER;
    /*this.serviceTypeService.getServiceTypesForFamily(4075334).subscribe(response => {
      this.affaireService.getAffaire(4364468).subscribe(affaire => {
        this.servicesChosen.push({ affaire: affaire, service: response[0], temporaryId: 0, discountedAmount: undefined, preTaxPrice: undefined } as ServiceTypeChosen);
        this.displayAddNewService = false;
      })
    })*/
  }

  chooseType(typeChosen: string) {
    this.typeChosen = typeChosen;
  }

  isAnOrder() {
    return this.typeChosen && this.typeChosen == TYPE_CHOSEN_ORDER;
  }

  isAQuotation() {
    return this.typeChosen && this.typeChosen == TYPE_CHOSEN_QUOTATION;
  }

  chooseNewService(service: ServiceTypeChosen) {
    if (service) {
      this.servicesChosen.push(service);
      this.displayAddNewService = false;
    }
  }

  removeService(service: ServiceTypeChosen) {
    if (this.servicesChosen)
      this.servicesChosen.splice(this.servicesChosen.indexOf(service), 1);
    if (this.servicesChosen.length == 0)
      this.addAService();
  }

  addAService() {
    this.displayAddNewService = true;
  }

  getCurrentAffaires() {
    let affaires: Affaire[] = [];
    if (this.servicesChosen)
      for (let service of this.servicesChosen) {
        let found = false;
        for (let affaire of affaires) {
          if (affaire.id == service.affaire.id)
            found = true;

          if (!found)
            affaires.push(service.affaire);
        }
      }
    return affaires;
  }

}
