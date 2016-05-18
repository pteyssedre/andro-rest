ANDRO-REST
===============

Simple HttpClient for Android. It has been design for REST API request, it can return `JSONObject` or `String` object. This library is based on `HttpUrlConnection` to ensure best compatibility. 

### Changelog
 - Adding `GZIP` request/response support.
 - Adding `HttpForm` class for `application/www-x-form-urlencoded`.
 - Enforce default charset to `UTF-8`.


Usage
----
Simple `GET` request :
```java
 HttpClient client = new HttpClient("http://www.example.com/api/users");
 JSONObject users = client.getJson();
```
The type of request can be specify using the [`HttpRequestType`](https://github.com/pteyssedre/andro-rest/blob/master/src/main/java/ca/teyssedre/restclient/HttpRequestType.java):
```java
 HttpClient get = new HttpClient("http://www.example.com/api/json", HttpRequestType.GET);
 HttpClient post = new HttpClient("http://www.example.com/api/jsonPost", HttpRequestType.POST);
```

Posting `application/www-x-form-urlencoded` data :
```java
 HttpClient client = new HttpClient("http://www.example.com/api/post");
 HttForm form = new HttpForm();
 form.add("key","value").add("key2", "value2");
 client.addFromData(form);
 JSONObject response = client.getJson();
```


License
-----
    Copyright 2016 Pierre Teyssedre
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 
Author
-----
[Pierre Teyssedre](https://www,teyssedre.ca)
