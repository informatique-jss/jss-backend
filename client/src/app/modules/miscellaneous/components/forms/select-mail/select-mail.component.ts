import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AppService } from 'src/app/services/app.service';
import { Mail } from '../../../model/Mail';
import { MailService } from '../../../services/mail.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-mail',
  templateUrl: './select-mail.component.html',
  styleUrls: ['./select-mail.component.css']
})
export class SelectMailComponent extends GenericSelectComponent<Mail> implements OnInit {


  types: Mail[] = [] as Array<Mail>;

  constructor(private formBuild: UntypedFormBuilder, private mailService: MailService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.mailService.getAllMails().subscribe(response => {
      this.types = response;
    })
  }
}
