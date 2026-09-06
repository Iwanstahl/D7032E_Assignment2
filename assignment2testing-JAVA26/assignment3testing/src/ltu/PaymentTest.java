package ltu;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class PaymentTest {
    private PaymentImpl getPayment(int year, int month, int day) throws IOException {
        ICalendar fixedCalendar = new ICalendar() {
            @Override
            public Date getDate() {
                Calendar c = Calendar.getInstance();
                c.set(year, month - 1, day);
                return c.getTime();
            }
        };
        return new PaymentImpl(fixedCalendar);
    }

    // --- ID 101: The student must be at least 20 years old to receive subsidiary and student loans ---
    @Test
    public void testAgeBelow20GetsNothing() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int result = payment.getMonthlyAmount("19970101-0000", 0, 100, 100);
        assertEquals(0, result);
    }
    @Test
    public void testAge20GetsPayment() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int result = payment.getMonthlyAmount("19960101-0000", 0, 100, 100);
        assertEquals(9904, result);
    }

    // --- ID 102:The student may receive subsidiary until the year they turn 56 ---
    @Test
    public void testAge57GetsNoSubsidy() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int result = payment.getMonthlyAmount("19590101-0000", 0, 100, 100);
        assertEquals(0, result);
    }
    @Test
    public void testAge56GetsSubsidy() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int result = payment.getMonthlyAmount("19600101-0000", 0, 100, 100);
        assertEquals(2816, result);
    }

    // --- ID 103: The student may not receive any student loans from the year they turn 47 --- 
    @Test
    public void testAge47GetsNoLoan() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int result = payment.getMonthlyAmount("19690101-0000", 0, 100, 100);
        assertEquals(2816, result);
    }
    @Test
    public void testAge46GetsLoan() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int result = payment.getMonthlyAmount("19700101-0000", 0, 100, 100);
        assertEquals(9904, result);
    }

    // --- ID 201: The student must be studying at least half time to receive any subsidiary. ---
    @Test
    public void testBelowHalfTimeGetsNothing() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int result = payment.getMonthlyAmount("19960101-0000", 0, 49, 100);
        assertEquals(0, result);
    }
    @Test
    public void testHalfTimeGetsSubsidairy() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int result = payment.getMonthlyAmount("19960101-0000", 0, 50, 100);
        assertEquals(4960, result);
    }
    // --- ID 202: A student studying less than full time is entitled to 50% subsidiary. ---
   @Test
    public void testLessThanFullTimeGetsPartTimeSubsidiary() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int result = payment.getMonthlyAmount("19960101-0000", 0, 99, 100);
        assertEquals(4960, result);
    }
    // --- ID 203: A student studying full time is entitled to 100% subsidiary. ---
    @Test
    public void testStudyingFullTime() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int result = payment.getMonthlyAmount("19960101-0000", 0, 100, 100);
        assertEquals(9904, result);
    }

    // --- ID 301:A student who is studying full time or more is permitted to earn a maximum of 85 813SEK per year in order to receive any subsidiary or student loans. ---
    @Test
    public void testFullTimeIncomeOverLimitGetsNothing() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int result = payment.getMonthlyAmount("19960101-0000", 85814, 100, 100);
        assertEquals(0, result);
    }
    @Test
    public void testFullTimeIncomeAtLimitGetsPayment() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int result = payment.getMonthlyAmount("19960101-0000", 85813, 100, 100);
        assertEquals(9904, result);
    }

    // --- ID 302: A student who is studying less than full time is allowed to earn a maximum of 128 722SEK per year in order to receive any subsidiary or student loans. ---
        @Test
    public void testPartTimeIncomeOverLimitGetsNothing() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int result = payment.getMonthlyAmount("19960101-0000", 128723, 99, 100);
        assertEquals(0, result);
    }
    @Test
    public void testPartTimeIncomeAtLimitGetsPayment() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int result = payment.getMonthlyAmount("19960101-0000", 128722, 99, 100);
        assertEquals(4960, result);
    }

    // --- ID 401: A student must have completed at least 50% of previous studies in order to receive any subsidiary or student loans. ---                                                                                                                                                                          
    @Test
    public void testHaveNotCompletedAtLeastHalfOfPreviousStudiesGetsNothing() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int result = payment.getMonthlyAmount("19960101-0000", 0, 100, 49);
        assertEquals(0, result);
    }
    @Test
    public void testHaveCompletedAtLeastHalfOfPreviousStudiesGetsPayment() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int result = payment.getMonthlyAmount("19960101-0000", 0, 100, 50);
        assertEquals(9904, result);

    }
    // --- ID 501: Full time student loan: 7088 SEK / month ---
    @Test
    public void testFullTimeLoanAmountIs7088() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int loanPlusSubsidy = payment.getMonthlyAmount("19960101-0000", 0, 100, 100);
        int subsidyOnly = payment.getMonthlyAmount("19690101-0000", 0, 100, 100);     
        int loanAmount = loanPlusSubsidy - subsidyOnly;
        assertEquals(7088, loanAmount);    
    }                                                 
    // --- ID 502: Full time subsidiary: 2816 SEK / month ---
    @Test
    public void testFullTimeSubsidairyIs2816() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int subsidyOnly = payment.getMonthlyAmount("19690101-0000", 0, 100, 100);     
        assertEquals(2816, subsidyOnly);    
    }
    // --- ID 503: Less than full time student loan: 3564 SEK / month ---
    @Test
    public void testLessThanFullTimeStudentLoanIs3564() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int loanPlusSubsidy = payment.getMonthlyAmount("19960101-0000", 0, 99, 100);
        int subsidyOnly = payment.getMonthlyAmount("19690101-0000", 0, 99, 100);     
        int loanAmount = loanPlusSubsidy - subsidyOnly;
        assertEquals(3564, loanAmount);    
    }
    // --- ID 504: Less than full time subsidiary: 1396 SEK / month ---
    @Test
    public void testLessThanFullTimeSubsidairyIs1396() throws IOException {
        PaymentImpl payment = getPayment(2016, 1, 1);
        int subsidyOnly = payment.getMonthlyAmount("19690101-0000", 0, 99, 100);     
        assertEquals(1396, subsidyOnly);    
    }
    // --- ID 505: A person who is entitled to receive a student loan will always receive the full amount. ---
    @Test
    public void testLoanAmountNotEntitledWithIncome() throws IOException {
    PaymentImpl payment = getPayment(2016, 1, 1);
    int lowIncome = payment.getMonthlyAmount("19960101-0000", 0, 100, 100);
    int nearLimitIncome = payment.getMonthlyAmount("19960101-0000", 85813, 100, 100);
    assertEquals(lowIncome, nearLimitIncome);
    }
    @Test
    public void testLoanAmountNotEntitledWithWithCompletionRatio() throws IOException {
    PaymentImpl payment = getPayment(2016, 1, 1);
    int lowCompletion = payment.getMonthlyAmount("19960101-0000", 0, 100, 50);
    int fullCompletion = payment.getMonthlyAmount("19960101-0000", 0, 100, 100);
    assertEquals(lowCompletion, fullCompletion);
    }

    // --- ID 506: Student loans and subsidiary is paid on the last weekday (Monday to Friday) every month. ---
    @Test
    public void testPaymentDateJanuary2016() throws IOException {
    PaymentImpl payment = getPayment(2016, 1, 15);
    assertEquals("20160129", payment.getNextPaymentDay()); // Jan 31 is Sunday -> Fri Jan 29
    }
    @Test
    public void testPaymentDateFebruary2016() throws IOException {
    PaymentImpl payment = getPayment(2016, 2, 15);
    assertEquals("20160229", payment.getNextPaymentDay()); // leap year, Feb 29 is Monday
    }
    @Test
    public void testPaymentDateMarch2016() throws IOException {
    PaymentImpl payment = getPayment(2016, 3, 15);
    assertEquals("20160331", payment.getNextPaymentDay()); // March 31 is Thursday
    }
    @Test
    public void testPaymentDateApril2016() throws IOException {
    PaymentImpl payment = getPayment(2016, 4, 15);
    assertEquals("20160429", payment.getNextPaymentDay()); // April 30 is Saturday -> Fri April 29
    }
    @Test
    public void testPaymentDateMay2016() throws IOException {
    PaymentImpl payment = getPayment(2016, 5, 15);
    assertEquals("20160531", payment.getNextPaymentDay()); // May 31 is Tuesday
    }
    @Test
    public void testPaymentDateJune2016() throws IOException {
    PaymentImpl payment = getPayment(2016, 6, 15);
    assertEquals("20160630", payment.getNextPaymentDay()); // June 30 is Thursday
    }

     // --- Input validation: rejects invalid arguments ---
      @Test(expected = IllegalArgumentException.class)
      public void testNullPersonIdThrows() throws IOException {
          PaymentImpl payment = getPayment(2016, 1, 1);
          payment.getMonthlyAmount(null, 0, 100, 100);
      }
      @Test(expected = IllegalArgumentException.class)
      public void testNegativeIncomeThrows() throws IOException {
          PaymentImpl payment = getPayment(2016, 1, 1);
          payment.getMonthlyAmount("19960101-0000", -1, 100, 100);
      }
      @Test(expected = IllegalArgumentException.class)
      public void testNegativeStudyRateThrows() throws IOException {
          PaymentImpl payment = getPayment(2016, 1, 1);
          payment.getMonthlyAmount("19960101-0000", 0, -1, 100);
      }
      @Test(expected = IllegalArgumentException.class)
      public void testNegativeCompletionRatioThrows() throws IOException {
          PaymentImpl payment = getPayment(2016, 1, 1);
          payment.getMonthlyAmount("19960101-0000", 0, 100, -1);
      }
      @Test(expected = IllegalArgumentException.class)
      public void testLongLengthPersonIdThrows() throws IOException {
          PaymentImpl payment = getPayment(2016, 1, 1);
          payment.getMonthlyAmount("1996010100", 0, 100, 100); // 10 chars, not 13
      }

}
