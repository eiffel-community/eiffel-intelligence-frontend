/*
   Copyright 2017 Ericsson AB.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.apache.tomcat.util.http.fileupload.IOUtils;

@Controller
public class SubscriptionFilesController {
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	@Autowired
	ServletContext servletContext;
	
	@Value("${ei.subscriptionFilePath}") private String subscriptionFilePath;
    private static final String APPLICATION_JSON = "application/json";

    
    @RequestMapping(value = "/download/subscriptiontemplate", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public void getSubscriptionJsonTemplate(HttpServletResponse response) throws IOException {

    	InputStream is = null;
    	try {
    		is = getClass().getResourceAsStream("/subscriptionsTemplate.json");
    	} catch (NullPointerException e) {
    		System.out.println("ERROR: " + e.getMessage());
    	}
        
    	try {
    		IOUtils.copy(is, response.getOutputStream());
    		response.getOutputStream().flush();
    	} catch (IOException e) {
    		System.out.println("Error :- " + e.getMessage());
    	}
    	
    }

    
    private File getFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (!file.exists()){
            throw new FileNotFoundException("File " + '"' + filePath + '"' + " was not found.");
        }
        return file;
    }
}

