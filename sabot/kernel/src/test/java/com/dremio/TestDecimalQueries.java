/*
 * Copyright (C) 2017-2019 Dremio Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dremio;

import java.math.BigDecimal;

import org.junit.Ignore;
import org.junit.Test;

public class TestDecimalQueries extends DecimalCompleteTest {

  @Test
  public void testDecimalFilterLiterals() throws Exception {

    final String query = "select val from cp" +
      ".\"parquet/decimals/simple-decimals-with-nulls.parquet\" where val != 1.0";
    test(query);
  }

  @Test
  public void testDecimalMixedTypes() throws Exception {

    final String query = "SELECT avg(distinct cast(a as decimal (2,1))) FROM cp"  +
      ".\"decimal/cast_avg_decimal.json\"";
    test(query);
  }

  @Test
  public void testCastDecimalFloat() throws Exception {

    final String query = "select cast(val as float) from cp" +
      ".\"parquet/decimals/simple-decimals-with-nulls.parquet\" limit 2";
    testBuilder().sqlQuery(query)
      .unOrdered()
      .baselineColumns("EXPR$0")
      .baselineValues(123.12346f)
      .baselineValues(1.1234568f)
      .go();
  }

  @Test
  public void testCastOnLiteral() throws Exception {
    final String query = "select Cast(( 2 / Sqrt(3.0) ) AS DECIMAL( 18, 0))";
    testBuilder().sqlQuery(query)
      .unOrdered()
      .baselineColumns("EXPR$0")
      .baselineValues(new BigDecimal(1))
      .go();
  }

  @Test
  public void testJoinFloatDecimal() throws Exception {

    final String query = "select count(*) from cp" +
      ".\"parquet/decimals/j2.parquet\" where c_date not in ( select c_date from cp.\"parquet/decimals/j2.parquet\" where " +
      "c_float not in ( select max(c_integer)*0.012 from cp.\"parquet/decimals/j6.parquet\"))";
    testBuilder().sqlQuery(query)
      .unOrdered()
      .baselineColumns("EXPR$0")
      .baselineValues(0l)
      .go();
  }

  @Test
  public void testDecimalUnionInAgg() throws Exception {

    final String query = "select sum(case when val != null " +
      " then cast(val as decimal(38,6)) * val" +
      " else 1 end) " +
      " from cp.\"parquet/decimals/simple-decimals-with-nulls.parquet\"";

    testBuilder().sqlQuery(query)
      .unOrdered()
      .baselineColumns("EXPR$0")
      .baselineValues(new BigDecimal(13))
      .go();
  }

  @Test
  public void testDecimalUnionInProject() throws Exception {

    final String query = "select (case when val != null " +
      " then cast(val as decimal(18,2))" +
      " else 10.00001 end) " +
      " from cp.\"parquet/decimals/simple-decimals-with-nulls.parquet\" limit 1";

    testBuilder().sqlQuery(query)
      .unOrdered()
      .baselineColumns("EXPR$0")
      .baselineValues(new BigDecimal("10.00001"))
      .go();
  }

  @Test
  public void testDecimalMisc() throws Exception {

    final String query = "select val, val - 0 from cp" +
      ".\"parquet/decimals/simple-decimals-with-nulls" +
      ".parquet\" limit 1";

    testBuilder().sqlQuery(query)
      .unOrdered()
      .baselineColumns("val","EXPR$1")
      .baselineValues(new BigDecimal("123.12345678901234567890"), new BigDecimal("123.12345678901234567890"))
      .go();
  }

  @Test
  public void testDecimalDivideLiteral() throws Exception {
    final String query = "select cast(17 as decimal(19,0)) / cast(1440.00 as decimal(7,2))";

    testBuilder().sqlQuery(query)
      .unOrdered()
      .baselineColumns("EXPR$0")
      .baselineValues(new BigDecimal("0.01180556"))
      .go();
  }

  @Test
  public void testDecimalDivideNegativeLiteral() throws Exception {
    final String query = "select cast(17 as decimal(19,0)) / cast(-1440.00 as decimal(7,2))";

    testBuilder().sqlQuery(query)
      .unOrdered()
      .baselineColumns("EXPR$0")
      .baselineValues(new BigDecimal("-0.01180556"))
      .go();
  }

  @Test
  public void testDecimalDivideZeroLiteral() throws Exception {
    final String query =
        "select val from cp.\"parquet/decimals/simple-decimals-with-nulls.parquet\" where val != 0 limit 1";

    testBuilder().sqlQuery(query)
      .unOrdered()
      .baselineColumns("val")
      .baselineValues(new BigDecimal("123.12345678901234567890"))
      .go();
  }

  @Test
  public void testDecimalDivideParquetLiteral() throws Exception {
    final String query = "select c_bigint / cast(1440.00 as decimal(7,2)) from cp.\"parquet/decimals/j6.parquet\" limit 2";

    testBuilder().sqlQuery(query)
      .unOrdered()
      .baselineColumns("EXPR$0")
      .baselineValues(new BigDecimal("-347133.00000000"))
      .baselineValues(new BigDecimal("-230222.82777778"))
      .go();
  }

  @Test
  public void testDecimalLargeLiteral() throws Exception {
    final String query = "select cast(24 as decimal(38,36)) * cast(2 as decimal(1,0))";

    testBuilder().sqlQuery(query)
      .unOrdered()
      .baselineColumns("EXPR$0")
      .baselineValues(new BigDecimal(48).setScale(36))
      .go();
  }

  @Ignore("DX-17746")
  @Test
  public void testDecimalLiteralPrecisionScale() throws Exception {

    final String query = "SELECT ({fn TIMESTAMPDIFF(SQL_TSI_DAY,{d '1900-01-01'},CAST(\"Calcs\".\"datetime0\" AS DATE))} " +
      "+ EXTRACT(HOUR FROM \"Calcs\".\"datetime0\") / 24.0 " +
      "+ EXTRACT(MINUTE FROM \"Calcs\".\"datetime0\") / (24.0 * 60) " +
      "+ EXTRACT(SECOND FROM \"Calcs\".\"datetime0\") / (24.0 * 60 * 60)) as EXPR$0 " +
      " FROM cp.\"parquet/decimals/Calcs.parquet\" \"Calcs\"" +
      " GROUP BY ({fn TIMESTAMPDIFF(SQL_TSI_DAY,{d '1900-01-01'},CAST(\"Calcs\".\"datetime0\" AS " +
      "DATE))} " +
      "+ EXTRACT(HOUR FROM \"Calcs\".\"datetime0\") / 24.0 " +
      "+ EXTRACT(MINUTE FROM \"Calcs\".\"datetime0\") / (24.0 * 60) " +
      "+ EXTRACT(SECOND FROM \"Calcs\".\"datetime0\") / (24.0 * 60 * 60)) order by EXPR$0 limit 2";

    testBuilder().sqlQuery(query)
      .unOrdered()
      .baselineColumns("EXPR$0")
      .baselineValues(new BigDecimal("38171.242684854074")  )
      .baselineValues(new BigDecimal("38171.843286701481")  )
      .go();
  }
}
