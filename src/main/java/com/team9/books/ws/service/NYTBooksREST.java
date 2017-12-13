package com.team9.books.ws.service;

import com.team9.books.ws.util.HashUtils;
import khttp.KHttp;
import khttp.responses.Response;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

@Stateless
@Named("nyt")
@Path("apis/nyt")
public class NYTBooksREST {
    static final String NYT_ENDPOINT = "http://api.nytimes.com/svc/books/v3/lists/";
    static final String NYT_KEY = "ec206fbb82f24fbd9545d8fbea6cd22c";
    static final String NYT_CACHEPT = "/media/wes/Samsung USB/jee/NYTCache";

    @GET
    @Path("listDetails/{date}/{listname}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getListDetails(@PathParam("date") String date,
                                 @PathParam("listname") String listname) {
        return query(String.format("%s/%s.json", date, listname), null, HashUtils.Validity.FOREVER);
    }

    @GET
    @Path("overview")
    @Produces(MediaType.APPLICATION_JSON)
    public String overview(@QueryParam("published_date") @DefaultValue("") String publishedDate) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        HashUtils.Validity v = HashUtils.Validity.WEEKLY;
        if(!publishedDate.isEmpty()) {
            params.put("published_date", publishedDate);
            v = HashUtils.Validity.FOREVER;
        }
        return query("overview.json", params, v);
    }

    @GET
    @Path("names")
    @Produces(MediaType.APPLICATION_JSON)
    public String names() {
        return query("names.json", null, HashUtils.Validity.YEARLY);
    }

    @GET
    @Path("best-sellers/history")
    @Produces(MediaType.APPLICATION_JSON)
    public String history(@QueryParam("isbn") @DefaultValue("") String isbn,
                          @QueryParam("author") @DefaultValue("") String author,
                          @QueryParam("title") @DefaultValue("") String title,
                          @QueryParam("offset") @DefaultValue("0") Integer offset) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
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

        return query("best-sellers/history.json", params, HashUtils.Validity.WEEKLY);
    }

    private String query(String path, LinkedHashMap<String, String> params, HashUtils.Validity validity) {
        if(params == null) {
            params = new LinkedHashMap<>();
        }
        params.put("api-key", NYT_KEY);

        String toHash = path + params.toString() + HashUtils.getHashNumber(validity);
        System.out.println(toHash);
        String filename = HashUtils.hashString(toHash, "MD5");
        System.out.println(filename);

        java.nio.file.Path pathNIO = Paths.get(NYT_CACHEPT, filename);
        if(Files.exists(pathNIO)) {
            try {
                return new String(Files.readAllBytes(pathNIO));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Response resp = KHttp.get(NYT_ENDPOINT + path, params, params);
        System.out.println(resp.getUrl() + " " + resp.getStatusCode());

        if(resp.getStatusCode() == 200) {
            try {
                Files.createFile(pathNIO);
                Files.write(pathNIO, resp.getText().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resp.getText();
    }
}
