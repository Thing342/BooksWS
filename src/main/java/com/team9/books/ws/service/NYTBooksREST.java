package com.team9.books.ws.service;

import khttp.KHttp;
import khttp.responses.Response;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Stateless
@Named("nyt")
@Path("apis/nyt")
public class NYTBooksREST {
    static final String NYT_ENDPOINT = "http://api.nytimes.com/svc/books/v3/lists/";
    static final String NYT_KEY = "ec206fbb82f24fbd9545d8fbea6cd22c";

    @GET
    @Path("listDetails/{date}/{listname}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getListDetails(@PathParam("date") String date,
                                 @PathParam("listname") String listname) {
        return query(String.format("%s/%s.json", date, listname), null);
    }

    @GET
    @Path("overview")
    @Produces(MediaType.APPLICATION_JSON)
    public String overview(@QueryParam("published_date") @DefaultValue("") String publishedDate) {
        Map<String, String> params = new HashMap<>();
        if(!publishedDate.isEmpty()) {
            params.put("published_date", publishedDate);
        }
        return query("overview.json", params);
    }

    @GET
    @Path("names")
    @Produces(MediaType.APPLICATION_JSON)
    public String names() {
        return query("names.json", null);
    }

    @GET
    @Path("best-sellers/history")
    @Produces(MediaType.APPLICATION_JSON)
    public String history(@QueryParam("isbn") @DefaultValue("") String isbn,
                          @QueryParam("author") @DefaultValue("") String author,
                          @QueryParam("title") @DefaultValue("") String title,
                          @QueryParam("offset") @DefaultValue("0") Integer offset) {
        Map<String, String> params = new HashMap<>();
        if(!isbn.isEmpty()) {
            params.put("isbn", isbn);
        }
        if(!author.isEmpty()) {
            params.put("author", author);
        }
        if(!title.isEmpty()) {
            params.put("title", title);
        }
        if(offset!=0) {
            params.put("offset", offset.toString());
        }

        return query("best-sellers/history.json", params);
    }

    private String query(String path, Map<String, String> params) {
        if(params == null) {
            params = new HashMap<>();
        }
        params.put("api-key", NYT_KEY);

        Response resp = KHttp.get(NYT_ENDPOINT + path, params, params);
        System.out.println(resp.getUrl());
        return resp.getText();
    }
}
