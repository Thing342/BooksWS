/*
 * Created by Wes Jordan (wesj2363@vt.edu) on 2017.12.05  * 
 * Copyright © 2017 Wes Jordan. All rights reserved. * 
 */
package com.team9.books.ws.service;

import com.team9.books.ws.Token;
import com.team9.books.ws.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 *
 * @author wes
 */
@Stateless
@Named("users")
@Path("users")
public class UserFacadeREST extends AbstractFacade<User> {

    @PersistenceContext(unitName = "com.team9_Books-WS_war_1.0PU")
    private EntityManager em;

    @EJB
    private TokenFacadeREST tokens;

    public UserFacadeREST() {
        super(User.class);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public User create(@FormParam("username") String username,
                       @FormParam("email") String email,
                       @FormParam("firstname") String firstname,
                       @FormParam("lastname") String lastname,
                       @FormParam("password") String password) {
        User entity = new User(0, username, email, firstname, lastname, password);
        super.create(entity);
        return entity;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public User edit(@HeaderParam("tokenid") Integer tokenid,
                     @FormParam("email") String email,
                     @FormParam("firstname") String firstname,
                     @FormParam("lastname") String lastname,
                     @FormParam("password") String password) {
        Token t = TokenFacadeREST.getTokenOr401(tokens, tokenid);
        User entity = t.getUser();

        entity.setEmail(email);
        entity.setFirstName(firstname);
        entity.setLastName(lastname);
        entity.setPassHash(BCrypt.hashpw(password, BCrypt.gensalt()).getBytes());

        super.edit(entity);

        return entity;
    }

    @DELETE
    public String remove(@HeaderParam("tokenid") Integer tokenid) {
        Token t = TokenFacadeREST.getTokenOr401(tokens, tokenid);
        User entity = t.getUser();

        tokens.logout(entity);
        super.remove(entity);

        return "Deleted user " + entity.getUsername();
    }

    @GET
    @Path("byID/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User find(@PathParam("id") Integer id) {
        User result = super.find(id);
        if(result == null) throw new NotFoundException();
        else return result;
    }

    @GET
    @Path("byUsername/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public User findByUsername(@PathParam("name") String username) {
        try {
            return (User) this.getEntityManager()
                    .createNamedQuery("User.findByUsername")
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (ClassCastException | NoResultException cce) {
            throw new NotFoundException(cce);
        }
    }

    @GET
    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
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
