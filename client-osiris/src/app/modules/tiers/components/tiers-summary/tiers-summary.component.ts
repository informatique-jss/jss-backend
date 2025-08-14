import { Component, OnInit } from '@angular/core';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { NgIcon } from '@ng-icons/core';
import { callNumber, prepareMail } from '../../../../libs/MailHelper';
import { Mail } from '../../../profile/model/Mail';
import { Phone } from '../../../profile/model/Phone';
import { Tiers } from '../../../profile/model/Tiers';
import { TiersService } from '../../services/tiers.service';

@Component({
    selector: 'tiers-resume',
    imports: [
        NgIcon,
        NgbDropdownModule
    ],
    standalone: true,
    templateUrl: "tiers-summary.component.html",
    styles: ``
})
export class TiersSummaryComponent implements OnInit {

    tiers: Tiers | undefined;

    constructor(
        private tiersService: TiersService,
    ) { }

    ngOnInit(): void {
        this.tiersService.getSelectedTiers().subscribe(tiersId => {
            if (tiersId) {
                this.tiersService.getTiersById(tiersId).subscribe(tiers => {
                    if (tiers)
                        this.tiers = tiers;
                })
            }
        });
    }

    call = function (phone: Phone) {
        callNumber(phone.phoneNumber);
    }

    sendMail = function (mail: Mail) {
        prepareMail(mail.mail, null, null);
    }

    deleteTiers(tiers: Tiers) {
        this.tiersService.deleteTiers(tiers);
    }
}
