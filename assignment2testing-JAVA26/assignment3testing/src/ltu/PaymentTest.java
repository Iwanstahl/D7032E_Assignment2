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
        assertTrue(result > 0);
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
        assertTrue(result > 0);
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

    // --- ID 203: A student studying full time is entitled to 100% subsidiary. ---


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
                                                                                                                                                         
    
    // --- ID 501: Full time student loan: 7088 SEK / month ---                                                                                            
                                                                                                                                                         
    // --- ID 502: Full time subsidiary: 2816 SEK / month ---

    // --- ID 503: Less than full time student loan: 3564 SEK / month ---

    // --- ID 504: Less than full time subsidiary: 1396 SEK / month ---

    // --- ID 505: A person who is entitled to receive a student loan will always receive the full amount. ---

    // --- ID 506: Student loans and subsidiary is paid on the last weekday (Monday to Friday) every month. ---





}
