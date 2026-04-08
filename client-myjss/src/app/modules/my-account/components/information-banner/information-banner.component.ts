import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';


@Component({
    selector: 'app-information-banner',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './information-banner.component.html',
    styleUrls: ['./information-banner.component.css']
})
export class InformationBannerComponent {

    @Input() message: string = "";


}