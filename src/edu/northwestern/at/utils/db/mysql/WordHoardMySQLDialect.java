package edu.northwestern.at.utils.db.mysql;

import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class WordHoardMySQLDialect extends MySQL5Dialect {
    /**
     * Register a custom function to provide regular expression matching in HQL.
     */
    public WordHoardMySQLDialect() {
        super();
        registerFunction(
                            "REGEXP_LIKE",
                            (SQLFunction) new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "?1 RLIKE ?2")
                        );
    }
}

/*
 * <p>
 * Copyright &copy; 2022 Martin Mueller and Craig A. Berry.
 * </p>
 * <p>
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * </p>
 * <p>
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more
 * details.
 * </p>
 * <p>
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA.
 * </p>
 */