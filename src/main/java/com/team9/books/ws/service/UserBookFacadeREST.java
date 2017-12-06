/*
 * Created by Wes Jordan (wesj2363@vt.edu) on 2017.12.05  * 
 * Copyright © 2017 Wes Jordan. All rights reserved. * 
 */
package com.team9.books.ws.service;

import com.team9.books.ws.Token;
import com.team9.books.ws.User;
import com.team9.books.ws.UserBook;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author wes
 */
@Stateless
@Path("com.team9.books.ws.userbook")
public class UserBookFacadeREST extends AbstractFacade<UserBook> {

    @PersistenceContext(unitName = "com.team9_Books-WS_war_1.0PU")
    private EntityManager em;

    @EJB
    private TokenFacadeREST tokens;

    public UserBookFacadeREST() {
        super(UserBook.class);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void create(@HeaderParam("tokenid") Integer tokenid,
                       @HeaderParam("isbn13") Integer isbn13) {
        Token token = tokens.getTokenOr401(tokenid);

        UserBook entity = new UserBook();
        entity.setUser(token.getUser());
        entity.setIsbn13(isbn13);
        entity.setAdded(new Date());

        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void edit(@HeaderParam("tokenid") Integer tokenid, @PathParam("id") Integer id, UserBook entity) {
        tokens.getTokenOr401(tokenid);
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@HeaderParam("tokenid") Integer tokenid, @PathParam("id") Integer id) {
        tokens.getTokenOr401(tokenid);
        super.remove(super.find(id));
    }

    @GET
    @Path("byUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<UserBook> userBooks(@HeaderParam("tokenid") Integer tokenid) {
        Token token = tokens.getTokenOr401(tokenid);
        return token.getUser().getUserBookCollection();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TokenFacadeREST getTokens() {
        return tokens;
    }

    public void setTokens(TokenFacadeREST tokens) {
        this.tokens = tokens;
    }
}
