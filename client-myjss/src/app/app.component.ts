import { AfterViewInit, Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent implements AfterViewInit {
  title = 'myjss';

  constructor(private router: Router) {
  }

  ngOnInit() {
    console.log("toto");
  }

  ngAfterViewInit() {
    this.loadScript('../assets/js/theme.js');
  }

  isDisplayGreyBackground() {
    let url: String = this.router.url;
    if (url)
      if (url.indexOf("account") >= 0 && url.indexOf("signin") <= 0)
        return true;
    return false;
  }

  isDisplayHeader() {
    let url: String = this.router.url;
    if (url)
      if (url.indexOf("signin") >= 0)
        return false;
    return true;
  }

  isDisplayFooter() {
    let url: String = this.router.url;
    if (url)
      if (url.indexOf("signin") >= 0)
        return false;
    return true;
  }

  loadScript(url: string) {
    const body = <HTMLDivElement>document.body;
    const script = document.createElement('script');
    script.innerHTML = '';
    script.src = url;
    script.async = false;
    script.defer = true;
    body.appendChild(script);
  }
}
