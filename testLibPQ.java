/*
 * $Id: TestLibPQ.java,v 1.1 2002/04/21 18:06:59 kevem Exp $
 * This file is part of jxDBCon: http://jxdbcon.sourceforge.net
 *
 * Copyright (c) 2001 Keve M¨¹ller
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 * This software is hosted by SourceForge.
 * SourceForge is a trademark of VA Linux Systems, Inc.
 */

package org.sourceforge.jxdbcon.postgresql;

import junit.framework.TestCase;

/**
 * Test LibPQ functionality.
 *
 * @author Keve M¨¹ller
 * @version $Revision: 1.1 $
 */
public final class TestLibPQ extends TestCase {
    public TestLibPQ(String name) {
        super(name);
    }

    public void testLibPQ() {
        int pgconn = PQconnectdb("dbname=test");
        System.out.println("connected: " + pgconn);
        System.out.println("db: " + PQdb(pgconn));
        System.out.println("user: " + PQuser(pgconn));
        System.out.println("error: " + PQerrorMessage(pgconn));

        int testout = LibC.fopen("test.out", "w+");
        PQtrace(pgconn, testout);

        int np = makeCallbackV(new Object() {
            void noticeProcessor(int msg) {
                String message = toUTF8String(msg);
                System.out.println("NOTIverycool: " + message);
            }}, "noticeProcessor", "(I)V");
        PQsetNoticeProcessor(pgconn, np, 0);

        int pgres = PQexec(pgconn, "SHOW CLIENT_ENCODING");
        PQclear(pgres);

        pgres = PQexec(pgconn, "SELECT * FROM pg_user");
        System.out.println("resStatus: " + PQresStatus(PQresultStatus(pgres)));
        System.out.println("resultErrorMessage: "
                + PQresultErrorMessage(pgres));
        System.out.println("cmdStatus: " + PQcmdStatus(pgres));
        System.out.println("cmdTuples: " + PQcmdTuples(pgres));
        int nTuples = PQntuples(pgres);
        int nFields = PQnfields(pgres);
        System.out.println("ntuples: " + nTuples);
        System.out.println("nfields: " + nFields);
        System.out.println("binaryTuples: " + PQbinaryTuples(pgres));
        for (int i = 0; i < nFields; i++) {
            System.out.print(PQfname(pgres, i) + ", ");
        }
        System.out.println();
        for (int j = 0; j < nTuples; j++) {
            for (int i = 0; i < nFields; i++) {
                if (0 == PQgetisnull(pgres, j, i)) {
                    int l = PQgetlength(pgres, j, i);
                    byte[] ba = PQgetvalue(pgres, j, i, l);
                    System.out.print(new String(ba) + ", ");
                } else {
                    System.out.print("null, ");
                }
            }
            System.out.println();
        }
        PQclear(pgres);

        PQfn(pgconn, 89, 0, 0);

        PQfinish(pgconn);
        System.out.println("finished");
    }
}