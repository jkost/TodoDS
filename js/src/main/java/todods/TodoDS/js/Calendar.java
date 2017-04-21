/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package todods.TodoDS.js;

import net.java.html.js.JavaScriptBody;
import net.java.html.js.JavaScriptResource;

/**
 *
 * @author antonepple
 */
@JavaScriptResource("moments.min.js")
public class Calendar {

    @JavaScriptBody(args = {"days"}, body = "window.current_date.add(days, 'day');\n"
            + "return moment().format('DDMMYY');")
    public static native String addDays_impl(int days);

    @JavaScriptBody(args = {"day"}, body = "var cd = moment(day,'DDMMYY');\n"
            + "return cd.format('dddd - DD. MMMM YY');")
    public static native String format(String day);

    @JavaScriptBody(args = {}, body = "var m = {}; m.m = moment(); return m;")
    public static native Object create();

    public static class LocalDate {

        public static LocalDate now() {
            return new LocalDate();
        }

        Object delegate;

        LocalDate() {
            this(Calendar.create());
        }

        LocalDate(Object delegate) {
            this.delegate = delegate;
        }

        public static LocalDate parse(String dueDate, String dmMyyyy) {
            return new LocalDate(parse_impl(dueDate, dmMyyyy));
        }

        @JavaScriptBody(args = {"dueDate", "dmMyyyy"}, body = "var m = {}; m.m = moment(dueDate,dmMyyyy); return m;")
        private static native Object parse_impl(String dueDate, String dmMyyyy);

        public int compareTo(LocalDate other) {
            return compareTo_impl(delegate, other.delegate);
        }

        @JavaScriptBody(args = {"me", "other"}, body = "return (me.m > other.m)? 1: me.m==other.m ? 0:-1;")
        private static native int compareTo_impl(Object me, Object other);

        @JavaScriptBody(args = { "delegate" }, body = "return delegate.m.dayOfYear();")
        public static native int getDayOfYear_impl(Object delegate); 

        public int getDayOfYear() {
            return getDayOfYear_impl(delegate);
        }

    }

}
