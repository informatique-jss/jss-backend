import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';

@Component({
  selector: 'avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.css']
})
export class AvatarComponent implements OnInit {

  @Input() employee: Employee | undefined;
  initials: string = "";
  @Input() size: number = 40;

  constructor(
    private employeeService: EmployeeService,
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
    if (this.employee && this.employee.firstname && this.employee.lastname) {
      let fullName = (this.employee.firstname.toLocaleLowerCase().replace(" de ", "") + " " + this.employee.lastname.toLocaleLowerCase().replace(" de ", "")).split(' ');
      this.initials = (fullName.shift()!.charAt(0) + fullName.pop()!.charAt(0)).toUpperCase();
    }
  }

  backgoundColor(): string {
    return this.employeeService.getEmployeeBackgoundColor(this.employee);
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
