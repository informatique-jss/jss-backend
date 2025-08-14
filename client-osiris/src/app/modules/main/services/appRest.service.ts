import { HttpClient, HttpContext, HttpContextToken, HttpEvent, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { PagedContent } from './PagedContent';

@Injectable({
  providedIn: 'root'
})
export abstract class AppRestService<T> {

  public static serverUrl: string = environment.backendUrl + "myjss/";

  cache: any;

  constructor(protected _http: HttpClient, @Inject(String) protected entryPoint: string) {
  }

  successfulToken: HttpContextToken<string> = new HttpContextToken<string>(() => "");
  errorToken: HttpContextToken<string> = new HttpContextToken<string>(() => "");
  doNotRedirectOnNonAuthenticatedToken: HttpContextToken<string> = new HttpContextToken<string>(() => "");

  getPagedList(params: HttpParams, api: string, successfulMessage: string = "", errorMessage: string = ""): Observable<PagedContent<T>> {
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    return this._http.get(AppRestService.serverUrl + this.entryPoint + "/" + api, { params, context }) as Observable<PagedContent<T>>;
  }

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

  postItem(params: HttpParams, api: string, item?: any, successfulMessage: string = "", errorMessage: string = ""): Observable<T> {
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    return this._http.post(AppRestService.serverUrl + this.entryPoint + "/" + api, item, { params, context }) as Observable<T>;
  }

  getById(api: string, id: number, successfulMessage: string = "", errorMessage: string = ""): Observable<T> {
    let params = new HttpParams().set('id', id);
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    return this._http.get(AppRestService.serverUrl + this.entryPoint + "/" + api, { params, context }) as Observable<T>;
  }

  get(params: HttpParams, api: string, successfulMessage: string = "", errorMessage: string = "", doNotRedirectOnNonAuthenticated: boolean = false): Observable<T> {
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage).set(this.doNotRedirectOnNonAuthenticatedToken, doNotRedirectOnNonAuthenticated + "");
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

  loginUser(params: HttpParams, api: string, item: T, successfulMessage: string = "", errorMessage: string = ""): Observable<T> {
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    return this._http.post(AppRestService.serverUrl + this.entryPoint + "/" + api, item, { params, context }) as Observable<T>;
  }

  downloadPost(params: HttpParams, api: string, item: T, successfulMessage: string = "", errorMessage: string = "") {
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    this._http.post(AppRestService.serverUrl + this.entryPoint + "/" + api, item, { params, responseType: 'blob' as 'arraybuffer', observe: 'response', context }).subscribe(
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
        let url = window.URL.createObjectURL(new Blob(binaryData, { type: response.headers.get("content-type") }));
        window.open(url, '_blank');
        return of(url);
      }
    )
  }

  previewFileUrl(params: HttpParams, api: string, successfulMessage: string = "", errorMessage: string = ""): any {
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    return this._http.get(AppRestService.serverUrl + this.entryPoint + "/" + api, { params, responseType: 'blob' as 'arraybuffer', observe: 'response', context });
  }

  uploadPost(api: string, file: File, formData: FormData, successfulMessage: string = "", errorMessage: string = ""): Observable<HttpEvent<any>> {
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    formData.append('file', file);

    return this._http.post(AppRestService.serverUrl + this.entryPoint + "/" + api, formData, { reportProgress: true, observe: "events", context }) as Observable<HttpEvent<any>>;
  }

  serializeParams(params: HttpParams): string {
    let stringParams = "";
    if (params)
      for (let paramKey of params.keys())
        stringParams += paramKey + params.get(paramKey);
    return stringParams;
  }

}
