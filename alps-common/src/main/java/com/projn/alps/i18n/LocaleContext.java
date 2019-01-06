package com.projn.alps.i18n;

import java.util.Locale;

/**
 * locale context
 *
 * @author : sunyuecheng
 */
public final class LocaleContext {
    private static ThreadLocal<Locale> threadLocale = new ThreadLocal<>();

    private LocaleContext() {

    }

    /**
     * set
     *
     * @param locale :
     */
    public static void set(Locale locale) {
        threadLocale.set(locale);
    }

    /**
     * get
     *
     * @return Locale :
     */
    public static Locale get() {
        Locale locale = threadLocale.get();
        if (null == locale) {
            locale = Locale.US;
        }

        return locale;
    }

    /**
     * remove
     */
    public static void remove() {
        threadLocale.remove();
    }
}
