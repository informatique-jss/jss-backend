import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { IOsirisUser } from '../../../profile/model/IOsirisUser';

@Component({
  selector: 'avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.css']
})
export class AvatarComponent implements OnInit {

  @Input() user: IOsirisUser | undefined;
  initials: string = "";
  @Input() size: number = 40;
  @Input() centerAvatar: boolean = true;


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
      let fullName = (this.user.firstname.toLocaleLowerCase().replace(" de ", "") + " " + this.user.lastname.toLocaleLowerCase().replace(" de ", "")).split(' ');
      this.initials = (fullName.shift()!.charAt(0) + fullName.pop()!.charAt(0)).toUpperCase();
    }
  }

  backgoundColor(): string {
    if (this.user && this.user.firstname) {
      const name = this.user.firstname + this.user.lastname;
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
