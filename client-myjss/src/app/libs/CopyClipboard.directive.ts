import { Directive, EventEmitter, HostListener, Input, Output } from "@angular/core";
import { PlatformService } from "../modules/main/services/platform.service";

@Directive({
  selector: '[copy-clipboard]',
  standalone: true
})
export class CopyClipboardDirective {

  constructor(private platformService: PlatformService) { }

  @Input("copy-clipboard")
  public payload: string = "";

  @Output("copied")
  public copied: EventEmitter<string> = new EventEmitter<string>();

  @HostListener("click", ["$event"])
  public onClick(event: MouseEvent): void {
    if (!this.platformService.isBrowser()) {
      return;
    }

    event.preventDefault();
    if (!this.payload)
      return;

    let listener = (e: ClipboardEvent) => {
      let clipboard = e.clipboardData || (window as any)["clipboardData"];
      clipboard.setData("text", this.payload.toString());
      e.preventDefault();

      this.copied.emit(this.payload);
    };

    document.addEventListener("copy", listener, false)
    if (navigator.clipboard?.writeText) {
      navigator.clipboard.writeText(this.payload).then(() => {
        this.copied.emit(this.payload);
      });
    } else {
      document.execCommand("copy");
    }
    document.removeEventListener("copy", listener, false);
  }
}
