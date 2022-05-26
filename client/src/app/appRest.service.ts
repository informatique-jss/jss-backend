import { environment } from './../environments/environment';
import { HttpClient, HttpContext, HttpContextToken, HttpEvent, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export abstract class AppRestService<T> {

  public static serverUrl: string = environment.backendUrl;

  constructor(protected _http: HttpClient, @Inject(String) protected entryPoint: string) {
  }

  successfulToken: HttpContextToken<string> = new HttpContextToken<string>(() => "");
  errorToken: HttpContextToken<string> = new HttpContextToken<string>(() => "");

  getList(params: HttpParams, api: string, successfulMessage: string = "", errorMessage: string = ""): Observable<T[]> {
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    return this._http.get(AppRestService.serverUrl + this.entryPoint + "/" + api, { params, context }) as Observable<T[]>;
  }

  postList(params: HttpParams, api: string, item?: any, successfulMessage: string = "", errorMessage: string = ""): Observable<T[]> {
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    return this._http.post(AppRestService.serverUrl + this.entryPoint + "/" + api, item, { params, context }) as Observable<T[]>;
  }

  getById(api: string, id: number, successfulMessage: string = "", errorMessage: string = ""): Observable<T> {
    let params = new HttpParams().set('id', id);
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    return this._http.get(AppRestService.serverUrl + this.entryPoint + "/" + api, { params, context }) as Observable<T>;
  }

  get(params: HttpParams, api: string, successfulMessage: string = "", errorMessage: string = ""): Observable<T> {
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    return this._http.get(AppRestService.serverUrl + this.entryPoint + "/" + api, { params, context }) as Observable<T>;
  }

  delete(params: HttpParams, api: string, successfulMessage: string = "", errorMessage: string = ""): Observable<T> {
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    return this._http.delete(AppRestService.serverUrl + this.entryPoint + "/" + api, { params, context }) as Observable<T>;
  }

  addOrUpdate(params: HttpParams, api: string, item: T, successfulMessage: string = "", errorMessage: string = ""): Observable<T> {
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    return this._http.post(AppRestService.serverUrl + this.entryPoint + "/" + api, item, { params, context }) as Observable<T>;
  }

  downloadPost(api: string, item: T, successfulMessage: string = "", errorMessage: string = "") {
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    this._http.post(AppRestService.serverUrl + this.entryPoint + "/" + api, item, { responseType: 'blob' as 'arraybuffer', observe: 'response', context }).subscribe(
      (response: any) => {
        let dataType = response.type;
        let binaryData = [];
        binaryData.push(response.body);
        let downloadLink = document.createElement('a');
        downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, { type: dataType }));
        if (response.headers.get("filename"))
          downloadLink.setAttribute('download', response.headers.get("filename"));
        document.body.appendChild(downloadLink);
        downloadLink.click();
      }
    )
  }

  downloadGet(params: HttpParams, api: string, successfulMessage: string = "", errorMessage: string = "") {
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    this._http.get(AppRestService.serverUrl + this.entryPoint + "/" + api, { params, responseType: 'blob' as 'arraybuffer', observe: 'response', context }).subscribe(
      (response: any) => {
        let dataType = response.type;
        let binaryData = [];
        binaryData.push(response.body);
        let downloadLink = document.createElement('a');
        downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, { type: dataType }));
        if (response.headers.get("filename"))
          downloadLink.setAttribute('download', response.headers.get("filename"));
        document.body.appendChild(downloadLink);
        downloadLink.click();
      }
    )
  }

  previewFileGet(params: HttpParams, api: string, successfulMessage: string = "", errorMessage: string = "") {
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    this._http.get(AppRestService.serverUrl + this.entryPoint + "/" + api, { params, responseType: 'blob' as 'arraybuffer', observe: 'response', context }).subscribe(
      (response: any) => {
        let dataType = response.type;
        let binaryData = [];
        binaryData.push(response.body);
        window.open(window.URL.createObjectURL(new Blob(binaryData, { type: response.headers.get("content-type") })), '_blank');
      }
    )
  }

  uploadPost(api: string, file: File, formData: FormData, successfulMessage: string = "", errorMessage: string = ""): Observable<HttpEvent<any>> {
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    formData.append('file', file);

    return this._http.post(AppRestService.serverUrl + this.entryPoint + "/" + api, formData, { reportProgress: true, observe: "events", context }) as Observable<HttpEvent<any>>;
  }
}
