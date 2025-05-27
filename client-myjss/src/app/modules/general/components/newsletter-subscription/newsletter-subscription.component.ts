import { AfterViewInit, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { debounceTime, fromEvent, Subject, takeUntil } from 'rxjs';
import { validateEmail } from '../../../../libs/CustomFormsValidatorsHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { PlatformService } from '../../../main/services/platform.service';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { CommunicationPreferencesService } from '../../services/communication.preference.service';

@Component({
  selector: 'newsletter-subscription',
  templateUrl: './newsletter-subscription.component.html',
  styleUrls: ['./newsletter-subscription.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericInputComponent]
})
export class NewsletterSubscriptionComponent implements OnInit, AfterViewInit {

  mail: string = '';
  isMobile: boolean = false;
  newsletterForm!: FormGroup;

  private eventResize = new Subject<void>();

  constructor(
    private communicationPreferencesService: CommunicationPreferencesService,
    private appService: AppService,
    private formBuilder: FormBuilder,
    private plaformService: PlatformService
  ) { }

  ngOnInit() {
    this.newsletterForm = this.formBuilder.group({});
    this.checkIfMobile();
  }

  ngAfterViewInit() {
    if (this.plaformService.isServer()) return;

    fromEvent(window, 'resize')
      .pipe(debounceTime(200), takeUntil(this.eventResize))
      .subscribe(() => {
        this.checkIfMobile();
      });
  }

  ngOnDestroy(): void {
    this.eventResize.next();
    this.eventResize.complete();
  }

  private checkIfMobile(): void {
    if (this.plaformService.isServer()) return;
    this.isMobile = window.innerWidth <= 768;
  }

  registerEmail(mailToRegister: string) {
    if (!mailToRegister) {
      return;
    }

    if (!validateEmail(mailToRegister)) {
      this.appService.displayToast("Impossible de finaliser votre inscription. Vérifiez votre adresse e-mail et réessayez.", true, "Une erreur s’est produite...", 3000);
      return;
    }

    this.communicationPreferencesService.subscribeToCorporateNewsletter(mailToRegister).subscribe(response => {
      if (response) {
        this.newsletterForm.reset();
        this.mail = "";
      }
    });
  }
}
