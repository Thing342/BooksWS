/*
 * Created by Wes Jordan (wesj2363@vt.edu) on 2017.12.05  * 
 * Copyright © 2017 Wes Jordan. All rights reserved. * 
 */
package com.team9.books.ws;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author wes
 */
@Entity
@Table(name = "UserBook")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserBook.findAll", query = "SELECT u FROM UserBook u")
    , @NamedQuery(name = "UserBook.findById", query = "SELECT u FROM UserBook u WHERE u.id = :id")
    , @NamedQuery(name = "UserBook.findByIsbn13", query = "SELECT u FROM UserBook u WHERE u.isbn13 = :isbn13")
    , @NamedQuery(name = "UserBook.findUID", query = "SELECT u FROM UserBook u WHERE u.user = :userid")
    , @NamedQuery(name = "UserBook.findByAdded", query = "SELECT u FROM UserBook u WHERE u.added = :added")})
public class UserBook implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "isbn13")
    private int isbn13;
    @Basic(optional = false)
    @NotNull
    @Column(name = "added")
    @Temporal(TemporalType.TIMESTAMP)
    private Date added;
    @JoinColumn(name = "user", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User user;

    public UserBook() {
    }

    public UserBook(Integer id) {
        this.id = id;
    }

    public UserBook(Integer id, int isbn13, Date added) {
        this.id = id;
        this.isbn13 = isbn13;
        this.added = added;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(int isbn13) {
        this.isbn13 = isbn13;
    }

    public Date getAdded() {
        return added;
    }

    public void setAdded(Date added) {
        this.added = added;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserBook)) {
            return false;
        }
        UserBook other = (UserBook) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.team9.books.ws.UserBook[ id=" + id + " ]";
    }
    
}
