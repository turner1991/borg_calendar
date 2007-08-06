/*
 This file is part of BORG.
 
 BORG is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.
 
 BORG is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with BORG; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 
 Copyright 2003 by Mike Berger
 */
/*
 * JdbcDB.java
 *
 * Created on February 2, 2004, 12:57 PM
 */

package net.sf.borg.model.db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.sf.borg.model.beans.KeyedBean;

/**
 * 
 * @author mberger
 */
// JdbcBeanDB extends JdbcDB to add support for KeyedBeans including caching
abstract public class JdbcBeanDB extends JdbcDB {

    // BORG needs its own caching. BORG rebuilds the map of DB data often
    // and going to the DB is too expensive. If BORG is changed to support
    // multi-user access, then the cache algorithm will have to be smarter.
    // currently, it is assumed that a single BORG client is the only DB
    // updater.
    // in the future, if there are multiple writers, the DB will have to
    // contain
    // some info to indicate when another process has written the DB to
    // force
    // a flush of the cache
    private boolean objectCacheOn_; // is caching on?

    private HashMap objectCache_; // the cache

    // For multiuser operation, we need to reserve key values even before a
    // record is created in the database. Any value here overrides the
    // current
    // maximum key in a particular table.
    protected int curMaxKey_ = Integer.MIN_VALUE;

    /** Creates a new instance of JdbcDB */
    JdbcBeanDB(String url, String username) throws Exception {
	super(url, username);

	objectCacheOn_ = true;
	objectCache_ = new HashMap();

	
    }

    JdbcBeanDB(Connection conn) {
	super(conn);
	objectCacheOn_ = true;
	objectCache_ = new HashMap();

    }

    // turn on caching
    public void cacheOn(boolean b) {
	objectCacheOn_ = b;
    }

    public void sync() {
	emptyCache();
    }

 

    protected void writeCache(KeyedBean bean) {
	// put a copy of the bean in the cache
	if (objectCacheOn_) {
	    objectCache_.put(new Integer(bean.getKey()), bean.copy());
	}
    }

    protected void emptyCache() {
	if (objectCacheOn_)
	    objectCache_.clear();
    }

    protected void delCache(int key) {
	// remove the bean from the cache
	if (objectCacheOn_) {
	    objectCache_.remove(new Integer(key));
	}
    }

    protected KeyedBean readCache(int key) {
	// need to remove cache here if DB has been updated
	// by any other process besides this one
	// TBD

	// if the bean is in the cache - return it
	if (objectCacheOn_) {
	    Object o = objectCache_.get(new Integer(key));

	    if (o != null) {
		KeyedBean r = (KeyedBean) o;
		return r.copy();
	    }
	}

	return (null);
    }


    public Collection readAll() throws Exception {
	PreparedStatement stmt = null;
	ResultSet r = null;
	try {
	    stmt = getPSAll();
	    r = stmt.executeQuery();
	    List lst = new ArrayList();
	    while (r.next()) {
		KeyedBean bean = createFrom(r);
		lst.add(bean);
		writeCache(bean);
	    }
	    return lst;
	} finally {
	    if (r != null)
		r.close();
	    if (stmt != null)
		stmt.close();
	}
    }

    public KeyedBean readObj(int key) throws Exception {
	KeyedBean bean = readCache(key);

	if (bean != null)
	    return bean;

	PreparedStatement stmt = null;
	ResultSet r = null;
	try {
	    stmt = getPSOne(key);
	    r = stmt.executeQuery();
	    if (r.next()) {
		bean = createFrom(r);
		writeCache(bean);
	    }
	    return bean;
	} finally {
	    if (r != null)
		r.close();
	    if (stmt != null)
		stmt.close();
	}
    }

    // package //
    abstract PreparedStatement getPSOne(int key) throws SQLException;

    abstract PreparedStatement getPSAll() throws SQLException;

    abstract KeyedBean createFrom(ResultSet rs) throws SQLException;
}
