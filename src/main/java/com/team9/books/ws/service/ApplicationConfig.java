/*
 * Created by Wes Jordan (wesj2363@vt.edu) on 2017.12.05  * 
 * Copyright © 2017 Wes Jordan. All rights reserved. * 
 */
package com.team9.books.ws.service;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author wes
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.team9.books.ws.service.NYTBooksREST.class);
        resources.add(com.team9.books.ws.service.TokenFacadeREST.class);
        resources.add(com.team9.books.ws.service.UserBookFacadeREST.class);
        resources.add(com.team9.books.ws.service.UserFacadeREST.class);
    }
    
}
