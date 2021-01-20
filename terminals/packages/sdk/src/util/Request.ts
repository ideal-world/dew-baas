/*
 * Copyright 2021. gudaoxuri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {JsonMap} from "../domain/Basic";
import {OptActionKind} from "../domain/Enum";

const TOKEN_FLAG = 'Dew-Token'
const APP_FLAG = 'Dew-App-Id'
const REQUEST_RESOURCE_URI_FLAG = "Dew-Resource-Uri"
const REQUEST_RESOURCE_ACTION_FLAG = "Dew-Resource-Action"

const AUTHENTICATION_HEAD_NAME = 'Authorization'
const DATE_HEAD_NAME = 'Dew-Date'

let _token: string = ''
let _serverUrl: string = ''
let _ak: string = ''
let _sk: string = ''
let _appId: string

let ajax: (url: string, headers?: JsonMap<any>, data?: any) => Promise<any>
let currentTime: () => string
let signature: (text: string, key: string) => string

const MOCK_DATA: JsonMap<Function> = {}

export function mock(items: JsonMap<Function>) {
    for (let k in items) {
        MOCK_DATA[k.toLowerCase()] = items[k]
    }
}

export function addMock(code: string, fun: Function) {
    MOCK_DATA[code.toLowerCase()] = fun
}

export function setAjaxImpl(impl: (url: string, headers?: JsonMap<any>, data?: any) => Promise<any>) {
    ajax = impl
}

export function setCurrentTime(impl: () => string) {
    currentTime = impl
}

export function setSignature(impl: (text: string, key: string) => string) {
    signature = impl
}

export function setAppId(appId: string): void {
    _appId = appId
}

export function setToken(token: string): void {
    _token = token
}

export function setAkSk(ak: string, sk: string): void {
    _ak = ak
    _sk = sk
}

export function setServerUrl(serverUrl: string): void {
    if (serverUrl.trim().endsWith("/")) {
        serverUrl = serverUrl.trim().substring(0, serverUrl.trim().length - 1)
    }
    _serverUrl = serverUrl
}

export function req<T>(name: string, resourceUri: string, optActionKind: OptActionKind, body?: any, headers?: JsonMap<any>, rawResult?: boolean): Promise<T> {
    if (MOCK_DATA.hasOwnProperty(name.toLowerCase())) {
        console.log('[Dew]Mock request [%s]%s', optActionKind, resourceUri)
        return new Promise<T>((resolve) => {
            resolve(MOCK_DATA[name.toLowerCase()].call(null, optActionKind, resourceUri, body))
        })
    }
    headers = headers ? headers : {}
    if (_appId) {
        headers[APP_FLAG] = _appId
    }
    headers[TOKEN_FLAG] = _token ? _token : ''
    let pathAndQuery = '/exec?' + REQUEST_RESOURCE_ACTION_FLAG + '=' + optActionKind + '&' + REQUEST_RESOURCE_URI_FLAG + '=' + encodeURIComponent(resourceUri)
    generateAuthentication('post', pathAndQuery, headers)
    let url = _serverUrl + pathAndQuery
    console.log('[Dew]Request [%s]%s , GW = [%s]', optActionKind, resourceUri, url)
    return new Promise<T>((resolve, reject) => {
        ajax(
            url,
            headers,
            typeof body === "undefined" ? "" : body
        )
            .then(res => {
                let data = res.data
                if (rawResult) {
                    resolve(data)
                } else {
                    if (data.code === '200') {
                        resolve(data.body)
                    } else {
                        console.error('请求错误 : [' + data.code + ']' + data.message)
                        reject('[' + data.code + ']' + data.message)
                    }
                }
            })
            .catch(error => {
                console.error('服务错误 : [' + error.message + ']' + error.stack)
                reject(error.message)
            })
    })
}

function generateAuthentication(method: string, pathAndQuery: string, headers: any): void {
    if (!_ak || !_sk) {
        return
    }
    let item = pathAndQuery.split('?')
    let path = item[0]
    let query = item.length === 2 ? item[1] : ''
    if (query) {
        query = query.split('&').sort((a, b) => a < b ? -1 : 1).join("&")
    }
    let date = currentTime()
    headers[AUTHENTICATION_HEAD_NAME] = _ak + ':' + signature(method + '\n' + date + '\n' + path + '\n' + query, _sk)
    headers[DATE_HEAD_NAME] = date
}
