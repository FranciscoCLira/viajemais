package com.viajemais.util;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatadorUtil {

    public static String formatarMoeda(Double valor) {
        if (valor == null) return "0,00";
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return nf.format(valor);
    }
}
