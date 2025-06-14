package com.viajemais.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

@Component
public class StrictDoubleFormatter implements Formatter<Double> {
    
	/* LOG PARA TESTES DO PREÇO  */
    private static final Logger log = LoggerFactory.getLogger(StrictDoubleFormatter.class);	
	
	private final DecimalFormat df;

    public StrictDoubleFormatter() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt","BR"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        this.df = new DecimalFormat("#,##0.00", symbols);
        this.df.setParseBigDecimal(false);
    }

    @Override
    public Double parse(String text, Locale locale) throws ParseException {
    	
    	/* LOG PARA TESTES DO PREÇO  */
        log.debug("StrictDoubleFormatter.parse('{}')", text);
    	
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        String t = text.trim();
        ParsePosition pos = new ParsePosition(0);
        Number num = df.parse(t, pos);
        if (num == null || pos.getIndex() != t.length()) {
            throw new ParseException("Formato inválido: " + text, pos.getErrorIndex());
        }
        return num.doubleValue();
    }

    @Override
    public String print(Double object, Locale locale) {
        return object == null ? "" : df.format(object);
    }
}
