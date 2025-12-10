import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { instanceOfTiers } from '../../../../libs/TypeHelper';
import { Employee } from '../../../profile/model/Employee';
import { ResponsableDto } from '../../../tiers/model/ResponsableDto';
import { TiersDto } from '../../../tiers/model/TiersDto';

@Component({
  selector: 'avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS,
    NgbTooltip
  ]
})
export class AvatarComponent implements OnInit {

  @Input() user: ResponsableDto | Employee | TiersDto | undefined;
  @Input() stringInput: string | undefined;

  fullName: string | undefined;

  initials: string = "";
  @Input() size: number = 40;
  @Input() centerAvatar: boolean = true;
  @Input() borderRadius: number = 100;


  constructor(
  ) { }

  ngOnInit() {
    this.computeInitials();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes)
      this.computeInitials();
  }

  computeInitials() {
    this.initials = "";
    if (this.user && this.user.firstname && this.user.lastname) {
      this.fullName = this.user.firstname + this.user.lastname;
      let fullNameArray = (this.user.firstname.toLocaleLowerCase().replace(" de ", " ") + " " + this.user.lastname.toLocaleLowerCase().replace(" de ", " ")).split(' ');
      this.initials = (fullNameArray.shift()!.charAt(0) + fullNameArray.pop()!.charAt(0)).toUpperCase();
    }

    if (this.user && instanceOfTiers(this.user) && this.user.denomination) {
      this.fullName = this.user.denomination;
      let fullName = this.user.denomination.toLocaleLowerCase().replace(" de ", " ").split(' ');
      if (fullName[0].toUpperCase() === "MAITRE") {
        fullName.shift();
      }
      this.initials = (fullName.shift()!.charAt(0).concat(fullName[fullName.length - 1] ? fullName[fullName.length - 1].charAt(0) : "")).toUpperCase();
    }

    if (!this.user && this.stringInput) {
      this.fullName = this.stringInput;
      let fullName = (this.stringInput.toLocaleLowerCase().replace(" de ", " ")).split(' ');
      this.initials = (fullName.shift()!.charAt(0) + fullName.pop()!.charAt(0)).toUpperCase();
    }
  }

  backgoundColor(): string {
    let name: string = "";
    if (this.user && this.user.firstname)
      name = this.user.firstname + this.user.lastname;

    if (this.stringInput)
      name = this.stringInput;

    if (name) {
      var hash = 0;
      for (var i = 0; i < name.length; i++) {
        hash = name.charCodeAt(i) + ((hash << 5) - hash);
      }
      var colour = '#';
      for (var i = 0; i < 3; i++) {
        var value = (hash >> (i * 8)) & 0xFF;
        colour += ('00' + value.toString(16)).substr(-2);
      }
      return colour;
    }
    return "#973434";
  }

  fontSize() {
    return Math.round(this.size / 2);
  }

  getFoncColor() {
    let bgColor = this.backgoundColor();
    var color = (bgColor.charAt(0) === '#') ? bgColor.substring(1, 7) : bgColor;
    var r = parseInt(color.substring(0, 2), 16); // hexToR
    var g = parseInt(color.substring(2, 4), 16); // hexToG
    var b = parseInt(color.substring(4, 6), 16); // hexToB
    var uicolors = [r / 255, g / 255, b / 255];
    var c = uicolors.map((col) => {
      if (col <= 0.03928) {
        return col / 12.92;
      }
      return Math.pow((col + 0.055) / 1.055, 2.4);
    });
    var L = (0.2126 * c[0]) + (0.7152 * c[1]) + (0.0722 * c[2]);
    return (L > 0.479) ? "#262626" : "#FFFFFF";
  }

}
