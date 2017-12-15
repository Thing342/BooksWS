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
@Path("books")
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
    public UserBook create(@HeaderParam("tokenid") Integer tokenid,
                       @FormParam("isbn") Long isbn) {
        Token token = TokenFacadeREST.getTokenOr401(tokens, tokenid);

        UserBook entity = new UserBook();
        entity.setUser(token.getUser());

        entity.setIsbn13(isbn);
        entity.setAdded(new Date());

        super.create(entity);
        return entity;
    }

    @DELETE
    public String remove(@HeaderParam("tokenid") Integer tokenid,
                       @FormParam("isbn") Long isbn) {
        Token t = TokenFacadeREST.getTokenOr401(tokens, tokenid);
        assert t != null;

        this.em.createQuery("DELETE FROM UserBook ub WHERE ub.user = :userid AND ub.isbn13 = :isbn")
                .setParameter("userid", t.getUser())
                .setParameter("isbn", isbn)
                .executeUpdate();

        return "Removed isbn " + isbn.toString() + " from user " + t.getUser().getUsername();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<UserBook> userBooks(@HeaderParam("tokenid") Integer tokenid) {
        Token token = TokenFacadeREST.getTokenOr401(tokens, tokenid);
        User u =  token.getUser();
        return this.em.createQuery("FROM UserBook ub WHERE ub.user IN (:userid)", UserBook.class)
                .setParameter("userid", u)
                .getResultList();
        
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
