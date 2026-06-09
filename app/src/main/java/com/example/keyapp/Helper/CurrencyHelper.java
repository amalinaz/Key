package com.example.keyapp.Helper;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyHelper {
    public static String formatRupiah(double number) {

        Locale localeID = new Locale("id", "ID");

        NumberFormat format =
                NumberFormat.getCurrencyInstance(localeID);

        format.setMaximumFractionDigits(0);

        return format.format(number)
                .replace("Rp", "Rp ");
    }

    public static double parseRupiah(String text) {

        String clean =
                text.replaceAll("[Rp,.\\s]", "");

        return Double.parseDouble(clean);
    }
}
