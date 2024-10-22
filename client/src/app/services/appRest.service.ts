import { HttpClient, HttpContext, HttpContextToken, HttpEvent, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export abstract class AppRestService<T> {

  public static serverUrl: string = environment.backendUrl;

  cache: any;

  constructor(protected _http: HttpClient, @Inject(String) protected entryPoint: string) {
  }

  successfulToken: HttpContextToken<string> = new HttpContextToken<string>(() => "");
  errorToken: HttpContextToken<string> = new HttpContextToken<string>(() => "");

  /**
   * Use it carrefully ! Cache is clientside and is cleared only at each website refresh.
   * Do not cache entity that are updated by many people.
   * Do not forget to call clear cache (with same params as getListCached) when you update the entity
   * @param params
   * @param api
   * @param successfulMessage
   * @param errorMessage
   * @returns
   */
  getListCached(params: HttpParams, api: string, successfulMessage: string = "", errorMessage: string = ""): Observable<T[]> {
    let context: HttpContext = new HttpContext();
    if (this.getFromCache(params, api))
      return new Observable<T[]>(observer => {
        observer.next(this.getFromCache(params, api)!);
        observer.complete;
      })
    else
      return new Observable<T[]>(observer => {
        this.getList(params, api, successfulMessage, errorMessage).subscribe(response => {
          this.setCache(params, api, response);
          observer.next(this.getFromCache(params, api)!);
          observer.complete;
        });
      })
  }

  clearListCache(params: HttpParams, api: string) {
    this.setCache(params, api, undefined);
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

  loginUser(params: HttpParams, api: string, item: T, successfulMessage: string = "", errorMessage: string = ""): Observable<T> {
    let context: HttpContext = new HttpContext();
    context.set(this.successfulToken, successfulMessage).set(this.errorToken, errorMessage);
    return this._http.post(AppRestService.serverUrl + this.entryPoint + "/" + api, item, { params, context }) as Observable<T>;
  }

  downloadPost(params: HttpParams, api: string, item: T, fallbackFilename: string = "", successfulMessage: string = "", errorMessage: string = "") {
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
        else if (fallbackFilename)
          downloadLink.setAttribute('download', fallbackFilename);
        document.body.appendChild(downloadLink);
        downloadLink.click();
      }
    )
  }

  downloadGet(params: HttpParams, api: string, fallbackFilename: string = "", successfulMessage: string = "", errorMessage: string = "") {
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
        else if (fallbackFilename)
          downloadLink.setAttribute('download', fallbackFilename);
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

  getFromCache(params: HttpParams, api: string): T[] | null {
    if (this.cache && this.cache[api] && this.cache[api][this.serializeParams(params)])
      return this.cache[api][this.serializeParams(params)];
    return null;
  }

  setCache(params: HttpParams, api: string, response: T[] | undefined) {
    if (!this.cache)
      this.cache = [];

    if (!this.cache[api])
      this.cache[api] = [];

    this.cache[api][this.serializeParams(params)] = response;
  }

  serializeParams(params: HttpParams): string {
    let stringParams = "";
    if (params)
      for (let paramKey of params.keys())
        stringParams += paramKey + params.get(paramKey);
    return stringParams;
  }

}
