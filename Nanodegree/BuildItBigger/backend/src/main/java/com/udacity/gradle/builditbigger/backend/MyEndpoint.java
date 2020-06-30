package com.udacity.gradle.builditbigger.backend;

import com.example.jokeslibrary.JokesLibrary;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

@Api(name = "myApi", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.builditbigger.gradle.udacity.com", ownerName = "backend.builditbigger.gradle.udacity.com"))
public class MyEndpoint {
    @ApiMethod(name = "sayHi")
    public MyBean getJoke() {
        MyBean response = new MyBean();
        response.setData(new JokesLibrary().getJokes());
        return response;
    }
}