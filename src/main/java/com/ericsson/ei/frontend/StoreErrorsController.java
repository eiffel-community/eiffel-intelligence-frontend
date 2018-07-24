/*
   Copyright 2018 Ericsson AB.
   For a full list of individual contributors, please see the commit history.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.ericsson.ei.frontend;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class StoreErrorsController {

    private JsonArray errors = new JsonArray();

    @RequestMapping(value = "/getErrors", method = RequestMethod.GET)
    public ResponseEntity<String> getErrors(Model model) {
        return new ResponseEntity<>(errors.toString(), getHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/addErrors", method = RequestMethod.POST)
    public ResponseEntity<String> addErrors(Model model, HttpServletRequest request) throws IOException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", request.getParameter("message"));
        jsonObject.addProperty("time", new SimpleDateFormat("HH.mm").format(new Date()));
        errors.add(jsonObject);
        return new ResponseEntity<>(getHeaders(), HttpStatus.OK);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
