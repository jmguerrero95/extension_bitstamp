package com.generalbytes.batm.server.extensions.extra.usdtbitstamp;

import com.generalbytes.batm.server.extensions.IRateSource;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class USDTPriceSource implements IRateSource {

    private static final Set<String> SUPPORTED_CRYPTO = new HashSet<>(Arrays.asList(
            "USDT", "USDTTTRON", "USDTTRON", "USDT_TRON", "USDT-TRON", "USDTTTRX", "USDTTRX"
    ));

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        final String cc = cryptoCurrency == null ? "" : cryptoCurrency.toUpperCase(Locale.ROOT);
        final String fc = fiatCurrency == null ? "" : fiatCurrency.toUpperCase(Locale.ROOT);

        // Acepta los alias TRON y USDT genérico
        if (!SUPPORTED_CRYPTO.contains(cc)) return null;

        // Esta fuente entrega solo en USD
        if (!"USD".equals(fc)) return null;

        try {
            URL url = new URL("https://www.bitstamp.net/api/v2/ticker/usdtusd/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            StringBuilder json = new StringBuilder();
            try (Scanner sc = new Scanner(conn.getInputStream())) {
                while (sc.hasNext()) json.append(sc.nextLine());
            }

            // parsing sencillo (Bitstamp devuelve {"last":"1.0000",...})
            String body = json.toString();
            int i = body.indexOf("\"last\":\"");
            if (i < 0) return null;
            int start = i + 8;
            int end = body.indexOf('"', start);
            if (end < 0) return null;

            String priceStr = body.substring(start, end);
            return new BigDecimal(priceStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override public Set<String> getFiatCurrencies() { return Collections.singleton("USD"); }
    @Override public String getPreferredFiatCurrency() { return "USD"; }

    @Override
    public Set<String> getCryptoCurrencies() {
        // Lo que reportamos como soportado
        return new HashSet<>(SUPPORTED_CRYPTO);
    }
}
