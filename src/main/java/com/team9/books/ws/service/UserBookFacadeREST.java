/*
 * Created by Wes Jordan (wesj2363@vt.edu) on 2017.12.05  * 
 * Copyright © 2017 Wes Jordan. All rights reserved. * 
 */
package com.team9.books.ws.service;

import com.team9.books.ws.Token;
import com.team9.books.ws.User;
import com.team9.books.ws.UserBook;
import org.intellij.lang.annotations.Language;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
                       @FormParam("isbn") Integer isbn) {
        Token token = TokenFacadeREST.getTokenOr401(tokens, tokenid);

        UserBook entity = new UserBook();
        entity.setUser(token.getUser());

        entity.setIsbn13(isbn);
        entity.setAdded(new Date());

        super.create(entity);
        return entity;
    }

    @DELETE
    public void remove(@HeaderParam("tokenid") Integer tokenid,
                       @FormParam("isbn") Integer isbn) {
        Token t = TokenFacadeREST.getTokenOr401(tokens, tokenid);
        assert t != null;

        String query = "DELETE FROM BooksDB.UserBook WHERE user = ? AND isbn13 = ?";
        this.em.createNativeQuery(query)
                .setParameter(1, t.getUser().getId())
                .setParameter(2, isbn)
                .executeUpdate();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<UserBook> userBooks(@HeaderParam("tokenid") Integer tokenid) {
        Token token = TokenFacadeREST.getTokenOr401(tokens, tokenid);
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
