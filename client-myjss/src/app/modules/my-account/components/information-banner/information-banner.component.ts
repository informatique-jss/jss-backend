
import { Component, Input } from '@angular/core';
import { InformationBanner } from '../../../miscellaneous/model/InformationBanner';


@Component({
    selector: 'information-banner',
    standalone: true,
    imports: [],
    templateUrl: './information-banner.component.html',
    styleUrls: ['./information-banner.component.css']
})
export class InformationBannerComponent {

    @Input() informationBanner = {} as InformationBanner;


}