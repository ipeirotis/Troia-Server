/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.gal.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.datascience.gal.DawidSkene;

/**
 * a class for persisting DawidSkene Objects persists dawidskene objects to
 * mysql
 * 
 * 
 * TODO: cache using memcached TODO: make not shitty TODO: make read a
 * configuration
 * 
 * @author josh
 * 
 * 
 *         create table projects ( id varchar(1000) NOT NULL PRIMARY KEY, data
 *         TEXT NOT NULL); create index idIndex on projects (id);
 */
public class DawidSkeneCache {
    private static Logger logger = Logger.getLogger(DawidSkeneCache.class);
    private ExecutorService executor = Executors
            .newFixedThreadPool(NUM_THREADS);

    private static String USER;
    private static String PW;
    private static String DB;
    private static String URL;

    private Connection connection;

    private static final String GET_DS = "SELECT data FROM projects WHERE id IN (?);";
    private static final String INSERT_DS = "INSERT INTO projects (id, data) VALUES (?, ?) ON DUPLICATE KEY UPDATE data = (?);";
    private static final String DELETE_DS = "DELETE FROM projects WHERE id = (?);";
    private static final String CHECK_DS = "SELECT 1 FROM projects WHERE id IN (?);";
    private static final int NUM_THREADS = 1;

    private PreparedStatement dsStatement;
    private ResultSet dsResults;

    private int cachesize = 5;
    private Map<String, DawidSkene> cache;

    public DawidSkeneCache(Properties disProps) throws ClassNotFoundException,
            SQLException, IOException {

        Class.forName("com.mysql.jdbc.Driver");

        USER = disProps.getProperty("USER");
        PW = disProps.getProperty("PASSWORD");
        DB = disProps.getProperty("DB");
        URL = disProps.getProperty("URL");

        if (disProps.containsKey("cacheSize"))
            cachesize = Integer.parseInt(disProps.getProperty("cacheSize"));

        logger.info("done loading props: " + disProps.toString());

        logger.info("attempting to connect to " + URL);
        Properties props = new Properties();
        props.setProperty("user", USER);
        if (PW != null)
            props.setProperty("password", PW);
        connection = DriverManager.getConnection("jdbc:mysql://" + URL + "/"
                + DB, props);
        logger.info("connected to " + URL);

        initializeCache();

    }

    /**
     * 
     * @param id
     *            identifier of ds task
     * @return dawid skene object with appropriate id or null.
     */
    public DawidSkene getDawidSkene(String id) {
        try {
            if (cache.containsKey(id)) {
                return cache.get(id);
            }

            dsStatement = connection.prepareStatement(GET_DS);
            dsStatement.setString(1, id);
            dsResults = dsStatement.executeQuery();

            if (dsResults.next()) {
                try {
                    String dsJson = dsResults.getString("data");
                    DawidSkene ds = JSONUtils.gson.fromJson(dsJson,
                            JSONUtils.dawidSkeneType);
                    logger.info("returning ds object with id " + id);
                    cache.put(id, ds);

                    return ds;
                } catch (Exception e) {
                    logger.error(e.getLocalizedMessage());
                    return null;
                }

            } else {
                logger.info("no ds object with id " + id
                        + " found. returning null");
                return null;
            }

        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    public DawidSkene insertDawidSkene(final DawidSkene ds) {
        DSDBInserter inserter = new DSDBInserter(ds);
        executor.execute(inserter);
        cache.put(ds.getId(), ds);
        return ds;
    }

    public boolean hasDawidSkene(String id) {

        try {
            if (cache.containsKey(id))
                return true;

            dsStatement = connection.prepareStatement(CHECK_DS);
            dsStatement.setString(1, id);

            dsResults = dsStatement.executeQuery();

            if (dsResults.next())
                return true;

            return false;
        } catch (Exception e) {
            logger.error("error checking ds object: " + e.getLocalizedMessage());
        }

        return false;
    }

    public void deleteDawidSkene(String id) {

        try {
            if (cache.containsKey(id))
                cache.remove(id);

            dsStatement = connection.prepareStatement(DELETE_DS);
            dsStatement.setString(1, id);

            dsStatement.executeUpdate();
            dsStatement.close();
            logger.info("deleted ds with id " + id);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public void close() throws SQLException {
        logger.info("closing db connections");
        connection.close();
    }

    private void initializeCache() {
        cache = new LinkedHashMap<String, DawidSkene>() {

            private static final long serialVersionUID = -3654983366018448082L;

            @Override
            protected boolean removeEldestEntry(
                    java.util.Map.Entry<String, DawidSkene> arg0) {
                return size() > cachesize;
            }
        };
    }

    public void setCacheSize(int cachesize) {
        this.cachesize = cachesize;
    }

    private class DSDBInserter implements Runnable {
        final DawidSkene ds;

        public DSDBInserter(DawidSkene ds) {
            this.ds = ds;
        }

        @Override
        public void run() {
            try {
                dsStatement = connection.prepareStatement(INSERT_DS);
                dsStatement.setString(1, ds.getId());
                String dsString = ds.toString();
                dsStatement.setString(2, dsString);
                dsStatement.setString(3, dsString);

                dsStatement.executeUpdate();
                dsStatement.close();
                logger.info("upserting ds with id " + ds.getId());
            } catch (SQLException e) {
                logger.error(e.getMessage());

            }
        }

    }
}
